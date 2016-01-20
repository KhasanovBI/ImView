package jat.imview.rest.restMethod;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import jat.imview.rest.HTTPUtil;
import jat.imview.rest.http.HTTPMethod;
import jat.imview.rest.http.Request;
import jat.imview.rest.resource.CommentResource;
import jat.imview.rest.restMethod.base.AbstractRestMethod;

import static jat.imview.rest.http.ConnectionParams.HOST;
import static jat.imview.rest.http.ConnectionParams.SCHEME;

public class CommentRestMethod extends AbstractRestMethod<CommentResource> {
    private HTTPMethod httpMethod;
    private int imageId;
    private final String text;
    private static final String PATH = "/comment/new";
    private static final URI COMMENT_NEW_URL = URI.create(SCHEME + HOST + PATH);

    public CommentRestMethod(HTTPMethod httpMethod, int imageId, String text) {
        this.httpMethod = httpMethod;
        this.imageId = imageId;
        this.text = text;
    }

    @Override
    protected Request buildRequest() {
        Map<String, String> params = new HashMap<>();
        params.put("image_id", String.valueOf(imageId));
        params.put("text", text);
        return new Request(httpMethod, COMMENT_NEW_URL, HTTPUtil.getPostDataString(params));
    }

    @Override
    protected CommentResource parseResponseBody(byte[] responseBody) throws Exception {
        return new CommentResource(responseBody);
    }
}
