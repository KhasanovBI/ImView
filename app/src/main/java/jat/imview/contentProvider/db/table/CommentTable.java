package jat.imview.contentProvider.db.table;

import android.net.Uri;

import jat.imview.contentProvider.db.table.base.BaseParams;

/**
 * Created by bulat on 10.01.16.
 */
public interface CommentTable extends BaseParams, ResourceTable {
    String TABLE_NAME = "comments";
    /* Columns */
    String IMAGE_ID = "image_id";
    String USER_ID = "user_id";
    String PUBLISH_DATE = "publish_date";
    String MESSAGE = "message";
    String RATING = "rating";

    String AUTHORITY = "jat.imview.commentProvider";
    String URI_PATH = "comments";
    String CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + AUTHORITY + "." + URI_PATH;
    String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + AUTHORITY + "." + URI_PATH;
    Uri CONTENT_URI = Uri.parse(SCHEME + AUTHORITY + "/" + URI_PATH);
}
