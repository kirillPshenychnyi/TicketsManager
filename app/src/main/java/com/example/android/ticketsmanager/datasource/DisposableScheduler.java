package com.example.android.ticketsmanager.datasource;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class DisposableScheduler {

    private static final Object LOCK = new Object();
    private static DisposableScheduler sInstance;

    private final CompositeDisposable compositeDisposable;

    public static DisposableScheduler getInstance() {
        if(sInstance == null){
            synchronized (LOCK){
                sInstance = new DisposableScheduler();
            }
        }

        return sInstance;
    }

    private DisposableScheduler() {
        compositeDisposable = new CompositeDisposable();
    }

    public void post(Disposable disposable){
        compositeDisposable.add(disposable);
    }
}
