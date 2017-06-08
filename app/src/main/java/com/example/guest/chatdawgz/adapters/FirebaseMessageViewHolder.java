package com.example.guest.chatdawgz.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import com.example.guest.chatdawgz.R;
import com.example.guest.chatdawgz.models.Message;
import com.example.guest.chatdawgz.models.User;
import com.squareup.picasso.Picasso;

public class FirebaseMessageViewHolder extends RecyclerView.ViewHolder {
    private static final int MAX_WIDTH = 100;
    private static final int MAX_HEIGHT = 100;

    View mView;
    Context mContext;

    public FirebaseMessageViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
        mContext = itemView.getContext();
    }

    public void bindMessage(Message message, User recipient, boolean sender) {
        TextView messageBodyTextView = (TextView) mView.findViewById(R.id.messageBody);
        LinearLayout messageLayout = (LinearLayout) mView.findViewById(R.id.chat_message_layout);
        CardView messageCard = (CardView) mView.findViewById(R.id.chat_message_card);
        ImageView recipientImage = (ImageView) mView.findViewById(R.id.recipientImage);
        Space recipientSpace = (Space) mView.findViewById(R.id.recipient_space);
        Space userSpace = (Space) mView.findViewById(R.id.user_space);
        if (!message.getSender().equals(recipient.getId())) {
            messageLayout.setGravity(Gravity.END);
            recipientSpace.setVisibility(View.GONE);
            userSpace.setVisibility(View.VISIBLE);
            recipientImage.setVisibility(View.GONE);
            messageCard.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.colorAccent));
        } else {
            messageLayout.setGravity(Gravity.START);
            recipientSpace.setVisibility(View.VISIBLE);
            userSpace.setVisibility(View.GONE);
            recipientImage.setVisibility(View.VISIBLE);
            Picasso.with(mContext).load(recipient.getImageUrl()).resize(MAX_WIDTH, MAX_HEIGHT).centerCrop().into(recipientImage);
        }

        messageBodyTextView.setText(message.getBody());
    }

}
