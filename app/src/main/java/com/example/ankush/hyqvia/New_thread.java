package com.example.ankush.hyqvia;

import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by varungove on 5/2/16.
 */
public class New_thread extends AppCompatActivity{

    JSONParser jsonParser = new JSONParser();
    private ProgressDialog pDialog;

    private static final String LOGIN_URL = "http://web.engr.illinois.edu/~goverdh2/addThread.php";

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    EditText subject, details;
    String subjectData, detailsData, post_username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_thread);

        subject = (EditText)findViewById(R.id.subject);
        details = (EditText)findViewById(R.id.details);

    }


    public void submit(View v) {

        subjectData = subject.getText().toString();
        detailsData = details.getText().toString();

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(New_thread.this);
        post_username = sp.getString("username", "anon");
        new addThread().execute();

    }

    //AsyncTask is a seperate thread than the thread that runs the GUI
    //Any type of networking should be done with asynctask.
    class addThread extends AsyncTask<String, String, String> {

        //three methods get called, first preExecture, then do in background, and once do
        //in back ground is completed, the onPost execture method will be called.

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(New_thread.this);
            pDialog.setMessage("Attempting to create thread...");
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
                params.add(new BasicNameValuePair("subject", subjectData));
                params.add(new BasicNameValuePair("data", detailsData));
                params.add(new BasicNameValuePair("user", post_username));

                // getting product details by making HTTP request
                JSONObject json = jsonParser.makeHttpRequest(
                        LOGIN_URL, "POST", params);

                // json success tag
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {

                    Intent intent = new Intent(New_thread.this, Forum_list.class);
                    startActivity(intent);
                    finish();
                    return json.getString(TAG_MESSAGE);

                } else {
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
                Toast.makeText(New_thread.this, string, Toast.LENGTH_LONG).show();
            }

        }

    }

}
