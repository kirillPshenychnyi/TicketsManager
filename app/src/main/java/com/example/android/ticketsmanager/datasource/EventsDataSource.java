package com.example.android.ticketsmanager.datasource;

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.android.ticketsmanager.App;
import com.example.android.ticketsmanager.R;
import com.example.android.ticketsmanager.db.ORMFactory;
import com.example.android.ticketsmanager.rest.JOM.Event;
import com.example.android.ticketsmanager.rest.JOM.EventsCollection;
import com.example.android.ticketsmanager.rest.JOM.TicketsResponse;
import com.example.android.ticketsmanager.utils.NetworkState;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class EventsDataSource{

    private static final String SORT_ORDER = "date,asc";

    private CompositeDisposable compositeDisposable;
    private final ORMFactory ormFactory;
    private final Context context;
    private MutableLiveData<NetworkState> networkState;

    int currentPage;
    int totalPages;

    public EventsDataSource(Context context){
        this.context = context;
        this.compositeDisposable = new CompositeDisposable();
        this.ormFactory = new ORMFactory(context);
        this.networkState = new MutableLiveData<>();
        initPages();
    }

    public MutableLiveData<NetworkState> getNetworkState() { return networkState;}

    public void fetchMore(QueryParams params){
        if(networkState.getValue() == NetworkState.LOADING || (totalPages  != -1 && currentPage >= totalPages)){
            return;
        }

        networkState.postValue(NetworkState.LOADING);

        Disposable disposable = App.getApi().getEvents(
                params.getCountryCode(),
                SORT_ORDER,
                currentPage,
                App.getApiKey()
        )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(events -> onLoaded(events), this::onError);

        ++currentPage;

        saveToSharedPreferences(
                context.getResources().getString(R.string.last_requested_page),
                currentPage
        );

        compositeDisposable.add(disposable);
    }

    public void reset(){
        totalPages = -1;
        currentPage = 0;
    }

    private void initPages(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        totalPages =
                sharedPreferences.getInt(
                        context.getResources().getString(R.string.total_pages),
                        -1);

        currentPage =
                sharedPreferences.getInt(
                        context.getResources().getString(R.string.last_requested_page),
                        0);
    }

    private void onLoaded(TicketsResponse response){
        if(response == null) {
            return;
        }

        if(totalPages == -1){
            totalPages = response.getPage().getTotalPages();
            saveToSharedPreferences(
                    context.getResources().getString(R.string.total_pages),
                    totalPages
            );
        }

        EventsCollection collection = response.getEventsCollection();

        if(collection == null){
            return;
        }

        Disposable adding =
                Completable.fromAction(() -> {
                            for (Event event : collection.getEvents())
                                ormFactory.convert(event);
                        }
                )
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> networkState.postValue(NetworkState.LOADED), this::onError);

        compositeDisposable.add(adding);
    }

    private void saveToSharedPreferences(String name, int value){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(name, value);

        editor.commit();
    }

    private void onError(Throwable error){
        Log.d(EventsDataSource.class.getName(), error.getMessage());
    }
}
