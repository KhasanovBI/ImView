package jat.imview.rest.restMethod.base;

import jat.imview.rest.http.HTTPClient;
import jat.imview.rest.http.Request;
import jat.imview.rest.http.Response;
import jat.imview.rest.resource.base.Resource;

public abstract class AbstractRestMethod<T extends Resource> implements RestMethod<T> {
    public RestMethodResult execute() {
        Request request = buildRequest();
        Response response = doRequest(request);
        return buildResult(response);
    }

    protected abstract Request buildRequest();

    protected RestMethodResult<T> buildResult(Response response) {
        int status = response.status;
        String statusMessage = "";
        T resource = null;

        if (response.status == 200) {
            try {
                resource = parseResponseBody(response.body);
            } catch (Exception ex) {
                ex.printStackTrace();
                status = 553;
                statusMessage = ex.getMessage();
            }
        }

        return new RestMethodResult<>(status, statusMessage, resource);
    }

    private Response doRequest(Request request) {
        HTTPClient client = new HTTPClient();
        return client.execute(request);
    }

    protected abstract T parseResponseBody(byte[] responseBody) throws Exception;
}