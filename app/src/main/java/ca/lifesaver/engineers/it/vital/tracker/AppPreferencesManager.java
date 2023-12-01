package ca.lifesaver.engineers.it.vital.tracker;

import android.content.Context;
import android.content.SharedPreferences;

public class AppPreferencesManager {
    private static final String PREF_NAME = "AppPreferences";
    private static final String KEY_DEVICE_STATE = "deviceState";

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static boolean isDeviceTurnedOn(Context context) {
        return getSharedPreferences(context).getBoolean(KEY_DEVICE_STATE, false);
    }

    public static void setDeviceState(Context context, boolean isTurnedOn) {
        getSharedPreferences(context)
                .edit()
                .putBoolean(KEY_DEVICE_STATE, isTurnedOn)
                .apply();
    }
}

