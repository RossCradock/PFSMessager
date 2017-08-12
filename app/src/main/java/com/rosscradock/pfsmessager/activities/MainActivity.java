package com.rosscradock.pfsmessager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.rosscradock.pfsmessager.R;
import com.rosscradock.pfsmessager.arrayAdapters.ContactArrayAdapter;
import com.rosscradock.pfsmessager.encrypt.KeyService;
import com.rosscradock.pfsmessager.model.Contact;

import java.util.List;

import io.realm.Realm;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Realm.init(this);
        Realm realm = Realm.getDefaultInstance();

        // check if logged in on pin lock
        if(!PreferenceManager.getDefaultSharedPreferences(this).getBoolean("loggedin", false)){
            startActivity(new Intent(this, PinLockActivity.class));
            finish();
        }

        final List<Contact> contacts = realm.where(Contact.class).findAll();
        ListView contactsListView = (ListView)findViewById(R.id.contacts_listview);
        contactsListView.setEmptyView(findViewById(R.id.empty_contacts_listview));
        ContactArrayAdapter adapter = new ContactArrayAdapter(this, R.layout.contact_listview_item, contacts);
        contactsListView.setAdapter(adapter);

        contactsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, MessageActivity.class);
                intent.putExtra("contact", contacts.get(position).getUsername());
                startActivity(intent);
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // goto new contact screen
                Intent intent = new Intent(MainActivity.this, NewUserActivity.class);
                intent.putExtra("origin", "contact");
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    public void onPause(){
        super.onPause();
        PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("loggedin", false).apply();
    }
}
