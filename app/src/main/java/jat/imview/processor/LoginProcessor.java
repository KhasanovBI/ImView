package jat.imview.processor;

import android.content.Context;

import java.lang.ref.WeakReference;

import jat.imview.rest.http.HTTPMethod;
import jat.imview.rest.resource.LoginResource;
import jat.imview.rest.restMethod.LoginRestMethod;
import jat.imview.rest.restMethod.base.RestMethod;
import jat.imview.rest.restMethod.base.RestMethodResult;

/**
 * Created by bulat on 23.12.15.
 */
public class LoginProcessor {
    private WeakReference<Context> weakContext;
    private String username;
    private String password;

    public LoginProcessor(Context context, String username, String password) {
        weakContext = new WeakReference<>(context);
        this.username = username;
        this.password = password;
    }

    public void getLogin(ProcessorCallback processorCallback) {
        RestMethod<LoginResource> loginRestMethod = new LoginRestMethod(HTTPMethod.POST, username, password);
        RestMethodResult<LoginResource> restMethodResult = loginRestMethod.execute();
        processorCallback.send(restMethodResult.getStatusCode());
    }
}