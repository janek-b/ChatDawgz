package com.example.guest.chatdawgz.ui;


import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.guest.chatdawgz.Constants;
import com.example.guest.chatdawgz.R;
import com.example.guest.chatdawgz.adapters.FirebaseMessageViewHolder;
import com.example.guest.chatdawgz.models.Chat;
import com.example.guest.chatdawgz.models.Message;
import com.example.guest.chatdawgz.models.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.parceler.Parcels;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ChatFragment extends Fragment implements View.OnClickListener {
    @BindView(R.id.newMessage) EditText mNewMessage;
    @BindView(R.id.sendMessageButton) ImageButton mSendMessageButton;
    @BindView(R.id.chatRecyclerView) RecyclerView mChatRecyclerView;
    private Unbinder unbinder;
    private FirebaseAuth mAuth;
    private FirebaseRecyclerAdapter mFirebaseAdapter;
    private DatabaseReference mChatMessagesRef;
    private DatabaseReference rootRef;

    private User user;
    private User recipient;
    private String chatId;

    public static ChatFragment newInstance(String chatId, User user, User recipient) {
        ChatFragment chatFragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putParcelable("user", Parcels.wrap(user));
        args.putParcelable("recipient", Parcels.wrap(recipient));
        args.putString("chatId", chatId);
        chatFragment.setArguments(args);
        return chatFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        user = Parcels.unwrap(args.getParcelable("user"));
        recipient = Parcels.unwrap(args.getParcelable("recipient"));
        chatId = args.getString("chatId");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        unbinder = ButterKnife.bind(this, view);
        mSendMessageButton.setOnClickListener(this);
        getActivity().findViewById(R.id.fab).setVisibility(View.GONE);
        getActivity().setTitle(recipient.getName());
        rootRef = FirebaseDatabase.getInstance().getReference();
        mChatMessagesRef = rootRef.child(Constants.FIREBASE_MESSAGE_REF).child(chatId);
        setUpFirebaseAdapter();

        mAuth = FirebaseAuth.getInstance();
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        mFirebaseAdapter.cleanup();
        getActivity().findViewById(R.id.fab).setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        if (v == mSendMessageButton) {
            String message = mNewMessage.getText().toString();
            Message newMessage = new Message(mAuth.getCurrentUser().getUid(), message);
            DatabaseReference messageRef = rootRef.child(Constants.FIREBASE_MESSAGE_REF).child(chatId).push();
            newMessage.setId(messageRef.getKey());
            messageRef.setValue(newMessage);
            mNewMessage.setText("");
        }
    }

    private void setUpFirebaseAdapter() {
        mFirebaseAdapter = new FirebaseRecyclerAdapter<Message, FirebaseMessageViewHolder>(Message.class,
                R.layout.message_item, FirebaseMessageViewHolder.class, mChatMessagesRef) {
            @Override
            protected void populateViewHolder(FirebaseMessageViewHolder viewHolder, Message model, int position) {
                viewHolder.bindMessage(model);
            }
        };
        mChatRecyclerView.setHasFixedSize(true);
        mChatRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mChatRecyclerView.setAdapter(mFirebaseAdapter);
    }

}
