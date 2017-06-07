package com.example.guest.chatdawgz.models;


import org.parceler.Parcel;

import java.util.HashMap;
import java.util.Map;

@Parcel
public class User {
    private String id;
    String name;
    String imageUrl;
    Map<String, Boolean> chats = new HashMap<>();

    public User() {}

    public User(String name, String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public void addChats(String chat) {
        this.chats.put(chat, true);
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Map<String, Boolean> getChats() {
        return chats;
    }
}
