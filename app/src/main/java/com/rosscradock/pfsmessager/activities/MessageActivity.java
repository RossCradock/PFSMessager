package com.rosscradock.pfsmessager.activities;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.rosscradock.pfsmessager.R;
import com.rosscradock.pfsmessager.arrayAdapters.MessageArrayAdapter;
import com.rosscradock.pfsmessager.interfaces.TaskCompleted;
import com.rosscradock.pfsmessager.listeners.SendMessageClickListener;
import com.rosscradock.pfsmessager.model.Contact;
import com.rosscradock.pfsmessager.model.Message;
import com.rosscradock.pfsmessager.model.User;
import com.rosscradock.pfsmessager.onlineServices.PostRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;

public class MessageActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_activity);

        Realm.init(this);
        final Realm realm = Realm.getDefaultInstance();

        // get contacts name and set to textview
        String contactUsername = getIntent().getStringExtra("contact");
        final Contact contact = realm.where(Contact.class).equalTo("username", contactUsername).findFirst();
        ((TextView)findViewById(R.id.contact_name_message_textview)).setText(contact.getUsername());

        //get message list, set the empty view and set adapter
        final RealmList<Message> messages = contact.getMessageList();
        ListView messageListView = (ListView)findViewById(R.id.messages_listview);
        messageListView.setEmptyView(findViewById(R.id.empty_message_listview));
        final MessageArrayAdapter adapter = new MessageArrayAdapter(this, R.layout.contact_message_listview_item, messages);
        messageListView.setAdapter(adapter);

        // delete message
        messageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Message deleteMessage = messages.get(position);
                        if(deleteMessage.isValid()){
                            deleteMessage.deleteFromRealm();
                            messages.remove(position);
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
            }
        });

        // send message
        TextView newMessageTextView = ((TextView)findViewById(R.id.new_message));
        (findViewById(R.id.send_message_imageview)).setOnClickListener(
                new SendMessageClickListener(
                        this, newMessageTextView.getText().toString(), contact
                ));

        // refresh keys
        Button refreshKeysButton = (Button)findViewById(R.id.change_keys_message_button);
        refreshKeysButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostRequest postRequest = new PostRequest(new TaskCompleted() {
                    @Override
                    public void onTaskCompleted(final String response) {
                        if(!response.contains("failed")){
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    contact.setHashedKeyCount(Integer.parseInt(response));
                                    Toast.makeText(MessageActivity.this, "Refreshed Keys", Toast.LENGTH_LONG).show();
                                }
                            });
                        }else{
                            Toast.makeText(MessageActivity.this, "Could Not Refresh Keys", Toast.LENGTH_LONG).show();
                        }
                    }
                });

                User user = realm.where(User.class).findFirst();
                JSONObject jsonObject = new JSONObject();
                try {
                    String url = "/keys/refreshKey";
                    jsonObject.put("username1", user.getUsername());
                    jsonObject.put("username2", contact.getUsername());
                    String data = jsonObject.toString();
                    postRequest.execute(url, data);
                } catch (JSONException e) {
                    Toast.makeText(MessageActivity.this, "JSON Error with username", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
