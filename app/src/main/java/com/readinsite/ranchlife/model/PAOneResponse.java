package com.readinsite.ranchlife.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PAOneResponse implements Serializable{
    @SerializedName("data")
    public OPA oPA;

    @SerializedName("success")
    public int isSuccess;

    @SerializedName("message")
    public String message;

    public PAOneResponse(){
        oPA = new OPA();
        message = "";
        isSuccess = 0;
    }
}
