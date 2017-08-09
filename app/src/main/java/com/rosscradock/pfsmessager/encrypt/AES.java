package com.rosscradock.pfsmessager.encrypt;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AES {

    private static SecretKeySpec secretKey ;
    private static byte[] key ;

    public static void setKey(String myKey){
        MessageDigest sha = null;
        try {
            key = myKey.getBytes("UTF-8");
            sha = MessageDigest.getInstance("SHA-2");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16); // use only first 128 bit
            secretKey = new SecretKeySpec(key, "AES");
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
        }
    }

    public static String encrypt(String message){
        try{
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.encodeToString(cipher.doFinal(message.getBytes("UTF-8")), Base64.DEFAULT);
        } catch (Exception e){
            return "error";
        }
    }

    public static String decrypt(String message){
        try{
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String((cipher.doFinal(Base64.decode(message, Base64.DEFAULT))), "UTF-8");
        } catch (Exception e){
            return "error";
        }
    }
}
