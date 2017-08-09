package com.rosscradock.pfsmessager.activities;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.rosscradock.pfsmessager.R;
import com.rosscradock.pfsmessager.arrayAdapters.MessageArrayAdapter;
import com.rosscradock.pfsmessager.listeners.SendMessageClickListener;
import com.rosscradock.pfsmessager.model.Contact;
import com.rosscradock.pfsmessager.model.Message;

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
        Contact contact = realm.where(Contact.class).equalTo("username", contactUsername).findFirst();
        ((TextView)findViewById(R.id.contact_name_message_textview)).setText(contact.getUsername());

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
    }
}
