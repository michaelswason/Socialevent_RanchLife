package com.readinsite.ranchlife.fragment;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CalendarContract;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.readinsite.ranchlife.R;
import com.readinsite.ranchlife.activity.MainActivity;
import com.readinsite.ranchlife.adapter.SlideImageViewAdapter;
import com.readinsite.ranchlife.app.UserPreferences;
import com.readinsite.ranchlife.model.CommonResponse;
import com.readinsite.ranchlife.model.EventResponse;
import com.readinsite.ranchlife.model.OEvent;
import com.readinsite.ranchlife.model.OPA;
import com.readinsite.ranchlife.model.PAOneResponse;
import com.readinsite.ranchlife.rest.ApiClient;
import com.readinsite.ranchlife.rest.ApiInterface;
import com.readinsite.ranchlife.ui.RoundedImageView;
import com.readinsite.ranchlife.utils.CommonUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.readinsite.ranchlife.fragment.PADetailsFragment.REQUEST_CODE_PA_DETAILS;

/**
 * @Created by Dima on 6/2/17.
 */

public class EventDetailsFragment extends BaseFragment implements View.OnClickListener {
    public static final int REQUEST_CODE_EVENT_DETAILS = 1;
    private UserPreferences preferences = UserPreferences.getInstance();
    private ViewPager viewPager;
    public static OEvent oEvent;
    private SlideImageViewAdapter adapter;
    private TextView tvName, tvTitle, tvDesc, tvReserveState, tvDate, tvPa;
    private LinearLayout layerBgAction;
    private LinearLayout layerTitle;
    private LinearLayout layerAction;
    private Button btnExpand;
    private TextView btnReserve;
    private MenuItem menuSave, menuShare;
    private RoundedImageView ivIcon;
    public Timer timer;
    public int page = 0;
    public List<String> imageUrls;
    private boolean isExpand, isSaved, isReserved, reservable, isPayment;
    private int currReserveCount, maxReserveCount;
    private float titleExpandWidth, titleCollpaseWidth;
    private static final int DURATION = 400;
    private OPA oPa;
    private String reserveText, browserUrl, endDate;


    public static EventDetailsFragment newInstance(OEvent event) {
        EventDetailsFragment fragment = new EventDetailsFragment();
        oEvent = event;

        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_event_details, container, false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        layerBgAction = (LinearLayout)view.findViewById(R.id.fragment_event_details_layer_bg_action);
        layerTitle = (LinearLayout)view.findViewById(R.id.fragment_event_details_layer_title);
        layerAction = (LinearLayout)view.findViewById(R.id.fragment_event_details_layer_actions);
        tvName = (TextView)view.findViewById(R.id.fragment_event_details_tv_name);
        tvTitle = (TextView)view.findViewById(R.id.fragment_event_details_tv_title);
        tvDesc = (TextView)view.findViewById(R.id.fragment_event_details_tv_desc);
        tvReserveState = (TextView)view.findViewById(R.id.fragment_event_details_tv_reservation_state);
        tvDate = (TextView)view.findViewById(R.id.fragment_event_details_tv_date);
        tvPa = (TextView)view.findViewById(R.id.fragment_event_details_tv_pa);
        tvPa.setOnClickListener(this);

        ivIcon = (RoundedImageView)view.findViewById(R.id.fragment_event_details_iv_icon);
        btnExpand = (Button)view.findViewById(R.id.fragment_event_details_btn_expand);
        btnExpand.setOnClickListener(this);
        btnReserve = (TextView)view.findViewById(R.id.fragment_event_details_btn_reserve);
        btnReserve.setOnClickListener(this);
        view.findViewById(R.id.fragment_event_details_btn_direction).setOnClickListener(this);
        view.findViewById(R.id.fragment_event_details_btn_calendar).setOnClickListener(this);
        viewPager = (ViewPager)view.findViewById(R.id.fragment_event_details_viewpager);
        adapter = new SlideImageViewAdapter(getActivity());

        viewPager.setAdapter(adapter);

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

        if (oEvent.iconPath != null && !oEvent.iconPath.isEmpty())
            Picasso.with(getActivity()).load(String.format("%s%s", ApiClient.BASE_IMAGE_URL, oEvent.iconPath)).into(ivIcon);

        pageSwitcher(3);
        getEventDetails();
    }

