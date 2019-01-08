package com.example.android.ticketsmanager.db;

import android.content.Context;

import com.example.android.ticketsmanager.rest.JOM.Date;
import com.example.android.ticketsmanager.rest.JOM.Venue;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class ORMFactory {

    private final AppDatabase database;

    public ORMFactory(Context context){
        database = AppDatabase.getsInstance(context);
    }

    public void convert(com.example.android.ticketsmanager.rest.JOM.Event event){

        Venue venue = event.getVenues().getVenues().get(0);
        long cityId =
                insertCity(
                        venue.getCity().getName(),
                        insertCountry(venue.getCountry().getCountryCode())
                );

        long id = database.getEventDao().insertEvent(
                new Event(
                        insertLocation(venue.getName(), cityId),
                        event.getName(),
                        convert(event.getDates().getStart()),
                        convert(event.getDates().getEnd())
                )
        );

        for(com.example.android.ticketsmanager.rest.JOM.Image image : event.getImages()){
            database.getImageDAO().insertImage(
                    new Image(
                            image.getUrl(),
                            image.getWidth(),
                            image.getHeight(),
                            id
                )
            );
        }
    }

    private long insertCountry(String countryName){

        LocationDao dao = database.getLocationDao();

        Country country = dao.getCountry(countryName);

        if(country != null){
            return country.getCountry_id();
        }

        return dao.insertCountry(new Country(countryName));
    }

    private long insertCity(String cityName, long countryId){

        LocationDao dao = database.getLocationDao();

        City city = dao.getCity(cityName);

        if(city != null){
            return city.getCity_id();
        }

        return dao.insertCity(new City(cityName, countryId));
    }

    private long insertLocation(String locationName, long cityId){

        LocationDao locationDao = database.getLocationDao();

        com.example.android.ticketsmanager.db.Location location =
                locationDao.getLocation(locationName);

        if(location != null){
            return location.getLocation_id();
        }

        return locationDao.insertLocation(
                new com.example.android.ticketsmanager.db.Location(locationName, cityId)
        );
    }

    private java.util.Date convert(Date date) {

        if(date == null){
            return null;
        }

        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm");
        java.util.Date result = null;

        String time =
                date.getLocalDate() + ' ' + date.getLocalTime();

        try {
            result = dateFormat.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return result;
    }
}
