package com.example.ankush.hyqvia;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ankush on 4/30/16.
 */
public class Register extends AppCompatActivity {

    JSONParser jsonParser = new JSONParser();
    private ProgressDialog pDialog;

    private static final String LOGIN_URL = "http://web.engr.illinois.edu/~goverdh2/register.php";

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    EditText name, pass, email;
    RadioGroup radioGroup;
    RadioButton radioButton;
    private Button btnDisplay;
    String username, useremail, userpass, role;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        name = (EditText)findViewById(R.id.name);
        pass = (EditText)findViewById(R.id.password);
        email = (EditText)findViewById(R.id.email);


    }



    public void signUp (View view) {
        radioGroup = (RadioGroup) findViewById(R.id.role);
        btnDisplay = (Button) findViewById(R.id.sign_up);
        username = name.getText().toString();
        userpass = pass.getText().toString();
        useremail = email.getText().toString();


        int selectedId = radioGroup.getCheckedRadioButtonId();


        radioButton = (RadioButton)findViewById(selectedId);
        role = radioButton.getText().toString();



        new CreateUser().execute();



    }

    class CreateUser extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        boolean failure = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Register.this);
            pDialog.setMessage("Creating User...");
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
                params.add(new BasicNameValuePair("password", userpass));
                params.add(new BasicNameValuePair("email", useremail));
                params.add(new BasicNameValuePair("role", role));

                Log.d("request!", "starting");

                //Posting user data to script
                JSONObject json = jsonParser.makeHttpRequest(
                        LOGIN_URL, "POST", params);

                // full json response
                Log.d("Login attempt", json.toString());

                // json success element
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    Log.d("User Created!", json.toString());

                    // save user data
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(Register.this);
                    SharedPreferences.Editor edit = sp.edit();
                    edit.putString("username", username.toLowerCase());
                    edit.commit();

                    Intent intent = new Intent(Register.this, Home.class);
                    startActivity(intent);
                    finish();
                    return json.getString(TAG_MESSAGE);
                }else{
                    Log.d("Login Failure!", json.getString(TAG_MESSAGE));
                    return json.getString(TAG_MESSAGE);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;

        }
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once product deleted
            pDialog.dismiss();
            if (file_url != null){
                Toast.makeText(Register.this, file_url, Toast.LENGTH_LONG).show();
            }

        }

    }
}
