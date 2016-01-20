package jat.imview.rest.restMethod;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import jat.imview.R;
import jat.imview.rest.HTTPUtil;
import jat.imview.rest.http.HTTPMethod;
import jat.imview.rest.http.Request;
import jat.imview.rest.resource.ImageResource;
import jat.imview.rest.restMethod.base.AbstractRestMethod;
import jat.imview.util.BitmapSender;

import static jat.imview.rest.http.ConnectionParams.HOST;
import static jat.imview.rest.http.ConnectionParams.SCHEME;

public class ImageRestMethod extends AbstractRestMethod<ImageResource> {
    private HTTPMethod httpMethod;
    private byte[] byteArray;
    private static final String PATH = "/image/new";
    private static final URI IMAGE_NEW_URL = URI.create(SCHEME + HOST + PATH);

    public ImageRestMethod(HTTPMethod httpMethod, byte[] byteArray) {
        this.httpMethod = httpMethod;
        this.byteArray = byteArray;
    }

    @Override
    protected Request buildRequest() {
        Map<String, String> params = new HashMap<>();
        params.put("file", new String(byteArray));

        //return new Request(httpMethod, IMAGE_NEW_URL, HTTPUtil.getPostDataString(params));
        return new Request(HTTPMethod.GET, IMAGE_NEW_URL, null);
    }

    @Override
    protected ImageResource parseResponseBody(byte[] responseBody) throws Exception {
        return new ImageResource(responseBody);
    }
}
