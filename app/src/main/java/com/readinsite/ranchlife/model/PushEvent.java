package com.readinsite.ranchlife.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmObject;


public class PushEvent implements Serializable {
    @SerializedName("eventinfo")
    public OEvent eventInfo;
    @SerializedName("email")
    public String email;

    public String firstname;

    public String lastname;

    public PushEvent() {
        eventInfo = new OEvent();
        email = "";
        firstname = "";
        lastname = "";
    }
}
