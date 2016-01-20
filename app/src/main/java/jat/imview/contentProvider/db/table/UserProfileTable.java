package jat.imview.contentProvider.db.table;

import android.net.Uri;

import jat.imview.contentProvider.db.table.base.BaseParams;

/**
 * Created by bulat on 10.01.16.
 */
public interface UserProfileTable extends BaseParams, ResourceTable {
    String TABLE_NAME = "user_profiles";
    /* Columns */
    String NAME = "name";

    String AUTHORITY = "jat.imview.userProfileProvider";
    String URI_PATH = "user_profiles";
    String CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + AUTHORITY + "." + URI_PATH;
    String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + AUTHORITY + "." + URI_PATH;
    Uri CONTENT_URI = Uri.parse(SCHEME + AUTHORITY + "/" + URI_PATH);
}
