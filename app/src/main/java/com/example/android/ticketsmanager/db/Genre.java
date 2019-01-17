package com.example.android.ticketsmanager.db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

@Entity(foreignKeys = @ForeignKey(
        entity = Segment.class,
        parentColumns = "segmentId",
        childColumns = "segmentId"))
public class Genre {

    @PrimaryKey
    private long genreId;

    @ColumnInfo(index = true)
    private long segmentId;

    private String genreName;

    public long getGenreId() {
        return genreId;
    }

    public Genre(long genreId, long segmentId, String genreName) {
        this.segmentId = segmentId;
        this.genreId = genreId;
        this.genreName = genreName;
    }

    public void setGenreId(long genreId) {
        this.genreId = genreId;
    }

    public String getGenreName() {
        return genreName;
    }

    public void setGenreName(String genreName) {
        this.genreName = genreName;
    }

    public long getSegmentId() { return segmentId; }

    public void setSegmentId(long segmentId) { this.segmentId = segmentId; }
}
