package com.example.android.ticketsmanager.db;

import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.Relation;
import android.support.v7.util.DiffUtil;

import java.util.Date;
import java.util.List;

public class EventInfo {

    public static class Comparator extends DiffUtil.Callback {

        private final List<EventInfo> oldList;
        private final List<EventInfo> newList;

        public Comparator(List<EventInfo> oldList, List<EventInfo> newList){
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return
                    oldList.get(oldItemPosition).getEventId() == newList.get(newItemPosition).getEventId();
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition) == newList.get(newItemPosition);
        }
    }

    @PrimaryKey
    private long eventId;

    private String eventName;

    private Date startDate;

    private String locationName;

    private String cityName;

    private String countryName;

    private String segmentName;

    private String genreName;

    private String subGenreName;

    @Relation(parentColumn = "eventId", entityColumn = "eventId")
    private List<Image> images;

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getSegmentName() {
        return segmentName;
    }

    public void setSegmentName(String segmentName) {
        this.segmentName = segmentName;
    }

    public String getGenreName() {
        return genreName;
    }

    public void setGenreName(String genreName) {
        this.genreName = genreName;
    }

    public String getSubGenreName() {
        return subGenreName;
    }

    public void setSubGenreName(String subGenreName) {
        this.subGenreName = subGenreName;
    }


    @Override
    public boolean equals(Object obj) {
        EventInfo other = (EventInfo)(obj);

        if(other == null){
            return false;
        }

        return
                other.getEventName() == getEventName() &&
                other.getCityName() == getCityName() &&
                other.getCountryName() == other.getCountryName() &&
                other.getLocationName() == other.getLocationName() &&
                other.getStartDate() == getStartDate();
    }
}
