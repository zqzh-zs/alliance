package com.zhu.androidalliance.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatUtil {
    public static String format(Date date) {
        // 定义日期格式（年-月-日 时:分:秒）
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        // 格式化日期
        String formattedDate = sdf.format(date);
        return formattedDate;

    }

    public static String formatDateRange(Date startTime, Date endTime) {
        String start = format(startTime);
        String end = format(endTime);
        return start+ "~" + end;
    }
}
