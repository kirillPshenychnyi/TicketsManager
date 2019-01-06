package com.example.android.ticketsmanager.db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

@Entity(
        foreignKeys = @ForeignKey(entity = Event.class, parentColumns = "event_id", childColumns = "eventId")
        //indices = @Index(value ="eventId")
)
public class Image {

    @PrimaryKey(autoGenerate = true)
    private long image_id;

    @ColumnInfo(index = true)
    private long eventId;

    private String url;
    private int width;
    private int height;

    @Ignore
    public Image(String url, int width, int height, long eventId){
        this.url = url;
        this.width = width;
        this.height = height;
        this.eventId = eventId;
    }

    public Image(long image_id, long eventId, String url, int width, int height){
        this.eventId = eventId;
        this.image_id = image_id;
        this.url = url;
        this.width = width;
        this.height = height;
    }

    public void setUrl(String url) { this.url = url; }

    public String getUrl() {
        return url;
    }

    public void setWidth(int width){ this.width = width; }

    public int getWidth() {
        return width;
    }

    public void setHeight(int height){ this.height = height; }

    public int getHeight() {
        return height;
    }

    public long getImage_id() {
        return image_id;
    }

    public void setImage_id(long image_id) {
        this.image_id = image_id;
    }

    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }
}
