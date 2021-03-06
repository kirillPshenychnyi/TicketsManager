package com.example.android.ticketsmanager.datasource;

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.util.Log;

import com.example.android.ticketsmanager.App;
import com.example.android.ticketsmanager.db.AppDatabase;
import com.example.android.ticketsmanager.db.ORMFactory;
import com.example.android.ticketsmanager.db.RequestInfo;
import com.example.android.ticketsmanager.db.RequestInfoDao;
import com.example.android.ticketsmanager.rest.JOM.Event;
import com.example.android.ticketsmanager.rest.JOM.EventsCollection;
import com.example.android.ticketsmanager.rest.JOM.TicketsResponse;
import com.example.android.ticketsmanager.utils.NetworkState;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class EventsDataSource{

    private final ORMFactory ormFactory;
    private final Context context;
    private final MutableLiveData<NetworkState> networkState;

    private QueryParams queryParams;

    private int currentPage;
    private int totalPages;

    public EventsDataSource(Context context){
        this.context = context;
        this.ormFactory = new ORMFactory(context, this::onError);

        this.networkState = new MutableLiveData<>();

        this.totalPages = -1;
        this.currentPage = 0;
    }

    public MutableLiveData<NetworkState> getNetworkState() { return networkState; }

    public void setQueryParams(QueryParams params){
        this.queryParams = params;
    }

    public void recoverFromError(){
        networkState.setValue(NetworkState.RECOVER);
    }

    public void fetchMore() {
        NetworkState networkStatus = networkState.getValue();

        if(networkStatus == NetworkState.RUNNING || networkStatus == NetworkState.FAILED || isEndReached()) {
            return;
        }

        if(totalPages != -1) {
            fetchData();
            return;
        }

        long requestId = queryParams.hashCode();

        Disposable requestParams =
                AppDatabase.getsInstance(context).getRequestInfoDao().getRequestInfo(requestId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                info -> {
                                    onParamsLoaded(info);
                                    fetchData();
                                },
                                this::onError,
                                () -> {
                                    totalPages = -1;
                                    currentPage = 0;
                                    fetchData();
                                }
                        );

        DisposableScheduler.getInstance().post(requestParams);
    }

    public void reset(){
        totalPages = -1;
        currentPage = 0;
    }

    private void fetchData(){
        if(isEndReached()){
            return;
        }

        networkState.setValue(NetworkState.RUNNING);

        Disposable disposable = App.getApi().getEvents(
                queryParams.getCountryCode(),
                queryParams.hasKeyword() ?  queryParams.getKeyword() : null,
                currentPage,
                App.getApiKey()
        )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onLoaded, this::onError);

        incrementPage();

        DisposableScheduler.getInstance().post(disposable);
    }

    private void incrementPage() {
        ++currentPage;

        RequestInfoDao requestInfoDao = AppDatabase.getsInstance(context).getRequestInfoDao();
        long id = queryParams.hashCode();

        Disposable adding =
                Completable.fromAction(() -> requestInfoDao.updateLastPage(id, currentPage))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> {}, this::onError);

        DisposableScheduler.getInstance().post(adding);
    }

    private void onParamsLoaded(RequestInfo info){
        if(info != null){
            totalPages = info.getTotalPagesInResponse();
            currentPage = info.getLastRequestedPage();
        }
    }

    private void fillDB(EventsCollection events){
        for (Event event : events.getEvents()) {
            ormFactory.convert(event);
            if(networkState.getValue() == NetworkState.FAILED){
                break;
            }
        }

        networkState.postValue(NetworkState.SUCCESS);
    }

    private void onLoaded(TicketsResponse response){
        if(response == null || response.getEventsCollection() == null) {
            networkState.setValue(NetworkState.SUCCESS);
            return;
        }

        if(totalPages == -1){
            totalPages = response.getPage().getTotalPages();
            saveRequestInfo();
        }

        EventsCollection collection = response.getEventsCollection();
        Disposable adding =
                Completable.fromAction( ()->fillDB(collection) )
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> {}, this::onError);

        DisposableScheduler.getInstance().post(adding);
    }

    private void saveRequestInfo(){
        RequestInfoDao requestInfoDao = AppDatabase.getsInstance(context).getRequestInfoDao();
        long id = queryParams.hashCode();

        Disposable adding =
                Completable.fromAction(() -> {
                        requestInfoDao.insert(new RequestInfo(id, currentPage, totalPages));
                })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> {}, this::onError);

        DisposableScheduler.getInstance().post(adding);
    }

    private boolean isEndReached(){
        return totalPages  != -1 && currentPage >= totalPages;
    }

    private void onError(Throwable error){
        networkState.postValue(NetworkState.FAILED);
        Log.d(EventsDataSource.class.getName(), error.getMessage());
    }
}
