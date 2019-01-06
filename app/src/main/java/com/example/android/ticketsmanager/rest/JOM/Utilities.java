package com.example.android.ticketsmanager.rest.JOM;

import android.content.Context;
import android.net.Uri;
import android.os.Build;

import com.example.android.ticketsmanager.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.List;

public class Utilities {

    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd");
    private static final SimpleDateFormat newDateFormat = new SimpleDateFormat("dd MMMM yyyy");
    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm");

    public static String getDate(Event event){

        Date start = event.getDates().getStart();

        java.util.Date parsed = null;

        try {
            parsed = format.parse(start.getLocalDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return newDateFormat.format(parsed);
    }

    public static String getTime(Event event, Context context){

        Dates dates = event.getDates();
        Date start = dates.getStart();

        if(start.getNoSpecificTime() || start.getLocalTime() == null){
            return context.getResources().getString(R.string.tba);
        }

         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return LocalTime.parse(start.getLocalTime()).toString();
        }

        return "";
    }

    public static Uri getImageUri(Event event){
        List<Image> images = event.getImages();

        if(images.isEmpty()){
            return Uri.EMPTY;
        }

        String imageUri = images.get(0).getUrl();

        for (Image img : images) {
            if(img.getWidth() == 1024)
                imageUri = img.getUrl();
        }

        return Uri.parse(imageUri);
    }

    public static String getLocation(Event event){

        String locationFormat = "%s, %s";

        List<Venue> venues = event.getVenues().getVenues();

        if(venues.isEmpty()) {
            return "";
        }

        Venue venue = venues.get(0);

        return String.format(locationFormat, venue.getName(), venue.getCity().getName());
    }
}
