package jat.imview.contentProvider.db.table;

import android.net.Uri;

import jat.imview.contentProvider.db.table.base.BaseParams;
import jat.imview.contentProvider.db.table.base.ImageListTable;

/**
 * Created by bulat on 07.01.16.
 */
public interface AbyssTable extends BaseParams, ImageListTable {
    String TABLE_NAME = "abyss";

    String URI_PATH = "abyss";
    Uri CONTENT_URI = Uri.parse(SCHEME + ImageTable.AUTHORITY + "/" + ImageTable.URI_PATH + "/" + URI_PATH);
}
