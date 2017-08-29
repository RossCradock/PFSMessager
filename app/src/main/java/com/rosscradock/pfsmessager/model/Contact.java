package com.rosscradock.pfsmessager.model;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;

public class Contact extends RealmObject{

    private String username;
    private String sharedKey;
    private String publicKey;
    private int hashedKeyCount;
    private boolean firstMessage;

    public Contact() {
    }

    public Contact(String username, String sharedKey,
                   int hashedKeyCount, boolean firstMessage) {
        this.username = username;
        this.sharedKey = sharedKey;
        this.hashedKeyCount = hashedKeyCount;
        this.firstMessage = firstMessage;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public int getHashedKeyCount() {
        return hashedKeyCount;
    }

    public void setHashedKeyCount(int hashedKeyCount) {
        this.hashedKeyCount = hashedKeyCount;

    }

    public boolean isFirstMessage() {
        return firstMessage;
    }

    public void setFirstMessage(boolean firstMessage) {
        this.firstMessage = firstMessage;
    }
}
