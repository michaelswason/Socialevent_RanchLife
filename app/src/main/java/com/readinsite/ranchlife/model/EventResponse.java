package com.readinsite.ranchlife.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EventResponse implements Serializable{
    @SerializedName("data")
    public List<OEvent> events;

    @SerializedName("success")
    public int isSuccess;

    @SerializedName("message")
    public String message;

    public EventResponse(){
        events = new ArrayList<OEvent>();
        message = "";
        isSuccess = 0;
    }
}
