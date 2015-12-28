package com.android.ape.network;

import com.android.ape.model.Feed;
import com.android.ape.model.Row;
import com.android.ape.orm.OrmFeedWorker;
import com.android.ape.util.Util;

import retrofit.RestAdapter;


public class FeedGetter implements IFeedGetter {

    private RestAdapter mRestAdapter;
    private FeedService mFeedService;
    private OrmFeedWorker mOrmFeedWorker;

    public FeedGetter() {
        mRestAdapter = new RestAdapter.Builder()
                .setEndpoint(Util.NET_PATH)
                .build();

        mFeedService = mRestAdapter.create(FeedService.class);

        mOrmFeedWorker = OrmFeedWorker.getInstance();
    }

    @Override
    public Feed getFromServer() {
        Feed feed = null;

        try {
            feed = mFeedService.getFromServer();
            preProcessData(feed);
            saveToDatabase(feed);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return feed;
    }

    @Override
    public Feed getFromDatabase() {
        return mOrmFeedWorker.getFeedFromDatabase();
    }

    @Override
    public Feed getSearchResult(String input) {
        Feed searchResult = new Feed();
        Feed fromDB = getFromDatabase();

        for (Row r : fromDB.getRows()) {
            if (r.getTitle().toLowerCase().contains(input.toLowerCase())) {
                searchResult.getRows().add(r);
            }
        }

        return searchResult;
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

    private void saveToDatabase(Feed feed) {
        mOrmFeedWorker.saveFeedToDatabase(feed);
    }

}
