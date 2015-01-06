package com.android.ape.network;

import com.android.ape.model.Feed;
import com.android.ape.util.Util;

import retrofit.RestAdapter;


public class FeedGetter {

    private RestAdapter mRestAdapter;
    private FeedService mFeedService;

    private Feed mFeed;

    public FeedGetter() {
        mRestAdapter = new RestAdapter.Builder()
                .setEndpoint(Util.NET_PATH)
                .build();

        mFeedService = mRestAdapter.create(FeedService.class);
    }

    public Feed getFeedFromServer() {
        try {
            mFeed = mFeedService.getFeedFromServer();
            deleteNullValueFromFeed();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mFeed;
    }

    private void deleteNullValueFromFeed() {
        for (int i = 0; i < mFeed.getRows().size(); i++) {
            if (mFeed.getRows().get(i).getTitle() == null
                    && mFeed.getRows().get(i).getDescription() == null
                    && mFeed.getRows().get(i).getImageHref() == null) {
                mFeed.getRows().remove(i);
                i = i - 1;
            }
        }
    }

}
