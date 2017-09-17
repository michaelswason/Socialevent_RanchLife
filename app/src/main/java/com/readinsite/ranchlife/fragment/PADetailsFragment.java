package com.readinsite.ranchlife.fragment;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.readinsite.ranchlife.R;
import com.readinsite.ranchlife.activity.MainActivity;
import com.readinsite.ranchlife.adapter.CommunityAdapter;
import com.readinsite.ranchlife.adapter.SlideImageViewAdapter;
import com.readinsite.ranchlife.model.CommonResponse;
import com.readinsite.ranchlife.model.EventResponse;
import com.readinsite.ranchlife.model.OEvent;
import com.readinsite.ranchlife.model.OPA;
import com.readinsite.ranchlife.rest.ApiClient;
import com.readinsite.ranchlife.rest.ApiInterface;
import com.readinsite.ranchlife.utils.CommonUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.readinsite.ranchlife.fragment.EventDetailsFragment.REQUEST_CODE_EVENT_DETAILS;

/**
 * @Created by Dima on 6/2/17.
 */

public class PADetailsFragment extends BaseFragment implements View.OnClickListener, CommunityAdapter.Listener, OnMapReadyCallback {
    public static final int REQUEST_CODE_PA_DETAILS = 1;
    private ViewPager viewPager;
    public static OPA oPA;
    private SlideImageViewAdapter adapter;
    private TextView tvName, tvTime, tvWeek, tvDesc;
    private LinearLayout layerTitle;
    private LinearLayout layerAction;
    private Button btnExpand;
    private TextView btnReserve;
    public Timer timer;
    public int page = 0;
    public List<String> imageUrls;
    private boolean isExpand;
    private float titleExpandWidth, titleCollpaseWidth;
    private static final int DURATION = 400;
    private ListView lvEvent;
    private CommunityAdapter eventAdapter;
    List<OEvent> eventArry = new ArrayList<>();

