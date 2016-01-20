package jat.imview.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.google.android.gms.ads.AdRequest;

import java.util.HashMap;
import java.util.Map;

import jat.imview.R;
import jat.imview.rest.http.HTTPClient;
import jat.imview.service.RequestType;
import jat.imview.service.SendServiceHelper;

public abstract class BaseActivity extends AppCompatActivity {
    private static final String LOG_TAG = "MyBaseActivity";
    protected boolean isNeedToShowAd = false;
    protected AdRequest adRequest;
    protected BroadcastReceiver requestReceiver;
    protected Map<Integer, RequestType> requestsIdMap = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        if (isNeedToShowAd) {
            adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .build();
        }
    }

    protected void handleResponseErrors(int resultCode) {
        Log.d(LOG_TAG, "Handle error " + String.valueOf(resultCode));
        if (resultCode >= 500) {
            Toast.makeText(getApplicationContext(), R.string.server_error, Toast.LENGTH_SHORT).show();
        } else {
            switch (resultCode) {
                case 401:
                    Intent intent = new Intent(this, LoginActivity.class);
                    Toast.makeText(getApplicationContext(), R.string.login_required, Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                    break;
                case -1:
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.network_unreachable), Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.unhandled_error) + ": " + resultCode, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (requestReceiver != null) {
            try {
                this.unregisterReceiver(requestReceiver);
            } catch (IllegalArgumentException e) {
                Log.e(LOG_TAG, e.getLocalizedMessage(), e);
            }
        }
    }
}
