package com.readinsite.ranchlife.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PushEventResponse implements Serializable{
    @SerializedName("data")
    public List<PushEvent> events;

    @SerializedName("success")
    public int isSuccess;

    @SerializedName("message")
    public String message;

    public PushEventResponse(){
        events = new ArrayList<PushEvent>();
        message = "";
        isSuccess = 0;
    }
}
