package com.example.cityfit.data.model;

import com.google.common.net.InetAddresses;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

import javax.annotation.Nullable;

public class Leaderboard {


    private String userId;
    @Nullable
    private Long seconds;
    private @ServerTimestamp Date timestamp;
//    @Nullable
//    private Weather weather;


   public Leaderboard(){ }
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getSeconds() {
        return seconds;
    }

    public void setSeconds(Long seconds) {
        this.seconds = seconds;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

//    public Weather getWeather() {
//        return weather;
//    }
//
//    @Nullable
//    public void setWeather(Weather weather) {
//        this.weather = weather;
//    }
}
