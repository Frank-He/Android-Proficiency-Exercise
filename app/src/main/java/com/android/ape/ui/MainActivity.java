package com.android.ape.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.ape.model.Feed;
import com.android.ape.presenter.MainViewPresenter;

import org.litepal.tablemanager.Connector;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnTextChanged;

public class MainActivity extends ActionBarActivity implements SwipeRefreshLayout.OnRefreshListener, IMainView {

    @InjectView(R.id.feed_list)
    ListView mFeedList;

    @InjectView(R.id.progress_bar)
    ProgressBar mProgressBar;

    @InjectView(R.id.user_search_bar)
    EditText mSearchBar;

    @InjectView(R.id.swipe_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private FeedListAdapter mFeedListAdapter;
    private MainViewPresenter mMainViewPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        initDatabase();

        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_red_dark);

        mFeedListAdapter = new FeedListAdapter(this);
        mFeedList.setAdapter(mFeedListAdapter);

        mMainViewPresenter = new MainViewPresenter(this);

        mMainViewPresenter.loadFeedFromServer();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            mMainViewPresenter.clickRefreshButtonAction();
            return true;
        } else if (id == R.id.action_search) {
            mMainViewPresenter.clickSearchButtonAction();
        } else {
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
        mMainViewPresenter.loadFeedFromServer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMainViewPresenter.doUnsubscribe();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMainViewPresenter.doUnsubscribe();
    }

    @OnTextChanged(R.id.user_search_bar)
    public void searchTextEntered(CharSequence charsEntered) {
        mMainViewPresenter.searchTextEntered(charsEntered);
    }

    @Override
    public void setSearchBarState(int state) {
        mSearchBar.setVisibility(state);
    }

    @Override
    public void showErrorMessage() {
        Toast.makeText(MainActivity.this, R.string.message_refresh_wrong, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void updateFeedListContent(Feed feed) {
        mFeedListAdapter.updateFeedData(feed);
    }

    @Override
    public void updateActionbarTitle(String title) {
        setTitle(title);
    }

    @Override
    public void setProgressBarState(int state) {
        mProgressBar.setVisibility(state);
    }

    @Override
    public void setSearchBarContent(CharSequence text) {
        mSearchBar.setText(text);
    }

    @Override
    public int getSearchBarState() {
        return mSearchBar.getVisibility();
    }

    @Override
    public Boolean isRefresh() {
        return mSwipeRefreshLayout.isRefreshing();
    }

    @Override
    public void setRefreshViewState(Boolean state) {
        mSwipeRefreshLayout.setRefreshing(state);
    }

    @Override
    public Activity getCurrentActivity() {
        return this;
    }

    private void initDatabase() {
        Connector.getDatabase();
    }

}
