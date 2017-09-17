package com.readinsite.ranchlife.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
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
import com.readinsite.ranchlife.model.OPA;
import com.readinsite.ranchlife.rest.ApiClient;
import com.readinsite.ranchlife.ui.RoundedImageView;
import com.readinsite.ranchlife.ui.listview.CommunityListItemView;
import com.readinsite.ranchlife.ui.listview.PAListItemView;
import com.readinsite.ranchlife.utils.CommonUtils;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @Created by Dima on 6/20/17.
 */
public class PAAdapter extends BaseAdapter {
    private final Context context;
    private final List<OPA> paArray =  new ArrayList<>();
    private Listener mListener;
    LayoutInflater mLayoutInflater;

    public PAAdapter(final Context context, Listener listener) {
        mLayoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.context = context;
        this.mListener = listener;
    }

    public interface Listener {
        void onBtnMoreClicked(OPA oPA);
    }

    public void addPA(@Nullable OPA oPA) {
        paArray.add(oPA);
        notifyDataSetChanged();
    }

    public void addPAs(@Nullable List<OPA> events) {
        if ( null != events && !events.isEmpty() ) {
            this.paArray.addAll(events);
        }
        notifyDataSetChanged();
    }

    public List<OPA> getEvents() {
        return paArray;
    }

    public void setPAs(@Nullable List<OPA> paList) {
        this.paArray.clear();
        addPAs(paList);
    }

    @Override
    public int getCount() {
        return paArray.size();
    }

    @Override
    public OPA getItem(final int position) {
        return paArray.get(position);
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
            convertView = new PAListItemView(context);
            holder = new ViewHolder();

            holder.lblName = (TextView)convertView.findViewById(R.id.view_pa_item_tv_title);
            holder.lblState = (TextView)convertView.findViewById(R.id.view_pa_item_tv_state);
            holder.ivPA = (ImageView)convertView.findViewById(R.id.view_pa_item_iv);
            holder.btnMore = (Button)convertView.findViewById(R.id.view_pa_item_btn_more);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        final OPA opa = getItem(position);

        holder.lblName.setText(opa.title);
        Picasso.with(context).load(String.format("%s%s", ApiClient.BASE_IMAGE_URL, opa.picturePath))
                .placeholder(R.drawable.placeholder)
                .into(holder.ivPA);

        if (OPA.isPAforToday(opa)) {
            if (OPA.isEalierThanNow(opa.fromdate) && !OPA.isEalierThanNow(opa.todate)) {
                holder.lblState.setText("Open Now");
                holder.lblState.setTextColor(Color.parseColor("#cf945e"));
            } else {
                holder.lblState.setText("Closed");
                holder.lblState.setTextColor(Color.parseColor("#cf945e"));
            }
        } else {
            holder.lblState.setText("Closed");
            holder.lblState.setTextColor(Color.parseColor("#cf945e"));
        }

        holder.btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onBtnMoreClicked(opa);
            }
        });

        return convertView;
    }

    private static class ViewHolder {
        private TextView lblName;
        private TextView lblState;
        private ImageView ivPA;
        private Button btnMore;
    }
}
