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
import com.example.guest.chatdawgz.models.User;
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
                            User recipientUser = user.getValue(User.class);
                            addRecipient(recipientUser);
                        }
                    }
                }
                @Override public void onCancelled(DatabaseError databaseError) {}
            });
        }
    }


    private void addRecipient(final User recipient) {
        final String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference userRef = rootRef.child(Constants.FIREBASE_USER_REF).child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                compareUserChats(user, recipient);
            }
            @Override public void onCancelled(DatabaseError databaseError) {}
        });
    }

    public void compareUserChats(final User user, final User recipient) {
        List<String> commonChats = new ArrayList<>(user.getChatKeys());
        commonChats.retainAll(recipient.getChatKeys());
        if (commonChats.size() > 0) {
            ((MainActivity)getActivity()).loadFragment(ChatFragment.newInstance(commonChats.get(0), user, recipient));
        } else {
            final String chatKey = rootRef.child(Constants.FIREBASE_CHAT_REF).push().getKey();
            final Chat newChat = new Chat();
            newChat.addUser(user.getId());
            newChat.addUser(recipient.getId());
            newChat.setId(chatKey);
            Map updateValues = new HashMap();
            updateValues.put(String.format("%s/%s/", Constants.FIREBASE_CHAT_REF, chatKey), newChat);
            updateValues.put(String.format("%s/%s/chats/%s/", Constants.FIREBASE_USER_REF, user.getId(), chatKey), true);
            updateValues.put(String.format("%s/%s/chats/%s/", Constants.FIREBASE_USER_REF, recipient.getId(), chatKey), true);
            rootRef.updateChildren(updateValues).addOnCompleteListener(getActivity(), new OnCompleteListener() {
                @Override public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        ((MainActivity)getActivity()).loadFragment(ChatFragment.newInstance(chatKey, user, recipient));
                    }
                }
            });
        }
    }

}
