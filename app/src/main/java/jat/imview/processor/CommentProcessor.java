package jat.imview.processor;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.List;

import jat.imview.contentProvider.db.table.CommentTable;
import jat.imview.contentProvider.db.table.ImageTable;
import jat.imview.contentProvider.db.table.UserProfileTable;
import jat.imview.model.Comment;
import jat.imview.model.UserProfile;
import jat.imview.rest.http.HTTPMethod;
import jat.imview.rest.resource.CommentListResource;
import jat.imview.rest.resource.CommentResource;
import jat.imview.rest.restMethod.CommentListRestMethod;
import jat.imview.rest.restMethod.CommentRestMethod;
import jat.imview.rest.restMethod.base.RestMethod;
import jat.imview.rest.restMethod.base.RestMethodResult;

/**
 * Created by bulat on 23.12.15.
 */
public class CommentProcessor {
    private WeakReference<Context> weakContext;
    private int imageId;
    private final String text;

    public CommentProcessor(Context context, int imageId, String text) {
        weakContext = new WeakReference<>(context);
        this.imageId = imageId;
        this.text = text;
    }

    public void getComment(ProcessorCallback processorCallback) {
        RestMethod<CommentResource> commentMethod = new CommentRestMethod(HTTPMethod.POST, imageId, text);
        RestMethodResult<CommentResource> restMethodResult = commentMethod.execute();

        if (restMethodResult.getStatusCode() == 200) {
            updateContentProvider(restMethodResult);
        }
        processorCallback.send(restMethodResult.getStatusCode());
    }

    private void updateContentProvider(RestMethodResult<CommentResource> restMethodResult) {
        CommentResource commentResource = restMethodResult.getResource();
        ContentResolver contentResolver = weakContext.get().getContentResolver();

        UserProfile userProfile = commentResource.getUserProfile();
        ContentValues userProfileValues = new ContentValues();
        userProfileValues.put(UserProfileTable.ID, userProfile.getId());
        userProfileValues.put(UserProfileTable.NAME, userProfile.getName());
        contentResolver.insert(UserProfileTable.CONTENT_URI, userProfileValues);

        Comment comment = commentResource.getComment();
        ContentValues commentValues = new ContentValues();
        commentValues.put(CommentTable.ID, comment.getId());
        commentValues.put(CommentTable.IMAGE_ID, comment.getImageId());
        commentValues.put(CommentTable.USER_ID, comment.getUserId());
        commentValues.put(CommentTable.PUBLISH_DATE, comment.getPublishDate().getTime() / 1000);
        commentValues.put(CommentTable.MESSAGE, comment.getMessage());
        commentValues.put(CommentTable.RATING, comment.getRating());
        contentResolver.insert(CommentTable.CONTENT_URI, commentValues);
    }
}