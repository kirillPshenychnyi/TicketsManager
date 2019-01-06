package com.example.android.ticketsmanager.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;

@Dao
public interface ImageDAO {
    @Insert
    Long insertImage(Image newEntry);
}
