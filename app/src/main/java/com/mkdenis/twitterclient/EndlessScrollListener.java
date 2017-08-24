package com.mkdenis.twitterclient;

import android.content.Context;
import android.widget.AbsListView;

import java.util.ArrayList;


public class EndlessScrollListener implements AbsListView.OnScrollListener {
    private int visibleThreshold = 3;
    private int currentPage = 0;
    private int previousTotal = 0;
    private boolean loading = true;
    private Context contex;

    public EndlessScrollListener() {
    }
    public EndlessScrollListener(int visibleThreshold, Context context) {
        this.visibleThreshold = visibleThreshold;
        this.contex = context;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        if (loading) {
            if (totalItemCount > previousTotal) {
                loading = false;
                previousTotal = totalItemCount;
                currentPage++;
            }
        }
        if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
            TweetListAdapter adapter = (TweetListAdapter) view.getAdapter();
            long maxId = adapter.getItemId(adapter.getCount() - 1) - 1;
            String url = contex.getString(R.string.HOME_TIMELINE_URL) + "?max_id=" + maxId;
            ArrayList<Tweet> tweets = TwitterRestAPIManager.getInstance().getTimeline(url);
            adapter.addArrayListToBottom(tweets);
            loading = true;
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }
}