    public void pageSwitcher(int seconds) {
        timer = new Timer(); // At this line a new Thread will be created
        timer.scheduleAtFixedRate(new RemindTask(), 0, seconds * 1000); // delay
        // in
        // milliseconds
    }

    private void getEventDetails() {
        final MainActivity parentActivity = (MainActivity)getActivity();
        parentActivity.startIndicator();

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<EventResponse> call = apiService.getEventDetails(String.valueOf(oEvent.eventId), preferences.getEmail());
        call.enqueue(new Callback<EventResponse>() {
            @Override
            public void onResponse(Call<EventResponse> call, Response<EventResponse> response) {
                parentActivity.stopIndicator();
                getPA();

                if (!response.isSuccessful()){
                    return;
                }

                if (response.body().isSuccess == 0) {
                    return;
                }

                isSaved = response.body().events.get(0).isSaved.equals("1");
                isReserved = response.body().events.get(0).isReserved.equals("1");
                reservable = response.body().events.get(0).reservation == 1;
                isPayment = response.body().events.get(0).payment == 1;

                maxReserveCount = response.body().events.get(0).maxReservableCount;
                currReserveCount = response.body().events.get(0).currentReservedCount;
                reserveText = response.body().events.get(0).reserveBtnText;
                browserUrl = response.body().events.get(0).browseUrl;
                endDate = response.body().events.get(0).endDate;

                configureUI();
            }

            @Override
            public void onFailure(Call<EventResponse> call, Throwable t) {
                parentActivity.stopIndicator();
                getPA();
            }
        });
    }

    private void getPA() {
        final MainActivity parentActivity = (MainActivity)getActivity();
        parentActivity.startIndicator();

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<PAOneResponse> call = apiService.getPAById(String.valueOf(oEvent.paId));
        call.enqueue(new Callback<PAOneResponse>() {
            @Override
            public void onResponse(Call<PAOneResponse> call, Response<PAOneResponse> response) {
                parentActivity.stopIndicator();

                if (!response.isSuccessful()){
                    return;
                }

                if (response.body().isSuccess == 0) {
                    return;
                }

                oPa = response.body().oPA;
                tvPa.setText(oPa.title);
            }

            @Override
            public void onFailure(Call<PAOneResponse> call, Throwable t) {
                parentActivity.stopIndicator();
            }
        });
    }

