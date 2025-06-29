package com.zhu.androidalliance.utils;

import android.content.Context;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

public class ImageLoader {
    public static void loadImage(Context context, String imageUrl, ImageView imageView, int defaultImage) {
        // 处理URL为null的情况
        if (imageUrl == null) {
            Glide.with(context)
                    .load(defaultImage)
                    .placeholder(defaultImage)
                    .error(defaultImage)
                    .centerInside()
                    .into(imageView);
            return;
        }

        // 正常加载图片
        Glide.with(context)
                .load(imageUrl)
                .placeholder(defaultImage)
                .error(defaultImage)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerInside()
                .into(imageView);
    }
}