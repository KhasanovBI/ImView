package jat.imview.rest.restMethod;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import jat.imview.rest.HTTPUtil;
import jat.imview.rest.http.HTTPMethod;
import jat.imview.rest.http.Request;
import jat.imview.rest.resource.LoginResource;
import jat.imview.rest.resource.SignupResource;
import jat.imview.rest.restMethod.base.AbstractRestMethod;

import static jat.imview.rest.http.ConnectionParams.HOST;
import static jat.imview.rest.http.ConnectionParams.SCHEME;

public class SignupRestMethod extends AbstractRestMethod<SignupResource> {
    private HTTPMethod httpMethod;
    private String username;
    private String password;
    private static final String PATH = "/signup";
    private static final URI SIGNUP_URL = URI.create(SCHEME + HOST + PATH);

    public SignupRestMethod(HTTPMethod httpMethod, String username, String password) {
        this.httpMethod = httpMethod;
        this.username = username;
        this.password = password;
    }

    @Override
    protected Request buildRequest() {
        Map<String, String> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
        return new Request(httpMethod, SIGNUP_URL, HTTPUtil.getPostDataString(params));
    }

    @Override
    protected SignupResource parseResponseBody(byte[] responseBody) throws Exception {
        return new SignupResource(responseBody);
    }
}
