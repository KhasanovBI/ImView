package jat.imview.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import jat.imview.processor.CommentListProcessor;
import jat.imview.processor.CommentProcessor;
import jat.imview.processor.CommentVoteProcessor;
import jat.imview.processor.ImageListProcessor;
import jat.imview.processor.ImageProcessor;
import jat.imview.processor.ImageVoteProcessor;
import jat.imview.processor.LoginProcessor;
import jat.imview.processor.ProcessorCallback;
import jat.imview.processor.SignupProcessor;
import jat.imview.rest.http.HTTPMethod;

/**
 * Created by bulat on 07.12.15.
 */
public class SendService extends IntentService {
    private static final String LOG_TAG = "MyService";

    public static final String METHOD_EXTRA = "jat.imview.service.METHOD_EXTRA";
    public static final String REQUEST_TYPE_EXTRA = "jat.imview.service.REQUEST_TYPE_EXTRA";
    public static final String SERVICE_CALLBACK_EXTRA = "jat.imview.service.SERVICE_CALLBACK_EXTRA";

    public static final String LOGIN_USERNAME_EXTRA = "jat.imview.service.LOGIN_USERNAME_EXTRA";
    public static final String LOGIN_PASSWORD_EXTRA = "jat.imview.service.LOGIN_PASSWORD_EXTRA";
    public static final String SIGNUP_USERNAME_EXTRA = "jat.imview.service.SIGNUP_USERNAME_EXTRA";
    public static final String SIGNUP_PASSWORD_EXTRA = "jat.imview.service.SIGNUP_PASSWORD_EXTRA";

    public static final String IMAGE_NEW_IMAGE_BYTE_ARRAY_EXTRA = "jat.imview.service.IMAGE_NEW_IMAGE_BYTE_ARRAY_EXTRA";
    public static final String IMAGE_GET_IMAGE_ID_EXTRA = "jat.imview.service.IMAGE_GET_IMAGE_ID_EXTRA";
    public static final String IMAGE_LIST_IS_FEATURED_EXTRA = "jat.imview.service.IMAGE_LIST_IS_FEATURED_EXTRA";
    public static final String IMAGE_VOTE_IMAGE_ID_EXTRA = "jat.imview.service.IMAGE_VOTE_IMAGE_ID_EXTRA";
    public static final String IMAGE_VOTE_IS_UP_VOTE_EXTRA = "jat.imview.service.IMAGE_VOTE_IS_UP_VOTE_EXTRA";


    public static final String COMMENT_NEW_IMAGE_ID_EXTRA = "jat.imview.service.COMMENT_NEW_IMAGE_ID_EXTRA";
    public static final String COMMENT_NEW_TEXT_EXTRA = "jat.imview.service.COMMENT_NEW_TEXT_EXTRA";
    public static final String COMMENT_LIST_IMAGE_ID_EXTRA = "jat.imview.service.COMMENT_LIST_IMAGE_ID_EXTRA";
    public static final String COMMENT_VOTE_COMMENT_ID_EXTRA = "jat.imview.service.COMMENT_VOTE_COMMENT_ID_EXTRA";
    public static final String COMMENT_VOTE_IS_UP_VOTE_EXTRA = "jat.imview.service.COMMENT_VOTE_IS_UP_VOTE_EXTRA";

    public static final String ORIGINAL_INTENT_EXTRA = "jat.imview.service.ORIGINAL_INTENT_EXTRA";

    private static final int INVALID_REQUEST = -1;

    private Intent mOriginalRequestIntent;
    private ResultReceiver mServiceCallback;

