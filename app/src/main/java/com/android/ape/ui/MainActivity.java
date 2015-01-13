package com.android.ape.ui;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.ape.model.Feed;
import com.android.ape.network.FeedGetter;
import com.android.ape.util.Util;

import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnTextChanged;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

public class MainActivity extends ActionBarActivity implements SwipeRefreshLayout.OnRefreshListener {

    @InjectView(R.id.feed_list)
    ListView mFeedList;

    @InjectView(R.id.progress_bar)
    ProgressBar mProgressBar;

    @InjectView(R.id.user_search_bar)
    EditText mSearchBar;

    @InjectView(R.id.swipe_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private FeedListAdapter mFeedListAdapter;

    private FeedGetter mFeedGetter;

    private Subscription mSubscription;

    private Subscription mSearchSubscription;
    private PublishSubject<Observable<String>> mSearchTextSubject;

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

        initSearchBar();
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
        } else if (id == R.id.action_search) {
            if (mSearchBar.getVisibility() == View.VISIBLE) {
                mSearchBar.setVisibility(View.GONE);
            } else {
                mSearchBar.setVisibility(View.VISIBLE);
            }
        } else {

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
        loadFeedFromServer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSubscription.unsubscribe();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mSubscription.unsubscribe();
    }

    @OnTextChanged(R.id.user_search_bar)
    public void searchTextEntered(CharSequence charsEntered) {
        mSearchTextSubject.onNext(getASearchObservableFor(charsEntered.toString()));
    }

    private void initSearchBar() {
        mSearchBar.setVisibility(View.GONE);
        mSearchTextSubject = PublishSubject.create();

        mSearchSubscription = AppObservable.bindActivity(this, Observable.switchOnNext(mSearchTextSubject))
                .debounce(Util.SEARCH_TIME_OUT, TimeUnit.MILLISECONDS, Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mSearchObserver);
    }

    private Observable<String> getASearchObservableFor(final String searchText) {
        return Observable.create(new Observable.OnSubscribe<String>() {

            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onNext(searchText);
            }
        }).subscribeOn(Schedulers.io());
    }

    private void loadFeedFromServer() {
        mSubscription = createFeedObservable().subscribe(mFeedObserver);
    }

    protected void updateActionbarTitle(String title) {
        setTitle(title);
    }

    private void updateFeedListContent(Feed feed) {
        mFeedListAdapter.updateFeedData(feed);
    }

    private Observer<Feed> mFeedObserver = new Observer<Feed>() {
        @Override
        public void onCompleted() {
            mSubscription.unsubscribe();
        }

        @Override
        public void onError(Throwable e) {
            Toast.makeText(MainActivity.this, R.string.message_refresh_wrong, Toast.LENGTH_SHORT).show();
            mSubscription.unsubscribe();
        }

        @Override
        public void onNext(Feed feed) {
            if (mSwipeRefreshLayout.isRefreshing()) {
                // pull to refresh
                mSwipeRefreshLayout.setRefreshing(false);
            } else {
                mProgressBar.setVisibility(View.GONE);
            }

            // Get feed success
            updateFeedListContent(feed);
            updateActionbarTitle(feed.getTitle());
        }
    };

    private Observable<Feed> createFeedObservable() {
        return Observable.create(new Observable.OnSubscribe<Feed>() {
            @Override
            public void call(Subscriber<? super Feed> subscriber) {
                subscriber.onNext(mFeedGetter.getFromServer());
                mSearchBar.setVisibility(View.GONE);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
    }

    private Observer<String> mSearchObserver = new Observer<String>() {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onNext(String s) {
            updateFeedListContent(mFeedGetter.getSearchResult(s));
        }
    };
}
