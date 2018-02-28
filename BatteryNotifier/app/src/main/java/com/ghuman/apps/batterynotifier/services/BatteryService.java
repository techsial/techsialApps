package com.ghuman.apps.batterynotifier.services;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.ghuman.apps.batterynotifier.receivers.BatteryReceiver;

/**
 * Created by Zaigham on 12/30/2016.
 */

public class BatteryService extends Service {
    /*
     * First Call to onStart we don't want to do anything
     */

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        BatteryReceiver batteryReceiver = new BatteryReceiver();
        registerReceiver(batteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    @Override
    public void onStart(Intent intent, int startId) {
//        Log.d("Debug", "Battery Service On Start");

//        Intent notificationIntent = new Intent(this, MainActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
//        Notification.Builder builder = new Notification.Builder(getBaseContext())
//                .setContentIntent(pendingIntent)
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setContentTitle(getString(R.string.app_name))
//                .setContentText("Let you informed about battery level.");
//        Notification notification = builder.build();
//
//        startForeground(startId, notification);
        BatteryReceiver batteryReceiver = new BatteryReceiver();
        registerReceiver(batteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
//        PreferenceUtil.updatePreference(BatteryService.this, "battery_monitor_on", false);
//        unregisterReceiver(receiver);
    }

}
