package com.android.ape.ui;

import android.app.Activity;

import com.android.ape.model.Feed;

public interface IMainView {

    public void showErrorMessage();

    public void updateFeedListContent(Feed feed);

    public void updateActionbarTitle(String title);

    public void setSearchBarState(int state);

    public void setProgressBarState(int state);

    public void setSearchBarContent(CharSequence text);

    public int getSearchBarState();

    public Boolean isRefresh();

    public void setRefreshViewState(Boolean state);

    public Activity getCurrentActivity();

}
