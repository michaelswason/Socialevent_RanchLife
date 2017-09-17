package com.readinsite.ranchlife.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.readinsite.ranchlife.R;
import com.readinsite.ranchlife.model.OEvent;
import com.readinsite.ranchlife.rest.ApiClient;
import com.readinsite.ranchlife.ui.listview.FeedListItemView;
import com.readinsite.ranchlife.utils.CommonUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Created by Dima on 6/14/17.
 */
public class FeedAdapter extends BaseAdapter {
    private final Context context;
    private final List<OEvent> eventArray =  new ArrayList<>();
    private Listener mListener;
    LayoutInflater mLayoutInflater;
    private int feedType;

    public FeedAdapter(final Context context, Listener listener) {
        mLayoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.context = context;
        this.mListener = listener;
    }

    public interface Listener {
        void onBtnEditClicked(OEvent event);
    }

    public void setType(int type) {
        feedType = type;
    }

    public void addEvent(@Nullable OEvent event) {
        eventArray.add(event);
        notifyDataSetChanged();
    }

    public void addEvents(@Nullable List<OEvent> events) {
        if ( null != events && !events.isEmpty() ) {
            this.eventArray.addAll(events);
        }
        notifyDataSetChanged();
    }

    public List<OEvent> getEvents() {
        return eventArray;
    }

    public void setEvents(@Nullable List<OEvent> events) {
        this.eventArray.clear();
        addEvents(events);
    }

    @Override
    public int getCount() {
        return eventArray.size();
    }

    @Override
    public OEvent getItem(final int position) {
        return eventArray.get(position);
    }

    @Override
    public long getItemId(final int position) {
        return position;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        ViewHolder holder;
        if ( null == convertView ) {
            convertView = new FeedListItemView(context);
            holder = new ViewHolder();

            holder.container = (FrameLayout)convertView.findViewById(R.id.view_feed_item_container);
            holder.topLayer = (LinearLayout)convertView.findViewById(R.id.view_feed_item_top_layer);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        if (holder.topLayer != null) {
            if (position == 0) {
                holder.topLayer.setVisibility(View.VISIBLE);
            } else {
                holder.topLayer.setVisibility(View.GONE);
            }
        }

        holder.container.removeAllViews();
        final OEvent event = getItem(position);

        if (event.category.equals("regular")) {
            View regularView = mLayoutInflater.inflate(R.layout.view_feed_regular_item, holder.container);

            holder.lblName = (TextView) regularView.findViewById(R.id.view_feed_regular_item_tv_title);
            holder.lblDetails = (TextView) regularView.findViewById(R.id.view_feed_regular_item_tv_details);
            holder.lblDay = (TextView)regularView.findViewById(R.id.view_feed_regular_item_tv_day);
            holder.lblWeek = (TextView)regularView.findViewById(R.id.view_feed_regular_item_tv_week);
            holder.lblMonth = (TextView)regularView.findViewById(R.id.view_feed_regular_item_tv_year);
            holder.ivEvent = (ImageView)regularView.findViewById(R.id.view_feed_regular_item_iv_event);
            holder.btnMore = (Button)regularView.findViewById(R.id.view_feed_regular_item_btn_more);
            holder.startLine = (LinearLayout)regularView.findViewById(R.id.view_feed_regular_item_start_line);
            holder.line = (LinearLayout)regularView.findViewById(R.id.view_feed_regular_item_line);
            holder.lblRecommended = (TextView)regularView.findViewById(R.id.view_feed_regular_item_tv_recommened_name);
            holder.followingLayer = (LinearLayout)regularView.findViewById(R.id.view_feed_regular_item_layer_recommended);
            holder.btnMore = (Button)regularView.findViewById(R.id.view_feed_regular_item_btn_more);
            holder.lblName.setText(event.name);
            holder.lblDetails.setText(event.details);
            Picasso.with(context).load(String.format("%s%s", ApiClient.BASE_IMAGE_URL, event.imagePath)).into(holder.ivEvent);

            if (feedType == 1) {
                holder.followingLayer.setVisibility(View.VISIBLE);
                holder.lblRecommended.setText(event.firstName + " " + event.lastName);
            } else {
                holder.followingLayer.setVisibility(View.GONE);
            }

            if (position == 0) {
                holder.line.setVisibility(View.GONE);
            } else {
                holder.line.setVisibility(View.VISIBLE);
            }

            Date date = CommonUtils.parseStringToDate(event.eventDate);

            holder.lblDay.setText(CommonUtils.getDayFromDate(date));
            holder.lblWeek.setText(CommonUtils.getDayOfWeekFromDate(date));
            holder.lblMonth.setText(String.format("%s %s", CommonUtils.getMonthStringFromDate(date), CommonUtils.getYearFromDate(date)));

        }
        else if (event.category.equals("offer") || event.category.equals("alert")) {
            View regularView = mLayoutInflater.inflate(R.layout.view_feed_offer_item, holder.container);

            holder.lblName = (TextView) regularView.findViewById(R.id.view_feed_offer_item_tv_title);
            holder.lblDetails = (TextView) regularView.findViewById(R.id.view_feed_offer_item_tv_details);
            holder.btnMore = (Button)regularView.findViewById(R.id.view_feed_offer_item_btn_more);
            holder.startLine = (LinearLayout)regularView.findViewById(R.id.view_feed_offer_item_start_line);
            holder.line = (LinearLayout)regularView.findViewById(R.id.view_feed_offer_item_line);
            holder.btnMore = (Button)regularView.findViewById(R.id.view_feed_offer_item_btn_more);

            holder.lblName.setText(event.name);
            holder.lblDetails.setText(event.title);


            if (position == 0) {
                holder.line.setVisibility(View.GONE);
            } else {
                holder.line.setVisibility(View.VISIBLE);
            }
        }

        if (event.category.equals("alert")) {
            holder.btnMore.setVisibility(View.GONE);
        } else {
            holder.btnMore.setVisibility(View.VISIBLE);
        }

        holder.btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!event.category.equals("alert"))
                    mListener.onBtnEditClicked(event);
            }
        });

        return convertView;
    }

    private static class ViewHolder {
        private LinearLayout topLayer;
        private LinearLayout startLine;
        private LinearLayout line;
        private LinearLayout followingLayer;
        private FrameLayout container;
        private TextView lblName;
        private TextView lblDetails;
        private TextView lblDay;
        private TextView lblWeek;
        private TextView lblMonth;
        private TextView lblRecommended;
        private ImageView ivEvent;
        private Button btnMore;
    }
}
