package com.github.florent37;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.wearable.view.WearableListView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by florentchampigny on 01/04/15.
 */
public class WearMenuListListViewAdapter extends WearableListView.Adapter {
    private List<String> mTitles;
    private List<Drawable> mDrawables;
    private Context mContext;
    private LayoutInflater mInflater;
    private int mListSelectedColor;
    private int mListTextColor;

    public WearMenuListListViewAdapter(Context context, List<String> titles) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    public WearMenuListListViewAdapter(Context context, List<String> titles, int listTextColor, int listSelectedColor) {
        this(context,titles);
        mTitles = titles;
        mListSelectedColor = listSelectedColor;
        mListTextColor = listTextColor;
    }

    public WearMenuListListViewAdapter(Context context, List<String> titles, List<Drawable> drawables, int listTextColor, int listSelectedColor) {
        this(context,titles,listTextColor, listSelectedColor);
        mDrawables = drawables;
    }

    @Override
    public WearableListView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        WearMenuListItemLayout layout = (WearMenuListItemLayout) mInflater.inflate(R.layout.wearmenu_list_element, null);
        layout.setListSelectedColor(mListSelectedColor);
        layout.setListTextColor(mListTextColor);
        return new WearMenuListViewAdapterViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(WearableListView.ViewHolder holder, int position) {
        WearMenuListViewAdapterViewHolder itemHolder = (WearMenuListViewAdapterViewHolder) holder;
        itemHolder.textView.setText(mTitles.get(position));
        holder.itemView.setTag(position);
        if(mDrawables != null){
            itemHolder.imageButton.setImageDrawable(mDrawables.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return mTitles.size();
    }

}
