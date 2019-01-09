package com.example.android.ticketsmanager.db;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class SubGenre {

    @PrimaryKey(autoGenerate = true)
    private long subGenreId;

    public SubGenre(long subGenreId, String subGenreName) {
        this.subGenreId = subGenreId;
        this.subGenreName = subGenreName;
    }

    @Ignore
    public SubGenre(String subGenreName) {
        this.subGenreName = subGenreName;
    }

    private String subGenreName;

    public long getSubGenreId() {
        return subGenreId;
    }

    public void setSubGenreId(long subGenreId) {
        this.subGenreId = subGenreId;
    }

    public String getSubGenreName() {
        return subGenreName;
    }

    public void setSubGenreName(String subGenreName) {
        this.subGenreName = subGenreName;
    }
}
