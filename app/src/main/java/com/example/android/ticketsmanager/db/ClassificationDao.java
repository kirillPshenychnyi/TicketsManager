package com.example.android.ticketsmanager.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

@Dao
public interface ClassificationDao {

    @Insert
    long insertGenre(Genre genre);

    @Query("SELECT * FROM Genre WHERE Genre.genreName = :genreName")
    Genre getGenre(String genreName);

    @Insert
    long insertSubGenre(SubGenre subGenre);

    @Query("SELECT * FROM SubGenre WHERE SubGenre.subGenreName = :subGenreName")
    SubGenre getSubGenre(String subGenreName);

    @Insert
    long insertSegment(Segment segment);

    @Query("SELECT * FROM Segment WHERE Segment.segmentName = :segmentName")
    Segment getSegment(String segmentName);
}
