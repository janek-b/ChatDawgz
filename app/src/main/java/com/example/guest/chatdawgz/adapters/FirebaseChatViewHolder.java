package com.example.guest.chatdawgz.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.guest.chatdawgz.R;
import com.example.guest.chatdawgz.models.Chat;
import com.example.guest.chatdawgz.models.User;
import com.example.guest.chatdawgz.ui.ChatFragment;
import com.example.guest.chatdawgz.ui.MainActivity;
import com.squareup.picasso.Picasso;

/**
 * Created by Guest on 6/8/17.
 */

public class FirebaseChatViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private static final int MAX_WIDTH = 150;
    private static final int MAX_HEIGHT = 150;

    View mView;
    Context mContext;
    Chat mChat;
    User mUser;
    User mRecipient;

    public FirebaseChatViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
        mContext = itemView.getContext();
        itemView.setOnClickListener(this);
    }

    public void bindChat(Chat chat, User user, User recipient) {
        mChat = chat;
        mUser = user;
        mRecipient = recipient;
        TextView chatTitle = (TextView) mView.findViewById(R.id.chatTitleTextView);
        ImageView chatImageView = (ImageView) mView.findViewById(R.id.chatPartnerImage);
        Picasso.with(mContext).load(mRecipient.getImageUrl()).resize(MAX_WIDTH, MAX_HEIGHT).centerCrop().into(chatImageView);
        chatTitle.setText(mRecipient.getName());
    }

    @Override
    public void onClick(View v) {
        ((MainActivity)mContext).loadFragment(ChatFragment.newInstance(mChat.getId(), mUser, mRecipient));
    }
}
