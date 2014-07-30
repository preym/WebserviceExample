package com.example.WebserviceExample;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.google.gson.Gson;

public class LoginActivity extends Activity {
    private LoginButton facebookButton;
    private Button twitterButton;
    private User userDetails;
    SharedPreferences preferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getSharedPreferences("user_preferences", 0);
        String user = preferences.getString("user", null);
        if (user != null) {
            callDashBoard();
        }
        setContentView(R.layout.login);
        getWidgets();
    }

    private void callDashBoard() {
        Intent intent = new Intent(getBaseContext(), DashboardActivity.class);
        startActivity(intent);
    }

    private void getWidgets() {
        facebookButton = (LoginButton) findViewById(R.id.button_facebook);
        twitterButton = (Button) findViewById(R.id.button_twitter);
        twitterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), DashboardActivity.class);
                startActivity(intent);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session session = Session.getActiveSession();
        session.onActivityResult(this, requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            getSession();
        }
    }


    private void getSession() {
        Session.openActiveSession(this, true, new Session.StatusCallback() {
            @Override
            public void call(Session session, SessionState state, Exception exception) {
                if (!session.isOpened())
                    session = new Session(getApplicationContext());
                if (session.isOpened()) {
                    Request.newMeRequest(session, new Request.GraphUserCallback() {
                        // callback after Graph API response with user object
                        @Override
                        public void onCompleted(GraphUser user, Response response) {
                            if (user != null) {
                                populateUserFromFB(user);
                            } else {
                            }
                        }
                    }).executeAsync();
                } else {
                }
            }
        });
    }

    private void populateUserFromFB(GraphUser currentUser) {
        userDetails = new User();
        userDetails.setFirstName(currentUser.getFirstName());
        userDetails.setLastName(currentUser.getLastName());
        userDetails.setUserName(currentUser.getUsername());
        userDetails.setEmail((String) currentUser.getProperty("email"));
        Log.d("test:", "User: " + userDetails);
        saveUser(userDetails);
        callDashBoard();
    }

    private void saveUser(User userDetails) {
        preferences.edit().putString("user", new Gson().toJson(userDetails)).commit();
    }
}


