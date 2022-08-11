package com.example.ppmtoolmobile.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.ppmtoolmobile.ProfileActivity;
import com.example.ppmtoolmobile.model.Project;
import com.example.ppmtoolmobile.model.User;
import com.example.ppmtoolmobile.model.UserAvatar;
import com.example.ppmtoolmobile.utils.DBUtils;
import com.example.ppmtoolmobile.utils.PasswordUtils;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class UserDAOImpl extends SQLiteOpenHelper implements UserDAO {

    private Context context;
    private ByteArrayOutputStream avatarOutputStream;
    private byte[] avatarByteArray;

    public UserDAOImpl(@Nullable Context context) {
        super(context, DBUtils.DATABASE_NAME, null, DBUtils.DATABASE_VERSION);
        this.context = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i("Warn", "Creating db");
        db.execSQL(DBUtils.CREATE_PROJECT_TABLE_QUERY);
        db.execSQL(DBUtils.CREATE_USER_TABLE_QUERY);
        db.execSQL(DBUtils.CREATE_USER_AVATAR_TABLE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        Log.i("Warn", "Updating db");
        db.execSQL(DBUtils.DROP_PROJECT_TABLE_QUERY);
        db.execSQL(DBUtils.DROP_USER_TABLE_QUERY);
        db.execSQL(DBUtils.DROP_USER_AVATAR_TABLE_QUERY);
        onCreate(db);
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public Boolean register(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        boolean valid = false;

        String password = user.getPassword();
        String salt = PasswordUtils.getSalt(30);
        String mySecurePassword = PasswordUtils.generateSecurePassword(password, salt);


        cv.put(DBUtils.COLUMN_USER_FIRST_NAME, user.getFirstName());
        cv.put(DBUtils.COLUMN_USER_LAST_NAME, user.getLastName());
        cv.put(DBUtils.COLUMN_USER_EMAIL_ADDRESS, user.getEmailAddress());
        cv.put(DBUtils.COLUMN_USER_PASSWORD, mySecurePassword);
        cv.put(DBUtils.COLUMN_USER_PASSWORD_SALT, salt);


        if(isEmailExists(user.getEmailAddress())) {
            Toast.makeText(context.getApplicationContext(), "User already exists with this Email Address.", Toast.LENGTH_SHORT).show();
        } else {
            db.insert(DBUtils.USER_TABLE, null, cv);
            valid = true;
            Toast.makeText(context.getApplicationContext(), "Your account was created successfully", Toast.LENGTH_SHORT).show();
        }

        return valid;

    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public Boolean login(User user) {
        SQLiteDatabase db = this.getReadableDatabase();
        boolean valid = false;

        String password = user.getPassword();
        Cursor cursor = db.query(DBUtils.USER_TABLE,// Selecting Table
                new String[]{DBUtils.COLUMN_USER_ID, DBUtils.COLUMN_USER_FIRST_NAME, DBUtils.COLUMN_USER_LAST_NAME, DBUtils.COLUMN_USER_EMAIL_ADDRESS, DBUtils.COLUMN_USER_PASSWORD, DBUtils.COLUMN_USER_PASSWORD_SALT},//Selecting columns want to query
                DBUtils.COLUMN_USER_EMAIL_ADDRESS + " = ? ",
                new String[]{user.getEmailAddress()},//Where clause
                null, null, null);


        if (cursor != null && cursor.moveToFirst() && cursor.getCount() > 0) {
            //if cursor has value then in user database there is user associated with this given email
            User user1 = new User(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));

            String securePassword  = user1.getPassword();
            String salt = cursor.getString(5);

            boolean passwordMatch = PasswordUtils.verifyUserPassword(password, securePassword, salt);

            if(passwordMatch) {
                System.out.println("Provided user password " + password + " is correct.");
                valid = true;
            } else {
                System.out.println("Provided password is incorrect");
            }

        }

        closeCursor(cursor);
        return valid;
    }

    private Boolean isEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(DBUtils.USER_TABLE,// Selecting Table
                new String[]{DBUtils.COLUMN_USER_EMAIL_ADDRESS},//Selecting columns want to query
                DBUtils.COLUMN_USER_EMAIL_ADDRESS + " = ?",
                new String[]{email},//Where clause
                null, null, null);

        if (cursor != null && cursor.moveToFirst() && cursor.getCount() > 0) {
            return true;
        }

        closeCursor(cursor);
        return false;
    }

    @Override
    public String getCurrentUserFirstName(String emailAddress) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(DBUtils.USER_TABLE,// Selecting Table
                new String[]{DBUtils.COLUMN_USER_FIRST_NAME},//Selecting columns want to query
                DBUtils.COLUMN_USER_EMAIL_ADDRESS + " = ?",
                new String[]{String.valueOf(emailAddress)},//Where clause
                null, null, null);

        String firstName = null;

        while(cursor.moveToNext()) {
            firstName = cursor.getString(0);
        }


        closeCursor(cursor);
        return firstName;
    }

    @Override
    public long getCurrentUserId(String emailAddress) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(DBUtils.USER_TABLE,// Selecting Table
                new String[]{DBUtils.COLUMN_USER_ID},//Selecting columns want to query
                DBUtils.COLUMN_USER_EMAIL_ADDRESS + " = ?",
                new String[]{String.valueOf(emailAddress)},//Where clause
                null, null, null);

        long userId = 0;

        while(cursor.moveToNext()) {
            userId = cursor.getLong(0);
        }

        closeCursor(cursor);
        return userId;

    }

    @Override
    public User getUserDetails(String theEmailAddress) {
        SQLiteDatabase db = this.getReadableDatabase();
        User user = null;
        Cursor cursor = db.query(DBUtils.USER_TABLE,// Selecting Table
                new String[]{DBUtils.COLUMN_USER_ID, DBUtils.COLUMN_USER_FIRST_NAME, DBUtils.COLUMN_USER_LAST_NAME, DBUtils.COLUMN_USER_EMAIL_ADDRESS, DBUtils.COLUMN_USER_PASSWORD},//Selecting columns want to query
                DBUtils.COLUMN_USER_EMAIL_ADDRESS + " = ?",
                new String[]{String.valueOf(theEmailAddress)},//Where clause
                null, null, null);

        System.out.println("cursor count: " + cursor.getCount());

        while(cursor.moveToNext()) {
            long userId = cursor.getLong(0);
            String firstName = cursor.getString(1);
            String lastName = cursor.getString(2);
            String emailAddress = cursor.getString(3);
            String password = cursor.getString(4);

            user = new User(userId, firstName, lastName, emailAddress, password);
        }


        closeCursor(cursor);
        return user;
    }

    @Override
    public Bitmap getAvatar(long theUserId) {
        SQLiteDatabase db = this.getReadableDatabase();
        User user = null;
        List<Object> userDetails = new ArrayList<>();
        Bitmap obj = null;


        Cursor cursor = db.query(DBUtils.USER_AVATAR_TABLE,// Selecting Table
                new String[]{DBUtils.COLUMN_USER_AVATAR_BLOB},//Selecting columns want to query
                DBUtils.COLUMN_USER_AVATAR_PK + " = ?",
                new String[]{String.valueOf(theUserId)},//Where clause
                null, null, null);

        while(cursor.moveToNext()) {
            byte[] blob = cursor.getBlob(0);
            obj = BitmapFactory.decodeByteArray(blob, 0, blob.length);
            obj = ProfileActivity.getCroppedBitmap(obj, 375);

        }

        closeCursor(cursor);
        return obj;
    }

    @Override
    public Boolean removeAvatar(long theUserId) {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean success = false;
        long result = 0;

        Cursor cursor = db.query(DBUtils.USER_AVATAR_TABLE,// Selecting Table
                new String[]{DBUtils.COLUMN_USER_AVATAR_BLOB},//Selecting columns want to query
                DBUtils.COLUMN_USER_AVATAR_PK + " = ?",
                new String[]{String.valueOf(theUserId)},//Where clause
                null, null, null);


        while(cursor.moveToNext()) {
            result = db.delete(DBUtils.USER_AVATAR_TABLE, DBUtils.COLUMN_USER_AVATAR_PK + " = ?", new String[]{String.valueOf(theUserId)});
        }

        closeCursor(cursor);
        return result == -1 ? false : true;
    }

    @Override
    public Boolean editUserDetails(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        boolean success = false;

        cv.put(DBUtils.COLUMN_USER_FIRST_NAME, user.getFirstName());
        cv.put(DBUtils.COLUMN_USER_LAST_NAME, user.getLastName());
        cv.put(DBUtils.COLUMN_USER_EMAIL_ADDRESS, user.getEmailAddress());
        cv.put(DBUtils.COLUMN_USER_PASSWORD, user.getPassword());


        Cursor cursor = db.rawQuery("SELECT " + DBUtils.COLUMN_USER_EMAIL_ADDRESS + " FROM " + DBUtils.USER_TABLE + " WHERE " + DBUtils.COLUMN_USER_EMAIL_ADDRESS + " = ?", new String[]{String.valueOf(user.getEmailAddress())});

        // if new email is unique, true
        if(!isEmailExists(user.getEmailAddress())) {
            System.out.println("email is unique, updated");
            success = true;
        }


        if(cursor.moveToFirst() && user.getEmailAddress().equals(cursor.getString(0))) {
            success = true;
            System.out.println("current email is not unique with itself");
        }

        if(success == true) {
            db.update(DBUtils.USER_TABLE, cv, DBUtils.COLUMN_USER_ID + " = ?", new String[]{String.valueOf(user.getId())});
        }

        closeCursor(cursor);

        return success;
    }

    @Override
    public Boolean saveAvatar(UserAvatar userAvatar, String emailAddress) {

        SQLiteDatabase dbWrite = this.getWritableDatabase();
        SQLiteDatabase dbRead = this.getReadableDatabase();
        Bitmap avatar = userAvatar.getAvatar();
        long userId = 0;
        long result = 0;

        avatarOutputStream = new ByteArrayOutputStream();
        avatar.compress(Bitmap.CompressFormat.JPEG, 100, avatarOutputStream);

        avatarByteArray = avatarOutputStream.toByteArray();

        ContentValues cv = new ContentValues();
        Cursor cursor = dbRead.rawQuery("SELECT " + DBUtils.COLUMN_USER_ID + " FROM " + DBUtils.USER_TABLE + " WHERE " + DBUtils.COLUMN_USER_EMAIL_ADDRESS + " = ?", new String[]{emailAddress});

        while(cursor.moveToNext()) {
            userId = cursor.getLong(0);
            cv.put(DBUtils.COLUMN_USER_AVATAR_NAME, userAvatar.getAvatarName());
            cv.put(DBUtils.COLUMN_USER_AVATAR_BLOB, avatarByteArray);
            cv.put(DBUtils.COLUMN_USER_AVATAR_PK, userId);
        }

        Cursor cursor2 = dbRead.rawQuery("SELECT " + DBUtils.COLUMN_USER_AVATAR_NAME + " FROM " + DBUtils.USER_AVATAR_TABLE + " WHERE " + DBUtils.COLUMN_USER_AVATAR_PK + " = ?", new String[]{String.valueOf(userId)});

        if(cursor2.getCount() > 0) {
            result = dbWrite.update(DBUtils.USER_AVATAR_TABLE, cv,DBUtils.COLUMN_USER_AVATAR_PK + " = " + userId, null);
        } else {
            result = dbWrite.insert(DBUtils.USER_AVATAR_TABLE,null, cv);
        }

        closeCursor(cursor);
        return result == -1 ? false : true;



    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void readDataFromCursor(List<Project> projectList, Cursor cursor) {
        while(cursor.moveToNext()) {
            long id = cursor.getLong(0);
            String title = cursor.getString(1);
            String description = cursor.getString(2);
            String dateCreated = cursor.getString(3);
            String dateDue = cursor.getString(4);

            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
            LocalDateTime dateCreatedFormatted = LocalDateTime.parse(dateCreated, formatter);
            LocalDateTime dateDueFormatted = LocalDateTime.parse(dateDue, formatter);

            String priority = cursor.getString(5);
            String remindMeInterval = cursor.getString(6);
            String checklist = cursor.getString(7);
            boolean status = cursor.getInt(8) > 0;
            int theUserId = cursor.getInt(9);

            Project project = new Project(id, title, description, dateCreatedFormatted, dateDueFormatted, priority, checklist, remindMeInterval, status, theUserId);

            projectList.add(project);
        }

        closeCursor(cursor);

    }

    private void closeCursor(Cursor cursor) {
        if(cursor != null) {
            cursor.close();
        }
    }

}
