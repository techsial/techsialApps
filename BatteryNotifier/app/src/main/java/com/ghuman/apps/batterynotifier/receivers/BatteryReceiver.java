package com.ghuman.apps.batterynotifier.receivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.BatteryManager;
import android.os.Vibrator;
import android.widget.Toast;

import com.ghuman.apps.batterynotifier.R;
import com.ghuman.apps.batterynotifier.activities.SplashActivity;
import com.ghuman.apps.batterynotifier.services.BatteryService;
import com.ghuman.apps.batterynotifier.services.SpeechService;
import com.ghuman.apps.batterynotifier.util.PreferenceUtility;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by Zaigham on 12/30/2016.
 */

public class BatteryReceiver extends BroadcastReceiver {

    private InterstitialAd mInterstitialAd;
    private Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        MobileAds.initialize(context, context.getString(R.string.app_id_admob));
        mContext = context;

        if (!PreferenceUtility.readBoolean(context, PreferenceUtility.IS_DEACTIVATED_NOTIFICATIONS, false)) {
            switch (intent.getAction()) {
                case Intent.ACTION_BATTERY_CHANGED:
//                    Toast.makeText(context, "Battery Changed", Toast.LENGTH_SHORT).show();
//                vibrate(context);
                    int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                    switch (level) {
                        case 7:
                            if (!PreferenceUtility.readBoolean(context, PreferenceUtility.IS_NOTIFICATION_SPOKEN, false)) {
                                if (!PreferenceUtility.readBoolean(context, PreferenceUtility.IS_POWER_CONNECTED, false)) {
                                    setSpokenFlag(context, R.raw.battery_low_sound);
                                    Toast.makeText(context, "Battery is low, needs recharge.", Toast.LENGTH_SHORT).show();
                                    context.startService(new Intent(context, SpeechService.class).putExtra("SPEECH", "Battery is low.. please recharge"));
                                    makeNotification("Battery Level Alerts", "Battery is low.. please recharge");
                                }
                            }
                            break;
                        case 15:
                            if (!PreferenceUtility.readBoolean(context, PreferenceUtility.IS_NOTIFICATION_SPOKEN, false)) {
                                if (!PreferenceUtility.readBoolean(context, PreferenceUtility.IS_POWER_CONNECTED, false)) {
                                    setSpokenFlag(context, R.raw.battery_low_sound);
                                    Toast.makeText(context, "Battery is low, needs recharge", Toast.LENGTH_SHORT).show();
                                    context.startService(new Intent(context, SpeechService.class).putExtra("SPEECH", "Battery is low.. please recharge"));
                                    makeNotification("Battery Level Alerts", "Battery is low.. please recharge");
                                }
                            }
                            break;
                        case 20:
                            if (PreferenceUtility.readBoolean(context, PreferenceUtility.CHECK_TWENTY_PERCENT, true)) {
                                if (!PreferenceUtility.readBoolean(context, PreferenceUtility.IS_NOTIFICATION_SPOKEN, false)) {
                                    setSpokenFlag(context, R.raw.battery_changed_sound);
                                    Toast.makeText(context, "Battery level reached 20%.", Toast.LENGTH_SHORT).show();
                                    context.startService(new Intent(context, SpeechService.class).putExtra("SPEECH", "Battery level reached twenty percent"));
                                    makeNotification("Battery Level Alerts", "Battery level reached twenty percent");
                                }
                            }
                            break;
                        case 30:
                            if (PreferenceUtility.readBoolean(context, PreferenceUtility.CHECK_THIRTY_PERCENT, true)) {
                                if (!PreferenceUtility.readBoolean(context, PreferenceUtility.IS_NOTIFICATION_SPOKEN, false)) {
                                    setSpokenFlag(context, R.raw.battery_changed_sound);
                                    Toast.makeText(context, "Battery level reached 30%.", Toast.LENGTH_SHORT).show();
                                    context.startService(new Intent(context, SpeechService.class).putExtra("SPEECH", "Battery level reached thirty percent"));
                                    makeNotification("Battery Level Alerts", "Battery level reached thirty percent");
                                }
                            }
                            break;
                        case 40:
                            if (PreferenceUtility.readBoolean(context, PreferenceUtility.CHECK_FORTY_PERCENT, true)) {
                                if (!PreferenceUtility.readBoolean(context, PreferenceUtility.IS_NOTIFICATION_SPOKEN, false)) {
                                    setSpokenFlag(context, R.raw.battery_changed_sound);
                                    Toast.makeText(context, "Battery level reached 40%.", Toast.LENGTH_SHORT).show();
                                    context.startService(new Intent(context, SpeechService.class).putExtra("SPEECH", "Battery level reached forty percent"));
                                    makeNotification("Battery Level Alerts", "Battery level reached forty percent");
                                    showInterstitialAd();
                                }
                            }
                            break;
                        case 50:
                            if (PreferenceUtility.readBoolean(context, PreferenceUtility.CHECK_FIFTY_PERCENT, true)) {
                                if (!PreferenceUtility.readBoolean(context, PreferenceUtility.IS_NOTIFICATION_SPOKEN, false)) {
                                    setSpokenFlag(context, R.raw.battery_changed_sound);
                                    Toast.makeText(context, "Battery level reached 50%.", Toast.LENGTH_SHORT).show();
                                    context.startService(new Intent(context, SpeechService.class).putExtra("SPEECH", "Battery level reached fifty percent"));
                                    makeNotification("Battery Level Alerts", "Battery level reached fifty percent");
                                }
                            }
                            break;
                        case 60:
                            if (PreferenceUtility.readBoolean(context, PreferenceUtility.CHECK_SIXTY_PERCENT, true)) {
                                if (!PreferenceUtility.readBoolean(context, PreferenceUtility.IS_NOTIFICATION_SPOKEN, false)) {
                                    setSpokenFlag(context, R.raw.battery_changed_sound);
                                    Toast.makeText(context, "Battery level reached 60%.", Toast.LENGTH_SHORT).show();
                                    context.startService(new Intent(context, SpeechService.class).putExtra("SPEECH", "Battery level reached sixty percent"));
                                    makeNotification("Battery Level Alerts", "Battery level reached sixty percent");
                                }
                            }
                            break;
                        case 70:
                            if (PreferenceUtility.readBoolean(context, PreferenceUtility.CHECK_SEVENTY_PERCENT, true)) {
                                if (!PreferenceUtility.readBoolean(context, PreferenceUtility.IS_NOTIFICATION_SPOKEN, false)) {
                                    setSpokenFlag(context, R.raw.battery_changed_sound);
                                    Toast.makeText(context, "Battery level reached 70%.", Toast.LENGTH_SHORT).show();
                                    context.startService(new Intent(context, SpeechService.class).putExtra("SPEECH", "Battery level reached seventy percent"));
                                    makeNotification("Battery Level Alerts", "Battery level reached seventy percent");
                                    showInterstitialAd();
                                }
                            }
                            break;
                        case 80:
                            if (PreferenceUtility.readBoolean(context, PreferenceUtility.CHECK_EIGHTY_PERCENT, true)) {
                                if (!PreferenceUtility.readBoolean(context, PreferenceUtility.IS_NOTIFICATION_SPOKEN, false)) {
                                    setSpokenFlag(context, R.raw.battery_changed_sound);
                                    Toast.makeText(context, "Battery level reached 80%.", Toast.LENGTH_SHORT).show();
                                    context.startService(new Intent(context, SpeechService.class).putExtra("SPEECH", "Battery level reached eighty percent"));
                                    makeNotification("Battery Level Alerts", "Battery level reached eighty percent");
                                }
                            }
                            break;
                        case 90:
                            if (PreferenceUtility.readBoolean(context, PreferenceUtility.CHECK_NINTY_PERCENT, true)) {
                                if (!PreferenceUtility.readBoolean(context, PreferenceUtility.IS_NOTIFICATION_SPOKEN, false)) {
                                    setSpokenFlag(context, R.raw.battery_changed_sound);
                                    Toast.makeText(context, "Battery level reached 90%.", Toast.LENGTH_SHORT).show();
                                    context.startService(new Intent(context, SpeechService.class).putExtra("SPEECH", "Battery level reached ninty percent"));
                                    makeNotification("Battery Level Alerts", "Battery level reached ninty percent");
                                }
                            }
                            break;
                        case 100:
                            if (!PreferenceUtility.readBoolean(context, PreferenceUtility.IS_NOTIFICATION_SPOKEN, false)) {
                                setSpokenFlag(context, R.raw.battery_full_sound);
                                Toast.makeText(context, "Battery is full, please remove the charger.", Toast.LENGTH_SHORT).show();
                                context.startService(new Intent(context, SpeechService.class).putExtra("SPEECH", "Battery is full. please remove the charger"));
                                makeNotification("Battery Level Alerts", "Battery is full. please remove the charger");
                                showInterstitialAd();
                            }
                            break;
                        default:
//                        setSpokenFlag(context, 102, null);
                            PreferenceUtility.writeBoolean(context, PreferenceUtility.IS_NOTIFICATION_SPOKEN, false);
                            break;
                    }
                    break;
                case Intent.ACTION_BATTERY_LOW:
//                Toast.makeText(context, " Battery Low ", Toast.LENGTH_SHORT).show();
                    context.startService(new Intent(context, BatteryService.class));
//                if (!PreferenceUtility.readBoolean(context, PreferenceUtility.IS_NOTIFICATION_SPOKEN, false)) {
//                    setSpokenFlag(context, R.raw.battery_low_sound);
//                    context.startService(new Intent(context, SpeechService.class).putExtra("SPEECH", "your battery is low.. please recharge"));
//                }
                    break;
                case Intent.ACTION_BATTERY_OKAY:
//                Toast.makeText(context, "Battery is in okay condition", Toast.LENGTH_SHORT).show();
                    break;
                case Intent.ACTION_POWER_CONNECTED:
                    Toast.makeText(context, " Power Connected. ", Toast.LENGTH_SHORT).show();
                    PreferenceUtility.writeBoolean(context, PreferenceUtility.IS_POWER_CONNECTED, true);
                    context.startService(new Intent(context, BatteryService.class));
                    break;
                case Intent.ACTION_POWER_DISCONNECTED:
                    Toast.makeText(context, " Power Disconnected. ", Toast.LENGTH_SHORT).show();
                    context.startService(new Intent(context, BatteryService.class));
                    PreferenceUtility.writeBoolean(context, PreferenceUtility.IS_POWER_CONNECTED, false);
                    break;
                case Intent.ACTION_BOOT_COMPLETED:
                case Intent.ACTION_REBOOT:
                case Intent.ACTION_LOCKED_BOOT_COMPLETED:
                    context.startService(new Intent(context, BatteryService.class));
                    break;
            }
        }

    }

    private void setSpokenFlag(Context context, int sound) {

        vibrate(context);
        MediaPlayer mp = MediaPlayer.create(context, sound);
        mp.start();
        PreferenceUtility.writeBoolean(context, PreferenceUtility.IS_NOTIFICATION_SPOKEN, true);

    }

    private void vibrate(Context context) {
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(1500);
    }

    private void makeNotification(String strTitle, String strDescription) {
        NotificationManager notificationManager = (NotificationManager)
                mContext.getSystemService(NOTIFICATION_SERVICE);
        Intent intentNotification = new Intent(mContext, SplashActivity.class);
// use System.currentTimeMillis() to have a unique ID for the pending intent
        PendingIntent pIntent = PendingIntent.getActivity(mContext, (int) System.currentTimeMillis(), intentNotification, 0);

// build notification
// the addAction re-use the same intent to keep the example short
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            Notification n = new Notification.Builder(mContext)
                    .setContentTitle(strTitle)
                    .setContentText(strDescription)
                    .setSmallIcon(R.drawable.app_icon_bar)
                    .setContentIntent(pIntent)
                    .setAutoCancel(true).build();

            notificationManager.notify(0, n);
        }

    }

    private void showInterstitialAd() {
        mInterstitialAd = new InterstitialAd(mContext);
        mInterstitialAd.setAdUnitId(mContext.getString(R.string.interstitial_id_main_screen));

        mInterstitialAd.loadAd(new AdRequest.Builder()
//                .addTestDevice("D823DFBE13477D4EF2A8C8EAF73F2E87")
                .build());

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
//                requestNewInterstitial();
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                mInterstitialAd.show();
            }
        });


    }
}
