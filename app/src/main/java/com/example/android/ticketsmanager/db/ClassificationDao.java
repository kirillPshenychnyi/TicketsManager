package com.example.android.ticketsmanager.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

@Dao
public interface ClassificationDao {

    @Insert
    long insertGenre(Genre genre);

    @Query("SELECT * FROM Genre WHERE Genre.genreId = :genreId")
    Genre getGenre(long genreId);

    @Insert
    long insertSubGenre(SubGenre subGenre);

    @Query("SELECT * FROM SubGenre WHERE SubGenre.subGenreId = :subGenreId")
    SubGenre getSubGenre(long subGenreId);

    @Insert
    long insertSegment(Segment segment);

    @Query("SELECT * FROM Segment WHERE Segment.segmentId = :segmentId")
    Segment getSegment(long segmentId);
}
