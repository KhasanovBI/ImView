package jat.imview.rest.resource;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import jat.imview.model.Comment;
import jat.imview.rest.resource.base.Resource;
import jat.imview.util.DateUtil;

/**
 * Created by bulat on 23.12.15.
 */
public class CommentVoteResource implements Resource {
    private static final String LOG_TAG = "MyResponse";
    private Comment comment;

    public CommentVoteResource(byte[] responseBody) {
        try {
            String responseString = new String(responseBody);
            Log.d(LOG_TAG, responseString);
            JSONObject jsonComment = new JSONObject(responseString);
            comment = new Comment(
                    jsonComment.getInt("id"),
                    jsonComment.getInt("image_id"),
                    jsonComment.getInt("user_id"),
                    DateUtil.parseServerString(jsonComment.getString("publish_date")),
                    jsonComment.getString("text"),
                    jsonComment.getInt("rating")
            );
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Comment getComment() {
        return comment;
    }
}
