package com.example.android.ticketsmanager.utils;

import android.content.Context;
import android.content.res.Resources;

import com.example.android.ticketsmanager.R;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableBiMap;

public class StringUtils {

    private BiMap<String, String> code2FullName;

    static StringUtils sInstance;

    static public StringUtils getInstance(Context context) {
        if(sInstance == null){
            sInstance = new StringUtils(context);
        }

        return sInstance;
    }

    private StringUtils(Context context){
        code2FullName = HashBiMap.create();

        ImmutableBiMap.Builder<String, String> builder = ImmutableBiMap.builder();

        Resources resources = context.getResources();

        String[] countryNames = resources.getStringArray(R.array.countries);
        String[] countryCodes = resources.getStringArray(R.array.country_codes);

        int entriesSize = countryNames.length;
        for(int i = 0; i < entriesSize; ++i){
            builder.put(countryNames[i], countryCodes[i]);
        }

        code2FullName = builder.build();
    }

    public String toCounrtyCode(String countryName) {
        String code = code2FullName.get(countryName);
        return code != null ? code : "";
    }

    public String fromCountryCode(String countryCode) {
        String name = code2FullName.inverse().get(countryCode);
        return name != null ? name : countryCode;
    }
}
