package com.example.cityfit.data.model;

import javax.annotation.Nullable;

public class Weather {

    public Location location;
    public CurrentCondition currentCondition = new CurrentCondition();
    public Temperature temperature = new Temperature();
    public Wind wind = new Wind();
//    @Nullable
////    public Rain rain = new Rain();
//    @Nullable
//    public Snow snow = new Snow()	;
    public Clouds clouds = new Clouds();

    public byte[] iconData;

    public  class CurrentCondition {

        public CurrentCondition(){ }


        private int weatherId;
        private String condition;
        private String descr;
        private String icon;
        private float pressure;
        private float humidity;

        public int getWeatherId() {
            return weatherId;
        }
        public void setWeatherId(int weatherId) {
            this.weatherId = weatherId;
        }
        public String getCondition() {
            return condition;
        }
        public void setCondition(String condition) {
            this.condition = condition;
        }
        public String getDescr() {
            return descr;
        }
        public void setDescr(String descr) {
            this.descr = descr;
        }
        public String getIcon() {
            return icon;
        }
        public void setIcon(String icon) {
            this.icon = icon;
        }
        public float getPressure() {
            return pressure;
        }
        public void setPressure(float pressure) {
            this.pressure = pressure;
        }
        public float getHumidity() {
            return humidity;
        }
        public void setHumidity(float humidity) {
            this.humidity = humidity;
        }


    }

    public  class Temperature {
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

    public  class Wind {
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

//    public  class Rain {
//        public Rain(){ }
//        private String time;
//        private float ammount;
//        public String getTime() {
//            return time;
//        }
//        public void setTime(String time) {
//            this.time = time;
//        }
//        public float getAmmount() {
//            return ammount;
//        }
//        public void setAmmount(float ammount) {
//            this.ammount = ammount;
//        }
//
//
//
//    }
//
//    public  class Snow {
//        public Snow() { }
//        private String time;
//        private float ammount;
//
//        public String getTime() {
//            return time;
//        }
//        public void setTime(String time) {
//            this.time = time;
//        }
//        public float getAmmount() {
//            return ammount;
//        }
//        public void setAmmount(float ammount) {
//            this.ammount = ammount;
//        }
//
//
//    }

    public  class Clouds {
        public Clouds(){}
        private int perc;

        public int getPerc() {
            return perc;
        }

        public void setPerc(int perc) {
            this.perc = perc;
        }


    }

}
