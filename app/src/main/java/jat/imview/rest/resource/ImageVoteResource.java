package jat.imview.rest.resource;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import jat.imview.model.Image;
import jat.imview.rest.resource.base.Resource;
import jat.imview.util.DateUtil;

/**
 * Created by bulat on 23.12.15.
 */
public class ImageVoteResource implements Resource {
    private static final String LOG_TAG = "MyResponse";
    private Image image;

    public ImageVoteResource(byte[] responseBody) {
        try {
            String responseString = new String(responseBody);
            Log.d(LOG_TAG, responseString);
            JSONObject jsonImage = new JSONObject(responseString);
            image = new Image(
                    jsonImage.getInt("id"),
                    jsonImage.getString("path"),
                    DateUtil.parseServerString(jsonImage.getString("publish_date")),
                    jsonImage.getInt("rating"),
                    jsonImage.getInt("comments_count")
            );
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Image getImage() {
        return image;
    }
}
