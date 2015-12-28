package com.android.ape.orm;

import com.android.ape.model.Feed;
import com.android.ape.model.Row;

import org.litepal.crud.DataSupport;

public class OrmFeedWorker {

    private static OrmFeedWorker mOrmFeedWorker = new OrmFeedWorker();

    private OrmFeedWorker() {
    }

    public static OrmFeedWorker getInstance() {
        return mOrmFeedWorker;
    }

    public boolean saveFeedToDatabase(Feed feed) {
        DataSupport.deleteAll(Feed.class);
        DataSupport.deleteAll(Row.class);

        for (Row row : feed.getRows()) {
            row.save();
        }

        return feed.save();
    }

    public Feed getFeedFromDatabase() {
        return DataSupport.findFirst(Feed.class, true);
    }

}
