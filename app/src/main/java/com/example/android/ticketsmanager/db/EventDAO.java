package com.example.android.ticketsmanager.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import java.util.List;

import io.reactivex.Maybe;

@Dao
public interface EventDAO {

    @Insert
    Long insertEvent(Event newEntry);

    @Transaction
    @Query(
            "SELECT Event.event_id AS eventId, Event.name AS eventName, " +
                    "Event.startDate AS startDate, " +
                    "Location.name AS locationName, City.cityName AS cityName, " +
                    "Country.countryName AS countryName, " +
                    "Segment.segmentName AS segmentName, Genre.genreName AS genreName, " +
                    "SubGenre.subGenreName AS subGenreName " +
                    "FROM Event, City, Country, Segment, Genre, SubGenre " +
                    "INNER JOIN Location ON Event.location_id = Location.location_id " +
                    "WHERE Location.city_id = City.city_id " +
                    "AND City.country_id = Country.country_id " +
                    "AND Event.subGenreId = SubGenre.subGenreId " +
                    "AND SubGenre.genreId = Genre.genreId " +
                    "AND Genre.segmentId = Segment.segmentId " +
                    "AND Country.countryName LIKE :countryCode " +
                    "AND (eventName LIKE :keyword OR genreName LIKE :keyword " +
                    "OR subGenreName LIKE :keyword OR segmentName LIKE :keyword) " +
                    "ORDER BY startDate ASC"
    )
    Maybe<List<EventInfo>> loadAll(String countryCode, String keyword);

    @Query("SELECT * FROM Event WHERE Event.event_id = :eventId ")
    Event getEvent(long eventId);
}
