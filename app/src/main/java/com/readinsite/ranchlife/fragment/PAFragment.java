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

import com.google.android.gms.maps.model.Circle;
import com.readinsite.ranchlife.R;
import com.readinsite.ranchlife.activity.MainActivity;
import com.readinsite.ranchlife.adapter.PAAdapter;
import com.readinsite.ranchlife.model.EventResponse;
import com.readinsite.ranchlife.model.OEvent;
import com.readinsite.ranchlife.model.OPA;
import com.readinsite.ranchlife.model.PAResponse;
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
import static com.readinsite.ranchlife.fragment.PADetailsFragment.REQUEST_CODE_PA_DETAILS;

/**
 * @Created by Dima on 6/2/17.
 */

public class PAFragment extends BaseFragment implements PAAdapter.Listener {
    private CustomListView lvPA;
    private CircleRefreshLayout refreshLayout;
    private boolean isPulling = false;
    List<OPA>listPA = new ArrayList<>();
    PAAdapter paAdapter;

    public static PAFragment newInstance() {
        return new PAFragment();
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pa, container, false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        refreshLayout = (CircleRefreshLayout)view.findViewById(R.id.fragment_pa_refresh_layout);
        lvPA = (CustomListView)view.findViewById(R.id.fragment_pa_listview);

        paAdapter = new PAAdapter(getActivity(), this);
        lvPA.setAdapter(paAdapter);

        refreshLayout.setOnRefreshListener(new CircleRefreshLayout.OnCircleRefreshListener() {
            @Override
            public void completeRefresh() {
                isPulling = false;
            }

            @Override
            public void refreshing() {
                isPulling = true;
                retrievePAs();
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
        setTitle(R.string.parks_amp_amenities);

        retrievePAs();
    }

//    @Override
//    public void onClick(final View v) {
//        switch ( v.getId() ) {
//            case R.id.fragment_login_btn_crew:
//                isClientlogin = false;
//                showBtnView(false);
//                break;
//            case R.id.fragment_login_btn_client:
//                isClientlogin = true;
//                showBtnView(false);
//                break;
//            case R.id.fragment_login_btn_back:
//                isClientlogin = false;
//                showBtnView(true);
//                break;
//            case R.id.fragment_login_btn_login:
//                onLoginClick();
//                break;
//            case R.id.fragment_login_btn_office_365:
//                onOffice365Click();
//                break;
//            default:
//                throw new RuntimeException("Unhandled action");
//        }
//    }

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

    @Override
    public void onPause() {
        super.onPause();
    }

    private void retrievePAs() {
        final MainActivity parentActivity = (MainActivity)getActivity();
        parentActivity.startIndicator();

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Date date = new Date();
        SimpleDateFormat dateFormatWithZone = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = dateFormatWithZone.format(date);

        String endDate = "";

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        int curHr = calendar.get(Calendar.HOUR);
        int curMin = calendar.get(Calendar.MINUTE);
        String timeStr = String.format("%d:%d", curHr, curMin);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == 7) {
            dayOfWeek = 0;
        }

        Call<PAResponse> call = apiService.searchPAByTime(timeStr, dayOfWeek);
        call.enqueue(new Callback<PAResponse>() {
            @Override
            public void onResponse(Call<PAResponse> call, Response<PAResponse> response) {
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

                if (listPA.size() > 0) {
                    listPA.clear();
                }

                listPA = response.body().opaList;
                Collections.sort(listPA, new Comparator<OPA>() {
                    public int compare(OPA pa1, OPA pa2) {
                        if (OPA.isPAforToday(pa1) && OPA.isPAforToday(pa2)) {
                            if (OPA.isEalierThanNow(pa1.fromdate) && OPA.isEalierThanNow(pa1.todate)) {
                                return -1;
                            } else if (OPA.isEalierThanNow(pa2.fromdate) && !OPA.isEalierThanNow(pa2.todate)) {
                                return 1;
                            }
                        } else if (OPA.isPAforToday(pa1) && !OPA.isPAforToday(pa2)) {
                            return -1;
                        } else if (!OPA.isPAforToday(pa1) && OPA.isPAforToday(pa2)) {
                            return 1;
                        }

                        return 0;

                    }
                });

                if (isPulling) {
                    refreshLayout.finishRefreshing();
                }

                paAdapter.setPAs(listPA);
            }

            @Override
            public void onFailure(Call<PAResponse> call, Throwable t) {
                parentActivity.stopIndicator();

                parentActivity.onNetworkFailed(getActivity());
                if (isPulling) {
                    refreshLayout.finishRefreshing();
                }
            }
        });
    }

    @Override
    public void onBtnMoreClicked(OPA opa) {
        final Fragment fragment = PADetailsFragment.newInstance(opa);
        fragment.setTargetFragment(this, REQUEST_CODE_PA_DETAILS);
        post(new Runnable() {
            @Override
            public void run() {
                pushFragment(fragment);
            }
        });
    }
}
