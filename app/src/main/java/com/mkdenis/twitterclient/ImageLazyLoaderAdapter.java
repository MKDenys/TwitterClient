package com.mkdenis.twitterclient;


import android.content.Context;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class ImageLazyLoaderAdapter implements ImageLazyLoader {
    @Override
    public void loadImageFromUrl(Context context, String url, ImageView target){
        Picasso.with(context).load(url).into(target);
    }
}
