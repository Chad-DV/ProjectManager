package com.example.projecto.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.projecto.model.Project;
import com.example.projecto.utils.DBUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ProjectDAOImpl extends SQLiteOpenHelper implements ProjectDAO{

    private Context context;


    public ProjectDAOImpl(@Nullable Context context) {
        super(context, DBUtils.DATABASE_NAME, null, DBUtils.DATABASE_VERSION);
        this.context = context;

    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DBUtils.CREATE_PROJECT_TABLE_QUERY);
        db.execSQL(DBUtils.CREATE_USER_TABLE_QUERY);
        db.execSQL(DBUtils.CREATE_USER_AVATAR_TABLE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL(DBUtils.DROP_PROJECT_TABLE_QUERY);
        db.execSQL(DBUtils.DROP_USER_TABLE_QUERY);
        db.execSQL(DBUtils.DROP_USER_AVATAR_TABLE_QUERY);
        onCreate(db);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public Boolean addProject(Project project, String emailAddress) {
        SQLiteDatabase dbWrite = this.getWritableDatabase();
        SQLiteDatabase dbRead = this.getReadableDatabase();
        ContentValues cv = new ContentValues();
        long result = 0;
        long userId = 0;

        Cursor cursor = dbRead.rawQuery("SELECT " + DBUtils.COLUMN_USER_ID + " FROM " + DBUtils.USER_TABLE + " WHERE " + DBUtils.COLUMN_USER_EMAIL_ADDRESS + " = ?", new String[]{emailAddress});

        if(cursor.moveToNext()) {
            userId = cursor.getLong(0);
        }
        cv.put(DBUtils.COLUMN_PROJECT_TITLE, project.getTitle());
        cv.put(DBUtils.COLUMN_PROJECT_DESCRIPTION, project.getDescription());
        cv.put(DBUtils.COLUMN_PROJECT_DATE_CREATED, project.getDateCreated().toString());
        cv.put(DBUtils.COLUMN_PROJECT_DATE_DUE, project.getDateDue().toString());
        cv.put(DBUtils.COLUMN_PROJECT_PRIORITY, project.getPriority());
        cv.put(DBUtils.COLUMN_PROJECT_REMIND_ME_INTERVAL, project.getRemindMeInterval());
        cv.put(DBUtils.COLUMN_PROJECT_CHECKLIST, project.getChecklist().toString());
        cv.put(DBUtils.COLUMN_USER_PROJECT_FK, userId);

        if(isDuplicateProject(userId, project.getTitle())) {
            Toast.makeText(context.getApplicationContext(), "Project already exists with this title.", Toast.LENGTH_SHORT).show();
            return false;
        }


        result = dbWrite.insert(DBUtils.PROJECT_TABLE, null, cv);
        closeCursor(cursor);

        return result == -1 ? false : true;

    }

    private Boolean isDuplicateProject(long userId, String title) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + DBUtils.PROJECT_TABLE + " WHERE " + DBUtils.COLUMN_USER_PROJECT_FK + " = ? AND " + DBUtils.COLUMN_PROJECT_TITLE + " = ?",
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
        long userId = 0;


        cv.put(DBUtils.COLUMN_PROJECT_TITLE, project.getTitle());
        cv.put(DBUtils.COLUMN_PROJECT_DESCRIPTION, project.getDescription());
        cv.put(DBUtils.COLUMN_PROJECT_DATE_DUE, project.getDateDue().toString());
        cv.put(DBUtils.COLUMN_PROJECT_PRIORITY, project.getPriority());
        cv.put(DBUtils.COLUMN_PROJECT_REMIND_ME_INTERVAL, project.getRemindMeInterval());
        cv.put(DBUtils.COLUMN_PROJECT_CHECKLIST, project.getChecklist().toString());

        Cursor cursor = db.rawQuery("SELECT * FROM " + DBUtils.PROJECT_TABLE + " WHERE " + DBUtils.COLUMN_PROJECT_ID + " = ?", new String[]{String.valueOf(project.getId())});


        if (cursor.getCount() > 0) {
            result = db.update(DBUtils.PROJECT_TABLE, cv, DBUtils.COLUMN_PROJECT_ID + " = ?", new String[]{String.valueOf(project.getId())});

        }

        db.close();
        closeCursor(cursor);

        return result == -1 ? false : true;

    }

    @Override
    public Boolean deleteProjectById(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = 0;

        Cursor cursor = db.rawQuery("SELECT * FROM " + DBUtils.PROJECT_TABLE + " WHERE " + DBUtils.COLUMN_PROJECT_ID + " = ?", new String[]{String.valueOf(id)});

        if (cursor.getCount() > 0) {
            result = db.delete(DBUtils.PROJECT_TABLE, DBUtils.COLUMN_PROJECT_ID + " = ?", new String[]{String.valueOf(id)});
        }

        closeCursor(cursor);
        db.close();
        return result == -1 ? false : true;
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public Project getProjectById(long projectId) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + DBUtils.PROJECT_TABLE + " WHERE " + DBUtils.COLUMN_PROJECT_ID + " = ?", new String[]{String.valueOf(projectId)});

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
            int userId = cursor.getInt(8);

            return new Project(id, title, description, dateCreatedFormatted, dateDueFormatted, priority, remindMeInterval, checklist, userId);
        }

        closeCursor(cursor);
        return null;
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.O)
    public List<Project> filterProjects(long userId, String query) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Project> projectList = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + DBUtils.PROJECT_TABLE + " WHERE " + DBUtils.COLUMN_USER_PROJECT_FK + " = ? AND " + DBUtils.COLUMN_PROJECT_TITLE + " LIKE ?",
                new String[]{String.valueOf(userId), "%" + query + "%"});


        if(cursor.getCount() > 0) {
            readDataFromCursor(projectList, cursor);
        }


        return projectList;
    }


    @Override
    public int getProjectCount(long userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DBUtils.PROJECT_TABLE + " WHERE " + DBUtils.COLUMN_USER_PROJECT_FK + " = ?", new String[]{String.valueOf(userId)});

        int projectCount = cursor.getCount();
        closeCursor(cursor);
        return projectCount;

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public List<Project> getUserProjects(long userId) {

        SQLiteDatabase db = this.getReadableDatabase();
        List<Project> projectList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DBUtils.PROJECT_TABLE + " WHERE " + DBUtils.COLUMN_USER_PROJECT_FK + " = ?", new String[]{String.valueOf(userId)});
        readDataFromCursor(projectList, cursor);

        return projectList;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public List<Project> sortByPriorityHighToLow(long userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Project> projectList = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + DBUtils.PROJECT_TABLE + " WHERE " + DBUtils.COLUMN_USER_PROJECT_FK + " = ? " + "ORDER BY " + DBUtils.COLUMN_PROJECT_PRIORITY + " DESC", new String[]{String.valueOf(userId)});
        readDataFromCursor(projectList, cursor);

        return projectList;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public List<Project> sortByPriorityLowToHigh(long userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Project> projectList = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + DBUtils.PROJECT_TABLE + " WHERE " + DBUtils.COLUMN_USER_PROJECT_FK + " = ? "
                + "ORDER BY " + DBUtils.COLUMN_PROJECT_PRIORITY + " ASC", new String[]{String.valueOf(userId)});

        readDataFromCursor(projectList, cursor);

        return projectList;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public List<Project> sortByDateNewestToOldest(long userId) {

        SQLiteDatabase db = getReadableDatabase();
        List<Project> projectList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DBUtils.PROJECT_TABLE + " WHERE " + DBUtils.COLUMN_USER_PROJECT_FK + " = ? "
                + "ORDER BY date(" + DBUtils.COLUMN_PROJECT_DATE_DUE + ") ASC", new String[]{String.valueOf(userId)});
        readDataFromCursor(projectList, cursor);

        return projectList;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public List<Project> sortByDateOldestToNewest(long userId) {

        SQLiteDatabase db = getReadableDatabase();
        List<Project> projectList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DBUtils.PROJECT_TABLE + " WHERE " + DBUtils.COLUMN_USER_PROJECT_FK + " = ? "
                + "ORDER BY date(" + DBUtils.COLUMN_PROJECT_DATE_DUE + ") DESC", new String[]{String.valueOf(userId)});

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
            int theUserId = cursor.getInt(8);

            Project project = new Project(id, title, description, dateCreatedFormatted, dateDueFormatted, priority, remindMeInterval , checklist, theUserId);

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
