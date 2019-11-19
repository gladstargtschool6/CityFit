package com.example.cityfit.data.model;


import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

@IgnoreExtraProperties
public class ActivityHistory {

    private String year;
    private String category;
    private String type;
    private String activity_average;
    private @ServerTimestamp Date Timestamp;

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getActivity_average() {
        return activity_average;
    }

    public void setActivity_average(String activity_avergae) {
        this.activity_average = activity_avergae;
    }

    public Date getTimestamp() {
        return Timestamp;
    }

    public void setTimestamp(Date timestamp) {
        Timestamp = timestamp;
    }
}
