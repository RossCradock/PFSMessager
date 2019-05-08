package com.rosscradock.pfsmessager.firbase;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.rosscradock.pfsmessager.activities.MainActivity;
import com.rosscradock.pfsmessager.encrypt.HMAC;
import com.rosscradock.pfsmessager.encrypt.KeyService;
import com.rosscradock.pfsmessager.model.Contact;
import com.rosscradock.pfsmessager.model.Message;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import io.realm.Realm;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private Message message;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d("*******", "Message data payload: " + remoteMessage.getData());
            handleNow(remoteMessage);
        }
    }

    private void handleNow(RemoteMessage remoteMessage) {
        Realm.init(this);
        Realm realm = Realm.getDefaultInstance();

        JSONObject jsonObject = (JSONObject)remoteMessage.getData();
        try {
            // create message object from json
            MessageDataHolder holder = new ObjectMapper().readValue(jsonObject.getString("data"),
                    MessageDataHolder.class);

            message = holder.message;
            message.setReceived(true);
            message.setRead(false);

            // check if new shared secret needs to be made
            if(!message.getSenderPublicKey().equals("")){
                // get shared key
                String sharedKey = KeyService.getSharedSecret(this, message.getRecipientPublicKey(),
                        message.getSenderPublicKey());

                // create contact
                final Contact contact = new Contact(message.getSender(), sharedKey, 0, false);

                // save to database
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.copyToRealm(contact);
                    }
                });
            }

            // check if the message is authenticated by hmac
            Contact contact = realm.where(Contact.class).equalTo("username", message.getSender()).findFirst();
            if(!message.getHmac().equals(HMAC.getHMAC(message.getMessage(), contact.getSharedKey()))){
                Toast.makeText(this, "Unauthenticated Message Received",Toast.LENGTH_LONG).show();
                return;
            }

            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.copyToRealm(message);
                }
            });
        }catch(JSONException | IOException e){
            e.printStackTrace();
        }

        sendNotification(remoteMessage.getData());
    }

    private void sendNotification(Map<String, String> messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle("New Message")
                .setContentText(messageBody.get("sender"))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }

    private static class MessageDataHolder{
        @JsonProperty("data")
        public Message message;
    }
}
