package com.rosscradock.pfsmessager.encrypt;

import com.rosscradock.pfsmessager.model.Contact;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class KeyDerivation {

    public static String updateSharedKey(Contact contact, int hashedKeyCount){

        String sharedKey = contact.getSharedKey();
        int difference = (hashedKeyCount - contact.getHashedKeyCount());
        for(int i = 0; i < difference; i++){
            try {
                MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
                sharedKey = new String(messageDigest.digest(sharedKey.getBytes(StandardCharsets.UTF_8)));
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }


        return sharedKey;
    }
}
