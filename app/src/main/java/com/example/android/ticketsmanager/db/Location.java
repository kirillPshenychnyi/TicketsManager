package com.example.android.ticketsmanager.db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity(foreignKeys = @ForeignKey(
        entity = City.class,
        parentColumns = "city_id",
        childColumns = "city_id"
    )
)
public class Location {

    @PrimaryKey(autoGenerate = true)
    private long location_id;

    private String name;

    @ColumnInfo(index = true)
    private long city_id;

    @Ignore
    public Location(String location, long city_id) {
        this.city_id = city_id;
        this.name = location;
    }

    public Location(long location_id, String name, long city_id) {
        this.location_id = location_id;
        this.city_id = city_id;
        this.name = name;
    }

    public long getCity_id() {
        return city_id;
    }

    public void setCity_id(long city_id) {
        this.city_id = city_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getLocation_id() {
        return location_id;
    }

    public void setLocation_id(long location_id) {
        this.location_id = location_id;
    }
}
