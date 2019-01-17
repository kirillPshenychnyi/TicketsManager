package com.example.android.ticketsmanager.db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

@Entity(foreignKeys = @ForeignKey(
        entity = Genre.class,
        parentColumns = "genreId",
        childColumns = "genreId"
))
public class SubGenre {

    @PrimaryKey
    private long subGenreId;

    @ColumnInfo(index = true)
    private long genreId;

    private String subGenreName;

    public SubGenre(long subGenreId, long genreId, String subGenreName) {
        this.genreId = genreId;
        this.subGenreId = subGenreId;
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
