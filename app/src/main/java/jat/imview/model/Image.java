package jat.imview.model;

import android.database.Cursor;

import java.io.Serializable;
import java.util.Date;

import jat.imview.contentProvider.db.table.ImageTable;
import jat.imview.rest.http.ConnectionParams;
import jat.imview.util.DateUtil;

/**
 * Created by bulat on 23.12.15.
 */
public class Image implements Serializable {
    private int id;
    private int rating;
    private String netpath;
    private Date publishDate;
    private int commentsCount;

    public Image() {
    }

    public Image(int id, String netpath, Date publishDate, int rating, int commentsCount) {
        this.id = id;
        this.netpath = netpath;
        this.publishDate = publishDate;
        this.rating = rating;
        this.commentsCount = commentsCount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getNetpath() {
        return netpath;
    }

    public void setNetpath(String netpath) {
        this.netpath = netpath;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    public static Image getByCursor(Cursor cursor) {
        Image image = new Image();
        int idIndex = cursor.getColumnIndex(ImageTable.ID);
        int ratingIndex = cursor.getColumnIndex(ImageTable.RATING);
        int netpathIndex = cursor.getColumnIndex(ImageTable.NETPATH);
        int publishDateIndex = cursor.getColumnIndex(ImageTable.PUBLISH_DATE);
        int commentsCountIndex = cursor.getColumnIndex(ImageTable.COMMENTS_COUNT);
        image.setId(cursor.getInt(idIndex));
        image.setRating(cursor.getInt(ratingIndex));
        image.setNetpath(cursor.getString(netpathIndex));
        image.setPublishDate(new Date(cursor.getLong(publishDateIndex) * 1000));
        image.setCommentsCount(cursor.getInt(commentsCountIndex));
        return image;
    }

    public String getFullNetpath() {
        return ConnectionParams.SCHEME + ConnectionParams.HOST + "/" + netpath;
    }
}
