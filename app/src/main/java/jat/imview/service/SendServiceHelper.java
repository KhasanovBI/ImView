package jat.imview.service;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import jat.imview.rest.http.HTTPMethod;

/**
 * Created by bulat on 07.12.15.
 */
public class SendServiceHelper {
    private static final String LOG_TAG = "MyServiceHelper";
    public static String ACTION_REQUEST_RESULT = "REQUEST_RESULT";
    public static String EXTRA_REQUEST_ID = "EXTRA_REQUEST_ID";
    public static String EXTRA_RESULT_CODE = "EXTRA_RESULT_CODE";

    private static final String REQUEST_ID = "REQUEST_ID";
    private AtomicInteger requestCounter = new AtomicInteger(0);
    private final Map<RequestType, Integer> pendingRequests = new HashMap<>();
    private WeakReference<Context> weakContext;
    public static volatile SendServiceHelper sendServiceHelperInstance;

    public static SendServiceHelper getInstance(Context context) {
        SendServiceHelper localInstance = sendServiceHelperInstance;
        if (localInstance == null) {
            synchronized (SendServiceHelper.class) {
                localInstance = sendServiceHelperInstance;
                if (localInstance == null) {
                    sendServiceHelperInstance = localInstance = new SendServiceHelper(context);
                }
            }
        }
        return localInstance;
    }

    private SendServiceHelper(Context context) {
        weakContext = new WeakReference<>(context.getApplicationContext());
    }

    public boolean isRequestPending(RequestType requestType) {
        return pendingRequests.containsKey(requestType);
    }

    private int generateRequestId() {
        return requestCounter.getAndIncrement();
    }

