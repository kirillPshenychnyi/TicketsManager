package com.example.android.ticketsmanager.viewmodel;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;

import com.example.android.ticketsmanager.datasource.EventsDataSource;
import com.example.android.ticketsmanager.datasource.QueryParams;
import com.example.android.ticketsmanager.db.AppDatabase;
import com.example.android.ticketsmanager.db.EventInfo;
import com.example.android.ticketsmanager.utils.NetworkState;

import java.util.List;

public class EventsViewModel extends ViewModel {

    private LiveData<List<EventInfo>> eventsLiveData;
    private final Context context;
    private QueryParams params;
    private final EventsDataSource dataSource;

    public EventsViewModel(Context context, QueryParams params){
        this.context = context;
        this.params = params;
        dataSource = new EventsDataSource(context);

        eventsLiveData = AppDatabase.getsInstance(context).takeEventDao().loadAll(params.getCountryCode());
    }

    public void unsubscribe(LifecycleOwner owner){
        eventsLiveData.removeObservers(owner);
        dataSource.getNetworkState().removeObservers(owner);
    }

    public void setParams(QueryParams params){
        this.params = params;
        eventsLiveData = AppDatabase.getsInstance(context).takeEventDao().loadAll(params.getCountryCode());
    }

    public void searchEvents(){
        if(shouldRequest()){
            fetchMore();
        }
    }

    private boolean shouldRequest(){
        if(eventsLiveData == null){
            return true;
        }

        List<EventInfo> eventInfos = eventsLiveData.getValue();
        return eventInfos == null || eventInfos.isEmpty();
    }

    public void fetchMore(){
        dataSource.fetchMore(params);
    }

    public LiveData<List<EventInfo>> getEventsLiveData() { return eventsLiveData; }

    public LiveData<NetworkState> getNetworkStateLiveData(){ return dataSource.getNetworkState(); }
}
