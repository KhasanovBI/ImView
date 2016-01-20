package jat.imview.rest.restMethod;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import jat.imview.rest.HTTPUtil;
import jat.imview.rest.http.HTTPMethod;
import jat.imview.rest.http.Request;
import jat.imview.rest.resource.LoginResource;
import jat.imview.rest.restMethod.base.AbstractRestMethod;

import static jat.imview.rest.http.ConnectionParams.HOST;
import static jat.imview.rest.http.ConnectionParams.SCHEME;

public class LoginRestMethod extends AbstractRestMethod<LoginResource> {
    private HTTPMethod httpMethod;
    private String username;
    private String password;
    private static final String PATH = "/login";
    private static final URI LOGIN_URL = URI.create(SCHEME + HOST + PATH);

    public LoginRestMethod(HTTPMethod httpMethod, String username, String password) {
        this.httpMethod = httpMethod;
        this.username = username;
        this.password = password;
    }

    @Override
    protected Request buildRequest() {
        Map<String, String> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
        return new Request(httpMethod, LOGIN_URL, HTTPUtil.getPostDataString(params));
    }

    @Override
    protected LoginResource parseResponseBody(byte[] responseBody) throws Exception {
        return new LoginResource(responseBody);
    }
}
