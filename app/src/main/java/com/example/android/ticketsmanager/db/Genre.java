package com.example.android.ticketsmanager.db;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Genre {

    @PrimaryKey(autoGenerate = true)
    private long genreId;

    private String genreName;

    public long getGenreId() {
        return genreId;
    }

    public Genre(long genreId, String genreName) {
        this.genreId = genreId;
        this.genreName = genreName;
    }

    @Ignore
    public Genre(String genreName) {
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
}
