package com.example.ppmtoolmobile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ppmtoolmobile.dao.ProjectAndUserDAOImpl;
import com.example.ppmtoolmobile.model.Project;
import com.example.ppmtoolmobile.model.User;
import com.example.ppmtoolmobile.model.UserAvatar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText profileFirstNameEditText, profileLastNameEditText, profileEmailAddressEditText;
    private TextView changeUserAvatarTextView;
    private ImageView userAvatarImageView;
    private BottomNavigationView bottomNavView;
    private Button profileUpdateBtn;
    private ImageView profileNavigationBack;
    private ProjectAndUserDAOImpl databaseHelper;
    private String authenticatedUser;
    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;
    private Uri avatarUri;
    Bitmap selectedAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
//
        profileFirstNameEditText = findViewById(R.id.profileFirstNameEditText);
        profileLastNameEditText = findViewById(R.id.profileLastNameEditText);
        profileEmailAddressEditText = findViewById(R.id.profileEmailAddressEditText);
        profileNavigationBack = findViewById(R.id.profileNavigationBack);
        changeUserAvatarTextView = findViewById(R.id.changeUserAvatarTextView);
        userAvatarImageView = findViewById(R.id.userAvatarImageView);
        profileUpdateBtn = findViewById(R.id.profileUpdateBtn);

        // getting current username through intent from ProjectActivity.class
        authenticatedUser = getIntent().getStringExtra("authenticatedUser");

        Toast.makeText(this, "profile activity: " + authenticatedUser, Toast.LENGTH_SHORT).show();
        databaseHelper = new ProjectAndUserDAOImpl(this);
        loadUserDetails();

        profileNavigationBack.setOnClickListener(this);
        changeUserAvatarTextView.setOnClickListener(this);
        profileUpdateBtn.setOnClickListener(this);

        bottomNavView = findViewById(R.id.bottomNavView);
        bottomNavView.setSelectedItemId(R.id.nav_profile);




        // Perform item selected listener
        bottomNavView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId())
                {
                    case R.id.nav_home:
                        Intent goToProjectActivityIntent = new Intent(ProfileActivity.this, ProjectActivity.class);
                        moveToIntent(goToProjectActivityIntent);
                        return true;
                    case R.id.nav_profile:
                        return true;
                    case R.id.nav_settings:
                        Intent goToSettingsActivityIntent = new Intent(ProfileActivity.this, SettingsActivity.class);
                        moveToIntent(goToSettingsActivityIntent);
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View view) {
        if(view == profileNavigationBack) {
            finish();
        } else if(view == changeUserAvatarTextView) {
            chooseImage(view);
        } else if(view == profileUpdateBtn) {
            updateUserDetails();
        }
    }

    private void chooseImage(View view) {
        try {
            Intent objectIntent = new Intent();
            objectIntent.setType("image/*");
            objectIntent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(objectIntent, IMAGE_PICK_CODE);
        } catch (Exception e) {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);

            if(requestCode == IMAGE_PICK_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
                avatarUri = data.getData();
                InputStream imageStream = getContentResolver().openInputStream(avatarUri);
                selectedAvatar = BitmapFactory.decodeStream(imageStream);
                selectedAvatar = getResizedBitmap(selectedAvatar, 400);

                userAvatarImageView.setImageBitmap(selectedAvatar);
            }
        } catch(Exception e) {

        }


    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    private void moveToIntent(Intent intent) {
//        Intent goToSettingsActivityIntent = new Intent(ProjectActivity.this, ProfileActivity.class);

        intent.putExtra("authenticatedUser", authenticatedUser);
        startActivity(intent);
        overridePendingTransition(0,0);
    }


    private void loadUserDetails() {
        List<Object> userAndAvatarDetails = databaseHelper.getUserAndAvatarDetails(authenticatedUser);
        if(!userAndAvatarDetails.isEmpty()) {
            Bitmap bitmap = (Bitmap) userAndAvatarDetails.get(4);
            profileFirstNameEditText.setText(userAndAvatarDetails.get(1).toString());
            profileLastNameEditText.setText(userAndAvatarDetails.get(2).toString());
            profileEmailAddressEditText.setText(userAndAvatarDetails.get(3).toString());
            userAvatarImageView.setImageBitmap(bitmap);

        } else {
            User userDetails = databaseHelper.getUserDetails(authenticatedUser);
            profileFirstNameEditText.setText(userDetails.getFirstName());
            profileLastNameEditText.setText(userDetails.getLastName());
            profileEmailAddressEditText.setText(userDetails.getEmailAddress());
        }




    }

    private void updateUserDetails() {

        String firstName = profileFirstNameEditText.getText().toString().trim();
        String lastName = profileLastNameEditText.getText().toString().trim();
        String emailAddress = profileEmailAddressEditText.getText().toString().trim();
        long userId = databaseHelper.getCurrentUserId(authenticatedUser);


        if(userAvatarImageView.getDrawable() != null && selectedAvatar != null) {
            UUID uuid = UUID. randomUUID();

            UserAvatar user = new UserAvatar(String.valueOf(uuid), selectedAvatar);
            boolean result = databaseHelper.saveAvatar(user, authenticatedUser);


            System.out.println(user.getAvatar());


            if(result) {
                Toast.makeText(this, "Avatar uploaded with user: " + authenticatedUser, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error adding avatar", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, "Please select image" + authenticatedUser, Toast.LENGTH_SHORT).show();
        }

    }


}