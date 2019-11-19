package com.example.cityfit.data.model;

import java.io.Serializable;

public  class Clouds implements Serializable {
    public Clouds(){}
    private int perc;

    public int getPerc() {
        return perc;
    }

    public void setPerc(int perc) {
        this.perc = perc;
    }


}