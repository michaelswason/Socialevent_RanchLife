package com.readinsite.ranchlife.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmObject;


public class OEvent  extends RealmObject implements Serializable {
    @SerializedName("eventid")
    public long eventId;
    @SerializedName("paid")
    public long paId;
    @SerializedName("triggerid")
    public long triggerId;

    @SerializedName("name")
    public String name;
    @SerializedName("title")
    public String title;
    @SerializedName("subtext")
    public String details;

    @SerializedName("picture")
    public String imagePath;

    @SerializedName("icon")
    public String iconPath;

    @SerializedName("allimages")
    public String slideImages;

    @SerializedName("videourl")
    public String videourl;

    @SerializedName("datetime")
    public String eventDate;

    @SerializedName("maxreservenum")
    public int maxReservableCount;

    @SerializedName("curreservenum")
    public int currentReservedCount;

    @SerializedName("amount")
    public double amount;

    @SerializedName("location")
    public String location;

    @SerializedName("latitude")
    public String latitude;

    @SerializedName("longitude")
    public String longitude;

    @SerializedName("category")
    public String category;

    @SerializedName("reservation")  //boolean
    public int reservation;

    @SerializedName("payment")   //boolean
    public int payment;

    @SerializedName("pushavailable")   //boolean
    public int pushable;

    @SerializedName("claimeoffer")    //boolean
    public int claimeoffer;
    //Following users;
    @SerializedName("reserve")    //boolean
    public String isReserved;
    @SerializedName("save")    //boolean
    public String isSaved;

    @SerializedName("btntext")    //boolean
    public String reserveBtnText;

    @SerializedName("browserurl")    //boolean
    public String browseUrl;

    @SerializedName("closetime")    //boolean
    public String endDate;

    public String followerEmail;
    public String firstName;
    public String lastName;
    public String readDate;

    public OEvent(){
        eventId = 0;
        paId = 0;
        triggerId = 0;
        name = "";
        title = "";
        details = "";
        imagePath = "";
        iconPath = "";
        slideImages = "";
        videourl = "";
        eventDate = "";
        maxReservableCount = 0;
        currentReservedCount = 0;
        location = "";
        latitude = "";
        longitude = "";
        amount = 0.0;
        category = "";
        reservation = 0;
        payment = 0;
        pushable = 0;
        claimeoffer = 0;
        isReserved = "0";
        isSaved = "0";
        reserveBtnText = "";
        browseUrl = "";
        endDate = "";
    }

}