    public SendService() {
        super("SendService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mOriginalRequestIntent = intent;
        HTTPMethod httpMethod = (HTTPMethod) mOriginalRequestIntent.getSerializableExtra(METHOD_EXTRA);
        RequestType requestType = (RequestType) mOriginalRequestIntent.getSerializableExtra(REQUEST_TYPE_EXTRA);
        mServiceCallback = mOriginalRequestIntent.getParcelableExtra(SERVICE_CALLBACK_EXTRA);
        Log.d(LOG_TAG, "Handle intent");
        switch (requestType) {
            case LOGIN:
                if (httpMethod.equals(HTTPMethod.POST)) {
                    Log.d(LOG_TAG, "Login");
                    String username = mOriginalRequestIntent.getStringExtra(LOGIN_USERNAME_EXTRA);
                    String password = mOriginalRequestIntent.getStringExtra(LOGIN_PASSWORD_EXTRA);
                    LoginProcessor loginProcessor = new LoginProcessor(getApplicationContext(), username, password);
                    loginProcessor.getLogin(makeProcessorCallback());
                } else {
                    sendInvalidRequestCode();
                }
                break;
            case SIGNUP:
                if (httpMethod.equals(HTTPMethod.POST)) {
                    Log.d(LOG_TAG, "Signup");
                    String username = mOriginalRequestIntent.getStringExtra(SIGNUP_USERNAME_EXTRA);
                    String password = mOriginalRequestIntent.getStringExtra(SIGNUP_PASSWORD_EXTRA);
                    SignupProcessor signupProcessor = new SignupProcessor(getApplicationContext(), username, password);
                    signupProcessor.getSignup(makeProcessorCallback());
                } else {
                    sendInvalidRequestCode();
                }
                break;
            case IMAGE_NEW:
                if (httpMethod.equals(HTTPMethod.POST)) {
                    Log.d(LOG_TAG, "Image new");
                    byte[] byteArray = mOriginalRequestIntent.getByteArrayExtra(IMAGE_NEW_IMAGE_BYTE_ARRAY_EXTRA);
                    ImageProcessor imageProcessor = new ImageProcessor(getApplicationContext(), byteArray);
                    imageProcessor.postImageNew(makeProcessorCallback());
                } else {
                    sendInvalidRequestCode();
                }
                break;
            case IMAGE_LIST:
                if (httpMethod.equals(HTTPMethod.GET)) {
                    Log.d(LOG_TAG, "Image List");
                    boolean isFeatured = mOriginalRequestIntent.getBooleanExtra(IMAGE_LIST_IS_FEATURED_EXTRA, false);
                    ImageListProcessor imageListProcessor = new ImageListProcessor(getApplicationContext(), isFeatured);
                    imageListProcessor.getImageList(makeProcessorCallback());
                } else {
                    sendInvalidRequestCode();
                }
                break;
            case IMAGE_VOTE:
                if (httpMethod.equals(HTTPMethod.POST)) {
                    Log.d(LOG_TAG, "Image Vote");
                    int imageId = mOriginalRequestIntent.getIntExtra(IMAGE_VOTE_IMAGE_ID_EXTRA, -1);
                    boolean isUpVote = mOriginalRequestIntent.getBooleanExtra(IMAGE_VOTE_IS_UP_VOTE_EXTRA, true);
                    ImageVoteProcessor imageVoteProcessor = new ImageVoteProcessor(getApplicationContext(), imageId, isUpVote);
                    imageVoteProcessor.getImage(makeProcessorCallback());
                } else {
                    sendInvalidRequestCode();
                }
                break;
            case COMMENT_NEW:
                if (httpMethod.equals(HTTPMethod.POST)) {
                    Log.d(LOG_TAG, "Comment New");
                    int imageId = mOriginalRequestIntent.getIntExtra(COMMENT_NEW_IMAGE_ID_EXTRA, 0);
                    String text = mOriginalRequestIntent.getStringExtra(COMMENT_NEW_TEXT_EXTRA);
                    CommentProcessor commentProcessor = new CommentProcessor(getApplicationContext(), imageId, text);
                    commentProcessor.getComment(makeProcessorCallback());
                } else {
                    sendInvalidRequestCode();
                }
                break;
            case COMMENT_LIST:
                if (httpMethod.equals(HTTPMethod.GET)) {
                    Log.d(LOG_TAG, "Comment List");
                    int imageId = mOriginalRequestIntent.getIntExtra(COMMENT_LIST_IMAGE_ID_EXTRA, 0);
                    CommentListProcessor commentListProcessor = new CommentListProcessor(getApplicationContext(), imageId);
                    commentListProcessor.getCommentList(makeProcessorCallback());
                } else {
                    sendInvalidRequestCode();
                }
                break;
            case COMMENT_VOTE:
                if (httpMethod.equals(HTTPMethod.POST)) {
                    Log.d(LOG_TAG, "Comment Vote");
                    int commentId = mOriginalRequestIntent.getIntExtra(COMMENT_VOTE_COMMENT_ID_EXTRA, -1);
                    boolean isUpVote = mOriginalRequestIntent.getBooleanExtra(COMMENT_VOTE_IS_UP_VOTE_EXTRA, true);
                    CommentVoteProcessor commentVoteProcessor = new CommentVoteProcessor(getApplicationContext(), commentId, isUpVote);
                    commentVoteProcessor.getImage(makeProcessorCallback());
                } else {
                    sendInvalidRequestCode();
                }
                break;
            default:
                sendInvalidRequestCode();
                break;
        }
    }

    private ProcessorCallback makeProcessorCallback() {
        Log.d(LOG_TAG, "makeProcessorCallback");
        return new ProcessorCallback() {
            @Override
            public void send(int resultCode) {
                if (mServiceCallback != null) {
                    mServiceCallback.send(resultCode, getOriginalIntentBundle());
                }
            }
        };
    }

    private void sendInvalidRequestCode() {
        mServiceCallback.send(INVALID_REQUEST, getOriginalIntentBundle());
    }

    private Bundle getOriginalIntentBundle() {
        Bundle originalRequest = new Bundle();
        originalRequest.putParcelable(ORIGINAL_INTENT_EXTRA, mOriginalRequestIntent);
        return originalRequest;
    }
}
