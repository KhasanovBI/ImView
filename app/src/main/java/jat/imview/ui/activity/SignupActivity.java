package jat.imview.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import jat.imview.R;
import jat.imview.rest.http.HTTPClient;
import jat.imview.service.SendServiceHelper;

public class SignupActivity extends BaseActivity implements View.OnClickListener {
    private static String LOG_TAG = "MyLoginActivity";
    private TextView mUsernameTextView;
    private TextView mPasswordTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mUsernameTextView = (TextView) findViewById(R.id.username);
        mPasswordTextView = (TextView) findViewById(R.id.password);

        findViewById(R.id.login_button).setOnClickListener(this);
        findViewById(R.id.back_button).setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(SendServiceHelper.ACTION_REQUEST_RESULT);
        requestReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int resultRequestId = intent.getIntExtra(SendServiceHelper.EXTRA_REQUEST_ID, 0);
                Log.d(LOG_TAG, "Received intent " + intent.getAction() + ", request ID " + resultRequestId);
                int resultCode = intent.getIntExtra(SendServiceHelper.EXTRA_RESULT_CODE, 0);
                Log.d(LOG_TAG, String.valueOf(resultCode));
                if (resultCode != 200) {
                    if (resultCode == 400) {
                        Toast.makeText(getApplicationContext(), R.string.user_already_exist, Toast.LENGTH_SHORT).show();
                    } else {
                        handleResponseErrors(resultCode);
                    }
                } else {
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                    if (sharedPreferences.getBoolean("save_cookies_at_close", false)) {
                        HTTPClient.writeCookiesToSharedPreferences(getSharedPreferences("cookies", MODE_PRIVATE));
                    }
                    finish();
                }
            }
        };
        registerReceiver(requestReceiver, filter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_button:
                finish();
                break;
            case R.id.login_button:
                String username = mUsernameTextView.getText().toString();
                String password = mPasswordTextView.getText().toString();
                if (username.length() > 0 && password.length() > 0) {
                    SendServiceHelper.getInstance(this).requestSignup(username, password);
                } else {
                    Toast.makeText(getApplicationContext(), R.string.fill_all_fields_please, Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }

    }
}
