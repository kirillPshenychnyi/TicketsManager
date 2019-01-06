package com.example.android.ticketsmanager.db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity(foreignKeys = @ForeignKey(
        entity = Country.class,
        parentColumns = "country_id",
        childColumns = "country_id"))
public class City {

    @PrimaryKey(autoGenerate = true)
    private long city_id;

    private String cityName;

    @ColumnInfo(index = true)
    private long country_id;

    public City(long city_id, String cityName, long country_id){
        this.country_id = country_id;
        this.city_id = city_id;
        this.cityName = cityName;
    }

    @Ignore
    public City(String cityName, long country_id){
        this.cityName = cityName;
        this.country_id = country_id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public long getCity_id() {
        return city_id;
    }

    public void setCity_id(int id) {
        this.city_id = id;
    }

    public long getCountry_id() {
        return country_id;
    }

    public void setCountry_id(long country_id) {
        this.country_id = country_id;
    }
}
