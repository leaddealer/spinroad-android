package com.leaddealer.spinroad.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Score {
    private static final String ALL_COINS = "all_coins";
    private static final String HIGHSCORE = "highscore";

    private static SharedPreferences getPrefs(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static int getAllCoins(Context context){
        return getPrefs(context).getInt(ALL_COINS, 0);
//        return 100000;
    }

    public static void addCoins(Context context, int addValue){
        int allCoins = getAllCoins(context);
        getPrefs(context).edit().putInt(ALL_COINS, (allCoins + addValue)).commit();
    }

    public static int getHighScore(Context context){
        return getPrefs(context).getInt(HIGHSCORE, 0);
    }

    public static boolean addHighScore(Context context, int highscore){
        int oldHighscore = getHighScore(context);
        if(highscore > oldHighscore) {
            getPrefs(context).edit().putInt(HIGHSCORE, highscore).commit();
            return true;
        } else
            return false;
    }
}
