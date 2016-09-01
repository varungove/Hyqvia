package com.example.ankush.hyqvia;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    JSONParser jsonParser = new JSONParser();
    private ProgressDialog pDialog;

    private static final String LOGIN_URL = "http://web.engr.illinois.edu/~goverdh2/login.php";

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    EditText user, pass ;
    String username, password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

         user = (EditText)findViewById(R.id.username);
         pass = (EditText)findViewById(R.id.password);

    }

    public void signIn (View view) {
        username = user.getText().toString();
        password = pass.getText().toString();
        new AttemptLogin().execute();

    }

    public void register (View view) {
        Intent intent = new Intent(LoginActivity.this, Register.class);
        startActivity(intent);
    }

    //AsyncTask is a seperate thread than the thread that runs the GUI
    //Any type of networking should be done with asynctask.
    class AttemptLogin extends AsyncTask<String, String, String> {

        //three methods get called, first preExecture, then do in background, and once do
        //in back ground is completed, the onPost execture method will be called.

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(LoginActivity.this);
            pDialog.setMessage("Attempting login...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... args) {

            // TODO Auto-generated method stub
            // Check for success tag
            int success;

            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("username", username));
                params.add(new BasicNameValuePair("password", password));

                // getting product details by making HTTP request
                JSONObject json = jsonParser.makeHttpRequest(
                        LOGIN_URL, "POST", params);

                // json success tag
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {

                    // save user data
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(LoginActivity.this);
                    SharedPreferences.Editor edit = sp.edit();
                    edit.putString("username", username.toLowerCase());
                    edit.commit();

                    Intent intent = new Intent(LoginActivity.this, Home.class);
                    startActivity(intent);
                    finish();
                    return json.getString(TAG_MESSAGE);
                }else{
                    return json.getString(TAG_MESSAGE);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;

        }

        protected void onPostExecute(String string) {

            // dismiss the dialog once product deleted
            pDialog.dismiss();
            if (string != null){
                Toast.makeText(LoginActivity.this, string, Toast.LENGTH_LONG).show();
            }
        }
    }
}
