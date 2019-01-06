package com.example.android.ticketsmanager.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.support.annotation.NonNull;

import com.example.android.ticketsmanager.datasource.QueryParams;

/**
 * Created by Кирилл on 11/5/2018.
 */

public class ViewModelFactory implements ViewModelProvider.Factory {

    private final Context context;
    private final QueryParams params;

    public ViewModelFactory(Context context, QueryParams params){
        this.context = context;
        this.params = params;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T)(new EventsViewModel(context, params));
    }
}
