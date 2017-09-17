package com.readinsite.ranchlife.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserResponse implements Serializable{
    @SerializedName("data")
    public List<OUser> users;

    @SerializedName("success")
    public int isSuccess;

    @SerializedName("message")
    public String message;

    public UserResponse(){
        users = new ArrayList<OUser>();
        message = "";
        isSuccess = 0;
    }
}
