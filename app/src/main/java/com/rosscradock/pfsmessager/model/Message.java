package com.rosscradock.pfsmessager.model;

import io.realm.RealmObject;

public class Message extends RealmObject{

    private String sender;
    private String recipient;
    private String message;
    private long timestamp;
    private String senderPublicKey;
    private String recipientPublicKey;
    private boolean read;
    private boolean received;
    private int hashedKeyCount;

    public Message(){

    }

    public Message(String sender, String recipient, String message, long timestamp, String senderPublicKey,
                   String recipientPublicKey, boolean read, boolean received, int hashedKeyCount) {
        this.sender = sender;
        this.recipient = recipient;
        this.message = message;
        this.timestamp = timestamp;
        this.senderPublicKey = senderPublicKey;
        this.recipientPublicKey = recipientPublicKey;
        this.read = read;
        this.received = received;
        this.hashedKeyCount = hashedKeyCount;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getSenderPublicKey() {
        return senderPublicKey;
    }

    public void setSenderPublicKey(String senderPublicKey) {
        senderPublicKey = senderPublicKey;
    }

    public String getRecipientPublicKey() {
        return recipientPublicKey;
    }

    public void setRecipientPublicKey(String recipientPublicKey) {
        this.recipientPublicKey = recipientPublicKey;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public boolean isReceived() {
        return received;
    }

    public void setReceived(boolean received) {
        this.received = received;
    }

    public int getHashedKeyCount() {
        return hashedKeyCount;
    }

    public void setHashedKeyCount(int hashedKeyCount) {
        this.hashedKeyCount = hashedKeyCount;
    }
}
