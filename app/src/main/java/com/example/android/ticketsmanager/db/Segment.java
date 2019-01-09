package com.example.android.ticketsmanager.db;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Segment {

    @PrimaryKey(autoGenerate = true)
    private long segmentId;

    private String segmentName;

    public Segment(long segmentId, String segmentName){
        this.segmentId = segmentId;
        this.segmentName = segmentName;
    }

    @Ignore
    public Segment(String segmentName){
        this.segmentName = segmentName;
    }

    public long getSegmentId() {
        return segmentId;
    }

    public void setSegmentId(long segmentId) {
        this.segmentId = segmentId;
    }

    public String getSegmentName() {
        return segmentName;
    }

    public void setSegmentName(String segmentName) {
        this.segmentName = segmentName;
    }
}
