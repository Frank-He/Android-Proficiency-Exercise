package com.android.ape.presenter;

import android.view.View;

import com.android.ape.event.UpdateFeedListEvent;
import com.android.ape.model.Feed;
import com.android.ape.network.FeedGetter;
import com.android.ape.network.IFeedGetter;
import com.android.ape.ui.IMainView;
import com.android.ape.util.Util;

import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

public class MainViewPresenter {

    private IMainView mMainView;
    private IFeedGetter mFeedGetter;

    private Subscription mSubscription;

    private Subscription mSearchSubscription;
    private PublishSubject<Observable<String>> mSearchTextSubject;

    public MainViewPresenter(IMainView mainView) {
        this.mMainView = mainView;
        mFeedGetter = new FeedGetter();

        initSearchView();
    }

    public void loadFeedFromServer() {
        mSubscription = createFeedObservable().subscribe(mFeedObserver);
    }

    public void doUnsubscribe() {
        mSubscription.unsubscribe();
        mSearchSubscription.unsubscribe();
    }

    public void searchTextEntered(CharSequence charsEntered) {
        mSearchTextSubject.onNext(getASearchObservableFor(charsEntered.toString()));
    }

    public void clickRefreshButtonAction() {
        mMainView.setProgressBarState(View.VISIBLE);
        loadFeedFromServer();
    }

    public void clickSearchButtonAction() {
        mMainView.setSearchBarContent("");
        if (mMainView.getSearchBarState() == View.VISIBLE) {
            mMainView.setSearchBarState(View.GONE);
        } else {
            mMainView.setSearchBarState(View.VISIBLE);
        }
    }

    public void clearRefreshUI() {
        if (mMainView.isRefresh()) {
            // pull to refresh
            mMainView.setRefreshViewState(false);
        } else {
            mMainView.setProgressBarState(View.GONE);
        }
    }

    private void initSearchView() {
        mMainView.setSearchBarState(View.GONE);
        mSearchTextSubject = PublishSubject.create();

        mSearchSubscription = AppObservable.bindActivity(mMainView.getCurrentActivity(), Observable.switchOnNext(mSearchTextSubject))
                .debounce(Util.SEARCH_TIME_OUT, TimeUnit.MILLISECONDS, Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mSearchObserver);
    }

    private Observable<Feed> createFeedObservable() {
        return Observable.create(new Observable.OnSubscribe<Feed>() {
            @Override
            public void call(Subscriber<? super Feed> subscriber) {
                mMainView.setSearchBarState(View.GONE);
                subscriber.onNext(mFeedGetter.getFromServer());
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
    }

    private Observer<Feed> mFeedObserver = new Observer<Feed>() {
        @Override
        public void onCompleted() {
        }

        @Override
        public void onError(Throwable e) {
            mMainView.showErrorMessage();

            clearRefreshUI();
            EventBus.getDefault().post(new UpdateFeedListEvent(mFeedGetter.getFromDatabase()));
        }

        @Override
        public void onNext(Feed feed) {
            clearRefreshUI();

            // Get feed success
            EventBus.getDefault().post(new UpdateFeedListEvent(feed));
            mMainView.updateActionbarTitle(feed.getTitle());
        }
    };

    private Observer<String> mSearchObserver = new Observer<String>() {
        @Override
        public void onCompleted() {
        }

        @Override
        public void onError(Throwable e) {
        }

        @Override
        public void onNext(String s) {
            EventBus.getDefault().post(new UpdateFeedListEvent(mFeedGetter.getSearchResult(s)));
        }
    };

    private Observable<String> getASearchObservableFor(final String searchText) {
        return Observable.create(new Observable.OnSubscribe<String>() {

            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onNext(searchText);
            }
        }).subscribeOn(Schedulers.io());
    }

}
