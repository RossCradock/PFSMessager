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
import java.util.HashMap;

import io.realm.Realm;

public class NewUserActivity extends AppCompatActivity {

    /**
     * This activity deals with both the user registering with the service for the first time
     * and a user searching for other users so there is a switch to determine the what to do.
     * The response for both "/account/newUser" and "/account/checkUsername" can be
     * "username exists".
     */

    private boolean newActivityStarted = false;

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
        final EditText usernameEntry = findViewById(R.id.new_user_username_entry);
        final TextView badEntry = findViewById(R.id.new_user_bad_entry);

        /*
        Possible responses from backend:
        newUser - {response: success}
                - {response: username exists}

        checkUsername   - {response: username exists... [username, key, token]}
                        - {response: failed}

         */
        findViewById(R.id.new_user_check_availability).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO need to check if username already in local database
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

                    //check that the server was actually reached
                    scenario = (response.contains("Unable to resolve host") ||
                            response.contains("HTTP Status")) ? 4: scenario;

                    switch(scenario){
                        case 0:
                            // new user with username taken1
                            badEntry.setText("Username Taken Try Another");
                            break;
                        case 1:
                            // new contact that exists in online database

                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                try{
                                    // take username from json response
                                    JSONObject jsonObject = new JSONObject(response);

                                    Contact contact = new Contact();
                                    contact.setUsername(jsonObject.getString("username"));
                                    contact.setPublicKey(jsonObject.getString("publicKey"));
                                    contact.setHashedKeyCount(0);
                                    realm.copyToRealm(contact);

                                    Toast.makeText(NewUserActivity.this, "New Contact Added", Toast.LENGTH_LONG).show();

                                    // open Message Activity
                                    Intent intent = new Intent(NewUserActivity.this, MessageActivity.class);
                                    intent.putExtra("contact", contact.getUsername());
                                    startActivity(intent);
                                    newActivityStarted = true;
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
                            newActivityStarted = true;
                            finish();
                            break;

                        case 3:
                            // new contact is not in online database
                            badEntry.setText("Username Does Not Exist");
                            break;
                        case 4:
                            badEntry.setText("Failed to reach server or an error occurred. Please try again.");
                    }
                    }
                });

                try {
                    boolean user = origin.equals("user");
                    String url;
                    HashMap<String, String> json = new HashMap<>();
                    String enteredUsername = usernameEntry.getText().toString();

                    if(user){
                        url = "/account/newUser";
                        //TODO: put token in with the json data as "username2"
                        // placeholder token for now
                        json.put("username2", "5af4");
                        json.put("username1", enteredUsername);
                    } else{
                        url = "/account/checkUsername";
                        String ownerUsername = realm.where(User.class).findFirst().getUsername();
                        json.put("username2", enteredUsername);
                        json.put("username1", ownerUsername);
                    }

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
        if(!newActivityStarted) {
            PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("loggedin", false).apply();
        }
    }
}