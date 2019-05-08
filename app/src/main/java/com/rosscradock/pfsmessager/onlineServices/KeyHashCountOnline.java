package com.rosscradock.pfsmessager.onlineServices;

import android.content.Context;

import com.rosscradock.pfsmessager.encrypt.KeyDerivation;
import com.rosscradock.pfsmessager.interfaces.TaskCompleted;
import com.rosscradock.pfsmessager.model.Contact;
import com.rosscradock.pfsmessager.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import io.realm.Realm;

public class KeyHashCountOnline {

    public static void checkHashCount(final Context context, final Contact contact){
        Realm.init(context);
        final Realm realm = Realm.getDefaultInstance();

        final int currentHashCount = contact.getHashedKeyCount();

        PostRequest postRequest = new PostRequest(new TaskCompleted() {
            @Override
            public void onTaskCompleted(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    final int onlineHashCount = json.getInt("hashCount");
                    if(currentHashCount < onlineHashCount){
                        final String newSharedSecret = KeyDerivation.updateSharedKey(contact, onlineHashCount);
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                contact.setHashedKeyCount(onlineHashCount);
                                contact.setSharedKey(newSharedSecret);
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        // create the json
        JSONObject data = new JSONObject();
        User user = realm.where(User.class).findFirst();
        try {
            data.put("username1", contact.getUsername());
            data.put("username2", user.getUsername());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = "/keyHashCount/getHashCount";
        postRequest.execute(url, data.toString());
    }
}
