package com.example.guest.chatdawgz.ui;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.guest.chatdawgz.Constants;
import com.example.guest.chatdawgz.R;
import com.example.guest.chatdawgz.models.Chat;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreateChatFragment extends Fragment implements View.OnClickListener {
    @BindView(R.id.addRecipientButton) ImageButton mAddRecipientButton;
    @BindView(R.id.messageRecipient) EditText mMessageRecipient;
    private Unbinder unbinder;
    private DatabaseReference rootRef;
    private FirebaseAuth mAuth;

    public CreateChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_chat, container, false);
        unbinder = ButterKnife.bind(this, view);
        mAddRecipientButton.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
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
                            final List<String> recipientChatKeys = new ArrayList<>();
                            for (DataSnapshot chat : user.child("chats").getChildren()) {
                                recipientChatKeys.add(chat.getKey());
                            }
                            addRecipient(user.getKey(), recipient, recipientChatKeys);
                        }
                    }
                }
                @Override public void onCancelled(DatabaseError databaseError) {}
            });
        }
    }


    private void addRecipient(final String recipientKey, final String recipient, final List<String> recipientChatKeys) {
        final String userId = mAuth.getCurrentUser().getUid();
        final List<String> userChatKeys = new ArrayList<>();
        DatabaseReference userChatsRef = rootRef.child(Constants.FIREBASE_USER_REF).child(userId).child("chats");
        userChatsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot chat : dataSnapshot.getChildren()) {
                    userChatKeys.add(chat.getKey());
                }
                compareChats(userChatKeys, recipientChatKeys, recipientKey, recipient, userId);
            }

            @Override public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void compareChats(List<String> userChatKeys, List<String> recipientChatKeys, final String recipientKey, final String recipient, final String userId) {
        List<String> commonChats = new ArrayList<>(userChatKeys);
        commonChats.retainAll(recipientChatKeys);
        if (commonChats.size() > 0) {
            DatabaseReference existingChat = rootRef.child(Constants.FIREBASE_CHAT_REF).child(commonChats.get(0));
            existingChat.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override public void onDataChange(DataSnapshot dataSnapshot) {
                    Chat thisChat = dataSnapshot.getValue(Chat.class);
                    ((MainActivity)getActivity()).loadFragment(ChatFragment.newInstance(thisChat));
                }
                @Override public void onCancelled(DatabaseError databaseError) {}
            });
        } else {
            String chatKey = rootRef.child(Constants.FIREBASE_CHAT_REF).push().getKey();
            final Chat newChat = new Chat();
            newChat.addUser(userId);
            newChat.addUser(recipientKey);
            newChat.setId(chatKey);
            Map updateValues = new HashMap();
            updateValues.put(String.format("%s/%s/", Constants.FIREBASE_CHAT_REF, chatKey), newChat);
            updateValues.put(String.format("%s/%s/chats/%s/", Constants.FIREBASE_USER_REF, userId, chatKey), true);
            updateValues.put(String.format("%s/%s/chats/%s/", Constants.FIREBASE_USER_REF, recipientKey, chatKey), true);
            rootRef.updateChildren(updateValues).addOnCompleteListener(getActivity(), new OnCompleteListener() {
                @Override public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        ((MainActivity)getActivity()).loadFragment(ChatFragment.newInstance(newChat));
                        getActivity().setTitle(recipient);
                    }
                }
            });
        }
    }

}
