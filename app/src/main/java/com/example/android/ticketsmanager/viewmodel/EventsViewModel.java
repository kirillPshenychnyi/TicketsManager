package com.example.android.ticketsmanager.viewmodel;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;

import com.example.android.ticketsmanager.datasource.EventsDataSource;
import com.example.android.ticketsmanager.datasource.QueryParams;
import com.example.android.ticketsmanager.db.AppDatabase;
import com.example.android.ticketsmanager.db.EventDAO;
import com.example.android.ticketsmanager.db.EventInfo;
import com.example.android.ticketsmanager.utils.NetworkState;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class EventsViewModel extends ViewModel {

    public interface OnDbResult{
        void onResult(List<EventInfo> infos);
    }

    private final Context context;
    private QueryParams params;
    private final EventsDataSource dataSource;

    public EventsViewModel(Context context, QueryParams params){
        this.context = context;

        dataSource = new EventsDataSource(context);
        setParams(params);
    }

    public void unsubscribe(LifecycleOwner owner){
        dataSource.getNetworkState().removeObservers(owner);
        dataSource.reset();
    }

    public void setParams(QueryParams params){
        this.params = params;
        dataSource.setQueryParams(params);
    }

    public void searchEvents(){
        Maybe<List<EventInfo>> flowable
                =   AppDatabase.getsInstance(context).getEventDao().loadAll(
                        params.getCountryCode(),
                        params.getKeyword()
                    );

        flowable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(eventInfos -> {
                if(eventInfos.isEmpty()) {
                    fetchMore();
                }
            });
    }

    public void fetchFromDb(OnDbResult onDbResult){
        EventDAO dao = AppDatabase.getsInstance(context).getEventDao();

        subscribe(dao.loadAll(params.getCountryCode(), params.getKeyword()), onDbResult);
    }

    private void subscribe(Maybe<List<EventInfo>> request, OnDbResult onDbResult){
        request.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(eventInfos -> onDbResult.onResult(eventInfos));
    }

    public void fetchMore(){
        dataSource.fetchMore();
    }

    public LiveData<NetworkState> getNetworkStateLiveData(){ return dataSource.getNetworkState(); }
}
