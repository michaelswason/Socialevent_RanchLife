package com.readinsite.ranchlife.service.weather.Service;


import com.readinsite.ranchlife.service.weather.Model.WeatherData;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.http.GET;
import retrofit.http.Query;


/**
 * @Created by Dima on 06/05/17.
 */
public class APIManager {

    private static Mc2Service mc2Service;
    private static final String URL = "http://api.openweathermap.org/data/2.5";


    public interface Mc2Service {

        @GET("/weather")
        void getWeatherInfo (@Query("lat") String latitude,
                             @Query("lon") String longitude,
                             @Query("cnt") String cnt,
                             @Query("appid") String appid,
                             @Query("units") String units,
                             Callback<WeatherData> cb);
    }

    public static Mc2Service getApiService () {
        RestAdapter restAdapter = new RestAdapter.Builder().
                setEndpoint(URL).build();

        mc2Service = restAdapter.create(Mc2Service.class);

        return mc2Service;


    }

}