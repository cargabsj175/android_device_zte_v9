package com.cyanogenmod.settings.device;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class V9PartsStartup extends BroadcastReceiver
{
    @Override
    public void onReceive(final Context context, final Intent bootintent) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        // Gestures
        Utils.writeValue("/sys/module/msm_ts/parameters/tscal_gesture_pressure", Integer.parseInt(prefs.getString("gesture_pressure", "1200")));
        Utils.writeValue("/sys/module/msm_ts/parameters/tscal_gesture_blindspot", Integer.parseInt(prefs.getString("gesture_blindspot", "100")));
        // USB charging
        if(prefs.getBoolean("usb_charging", true))
            Utils.writeValue("/sys/module/msm_battery/parameters/usb_chg_enable", 1);
        else
            Utils.writeValue("/sys/module/msm_battery/parameters/usb_chg_enable", 0);
    }
}
