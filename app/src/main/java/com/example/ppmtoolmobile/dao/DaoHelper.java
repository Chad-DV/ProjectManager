package com.example.ppmtoolmobile.dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.ppmtoolmobile.model.Priority;
import com.example.ppmtoolmobile.model.Project;
import com.example.ppmtoolmobile.model.User;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DaoHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "ppmtool.db";
    public static final String USER_TABLE = "user";
    public static final String COLUMN_USER_ID = "id";
    public static final String COLUMN_USER_FIRST_NAME = "first_name";
    public static final String COLUMN_USER_LAST_NAME = "last_name";
    public static final String COLUMN_USER_EMAIL_ADDRESS = "email_address";
    public static final String COLUMN_USER_PASSWORD = "password";
    public static final String PROJECT_TABLE = "project";
    public static final String COLUMN_PROJECT_ID = "id";
    public static final String COLUMN_PROJECT_TITLE = "title";
    public static final String COLUMN_PROJECT_DESCRIPTION = "description";
    public static final String COLUMN_PROJECT_DATE_CREATED = "date_created";
    public static final String COLUMN_PROJECT_DATE_DUE = "date_due";
    public static final String COLUMN_PROJECT_DUE_TIME = "due_time";
    public static final String COLUMN_PROJECT_PRIORITY = "priority";
    public static final String COLUMN_PROJECT_CHECKLIST = "checklist";
    public static final String COLUMN_USER_PROJECT_FK = "user_id";


    private String CREATE_USER_TABLE_QUERY = "CREATE TABLE " + USER_TABLE + "(" + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_USER_FIRST_NAME + " TEXT,"
            + COLUMN_USER_LAST_NAME + " TEXT," + COLUMN_USER_EMAIL_ADDRESS + " TEXT," + COLUMN_USER_PASSWORD + " TEXT" + ")";

    private String CREATE_PROJECT_TABLE_QUERY = "CREATE TABLE " + PROJECT_TABLE +  "(" + COLUMN_PROJECT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_PROJECT_TITLE + " TEXT,"
            + COLUMN_PROJECT_DESCRIPTION + " TEXT," + COLUMN_PROJECT_DATE_CREATED + " TEXT," + COLUMN_PROJECT_DATE_DUE + " TEXT,"
            + COLUMN_PROJECT_PRIORITY + " TEXT," + COLUMN_PROJECT_CHECKLIST + " TEXT," + COLUMN_USER_PROJECT_FK + " INTEGER,"
            + "FOREIGN KEY(" + COLUMN_USER_PROJECT_FK +") REFERENCES " + USER_TABLE + "(" + COLUMN_USER_ID +") ON DELETE CASCADE ON UPDATE CASCADE)";


    private String DROP_USER_TABLE_QUERY = "DROP TABLE IF EXISTS " + USER_TABLE;
    private String DROP_PROJECT_TABLE_QUERY = "DROP TABLE IF EXISTS " + PROJECT_TABLE;



    public DaoHelper(@Nullable Context context) {
        super(context,DATABASE_NAME, null, 1);
    }




    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER_TABLE_QUERY);
        db.execSQL(CREATE_PROJECT_TABLE_QUERY);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        //Drop User Table if exist
        db.execSQL(DROP_USER_TABLE_QUERY);
        db.execSQL(DROP_PROJECT_TABLE_QUERY);
        // Create tables again
        onCreate(db);
    }


    public void register(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_USER_FIRST_NAME, user.getFirstName());
        cv.put(COLUMN_USER_LAST_NAME, user.getLastName());
        cv.put(COLUMN_USER_EMAIL_ADDRESS, user.getEmailAddress());
        cv.put(COLUMN_USER_PASSWORD, user.getPassword());

        long result = db.insert(USER_TABLE, null, cv);

        System.out.println("User created successfully: " + result);
//        if(result == -1) {
//            return false;
//        } else {
//            return true;
//        }


    }


    public boolean login(User user) {
        SQLiteDatabase db = this.getReadableDatabase();
        boolean valid = false;


        Cursor cursor = db.query(USER_TABLE,// Selecting Table
                new String[]{COLUMN_USER_ID, COLUMN_USER_FIRST_NAME, COLUMN_USER_LAST_NAME, COLUMN_USER_EMAIL_ADDRESS, COLUMN_USER_PASSWORD},//Selecting columns want to query
                COLUMN_USER_EMAIL_ADDRESS + "=?",
                new String[]{user.getEmailAddress()},//Where clause
                null, null, null);


        if (cursor != null && cursor.moveToFirst()&& cursor.getCount() > 0) {
            //if cursor has value then in user database there is user associated with this given email
            User user1 = new User(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));

            //Match both passwords check they are same or not
            if (user.getPassword().equalsIgnoreCase(user1.getPassword())) {
                valid = true;
            } else {
                valid = false;
            }
        }

        cursor.close();
        return valid;
    }

    public boolean isEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(USER_TABLE,// Selecting Table
                new String[]{COLUMN_USER_ID, COLUMN_USER_FIRST_NAME, COLUMN_USER_LAST_NAME, COLUMN_USER_EMAIL_ADDRESS, COLUMN_USER_PASSWORD},//Selecting columns want to query
                COLUMN_USER_EMAIL_ADDRESS + "=?",
                new String[]{email},//Where clause
                null, null, null);

        if (cursor != null && cursor.moveToFirst()&& cursor.getCount()>0) {
            System.out.println("User with email " + email + " already exists");
            //if cursor has value then in user database there is user associated with this given email so return true
            return true;
        }

        cursor.close();

        System.out.println("Email does not exist");
        //if email does not exist return false
        return false;
    }


    public void addProject(Project project) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_PROJECT_TITLE, project.getTitle());
        cv.put(COLUMN_PROJECT_DESCRIPTION, project.getDescription());
        cv.put(COLUMN_PROJECT_DATE_CREATED, project.getDateCreated().toString());
        cv.put(COLUMN_PROJECT_DATE_DUE, project.getDateDue().toString());
        cv.put(COLUMN_PROJECT_PRIORITY, project.getPriority());
//        cv.put(COLUMN_USER_PROJECT_FK, project.get);

        db.insert(PROJECT_TABLE, null, cv);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public List<Project> getAllProjects() {


        SQLiteDatabase db = this.getReadableDatabase();
        List<Project> projectList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + PROJECT_TABLE, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String title = cursor.getString(1);
                String description = cursor.getString(2);
//                String dateCreated = cursor.getString(3);
                String dateDue = cursor.getString(4);
                String priority = cursor.getString(5);
                String checklist = cursor.getString(6);
                int userId = cursor.getInt(7);

                Project project = new Project(id, title, description, LocalDateTime.now(), priority, checklist, userId);

                projectList.add(project);


            } while (cursor.moveToNext());

            System.out.println(projectList);

            cursor.close();


        }
        return null;
    }

    public List<Project> getUserProjects(int id) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(PROJECT_TABLE,
                new String[]{COLUMN_PROJECT_ID, COLUMN_PROJECT_TITLE},
                COLUMN_USER_PROJECT_FK + " = " + id,
                null, null, null, null, null);

        String query = "SELECT * FROM " + PROJECT_TABLE + " WHERE " + COLUMN_USER_PROJECT_FK + " = " + id;



        return null;
    }


}
