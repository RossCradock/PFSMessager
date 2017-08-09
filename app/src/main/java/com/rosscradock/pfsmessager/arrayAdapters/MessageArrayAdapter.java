package com.rosscradock.pfsmessager.arrayAdapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.rosscradock.pfsmessager.R;
import com.rosscradock.pfsmessager.model.Message;

import java.util.List;

public class MessageArrayAdapter extends ArrayAdapter {

    private Context context;
    private int resource;
    private List<Message> messages;

    public MessageArrayAdapter(Context context, int resource, List<Message> messages) {
        super(context, resource, messages);
        this.context = context;
        this.resource = resource;
        this.messages = messages;
    }

    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {

        Message currentMessage = messages.get(position);

        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        if(currentMessage.isReceived()) {
            convertView = inflater.inflate(R.layout.contact_message_listview_item, parent, false);
        }else{
            convertView = inflater.inflate(R.layout.user_message_listview_item, parent, false);
        }

        TextView messageTextview = (TextView)convertView.findViewById(R.id.message_textview);
        messageTextview.setText(currentMessage.getMessage());

        return convertView;
    }
}
