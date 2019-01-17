package com.example.android.ticketsmanager.utils;

public class NetworkState {

    public enum Status{
        RUNNING,
        SUCCESS,
        FAILED
    }

    private final Status status;
    private final String msg;

    public static final NetworkState LOADED;
    public static final NetworkState LOADING;
    public static final NetworkState FAIL;

    public NetworkState(Status status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    static {
        LOADED = new NetworkState(Status.SUCCESS,"Success");
        LOADING = new NetworkState(Status.RUNNING,"Running");
        FAIL = new NetworkState(Status.FAILED, "Network exception");
    }

    public Status getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }
}