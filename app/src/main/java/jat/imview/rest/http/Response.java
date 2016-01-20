package jat.imview.rest.http;

public class Response {
    public int status;
    public byte[] body;

    protected Response(int status, byte[] body) {
        this.status = status;
        this.body = body;
    }
}

