package com.mkdenis.twitterclient;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

public class ProgressViewHolder extends RecyclerView.ViewHolder {
    public ProgressBar progressBar;
    public ProgressViewHolder(View view) {
        super(view);
        this.progressBar = (ProgressBar)view.findViewById(R.id.more_progress);
    }
}
