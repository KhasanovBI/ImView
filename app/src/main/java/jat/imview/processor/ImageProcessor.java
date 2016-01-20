package jat.imview.processor;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.lang.ref.WeakReference;

import jat.imview.contentProvider.db.table.CommentTable;
import jat.imview.contentProvider.db.table.UserProfileTable;
import jat.imview.model.Comment;
import jat.imview.model.UserProfile;
import jat.imview.rest.http.HTTPMethod;
import jat.imview.rest.resource.CommentResource;
import jat.imview.rest.resource.ImageResource;
import jat.imview.rest.restMethod.CommentRestMethod;
import jat.imview.rest.restMethod.ImageRestMethod;
import jat.imview.rest.restMethod.base.RestMethod;
import jat.imview.rest.restMethod.base.RestMethodResult;
import jat.imview.util.BitmapSender;

/**
 * Created by bulat on 23.12.15.
 */
public class ImageProcessor {
    private WeakReference<Context> weakContext;
    private byte[] byteArray;

    public ImageProcessor(Context context, byte[] byteArray) {
        weakContext = new WeakReference<>(context);
        this.byteArray = byteArray;
    }

    public void postImageNew(ProcessorCallback processorCallback) {
        RestMethod<ImageResource> imageMethod = new ImageRestMethod(HTTPMethod.POST, byteArray);
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        BitmapSender.send(bitmap);
        /*RestMethodResult<ImageResource> restMethodResult = imageMethod.execute();
        if (restMethodResult.getStatusCode() == 200) {
            updateContentProvider(restMethodResult);
        }
        processorCallback.send(restMethodResult.getStatusCode());*/
    }

    private void updateContentProvider(RestMethodResult<ImageResource> restMethodResult) {
        ImageResource imageResource = restMethodResult.getResource();
        ContentResolver contentResolver = weakContext.get().getContentResolver();
/*
        UserProfile userProfile = commentResource.getUserProfile();
        ContentValues userProfileValues = new ContentValues();
        userProfileValues.put(UserProfileTable.ID, userProfile.getId());
        userProfileValues.put(UserProfileTable.NAME, userProfile.getName());
        contentResolver.insert(UserProfileTable.CONTENT_URI, userProfileValues);
        */
    }
}