package com.rosscradock.pfsmessager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.rosscradock.pfsmessager.R;
import com.rosscradock.pfsmessager.interfaces.TaskCompleted;
import com.rosscradock.pfsmessager.model.Contact;
import com.rosscradock.pfsmessager.model.User;
import com.rosscradock.pfsmessager.onlineServices.KeyCheckOnline;
import com.rosscradock.pfsmessager.onlineServices.MapToJsonString;
import com.rosscradock.pfsmessager.onlineServices.PostRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;

public class NewUserActivity extends AppCompatActivity {

    /**
     * This activity deals with both the user registering with the service for the first time
     * and a user searching for other users so there is a switch to determine the what to do.
     * The response for both "/account/newUser" and "/account/checkUsername" can be
     * "username exists".
     */

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_user_activity);
        Realm.init(this);
        final Realm realm = Realm.getDefaultInstance();

        final String origin = getIntent().getStringExtra("origin");

        if(origin.equals("user")) {
            setTextToViews("Enter a New Account Name:");
        }else{
            setTextToViews("Enter New Contact Name:");
        }
        final EditText usernameEntry = (EditText)findViewById(R.id.new_user_username_entry);
        final TextView badEntry = (TextView)findViewById(R.id.new_user_bad_entry);

        findViewById(R.id.new_user_check_availability).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: need to check if online otherwise it thinks the username check was good
                // start new aync task to check username
                PostRequest postRequest = new PostRequest(new TaskCompleted() {
                    @Override
                    public void onTaskCompleted(final String response) {

                        boolean userNameInDatabase = response.contains("username exists");
                        boolean newUser = origin.equals("user");

                        // check the circumstance for switch
                        int scenario = 0;
                        scenario = (userNameInDatabase && !newUser) ? 1 :scenario;
                        scenario = (!userNameInDatabase && newUser) ? 2 :scenario;
                        scenario = (!userNameInDatabase && !newUser) ? 3 :scenario;

                        switch(scenario){
                            case 0:
                                // new user with username taken
                                badEntry.setText("Username Taken Try Another");
                                break;

                            case 1:
                                // new contact that exists in online database
                                final Contact contact = new Contact();
                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        try{
                                            // take username from json response
                                            JSONObject jsonObject = new JSONObject(response);
                                            contact.setUsername(jsonObject.getString("username"));
                                            contact.setPublicKey(jsonObject.getString("publicKey"));
                                            contact.setHashedKeyCount(0);

                                            Toast.makeText(NewUserActivity.this, "New Contact Added", Toast.LENGTH_LONG).show();

                                            startActivity(new Intent(NewUserActivity.this, MessageActivity.class));
                                            finish();
                                        } catch (JSONException e) {
                                            Toast.makeText(NewUserActivity.this, "JSON Error with response conversion to contact", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });

                                break;

                            case 2:
                                // new user with username not taken in data base
                                KeyCheckOnline.newUser(NewUserActivity.this, usernameEntry.getText().toString());

                                // find user in database and set the username
                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        User user = realm.where(User.class).findFirst();
                                        user.setUsername(usernameEntry.getText().toString());
                                    }
                                });

                                // goto to main screen
                                startActivity(new Intent(NewUserActivity.this, MainActivity.class));
                                finish();
                                break;

                            case 3:
                                // new contact is not in online database
                                badEntry.setText("Username Does Not Exist");
                                break;
                        }
                    }
                });

                try {
                    String url = (origin.equals("user")) ? "/account/newUser" : "/account/checkUsername";

                    HashMap<String, String> json = new HashMap<>();
                    json.put("username", usernameEntry.getText().toString());
                    String data = MapToJsonString.get(json);

                    postRequest.execute(url, data);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setTextToViews(String prompt){
        ((TextView) findViewById(R.id.new_user_prompt)).setText(prompt);
    }

    public void onPause(){
        super.onPause();
        PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("loggedin", false).apply();
    }
}
