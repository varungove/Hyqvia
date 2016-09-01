package com.example.ankush.hyqvia;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
public class Messages extends AppCompatActivity implements View.OnClickListener {

    JSONObject jsonObject;
    JSONParser jsonParser = new JSONParser();
    private ProgressDialog pDialog;

    private static final String GET_MESSAGES_URL = "http://web.engr.illinois.edu/~goverdh2/returnInbox.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messages);

        new getMessages().execute();

    }

    @Override
    public void onClick(View v) {
        TextView name_tv = (TextView) v.findViewById(R.id.person_name);
        String friend_name = name_tv.getText().toString();

        Intent intent = new Intent(Messages.this, Chat.class);
        intent.putExtra("friend_name", friend_name);
        startActivity(intent);
    }

    class getMessages extends AsyncTask<String, String, String> {

        //three methods get called, first preExecture, then do in background, and once do
        //in back ground is completed, the onPost execture method will be called.

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Messages.this);
            pDialog.setMessage("Getting the threads...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... args) {

            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(Messages.this);
            String username = sp.getString("username", null);

            // Building Parameters
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("uid", username));

            // getting product details by making HTTP request
            jsonObject = jsonParser.makeHttpRequest(GET_MESSAGES_URL, "POST", params);

            return null;
        }

        protected void onPostExecute(String string) {
            // dismiss the dialog once product deleted
            pDialog.dismiss();
            populateMessages();

        }

    }

    private void populateMessages() {
        JSONArray jsonArray = null;
        try {
            jsonArray = jsonObject.getJSONArray("message");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject thread = null;
            try {
                thread = jsonArray.getJSONObject(i);

                LinearLayout myLayout = (LinearLayout)findViewById(R.id.container);
                View hiddenInfo = getLayoutInflater().inflate(R.layout.messages_user_row, myLayout, false);
                myLayout.addView(hiddenInfo);

                TextView uid = (TextView) hiddenInfo.findViewById(R.id.person_name);
                uid.setText(thread.getString("uid"));

                hiddenInfo.setOnClickListener(this);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


}
