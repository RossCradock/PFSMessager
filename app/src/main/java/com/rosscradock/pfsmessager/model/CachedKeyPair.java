package com.rosscradock.pfsmessager.model;

import java.security.KeyPair;

import io.realm.RealmObject;

public class CachedKeyPair extends RealmObject{

    private String publicKey;
    private String privateKey;

    public CachedKeyPair(){

    }

    public CachedKeyPair(String publicKey, String privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }
}
