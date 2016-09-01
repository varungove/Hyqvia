package com.example.ankush.hyqvia;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ankush on 5/1/16.
 */
public class Profile extends AppCompatActivity {

    EditText info;
    TextView email;
    TextView name;
    Button edit;
    String about_me, username;

    JSONObject jsonObject;
    JSONParser jsonParser = new JSONParser();
    private ProgressDialog pDialog;

    private static final String GET_PROFILE_URL = "http://web.engr.illinois.edu/~goverdh2/returnUser.php";
    private static final String UPDATE_URL = "http://web.engr.illinois.edu/~goverdh2/addBio.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        username = getIntent().getExtras().getString("uid");

        info = (EditText) findViewById(R.id.info);
        email = (TextView) findViewById(R.id.email);
        edit = (Button) findViewById(R.id.edit_info);
        name = (TextView) findViewById(R.id.name);

        new getUser().execute();

    }

    public void editInfo (View view) {
        if ((edit.getText().toString()).equals("EDIT ABOUT ME")) {
            info.setClickable(true);
            info.setCursorVisible(true);
            info.setFocusable(true);
            info.setFocusableInTouchMode(true);
            info.requestFocus();

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(info, InputMethodManager.SHOW_IMPLICIT);

            edit.setText("UPDATE");
        } else {
            about_me = info.getText().toString();
            new updateInfo().execute();

            info.setClickable(false);
            info.setCursorVisible(false);
            info.setFocusable(false);
            info.setFocusableInTouchMode(false);
            info.clearFocus();

            edit.setText("EDIT ABOUT ME");
        }

    }

    public void message (View view) {
        Intent intent = new Intent(Profile.this, Chat.class);
        intent.putExtra("friend_name", email.getText().toString());
        startActivity(intent);
    }

    class getUser extends AsyncTask<String, String, String> {

        //three methods get called, first preExecture, then do in background, and once do
        //in back ground is completed, the onPost execture method will be called.

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Profile.this);
            pDialog.setMessage("Getting profile...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("uid", username));

            // getting product details by making HTTP request
            jsonObject = jsonParser.makeHttpRequest(GET_PROFILE_URL, "POST", params);

            return null;
        }

        protected void onPostExecute(String string) {
            // dismiss the dialog once product deleted
            pDialog.dismiss();
            populateProfile();

        }

    }

    private void populateProfile() {
        JSONObject user_detail = null;
        try {
            user_detail = jsonObject.getJSONObject("message");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            name.setText(user_detail.getString("name"));
            email.setText(user_detail.getString("uid"));
            info.setText(user_detail.getString("bio"));

            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(Profile.this);
            String current_user = sp.getString("username", null);

            if((user_detail.getString("uid")).equals(current_user)) {
                edit.setVisibility(View.VISIBLE);
            } else {
                Button message = (Button) findViewById(R.id.profile_message);
                message.setVisibility(View.VISIBLE);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    class updateInfo extends AsyncTask<String, String, String> {

        //three methods get called, first preExecture, then do in background, and once do
        //in back ground is completed, the onPost execture method will be called.

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Profile.this);
            pDialog.setMessage("Updating About Me...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("uid", username));
            params.add(new BasicNameValuePair("data", about_me));

            // getting product details by making HTTP request
            jsonObject = jsonParser.makeHttpRequest(UPDATE_URL, "POST", params);

            return null;
        }

        protected void onPostExecute(String string) {
            // dismiss the dialog once product deleted
            pDialog.dismiss();

        }

    }
}
