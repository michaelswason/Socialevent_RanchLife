package com.readinsite.ranchlife.rest;

import com.readinsite.ranchlife.model.CommonResponse;
import com.readinsite.ranchlife.model.EventResponse;
import com.readinsite.ranchlife.model.PAOneResponse;
import com.readinsite.ranchlife.model.PAResponse;
import com.readinsite.ranchlife.model.PushEventResponse;
import com.readinsite.ranchlife.model.UserResponse;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface ApiInterface {
    @GET("index.php?method=login")
    Call<UserResponse> doLogin(@Query("email") String emailAddress, @Query("password") String password, @Query("devicetoken") String deviceId, @Query("devicetype") String deviceType);

    @GET("index.php?method=forgotPassword")
    Call<UserResponse> doForgot(@Query("email") String email);

    @GET("index.php?method=signup")
    Call<UserResponse> doRegister(@Query("firstname") String firstName,@Query("lastname") String lastName, @Query("email") String email, @Query("password") String password, @Query("phone") String phone, @Query("passcode") String passcode, @Query("devicetoken") String deviceId, @Query("devicetype") String deviceType);

    @GET("index.php?method=getSavedEvents")
    Call<EventResponse> getSavedEvents(@Query("email") String email);

    @GET("index.php?method=getLoginEvent")
    Call<PushEventResponse> getLoginEvents(@Query("email") String email);

    @GET("index.php?method=getUnreadEvents")
    Call<PushEventResponse> getUnreadEvents(@Query("email") String email);

    @GET("index.php?method=getCommunityEvents")
    Call<EventResponse> getCommunityEvents(@Query("email") String email, @Query("fromdate") String fromDate, @Query("todate") String toDate, @Query("searchStr") String pattern );

    @GET("index.php?method=getEventDetails")
    Call<EventResponse> getEventDetails(@Query("eventid") String eventId, @Query("email") String email );

    @GET("index.php?method=updateUserlocation")
    Call<UserResponse> updateUserlocation(@Query("email") String email,@Query("latitude") String latitude, @Query("longitude") String longitude, @Query("devicetoken") String devicetoken);

    @GET("index.php?method=getPAById")
    Call<PAOneResponse> getPAById(@Query("paid") String paid);

    @GET("index.php?method=saveEvent")
    Call<CommonResponse> saveEvent(@Query("eventid") String eventid, @Query("email") String email);

    @GET("index.php?method=shareEvent")
    Call<CommonResponse> shareEvent(@Query("eventid") String eventid, @Query("email") String email);
    @GET("index.php?method=reserveEvent")
    Call<CommonResponse> reserveEvent(@Query("eventid") String eventid, @Query("email") String email);
    @GET("index.php?method=unreserveEvent")
    Call<CommonResponse> unreserveEvent(@Query("eventid") String eventid, @Query("email") String email);

    @GET("index.php?method=claimeOffer")
    Call<CommonResponse> claimeOffer(@Query("eventid") String eventid, @Query("email") String email);

    @GET("index.php?method=searchPAByTime")
    Call<PAResponse> searchPAByTime(@Query("time") String time, @Query("weeklynum") int weeknum);

    @GET("index.php?method=getEventNearByPA")
    Call<EventResponse> getEventNearByPA(@Query("paId") long paID);

    @GET("index.php?method=sendPA")
    Call<CommonResponse> reservePA(@Query("paId") long paID);
}
