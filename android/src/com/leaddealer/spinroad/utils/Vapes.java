package com.leaddealer.spinroad.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.leaddealer.spinroad.activities.VapesActivity;

public class Vapes {
    private static final String VAPE_BOUGHT = "vape_bought";
    private static final String VAPE_CHOSEN = "vape_chosen";

    private static SharedPreferences getPrefs(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static boolean isVapeBought(Context context, int position){
        if(position == 0) return true;
        return getPrefs(context).getBoolean(VAPE_BOUGHT + String.valueOf(position), false);
    }

    public static void setVapeBought(Context context, int position){
        getPrefs(context).edit().putBoolean(VAPE_BOUGHT + String.valueOf(position), true).commit();
    }

    public static boolean isVapeChosen(Context context, int position){
        int chosenSpinner = -1;
        for(int i = 0; i< VapesActivity.MAX_VAPES; i++){
            if(getPrefs(context).getBoolean(VAPE_CHOSEN + String.valueOf(i), false))
                chosenSpinner = i;
        }

        if(chosenSpinner == -1){
            return position == 0;
        } else {
            return position == chosenSpinner;
        }
    }

    public static void setVapeChosen(Context context, int position){
        for(int i=0; i< VapesActivity.MAX_VAPES; i++){
            getPrefs(context).edit().putBoolean(VAPE_CHOSEN + String.valueOf(i), false).commit();
        }
        getPrefs(context).edit().putBoolean(VAPE_CHOSEN + String.valueOf(position), true).commit();
    }

    public static int getCurrent(Context context){
        int chosenSpinner = 0;
        for(int i=0; i< VapesActivity.MAX_VAPES; i++){
            if(getPrefs(context).getBoolean(VAPE_CHOSEN + String.valueOf(i), false))
                chosenSpinner = i;
        }
        return chosenSpinner;
    }
}
