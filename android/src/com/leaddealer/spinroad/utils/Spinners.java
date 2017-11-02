package com.leaddealer.spinroad.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.leaddealer.spinroad.activities.SpinnersActivity;

public class Spinners {

    private static final String SPINNER_BOUGHT = "spinner_bought";
    private static final String SPINNER_CHOSEN = "spinner_chosen";

    private static SharedPreferences getPrefs(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static boolean isSpinnerBought(Context context, int position){
        if(position == 0) return true;
        return getPrefs(context).getBoolean(SPINNER_BOUGHT + String.valueOf(position), false);
    }

    public static void setSpinnerBought(Context context, int position){
        getPrefs(context).edit().putBoolean(SPINNER_BOUGHT + String.valueOf(position), true).commit();
    }

    public static boolean isSpinnerChosen(Context context, int position){
        int chosenSpinner = -1;
        for(int i=0; i< SpinnersActivity.MAX_SPINNERS; i++){
            if(getPrefs(context).getBoolean(SPINNER_CHOSEN + String.valueOf(i), false))
                chosenSpinner = i;
        }

        if(chosenSpinner == -1){
            return position == 0;
        } else {
            return position == chosenSpinner;
        }
    }

    public static void setSpinnerChosen(Context context, int position){
        for(int i=0; i< SpinnersActivity.MAX_SPINNERS; i++){
            getPrefs(context).edit().putBoolean(SPINNER_CHOSEN + String.valueOf(i), false).commit();
        }
        getPrefs(context).edit().putBoolean(SPINNER_CHOSEN + String.valueOf(position), true).commit();
    }

    public static int getCurrent(Context context){
        int chosenSpinner = 0;
        for(int i=0; i< SpinnersActivity.MAX_SPINNERS; i++){
            if(getPrefs(context).getBoolean(SPINNER_CHOSEN + String.valueOf(i), false))
                chosenSpinner = i;
        }
        return chosenSpinner;
    }
}
