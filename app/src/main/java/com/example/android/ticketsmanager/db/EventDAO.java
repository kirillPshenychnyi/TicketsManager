package com.example.android.ticketsmanager.db;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import java.util.List;

@Dao
public interface EventDAO {

    @Insert
    Long insertEvent(Event newEntry);

    @Transaction
    @Query(
            "SELECT Event.event_id AS eventId, Event.name AS eventName, Event.startDate AS startDate, " +
                    "Location.name AS locationName, City.cityName AS cityName, " +
                    "Country.countryName AS countryName " +
                    "FROM Event, City, Country " +
                    "INNER JOIN Location ON Event.location_id = Location.location_id " +
                    "WHERE Location.city_id = City.city_id " +
                    "AND City.country_id = Country.country_id " +
                    "AND Country.countryName LIKE :countryCode " +
                    "ORDER BY startDate ASC"
    )
    LiveData<List<EventInfo>> loadAll(String countryCode);
}
