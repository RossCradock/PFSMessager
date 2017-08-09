package com.rosscradock.pfsmessager.listeners;

import android.content.Context;
import android.preference.PreferenceManager;
import android.view.View;

import com.rosscradock.pfsmessager.encrypt.AES;
import com.rosscradock.pfsmessager.encrypt.HMAC;
import com.rosscradock.pfsmessager.model.Contact;
import com.rosscradock.pfsmessager.model.Message;

import io.realm.Realm;

public class SendMessageClickListener implements View.OnClickListener {

    private Context context;
    private String newMessage;
    private Contact contact;

    public SendMessageClickListener(Context context, String newMessage, Contact contact){
        this.context = context;
        this.newMessage = newMessage;
        this.contact = contact;
    }

    @Override
    public void onClick(View v) {
        Realm.init(context);
        final Realm realm = Realm.getDefaultInstance();

        String senderPublicKey = "";
        String recipientPublicKey = "";

        // get user's own username
        String ownUserName = PreferenceManager.getDefaultSharedPreferences(context).getString("ownUserName", null);

        // get contact's name


        // encrypt message
        AES.setKey(contact.getSharedKey());
        String encryptedMessage = AES.encrypt(newMessage.trim());

        // create message object
        final Message newMessageObject = new Message(ownUserName,
                contact.getUsername(),
                encryptedMessage,
                System.currentTimeMillis(),
                HMAC.getHMAC(newMessage, contact.getSharedKey()),
                "",
                "",
                false,
                false);

        // store in database this new message
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealm(newMessageObject);
            }
        });

        // send to server
    }
}
