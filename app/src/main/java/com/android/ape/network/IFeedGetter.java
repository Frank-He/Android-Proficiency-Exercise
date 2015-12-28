package com.android.ape.network;

import com.android.ape.model.Feed;

public interface IFeedGetter {

    public Feed getFromServer();

    public Feed getFromDatabase();

    public Feed getSearchResult(String input);

}
