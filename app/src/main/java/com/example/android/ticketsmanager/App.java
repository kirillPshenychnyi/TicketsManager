package com.example.android.ticketsmanager;

import android.app.Application;

import com.example.android.ticketsmanager.rest.TicketMasterApi;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class App extends Application {

    private Retrofit mRetrofit;
    private static TicketMasterApi mMasterApi;

    private static String API_KEY = "c7gMRDpkA71TyWGoM9WE33zg0ADZJGO3";

    @Override
    public void onCreate() {
        super.onCreate();

        mRetrofit = new Retrofit.Builder()
                .baseUrl("https://app.ticketmaster.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        mMasterApi = mRetrofit.create(TicketMasterApi.class);
    }

    public static TicketMasterApi getApi() {
        return mMasterApi;
    }

    public static String getApiKey() {
        return API_KEY;
    }
}
