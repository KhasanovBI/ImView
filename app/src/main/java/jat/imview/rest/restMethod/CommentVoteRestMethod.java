package jat.imview.rest.restMethod;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import jat.imview.rest.HTTPUtil;
import jat.imview.rest.http.HTTPMethod;
import jat.imview.rest.http.Request;
import jat.imview.rest.resource.CommentVoteResource;
import jat.imview.rest.restMethod.base.AbstractRestMethod;

import static jat.imview.rest.http.ConnectionParams.HOST;
import static jat.imview.rest.http.ConnectionParams.SCHEME;

public class CommentVoteRestMethod extends AbstractRestMethod<CommentVoteResource> {
    private HTTPMethod httpMethod;
    private int commentId;
    private boolean isUpVote;
    private static final String PATH = "/comment/vote";
    private static final URI COMMENT_VOTE_URL = URI.create(SCHEME + HOST + PATH);

    public CommentVoteRestMethod(HTTPMethod httpMethod, int commentId, boolean isUpVote) {
        this.httpMethod = httpMethod;
        this.commentId = commentId;
        this.isUpVote = isUpVote;
    }

    @Override
    protected Request buildRequest() {
        Map<String, String> params = new HashMap<>();
        params.put("comment_id", String.valueOf(commentId));
        params.put("is_upvote", String.valueOf(isUpVote ? 1 : -1));
        return new Request(httpMethod, COMMENT_VOTE_URL, HTTPUtil.getPostDataString(params));
    }

    @Override
    protected CommentVoteResource parseResponseBody(byte[] responseBody) throws Exception {
        return new CommentVoteResource(responseBody);
    }
}
