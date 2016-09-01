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
 * Created by varungove on 5/1/16.
 */
public class Forum_list extends AppCompatActivity implements View.OnClickListener {

    JSONObject jsonObject;
    JSONParser jsonParser = new JSONParser();
    private ProgressDialog pDialog;

    private static final String GET_THREAD_URL = "http://web.engr.illinois.edu/~goverdh2/returnThread.php";
    String[] data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forum_list);

        new getThread().execute();

    }

    @Override
    public void onClick(View v) {
        TextView tid_tv = (TextView) v.findViewById(R.id.tid);
        String tid = tid_tv.getText().toString();

        TextView id_tv = (TextView) v.findViewById(R.id.id);
        String id = id_tv.getText().toString();

        TextView subject_tv = (TextView) v.findViewById(R.id.forum_subject);
        String subject = subject_tv.getText().toString();

        Intent intent = new Intent(Forum_list.this, Forum_thread.class);
        intent.putExtra("tid", tid);
        intent.putExtra("subject", subject);
        intent.putExtra("data", data[Integer.parseInt(id)]);
        startActivity(intent);
    }

    public void newThread (View view) {
        Intent intent = new Intent(Forum_list.this, New_thread.class);
        startActivity(intent);
    }

    class getThread extends AsyncTask<String, String, String> {

        //three methods get called, first preExecture, then do in background, and once do
        //in back ground is completed, the onPost execture method will be called.

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Forum_list.this);
            pDialog.setMessage("Getting the threads...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... args) {

            // TODO Auto-generated method stub
            // Check for success tag
            int success;

            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(Forum_list.this);
            String username = sp.getString("username", null);

            // Building Parameters
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("username", username));

            // getting product details by making HTTP request
            jsonObject = jsonParser.makeHttpRequest(GET_THREAD_URL, "POST", params);

            return null;
        }

        protected void onPostExecute(String string) {
            // dismiss the dialog once product deleted
            pDialog.dismiss();
            populateList();

        }

    }

    private void populateList() {
        JSONArray jsonArray = null;
        try {
            jsonArray = jsonObject.getJSONArray("message");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        data = new String[jsonArray.length()];

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject thread = null;
            try {
                thread = jsonArray.getJSONObject(i);

                LinearLayout myLayout = (LinearLayout) findViewById(R.id.forum_container);
                View hiddenInfo = getLayoutInflater().inflate(R.layout.forum_thread, myLayout, false);
                myLayout.addView(hiddenInfo);

                TextView subject = (TextView) hiddenInfo.findViewById(R.id.forum_subject);
                subject.setText(thread.getString("subject"));

                TextView data_tv = (TextView) hiddenInfo.findViewById(R.id.data);
                data_tv.setText(thread.getString("data"));
                data[i] = thread.getString("data");

                TextView tid = (TextView) hiddenInfo.findViewById(R.id.tid);
                tid.setText(thread.getString("tid"));

                TextView id_tv = (TextView) hiddenInfo.findViewById(R.id.id);
                id_tv.setText(""+i);

                hiddenInfo.setOnClickListener(this);
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }
}