    public static PADetailsFragment newInstance(OPA opa) {
        PADetailsFragment fragment = new PADetailsFragment();
        oPA = opa;

        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pa_details, container, false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        layerTitle = (LinearLayout)view.findViewById(R.id.fragment_pa_details_layer_title);
        layerAction = (LinearLayout)view.findViewById(R.id.fragment_pa_details_layer_actions);
        tvName = (TextView)view.findViewById(R.id.fragment_pa_details_tv_name);
        btnExpand = (Button)view.findViewById(R.id.fragment_pa_details_btn_expand);
        btnExpand.setOnClickListener(this);
        btnReserve = (TextView)view.findViewById(R.id.fragment_pa_details_btn_reserve);
        btnReserve.setOnClickListener(this);
        view.findViewById(R.id.fragment_pa_details_btn_direction).setOnClickListener(this);
        viewPager = (ViewPager)view.findViewById(R.id.fragment_pa_details_viewpager);
        adapter = new SlideImageViewAdapter(getActivity());
        viewPager.setAdapter(adapter);

        lvEvent = (ListView)view.findViewById(R.id.fragment_pa_details_listview);
        LayoutInflater myinflater = getActivity().getLayoutInflater();
        ViewGroup myHeader = (ViewGroup)myinflater.inflate(R.layout.header_pa_details, null);
        lvEvent.addHeaderView(myHeader);

        tvTime = (TextView)myHeader.findViewById(R.id.header_pa_details_tv_time);
        tvWeek = (TextView)myHeader.findViewById(R.id.header_pa_details_tv_week);
        tvDesc = (TextView)myHeader.findViewById(R.id.header_pa_details_tv_details);
        addMapFragment(R.id.header_pa_details_fragment_map);

        eventAdapter = new CommunityAdapter(getActivity(), this);
        lvEvent.setAdapter(eventAdapter);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;

        titleExpandWidth = CommonUtils.pxFromDp(getActivity(), 165) + (width - CommonUtils.pxFromDp(getActivity(), 165))/2;

        titleCollpaseWidth = (width - CommonUtils.pxFromDp(getActivity(), 165))/4;

        RelativeLayout.LayoutParams mLayoutParams2=(RelativeLayout.LayoutParams)layerTitle.getLayoutParams();
        mLayoutParams2.width = (int)titleExpandWidth;
        mLayoutParams2.addRule(RelativeLayout.LEFT_OF,0);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onStart() {
        super.onStart();
        setTitle("");

        getActivity().invalidateOptionsMenu();

        if (oPA == null)
            return;

        tvName.setText(oPA.title);
        tvDesc.setText(oPA.details);

        imageUrls = new ArrayList<>();

        if (oPA.slideImages != null && !oPA.slideImages.isEmpty()) {
            imageUrls = Arrays.asList(oPA.slideImages.split(","));
        }

        if (imageUrls.size() == 0) {
            imageUrls.add(oPA.picturePath);
        }

        if (imageUrls.size() > 0) {
            adapter.setItems(imageUrls);
        }

        if (!oPA.fromdate.equals("") && !oPA.todate.equals("")) {
            String startTime = OPA.convert12HoursFormat(oPA.fromdate);
            String endTime = OPA.convert12HoursFormat(oPA.todate);
            tvTime.setText(String.format("%s ~ %s", startTime, endTime));
        }

        String dateStr = "";

        if (!oPA.weekly.isEmpty()) {
            String[] items = oPA.weekly.split(",");
            if (items.length == 7) {
                dateStr = "All Days of the Week";
            } else if (items.length > 0) {
                for (String dayweek : items) {
                    if (dayweek.equals("su"))
                        dateStr = String.format("%sSunday  ", dateStr);
                    if (dayweek.equals("mo"))
                        dateStr = String.format("%sMonday  ", dateStr);
                    if (dayweek.equals("tu"))
                        dateStr = String.format("%sTuesday  ", dateStr);
                    if (dayweek.equals("we"))
                        dateStr = String.format("%sWednesday  ", dateStr);
                    if (dayweek.equals("th"))
                        dateStr = String.format("%sThursday  ", dateStr);
                    if (dayweek.equals("fr"))
                        dateStr = String.format("%sFriday  ", dateStr);
                    if (dayweek.equals("sa"))
                        dateStr = String.format("%sSaturday  ", dateStr);
                }
            }
        }

        tvWeek.setText(dateStr);

        if (oPA.email != null && !oPA.email.isEmpty()) {
            btnReserve.setEnabled(true);
        } else {
            btnReserve.setEnabled(false);
        }

        pageSwitcher(3);
        getEvents();
    }

    protected void addMapFragment(@IdRes int fragmentContainerId) {
        String tag = SupportMapFragment.class.getName();
        SupportMapFragment fragment = (SupportMapFragment)getChildFragmentManager()
                .findFragmentByTag(tag);
        if ( null == fragment ) {
            fragment = SupportMapFragment.newInstance();
            getChildFragmentManager().beginTransaction()
                    .add(fragmentContainerId, fragment, tag)
                    .commit();
        }

        fragment.getMapAsync(this);
    }

    public void pageSwitcher(int seconds) {
        timer = new Timer(); // At this line a new Thread will be created
        timer.scheduleAtFixedRate(new RemindTask(), 0, seconds * 1000); // delay
        // in
        // milliseconds
    }

    private void getEvents() {
        final MainActivity parentActivity = (MainActivity)getActivity();
        parentActivity.startIndicator();

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<EventResponse> call = apiService.getEventNearByPA(oPA.paId);
        call.enqueue(new Callback<EventResponse>() {
            @Override
            public void onResponse(Call<EventResponse> call, Response<EventResponse> response) {
                parentActivity.stopIndicator();

                if (!response.isSuccessful()){
                    return;
                }

                if (response.body().isSuccess == 0) {
                    return;
                }

                if (eventArry.size() > 0) {
                    eventArry.clear();
                }

                for (OEvent event : response.body().events) {
                    eventArry.add(event);
                    if (eventArry.size() >= 3)
                        break;
                }

                eventAdapter.setEvents(eventArry);
            }

            @Override
            public void onFailure(Call<EventResponse> call, Throwable t) {
                parentActivity.stopIndicator();
            }
        });
    }


    @Override
    public void onClick(final View v) {
        switch ( v.getId() ) {
            case R.id.fragment_pa_details_btn_expand:
                isExpand = !isExpand;
                if (isExpand) {
                    expandTitle(layerTitle);
                    btnExpand.setBackgroundResource(R.drawable.close);
                } else {
                    collapseTitle(layerTitle);
                    btnExpand.setBackgroundResource(R.drawable.plus);
                }
                break;
            case R.id.fragment_pa_details_btn_reserve:
                reservePA();
                break;
            case R.id.fragment_pa_details_btn_direction:
                openGoogleMap();
                break;
            default:
                throw new RuntimeException("Unhandled action");
        }
    }

    private void reservePA() {
        final MainActivity parentActivity = (MainActivity)getActivity();
        parentActivity.startIndicator();

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<CommonResponse> call = apiService.reservePA(oPA.paId);
        call.enqueue(new Callback<CommonResponse>() {
            @Override
            public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
                parentActivity.stopIndicator();

                if (!response.isSuccessful()){
                    parentActivity.onConnectionFailed(getActivity());
                    return;
                }

                if (response.body().isSuccess == 0) {
                    parentActivity.showAlertDialog(getActivity(), "Error", response.body().message);
                    return;
                }

                btnReserve.setEnabled(false);
            }

            @Override
            public void onFailure(Call<CommonResponse> call, Throwable t) {
                parentActivity.onNetworkFailed(getActivity());
                parentActivity.stopIndicator();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    @Override
    public void onPause() {
        super.onPause();
        timer.cancel();
    }

    private void openGoogleMap() {
        String uri = String.format(Locale.ENGLISH, "geo:%s,%s", oPA.latitude, oPA.longitude);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        getActivity().startActivity(intent);
    }

    public void expandTitle(final View v) {
        ValueAnimator va = ValueAnimator.ofInt((int)titleExpandWidth, (int)titleCollpaseWidth);
        va.setDuration(DURATION);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                v.getLayoutParams().width = value.intValue();
                v.requestLayout();
            }
        });
        va.start();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                layerAction.setVisibility(View.VISIBLE);
            }
        }, DURATION);
    }

    public void collapseTitle(final View v) {
        layerAction.setVisibility(View.INVISIBLE);
        ValueAnimator va = ValueAnimator.ofInt((int)titleCollpaseWidth, (int)titleExpandWidth);
        va.setDuration(DURATION);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                v.getLayoutParams().width = value.intValue();
                v.requestLayout();
            }
        });
        va.start();
    }

    @Override
    public void onBtnMoreClicked(OEvent event) {
        final Fragment fragment = EventDetailsFragment.newInstance(event);
        fragment.setTargetFragment(this, REQUEST_CODE_EVENT_DETAILS);
        post(new Runnable() {
            @Override
            public void run() {
                pushFragment(fragment);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (oPA == null) return;

        LatLng loc = new LatLng(Double.parseDouble(oPA.latitude), Double.parseDouble(oPA.longitude));
        MarkerOptions options = new MarkerOptions()
                .position(loc)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));


        googleMap.addMarker(options);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 15.0f));
    }

    private class RemindTask extends TimerTask {

        @Override
        public void run() {

            // As the TimerTask run on a seprate thread from UI thread we have
            // to call runOnUiThread to do work on UI thread.
            getActivity().runOnUiThread(new Runnable() {
                public void run() {

                    if (page >= imageUrls.size()) { // In my case the number of pages are 5
                        page = 0;
                        viewPager.setCurrentItem(page, false);

                    } else {
                        viewPager.setCurrentItem(page++);
                    }
                }
            });

        }
    }

}
