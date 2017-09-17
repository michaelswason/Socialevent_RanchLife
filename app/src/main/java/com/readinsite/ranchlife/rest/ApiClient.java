package com.readinsite.ranchlife.rest;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {


    /**
     * backend server url
     */
    public static final String BASE_URL = "http://adm.myranchlife.com/api/";
    public static final String BASE_IMAGE_URL = "http://adm.myranchlife.com/";
    private static Retrofit retrofit = null;

    /**
     * Function to get retrofit instance
     * @return retrofit instance
     */
    public static Retrofit getClient() {
        if (retrofit==null) {

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(5, TimeUnit.SECONDS)
                    .readTimeout(5, TimeUnit.SECONDS).build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }
}
