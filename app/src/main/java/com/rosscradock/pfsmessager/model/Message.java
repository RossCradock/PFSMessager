package com.rosscradock.pfsmessager.model;

import io.realm.RealmObject;

public class Message extends RealmObject{

    private String sender;
    private String recipient;
    private String message;
    private long timestamp;
    private String hmac;
    private String SenderPublicKey;
    private String recipientPublicKey;
    private boolean read;
    private boolean received;

    public Message(){

    }

    public Message(String sender, String recipient, String message, long timestamp, String hmac,
                   String senderPublicKey, String recipientPublicKey, boolean read, boolean received) {
        this.sender = sender;
        this.recipient = recipient;
        this.message = message;
        this.timestamp = timestamp;
        this.hmac = hmac;
        SenderPublicKey = senderPublicKey;
        this.recipientPublicKey = recipientPublicKey;
        this.read = read;
        this.received = received;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getHmac() {
        return hmac;
    }

    public void setHmac(String hmac) {
        this.hmac = hmac;
    }

    public String getSenderPublicKey() {
        return SenderPublicKey;
    }

    public void setSenderPublicKey(String senderPublicKey) {
        SenderPublicKey = senderPublicKey;
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
}
