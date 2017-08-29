package com.rosscradock.pfsmessager.onlineServices;

import android.os.AsyncTask;
import android.util.Log;

import com.rosscradock.pfsmessager.interfaces.TaskCompleted;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class PostRequest extends AsyncTask<String, Void, String> {

    private TaskCompleted listener;

    public PostRequest(TaskCompleted listener){
        this.listener = listener;
    }

    // data[0] = url
    // data[1] = data
    @Override
    protected String doInBackground(String... data){

        // connect to the server
        URL url;
        HttpURLConnection client = null;
        String response;
        try {
            url = new URL("http://pfsmessager.ddns.net" + data[0]);
            client = (HttpURLConnection) url.openConnection();
            client.setRequestMethod("POST");
            //client.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            //client.setRequestProperty("Accept","application/json");
            client.setDoOutput(true);
            client.setDoInput(true);

            OutputStream outputStream = client.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            writer.write(data[1]);
            Log.e("*****DATA*****", data[1]);
            writer.flush();
            writer.close();

            int statusCode = client.getResponseCode();
            if(statusCode == 200){
                InputStream inputStream = new BufferedInputStream(client.getInputStream());
                InputStreamReader reader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(reader);
                StringBuilder stringBuilder = new StringBuilder();
                String chunks;
                while((chunks = bufferedReader.readLine()) != null){
                    stringBuilder.append(chunks);
                }
                response = stringBuilder.toString();
            } else{
                InputStream errorStream = new BufferedInputStream(client.getErrorStream());
                InputStreamReader reader = new InputStreamReader(errorStream);
                BufferedReader bufferedReader = new BufferedReader(reader);
                StringBuilder builder = new StringBuilder();
                String chunks;
                while((chunks = bufferedReader.readLine()) != null){
                    builder.append(chunks);
                }
                response = builder.toString();
            }
        } catch(MalformedURLException mue){
            return "ERROR:MUE Malformed URL";
        } catch(IOException ioe) {
            return ioe.getMessage();
        } finally{
            if(client != null)client.disconnect();
        }
        return response;
    }

    @Override
    protected void onPostExecute(String response){
        listener.onTaskCompleted(response);
    }

    @Override
    protected void onPreExecute() {}

    @Override
    protected void onProgressUpdate(Void... values) {}
}