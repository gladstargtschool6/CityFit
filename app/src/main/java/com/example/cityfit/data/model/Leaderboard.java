package com.example.cityfit.data.model;

import com.google.common.net.InetAddresses;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

import javax.annotation.Nullable;

public class Leaderboard {


    private String userId;
    @Nullable
    private Long seconds;
    private Long walking;
    private Long onFoot;
    private Long driving;
    private Long cycling;
    private Long still;
    private Long tilting;
    private Long unknown;
    private Long running;



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

    @Nullable
    public Long getSeconds() {
        return seconds;
    }

    public void setSeconds(@Nullable Long seconds) {
        this.seconds = seconds;
    }

    public Long getRunning() {
        return running;
    }

    public void setRunning(Long running) {
        this.running = running;
    }

    public Long getWalking() {
        return walking;
    }

    public void setWalking(Long walking) {
        this.walking = walking;
    }

    public Long getOnFoot() {
        return onFoot;
    }

    public void setOnFoot(Long onFoot) {
        this.onFoot = onFoot;
    }

    public Long getDriving() {
        return driving;
    }

    public void setDriving(Long driving) {
        this.driving = driving;
    }

    public Long getCycling() {
        return cycling;
    }

    public void setCycling(Long cycling) {
        this.cycling = cycling;
    }

    public Long getStill() {
        return still;
    }

    public void setStill(Long still) {
        this.still = still;
    }

    public Long getTilting() {
        return tilting;
    }

    public void setTilting(Long tilting) {
        this.tilting = tilting;
    }

    public Long getUnknown() {
        return unknown;
    }

    public void setUnknown(Long unknown) {
        this.unknown = unknown;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
