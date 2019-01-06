package com.example.android.ticketsmanager.datasource;

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.util.Log;

import com.example.android.ticketsmanager.App;
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

    private CompositeDisposable compositeDisposable;
    private final ORMFactory ormFactory;
    private MutableLiveData<NetworkState> networkState;

    int currentPage;
    int totalPages;

    public EventsDataSource(Context context){
        compositeDisposable = new CompositeDisposable();
        ormFactory = new ORMFactory(context);
        networkState = new MutableLiveData<>();
        currentPage = 0;
        totalPages = -1;
    }

    public void fetchMore(QueryParams params){
        if(networkState.getValue() == NetworkState.LOADING || (totalPages  != -1 && currentPage >= totalPages)){
            return;
        }

        networkState.postValue(NetworkState.LOADING);

        Disposable disposable = App.getApi().getEvents(params.getCountryCode(), App.getApiKey(), currentPage)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(events -> onLoaded(events), this::onError);

        ++currentPage;
        compositeDisposable.add(disposable);
    }

    private void onLoaded(TicketsResponse response){
        if(response == null) {
            return;
        }

        if(totalPages == -1){
            totalPages = response.getPage().getTotalPages();
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

    public void reset(){
        totalPages = -1;
        currentPage = 0;
    }

    public MutableLiveData<NetworkState> getNetworkState() { return networkState;}

    private void onError(Throwable error){
        Log.d(EventsDataSource.class.getName(), error.getMessage());
    }
}
