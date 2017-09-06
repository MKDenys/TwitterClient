package com.mkdenis.twitterclient;

import android.widget.ImageView;

public interface ImageLazyLoader {
    void loadImageFromUrl(String url, ImageView target);
}
