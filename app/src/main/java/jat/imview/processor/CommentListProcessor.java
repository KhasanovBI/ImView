package jat.imview.processor;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.List;

import jat.imview.contentProvider.db.table.CommentTable;
import jat.imview.contentProvider.db.table.UserProfileTable;
import jat.imview.model.Comment;
import jat.imview.model.UserProfile;
import jat.imview.rest.http.HTTPMethod;
import jat.imview.rest.resource.CommentListResource;
import jat.imview.rest.http.Response;
import jat.imview.rest.resource.ImageListResource;
import jat.imview.rest.restMethod.CommentListRestMethod;
import jat.imview.rest.restMethod.ImageListRestMethod;
import jat.imview.rest.restMethod.base.RestMethod;
import jat.imview.rest.restMethod.base.RestMethodResult;

/**
 * Created by bulat on 23.12.15.
 */
public class CommentListProcessor {
    private WeakReference<Context> weakContext;
    private int imageId;

    public CommentListProcessor(Context context, int imageId) {
        weakContext = new WeakReference<>(context);
        this.imageId = imageId;
    }

    public void getCommentList(ProcessorCallback processorCallback) {
        RestMethod<CommentListResource> commentListMethod = new CommentListRestMethod(HTTPMethod.GET, imageId);
        RestMethodResult<CommentListResource> restMethodResult = commentListMethod.execute();

        if (restMethodResult.getStatusCode() == 200) {
            updateContentProvider(restMethodResult);
        }
        processorCallback.send(restMethodResult.getStatusCode());
    }

    private void updateContentProvider(RestMethodResult<CommentListResource> restMethodResult) {
        CommentListResource commentListResource = restMethodResult.getResource();
        ContentResolver contentResolver = weakContext.get().getContentResolver();

        List<UserProfile> userProfileList = commentListResource.getUserProfileList();
        ContentValues[] userProfileValuesArray = new ContentValues[userProfileList.size()];
        for (int i = 0; i < userProfileList.size(); ++i) {
            UserProfile userProfile = userProfileList.get(i);
            ContentValues values = new ContentValues();
            values.put(UserProfileTable.ID, userProfile.getId());
            values.put(UserProfileTable.NAME, userProfile.getName());
            userProfileValuesArray[i] = values;
        }
        contentResolver.bulkInsert(UserProfileTable.CONTENT_URI, userProfileValuesArray);

        List<Comment> commentList = commentListResource.getCommentList();
        ContentValues[] commentValuesArray = new ContentValues[commentList.size()];
        for (int i = 0; i < commentList.size(); ++i) {
            Comment comment = commentList.get(i);
            ContentValues values = new ContentValues();
            values.put(CommentTable.ID, comment.getId());
            values.put(CommentTable.IMAGE_ID, comment.getImageId());
            values.put(CommentTable.USER_ID, comment.getUserId());
            values.put(CommentTable.PUBLISH_DATE, comment.getPublishDate().getTime() / 1000);
            values.put(CommentTable.MESSAGE, comment.getMessage());
            values.put(CommentTable.RATING, comment.getRating());
            commentValuesArray[i] = values;
        }
        contentResolver.bulkInsert(CommentTable.CONTENT_URI, commentValuesArray);
    }
}