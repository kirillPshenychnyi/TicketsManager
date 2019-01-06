package com.example.android.ticketsmanager.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

@Database(entities = {Event.class, Image.class, Country.class, City.class, Location.class}, version = 25, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    private static final Object LOCK = new Object();
    private static final String Name = "AppDatabase";

    private static AppDatabase sInstance;

    public static AppDatabase getsInstance(Context context){
        if(sInstance == null) {
            synchronized (LOCK){
                sInstance = Room.databaseBuilder(
                                context.getApplicationContext(),
                                AppDatabase.class,
                                AppDatabase.Name
                            )
                            .fallbackToDestructiveMigration()
                            .build();
            }
        }

        return sInstance;
    }

    public abstract EventDAO takeEventDao();

    public abstract ImageDAO takeImageDAO();

    public abstract LocationDao takeLocationDao();
}
