package com.ghuman.apps.batterynotifier.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PreferenceUtility {
    public static final String PREF_NAME = "PREF_FALCON_LITE_SBL";
    public static final int MODE = Context.MODE_PRIVATE;

    public static final String IS_RATED = "IS_RATED";
    public static final String CHECK_TWENTY_PERCENT = "CHECK_TWENTY_PERCENT";
    public static final String CHECK_THIRTY_PERCENT = "CHECK_THIRTY_PERCENT";
    public static final String CHECK_FORTY_PERCENT = "CHECK_FORTY_PERCENT";
    public static final String CHECK_FIFTY_PERCENT = "CHECK_FIFTY_PERCENT";
    public static final String CHECK_SIXTY_PERCENT = "CHECK_SIXTY_PERCENT";
    public static final String CHECK_SEVENTY_PERCENT = "CHECK_SEVENTY_PERCENT";
    public static final String CHECK_EIGHTY_PERCENT = "CHECK_EIGHTY_PERCENT";
    public static final String CHECK_NINTY_PERCENT = "CHECK_NINTY_PERCENT";
//    public static final String CHECK_HUNDRED_PERCENT = "CHECK_HUNDRED_PERCENT";

    public static final String IS_DEACTIVATED_NOTIFICATIONS = "IS_DEACTIVATED_NOTIFICATIONS";
    public static final String IS_NOTIFICATION_SPOKEN = "IS_NOTIFICATION_SPOKEN";
    public static final String IS_POWER_CONNECTED = "IS_POWER_CONNECTED";
//    public static final String IS_BATTERY_LOW_SPOKEN = "IS_BATTERY_LOW_SPOKEN";
//    public static final String IS_TWENTY_SPOKEN = "IS_TWENTY_SPOKEN";
//    public static final String IS_THIRTY_SPOKEN = "IS_THIRTY_SPOKEN";
//    public static final String IS_FORTY_SPOKEN = "IS_FORTY_SPOKEN";
//    public static final String IS_FIFTY_SPOKEN = "IS_FIFTY_SPOKEN";
//    public static final String IS_SIXTY_SPOKEN = "IS_SIXTY_SPOKEN";
//    public static final String IS_SEVENTY_SPOKEN = "IS_SEVENTY_SPOKEN";
//    public static final String IS_EIGHTY_SPOKEN = "IS_EIGHTY_SPOKEN";
//    public static final String IS_NINTY_SPOKEN = "IS_NINTY_SPOKEN";
//    public static final String IS_HUNDRED_SPOKEN = "IS_HUNDRED_SPOKEN";

    public static void writeBoolean(Context context, String key, boolean value) {
        getEditor(context).putBoolean(key, value).commit();
    }

    public static boolean readBoolean(Context context, String key,
                                      boolean defValue) {
        return getPreferences(context).getBoolean(key, defValue);
    }

    public static void writeInteger(Context context, String key, int value) {
        getEditor(context).putInt(key, value).commit();
    }

    public static int readInteger(Context context, String key, int defValue) {
        return getPreferences(context).getInt(key, defValue);
    }

    public static void writeString(Context context, String key, String value) {
        getEditor(context).putString(key, value).commit();
    }

    public static String readString(Context context, String key, String defValue) {
        return getPreferences(context).getString(key, defValue);
    }

    public static void writeFloat(Context context, String key, float value) {
        getEditor(context).putFloat(key, value).commit();
    }

    public static float readFloat(Context context, String key, float defValue) {
        return getPreferences(context).getFloat(key, defValue);
    }

    public static void writeLong(Context context, String key, long value) {
        getEditor(context).putLong(key, value).commit();
    }

    public static long readLong(Context context, String key, long defValue) {
        return getPreferences(context).getLong(key, defValue);
    }

    public static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREF_NAME, MODE);
    }

    public static Editor getEditor(Context context) {
        return getPreferences(context).edit();
    }
}
