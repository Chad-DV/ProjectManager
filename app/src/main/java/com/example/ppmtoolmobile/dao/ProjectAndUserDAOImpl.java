package com.example.ppmtoolmobile.dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.ppmtoolmobile.ProfileActivity;
import com.example.ppmtoolmobile.model.Priority;
import com.example.ppmtoolmobile.model.Project;
import com.example.ppmtoolmobile.model.User;
import com.example.ppmtoolmobile.model.UserAvatar;
import com.example.ppmtoolmobile.utils.PasswordUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLOutput;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ProjectAndUserDAOImpl extends SQLiteOpenHelper implements ProjectAndUserDAO {

    public static final String DATABASE_NAME = "ppmtool.db";
    public static final String USER_TABLE = "user";
    public static final String COLUMN_USER_ID = "id";
    public static final String COLUMN_USER_FIRST_NAME = "first_name";
    public static final String COLUMN_USER_LAST_NAME = "last_name";
    public static final String COLUMN_USER_EMAIL_ADDRESS = "email_address";
    public static final String COLUMN_USER_PASSWORD = "password";
    public static final String COLUMN_USER_PASSWORD_SALT = "salt";

    public static final String PROJECT_TABLE = "project";
    public static final String COLUMN_PROJECT_ID = "id";
    public static final String COLUMN_PROJECT_TITLE = "title";
    public static final String COLUMN_PROJECT_DESCRIPTION = "description";
    public static final String COLUMN_PROJECT_DATE_CREATED = "date_created";
    public static final String COLUMN_PROJECT_DATE_DUE = "date_due";
    public static final String COLUMN_PROJECT_PRIORITY = "priority";
    public static final String COLUMN_PROJECT_CHECKLIST = "checklist";
    public static final String COLUMN_PROJECT_REMIND_ME_INTERVAL = "remind_me_interval";
    public static final String COLUMN_PROJECT_STATUS = "status";
    public static final String COLUMN_USER_PROJECT_FK = "user_id";

    public static final String USER_AVATAR_TABLE = "user_avatar";
    public static final String COLUMN_USER_AVATAR_NAME = "avatar_name";
    public static final String COLUMN_USER_AVATAR_BLOB = "avatar";
    public static final String COLUMN_USER_AVATAR_PK = "user_id";

    private Context context;
    private ByteArrayOutputStream avatarOutputStream;
    private byte[] avatarByteArray;

    private static int DATABASE_VERSION = 7;


    private String CREATE_USER_TABLE_QUERY = "CREATE TABLE " + USER_TABLE + "(" + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_USER_FIRST_NAME + " TEXT,"
            + COLUMN_USER_LAST_NAME + " TEXT," + COLUMN_USER_EMAIL_ADDRESS + " TEXT," + COLUMN_USER_PASSWORD + " TEXT," + COLUMN_USER_PASSWORD_SALT + " TEXT)";

    private String CREATE_PROJECT_TABLE_QUERY = "CREATE TABLE " + PROJECT_TABLE + "(" + COLUMN_PROJECT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_PROJECT_TITLE + " TEXT,"
            + COLUMN_PROJECT_DESCRIPTION + " TEXT," + COLUMN_PROJECT_DATE_CREATED + " TEXT," + COLUMN_PROJECT_DATE_DUE + " TEXT,"
            + COLUMN_PROJECT_PRIORITY + " TEXT," + COLUMN_PROJECT_REMIND_ME_INTERVAL + " TEXT," + COLUMN_PROJECT_CHECKLIST + " TEXT," + COLUMN_PROJECT_STATUS + " INTEGER, " + COLUMN_USER_PROJECT_FK + " INTEGER,"
            + "FOREIGN KEY(" + COLUMN_USER_PROJECT_FK + ") REFERENCES " + USER_TABLE + "(" + COLUMN_USER_ID + ") ON DELETE CASCADE ON UPDATE CASCADE)";

    private String CREATE_USER_AVATAR_TABLE_QUERY = "CREATE TABLE " + USER_AVATAR_TABLE + "(" + COLUMN_USER_AVATAR_NAME + " TEXT, " + COLUMN_USER_AVATAR_BLOB + " BLOB," + COLUMN_USER_AVATAR_PK + " INTEGER UNIQUE,"
            + "FOREIGN KEY(" + COLUMN_USER_AVATAR_PK + ") REFERENCES " + USER_TABLE + "(" + COLUMN_USER_ID + ") ON DELETE CASCADE ON UPDATE CASCADE)";


    private String DROP_USER_TABLE_QUERY = "DROP TABLE IF EXISTS " + USER_TABLE;
    private String DROP_PROJECT_TABLE_QUERY = "DROP TABLE IF EXISTS " + PROJECT_TABLE;
    private String DROP_USER_AVATAR_TABLE_QUERY = "DROP TABLE IF EXISTS " + USER_AVATAR_TABLE;


    public ProjectAndUserDAOImpl(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;

    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_USER_TABLE_QUERY);
        db.execSQL(CREATE_PROJECT_TABLE_QUERY);
        db.execSQL(CREATE_USER_AVATAR_TABLE_QUERY);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        //Drop User Table if exist
        db.execSQL(DROP_USER_TABLE_QUERY);
        db.execSQL(DROP_PROJECT_TABLE_QUERY);
        db.execSQL(DROP_USER_AVATAR_TABLE_QUERY);

        // Create tables again
        onCreate(db);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public Boolean register(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        long result = 0;

        String password = user.getPassword();
        String salt = PasswordUtils.getSalt(30);
        String mySecurePassword = PasswordUtils.generateSecurePassword(password, salt);


        cv.put(COLUMN_USER_FIRST_NAME, user.getFirstName());
        cv.put(COLUMN_USER_LAST_NAME, user.getLastName());
        cv.put(COLUMN_USER_EMAIL_ADDRESS, user.getEmailAddress());
        cv.put(COLUMN_USER_PASSWORD, mySecurePassword);
        cv.put(COLUMN_USER_PASSWORD_SALT, salt);


        if(isEmailExists(user.getEmailAddress())) {
            Toast.makeText(context.getApplicationContext(), "User already exists with this Email Address.", Toast.LENGTH_SHORT).show();
        } else {
            result = db.insert(USER_TABLE, null, cv);
            Toast.makeText(context.getApplicationContext(), "Your account was created successfully", Toast.LENGTH_SHORT).show();
        }

        return result == -1 ? false : true;


    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public Boolean login(User user) {
        SQLiteDatabase db = this.getReadableDatabase();
        boolean valid = false;

        String password = user.getPassword();
        Cursor cursor = db.query(USER_TABLE,// Selecting Table
                new String[]{COLUMN_USER_ID, COLUMN_USER_FIRST_NAME, COLUMN_USER_LAST_NAME, COLUMN_USER_EMAIL_ADDRESS, COLUMN_USER_PASSWORD, COLUMN_USER_PASSWORD_SALT},//Selecting columns want to query
                COLUMN_USER_EMAIL_ADDRESS + " = ? ",
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
        Cursor cursor = db.query(USER_TABLE,// Selecting Table
                new String[]{COLUMN_USER_EMAIL_ADDRESS},//Selecting columns want to query
                COLUMN_USER_EMAIL_ADDRESS + " = ?",
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

        Cursor cursor = db.query(USER_TABLE,// Selecting Table
                new String[]{COLUMN_USER_FIRST_NAME},//Selecting columns want to query
                COLUMN_USER_EMAIL_ADDRESS + " = ?",
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

        Cursor cursor = db.query(USER_TABLE,// Selecting Table
                new String[]{COLUMN_USER_ID},//Selecting columns want to query
                COLUMN_USER_EMAIL_ADDRESS + " = ?",
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
        Cursor cursor = db.query(USER_TABLE,// Selecting Table
                new String[]{COLUMN_USER_ID, COLUMN_USER_FIRST_NAME, COLUMN_USER_LAST_NAME, COLUMN_USER_EMAIL_ADDRESS, COLUMN_USER_PASSWORD},//Selecting columns want to query
                COLUMN_USER_EMAIL_ADDRESS + " = ?",
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


        Cursor cursor = db.query(USER_AVATAR_TABLE,// Selecting Table
                new String[]{COLUMN_USER_AVATAR_BLOB},//Selecting columns want to query
                COLUMN_USER_AVATAR_PK + " = ?",
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

        Cursor cursor = db.query(USER_AVATAR_TABLE,// Selecting Table
                new String[]{COLUMN_USER_AVATAR_BLOB},//Selecting columns want to query
                COLUMN_USER_AVATAR_PK + " = ?",
                new String[]{String.valueOf(theUserId)},//Where clause
                null, null, null);


        while(cursor.moveToNext()) {
            result = db.delete(USER_AVATAR_TABLE, COLUMN_USER_AVATAR_PK + " = ?", new String[]{String.valueOf(theUserId)});
        }

        closeCursor(cursor);
        return result == -1 ? false : true;
    }

    @Override
    public Boolean editUserDetails(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        boolean success = false;

        cv.put(COLUMN_USER_FIRST_NAME, user.getFirstName());
        cv.put(COLUMN_USER_LAST_NAME, user.getLastName());
        cv.put(COLUMN_USER_EMAIL_ADDRESS, user.getEmailAddress());
        cv.put(COLUMN_USER_PASSWORD, user.getPassword());


        Cursor cursor = db.rawQuery("SELECT " + COLUMN_USER_EMAIL_ADDRESS + " FROM " + USER_TABLE + " WHERE " + COLUMN_USER_EMAIL_ADDRESS + " = ?", new String[]{String.valueOf(user.getEmailAddress())});

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
            db.update(USER_TABLE, cv, COLUMN_USER_ID + " = ?", new String[]{String.valueOf(user.getId())});
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
            Cursor cursor = dbRead.rawQuery("SELECT " + COLUMN_USER_ID + " FROM " + USER_TABLE + " WHERE " + COLUMN_USER_EMAIL_ADDRESS + " = ?", new String[]{emailAddress});

            while(cursor.moveToNext()) {
                userId = cursor.getLong(0);
                cv.put(COLUMN_USER_AVATAR_NAME, userAvatar.getAvatarName());
                cv.put(COLUMN_USER_AVATAR_BLOB, avatarByteArray);
                cv.put(COLUMN_USER_AVATAR_PK, userId);
            }

            Cursor cursor2 = dbRead.rawQuery("SELECT " + COLUMN_USER_AVATAR_NAME + " FROM " + USER_AVATAR_TABLE + " WHERE " + COLUMN_USER_AVATAR_PK + " = ?", new String[]{String.valueOf(userId)});

            if(cursor2.getCount() > 0) {
                result = dbWrite.update(USER_AVATAR_TABLE, cv,COLUMN_USER_AVATAR_PK + " = " + userId, null);
            } else {
                result = dbWrite.insert(USER_AVATAR_TABLE,null, cv);
            }

            closeCursor(cursor);
            return result == -1 ? false : true;



    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public Boolean addProject(Project project, String emailAddress) {
        SQLiteDatabase dbWrite = this.getWritableDatabase();
        SQLiteDatabase dbRead = this.getReadableDatabase();
        ContentValues cv = new ContentValues();
        long result = 0;
        long userId = 0;

        Cursor cursor = dbRead.rawQuery("SELECT " + COLUMN_USER_ID + " FROM " + USER_TABLE + " WHERE " + COLUMN_USER_EMAIL_ADDRESS + " = ?", new String[]{emailAddress});

        while(cursor.moveToNext()) {
            userId = cursor.getLong(0);
            cv.put(COLUMN_PROJECT_TITLE, project.getTitle());
            cv.put(COLUMN_PROJECT_DESCRIPTION, project.getDescription());
            cv.put(COLUMN_PROJECT_DATE_CREATED, project.getDateCreated().toString());
            cv.put(COLUMN_PROJECT_DATE_DUE, project.getDateDue().toString());
            cv.put(COLUMN_PROJECT_PRIORITY, project.getPriority());
            cv.put(COLUMN_PROJECT_REMIND_ME_INTERVAL, project.getRemindMeInterval());
            cv.put(COLUMN_PROJECT_CHECKLIST, project.getChecklist());
            cv.put(COLUMN_PROJECT_STATUS, project.isStatus());
            cv.put(COLUMN_USER_PROJECT_FK, userId);
        }

        if(isDuplicateProject(userId, project.getTitle())) {
            Toast.makeText(context.getApplicationContext(), "Project already exists with this title.", Toast.LENGTH_SHORT).show();
            return false;
        }


        result = dbWrite.insert(PROJECT_TABLE, null, cv);
        closeCursor(cursor);

        return result == -1 ? false : true;

    }

    private Boolean isDuplicateProject(long userId, String title) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + PROJECT_TABLE + " WHERE " + COLUMN_USER_PROJECT_FK + " = ? AND " + COLUMN_PROJECT_TITLE + " = ?",
                new String[]{String.valueOf(userId), title});

        if (cursor != null && cursor.moveToFirst() && cursor.getCount() > 0) {
            return true;
        }
        closeCursor(cursor);
        return false;
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public Boolean editProject(Project project) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        long result = 0;

        cv.put(COLUMN_PROJECT_TITLE, project.getTitle());
        cv.put(COLUMN_PROJECT_DESCRIPTION, project.getDescription());
//        cv.put(COLUMN_PROJECT_DATE_CREATED, project.getDateCreated().toString());
        cv.put(COLUMN_PROJECT_DATE_DUE, project.getDateDue().toString());
        cv.put(COLUMN_PROJECT_PRIORITY, project.getPriority());
        cv.put(COLUMN_PROJECT_REMIND_ME_INTERVAL, project.getRemindMeInterval());
        cv.put(COLUMN_PROJECT_CHECKLIST, project.getChecklist());

        Cursor cursor = db.rawQuery("SELECT * FROM " + PROJECT_TABLE + " WHERE " + COLUMN_PROJECT_ID + " = ?", new String[]{String.valueOf(project.getId())});


        if (cursor.getCount() > 0) {
            result = db.update(PROJECT_TABLE, cv, COLUMN_PROJECT_ID + " = ?", new String[]{String.valueOf(project.getId())});

        }

        db.close();
        closeCursor(cursor);

        return result == -1 ? false : true;

    }

    @Override
    public Boolean deleteProjectById(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = 0;

        Cursor cursor = db.rawQuery("SELECT * FROM " + PROJECT_TABLE + " WHERE " + COLUMN_PROJECT_ID + " = ?", new String[]{String.valueOf(id)});

        if (cursor.getCount() > 0) {
            result = db.delete(PROJECT_TABLE, COLUMN_PROJECT_ID + " = ?", new String[]{String.valueOf(id)});
        }

        closeCursor(cursor);
        db.close();
        return result == -1 ? false : true;
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public Project getProjectById(long projectId) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + PROJECT_TABLE + " WHERE " + COLUMN_PROJECT_ID + " = ?", new String[]{String.valueOf(projectId)});

        if(cursor.moveToNext()) {
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
            int userId = cursor.getInt(9);

            return new Project(id, title, description, dateCreatedFormatted, dateDueFormatted, priority, checklist, remindMeInterval, status, userId);
        }

        closeCursor(cursor);
        return null;
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.O)
    public List<Project> searchProjects(long userId, String query) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Project> projectList = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + PROJECT_TABLE + " WHERE " + COLUMN_USER_PROJECT_FK + " = ? AND " + COLUMN_PROJECT_TITLE + " LIKE ?",
                new String[]{String.valueOf(userId), query});


        if(cursor.getCount() > 0) {
            readDataFromCursor(projectList, cursor);
        }


        return projectList;
    }


    @Override
    public int getProjectCount(long userId) {
        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = db.rawQuery("SELECT * FROM " + PROJECT_TABLE, null);
        Cursor cursor = db.rawQuery("SELECT * FROM " + PROJECT_TABLE + " WHERE " + COLUMN_USER_PROJECT_FK + " = ?", new String[]{String.valueOf(userId)});

        int projectCount = cursor.getCount();
        closeCursor(cursor);
        return projectCount;

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public List<Project> getUserProjects(long userId) {

        SQLiteDatabase db = this.getReadableDatabase();
        List<Project> projectList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + PROJECT_TABLE + " WHERE " + COLUMN_USER_PROJECT_FK + " = ?", new String[]{String.valueOf(userId)});
        readDataFromCursor(projectList, cursor);

        return projectList;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public List<Project> sortByPriorityHighToLow(long userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Project> projectList = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + PROJECT_TABLE + " WHERE " + COLUMN_USER_PROJECT_FK + " = ? " + "ORDER BY " + COLUMN_PROJECT_PRIORITY + " DESC", new String[]{String.valueOf(userId)});
        readDataFromCursor(projectList, cursor);

        return projectList;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public List<Project> sortByPriorityLowToHigh(long userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Project> projectList = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + PROJECT_TABLE + " WHERE " + COLUMN_USER_PROJECT_FK + " = ? "
                + "ORDER BY " + COLUMN_PROJECT_PRIORITY + " ASC", new String[]{String.valueOf(userId)});

        readDataFromCursor(projectList, cursor);

        return projectList;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public List<Project> sortByDateNewestToOldest(long userId) {

        SQLiteDatabase db = getReadableDatabase();
        List<Project> projectList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + PROJECT_TABLE + " WHERE " + COLUMN_USER_PROJECT_FK + " = ? "
                + "ORDER BY date(" + COLUMN_PROJECT_DATE_DUE + ") ASC", new String[]{String.valueOf(userId)});
        readDataFromCursor(projectList, cursor);

        return projectList;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public List<Project> sortByDateOldestToNewest(long userId) {

        SQLiteDatabase db = getReadableDatabase();
        List<Project> projectList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + PROJECT_TABLE + " WHERE " + COLUMN_USER_PROJECT_FK + " = ? "
                + "ORDER BY date(" + COLUMN_PROJECT_DATE_DUE + ") DESC", new String[]{String.valueOf(userId)});

        readDataFromCursor(projectList, cursor);

        return projectList;
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

    private void closeDB(SQLiteDatabase db) {
        if(db != null) {
            db.close();
        }
    }
}

