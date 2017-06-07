package com.example.guest.chatdawgz.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.guest.chatdawgz.R;
import com.example.guest.chatdawgz.models.Message;

/**
 * Created by Guest on 6/7/17.
 */

public class FirebaseMessageViewHolder extends RecyclerView.ViewHolder {

    View mView;
    Context mContext;

    public FirebaseMessageViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
        mContext = itemView.getContext();
    }

    public void bindMessage(Message message) {
        TextView messageBodyTextView = (TextView) mView.findViewById(R.id.messageBody);

        messageBodyTextView.setText(message.getBody());
    }

}
