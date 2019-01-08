package com.example.android.ticketsmanager.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import io.reactivex.Maybe;

@Dao
public interface RequestInfoDao {

    @Insert
    void insert(RequestInfo info);

    @Query("UPDATE RequestInfo SET lastRequestedPage = :requestedPages WHERE id = :id")
    void updateLastPage(long id, int requestedPages);

    @Query("SELECT * FROM RequestInfo WHERE RequestInfo.id = :id")
    Maybe<RequestInfo> getRequestInfo(long id);
}
