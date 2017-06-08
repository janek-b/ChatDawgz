package com.example.guest.chatdawgz.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.guest.chatdawgz.R;
import com.example.guest.chatdawgz.models.Chat;
import com.example.guest.chatdawgz.ui.ChatFragment;
import com.example.guest.chatdawgz.ui.MainActivity;

/**
 * Created by Guest on 6/8/17.
 */

public class FirebaseChatViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    View mView;
    Context mContext;
    Chat mChat;

    public FirebaseChatViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
        mContext = itemView.getContext();
        itemView.setOnClickListener(this);
    }

    public void bindChat(Chat chat) {
        mChat = chat;
        TextView chatTitle = (TextView) mView.findViewById(R.id.chatTitleTextView);
        chatTitle.setText(chat.getUsers().keySet().toArray()[0].toString());
    }

    @Override
    public void onClick(View v) {
        Log.d("chatViewHolder", "click");
        ((MainActivity)mContext).loadFragment(ChatFragment.newInstance(mChat));
    }
}
