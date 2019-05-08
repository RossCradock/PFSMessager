package com.rosscradock.pfsmessager.activities;

import android.content.Context;
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
import io.realm.RealmResults;

public class PinLockActivity extends AppCompatActivity {

    private boolean newActivityStarted = false;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pin_lock_activity);
        Realm.init(this);
        final Realm realm = Realm.getDefaultInstance();
        final Context context = this;

        // check if pin has been set
        if(null == realm.where(User.class).findFirst()){
            startActivity(new Intent(this, SetPinActivity.class));
            newActivityStarted = true;
            finish();
        }

        PinLockView pinLockView = findViewById(R.id.pin_lock_view);
        IndicatorDots indicatorDots = findViewById(R.id.indicator_dots);

        pinLockView.attachIndicatorDots(indicatorDots);
        pinLockView.setPinLockListener(new PinLockListener() {
            @Override
            public void onComplete(String pin) {
                RealmResults<User> users = realm.where(User.class).findAll();
                for(User user : users) {
                    if (user.getPin().equals(pin)) {
                        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("loggedin", true).apply();
                        startActivity(new Intent(PinLockActivity.this, MainActivity.class));
                        newActivityStarted = true;
                        finish();
                    }
                }
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
        if(!newActivityStarted) {
            PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("loggedin", false).apply();
        }
    }
}
