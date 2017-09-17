package com.readinsite.ranchlife.service.weather.Interface;


import com.readinsite.ranchlife.service.weather.Model.Weather;

/**
 * @Created by Dima on 06/05/17.
 */
public interface HttpCallback {

    public void onSuccess(Weather response);

    public void onFailure (String error);

}