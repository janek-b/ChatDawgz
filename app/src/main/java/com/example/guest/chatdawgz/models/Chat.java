package com.example.guest.chatdawgz.models;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Parcel
public class Chat {
    private String id;
    Map<String, Boolean> users = new HashMap<>();

    public Chat() {}

    public void addUser(String user) {
        this.users.put(user, true);
    }

    public Map<String, Boolean> getUsers() {
        return users;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getUserKeys() {
        List<String> chatKeys = new ArrayList<>(users.keySet());
        return chatKeys;
//        return new ArrayList<>(chats.keySet());
    }
}
