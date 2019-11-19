package com.example.cityfit.data.model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class User {


    private String name;
    private String email;
    private String rank;
    private @ServerTimestamp Date timestamp;

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public User()
    { }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
