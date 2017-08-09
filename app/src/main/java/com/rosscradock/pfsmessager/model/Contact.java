package com.rosscradock.pfsmessager.model;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;

public class Contact extends RealmObject{

    private int id;
    private String username;
    private RealmList<Message> messageList;
    private String sharedKey;
    private String publicKey;

    public Contact() {
    }

    public Contact(int id, String username, RealmList<Message> messageList, String sharedKey) {
        this.id = id;
        this.username = username;
        this.messageList = messageList;
        this.sharedKey = sharedKey;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public RealmList<Message> getMessageList() {
        return messageList;
    }

    public void setMessageList(RealmList<Message> messageList) {
        this.messageList = messageList;
    }

    public String getSharedKey() {
        return sharedKey;
    }

    public void setSharedKey(String sharedKey) {
        this.sharedKey = sharedKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }
}
