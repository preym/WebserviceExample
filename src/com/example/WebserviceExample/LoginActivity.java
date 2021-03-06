package com.example.WebserviceExample;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.google.gson.Gson;

import java.util.Arrays;

public class LoginActivity extends Activity {
    private LoginButton facebookButton;
    private Button twitterButton;
    private User userDetails;
    SharedPreferences preferences;
    ProgressDialog progressBar;
    String accessToken;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("test:", "In onCreate");
        super.onCreate(savedInstanceState);
        preferences = getSharedPreferences("user_preferences", 0);
        String user = preferences.getString("user", null);
        if (user != null) {
            callDashBoard();
            finish();
        }
        setContentView(R.layout.login);
        getWidgets();
    }

    private void callDashBoard() {
        Intent intent = new Intent(getBaseContext(), DashboardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void getWidgets() {
        progressBar = new ProgressDialog(this);
        facebookButton = (LoginButton) findViewById(R.id.button_facebook);
        facebookButton.setReadPermissions(Arrays.asList("public_profile", "name", "email", "user_location", "user_birthday", "user_likes"));
        twitterButton = (Button) findViewById(R.id.button_twitter);
        twitterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                twitterAuthenticate();
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
                    accessToken = session.getAccessToken();
                    Request.newMeRequest(session, new Request.GraphUserCallback() {
                        // callback after Graph API response with user object
                        @Override
                        public void onCompleted(GraphUser user, Response response) {
                            if (user != null) {
                                populateUserFromFB(user);
                            } else {
                                Toast.makeText(getApplicationContext(), "Problem connecting to server", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).executeAsync();
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
        userDetails.setAccessToken(accessToken);
        saveUser(userDetails);
        callDashBoard();
    }


    public void populateUserFromTwitter(twitter4j.User user) {
        userDetails = new User();
        userDetails.setId(user.getId());
        userDetails.setUserName(user.getName());
        userDetails.setAccessToken(getPreferences(0).getString("ACCESS_TOKEN", null));
        userDetails.setSecretKey(getPreferences(0).getString("ACCESS_TOKEN_SECRET", null));
        saveUser(userDetails);
        callDashBoard();
    }

    private void saveUser(User userDetails) {
        preferences.edit().putString("user", new Gson().toJson(userDetails)).commit();
    }

    public void twitterAuthenticate() {
        Fragment login = new LoginFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, login);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.addToBackStack(null);
        ft.commit();
    }
}


