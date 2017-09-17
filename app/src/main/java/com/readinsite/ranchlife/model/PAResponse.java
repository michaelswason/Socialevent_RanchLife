package com.readinsite.ranchlife.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PAResponse implements Serializable{
    @SerializedName("data")
    public List<OPA> opaList;

    @SerializedName("success")
    public int isSuccess;

    @SerializedName("message")
    public String message;

    public PAResponse(){
        opaList = new ArrayList<OPA>();
        message = "";
        isSuccess = 0;
    }
}
