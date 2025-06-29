package com.zhu.androidalliance;

import android.app.Application;
import java.io.File;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // 手动创建WebView缓存目录
        File webViewCacheDir = new File(getCacheDir(), "WebView/Crashpad");
        if (!webViewCacheDir.exists()) {
            if (!webViewCacheDir.mkdirs()) {
                // 记录创建失败日志
                android.util.Log.e("MyApplication", "Failed to create WebView cache directory");
            }
        }
    }
}
