package com.asterisk.mytodo;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import java.util.Objects;

public class ToDoProvider extends ContentProvider {
    static final String PROVIDER_NAME = "com.asterisk.myToDo"; //Provider name
    static final String URL = "content://" + PROVIDER_NAME + "/todo"; //Provider URL
    public static final Uri CONTENT_URI = Uri.parse(URL); //Content URL in URI format

    //Database column ID
    public final String ID = ToDoDbHelper.COLUMN_ID;

    //Uri code
    static final int TODOS = 1;
    static final int TODO_ID = 2;
    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "todo", TODOS);
        uriMatcher.addURI(PROVIDER_NAME, "todo/#", TODO_ID);
    }

    //Database specific constant declarations
    SQLiteDatabase toDoDB;
    static final String TABLE = ToDoDbHelper.DATABASE_TABLE;

    @Override
    public boolean onCreate() {
        Context context = getContext();
        ToDoDbHelper dbHelper = new ToDoDbHelper(context);

        /* Create a writable database which will trigger its creation if it doesn't exists */
        toDoDB = dbHelper.getWritableDatabase();
        return toDoDB != null;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(TABLE);

        if (uriMatcher.match(uri) == TODO_ID) {
            queryBuilder.appendWhere(ID + " = " + uri.getPathSegments().get(1));
        }
        if (sortOrder == null || Objects.equals(sortOrder, "")) {
            sortOrder = ID;
        }
        Cursor cursor = queryBuilder.query(toDoDB, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case TODOS:
                return "vnd.android.cursor.dir/vnd.asterisk.myToDo.todo";
            case TODO_ID:
                return "vnd.android.cursor.item/vnd.asterisk.myToDo.todo";
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        /* Add a new task */
        long rowID = toDoDB.insert(TABLE, "", values);

        /* If successful */
        if (rowID > 0) {
            Uri uri1 = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(uri1, null);
            return uri1;
        }
        throw new SQLException("Failed to add new record into " + uri);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count;
        switch (uriMatcher.match(uri)) {
            case TODOS:
                count = toDoDB.delete(TABLE, selection, selectionArgs);
                break;
            case TODO_ID:
                String id = uri.getPathSegments().get(1);
                count = toDoDB.delete(TABLE, ID + " = " + id + (!TextUtils.isEmpty(selection) ? "AND (" + selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count;
        switch (uriMatcher.match(uri)) {
            case TODOS:
                count = toDoDB.update(TABLE, values, selection, selectionArgs);
                break;
            case TODO_ID:
                String id = uri.getPathSegments().get(1);
                count = toDoDB.update(TABLE, values, ID + " = " + id + (!TextUtils.isEmpty(selection) ? "AND (" + selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}
