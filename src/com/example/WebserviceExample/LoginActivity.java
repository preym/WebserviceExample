package com.example.WebserviceExample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.facebook.widget.LoginButton;

public class LoginActivity extends Activity {
    private LoginButton facebookButton;
    private Button twitterButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        getWidgets();
    }

    private void getWidgets() {
//        facebookButton = (LoginButton) findViewById(R.id.button_facebook);
        twitterButton = (Button) findViewById(R.id.button_twitter);
        twitterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), DashboardActivity.class);
                startActivity(intent);
            }
        });
    }
}
