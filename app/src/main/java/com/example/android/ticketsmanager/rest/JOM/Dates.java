package com.example.android.ticketsmanager.rest.JOM;

import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Dates {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @SerializedName("start")
    @Expose
    private Date start;
    @SerializedName("end")
    @Expose
    private Date end;
    @SerializedName("timezone")
    @Expose
    private String timezone;
    @SerializedName("spanMultipleDays")
    @Expose
    private Boolean spanMultipleDays;

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public Boolean getSpanMultipleDays() {
        return spanMultipleDays;
    }

    public void setSpanMultipleDays(Boolean spanMultipleDays) {
        this.spanMultipleDays = spanMultipleDays;
    }

    public int getId(){ return id; };

    public void setId(int id){ this.id = id; }
}