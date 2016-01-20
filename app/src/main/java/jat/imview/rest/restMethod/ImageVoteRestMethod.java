package jat.imview.rest.restMethod;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import jat.imview.rest.HTTPUtil;
import jat.imview.rest.http.HTTPMethod;
import jat.imview.rest.http.Request;
import jat.imview.rest.resource.ImageVoteResource;
import jat.imview.rest.restMethod.base.AbstractRestMethod;

import static jat.imview.rest.http.ConnectionParams.HOST;
import static jat.imview.rest.http.ConnectionParams.SCHEME;

public class ImageVoteRestMethod extends AbstractRestMethod<ImageVoteResource> {
    private HTTPMethod httpMethod;
    private int imageId;
    private boolean isUpVote;
    private static final String PATH = "/image/vote";
    private static final URI IMAGE_VOTE_URL = URI.create(SCHEME + HOST + PATH);

    public ImageVoteRestMethod(HTTPMethod httpMethod, int imageId, boolean isUpVote) {
        this.httpMethod = httpMethod;
        this.imageId = imageId;
        this.isUpVote = isUpVote;
    }

    @Override
    protected Request buildRequest() {
        Map<String, String> params = new HashMap<>();
        params.put("image_id", String.valueOf(imageId));
        params.put("is_upvote", String.valueOf(isUpVote ? 1 : -1));
        return new Request(httpMethod, IMAGE_VOTE_URL, HTTPUtil.getPostDataString(params));
    }

    @Override
    protected ImageVoteResource parseResponseBody(byte[] responseBody) throws Exception {
        return new ImageVoteResource(responseBody);
    }
}
