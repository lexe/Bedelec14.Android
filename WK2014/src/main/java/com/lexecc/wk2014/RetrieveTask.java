package com.lexecc.wk2014;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class RetrieveTask extends AsyncTask<String[], Void, String> {

    public RetrieveTaskResponse delegate = null;

    protected String doInBackground(String[]... parameters) {
        InputStream is = null;

        // get response
        try {
            // data to send
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("user_name", parameters[0][1]));
            nameValuePairs.add(new BasicNameValuePair("password", parameters[0][2]));
            for (int i = 3; i < parameters[0].length; i++) {
                String[] split = parameters[0][i].split(";");
                nameValuePairs.add(new BasicNameValuePair(split[0], split[1]));
            }

            // execute
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(parameters[0][0]);
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpclient.execute(httppost);
            is = response.getEntity().getContent();
        } catch (Exception e) {
            Log.e("WK2014", "Error in http connection: " + e.toString());
        }

        // get data
        try {
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
            sb.append(reader.readLine() + "\n");

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            return sb.toString();

        } catch (Exception e) {
            Log.e("WK2014", "Error converting result: " + e.toString());
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        delegate.dataReceived(result);
    }
}