package jat.imview.util;

import android.graphics.Bitmap;
import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import jat.imview.rest.http.ConnectionParams;
import jat.imview.rest.http.HTTPClient;

/**
 * Created by bulat on 20.01.16.
 */
public class BitmapSender {
    private static String attachmentName = "file";
    private static String attachmentFileName = "bitmap.png";
    private static String crlf = "\r\n";
    private static String twoHyphens = "--";
    private static String boundary = "*****";
    public static void send(Bitmap bitmap) {
        // Static stuff:

        // Setup the request:

        HttpURLConnection httpUrlConnection = null;
        URL url = null;
        try {
            url = new URL(ConnectionParams.SCHEME + ConnectionParams.HOST + "/image/new");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            httpUrlConnection = (HttpURLConnection) url.openConnection();

            httpUrlConnection.setUseCaches(false);
            httpUrlConnection.setDoOutput(true);

            httpUrlConnection.setRequestMethod("POST");
            httpUrlConnection.setRequestProperty("Connection", "Keep-Alive");
            httpUrlConnection.setRequestProperty("Cache-Control", "no-cache");
            httpUrlConnection.setRequestProperty(
                    "Content-Type", "multipart/form-data;boundary=" + boundary);

            if (HTTPClient.msCookieManager.getCookieStore().getCookies().size() > 0) {
                //While joining the Cookies, use ',' or ';' as needed. Most of the server are using ';'
                httpUrlConnection.setRequestProperty("Cookie", TextUtils.join(";", HTTPClient.msCookieManager.getCookieStore().getCookies()));
            }

            //Start content wrapper:

            DataOutputStream request = new DataOutputStream(httpUrlConnection.getOutputStream());
            request.writeBytes(twoHyphens + boundary + crlf);
            request.writeBytes("Content-Disposition: form-data; name=\"" +
                    attachmentName + "\";filename=\"" +
                    attachmentFileName + "\"" + crlf);
            request.writeBytes(crlf);

            //Convert Bitmap to ByteBuffer:

            //I want to send only 8 bit black & white bitmaps
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] pixels = stream.toByteArray();

            request.write(pixels);
            //End content wrapper:

            request.writeBytes(crlf);
            request.writeBytes(twoHyphens + boundary + twoHyphens + crlf);
            //Flush output buffer:

            request.flush();
            request.close();
            //Get response:

            InputStream responseStream = new BufferedInputStream(httpUrlConnection.getInputStream());

            BufferedReader responseStreamReader = new BufferedReader(new InputStreamReader(responseStream));

            String line = "";
            StringBuilder stringBuilder = new StringBuilder();

            while ((line = responseStreamReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            responseStreamReader.close();

            String response = stringBuilder.toString();
            //Close response stream:

            responseStream.close();
            //Close the connection:
        } catch (IOException e) {
            e.printStackTrace();
        }
        httpUrlConnection.disconnect();
    }
}
