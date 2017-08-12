package com.rosscradock.pfsmessager.listeners;

import android.content.Context;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Toast;

import com.rosscradock.pfsmessager.activities.NewUserActivity;
import com.rosscradock.pfsmessager.encrypt.AES;
import com.rosscradock.pfsmessager.encrypt.HMAC;
import com.rosscradock.pfsmessager.encrypt.KeyDerivation;
import com.rosscradock.pfsmessager.encrypt.KeyService;
import com.rosscradock.pfsmessager.interfaces.TaskCompleted;
import com.rosscradock.pfsmessager.model.Contact;
import com.rosscradock.pfsmessager.model.Message;
import com.rosscradock.pfsmessager.model.User;
import com.rosscradock.pfsmessager.onlineServices.PostRequest;

import org.json.JSONException;
import org.json.JSONObject;

import io.realm.Realm;

public class SendMessageClickListener implements View.OnClickListener {

    private Context context;
    private String newMessage;
    private Contact contact;
    private Realm realm;

    public SendMessageClickListener(Context context, String newMessage, Contact contact){
        this.context = context;
        this.newMessage = newMessage;
        this.contact = contact;
        Realm.init(context);
        this.realm = Realm.getDefaultInstance();
    }

    @Override
    public void onClick(View v) {

        if(contact.isFirstMessage()) {
            // todo get online public key from contact
            // sharedsecret
            // save secret to contact

            PostRequest postRequest = new PostRequest(new TaskCompleted() {
                @Override
                public void onTaskCompleted(final String response) {

                    if(!response.contains("failed")){
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    String sharedSecret = KeyService.getSharedSecret(context, jsonObject.getString("publicKey"));
                                    contact.setSharedKey(sharedSecret);
                                    sendMessage();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }
            });

            JSONObject jsonObject = new JSONObject();
            try {
                String url = "/keys/getPublicKey";
                jsonObject.put("username", contact.getUsername());
                String data = jsonObject.toString();
                postRequest.execute(url, data);
            } catch (JSONException e) {
                Toast.makeText(context, "JSON Error sendmessageclicklistener", Toast.LENGTH_LONG).show();
            }

        }else{
            sendMessage();
        }

    }

    private void sendMessage(){

        // get user's own username
        User user = realm.where(User.class).findFirst();
        String ownUserName = user.getUsername();

        // check if need to send keys
        String senderPublicKey = "";
        String recipientPublicKey = "";

        // encrypt message
        AES.setKey(contact.getSharedKey());
        String encryptedMessage = AES.encrypt(newMessage.trim());

        // create message object
        final Message newMessageObject = new Message(ownUserName,
                contact.getUsername(),
                encryptedMessage,
                System.currentTimeMillis(),
                senderPublicKey,
                recipientPublicKey,
                false,
                false,
                contact.getHashedKeyCount());

        //send message to server
        PostRequest postRequest = new PostRequest(new TaskCompleted() {
            @Override
            public void onTaskCompleted(final String response) {
                if(!response.contains("hashed key count:")){
                    int index = response.indexOf("count:");
                    final int hashedKeyCount = Integer.parseInt(Character.toString(response.charAt(index + 6)));
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            String sharedKey = KeyDerivation.updateSharedKey(contact, hashedKeyCount);
                            contact.setSharedKey(sharedKey);
                            contact.setHashedKeyCount(hashedKeyCount);
                            sendMessage();
                        }
                    });
                }else{
                    // store this new message in database
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            realm.copyToRealm(newMessageObject);
                        }
                    });
                }
            }
        });

        String url = "/message/sendMessage";
        String data = messageToJson(newMessageObject);
        postRequest.execute(url, data);

    }

    private String messageToJson(Message message){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("sender", message.getSender());
            jsonObject.put("recipient", message.getRecipient());
            jsonObject.put("message", message.getMessage());
            jsonObject.put("timeStamp", message.getTimestamp());
            jsonObject.put("senderPublicKey", message.getSenderPublicKey());
            jsonObject.put("recipientPublicKey", message.getRecipientPublicKey());
            jsonObject.put("hashedKeyCount", message.getHashedKeyCount());
            return jsonObject.toString();
        } catch (JSONException e) {
            Toast.makeText(context, "JSON Error sendmessageclicklistener", Toast.LENGTH_LONG).show();
            return "";
        }
    }
}
