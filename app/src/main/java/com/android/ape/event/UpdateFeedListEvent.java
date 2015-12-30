package com.android.ape.event;

import com.android.ape.model.Feed;

public class UpdateFeedListEvent {

    private Feed mFeed;

    public UpdateFeedListEvent(Feed feed) {
        mFeed = feed;
    }

    public Feed getFeed() {
        return mFeed;
    }

}
