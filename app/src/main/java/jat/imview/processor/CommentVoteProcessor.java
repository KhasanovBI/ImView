package jat.imview.processor;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;

import java.lang.ref.WeakReference;

import jat.imview.contentProvider.db.table.CommentTable;
import jat.imview.model.Comment;
import jat.imview.rest.http.HTTPMethod;
import jat.imview.rest.resource.CommentVoteResource;
import jat.imview.rest.restMethod.CommentVoteRestMethod;
import jat.imview.rest.restMethod.base.RestMethod;
import jat.imview.rest.restMethod.base.RestMethodResult;

/**
 * Created by bulat on 23.12.15.
 */
public class CommentVoteProcessor {
    private WeakReference<Context> weakContext;
    private int commentId;
    private boolean isUpVote;

    public CommentVoteProcessor(Context context, int commentId, boolean isUpVote) {
        weakContext = new WeakReference<>(context);
        this.commentId = commentId;
        this.isUpVote = isUpVote;
    }

    public void getImage(ProcessorCallback processorCallback) {
        RestMethod<CommentVoteResource> commentVoteRestMethod = new CommentVoteRestMethod(HTTPMethod.POST, commentId, isUpVote);
        RestMethodResult<CommentVoteResource> restMethodResult = commentVoteRestMethod.execute();
        if (restMethodResult.getStatusCode() == 200) {
            updateContentProvider(restMethodResult);
        }
        processorCallback.send(restMethodResult.getStatusCode());
    }

    private void updateContentProvider(RestMethodResult<CommentVoteResource> restMethodResult) {
        CommentVoteResource commentVoteResource = restMethodResult.getResource();
        Comment comment = commentVoteResource.getComment();
        ContentResolver contentResolver = weakContext.get().getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CommentTable.ID, comment.getId());
        values.put(CommentTable.IMAGE_ID, comment.getImageId());
        values.put(CommentTable.USER_ID, comment.getUserId());
        values.put(CommentTable.PUBLISH_DATE, comment.getPublishDate().getTime() / 1000);
        values.put(CommentTable.MESSAGE, comment.getMessage());
        values.put(CommentTable.RATING, comment.getRating());
        contentResolver.insert(CommentTable.CONTENT_URI, values);
    }
}