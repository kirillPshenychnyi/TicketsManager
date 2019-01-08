package com.example.android.ticketsmanager.utils;

import android.content.Context;
import android.content.res.Resources;

import com.example.android.ticketsmanager.R;

public class StringUtils {

    static public String toCounrtyCode(Context context, String countryName){
        Resources resources = context.getResources();
        if(countryName.equals(resources.getString(R.string.united_kingdom))){
            return resources.getString(R.string.uk);
        }

        if(countryName.equals(resources.getString(R.string.united_states))){
            return resources.getString(R.string.us);
        }

        if(countryName.equals(resources.getString(R.string.canada))){
            return resources.getString(R.string.ca);
        }

        return "";
    }
}