    private Intent prepareIntent(Context context, int requestId, RequestType requestType, HTTPMethod httpMethod) {
        Log.d(LOG_TAG, "prepare intent");
        ResultReceiver serviceCallback = new ResultReceiver(null) {
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                handleResponse(resultCode, resultData);
            }
        };
        Intent intent = new Intent(context, SendService.class);
        intent.putExtra(REQUEST_ID, requestId);
        intent.putExtra(SendService.METHOD_EXTRA, httpMethod);
        intent.putExtra(SendService.REQUEST_TYPE_EXTRA, requestType);
        intent.putExtra(SendService.SERVICE_CALLBACK_EXTRA, serviceCallback);
        return intent;
    }

    private void handleResponse(int resultCode, Bundle resultData) {
        Log.d(LOG_TAG, "handleResponse");
        Intent originalIntent = resultData.getParcelable(SendService.ORIGINAL_INTENT_EXTRA);
        if (originalIntent != null) {
            int requestId = originalIntent.getIntExtra(REQUEST_ID, -1);
            Log.d(LOG_TAG, "Remove request from map");
            pendingRequests.values().remove(requestId);

            Intent broadcastIntent = new Intent(ACTION_REQUEST_RESULT);
            broadcastIntent.putExtra(EXTRA_REQUEST_ID, requestId);
            broadcastIntent.putExtra(EXTRA_RESULT_CODE, resultCode);
            weakContext.get().sendBroadcast(broadcastIntent);
        }
    }

    public int requestLogin(String username, String password) {
        RequestType requestType = RequestType.LOGIN;
        if (isRequestPending(requestType)) {
            return pendingRequests.get(requestType);
        }
        int requestId = generateRequestId();
        pendingRequests.put(requestType, requestId);

        Context context = weakContext.get();
        Intent intent = prepareIntent(context, requestId, requestType, HTTPMethod.POST);
        intent.putExtra(SendService.LOGIN_USERNAME_EXTRA, username);
        intent.putExtra(SendService.LOGIN_PASSWORD_EXTRA, password);
        context.startService(intent);
        return requestId;
    }

    public int requestSignup(String username, String password) {
        RequestType requestType = RequestType.SIGNUP;
        if (isRequestPending(requestType)) {
            return pendingRequests.get(requestType);
        }
        int requestId = generateRequestId();
        pendingRequests.put(requestType, requestId);

        Context context = weakContext.get();
        Intent intent = prepareIntent(context, requestId, requestType, HTTPMethod.POST);
        intent.putExtra(SendService.SIGNUP_USERNAME_EXTRA, username);
        intent.putExtra(SendService.SIGNUP_PASSWORD_EXTRA, password);
        context.startService(intent);
        return requestId;
    }

    public int requestImageNew(Bitmap bitmap) {
        RequestType requestType = RequestType.IMAGE_NEW;
        if (isRequestPending(requestType)) {
            return pendingRequests.get(requestType);
        }
        int requestId = generateRequestId();
        pendingRequests.put(requestType, requestId);

        Context context = weakContext.get();
        Intent intent = prepareIntent(context, requestId, requestType, HTTPMethod.POST);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        intent.putExtra(SendService.IMAGE_NEW_IMAGE_BYTE_ARRAY_EXTRA, byteArray);
        context.startService(intent);
        return requestId;
    }

    public int requestImageGet(int imageId) {
        RequestType requestType = RequestType.IMAGE_GET;
        if (isRequestPending(requestType)) {
            return pendingRequests.get(requestType);
        }
        int requestId = generateRequestId();
        pendingRequests.put(requestType, requestId);

        Context context = weakContext.get();
        Intent intent = prepareIntent(context, requestId, requestType, HTTPMethod.GET);
        intent.putExtra(SendService.IMAGE_GET_IMAGE_ID_EXTRA, imageId);
        context.startService(intent);
        return requestId;
    }

    public int requestImageList(boolean isFeatured) {
        RequestType requestType = RequestType.IMAGE_LIST;
        if (isRequestPending(requestType)) {
            return pendingRequests.get(requestType);
        }
        int requestId = generateRequestId();
        pendingRequests.put(requestType, requestId);

        Context context = weakContext.get();
        Intent intent = prepareIntent(context, requestId, requestType, HTTPMethod.GET);
        intent.putExtra(SendService.IMAGE_LIST_IS_FEATURED_EXTRA, isFeatured);
        context.startService(intent);
        return requestId;
    }

    public int requestImageVote(int imageId, boolean isUpVote) {
        RequestType requestType = RequestType.IMAGE_VOTE;
        if (isRequestPending(requestType)) {
            return pendingRequests.get(requestType);
        }
        int requestId = generateRequestId();
        pendingRequests.put(requestType, requestId);

        Context context = weakContext.get();
        Intent intent = prepareIntent(context, requestId, requestType, HTTPMethod.POST);
        intent.putExtra(SendService.IMAGE_VOTE_IMAGE_ID_EXTRA, imageId);
        intent.putExtra(SendService.IMAGE_VOTE_IS_UP_VOTE_EXTRA, isUpVote);
        context.startService(intent);
        return requestId;
    }

    public int requestCommentNew(int imageId, String text) {
        RequestType requestType = RequestType.COMMENT_NEW;
        if (isRequestPending(requestType)) {
            return pendingRequests.get(requestType);
        }
        int requestId = generateRequestId();
        pendingRequests.put(requestType, requestId);

        Context context = weakContext.get();
        Intent intent = prepareIntent(context, requestId, requestType, HTTPMethod.POST);
        intent.putExtra(SendService.COMMENT_NEW_IMAGE_ID_EXTRA, imageId);
        intent.putExtra(SendService.COMMENT_NEW_TEXT_EXTRA, text);
        context.startService(intent);
        return requestId;
    }

    public int requestCommentList(int imageId) {
        RequestType requestType = RequestType.COMMENT_LIST;
        if (isRequestPending(requestType)) {
            return pendingRequests.get(requestType);
        }
        int requestId = generateRequestId();
        pendingRequests.put(requestType, requestId);

        Context context = weakContext.get();
        Intent intent = prepareIntent(context, requestId, requestType, HTTPMethod.GET);
        intent.putExtra(SendService.COMMENT_LIST_IMAGE_ID_EXTRA, imageId);
        context.startService(intent);
        return requestId;
    }

    public int requestCommentVote(int commentId, boolean isUpVote) {
        RequestType requestType = RequestType.COMMENT_VOTE;
        if (isRequestPending(requestType)) {
            return pendingRequests.get(requestType);
        }
        int requestId = generateRequestId();
        pendingRequests.put(requestType, requestId);

        Context context = weakContext.get();
        Intent intent = prepareIntent(context, requestId, requestType, HTTPMethod.POST);
        intent.putExtra(SendService.COMMENT_VOTE_COMMENT_ID_EXTRA, commentId);
        intent.putExtra(SendService.COMMENT_VOTE_IS_UP_VOTE_EXTRA, isUpVote);
        context.startService(intent);
        return requestId;
    }
}
