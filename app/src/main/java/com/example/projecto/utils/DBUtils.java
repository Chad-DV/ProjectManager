package com.example.projecto.utils;

public class DBUtils {

    public static final int USER_AVATAR_MAX_SIZE = 420;
    public static final String AUTHENTICATED_USER = "authenticatedUser";
    public static final String USER_ID = "userId";
    public static final String PROJECT_ID = "projectId";
    public static final String DATABASE_NAME = "ppmtool.db";
    public static final int DATABASE_VERSION = 20;
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
    public static final String COLUMN_USER_PROJECT_FK = "user_id";

    public static final String USER_AVATAR_TABLE = "user_avatar";
    public static final String COLUMN_USER_AVATAR_NAME = "avatar_name";
    public static final String COLUMN_USER_AVATAR_BLOB = "avatar";
    public static final String COLUMN_USER_AVATAR_PK = "user_id";

    public static final String CREATE_USER_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS " + USER_TABLE + "(" + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_USER_FIRST_NAME + " TEXT,"
            + COLUMN_USER_LAST_NAME + " TEXT," + COLUMN_USER_EMAIL_ADDRESS + " TEXT," + COLUMN_USER_PASSWORD + " TEXT," + COLUMN_USER_PASSWORD_SALT + " TEXT)";

    public static final String CREATE_PROJECT_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS " + PROJECT_TABLE + "(" + COLUMN_PROJECT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_PROJECT_TITLE + " TEXT,"
            + COLUMN_PROJECT_DESCRIPTION + " TEXT," + COLUMN_PROJECT_DATE_CREATED + " TEXT," + COLUMN_PROJECT_DATE_DUE + " TEXT,"
            + COLUMN_PROJECT_PRIORITY + " TEXT," + COLUMN_PROJECT_REMIND_ME_INTERVAL + " TEXT," + COLUMN_PROJECT_CHECKLIST + " TEXT," + COLUMN_USER_PROJECT_FK + " INTEGER,"
            + "FOREIGN KEY(" + COLUMN_USER_PROJECT_FK + ") REFERENCES " + USER_TABLE + "(" + COLUMN_USER_ID + ") ON DELETE CASCADE ON UPDATE CASCADE)";

    public static final String CREATE_USER_AVATAR_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS " + USER_AVATAR_TABLE + "(" + COLUMN_USER_AVATAR_NAME + " TEXT, " + COLUMN_USER_AVATAR_BLOB + " BLOB," + COLUMN_USER_AVATAR_PK + " INTEGER UNIQUE,"
            + "FOREIGN KEY(" + COLUMN_USER_AVATAR_PK + ") REFERENCES " + USER_TABLE + "(" + COLUMN_USER_ID + ") ON DELETE CASCADE ON UPDATE CASCADE)";


    public static final String DROP_USER_TABLE_QUERY = "DROP TABLE IF EXISTS " + USER_TABLE;
    public static final String DROP_PROJECT_TABLE_QUERY = "DROP TABLE IF EXISTS " + PROJECT_TABLE;
    public static final String DROP_USER_AVATAR_TABLE_QUERY = "DROP TABLE IF EXISTS " + USER_AVATAR_TABLE;
}
