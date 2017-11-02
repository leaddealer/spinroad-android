package com.leaddealer.spinroad.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.leaddealer.spinroad.activities.BackgroundActivity;

public class Backgrounds {
    private static final String BACKGROUND_BOUGHT = "background_bought";
    private static final String BACKGROUND_CHOSEN = "background_chosen";

    private static SharedPreferences getPrefs(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static boolean isBackgroundBought(Context context, int position){
        if(position == 0) return true;
        return getPrefs(context).getBoolean(BACKGROUND_BOUGHT + String.valueOf(position), false);
    }

    public static void setBackgroundBought(Context context, int position){
        getPrefs(context).edit().putBoolean(BACKGROUND_BOUGHT + String.valueOf(position), true).commit();
    }

    public static boolean isBackgroundChosen(Context context, int position){
        int chosenSpinner = -1;
        for(int i = 0; i< BackgroundActivity.MAX_BACKGROUNDS; i++){
            if(getPrefs(context).getBoolean(BACKGROUND_CHOSEN + String.valueOf(i), false))
                chosenSpinner = i;
        }

        if(chosenSpinner == -1){
            return position == 0;
        } else {
            return position == chosenSpinner;
        }
    }

    public static void setBackgroundChosen(Context context, int position){
        for(int i=0; i< BackgroundActivity.MAX_BACKGROUNDS; i++){
            getPrefs(context).edit().putBoolean(BACKGROUND_CHOSEN + String.valueOf(i), false).commit();
        }
        getPrefs(context).edit().putBoolean(BACKGROUND_CHOSEN + String.valueOf(position), true).commit();
    }

    public static int getCurrent(Context context){
        int chosenSpinner = 0;
        for(int i=0; i< BackgroundActivity.MAX_BACKGROUNDS; i++){
            if(getPrefs(context).getBoolean(BACKGROUND_CHOSEN + String.valueOf(i), false))
                chosenSpinner = i;
        }
        return chosenSpinner;
    }
}
