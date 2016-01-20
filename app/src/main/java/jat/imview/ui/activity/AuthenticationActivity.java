package jat.imview.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import jat.imview.R;
import jat.imview.rest.http.HTTPClient;

public class AuthenticationActivity extends DrawerActivity implements View.OnClickListener {

    @Override
    public NavigationDrawerItem getCurrentNavDrawerItem() {
        return NavigationDrawerItem.AUTHENTICATION;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        findViewById(R.id.login_button).setOnClickListener(this);
        findViewById(R.id.signup_button).setOnClickListener(this);
        findViewById(R.id.logout_button).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.login_button:
                intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.signup_button:
                intent = new Intent(this, SignupActivity.class);
                startActivity(intent);
                break;
            case R.id.logout_button:
                HTTPClient.clearCookies();
                getSharedPreferences("cookies", MODE_PRIVATE).edit().remove("cookiesSet").apply();
                Toast.makeText(getApplicationContext(), R.string.you_successful_logout, Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
