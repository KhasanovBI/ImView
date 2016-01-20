package jat.imview.rest.http;

import java.net.URI;

public class Request {
    private URI requestUri;
    private HTTPMethod method;
    private String postDataString;

    public Request(HTTPMethod method, URI requestUri, String postDataString) {
        this.method = method;
        this.requestUri = requestUri;
        this.postDataString = postDataString;
    }

    public HTTPMethod getMethod() {
        return method;
    }

    public URI getRequestUri() {
        return requestUri;
    }

    public String getPostDataString() {
        return postDataString;
    }
}