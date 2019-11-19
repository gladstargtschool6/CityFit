package com.example.cityfit.data.model;
import java.io.Serializable;

public  class Temperature implements Serializable {
    private float temp;
    private float minTemp;
    private float maxTemp;

    public Temperature() { }
    public float getTemp() {
        return temp;
    }
    public void setTemp(float temp) {
        this.temp = temp;
    }
    public float getMinTemp() {
        return minTemp;
    }
    public void setMinTemp(float minTemp) {
        this.minTemp = minTemp;
    }
    public float getMaxTemp() {
        return maxTemp;
    }
    public void setMaxTemp(float maxTemp) {
        this.maxTemp = maxTemp;
    }

}