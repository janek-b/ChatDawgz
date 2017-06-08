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
    @BindView(R.id.messageRecipient) EditText mMessageRecipient;
    @BindView(R.id.newMessage) EditText mNewMessage;
    @BindView(R.id.sendMessageButton) ImageButton mSendMessageButton;
    @BindView(R.id.addRecipientButton) ImageButton mAddRecipientButton;
    @BindView(R.id.chatRecyclerView) RecyclerView mChatRecyclerView;
    private Unbinder unbinder;
    private FirebaseAuth mAuth;
    private FirebaseRecyclerAdapter mFirebaseAdapter;
    private DatabaseReference mChatMessagesRef;
    private DatabaseReference rootRef;

    private Chat chat;

    public static ChatFragment newInstance(@Nullable Chat chat) {
        ChatFragment chatFragment = new ChatFragment();
        if (chat != null) {
            Bundle args = new Bundle();
            args.putParcelable("chat", Parcels.wrap(chat));
            chatFragment.setArguments(args);
        }
        return chatFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().getParcelable("chat") != null) {
            chat = Parcels.unwrap(getArguments().getParcelable("chat"));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        unbinder = ButterKnife.bind(this, view);
        mAddRecipientButton.setOnClickListener(this);
        mSendMessageButton.setOnClickListener(this);
        updateVisibility();
        getActivity().findViewById(R.id.fab).setVisibility(View.GONE);
        rootRef = FirebaseDatabase.getInstance().getReference();
        mChatMessagesRef = rootRef.child(Constants.FIREBASE_MESSAGE_REF).child(chat.getId());
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
        if (v == mAddRecipientButton) {
            final String recipient = mMessageRecipient.getText().toString();
            Query query = rootRef.child(Constants.FIREBASE_USER_REF).orderByChild("name").equalTo(recipient);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists() && (dataSnapshot.getChildrenCount() == 1)) {
                        for (DataSnapshot user : dataSnapshot.getChildren()) {
                            addRecipient(user.getKey(), recipient);
                        }
                    }
                }
                @Override public void onCancelled(DatabaseError databaseError) {}
            });
        }
        if (v == mSendMessageButton) {
            String message = mNewMessage.getText().toString();
            Message newMessage = new Message(mAuth.getCurrentUser().getUid(), message);
            DatabaseReference messageRef = rootRef.child(Constants.FIREBASE_MESSAGE_REF).child(chat.getId()).push();
            newMessage.setId(messageRef.getKey());
            messageRef.setValue(newMessage);
            mNewMessage.setText("");
        }
    }

    public void updateVisibility() {
        if (chat.getUsers().size() < 2) {
            mNewMessage.setVisibility(View.GONE);
            mSendMessageButton.setVisibility(View.GONE);
            mMessageRecipient.setVisibility(View.VISIBLE);
            mAddRecipientButton.setVisibility(View.VISIBLE);
        } else {
            mNewMessage.setVisibility(View.VISIBLE);
            mSendMessageButton.setVisibility(View.VISIBLE);
            mMessageRecipient.setVisibility(View.GONE);
            mAddRecipientButton.setVisibility(View.GONE);

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

    private void addRecipient(final String recipientKey, final String recipient) {
        chat.addUser(recipientKey);
        Map updates = new HashMap();
        updates.put(String.format("%s/%s/", Constants.FIREBASE_CHAT_REF, chat.getId()), chat);
        updates.put(String.format("%s/%s/chats/%s", Constants.FIREBASE_USER_REF, recipientKey, chat.getId()), true);
        rootRef.updateChildren(updates).addOnCompleteListener(getActivity(), new OnCompleteListener() {
            @Override public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    getActivity().setTitle(recipient);
                    updateVisibility();
                }
            }
        });
    }

}
