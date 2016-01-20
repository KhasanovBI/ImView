package jat.imview.contentProvider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.sql.SQLException;

import jat.imview.contentProvider.db.DBHelper;
import jat.imview.contentProvider.db.table.UserProfileTable;

/**
 * Created by bulat on 07.12.15.
 */
public class UserProvider extends ContentProvider {
    public static final UriMatcher uriMatcher;
    private DBHelper dbHelper;
    private SQLiteDatabase db;

    public static final int URI_USERS = 1;
    public static final int URI_USER_ID = 2;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(UserProfileTable.AUTHORITY, UserProfileTable.URI_PATH, URI_USERS);
        uriMatcher.addURI(UserProfileTable.AUTHORITY, UserProfileTable.URI_PATH + "/#", URI_USER_ID);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new DBHelper(this.getContext());
        return true;
    }

    @Nullable
    @Override
    public String[] getStreamTypes(Uri uri, String mimeTypeFilter) {
        return super.getStreamTypes(uri, mimeTypeFilter);
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case URI_USERS:
                return UserProfileTable.CONTENT_TYPE;
            case URI_USER_ID:
                return UserProfileTable.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        if (uriMatcher.match(uri) != URI_USERS) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        db = dbHelper.getWritableDatabase();
        long id = db.insertWithOnConflict(UserProfileTable.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        Uri newUri = ContentUris.withAppendedId(UserProfileTable.CONTENT_URI, id);
        getContext().getContentResolver().notifyChange(newUri, null);
        return newUri;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, ContentValues[] values) {
        int insertCount = 0;
        switch (uriMatcher.match(uri)) {
            case URI_USERS:
                db = dbHelper.getWritableDatabase();
                db.beginTransaction();
                try {
                    for (ContentValues valuesItem : values) {
                        long newId = db.insertWithOnConflict(UserProfileTable.TABLE_NAME, null, valuesItem, SQLiteDatabase.CONFLICT_REPLACE);
                        if (newId <= 0) {
                            throw new SQLException("Failed to insert row into " + uri);
                        }
                    }
                    db.setTransactionSuccessful();
                    insertCount = values.length;
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
        }
        return insertCount;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        switch (uriMatcher.match(uri)) {
            case URI_USERS:
                break;
            case URI_USER_ID:
                int id = Integer.parseInt(uri.getLastPathSegment());
                selection = DBHelper.appendRowId(selection, id);
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(UserProfileTable.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        switch (uriMatcher.match(uri)) {
            case URI_USERS:
                throw new UnsupportedOperationException("Not implemented");
            case URI_USER_ID:
                int id = Integer.parseInt(uri.getLastPathSegment());
                selection = DBHelper.appendRowId(selection, id);
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        db = dbHelper.getWritableDatabase();
        int count = db.update(UserProfileTable.TABLE_NAME, values, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
