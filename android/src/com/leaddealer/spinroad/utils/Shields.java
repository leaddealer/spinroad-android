package com.leaddealer.spinroad.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.leaddealer.spinroad.activities.ShieldsActivity;

public class Shields {

    private static final String SHIELD_BOUGHT = "shield_bought";
    private static final String SHIELD_CHOSEN = "shield_chosen";

    private static SharedPreferences getPrefs(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static boolean isShieldBought(Context context, int position){
        if(position == 0) return true;
        return getPrefs(context).getBoolean(SHIELD_BOUGHT + String.valueOf(position), false);
    }

    public static void setShieldBought(Context context, int position){
        getPrefs(context).edit().putBoolean(SHIELD_BOUGHT + String.valueOf(position), true).commit();
    }

    public static boolean isShieldChosen(Context context, int position){
        int chosenSpinner = -1;
        for(int i = 0; i< ShieldsActivity.MAX_SHIELDS; i++){
            if(getPrefs(context).getBoolean(SHIELD_CHOSEN + String.valueOf(i), false))
                chosenSpinner = i;
        }

        if(chosenSpinner == -1){
            return position == 0;
        } else {
            return position == chosenSpinner;
        }
    }

    public static void setShieldChosen(Context context, int position){
        for(int i=0; i< ShieldsActivity.MAX_SHIELDS; i++){
            getPrefs(context).edit().putBoolean(SHIELD_CHOSEN + String.valueOf(i), false).commit();
        }
        getPrefs(context).edit().putBoolean(SHIELD_CHOSEN + String.valueOf(position), true).commit();
    }

    public static int getCurrent(Context context){
        int chosenSpinner = 0;
        for(int i=0; i< ShieldsActivity.MAX_SHIELDS; i++){
            if(getPrefs(context).getBoolean(SHIELD_CHOSEN + String.valueOf(i), false))
                chosenSpinner = i;
        }
        return chosenSpinner;
    }
}
