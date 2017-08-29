package com.rosscradock.pfsmessager.arrayAdapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.rosscradock.pfsmessager.R;
import com.rosscradock.pfsmessager.model.Contact;
import com.rosscradock.pfsmessager.model.Message;

import java.util.List;

import io.realm.Realm;

public class ContactArrayAdapter extends ArrayAdapter {

    private Context context;
    private int resource;
    private List<Contact> contacts;
    private Contact currentContact;

    public ContactArrayAdapter(Context context, int resource, List<Contact> contacts){
        super(context, resource, contacts);
        this.context = context;
        this.resource = resource;
        this.contacts = contacts;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {

        currentContact = contacts.get(position);

        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        convertView = inflater.inflate(resource, null);

        TextView contactName = (TextView)convertView.findViewById(R.id.contact_name);
        TextView newMessages = (TextView)convertView.findViewById(R.id.new_mesage_textview);

        contactName.setText(currentContact.getUsername());
        newMessages.setText(numberOfUnreadMessages());

        return convertView;
    }

    private String numberOfUnreadMessages(){
        Realm.init(context);
        Realm realm = Realm.getDefaultInstance();

        List<Message> messages = realm.where(Message.class)
                .equalTo("sender", currentContact.getUsername()).or()
                .equalTo("recipient", currentContact.getUsername())
                .findAll();
        int totalUnreadMessages = 0;
        for(Message message : messages){
            if(!message.isRead()){
                totalUnreadMessages++;
            }
        }
        switch (totalUnreadMessages){
            case 0:
                return "No New Messages";
            case 1:
                return "1 New Message";
            default:
                return totalUnreadMessages + " New Messages";
        }
    }
}
