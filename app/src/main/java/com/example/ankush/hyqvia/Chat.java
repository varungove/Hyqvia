package com.example.ankush.hyqvia;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ankush on 5/3/16.
 */

public class Chat extends AppCompatActivity {

    String friend_name, username, data;

    JSONObject jsonObject;

    JSONParser jsonParser = new JSONParser();
    private ProgressDialog pDialog;

    private static final String GET_CHAT_URL = "http://web.engr.illinois.edu/~goverdh2/returnMessage.php";
    private static final String ADD_MESSAGE_URL = "http://web.engr.illinois.edu/~goverdh2/addMessage.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);
        friend_name = getIntent().getExtras().getString("friend_name");

        new getComments().execute();

    }

    class getComments extends AsyncTask<String, String, String> {

        //three methods get called, first preExecture, then do in background, and once do
        //in back ground is completed, the onPost execture method will be called.

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Chat.this);
            pDialog.setMessage("Getting thread...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... args) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(Chat.this);
            username = sp.getString("username", null);

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("fromUser", username));
            params.add(new BasicNameValuePair("toUser", friend_name));

            // getting product details by making HTTP request
            jsonObject = jsonParser.makeHttpRequest(GET_CHAT_URL, "POST", params);

            return null;

        }

        protected void onPostExecute(String string) {

            // dismiss the dialog once product deleted
            pDialog.dismiss();
            populateChat();
        }

    }

    private void populateChat() {
        JSONArray jsonArray = null;
        try {
            jsonArray = jsonObject.getJSONArray("message");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject msg = null;
            try {
                msg = jsonArray.getJSONObject(i);

                LinearLayout myLayout = (LinearLayout) findViewById(R.id.chat_container);

                if ((msg.getString("fromUser")).equals(friend_name)) {
                    View hiddenInfo = getLayoutInflater().inflate(R.layout.message_received, myLayout, false);
                    myLayout.addView(hiddenInfo);

                    TextView msg_tv = (TextView) hiddenInfo.findViewById(R.id.to_msg);
                    msg_tv.setText(msg.getString("data"));
                } else {
                    View hiddenInfo = getLayoutInflater().inflate(R.layout.message_sent, myLayout, false);
                    myLayout.addView(hiddenInfo);

                    TextView msg_tv = (TextView) hiddenInfo.findViewById(R.id.from_msg);
                    msg_tv.setText(msg.getString("data"));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    public void sendMsg (View view) {
        EditText msg = (EditText) findViewById(R.id.msg);
        data = msg.getText().toString();

        new sendMessage().execute();
    }
    class sendMessage extends AsyncTask<String, String, String> {

        //three methods get called, first preExecture, then do in background, and once do
        //in back ground is completed, the onPost execture method will be called.

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Chat.this);
            pDialog.setMessage("Sending message...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... args) {

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("fromUser", username));
            params.add(new BasicNameValuePair("toUser", friend_name));
            params.add(new BasicNameValuePair("data", data));


            // getting product details by making HTTP request
            JSONObject json = jsonParser.makeHttpRequest(ADD_MESSAGE_URL, "POST", params);

            return null;

        }

        protected void onPostExecute(String string) {

            // dismiss the dialog once product deleted
            pDialog.dismiss();

            LinearLayout myLayout = (LinearLayout) findViewById(R.id.chat_container);
            View hiddenInfo = getLayoutInflater().inflate(R.layout.message_sent, myLayout, false);
            myLayout.addView(hiddenInfo);

            TextView msg_tv = (TextView) hiddenInfo.findViewById(R.id.from_msg);
            msg_tv.setText(data);

            EditText msg = (EditText) findViewById(R.id.msg);
            msg.clearFocus();
            msg.setText("");

            InputMethodManager imm = (InputMethodManager) getSystemService(
                    INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }

    }
}