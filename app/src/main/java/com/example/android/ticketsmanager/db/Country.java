package com.example.android.ticketsmanager.db;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Country {

    @PrimaryKey(autoGenerate = true)
    private long country_id;

    private String countryName;

    public Country(long country_id, String countryName){
        this.country_id = country_id;
    }

    @Ignore
    public Country(String countryName){
        this.countryName = countryName;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public long getCountry_id() {
        return country_id;
    }

    public void setCountry_id(int id) {
        this.country_id = id;
    }
}
