package com.example.guest.chatdawgz.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.guest.chatdawgz.Constants;
import com.example.guest.chatdawgz.R;
import com.example.guest.chatdawgz.adapters.FirebaseChatViewHolder;
import com.example.guest.chatdawgz.models.Chat;
import com.firebase.ui.database.FirebaseIndexRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatListFragment extends Fragment {
    @BindView(R.id.chatListRecyclerView) RecyclerView mChatListRecyclerView;
    private Unbinder unbinder;
    private FirebaseRecyclerAdapter mFirebaseAdapter;
    private DatabaseReference mUserChatKeyRef;
    private DatabaseReference mChatRef;
    private DatabaseReference rootRef;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;

    public ChatListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        rootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mUserChatKeyRef = rootRef.child(Constants.FIREBASE_USER_REF).child(currentUser.getUid()).child("chats");
        mChatRef = rootRef.child(Constants.FIREBASE_CHAT_REF);

        setUpFirebaseAdapter();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void setUpFirebaseAdapter() {
        mFirebaseAdapter = new FirebaseIndexRecyclerAdapter<Chat, FirebaseChatViewHolder>(Chat.class,
                R.layout.chat_list_item, FirebaseChatViewHolder.class, mUserChatKeyRef, mChatRef) {
            @Override
            protected void populateViewHolder(FirebaseChatViewHolder viewHolder, Chat model, int position) {
                viewHolder.bindChat(model);
            }
        };
        mChatListRecyclerView.setHasFixedSize(true);
        mChatListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mChatListRecyclerView.setAdapter(mFirebaseAdapter);
    }

}
