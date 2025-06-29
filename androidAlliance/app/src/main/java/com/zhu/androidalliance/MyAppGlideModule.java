package com.zhu.androidalliance;

import android.content.Context;

import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;

@GlideModule
public final class MyAppGlideModule extends AppGlideModule {
    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        // 设置内存缓存大小（10MB）
        builder.setMemoryCache(new LruResourceCache(10 * 1024 * 1024));

        // 设置磁盘缓存策略
        builder.setDiskCache(new InternalCacheDiskCacheFactory(context, 100 * 1024 * 1024));

        // 设置默认占位符
        builder.setDefaultRequestOptions(
                new RequestOptions()
                        .placeholder(R.drawable.ic_placeholder)
                        .error(R.drawable.ic_error)
        );
    }
}