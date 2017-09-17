package com.readinsite.ranchlife.fragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.readinsite.ranchlife.R;
import com.readinsite.ranchlife.activity.MainActivity;
import com.readinsite.ranchlife.adapter.FeedAdapter;
import com.readinsite.ranchlife.model.EventResponse;
import com.readinsite.ranchlife.model.OEvent;
import com.readinsite.ranchlife.model.PushEvent;
import com.readinsite.ranchlife.model.PushEventResponse;
import com.readinsite.ranchlife.rest.ApiClient;
import com.readinsite.ranchlife.rest.ApiInterface;
import com.readinsite.ranchlife.app.UserPreferences;
import com.readinsite.ranchlife.service.TrackerService;
import com.readinsite.ranchlife.service.weather.Model.WeatherData;
import com.readinsite.ranchlife.service.weather.Service.APIManager;
import com.readinsite.ranchlife.ui.CustomListView;
import com.readinsite.ranchlife.ui.refresh.CircleRefreshLayout;
import com.readinsite.ranchlife.utils.CommonUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import info.hoang8f.android.segmented.SegmentedGroup;
import io.realm.Realm;
import io.realm.RealmResults;
import retrofit.RetrofitError;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.readinsite.ranchlife.fragment.EventDetailsFragment.REQUEST_CODE_EVENT_DETAILS;
import static com.readinsite.ranchlife.fragment.OfferDetailsFragment.REQUEST_CODE_OFFER_DETAILS;

/**
 * @Created by Dima on 6/3/17.
 */

public class MyFeedFragment  extends BaseFragment implements RadioGroup.OnCheckedChangeListener, FeedAdapter.Listener {
    public static MyFeedFragment newInstance() {
        return new MyFeedFragment();
    }

    private List<OEvent> eventArry = new ArrayList<OEvent>();
    public static final int OFFER_ALERT_EXIST_DURATION = 5;

    private UserPreferences preferences = UserPreferences.getInstance();
    private CustomListView lvEvent;
    private FeedAdapter feedAdapter;
    private LinearLayout layerHeader, topbar, layerTop, headerViewLayer;
    private CircleRefreshLayout refreshLayout;
    private TextView tvGreeting, tvName, tvTemp, tvTempIcon;
    private SegmentedGroup segmented;
    private final static String PATH_TO_WEATHER_FONT = "fonts/weather.ttf";
    private Realm realm;
    private final BroadcastReceiver updateLocationBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Location location = intent.getParcelableExtra(TrackerService.EXTRA_LOCATION);

