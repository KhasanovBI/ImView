package jat.imview.contentProvider.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import jat.imview.contentProvider.db.table.AbyssTable;
import jat.imview.contentProvider.db.table.base.BaseParams;
import jat.imview.contentProvider.db.table.CommentTable;
import jat.imview.contentProvider.db.table.FeaturedTable;
import jat.imview.contentProvider.db.table.base.ImageListTable;
import jat.imview.contentProvider.db.table.ImageTable;
import jat.imview.contentProvider.db.table.UserProfileTable;

/**
 * Created by bulat on 23.12.15.
 */
public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "imview.db";
    private static final int DATABASE_VERSION = 1;
    private static final String IMAGE_LIST_GET_SQL_QUERY;
    private static final String COMMENT_JOIN_USER_SQL_QUERY;
    private static final String TRUNCATE_SQL_QUERY;

    static {
        StringBuilder sqlBuilder = new StringBuilder();

        sqlBuilder.append("SELECT * FROM %s");
        sqlBuilder.append(" INNER JOIN ").append(ImageTable.TABLE_NAME);
        sqlBuilder.append(" ON %s").append(".").append(ImageListTable.IMAGE_ID)
                .append(" = ").append(ImageTable.TABLE_NAME).append(".").append(ImageTable.ID);
        sqlBuilder.append(" ORDER BY ").append(ImageTable.PUBLISH_DATE).append(" DESC").append(";");
        IMAGE_LIST_GET_SQL_QUERY = sqlBuilder.toString();

        sqlBuilder.setLength(0);
        sqlBuilder.append(CommentTable.TABLE_NAME);
        sqlBuilder.append(" INNER JOIN ").append(UserProfileTable.TABLE_NAME);
        sqlBuilder.append(" ON ").append(CommentTable.USER_ID).append(" = ")
                .append(UserProfileTable.TABLE_NAME).append(".").append(UserProfileTable.ID);
        COMMENT_JOIN_USER_SQL_QUERY = sqlBuilder.toString();

        sqlBuilder.setLength(0);
        sqlBuilder.append("DELETE FROM %s;");
        sqlBuilder.append("VACUUM;");
        TRUNCATE_SQL_QUERY = sqlBuilder.toString();
    }

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        StringBuilder sqlBuilder = new StringBuilder();
        String sqlQuery;

        // USER TABLE
        sqlBuilder.append("CREATE TABLE ").append(UserProfileTable.TABLE_NAME).append(" (");
        sqlBuilder.append(UserProfileTable.ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sqlBuilder.append(UserProfileTable.NAME).append(" TEXT NOT NULL");
        sqlBuilder.append(");");
        sqlQuery = sqlBuilder.toString();
        db.execSQL(sqlQuery);

        // IMAGE TABLE
        sqlBuilder.setLength(0);
        sqlBuilder.append("CREATE TABLE ").append(ImageTable.TABLE_NAME).append(" (");
        sqlBuilder.append(ImageTable.ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sqlBuilder.append(ImageTable.NETPATH).append(" TEXT NOT NULL, ");
        sqlBuilder.append(ImageTable.RATING).append(" INTEGER NOT NULL DEFAULT 0, ");
        sqlBuilder.append(ImageTable.COMMENTS_COUNT).append(" INTEGER NOT NULL DEFAULT 0, ");
        sqlBuilder.append(ImageTable.PUBLISH_DATE).append(" INTEGER NOT NULL, ");
        sqlBuilder.append(ImageTable.STATE).append(" INTEGER NOT NULL DEFAULT ")
                .append(RequestState.WAITING.ordinal());
        sqlBuilder.append(");");
        sqlQuery = sqlBuilder.toString();
        db.execSQL(sqlQuery);

        // IMAGE LIST QUERIES
        sqlBuilder.setLength(0);
        sqlBuilder.append("CREATE TABLE %s (");
        sqlBuilder.append(BaseParams.ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sqlBuilder.append(ImageListTable.IMAGE_ID).append(" INTEGER NOT NULL, ");
        sqlBuilder.append("FOREIGN KEY(").append(ImageListTable.IMAGE_ID).append(") REFERENCES ")
                .append(ImageTable.TABLE_NAME).append("(").append(ImageTable.ID).append(")");
        sqlBuilder.append(");");
        String imageListCreateSQLQuery = sqlBuilder.toString();

        // FEATURED TABLE
        db.execSQL(String.format(imageListCreateSQLQuery, FeaturedTable.TABLE_NAME));

        // ABYSS TABLE
        db.execSQL(String.format(imageListCreateSQLQuery, AbyssTable.TABLE_NAME));

        // COMMENT TABLE
        sqlBuilder.setLength(0);
        sqlBuilder.append("CREATE TABLE ").append(CommentTable.TABLE_NAME).append(" (");
        sqlBuilder.append(CommentTable.ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sqlBuilder.append(CommentTable.IMAGE_ID).append(" INTEGER NOT NULL, ");
        sqlBuilder.append(CommentTable.USER_ID).append(" INTEGER NOT NULL, ");
        sqlBuilder.append(CommentTable.PUBLISH_DATE).append(" INTEGER NOT NULL, ");
        sqlBuilder.append(CommentTable.MESSAGE).append(" TEXT NOT NULL, ");
        sqlBuilder.append(CommentTable.RATING).append(" INTEGER NOT NULL DEFAULT 0, ");
        sqlBuilder.append(ImageTable.STATE).append(" INTEGER NOT NULL DEFAULT ")
                .append(RequestState.WAITING.ordinal()).append(", ");
        sqlBuilder.append("FOREIGN KEY(").append(CommentTable.IMAGE_ID).append(") REFERENCES ")
                .append(ImageTable.TABLE_NAME).append("(").append(ImageTable.ID).append("), ");
        sqlBuilder.append("FOREIGN KEY(").append(CommentTable.USER_ID).append(") REFERENCES ")
                .append(UserProfileTable.TABLE_NAME).append("(").append(UserProfileTable.ID)
                .append(")");
        sqlBuilder.append(");");
        sqlQuery = sqlBuilder.toString();
        db.execSQL(sqlQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public static String getTruncateSqlQuery(String tableName) {
        return String.format(TRUNCATE_SQL_QUERY, tableName);
    }

    public static String getImageListSqlQuery(String tableName) {
        return String.format(IMAGE_LIST_GET_SQL_QUERY, tableName, tableName);
    }

    public static String getCommentJoinUserSqlQuery() {
        return COMMENT_JOIN_USER_SQL_QUERY;
    }

    // Чтобы проверяло и по id
    public static String appendRowId(String selection, long id) {
        return ImageTable.ID + " = " + id + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : "");
    }
}
