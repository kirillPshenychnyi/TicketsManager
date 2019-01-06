package com.example.android.ticketsmanager.db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.Relation;
import android.support.v7.util.DiffUtil;

import java.util.Date;

@Entity(
    foreignKeys = @ForeignKey(
            entity = Location.class,
            parentColumns = "location_id",
            childColumns = "location_id"
    )
)
public class Event {

    @Override
    public boolean equals(Object obj) {

        if(obj == this){
            return true;
        }

        Event other = (Event) obj;

        return other.getEvent_id() == getEvent_id();
    }

    public static DiffUtil.ItemCallback<Event> DIFF_CALLBACK = new DiffUtil.ItemCallback<Event>() {
        @Override
        public boolean areItemsTheSame(Event oldItem, Event newItem) {
            return oldItem.getEvent_id() == newItem.getEvent_id();
        }

        @Override
        public boolean areContentsTheSame(Event oldItem, Event newItem) {
            return oldItem.equals(newItem);
        }
    };

    @PrimaryKey(autoGenerate = true)
    private long event_id;

    @ColumnInfo(index = true)
    private long location_id;

    private String name;

    private Date startDate;
    private Date endDate;

    @Ignore
    public Event(long location_id, String name, Date startDate, Date endDate){
        this.location_id = location_id;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Event(long location_id, long event_id, String name, Date startDate, Date endDate){
        this.location_id = location_id;
        this.name = name;
        this.event_id = event_id;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Date getEndDate() { return endDate; }

    public Date getStartDate() { return startDate; }

    public String getName() { return name; }

    public long getLocation_id() {
        return location_id;
    }

    public void setLocation_id(long location_id) {
        this.location_id = location_id;
    }

    public long getEvent_id() {
        return event_id;
    }

    public void setEvent_id(long event_id) {
        this.event_id = event_id;
    }
}
