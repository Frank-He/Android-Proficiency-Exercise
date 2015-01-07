package com.android.ape.network;

import com.android.ape.model.Feed;
import com.android.ape.util.Util;

import retrofit.RestAdapter;


public class FeedGetter {

    private RestAdapter mRestAdapter;
    private FeedService mFeedService;

    public FeedGetter() {
        mRestAdapter = new RestAdapter.Builder()
                .setEndpoint(Util.NET_PATH)
                .build();

        mFeedService = mRestAdapter.create(FeedService.class);
    }

    public Feed getFromServer() {
        Feed feed = null;

        try {
            feed = mFeedService.getFromServer();
            preProcessData(feed);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return feed;
    }

    private void preProcessData(Feed feed) {
        // Delete null value from data
        for (int i = 0; i < feed.getRows().size(); i++) {
            if (feed.getRows().get(i).getTitle() == null
                    && feed.getRows().get(i).getDescription() == null
                    && feed.getRows().get(i).getImageHref() == null) {
                feed.getRows().remove(i);
                i = i - 1;
            }
        }
    }

}
