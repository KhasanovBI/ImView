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
import jat.imview.contentProvider.db.table.CommentTable;
import jat.imview.contentProvider.db.table.UserProfileTable;
import jat.imview.model.Comment;

/**
 * Created by bulat on 10.01.16.
 */
public class CommentProvider extends ContentProvider {
    public static final UriMatcher uriMatcher;
    private static final String LOG_TAG = "MyCommentProvider";
    private DBHelper dbHelper;
    private SQLiteDatabase db;

    private static final int URI_COMMENTS = 1;
    private static final int URI_COMMENT_ID = 2;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(CommentTable.AUTHORITY, CommentTable.URI_PATH, URI_COMMENTS);
        uriMatcher.addURI(CommentTable.AUTHORITY, CommentTable.URI_PATH + "/#", URI_COMMENT_ID);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new DBHelper(this.getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor;
        db = dbHelper.getReadableDatabase();
        if (sortOrder == null) {
            sortOrder = CommentTable.PUBLISH_DATE + " DESC";
        }
        if (projection == null) {
            projection = new String[] {
                    CommentTable.TABLE_NAME + "." + CommentTable.ID + " AS " + CommentTable.ID,
                    CommentTable.IMAGE_ID,
                    CommentTable.USER_ID,
                    CommentTable.PUBLISH_DATE,
                    CommentTable.MESSAGE,
                    CommentTable.RATING,
                    UserProfileTable.NAME
            };
        }
        switch (uriMatcher.match(uri)) {
            case URI_COMMENTS:
                cursor = db.query(DBHelper.getCommentJoinUserSqlQuery(), projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case URI_COMMENT_ID:
                int id = Integer.parseInt(uri.getLastPathSegment());
                selection = DBHelper.appendRowId(selection, id);
                cursor = db.query(CommentTable.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (uriMatcher.match(uri)) {
            case URI_COMMENTS:
                return CommentTable.CONTENT_TYPE;
            case URI_COMMENT_ID:
                return CommentTable.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        if (uriMatcher.match(uri) != URI_COMMENTS) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        db = dbHelper.getWritableDatabase();
        long id = db.insertWithOnConflict(CommentTable.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        Uri newUri = ContentUris.withAppendedId(CommentTable.CONTENT_URI, id);
        getContext().getContentResolver().notifyChange(newUri, null);
        return newUri;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, ContentValues[] values) {
        int insertCount = 0;
        switch (uriMatcher.match(uri)) {
            case URI_COMMENTS:
                db = dbHelper.getWritableDatabase();
                db.beginTransaction();
                try {
                    for (ContentValues valuesItem : values) {
                        long newId = db.insertWithOnConflict(CommentTable.TABLE_NAME, null, valuesItem, SQLiteDatabase.CONFLICT_REPLACE);
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


    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
