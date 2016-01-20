package jat.imview.processor;

import android.content.Context;
import android.util.Log;

import java.lang.ref.WeakReference;

import jat.imview.rest.http.HTTPMethod;
import jat.imview.rest.resource.LoginResource;
import jat.imview.rest.resource.SignupResource;
import jat.imview.rest.restMethod.LoginRestMethod;
import jat.imview.rest.restMethod.SignupRestMethod;
import jat.imview.rest.restMethod.base.RestMethod;
import jat.imview.rest.restMethod.base.RestMethodResult;

/**
 * Created by bulat on 23.12.15.
 */
public class SignupProcessor {
    private WeakReference<Context> weakContext;
    private String username;
    private String password;

    public SignupProcessor(Context context, String username, String password) {
        weakContext = new WeakReference<>(context);
        this.username = username;
        this.password = password;
    }

    public void getSignup(ProcessorCallback processorCallback) {
        RestMethod<SignupResource> signupRestMethod = new SignupRestMethod(HTTPMethod.POST, username, password);
        RestMethodResult<SignupResource> restMethodResult = signupRestMethod.execute();
        processorCallback.send(restMethodResult.getStatusCode());
    }
}