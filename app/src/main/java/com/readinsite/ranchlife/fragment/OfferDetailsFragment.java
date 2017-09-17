package com.readinsite.ranchlife.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.readinsite.ranchlife.adapter.SlideImageViewAdapter;
import com.readinsite.ranchlife.app.UserPreferences;
import com.readinsite.ranchlife.model.CommonResponse;
import com.readinsite.ranchlife.model.OEvent;
import com.readinsite.ranchlife.rest.ApiClient;
import com.readinsite.ranchlife.rest.ApiInterface;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @Created by Dima on 6/25/17.
 */

public class OfferDetailsFragment extends BaseFragment implements View.OnClickListener, OnMapReadyCallback {
    public static final int REQUEST_CODE_OFFER_DETAILS = 1;
    private UserPreferences preferences = UserPreferences.getInstance();
    private ViewPager viewPager;
    public static OEvent oEvent;
    private SlideImageViewAdapter adapter;
    private TextView tvName, tvTitle, tvDesc;
    private TextView btnOffer;
    public Timer timer;
    public int page = 0;
    public List<String> imageUrls;


    public static OfferDetailsFragment newInstance(OEvent event) {
        OfferDetailsFragment fragment = new OfferDetailsFragment();
        oEvent = event;

        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_offer_details, container, false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvName = (TextView)view.findViewById(R.id.fragment_offer_details_tv_name);
        tvTitle = (TextView)view.findViewById(R.id.fragment_offer_details_tv_title);
        tvDesc = (TextView)view.findViewById(R.id.fragment_offer_details_tv_desc);

        btnOffer = (TextView) view.findViewById(R.id.fragment_offer_details_btn_offer);
        btnOffer.setOnClickListener(this);

        viewPager = (ViewPager)view.findViewById(R.id.fragment_offer_details_viewpager);
        adapter = new SlideImageViewAdapter(getActivity());
        addMapFragment(R.id.fragment_offer_details_fragment_map);
        viewPager.setAdapter(adapter);
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

    @SuppressLint("DefaultLocale")
    @Override
    public void onStart() {
        super.onStart();
        setTitle("");

        getActivity().invalidateOptionsMenu();

        if (oEvent == null)
            return;

        tvName.setText(oEvent.name);
        tvTitle.setText(oEvent.title);
        tvDesc.setText(oEvent.details);

        imageUrls = new ArrayList<>();

        if (oEvent.slideImages != null && !oEvent.slideImages.isEmpty()) {
            imageUrls = Arrays.asList(oEvent.slideImages.split(","));
        }

        if (imageUrls.size() == 0) {
            imageUrls.add(oEvent.imagePath);
        }

        if (imageUrls.size() > 0) {
            adapter.setItems(imageUrls);
        }

        pageSwitcher(3);
    }

    public void pageSwitcher(int seconds) {
        timer = new Timer(); // At this line a new Thread will be created
        timer.scheduleAtFixedRate(new RemindTask(), 0, seconds * 1000); // delay
        // in
        // milliseconds
    }


    @Override
    public void onClick(final View v) {
        switch ( v.getId() ) {
            case R.id.fragment_offer_details_btn_offer:
                offerEvent();
                break;
            default:
                throw new RuntimeException("Unhandled action");
        }
    }

    private void offerEvent() {
        final MainActivity parentActivity = (MainActivity)getActivity();
        parentActivity.startIndicator();

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<CommonResponse> call = apiService.claimeOffer(String.valueOf(oEvent.eventId), preferences.getEmail());
        call.enqueue(new Callback<CommonResponse>() {
            @Override
            public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
                parentActivity.stopIndicator();

                if (!response.isSuccessful()){
                    parentActivity.onConnectionFailed(getActivity());
                    return;
                }

                if (response.body().isSuccess == 0) {
                    parentActivity.showAlertDialog(getActivity(), "Failed Claim Offer", null);
                    return;
                }

                btnOffer.setEnabled(false);
                parentActivity.showAlertDialog(getActivity(), "Offer Claimed", null);

            }

            @Override
            public void onFailure(Call<CommonResponse> call, Throwable t) {
                parentActivity.stopIndicator();
                parentActivity.onNetworkFailed(getActivity());
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (oEvent == null) return;

        LatLng loc = new LatLng(Double.parseDouble(oEvent.latitude), Double.parseDouble(oEvent.longitude));
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