    private void configureUI() {
        if (menuSave != null) {
            menuSave.setIcon(isSaved ? R.drawable.ic_heart_fill : R.drawable.ic_heart);
        }

        if ((!reservable || maxReserveCount == 0 || currReserveCount == maxReserveCount) && browserUrl.equals("")){
            btnReserve.setEnabled(false);
        } else {
            btnReserve.setEnabled(true);
        }


        if (isReserved && reservable) {
            isReserved = true;
            btnReserve.setEnabled(true);
        } else {
            isReserved = false;
        }

        if (!browserUrl.equals("") && !reservable) {
            isReserved = false;
            btnReserve.setEnabled(true);
        } else if (browserUrl.equals("")&& reservable) {
            btnReserve.setEnabled(true);
        }


        setReserveButton(isReserved);

        setReservableAlertTitle();

        displayEventDate();
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void setReservableAlertTitle() {
        if (!reservable) {
            tvReserveState.setText("No reservation required");
        } else {
            tvReserveState.setText(String.format("Spots remaining: %d", maxReserveCount - currReserveCount));
        }
    }

    @SuppressLint("DefaultLocale")
    private void displayEventDate()
    {
        try {
            if (oEvent.eventDate != null && !oEvent.eventDate.isEmpty()) {
                Date date = CommonUtils.parseStringToDate(oEvent.eventDate);

                int startHour = Integer.parseInt(CommonUtils.getHourFromDate(date)) - 12;
                String startMin = CommonUtils.getMinuteFromDate(date);
                String startTime = String.format("%d:%s%s", startHour > 0 ? startHour : startHour + 12, startMin, startHour > 0 ? "PM" : "AM");

                String endTime = "11:59PM";

                if (endDate != null && !endDate.isEmpty()) {
                    Date eDate = CommonUtils.parseStringToDate(endDate);

                    int endHour = Integer.parseInt(CommonUtils.getHourFromDate(eDate)) - 12;
                    String endMin = CommonUtils.getMinuteFromDate(eDate);

                    endTime = String.format("%d:%s%s", endHour > 0 ? endHour : endHour + 12, endMin, endHour > 0 ? "PM" : "AM");
                }

                tvDate.setText(String.format("%s %s, %s %s ~ %s", CommonUtils.getMonthStringFromDate(date), CommonUtils.getDayFromDate(date), CommonUtils.getYearFromDate(date), startTime, endTime));
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    private void setReserveButton(boolean reserved) {
        if (reserved) {
            btnReserve.setText("Cancel Reservation");
        } else {
            btnReserve.setText("I'm Interested");

        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_event_details, menu);
        menuSave = menu.findItem(R.id.menu_event_details_save);
        menuShare = menu.findItem(R.id.menu_event_details_share);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch ( item.getItemId() ) {
            case R.id.menu_event_details_save:
                saveEvent();
                break;
            case R.id.menu_event_details_share:
                shareEvent();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onClick(final View v) {
        switch ( v.getId() ) {
            case R.id.fragment_event_details_btn_expand:
                isExpand = !isExpand;
                if (isExpand) {
                    expandBG(layerBgAction);
                    expandTitle(layerTitle);
                    btnExpand.setBackgroundResource(R.drawable.close);
                } else {
                    collapseBG(layerBgAction);
                    collapseTitle(layerTitle);
                    btnExpand.setBackgroundResource(R.drawable.plus);
                }
                break;
            case R.id.fragment_event_details_btn_reserve:
                reserveEvent();
                break;
            case R.id.fragment_event_details_btn_calendar:
                putInCalendar();
                break;
            case R.id.fragment_event_details_btn_direction:
                openGoogleMap();
                break;
            case R.id.fragment_event_details_tv_pa:
                goToPA();
                break;
            default:
                throw new RuntimeException("Unhandled action");
        }
    }

    private void goToPA() {
        if (oPa != null) {
            final Fragment fragment = PADetailsFragment.newInstance(oPa);
            fragment.setTargetFragment(this, REQUEST_CODE_PA_DETAILS);
            post(new Runnable() {
                @Override
                public void run() {
                    pushFragment(fragment);
                }
            });
        }
    }

    private void shareEvent() {
        final MainActivity parentActivity = (MainActivity)getActivity();
        parentActivity.startIndicator();

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<CommonResponse> call = apiService.shareEvent(String.valueOf(oEvent.eventId), preferences.getEmail());
        call.enqueue(new Callback<CommonResponse>() {
            @Override
            public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
                parentActivity.stopIndicator();

                if (!response.isSuccessful()){
                    return;
                }

                if (response.body().isSuccess == 0) {
                    return;
                }

                if (menuShare != null) {
                    menuShare.setIcon(R.drawable.ic_share_fill);
                    menuShare.setEnabled(false);
                }
            }

            @Override
            public void onFailure(Call<CommonResponse> call, Throwable t) {
                parentActivity.stopIndicator();
            }
        });
    }

    private void saveEvent() {
        final MainActivity parentActivity = (MainActivity)getActivity();
        parentActivity.startIndicator();

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<CommonResponse> call = apiService.saveEvent(String.valueOf(oEvent.eventId), preferences.getEmail());
        call.enqueue(new Callback<CommonResponse>() {
            @Override
            public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
                parentActivity.stopIndicator();

                if (!response.isSuccessful()){
                    return;
                }

                if (response.body().isSuccess == 0) {
                    return;
                }

                isSaved = !isSaved;
                if (menuSave != null) {
                    menuSave.setIcon(isSaved ? R.drawable.ic_heart_fill : R.drawable.ic_heart);
                }

            }

            @Override
            public void onFailure(Call<CommonResponse> call, Throwable t) {
                parentActivity.stopIndicator();
            }
        });
    }

    private void reserveEvent() {
        if (!browserUrl.equals("") && !reservable){
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(browserUrl));
            startActivity(browserIntent);
            return;
        }

        if (isReserved) {
            final MainActivity parentActivity = (MainActivity)getActivity();
            parentActivity.startIndicator();

            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

            Call<CommonResponse> call = apiService.unreserveEvent(String.valueOf(oEvent.eventId), preferences.getEmail());
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

                    showAlertDialog("Cancelled", "Your reservation has been cancelled for this event", false);
                    currReserveCount--;
                    setReservableAlertTitle();

                    isReserved = !isReserved;
                    setReserveButton(isReserved);
                }

                @Override
                public void onFailure(Call<CommonResponse> call, Throwable t) {
                    parentActivity.onNetworkFailed(getActivity());
                    parentActivity.stopIndicator();
                }
            });
        } else {
            final MainActivity parentActivity = (MainActivity)getActivity();
            parentActivity.startIndicator();

            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

            Call<CommonResponse> call = apiService.reserveEvent(String.valueOf(oEvent.eventId), preferences.getEmail());
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

                    showAlertDialog("Reserved", "Your spot has been saved for this event", isPayment);
                    currReserveCount++;
                    setReservableAlertTitle();

                    isReserved = !isReserved;
                    setReserveButton(isReserved);
                }

                @Override
                public void onFailure(Call<CommonResponse> call, Throwable t) {
                    parentActivity.stopIndicator();
                    parentActivity.onNetworkFailed(getActivity());
                }
            });
        }
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
        String uri = String.format(Locale.ENGLISH, "geo:%s,%s", oEvent.latitude, oEvent.longitude);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        getActivity().startActivity(intent);
    }

    private void putInCalendar() {
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType("vnd.android.cursor.item/event");


        Date evetnDate = CommonUtils.parseStringToDate(oEvent.eventDate);
        long startTime = evetnDate.getTime();
        long endTime = evetnDate.getTime() + 120 * 60 * 1000;

        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startTime);
        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime);
        intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true);

        intent.putExtra(CalendarContract.Events.TITLE, oEvent.name);
        intent.putExtra(CalendarContract.Events.DESCRIPTION, oEvent.details);
        intent.putExtra(CalendarContract.Events.EVENT_LOCATION, oEvent.location);
        intent.putExtra(CalendarContract.Events.RRULE, "FREQ=YEARLY");

        startActivity(intent);
    }

    public void expandBG(final View v) {
        float targetHeight = CommonUtils.pxFromDp(getActivity(), 137);
        float initalHeight = CommonUtils.pxFromDp(getActivity(), 80);
        ValueAnimator va = ValueAnimator.ofInt((int)initalHeight, (int)targetHeight);
        va.setDuration(DURATION);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                v.getLayoutParams().height = value.intValue();
                v.requestLayout();
            }
        });
        va.start();
    }

    public void collapseBG(final View v) {
        float targetHeight = CommonUtils.pxFromDp(getActivity(), 80);
        float initalHeight = CommonUtils.pxFromDp(getActivity(), 137);
        ValueAnimator va = ValueAnimator.ofInt((int)initalHeight, (int)targetHeight);
        va.setDuration(DURATION);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                v.getLayoutParams().height = value.intValue();
                v.requestLayout();
            }
        });
        va.start();
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

    private void showAlertDialog(String title, String message, final boolean payable) {
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                        if (payable) {
                            payEvent();
                        }
                    }
                });
        alertDialog.show();
    }

    private void payEvent() {
        if (oEvent == null)
            return;

        showAlertDialog("Payment Needed", "Please provide your payment information on the settings page", false);

    }
}
