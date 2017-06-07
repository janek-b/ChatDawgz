package com.example.guest.chatdawgz.models;


import org.parceler.Parcel;

@Parcel
public class Message {
    private String id;
    String sender;
    String body;

    public Message() {}

    public Message(String sender, String body) {
        this.sender = sender;
        this.body = body;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getSender() {
        return sender;
    }

    public String getBody() {
        return body;
    }
}
