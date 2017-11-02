package com.leaddealer.spinroad.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class TimerUtils {
    private static final String LAST_TIME = "last_time";

    private static SharedPreferences getPrefs(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static long getLastTime(Context context){
        return getPrefs(context).getLong(LAST_TIME, 0);
    }

    public static void setLastTime(Context context, long lastTime){
        getPrefs(context).edit().putLong(LAST_TIME, lastTime).commit();
    }
}
