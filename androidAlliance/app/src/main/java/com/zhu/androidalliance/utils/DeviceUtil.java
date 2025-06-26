package com.zhu.androidalliance.utils;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.text.TextUtils;

import java.util.UUID;

// DeviceUtils.java
public class DeviceUtil {
    public static String getDeviceId(Context context) {
        // 尝试获取Android ID
        String androidId = Settings.Secure.getString(
                context.getContentResolver(),
                Settings.Secure.ANDROID_ID
        );

        // 如果Android ID不可用，生成随机ID
        if (TextUtils.isEmpty(androidId)){
            SharedPreferences prefs = context.getSharedPreferences("device_prefs", MODE_PRIVATE);
            androidId = prefs.getString("device_id", null);
            if (androidId == null) {
                androidId = UUID.randomUUID().toString();
                prefs.edit().putString("device_id", androidId).apply();
            }
        }

        return androidId;
    }
}