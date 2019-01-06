package com.example.android.ticketsmanager.rest;

import com.example.android.ticketsmanager.rest.JOM.TicketsResponse;

import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TicketMasterApi {

    @GET("/discovery/v2/events.json")
    Single<TicketsResponse> getEvents(
            @Query("countryCode")String countryCode,
            @Query("sort") String sort,
            @Query("page")int page,
            @Query("apikey")String apiKey
    );
}
