package com.example.WebserviceExample;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import com.facebook.Session;
import com.google.gson.Gson;

/**
 * Created with IntelliJ IDEA.
 * User: ehc
 * Date: 28/7/14
 * Time: 4:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class DashboardActivity extends Activity {
    SharedPreferences preferences;
    User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);
        getCurrentUser();
        populateStats();
    }

    private void populateStats() {
        TextView userName = (TextView) findViewById(R.id.user_name);
        userName.setText((user.getUserName()) != null ? user.getUserName() : user.getFirstName());
    }

    private void getCurrentUser() {
        preferences = getSharedPreferences("user_preferences", 0);
        String jsonString = preferences.getString("user", null);
        user = new Gson().fromJson(jsonString, User.class);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        user = null;
        preferences.edit().putString("user", null).commit();
        Session session = Session.getActiveSession();
        if (session != null) session.close();
        callLoginActivity();
        return true;
    }

    private void callLoginActivity() {
        Intent intent = new Intent(this,
                LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
