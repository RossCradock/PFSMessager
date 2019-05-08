package com.rosscradock.pfsmessager.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.rosscradock.pfsmessager.R;
import com.rosscradock.pfsmessager.model.User;

import io.realm.Realm;

public class SetPinActivity extends AppCompatActivity {

    private Realm realm;
    private boolean newActivityStarted = false;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_pin_activity);
        Realm.init(this);
        realm = Realm.getDefaultInstance();

        EditText pinText = findViewById(R.id.pinText);
        pinText.requestFocus();

        pinText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // once 4 digits are entered bring up the dialog
                if(s.length() == 4){
                    getVerifyDialog(s.toString()).show();
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private AlertDialog.Builder getVerifyDialog(final String pin){
        AlertDialog.Builder verifyPinDialogBuilder = new AlertDialog.Builder(SetPinActivity.this);
        verifyPinDialogBuilder.setMessage("Set Pin As: " + pin);

        verifyPinDialogBuilder.setPositiveButton("Set", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        User user = new User();
                        user.setPin(pin);
                        PreferenceManager.getDefaultSharedPreferences(SetPinActivity.this)
                                .edit()
                                .putBoolean("loggedin", true)
                                .apply();
                        realm.copyToRealm(user);

                        Intent intent = new Intent(SetPinActivity.this, NewUserActivity.class);
                        intent.putExtra("origin", "user");
                        startActivity(intent);
                        newActivityStarted = true;
                        finish();
                    }
                });
            }
        });

        verifyPinDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                newActivityStarted = true;
                finish();
            }
        });

        return verifyPinDialogBuilder;
    }

    public void onPause(){
        super.onPause();
        if(!newActivityStarted) {
            PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("loggedin", false).apply();
        }
    }
}
