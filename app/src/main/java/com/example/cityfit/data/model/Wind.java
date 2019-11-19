package com.example.cityfit.data.model;

import java.io.Serializable;

public class Wind implements Serializable {
    public Wind() {}
    private float speed;
    private float deg;
    public float getSpeed() {
        return speed;
    }
    public void setSpeed(float speed) {
        this.speed = speed;
    }
    public float getDeg() {
        return deg;
    }
    public void setDeg(float deg) {
        this.deg = deg;
    }


}