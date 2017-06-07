package com.example.guest.chatdawgz.models;

import org.parceler.Parcel;

import java.util.HashMap;
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
}
