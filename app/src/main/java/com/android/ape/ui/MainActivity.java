package com.android.ape.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.ape.model.Feed;
import com.android.ape.network.FeedGetter;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends ActionBarActivity implements SwipeRefreshLayout.OnRefreshListener {

    @InjectView(R.id.feed_list)
    ListView mFeedList;

    @InjectView(R.id.progress_bar)
    ProgressBar mProgressBar;

    @InjectView(R.id.swipe_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private FeedListAdapter mFeedListAdapter;

    private FeedGetter mFeedGetter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_red_dark);

        mFeedGetter = new FeedGetter();

        mFeedListAdapter = new FeedListAdapter(this);
        mFeedList.setAdapter(mFeedListAdapter);

        loadFeedFromServer();
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
            mProgressBar.setVisibility(View.VISIBLE);
            loadFeedFromServer();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
        loadFeedFromServer();
    }

    private void loadFeedFromServer() {
        FeedLoadTask feedLoadTask = new FeedLoadTask();
        feedLoadTask.execute((Void[]) null);
    }

    protected void updateActionbarTitle(String title) {
        setTitle(title);
    }

    private void updateFeedListContent(Feed feed) {
        mFeedListAdapter.updateFeedData(feed);
    }

    class FeedLoadTask extends AsyncTask<Void, Void, Feed> {

        @Override
        protected Feed doInBackground(Void... params) {
            return mFeedGetter.getFeedFromServer();
        }

        @Override
        protected void onPreExecute() {
            if (!mSwipeRefreshLayout.isRefreshing()) {
                mProgressBar.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected void onPostExecute(Feed feed) {
            if (mSwipeRefreshLayout.isRefreshing()) {
                mSwipeRefreshLayout.setRefreshing(false);
            } else {
                mProgressBar.setVisibility(View.GONE);
            }

            if (feed == null) {
                Toast.makeText(MainActivity.this, R.string.message_refresh_wrong, Toast.LENGTH_SHORT).show();
            } else {
                updateFeedListContent(feed);
                updateActionbarTitle(feed.getTitle());
            }
        }
    }
}
