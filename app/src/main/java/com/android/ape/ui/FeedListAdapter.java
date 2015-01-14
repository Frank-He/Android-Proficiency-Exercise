package com.android.ape.ui;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.ape.model.Feed;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class FeedListAdapter extends BaseAdapter {

    private Context mContext;

    private Feed mFeed = null;

    public FeedListAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return mFeed == null ? 0 : mFeed.getRows().size();
    }

    @Override
    public Object getItem(int position) {
        return mFeed.getRows().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.feed_list_item_layout, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.title.setText(mFeed.getRows().get(position).getTitle());
        viewHolder.description.setText(mFeed.getRows().get(position).getDescription());
        Picasso.with(mContext).load(mFeed.getRows().get(position).getImageHref()).into(viewHolder.image);

        return convertView;
    }

    public void updateFeedData(Feed feed) {
        mFeed = feed;
        notifyDataSetChanged();
    }

    static class ViewHolder {
        @InjectView(R.id.title)
        TextView title;
        @InjectView(R.id.description)
        TextView description;
        @InjectView(R.id.image)
        ImageView image;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
