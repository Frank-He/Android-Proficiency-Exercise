package com.android.ape.orm;

import com.android.ape.model.Feed;
import com.android.ape.model.Row;

import org.litepal.crud.DataSupport;

public class OrmWorker {

    private static OrmWorker mOrmWorker = null;

    private OrmWorker() {

    }

    public static OrmWorker getInstance() {
        if (mOrmWorker == null) {
            mOrmWorker = new OrmWorker();
        }

        return mOrmWorker;
    }

    private RowTable addItemToRowTable(Row row) {
        RowTable rowTable = new RowTable();
        rowTable.setTitle(row.getTitle());
        rowTable.setDescription(row.getDescription());
        rowTable.setImageHref(row.getImageHref());
        rowTable.save();
        return rowTable;
    }

    public boolean saveFeedToDatabase(Feed feed) {
        DataSupport.deleteAll(FeedTable.class);
        DataSupport.deleteAll(RowTable.class);

        FeedTable feedTable = new FeedTable();
        feedTable.setTitle(feed.getTitle());

        for (Row row : feed.getRows()) {
            feedTable.getRows().add(addItemToRowTable(row));
        }

        return feedTable.save();
    }

    public Feed getFeedFromDatabase() {
        Feed feed = new Feed();

        FeedTable feedTable = DataSupport.findFirst(FeedTable.class, true);

        feed.setTitle(feedTable.getTitle());
        for (RowTable rowTable : feedTable.getRows()) {
            Row row = new Row();
            row.setTitle(rowTable.getTitle());
            row.setDescription(rowTable.getDescription());
            row.setImageHref(rowTable.getImageHref());

            feed.getRows().add(row);
        }

        return feed;
    }
}
