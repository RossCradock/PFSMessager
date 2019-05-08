package com.rosscradock.pfsmessager.model;

import io.realm.RealmObject;

public class User extends RealmObject{

    private String pin;
    private String username;

    public User() {
    }

    public User(String pin, String username) {
        this.pin = pin;
        this.username = username;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
