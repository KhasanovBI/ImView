package jat.imview.contentProvider.db.table;

import android.net.Uri;

import jat.imview.contentProvider.db.table.base.BaseParams;
import jat.imview.contentProvider.db.table.base.ImageListTable;

/**
 * Created by bulat on 07.01.16.
 */
public interface FeaturedTable extends BaseParams, ImageListTable {
    String TABLE_NAME = "featured";

    String URI_PATH = "featured";
    Uri CONTENT_URI = Uri.parse(SCHEME + ImageTable.AUTHORITY + "/" + ImageTable.URI_PATH + "/" + URI_PATH);
}
