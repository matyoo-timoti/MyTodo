package com.asterisk.mytodo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class ToDoDbHelper extends SQLiteOpenHelper {
    static final int DATABASE_VERSION = 1;
    static final String DATABASE_NAME = "ToDoDB";
    static final String DATABASE_TABLE = "ToDo";
    static final String COLUMN_ID = "_id";
    static final String COLUMN_TASK = "task";
    static final String COLUMN_STAT = "status";

    public ToDoDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        try {
            String CREATE_BOOKS_TABLE = "CREATE TABLE "
                    + DATABASE_TABLE + "(" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_TASK + " TEXT," +
                    COLUMN_STAT + " INTEGER DEFAULT 0" + ")";
            database.execSQL(CREATE_BOOKS_TABLE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.w("SQLiteDatabase", "Upgrading database from version " + oldVersion +
                "to" + newVersion + ", which will destroy all old data.");
        database.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
        onCreate(database);
    }

    ArrayList<ToDoModel> listOfTasks() {
        String sqlSelect = "SELECT * FROM " + DATABASE_TABLE;
        SQLiteDatabase database = this.getReadableDatabase();
        ArrayList<ToDoModel> storeBooks = new ArrayList<>();
        Cursor cursor = database.rawQuery(sqlSelect, null);
        if (cursor.moveToFirst()) {
            do {
                int id = (cursor.getInt(0));
                String task = cursor.getString(1);
                int status = cursor.getInt(2);
                storeBooks.add(new ToDoModel(id, task, status));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return storeBooks;
    }

    void addTask(ToDoModel toDoModel) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_TASK, toDoModel.getTask());
        SQLiteDatabase database = this.getWritableDatabase();
        database.insert(DATABASE_TABLE, null, values);
    }

    void updateTask(ToDoModel toDoModel) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, toDoModel.getId());
        values.put(COLUMN_TASK, toDoModel.getTask());
        values.put(COLUMN_STAT, toDoModel.getStatus());
        SQLiteDatabase database = this.getWritableDatabase();
        database.update(DATABASE_TABLE, values, COLUMN_ID + " = ?", new String[]{String.valueOf(toDoModel.getId())});
    }

    void deleteBook(int ID) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(DATABASE_TABLE, COLUMN_ID + " = ?", new String[]{String.valueOf(ID)});
    }

    void deleteAll() {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(DATABASE_TABLE, null, null);
        database.execSQL("VACUUM");
    }
}
