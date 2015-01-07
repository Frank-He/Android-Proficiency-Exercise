package com.android.ape.network;

import com.android.ape.model.Feed;
import com.android.ape.util.Util;

import retrofit.http.GET;

public interface FeedService {
    @GET(Util.NET_VALUE)
    Feed getFromServer();
}
