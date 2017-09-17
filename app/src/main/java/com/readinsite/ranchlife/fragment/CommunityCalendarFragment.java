package com.readinsite.ranchlife.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.readinsite.ranchlife.R;
import com.readinsite.ranchlife.activity.MainActivity;
import com.readinsite.ranchlife.adapter.CommunityAdapter;
import com.readinsite.ranchlife.app.UserPreferences;
import com.readinsite.ranchlife.model.EventResponse;
import com.readinsite.ranchlife.model.OEvent;
import com.readinsite.ranchlife.rest.ApiClient;
import com.readinsite.ranchlife.rest.ApiInterface;
import com.readinsite.ranchlife.ui.CustomListView;
import com.readinsite.ranchlife.ui.refresh.CircleRefreshLayout;
import com.readinsite.ranchlife.utils.CommonUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.readinsite.ranchlife.fragment.EventDetailsFragment.REQUEST_CODE_EVENT_DETAILS;

/**
 * @Created by Dima on 6/2/17.
 */

public class CommunityCalendarFragment  extends BaseFragment implements CommunityAdapter.Listener {
    private CustomListView lvEvent;
    private CircleRefreshLayout refreshLayout;
    private CommunityAdapter communityAdapter;
    List<OEvent> eventArry = new ArrayList<>();
    private boolean isPulling = false;
    UserPreferences preference = UserPreferences.getInstance();
    public static boolean upcomingEvent = false;


    public static CommunityCalendarFragment newInstance() {
        upcomingEvent = false;
        return new CommunityCalendarFragment();
    }

    public static CommunityCalendarFragment newInstance(boolean isToday) {
        upcomingEvent = isToday;

        return new CommunityCalendarFragment();
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_community_calendar, container, false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lvEvent = (CustomListView)view.findViewById(R.id.fragment_community_calendar_listview);
        communityAdapter = new CommunityAdapter(getActivity(), this);
        lvEvent.setAdapter(communityAdapter);

        refreshLayout = (CircleRefreshLayout)view.findViewById(R.id.fragment_community_calendar_refresh_layout);
        refreshLayout.setOnRefreshListener(new CircleRefreshLayout.OnCircleRefreshListener() {
            @Override
            public void completeRefresh() {
                isPulling = false;
            }

            @Override
            public void refreshing() {
                isPulling = true;
                retrieveEvents();
            }

            @Override
            public void startRefresh() {

            }

            @Override
            public void endRefresh() {
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        setTitle(R.string.community_calendar);

        retrieveEvents();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_community, menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch ( item.getItemId() ) {
            case R.id.menu_community_search:
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void retrieveEvents() {
        final MainActivity parentActivity = (MainActivity)getActivity();
        parentActivity.startIndicator();

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Date date = new Date();
        SimpleDateFormat dateFormatWithZone = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = dateFormatWithZone.format(date);

        String endDate = "";
        if (upcomingEvent) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DAY_OF_YEAR, 7);
            Date newDate = calendar.getTime();
            endDate = dateFormatWithZone.format(newDate);
        }

        Call<EventResponse> call = apiService.getCommunityEvents(preference.getEmail(), currentDate, endDate, "");
        call.enqueue(new Callback<EventResponse>() {
            @Override
            public void onResponse(Call<EventResponse> call, Response<EventResponse> response) {
                parentActivity.stopIndicator();

                if (!response.isSuccessful()){

                    parentActivity.onConnectionFailed(getActivity());
                    if (isPulling) {
                        refreshLayout.finishRefreshing();
                    }
                    return;
                }

                if (response.body().isSuccess == 0) {
                    String message = response.body().message;
                    parentActivity.showAlertDialog(getActivity(), getResources().getString(R.string.loggin_alert_title), message);
                    if (isPulling) {
                        refreshLayout.finishRefreshing();
                    }
                    return;
                }

                if (eventArry.size() > 0) {
                    eventArry.clear();
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

                if (isPulling) {
                    refreshLayout.finishRefreshing();
                }

                communityAdapter.setEvents(eventArry);
            }

            @Override
            public void onFailure(Call<EventResponse> call, Throwable t) {
                parentActivity.stopIndicator();

                parentActivity.onNetworkFailed(getActivity());
                if (isPulling) {
                    refreshLayout.finishRefreshing();
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onBtnMoreClicked(OEvent oEvent) {
        final Fragment fragment = EventDetailsFragment.newInstance(oEvent);
        fragment.setTargetFragment(this, REQUEST_CODE_EVENT_DETAILS);
        post(new Runnable() {
            @Override
            public void run() {
                pushFragment(fragment);
            }
        });
    }
}
