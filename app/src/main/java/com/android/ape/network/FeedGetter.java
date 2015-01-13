package com.android.ape.network;

import com.android.ape.model.Feed;
import com.android.ape.model.Row;
import com.android.ape.util.Util;

import java.util.ArrayList;

import retrofit.RestAdapter;


public class FeedGetter {

    private RestAdapter mRestAdapter;
    private FeedService mFeedService;

    private Feed mFeed = null;
    private Feed mSearchResult = new Feed();

    public FeedGetter() {
        mRestAdapter = new RestAdapter.Builder()
                .setEndpoint(Util.NET_PATH)
                .build();

        mFeedService = mRestAdapter.create(FeedService.class);

        mSearchResult.setRows(new ArrayList<Row>());
    }

    public Feed getFromServer() {
        try {
            mFeed = mFeedService.getFromServer();
            preProcessData(mFeed);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mFeed;
    }

    public Feed getSearchResult(String input) {
        mSearchResult.getRows().clear();
        for (Row r : mFeed.getRows()) {
            if (r.getTitle().toLowerCase().contains(input.toLowerCase())) {
                mSearchResult.getRows().add(r);
            }
        }

        return mSearchResult;
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
