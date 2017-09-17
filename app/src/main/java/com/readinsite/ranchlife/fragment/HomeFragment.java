package com.readinsite.ranchlife.fragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.readinsite.ranchlife.R;
import com.readinsite.ranchlife.activity.MainActivity;
import com.readinsite.ranchlife.app.UserPreferences;
import com.readinsite.ranchlife.service.TrackerService;
import com.readinsite.ranchlife.service.weather.Model.WeatherData;
import com.readinsite.ranchlife.service.weather.Service.APIManager;

import java.util.Calendar;
import java.util.Locale;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * @Created by Dima on 06/02/17.
 */

public class HomeFragment extends BaseFragment implements View.OnClickListener {
    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    private TextView tvGreeting, tvName, tvTemp, tvTempIcon;
    private final static String PATH_TO_WEATHER_FONT = "fonts/weather.ttf";
    private UserPreferences preferences = UserPreferences.getInstance();
    private final BroadcastReceiver updateLocationBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Location location = intent.getParcelableExtra(TrackerService.EXTRA_LOCATION);

            Log.e("dima", "location : " + location);

            getRequestWeather(location);
        }
    };

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvGreeting = (TextView)view.findViewById(R.id.fragment_home_tv_greeting);
        tvName = (TextView)view.findViewById(R.id.fragment_home_tv_name);
        tvTemp = (TextView)view.findViewById(R.id.fragment_home_tv_temp);
        tvTempIcon = (TextView)view.findViewById(R.id.fragment_home_tv_icon_temp);
        view.findViewById(R.id.fragment_home_btn_hap_today).setOnClickListener(this);
        view.findViewById(R.id.fragment_home_btn_feed).setOnClickListener(this);
        view.findViewById(R.id.fragment_home_btn_pa).setOnClickListener(this);
        view.findViewById(R.id.fragment_home_btn_community).setOnClickListener(this);

        Typeface weatherFont = Typeface.createFromAsset(getActivity().getAssets(), PATH_TO_WEATHER_FONT);
        tvTempIcon.setTypeface(weatherFont);
    }

    @Override
    public void onClick(final View v) {
        switch ( v.getId() ) {
            case R.id.fragment_home_btn_hap_today:
                goToSelectedFragment(R.id.main_navigation_item_community, true);
                break;
            case R.id.fragment_home_btn_feed:
                goToSelectedFragment(R.id.main_navigation_item_feed, false);
                break;
            case R.id.fragment_home_btn_pa:
                goToSelectedFragment(R.id.main_navigation_item_pa, false);
                break;
            case R.id.fragment_home_btn_community:
                goToSelectedFragment(R.id.main_navigation_item_community, false);
                break;

            default:
                throw new RuntimeException("Unhandled action");
        }
    }

    private void goToSelectedFragment(int menuid, boolean isToday) {
        try {
            MainActivity activity = (MainActivity) getActivity();
            activity.navigateMenu(menuid, isToday);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @SuppressLint("SetTextI18n")
    @Override
    public void onStart() {
        super.onStart();

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

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(updateLocationBroadcastReceiver,
                new IntentFilter(TrackerService.ACTION_UPDATE_LOCATION));

        if (TrackerService.getLocation() != null) {
            getRequestWeather(TrackerService.getLocation());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(updateLocationBroadcastReceiver);
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

    private Callback<WeatherData> callback = new Callback<WeatherData>() {
        @SuppressLint("DefaultLocale")
        @Override
        public void success (WeatherData response, Response response2) {
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
}