            getRequestWeather(location);
        }
    };

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_feed, container, false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        realm = Realm.getDefaultInstance();

        segmented = (SegmentedGroup) view.findViewById(R.id.fragment_feed_segmented);
        segmented.setOnCheckedChangeListener(this);

        tvGreeting = (TextView)view.findViewById(R.id.fragment_feed_tv_greeting);
        tvName = (TextView)view.findViewById(R.id.fragment_feed_tv_name);
        tvTemp = (TextView)view.findViewById(R.id.fragment_feed_tv_temp);
        tvTempIcon = (TextView)view.findViewById(R.id.fragment_feed_tv_icon_temp);

        Typeface weatherFont = Typeface.createFromAsset(getActivity().getAssets(), PATH_TO_WEATHER_FONT);
        tvTempIcon.setTypeface(weatherFont);

        layerHeader = (LinearLayout)view.findViewById(R.id.fragment_feed_header);
        topbar = (LinearLayout)view.findViewById(R.id.fragment_feed_topbar);
        layerTop = (LinearLayout)view.findViewById(R.id.fragment_feed_layer_top);

        lvEvent = (CustomListView) view.findViewById(R.id.fragment_feed_listview);
        feedAdapter = new FeedAdapter(getActivity(), this);
        lvEvent.setAdapter(feedAdapter);

        RadioButton rBtn = (RadioButton) segmented.findViewById(R.id.fragment_feed_segmented_for_you);
        rBtn.setChecked(true);

        View headerView = getActivity().getLayoutInflater().inflate(R.layout.view_feed_header, null);
        lvEvent.addHeaderView(headerView);
        headerViewLayer = (LinearLayout) headerView.findViewById(R.id.view_feed_header_layer);


        refreshLayout = (CircleRefreshLayout)view.findViewById(R.id.fragment_feed_refresh_layout);
        refreshLayout.setOnRefreshListener(new CircleRefreshLayout.OnCircleRefreshListener() {
            @Override
            public void completeRefresh() {

            }

            @Override
            public void refreshing() {
                readEvents(segmented.getCheckedRadioButtonId());

                Handler mHandler = new Handler(){
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);

                        refreshLayout.finishRefreshing();
                    }
                };
                mHandler.sendEmptyMessageDelayed(0, 3000);
            }

            @Override
            public void startRefresh() {
                changeHeaderHeight(true);

            }

            @Override
            public void endRefresh() {
                changeHeaderHeight(false);
            }
        });

        lvEvent.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {


                int offset = lvEvent.computeVerticalScrollOffset();

                float maxValue = CommonUtils.pxFromDp(getActivity(), 80);
                float offsetPixels = CommonUtils.pxFromDp(getActivity(), offset);

                if (maxValue < offsetPixels) {
                    topbar.setVisibility(View.INVISIBLE);
                    moveLayer(0);
                } else if (offsetPixels >= 0) {
                    topbar.setVisibility(View.VISIBLE);
                    moveLayer((int)(maxValue - offsetPixels));
                } else {
                    topbar.setVisibility(View.VISIBLE);
                }

            }
        });

    }

    private void changeHeaderHeight(boolean isShowed) {
        layerHeader.getLayoutParams().height = isShowed ? (int)CommonUtils.pxFromDp(getActivity(), 170) : 0;
        layerHeader.requestLayout();

        headerViewLayer.getLayoutParams().height = isShowed ? 0 : (int)CommonUtils.pxFromDp(getActivity(), 170);
        headerViewLayer.requestLayout();
    }

    private void moveLayer(int offset) {
        if (offset >= 0) {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) layerTop.getLayoutParams();
            params.setMargins(0, offset, 0, 0);
            layerTop.setLayoutParams(params);
            layerTop.requestLayout();
        }
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onStart() {
        super.onStart();
        setTitle(R.string.my_ranch_life);

        MainActivity parentActivity = (MainActivity)getActivity();
        parentActivity.showLogoTitle(true);

        UserPreferences prefrences = UserPreferences.getInstance();

        tvName.setText(prefrences.getName());
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        if (hour >= 5 && hour < 12) {
            tvGreeting.setText("Good Morning");
        } else if (hour >= 12 && hour < 19) {
            tvGreeting.setText("Good Afternoon");
        } else if ((hour >= 19 && hour <= 24) || (hour >= 0 && hour < 5)) {
            tvGreeting.setText("Good Evening");
        }

        tvTemp.setText(String.format("%d°", prefrences.getTemporary()));

    }

    @Override
    public void onResume() {
        super.onResume();

        getActivity().invalidateOptionsMenu();

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(updateLocationBroadcastReceiver,
                new IntentFilter(TrackerService.ACTION_UPDATE_LOCATION));

        if (TrackerService.getLocation() != null) {
            getRequestWeather(TrackerService.getLocation());
        }

        getUnreadEvents();
    }

    private void getRequestWeather(Location location) {
        String stringLatitude = String.valueOf(location.getLatitude());
        String stringLongitude = String.valueOf(location.getLongitude());

        APIManager.getApiService().getWeatherInfo(stringLatitude,
                stringLongitude,
                "1",
                getResources().getString(R.string.weather_app_id),
                "imperial",
                callback);
    }

    private retrofit.Callback<WeatherData> callback = new retrofit.Callback<WeatherData>() {
        @SuppressLint("DefaultLocale")
        @Override
        public void success (WeatherData response, retrofit.client.Response response2) {
            double tmp = response.getMain().getTemp();
            preferences.setTemporary((int)tmp);
            tvTemp.setText(String.format("%d°", (int)tmp));

            switch (response.getWeather().get(0).getIcon()){
                case "01d":
                    tvTempIcon.setText(R.string.wi_day_sunny);
                    break;
                case "02d":
                    tvTempIcon.setText(R.string.wi_cloudy_gusts);
                    break;
                case "03d":
                    tvTempIcon.setText(R.string.wi_cloud_down);
                    break;
                case "04d":
                    tvTempIcon.setText(R.string.wi_cloudy);
                    break;
                case "04n":
                    tvTempIcon.setText(R.string.wi_night_cloudy);
                    break;
                case "10d":
                    tvTempIcon.setText(R.string.wi_day_rain_mix);
                    break;
                case "11d":
                    tvTempIcon.setText(R.string.wi_day_thunderstorm);
                    break;
                case "13d":
                    tvTempIcon.setText(R.string.wi_day_snow);
                    break;
                case "01n":
                    tvTempIcon.setText(R.string.wi_night_clear);
                    break;
                case "02n":
                    tvTempIcon.setText(R.string.wi_night_cloudy);
                    break;
                case "03n":
                    tvTempIcon.setText(R.string.wi_night_cloudy_gusts);
                    break;
                case "10n":
                    tvTempIcon.setText(R.string.wi_night_cloudy_gusts);
                    break;
                case "11n":
                    tvTempIcon.setText(R.string.wi_night_rain);
                    break;
                case "13n":
                    tvTempIcon.setText(R.string.wi_night_snow);
                    break;
            }


        }

        @Override
        public void failure (RetrofitError error) {
            Log.e("Dima", "failure", error);
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(updateLocationBroadcastReceiver);
    }
    @Override
    public void onDestroy() {

        realm.close();
        super.onDestroy();
    }

    private void readEvents(int checkedId) {
        switch (checkedId) {
            case R.id.fragment_feed_segmented_for_you:
                readForYouEvents();
                break;
            case R.id.fragment_feed_segmented_following:
                readFollowingEvents();
                break;
            case R.id.fragment_feed_segmented_saved:
                retrieveSavedEvents();
                break;

        }
    }
    @Override
    public void onCheckedChanged(RadioGroup radioGroup, @IdRes int checkedId) {
        readEvents(checkedId);
    }

    private void readForYouEvents() {
        if (eventArry.size() > 0) {
            eventArry.clear();
        }

        if (realm == null)
            realm = Realm.getDefaultInstance();

        RealmResults<OEvent> results = realm.where(OEvent.class).findAll();

        Log.e("Dima", "result " + results + "--" + results.size());

        for (OEvent event : results) {
            if (event.followerEmail != null && !event.followerEmail.isEmpty()) {
                continue;
            }

            if (event.eventId <= 0 || event.name == null) {
                continue;
            }

            if (event.readDate != null && !event.readDate.isEmpty()) {
                if (event.category.equals("offer") || event.category.equals("alert")) {
                    long now = System.currentTimeMillis() / 1000;
                    long readDate = Long.parseLong(event.readDate);
                    long interval = now - readDate;

                    if ((interval / 86400) < OFFER_ALERT_EXIST_DURATION) {
                        eventArry.add(event);
                    }
                } else {
                    Date edate = CommonUtils.parseStringToDate(event.eventDate);
                    Date curDate = new Date();
                    if (curDate.compareTo(edate) < 0) {
                        eventArry.add(event);
                    }
                }
            }
        }

        if (eventArry.size() > 0) {
            Collections.sort(eventArry, new Comparator<OEvent>() {
                public int compare(OEvent o1, OEvent o2) {
                    Date date1 = CommonUtils.parseStringToDate(o1.eventDate);
                    Date date2 = CommonUtils.parseStringToDate(o2.eventDate);

                    if (date1 == null || date2 == null)
                        return 0;

                    return date1.compareTo(date2);
                }
            });
        }
        feedAdapter.setType(0);   // type = 0 -- > for you events
        feedAdapter.setEvents(eventArry);
    }

    private void readFollowingEvents() {
        if (eventArry.size() > 0) {
            eventArry.clear();
        }

        if (realm == null)
            realm = Realm.getDefaultInstance();

        RealmResults<OEvent> results = realm.where(OEvent.class).findAll();

        Log.e("Dima", "result " + results + "--" + results.size());

        for (OEvent event : results) {
            if (event.followerEmail == null || event.followerEmail.isEmpty()) {
                continue;
            }

            if (event.eventId <= 0 || event.name == null) {
                continue;
            }

            if (event.readDate != null && !event.readDate.isEmpty()) {
                if (event.category.equals("offer") || event.category.equals("alert")) {
                    long now = System.currentTimeMillis() / 1000;
                    long readDate = Long.parseLong(event.readDate);
                    long interval = now - readDate;

                    if ((interval / 86400) < OFFER_ALERT_EXIST_DURATION) {
                        eventArry.add(event);
                    }
                } else {
                    Date edate = CommonUtils.parseStringToDate(event.eventDate);
                    Date curDate = new Date();
                    if (curDate.compareTo(edate) < 0) {
                        eventArry.add(event);
                    }
                }
            }
        }

        if (eventArry.size() > 0) {
            Collections.sort(eventArry, new Comparator<OEvent>() {
                public int compare(OEvent o1, OEvent o2) {
                    Date date1 = CommonUtils.parseStringToDate(o1.eventDate);
                    Date date2 = CommonUtils.parseStringToDate(o2.eventDate);

                    if (date1 == null || date2 == null)
                        return 0;

                    return date1.compareTo(date2);
                }
            });
        }

        feedAdapter.setType(1);   // type = 0 -- > for you events
        feedAdapter.setEvents(eventArry);
    }


    private void retrieveSavedEvents() {
        if (eventArry.size() > 0) {
            eventArry.clear();
        }

        feedAdapter.setType(2);   // type = 2 -- > saved event
        feedAdapter.setEvents(eventArry);

        final MainActivity parentActivity = (MainActivity)getActivity();
        parentActivity.startIndicator();

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        String email = preferences.getEmail();
        Call<EventResponse> call = apiService.getSavedEvents(email);
        call.enqueue(new Callback<EventResponse>() {
            @Override
            public void onResponse(Call<EventResponse> call, Response<EventResponse> response) {
                parentActivity.stopIndicator();

                if (!response.isSuccessful()){

                    parentActivity.onConnectionFailed(getActivity());

                    return;
                }

                if (response.body().isSuccess == 0) {
                    String message = response.body().message;
                    parentActivity.showAlertDialog(getActivity(), getResources().getString(R.string.loggin_alert_title), message);
                    return;
                }

                eventArry = response.body().events;
                Collections.sort(eventArry, new Comparator<OEvent>() {
                    public int compare(OEvent o1, OEvent o2) {
                        Date date1 = CommonUtils.parseStringToDate(o1.eventDate);
                        Date date2 = CommonUtils.parseStringToDate(o2.eventDate);

                        if (date1 == null || date2 == null)
                            return 0;

                        return date1.compareTo(date2);
                    }
                });

                feedAdapter.setType(2);   // type = 2 -- > saved event
                feedAdapter.setEvents(eventArry);
            }

            @Override
            public void onFailure(Call<EventResponse> call, Throwable t) {
                parentActivity.stopIndicator();

                parentActivity.onNetworkFailed(getActivity());
            }
        });
    }

    @Override
    public void onBtnEditClicked(OEvent event) {
        if (event.category.equals("regular")) {
            final MainActivity parentActivity = (MainActivity)getActivity();
            parentActivity.showLogoTitle(false);
            final Fragment fragment = EventDetailsFragment.newInstance(event);
            fragment.setTargetFragment(this, REQUEST_CODE_EVENT_DETAILS);
            post(new Runnable() {
                @Override
                public void run() {
                    pushFragment(fragment);
                }
            });
        } else if (event.category.equals("offer")) {
            final MainActivity parentActivity = (MainActivity)getActivity();
            parentActivity.showLogoTitle(false);
            final Fragment fragment = OfferDetailsFragment.newInstance(event);
            fragment.setTargetFragment(this, REQUEST_CODE_OFFER_DETAILS);
            post(new Runnable() {
                @Override
                public void run() {
                    pushFragment(fragment);
                }
            });
        }
    }

    private void getUnreadEvents() {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<PushEventResponse> call = apiService.getUnreadEvents(preferences.getEmail());
        call.enqueue(new Callback<PushEventResponse>() {
            @Override
            public void onResponse(Call<PushEventResponse> call, Response<PushEventResponse> response) {

                if (!response.isSuccessful()) {
                    return;
                }

                if (response.body().isSuccess == 0) {
                    return;
                }
                saveEvents(response.body().events);

            }

            @Override
            public void onFailure(Call<PushEventResponse> call, Throwable t) {
            }
        });


    }

    public void saveEvents(final List<PushEvent> lsEvent) {
        if (realm == null)
            realm = Realm.getDefaultInstance();

        try {
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm bgRealm) {
                    for (PushEvent pEvent : lsEvent) {
                        if (!isExistEvent(pEvent, bgRealm)) {
                            OEvent event = pEvent.eventInfo;
                            OEvent oEvent = bgRealm.createObject(OEvent.class);

                            oEvent.eventId = event.eventId;
                            oEvent.name = event.name;
                            oEvent.title = event.title;
                            oEvent.details = event.details;
                            oEvent.latitude = event.latitude;
                            oEvent.longitude = event.longitude;
                            oEvent.triggerId = event.triggerId;
                            oEvent.paId = event.paId;
                            oEvent.iconPath = event.iconPath;
                            oEvent.imagePath = event.imagePath;
                            oEvent.slideImages = event.slideImages;
                            oEvent.videourl = event.videourl;
                            oEvent.maxReservableCount = event.maxReservableCount;
                            oEvent.currentReservedCount = event.currentReservedCount;
                            oEvent.eventDate = event.eventDate;
                            oEvent.reservation = event.reservation;
                            oEvent.payment = event.payment;
                            oEvent.amount = event.amount;
                            oEvent.claimeoffer = event.claimeoffer;
                            oEvent.pushable = event.pushable;
                            oEvent.location = event.location;
                            oEvent.category = event.category;

                            oEvent.followerEmail = pEvent.email;
                            oEvent.firstName = pEvent.firstname;
                            oEvent.lastName = pEvent.lastname;
                            Long tsLong = System.currentTimeMillis()/1000;
                            oEvent.readDate = tsLong.toString();
                        }
                    }
                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    readEvents(segmented.getCheckedRadioButtonId());
                }
            }, new Realm.Transaction.OnError() {
                @Override
                public void onError(Throwable error) {
                }
            });
        } finally {
            //nothing
        }

    }

    private boolean isExistEvent(PushEvent pushEvent, Realm bgRealm) {

        RealmResults<OEvent> lsEvent = bgRealm.where(OEvent.class).equalTo("eventId", pushEvent.eventInfo.eventId).findAll();

        return !(lsEvent == null || lsEvent.size() == 0);
    }

}
