package com.readinsite.ranchlife.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CommonResponse implements Serializable{
    @SerializedName("success")
    public int isSuccess;

    @SerializedName("message")
    public String message;

    public CommonResponse(){
        message = "";
        isSuccess = 0;
    }
}
