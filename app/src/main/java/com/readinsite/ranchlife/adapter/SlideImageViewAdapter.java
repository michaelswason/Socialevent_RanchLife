package com.readinsite.ranchlife.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.readinsite.ranchlife.R;
import com.readinsite.ranchlife.rest.ApiClient;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * @Created by Dima on 6/22/17.
 */

public class SlideImageViewAdapter extends PagerAdapter {
    private Context mContext;
    private List<String>urls = new ArrayList<>();
    private LayoutInflater mLayoutInflater;

    public SlideImageViewAdapter(Context context) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setItems(List<String> imageUrls) {
        urls = imageUrls;
        notifyDataSetChanged();
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.pager_item, collection, false);

        ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
        collection.addView(itemView);

        Picasso.with(mContext).load(String.format("%s%s", ApiClient.BASE_IMAGE_URL, urls.get(position))).into(imageView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        return urls.size();
    }

    @Override
    public int getItemPosition(Object object){
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }
}
