package com.example.android.ticketsmanager.datasource;

import android.content.Context;
import android.content.Intent;

public class RequestExtractor {

    public interface IntentDataGetter <T>{
        T extract(String extraName);
    }

    public interface QueryParamSetter<T>{
        void set(T data);
    }

    private final Context context;
    private final Intent data;

    public RequestExtractor(Context context, Intent data){
        this.context = context;
        this.data = data;
    }

    public <T> void setExtra(
            int paramId,
            IntentDataGetter<T> getter,
            QueryParamSetter<T> setter
    ){
        String extraName = context.getResources().getString(paramId);

        if(data.hasExtra(extraName)){
            setter.set(getter.extract(extraName));
        }
    }
}
