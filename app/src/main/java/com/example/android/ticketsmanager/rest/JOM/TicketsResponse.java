package com.example.android.ticketsmanager.rest.JOM;

import com.google.gson.annotations.SerializedName;
import com.google.gson.annotations.Expose;

public class TicketsResponse {

    @SerializedName("_embedded")
    @Expose
    private EventsCollection eventsCollection;

    @SerializedName("page")
    @Expose
    private Page page;

    public EventsCollection getEventsCollection() {
        return eventsCollection;
    }

    public void set_embedded(EventsCollection eventsCollection) {
        this.eventsCollection = eventsCollection;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }
}