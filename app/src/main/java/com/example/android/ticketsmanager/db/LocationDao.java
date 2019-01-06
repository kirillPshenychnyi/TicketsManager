package com.example.android.ticketsmanager.db;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.util.Log;

@Dao
public interface LocationDao {

    @Insert
    long insertCity(City city);

    @Query("SELECT * FROM City WHERE cityName = :cityName")
    City getCity(String cityName);

    @Insert
    long insertCountry(Country country);

    @Query("SELECT * FROM Country WHERE countryName = :countryName")
    Country getCountry(String countryName);

    @Insert
    long insertLocation(Location location);

    @Query("SELECT * FROM Location WHERE name = :name")
    Location getLocation(String name);

    @Query(
                "SELECT Location.name AS locationName, City.cityName AS city FROM Location, City " +
                "WHERE location_id = :id AND Location.city_id = City.city_id"
    )
    LocationInfo getLocation(long id);

    class LocationInfo{
        public String locationName;
        public String city;
        //public String country;
    }
}
