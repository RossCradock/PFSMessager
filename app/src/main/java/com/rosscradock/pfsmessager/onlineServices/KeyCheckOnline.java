package com.rosscradock.pfsmessager.onlineServices;

import android.content.Context;
import android.widget.Toast;

import com.rosscradock.pfsmessager.encrypt.KeyService;
import com.rosscradock.pfsmessager.interfaces.TaskCompleted;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class KeyCheckOnline {

    public static void checkSpareKeys(final Context context, final String username){
        PostRequest postRequest = new PostRequest(new TaskCompleted() {
            @Override
            public void onTaskCompleted(String response) {
                if(response.contains("requires")){
                    int index = response.indexOf("requires ");
                    int numberOfKeysNeeded = Integer.parseInt(Character.toString(response.charAt(index + 9)));

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

        String[] publicKeys = {};
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

        try {
            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray(publicKeys);
            jsonObject.put("username", username);
            jsonObject.put("keys", jsonArray);
            String url = "/keys/storeKeys";
            String data = jsonObject.toString();
            postRequest.execute(url, data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
