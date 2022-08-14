package com.example.ppmtoolmobile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ppmtoolmobile.dao.UserDAOImpl;
import com.example.ppmtoolmobile.model.User;
import com.example.ppmtoolmobile.model.UserAvatar;
import com.example.ppmtoolmobile.utils.DBUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.InputStream;
import java.util.UUID;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText profileFirstNameEditText, profileLastNameEditText, profileEmailAddressEditText, profilePasswordEditText;
    private TextView changeUserAvatarTextView;
    private ImageView userAvatarImageView;
    private BottomNavigationView bottomNavView;
    private ProgressBar avatarProgressBar;
    private Button profileUpdateBtn;
    private ImageView profileNavigationBack, profileMenu;
    private UserDAOImpl userHelper;
    private long theUserId;
    private String authenticatedUser;
    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;
    private Uri avatarUri;
    private Bitmap selectedAvatar;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileFirstNameEditText = findViewById(R.id.profileFirstNameEditText);
        profileLastNameEditText = findViewById(R.id.profileLastNameEditText);
        profileEmailAddressEditText = findViewById(R.id.profileEmailAddressEditText);
        profileNavigationBack = findViewById(R.id.profileNavigationBack);
        changeUserAvatarTextView = findViewById(R.id.changeUserAvatarTextView);
        userAvatarImageView = findViewById(R.id.userAvatarImageView);
        profileUpdateBtn = findViewById(R.id.profileUpdateBtn);
        avatarProgressBar = findViewById(R.id.avatarProgressBar);
        profileMenu = findViewById(R.id.profileMenu);
        dialog = new Dialog(this);


        authenticatedUser = getIntent().getStringExtra(DBUtils.AUTHENTICATED_USER);

        userHelper = new UserDAOImpl(this);
        loadUserDetails();

        profileNavigationBack.setOnClickListener(this);
        changeUserAvatarTextView.setOnClickListener(this);
        profileUpdateBtn.setOnClickListener(this);
        profileMenu.setOnClickListener(this);

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
        } else if(view == profileMenu) {
            showMenu();
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
                selectedAvatar = getCroppedBitmap(selectedAvatar, 375);

                userAvatarImageView.setImageBitmap(selectedAvatar);
            }
        } catch(Exception e) {
            Toast.makeText(this, "There was an error, please try again shortly", Toast.LENGTH_SHORT).show();
        }


    }

    private void showMenu() {
        PopupMenu popupMenu = new PopupMenu(ProfileActivity.this, profileMenu);

        popupMenu.getMenuInflater().inflate(R.menu.profile_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.option_remove_avatar:
                        removeAvatar();
                        break;
                }
                return true;
            }

        });
        // Showing the popup menu
        popupMenu.show();
    }

    private void removeAvatar() {
        displayDialog(R.layout.caution_dialog_layout);

        Button Okay = dialog.findViewById(R.id.btn_okay);
        Button Cancel = dialog.findViewById(R.id.btn_cancel);

        Okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean isRemoved = userHelper.removeAvatar(theUserId);
                if(isRemoved) {
                    Toast.makeText(ProfileActivity.this, "Your avatar was removed successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProfileActivity.this, "Error removing avatar, please try again shortly", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });

        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(ProfileActivity.this, "Cancel", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    private void displayDialog(int layoutView) {
        dialog.setContentView(layoutView);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.custom_dialog_background));
        }
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false); //Optional
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation; //Setting the animations to dialog

    }

    public static Bitmap getCroppedBitmap(Bitmap bitmap, int maxSize) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }

        return Bitmap.createScaledBitmap(output, width, height, false);
//        return output;
    }

    private void moveToIntent(Intent intent) {
//        Intent goToSettingsActivityIntent = new Intent(ProjectActivity.this, ProfileActivity.class);

        intent.putExtra(DBUtils.AUTHENTICATED_USER, authenticatedUser);
        startActivity(intent);
        overridePendingTransition(0,0);
    }


    private void loadUserDetails() {
        theUserId = userHelper.getCurrentUserId(authenticatedUser);
        Toast.makeText(this, "USER : " + authenticatedUser, Toast.LENGTH_SHORT).show();
        Bitmap userAvatar = userHelper.getAvatar(theUserId);

        System.out.println("current user no avatar: " + userHelper.getUserDetails(authenticatedUser));
        System.out.println("current user avatar: " + userAvatar);

        if(userAvatar != null) {
            avatarProgressBar.setVisibility(View.GONE);
            User userDetails = userHelper.getUserDetails(authenticatedUser);
            profileFirstNameEditText.setText(userDetails.getFirstName());
            profileLastNameEditText.setText(userDetails.getLastName());
            profileEmailAddressEditText.setText(userDetails.getEmailAddress());
            profilePasswordEditText.setText(userDetails.getPassword());
            userAvatarImageView.setImageBitmap(userAvatar);


//            Bitmap bitmap = (Bitmap) userAndAvatarDetails.get(4);
//            profileFirstNameEditText.setText(userAndAvatarDetails.get(1).toString());
//            profileLastNameEditText.setText(userAndAvatarDetails.get(2).toString());
//            profileEmailAddressEditText.setText(userAndAvatarDetails.get(3).toString());
//
//            userAvatarImageView.setImageBitmap(bitmap);

        } else {
            User userDetails = userHelper.getUserDetails(authenticatedUser);
            profileFirstNameEditText.setText(userDetails.getFirstName());
            profileLastNameEditText.setText(userDetails.getLastName());
            profileEmailAddressEditText.setText(userDetails.getEmailAddress());
        }






    }

    private void updateUserDetails() {

        String firstName = profileFirstNameEditText.getText().toString().trim();
        String lastName = profileLastNameEditText.getText().toString().trim();
        String emailAddress = profileEmailAddressEditText.getText().toString().trim();
        String password = profilePasswordEditText.getText().toString().trim();

        long userId = userHelper.getCurrentUserId(authenticatedUser);

        User user = new User(userId, firstName, lastName, emailAddress, password);




//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                SharedPreferences preferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
//                SharedPreferences.Editor editor = preferences.edit();
//                editor.putBoolean("saveLogin", false);
//                editor.apply();
//                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
//            }
//        }, 1500);




        if(userAvatarImageView.getDrawable() != null && selectedAvatar != null) {
            UUID uuid = UUID. randomUUID();

            UserAvatar userAvatar = new UserAvatar(String.valueOf(uuid), selectedAvatar);

            boolean result = userHelper.saveAvatar(userAvatar, authenticatedUser);
            boolean res = userHelper.editUserDetails(user);
            System.out.println("did update? : " + res);
            Toast.makeText(this, "Avatar uploaded with user: " + authenticatedUser, Toast.LENGTH_SHORT).show();

        } else {
            boolean res = userHelper.editUserDetails(user);

            System.out.println("did update? : " + res);

        }

    }


}