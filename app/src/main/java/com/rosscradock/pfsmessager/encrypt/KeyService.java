package com.rosscradock.pfsmessager.encrypt;

import android.content.Context;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.rosscradock.pfsmessager.model.CachedKeyPair;

import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

import javax.crypto.KeyAgreement;

import io.realm.Realm;

public class KeyService{

    public static void generateKeys(Context context){

        Realm.init(context);
        Realm realm = Realm.getDefaultInstance();

        KeyPairGenerator keyPairGenerator = null;
        try {
            keyPairGenerator = KeyPairGenerator.getInstance("EC");
        } catch (NoSuchAlgorithmException e) {
            Toast.makeText(context, "Key Generation Failed", Toast.LENGTH_LONG).show();
        }

        keyPairGenerator.initialize(256);

        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();

        try {
            final CachedKeyPair cachedKeyPair = new CachedKeyPair(publicKeyToString(publicKey), privateKeyToString(privateKey));
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.copyToRealm(cachedKeyPair);
                }
            });
        } catch (GeneralSecurityException e) {
            Toast.makeText(context, "key to string operation failed", Toast.LENGTH_LONG).show();
        }
    }

    public static byte[] getSharedSecret(Context context, CachedKeyPair cachedKeyPair, String otherPublicKeyString){

        // use else where
        //CachedKeyPair cachedKeyPair = realm.where(CachedKeyPair.class).equalTo("publicKey", ownPublicKeyString).findFirst();

        KeyAgreement keyAgreement = null;
        try {
            keyAgreement = KeyAgreement.getInstance("ECDH");

            try {
                keyAgreement.init(stringToPrivateKey(cachedKeyPair.getPrivateKey()));
                keyAgreement.doPhase(stringToPublicKey(otherPublicKeyString), true);
            } catch (InvalidKeyException e) {
                Toast.makeText(context, "invalid key for key agreement", Toast.LENGTH_LONG).show();
            } catch (GeneralSecurityException e) {
                Toast.makeText(context, "string to key failed", Toast.LENGTH_LONG).show();
            }

            return keyAgreement.generateSecret();
            //Log.e("Shared secret: ", new String(sharedSecret, StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            Toast.makeText(context, "elliptic curve algorithm failed", Toast.LENGTH_LONG).show();
            return null;
        }
    }

    private static PrivateKey stringToPrivateKey(String privateKeyString) throws GeneralSecurityException {
        byte[] privateKeyStringBytes = Base64.decode(privateKeyString, Base64.DEFAULT);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyStringBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

        Arrays.fill(privateKeyStringBytes, (byte) 0);
        return privateKey;
    }


    private static PublicKey stringToPublicKey(String stored) throws GeneralSecurityException {
        byte[] data = Base64.decode(stored, Base64.DEFAULT);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(data);
        KeyFactory fact = KeyFactory.getInstance("EC");
        return fact.generatePublic(spec);
    }

    private static String privateKeyToString(PrivateKey privateKey) throws GeneralSecurityException {
        KeyFactory fact = KeyFactory.getInstance("EC");
        PKCS8EncodedKeySpec spec = fact.getKeySpec(privateKey, PKCS8EncodedKeySpec.class);
        byte[] packed = spec.getEncoded();
        String privateKeyString = Base64.encodeToString(packed, Base64.DEFAULT);

        Arrays.fill(packed, (byte) 0);
        return privateKeyString;
    }


    private static String publicKeyToString(PublicKey publicKey) throws GeneralSecurityException {
        KeyFactory fact = KeyFactory.getInstance("EC");
        X509EncodedKeySpec spec = fact.getKeySpec(publicKey, X509EncodedKeySpec.class);
        return Base64.encodeToString(spec.getEncoded(), Base64.DEFAULT);
    }

}
