package com.mkdenis.twitterclient;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public abstract class EndlessRecyclerViewScrollListener extends RecyclerView.OnScrollListener {

    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 5;
    private int firstVisibleItem, visibleItemCount, totalItemCount;
    private LinearLayoutManager linearLayoutManager;

    public EndlessRecyclerViewScrollListener(LinearLayoutManager linearLayoutManager) {
        this.linearLayoutManager = linearLayoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        this.visibleItemCount = recyclerView.getChildCount();
        this.totalItemCount = this.linearLayoutManager.getItemCount();
        this.firstVisibleItem = this.linearLayoutManager.findFirstVisibleItemPosition();

        if (this.loading) {
            if (this.totalItemCount > this.previousTotal+1) {
                this.loading = false;
                this.previousTotal = this.totalItemCount;
            }
        }
        if (!this.loading && (this.totalItemCount - this.visibleItemCount) <= (this.firstVisibleItem + this.visibleThreshold)) {
            this.loading = true;
            onLoadMore(recyclerView);
        }
    }

    public abstract void onLoadMore(RecyclerView view);
}