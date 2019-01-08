package com.example.android.ticketsmanager.db;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class RequestInfo {

    @PrimaryKey
    private long id;

    private int lastRequestedPage;
    private int totalPagesInResponse;

    public RequestInfo(long id, int lastRequestedPage, int totalPagesInResponse){
        this.id = id;
        this.lastRequestedPage = lastRequestedPage;
        this.totalPagesInResponse = totalPagesInResponse;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getLastRequestedPage() {
        return lastRequestedPage;
    }

    public void setLastRequestedPage(int lastRequestedPage) {
        this.lastRequestedPage = lastRequestedPage;
    }

    public int getTotalPagesInResponse() {
        return totalPagesInResponse;
    }

    public void setTotalPagesInResponse(int totalPagesInResponse) {
        this.totalPagesInResponse = totalPagesInResponse;
    }
}
