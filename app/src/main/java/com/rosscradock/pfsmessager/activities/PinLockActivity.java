package com.rosscradock.pfsmessager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;
import com.rosscradock.pfsmessager.R;
import com.rosscradock.pfsmessager.model.User;

import io.realm.Realm;

public class PinLockActivity extends AppCompatActivity {

    private String pin;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pin_lock_activity);
        Realm.init(this);
        Realm realm = Realm.getDefaultInstance();

        // check if pin has been set
        try{
            pin = realm.where(User.class).findFirst().getPin();
        }catch (NullPointerException e){
            startActivity(new Intent(this, SetPinActivity.class));
            finish();
        }

        PinLockView pinLockView = (PinLockView) findViewById(R.id.pin_lock_view);
        IndicatorDots indicatorDots = (IndicatorDots) findViewById(R.id.indicator_dots);

        pinLockView.attachIndicatorDots(indicatorDots);
        pinLockView.setPinLockListener(new PinLockListener() {
            @Override
            public void onComplete(String pin) {

            }

            @Override
            public void onEmpty() {

            }

            @Override
            public void onPinChange(int pinLength, String intermediatePin) {
                Log.d("PIN********", "Pin changed, new length " + pinLength + " with intermediate pin " + intermediatePin);
            }
        });

        pinLockView.setPinLength(4);
        pinLockView.setTextColor(ContextCompat.getColor(this, R.color.white));

        indicatorDots.setIndicatorType(IndicatorDots.IndicatorType.FILL_WITH_ANIMATION);
    }

    public void onPause(){
        super.onPause();
        PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("loggedin", false).apply();
    }
}
