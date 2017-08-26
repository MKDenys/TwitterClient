package com.mkdenis.twitterclient;


import android.content.Context;
import android.widget.ImageView;

public interface ImageLazyLoader {
    void loadImageFromUrl(Context context, String url, ImageView target);
}
