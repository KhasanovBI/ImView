package jat.imview.processor;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import java.lang.ref.WeakReference;
import java.util.List;

import jat.imview.contentProvider.db.table.AbyssTable;
import jat.imview.contentProvider.db.table.FeaturedTable;
import jat.imview.contentProvider.db.table.base.ImageListTable;
import jat.imview.model.Image;
import jat.imview.rest.http.HTTPMethod;
import jat.imview.rest.resource.ImageListResource;
import jat.imview.contentProvider.db.table.ImageTable;
import jat.imview.rest.restMethod.base.RestMethod;
import jat.imview.rest.restMethod.base.RestMethodResult;
import jat.imview.rest.restMethod.ImageListRestMethod;

/**
 * Created by bulat on 23.12.15.
 */
public class ImageListProcessor {
    private WeakReference<Context> weakContext;
    private boolean isFeatured;

    public ImageListProcessor(Context context, boolean isFeatured) {
        weakContext = new WeakReference<>(context);
        this.isFeatured = isFeatured;
    }

    public void getImageList(ProcessorCallback processorCallback) {
        RestMethod<ImageListResource> imageListMethod = new ImageListRestMethod(HTTPMethod.GET, isFeatured);
        RestMethodResult<ImageListResource> restMethodResult = imageListMethod.execute();

        if (restMethodResult.getStatusCode() == 200) {
            updateContentProvider(restMethodResult);
        }
        processorCallback.send(restMethodResult.getStatusCode());
    }

    private void updateContentProvider(RestMethodResult<ImageListResource> restMethodResult) {
        ImageListResource imageListResource = restMethodResult.getResource();
        List<Image> imageList = imageListResource.getImageList();
        ContentResolver contentResolver = weakContext.get().getContentResolver();
        ContentValues[] ImageListValuesArray = new ContentValues[imageList.size()];
        for (int i = 0; i < imageList.size(); ++i) {
            Image image = imageList.get(i);
            ContentValues values = new ContentValues();
            values.put(ImageTable.ID, image.getId());
            values.put(ImageTable.NETPATH, image.getNetpath());
            values.put(ImageTable.RATING, image.getRating());
            values.put(ImageTable.PUBLISH_DATE, image.getPublishDate().getTime() / 1000);
            values.put(ImageTable.COMMENTS_COUNT, String.valueOf(image.getCommentsCount()));
            contentResolver.insert(ImageTable.CONTENT_URI, values);

            ContentValues imageListValues = new ContentValues();
            imageListValues.put(ImageListTable.IMAGE_ID, image.getId());
            ImageListValuesArray[i] = imageListValues;
        }
        Uri imageTableConcreteUri;
        if (isFeatured) {
            imageTableConcreteUri = FeaturedTable.CONTENT_URI;
        } else {
            imageTableConcreteUri = AbyssTable.CONTENT_URI;
        }
        contentResolver.delete(imageTableConcreteUri, null, null);
        contentResolver.bulkInsert(imageTableConcreteUri, ImageListValuesArray);
    }
}