package com.example.android.ticketsmanager.db;

import android.content.Context;

import com.example.android.ticketsmanager.rest.JOM.Classification;
import com.example.android.ticketsmanager.rest.JOM.Date;
import com.example.android.ticketsmanager.rest.JOM.Venue;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class ORMFactory {

    private interface DaoChecker<T>{
        T contains(long id);
    }

    private interface DaoInserter<T>{
        long insertEntity(T entity);
    }

    private interface IdGetter<T> {
        String getId();
    }

    private interface Factory<T> {
        T create(long primaryKey);
    }

    private final AppDatabase database;

    public ORMFactory(Context context){
        database = AppDatabase.getsInstance(context);
    }

    public void convert(com.example.android.ticketsmanager.rest.JOM.Event event){
        EventDAO dao = database.getEventDao();

        Venue venue = event.getVenues().getVenues().get(0);

        long cityId =
                insertCity(
                        venue.getCity().getName(),
                        insertCountry(venue.getCountry().getCountryCode())
                );

        long eventId =
                dao.insertEvent(
                        new Event(
                                insertLocation(venue.getName(), cityId),
                                insertClassification(event.getClassifications().get(0)),
                                event.getName(),
                                convert(event.getDates().getStart()),
                                convert(event.getDates().getEnd())
                        )
                );

        for (com.example.android.ticketsmanager.rest.JOM.Image image : event.getImages()) {
            database.getImageDAO().insertImage(
                    new Image(
                            image.getUrl(),
                            image.getWidth(),
                            image.getHeight(),
                            eventId
                    )
            );
        }
    }

    private long insertClassification(Classification classification){
        long segmentId = insertSegment(classification.getSegment());
        long genreId = insertGenre(classification.getGenre(), segmentId);
        return insertSubGenre(classification.getSubGenre(), genreId);
    }

    private long insertSegment(com.example.android.ticketsmanager.rest.JOM.Segment segment){
        ClassificationDao dao = database.getClassificationDao();

        return this.<Segment, com.example.android.ticketsmanager.rest.JOM.Segment>getEntity(
                segment::getId,
                (long primaryKey) -> new Segment(primaryKey, segment.getName()),
                dao::getSegment,
                dao::insertSegment
        );
    }

    private long insertGenre(com.example.android.ticketsmanager.rest.JOM.Genre genre, long segmentId){
        ClassificationDao dao = database.getClassificationDao();

        return this.<Genre, com.example.android.ticketsmanager.rest.JOM.Genre>getEntity(
                genre::getId,
                (long primaryKey) -> new Genre(primaryKey, segmentId, genre.getName()),
                dao::getGenre,
                dao::insertGenre
        );
    }

    private long insertSubGenre(com.example.android.ticketsmanager.rest.JOM.SubGenre subGenre, long genreId){
        if (subGenre != null) {
            ClassificationDao dao = database.getClassificationDao();
            return this.<SubGenre, com.example.android.ticketsmanager.rest.JOM.SubGenre>getEntity(
                    subGenre::getId,
                    (long primaryKey) -> new SubGenre(primaryKey, genreId, subGenre.getName()),
                    dao::getSubGenre,
                    dao::insertSubGenre
            );
        }

        return genreId;
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

    private <DbEntry, JOM>long getEntity(
            IdGetter<JOM> idGetter,
            Factory<DbEntry> factory,
            DaoChecker<DbEntry> checker,
            DaoInserter<DbEntry> inserter
    ){
        long id = idGetter.getId().hashCode();
        DbEntry entry = checker.contains(id);
        if(entry != null){
            return id;
        }

        return inserter.insertEntity(factory.create(id));
    }

    private java.util.Date convert(Date date) {
        if(date == null){
            return null;
        }

        Boolean dateTBD = date.getDateTBD();
        if(dateTBD != null && dateTBD) {
            return null;
        }

        Boolean dateTBA = date.getTimeTBA();
        if(dateTBA != null && dateTBA){
            return null;
        }

        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");

        java.util.Date result = null;
        String dateStr = date.getLocalDate();

        try {
            result = dateFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Boolean timeTBA = date.getTimeTBA();

        String localTime = date.getLocalTime();

        if(timeTBA != null && !timeTBA && localTime != null){
            try {
                DateFormat timeFormat = new SimpleDateFormat("hh:mm");
                java.util.Date time = timeFormat.parse(date.getLocalTime());
                result.setHours(time.getHours());
                result.setMinutes(time.getMinutes());
            }
            catch (ParseException ex){
                ex.printStackTrace();
            }
        }

        return result;
    }
}
