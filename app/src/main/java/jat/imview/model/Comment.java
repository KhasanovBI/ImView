package jat.imview.model;

import android.database.Cursor;

import java.util.Date;

import jat.imview.contentProvider.db.table.CommentTable;
import jat.imview.contentProvider.db.table.UserProfileTable;

/**
 * Created by bulat on 17.12.15.
 */
public class Comment {
    private int id;
    private int imageId;
    private int userId;
    private String userName;
    private Date publishDate;
    private String message;
    private int rating;

    public Comment() {
    }

    public Comment(int id, int imageId, int userId, Date publishDate, String message, int rating) {
        this.id = id;
        this.imageId = imageId;
        this.userId = userId;
        this.publishDate = publishDate;
        this.message = message;
        this.rating = rating;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public static Comment getByCursor(Cursor cursor) {
        Comment comment = new Comment();
        int idIndex = cursor.getColumnIndex(CommentTable.ID);
        int userProfileNameIndex = cursor.getColumnIndex(UserProfileTable.NAME);
        int publishDateIndex = cursor.getColumnIndex(CommentTable.PUBLISH_DATE);
        int messageIndex = cursor.getColumnIndex(CommentTable.MESSAGE);
        int ratingIndex = cursor.getColumnIndex(CommentTable.RATING);

        comment.setId(cursor.getInt(idIndex));
        comment.setUserName(cursor.getString(userProfileNameIndex));
        comment.setPublishDate(new Date(cursor.getLong(publishDateIndex) * 1000));
        comment.setMessage(cursor.getString(messageIndex));
        comment.setRating(cursor.getInt(ratingIndex));

        return comment;
    }

}
