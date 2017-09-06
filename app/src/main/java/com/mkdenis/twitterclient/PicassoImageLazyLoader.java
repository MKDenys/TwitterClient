package com.mkdenis.twitterclient;

import android.content.Context;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;


public class PicassoImageLazyLoader implements ImageLazyLoader {
    private final Context context;

    public PicassoImageLazyLoader(Context context){
        this.context = context;
    }

    @Override
    public void loadImageFromUrl(final String url, final ImageView target){
        Picasso.with(this.context).load(url).into(target);
    }
}
