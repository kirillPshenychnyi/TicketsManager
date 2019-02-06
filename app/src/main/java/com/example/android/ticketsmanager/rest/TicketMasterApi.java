package com.example.android.ticketsmanager.rest;

import com.example.android.ticketsmanager.rest.JOM.TicketsResponse;
import com.example.android.ticketsmanager.rest.JOM.Venue;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TicketMasterApi {

    @GET("/discovery/v2/events.json")
    Single<TicketsResponse> getEvents(
            @Query("countryCode")String countryCode,
            @Query("keyword") String keyword,
            @Query("page")int page,
            @Query("apikey")String apiKey);

   @GET("/discovery/v2/venues/{id}")
   Single<Venue> getVenue(@Path("id") String id,  @Query("apikey")String apiKey);
}
