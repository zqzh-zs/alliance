package com.zhu.androidalliance.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

public class ImageLoader {
    private static final String DEFAULT_IMAGE_URL = "https://i0.hdslb.com/bfs/new_dyn/6b90ce50a7cd9bbcba156c7eaf5fb99f3494377341061571.jpg@.webp";

    public static void loadImage(Context context, String imageUrl, ImageView imageView) {
        // 处理URL为null的情况
        if (imageUrl == null) {
            Log.i("图片路径","null，使用默认会议背景图片");
            imageUrl = DEFAULT_IMAGE_URL;
        }

        // 正常加载图片
        String finalImageUrl = imageUrl;
        Glide.with(context)
                .load(imageUrl)
                .placeholder(com.zhu.androidalliance.R.drawable.ic_placeholder)
                .error(com.zhu.androidalliance.R.drawable.ic_error)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerInside()
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Log.e("ImageLoader", "图片加载失败，URL: " + finalImageUrl + "，错误: " + (e != null ? e.getMessage() : "未知错误"));
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        Log.d("ImageLoader", "图片加载成功，URL: " + finalImageUrl);
                        return false;
                    }
                })
                .into(imageView);
    }
}