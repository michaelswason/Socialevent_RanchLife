package com.readinsite.ranchlife.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class OUser implements Serializable {
    @SerializedName("userid")
    public int id;

    @SerializedName("email")
    public String email;

    @SerializedName("usertype")
    public String userType;

    @SerializedName("firstname")
    public String firstName;

    @SerializedName("lastname")
    public String lastName;

    @SerializedName("phone")
    public String phone;

    @SerializedName("userpicture")
    public String picture;

    @SerializedName("searchable")
    public boolean searchable;

    public OUser(){
        email = "";
        userType = "";
        firstName = "";
        lastName = "";
        phone = "";
        picture = "";
        searchable = false;
    }
}
