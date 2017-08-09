package com.rosscradock.pfsmessager.onlineServices;

import android.os.AsyncTask;

import com.rosscradock.pfsmessager.interfaces.TaskCompleted;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

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
            url = new URL("http://webservice.golfbirdie.ie/" + data[0]);
            client = (HttpURLConnection) url.openConnection();
            client.setRequestMethod("GET");
            try{
                client.addRequestProperty("apptoken", data[1]);
            } catch(ArrayIndexOutOfBoundsException ignored){}
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
                response = "Error occurred, status code: " + statusCode;
            }
        } catch(MalformedURLException mue){
            return "ERROR:MUE Malformed URL";
        } catch(IOException ioe) {
            return "ERROR:IOE Could Not Connect To Client";
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