package com.rosscradock.pfsmessager.encrypt;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class HMAC {

    public static String getHMAC(String message, String key){

        Mac sha512_HMAC;

        try{
            byte [] byteKey = key.getBytes("UTF-8");
            final String HMAC_SHA256 = "HmacSHA512";
            sha512_HMAC = Mac.getInstance(HMAC_SHA256);
            SecretKeySpec keySpec = new SecretKeySpec(byteKey, HMAC_SHA256);
            sha512_HMAC.init(keySpec);
            byte [] mac_data = sha512_HMAC.doFinal(message.getBytes("UTF-8"));
            return bytesToHex(mac_data);
        } catch (UnsupportedEncodingException e) {
            return "error";
        } catch (NoSuchAlgorithmException e) {
            return "error";
        } catch (InvalidKeyException e) {
            return "error";
        }
    }

    private static String bytesToHex(byte[] bytes) {
        char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for(int i = 0; i < bytes.length; i++){
            int v = bytes[i] & 0xFF;
            hexChars[i * 2] = hexArray[v >>> 4];
            hexChars[i * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}