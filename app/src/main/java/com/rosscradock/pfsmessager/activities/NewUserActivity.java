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
import com.rosscradock.pfsmessager.onlineServices.PostRequest;

import org.json.JSONException;
import org.json.JSONObject;

import io.realm.Realm;

public class NewUserActivity extends AppCompatActivity {

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

                // start new aync task to check username
                PostRequest postRequest = new PostRequest(new TaskCompleted() {
                    @Override
                    public void onTaskCompleted(final String response) {

                        if(response.contains("username exists")){
                            if(origin.equals("user")){
                                badEntry.setText("Username Taken Try Another");
                            }else{

                                // for new contact
                                final Contact contact = new Contact();
                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        try{
                                            // take username from json response
                                            JSONObject jsonObject = new JSONObject(response);
                                            contact.setUsername(jsonObject.getString("username"));
                                        } catch (JSONException e) {
                                            Toast.makeText(NewUserActivity.this, "JSON Error with response conversion to contact", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                                Intent intent =new Intent(NewUserActivity.this, MessageActivity.class);
                                intent.putExtra("contact", contact.getUsername());
                                startActivity(intent);
                            }
                        }

                        if(response.contains("username does not exist")){
                            if(origin.equals("user")) {

                                // store in database
                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        User user = realm.where(User.class).findFirst();
                                        user.setUsername(usernameEntry.getText().toString());
                                    }
                                });
                                KeyCheckOnline.newUser(usernameEntry.getText().toString());
                                // goto to main screen
                                startActivity(new Intent(NewUserActivity.this, MainActivity.class));
                            } else{
                                badEntry.setText("Username Does Not Exist");
                            }
                        }
                    }
                });

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("username", usernameEntry.getText().toString());
                } catch (JSONException e) {
                    Toast.makeText(NewUserActivity.this, "JSON Error with username", Toast.LENGTH_LONG).show();
                }
                String url = "/account/checkUsername";
                String data = jsonObject.toString();

                postRequest.execute(url, data);
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
