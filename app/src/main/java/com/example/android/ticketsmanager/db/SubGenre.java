package com.example.android.ticketsmanager.db;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity(foreignKeys = @ForeignKey(
        entity = Genre.class,
        parentColumns = "genreId",
        childColumns = "genreId"
))
public class SubGenre {

    @PrimaryKey(autoGenerate = true)
    private long subGenreId;

    private long genreId;

    private String subGenreName;

    public SubGenre(long subGenreId, long genreId, String subGenreName) {
        this.genreId = genreId;
        this.subGenreId = subGenreId;
        this.subGenreName = subGenreName;
    }

    @Ignore
    public SubGenre(String subGenreName, long genreId) {
        this.genreId = genreId;
        this.subGenreName = subGenreName;
    }

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

    public long getGenreId() { return genreId; }

    public void setGenreId(long genreId) { this.genreId = genreId; }
}
