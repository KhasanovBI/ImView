package jat.imview.rest.restMethod;

import android.net.Uri;

import java.net.URI;

import jat.imview.rest.http.HTTPMethod;
import jat.imview.rest.http.Request;
import jat.imview.rest.resource.ImageListResource;
import jat.imview.rest.restMethod.base.AbstractRestMethod;

import static jat.imview.rest.http.ConnectionParams.HOST;
import static jat.imview.rest.http.ConnectionParams.SCHEME;

public class ImageListRestMethod extends AbstractRestMethod<ImageListResource> {
    private HTTPMethod httpMethod;
    private boolean isFeatured;
    private static final String PATH = "/image/list";
    private static final URI IMAGE_LIST_URL = URI.create(SCHEME + HOST + PATH);

    public ImageListRestMethod(HTTPMethod httpMethod, boolean isFeatured) {
        this.httpMethod = httpMethod;
        this.isFeatured = isFeatured;
    }

    @Override
    protected Request buildRequest() {
        URI currentURL = IMAGE_LIST_URL;
        if (isFeatured) {
            currentURL = URI.create(
                    Uri.parse(IMAGE_LIST_URL.toString()).buildUpon()
                            .appendQueryParameter("is_featured", "1")
                            .build().toString()
            );
        }
        return new Request(httpMethod, currentURL, null);
    }

    @Override
    protected ImageListResource parseResponseBody(byte[] responseBody) throws Exception {
        return new ImageListResource(responseBody);
    }
}
