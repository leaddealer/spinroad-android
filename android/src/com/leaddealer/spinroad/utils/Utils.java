package com.leaddealer.spinroad.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;

import java.io.IOException;

public class Utils {
    public static Bitmap loadBitmap(Context context, String name) {
        new BitmapFactory.Options().inJustDecodeBounds = true;
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(context.getAssets().open(name));
            return bitmap;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static SharedPreferences getPrefs(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static boolean isPremium(Context context) {
        return getPrefs(context).getBoolean("ispremium", false);
    }

    public static void setPremium(Context context, boolean isPremium) {
        getPrefs(context).edit().putBoolean("ispremium", isPremium).commit();
    }
}
