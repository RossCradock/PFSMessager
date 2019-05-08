package com.rosscradock.pfsmessager.onlineServices;

import android.content.Context;
import android.widget.Toast;

import com.rosscradock.pfsmessager.encrypt.KeyService;
import com.rosscradock.pfsmessager.interfaces.TaskCompleted;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

public class KeyCheckOnline {

    public static void checkSpareKeys(final Context context, final String username){
        PostRequest postRequest = new PostRequest(new TaskCompleted() {
            @Override
            public void onTaskCompleted(String response) {

            try {
                JSONObject json = new JSONObject(response);
                int numberOfKeysNeeded = json.getInt("spareKeys");

                String[] publicKeys = {};
                for(int i = 0; i < numberOfKeysNeeded; i++){
                    String publicKey = KeyService.generateOnlineKeys(context);
                    if(publicKey.contains("failed")){
                        Toast.makeText(context, "Key to string failed in keyCheckOnline", Toast.LENGTH_LONG).show();
                        return;
                    }
                    publicKeys[i] = publicKey;
                }
                sendPublicKeysToDatabase(context, publicKeys, username);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            }
        });

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", username);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = "/keys/checkSpareKeys";
        String data = jsonObject.toString();
        postRequest.execute(url, data);
    }

    public static void newUser(final Context context, final String username){

        String[] publicKeys = new String[10];
        for(int i = 0; i < 10; i++){
            String publicKey = KeyService.generateOnlineKeys(context);
            if(publicKey.contains("failed")){
                Toast.makeText(context, "Key to string failed in keyCheckOnline", Toast.LENGTH_LONG).show();
            }
            publicKeys[i] = publicKey;
        }

        sendPublicKeysToDatabase(context, publicKeys, username);
    }

    private static void sendPublicKeysToDatabase(final Context context, String[] publicKeys, String username){
        PostRequest postRequest = new PostRequest(new TaskCompleted() {
            @Override
            public void onTaskCompleted(String response) {
                if(response.contains("failed")){
                    Toast.makeText(context, "Could Not Update Keys Online", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(context, "Keys updated online", Toast.LENGTH_LONG).show();
                }
            }
        });

        String data = null;
        try {
            HashMap<String, String> json = new HashMap<>();

            json.put("username", username);
            data = MapToJsonString.get(json);
            data = data + "&" + URLEncoder.encode("keys", "UTF-8") + "=" + MapToJsonString.getArray(publicKeys);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String url = "/keys/storeKeys";

        postRequest.execute(url, data);
    }
}
