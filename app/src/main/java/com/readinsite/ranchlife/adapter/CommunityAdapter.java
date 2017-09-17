package com.readinsite.ranchlife.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.readinsite.ranchlife.R;
import com.readinsite.ranchlife.model.OEvent;
import com.readinsite.ranchlife.rest.ApiClient;
import com.readinsite.ranchlife.ui.RoundedImageView;
import com.readinsite.ranchlife.ui.listview.CommunityListItemView;
import com.readinsite.ranchlife.utils.CommonUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Created by Dima on 6/20/17.
 */
public class CommunityAdapter extends BaseAdapter {
    private final Context context;
    private final List<OEvent> eventArray =  new ArrayList<>();
    private Listener mListener;
    LayoutInflater mLayoutInflater;

    public CommunityAdapter(final Context context, Listener listener) {
        mLayoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.context = context;
        this.mListener = listener;
    }

    public interface Listener {
        void onBtnMoreClicked(OEvent event);
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
            convertView = new CommunityListItemView(context);
            holder = new ViewHolder();

            holder.lblName = (TextView)convertView.findViewById(R.id.view_community_item_tv_title);
            holder.lblDay = (TextView)convertView.findViewById(R.id.view_community_item_tv_day);
            holder.lblWeek = (TextView)convertView.findViewById(R.id.view_community_item_tv_week);
            holder.lblMonth = (TextView)convertView.findViewById(R.id.view_community_item_tv_year);
            holder.ivIcon = (RoundedImageView)convertView.findViewById(R.id.view_community_item_iv_icon);
            holder.ivEvent = (ImageView)convertView.findViewById(R.id.view_community_item_iv_event);
            holder.btnMore = (Button)convertView.findViewById(R.id.view_community_item_btn_more);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        final OEvent event = getItem(position);
        holder.lblName.setText(event.name);

        Date date = CommonUtils.parseStringToDate(event.eventDate);

        holder.lblDay.setText(CommonUtils.getDayFromDate(date));
        holder.lblWeek.setText(CommonUtils.getDayOfWeekFromDate(date));
        holder.lblMonth.setText(String.format("%s %s", CommonUtils.getMonthStringFromDate(date), CommonUtils.getYearFromDate(date)));

        Picasso.with(context).load(String.format("%s%s", ApiClient.BASE_IMAGE_URL, event.iconPath)).into(holder.ivIcon);
        Picasso.with(context).load(String.format("%s%s", ApiClient.BASE_IMAGE_URL, event.imagePath)).placeholder(R.drawable.placeholder).into(holder.ivEvent);

        holder.btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onBtnMoreClicked(event);
            }
        });

        return convertView;
    }

    private static class ViewHolder {
        private TextView lblName;
        private TextView lblDay;
        private TextView lblWeek;
        private TextView lblMonth;
        private RoundedImageView ivIcon;
        private ImageView ivEvent;
        private Button btnMore;
    }
}
