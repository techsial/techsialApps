package com.techsial.apps.timewatcher.productivity.timer;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import static android.app.AlarmManager.ELAPSED_REALTIME_WAKEUP;
import static android.media.AudioManager.RINGER_MODE_SILENT;
import static android.media.AudioManager.STREAM_ALARM;
import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static com.techsial.apps.timewatcher.productivity.timer.Notifications.createCompletionNotification;
import static com.techsial.apps.timewatcher.productivity.timer.Notifications.createForegroundNotification;
import static com.techsial.apps.timewatcher.productivity.timer.Preferences.PREFERENCES_NAME;
import static com.techsial.apps.timewatcher.productivity.timer.SessionType.LONG_BREAK;
import static com.techsial.apps.timewatcher.productivity.timer.SessionType.WORK;
import static com.techsial.apps.timewatcher.productivity.timer.TimerActivity.NOTIFICATION_TAG;
import static com.techsial.apps.timewatcher.productivity.timer.TimerState.ACTIVE;
import static com.techsial.apps.timewatcher.productivity.timer.TimerState.INACTIVE;
import static com.techsial.apps.timewatcher.productivity.timer.TimerState.PAUSED;

public class TimerService extends Service {

    public static final String ACTION_FINISHED_UI = "com.techsial.apps.timewatcher.productivity.timer.FINISHED_UI";
    private static final String ACTION_FINISHED = "com.techsial.apps.timewatcher.productivity.timer.FINISHED";
    private static final int NOTIFICATION_ID = 1;
    private static final String TAG = "TimerService";
    private final IBinder mBinder = new TimerBinder();
    private long mCountDownFinishedTime;
    private int mRemainingTimePaused;
    private int mCurrentSessionStreak;
    private TimerState mTimerState;
    private LocalBroadcastManager mBroadcastManager;
    private int mPreviousRingerMode;
    private boolean mPreviousWifiMode;
    private Preferences mPref;
    private SessionType mCurrentSession;

    private BroadcastReceiver mAlarmReceiver;
    private AlarmManager mAlarmManager;

    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferences preferences = getApplicationContext().getSharedPreferences(
                PREFERENCES_NAME,
                Context.MODE_PRIVATE
        );
        mPref = new Preferences(preferences);
        mTimerState = INACTIVE;
        mCurrentSession = WORK;

        mBroadcastManager = LocalBroadcastManager.getInstance(this);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            this.unregisterReceiver(mAlarmReceiver);
        } catch (Throwable th) {
            // ignoring this exception
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    public void startSession(SessionType sessionType) {
        mCountDownFinishedTime = calculateEndTimeFor(sessionType);
        Log.i(TAG, "Starting new timer for " + sessionType + ", ending in "
                + getRemainingTime() + " seconds.");

        if (mPref.getDisableSoundAndVibration() && sessionType == WORK) {
            saveCurrentStateOfSound();
            disableSound();
        }
        if (mPref.getDisableWifi() && sessionType == WORK) {
            saveCurrentStateOfWifi();
            disableWifi();
        }

        mTimerState = ACTIVE;
        mCurrentSession = sessionType;
        setAlarm(mCountDownFinishedTime);
    }

    public void stopSession() {
        Log.d(TAG, "Session stopped");

        sendToBackground();

        if (mPref.getDisableSoundAndVibration()) {
            restoreSound();
        }
        if (mPref.getDisableWifi()) {
            restoreWifi();
        }
        if (mCurrentSession == LONG_BREAK) {
            resetCurrentSessionStreak();
        }

        mTimerState = INACTIVE;
        cancelAlarm();
    }

    private long calculateEndTimeFor(SessionType sessionType) {
        long currentTime = SystemClock.elapsedRealtime();
        switch (sessionType) {
            case WORK:
                return currentTime + TimeUnit.MINUTES.toMillis(mPref.getSessionDuration());
            case BREAK:
                return currentTime + TimeUnit.MINUTES.toMillis(mPref.getBreakDuration());
            case LONG_BREAK:
                return currentTime + TimeUnit.MINUTES.toMillis(mPref.getLongBreakDuration());
            default:
                throw new IllegalStateException("This cannot happen");
        }
    }

    public void pauseSession() {
        mTimerState = PAUSED;
        mRemainingTimePaused = getRemainingTime();
        cancelAlarm();
    }

    public void unPauseSession() {
        mTimerState = ACTIVE;
        mCountDownFinishedTime = SystemClock.elapsedRealtime() +
                TimeUnit.SECONDS.toMillis(mRemainingTimePaused);
        Log.i(TAG, "Resuming countdown for " + getSessionType() + ", ending in "
                + getRemainingTime() + " seconds.");
        setAlarm(mCountDownFinishedTime);
    }

    private void onCountdownFinished() {
        Log.d(TAG, "Countdown finished");

        if (mPref.getDisableSoundAndVibration()) {
            restoreSound();
        }
        if (mPref.getDisableWifi()) {
            restoreWifi();
        }

        sendFinishedNotification(!mPref.getContinuousMode());

        mTimerState = INACTIVE;
        sendToBackground();
    }

    private void saveCurrentStateOfSound() {
        AudioManager aManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        mPreviousRingerMode = aManager.getRingerMode();
    }

    private void saveCurrentStateOfWifi() {
        WifiManager wifiManager = (WifiManager) this.getSystemService(WIFI_SERVICE);
        mPreviousWifiMode = wifiManager.isWifiEnabled();
    }

    private void disableSound() {
        Log.d(TAG, "Disabling sound");
        AudioManager aManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        aManager.setRingerMode(RINGER_MODE_SILENT);
    }

    private void disableWifi() {
        Log.d(TAG, "Disabling Wifi");
        WifiManager wifiManager = (WifiManager) this.getSystemService(WIFI_SERVICE);
        wifiManager.setWifiEnabled(false);
    }

    private void restoreSound() {
        Log.d(TAG, "Restoring sound mode");
        AudioManager aManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        aManager.setRingerMode(mPreviousRingerMode);
    }

    private void restoreWifi() {
        Log.d(TAG, "Restoring Wifi mode");
        WifiManager wifiManager = (WifiManager) this.getSystemService(WIFI_SERVICE);
        wifiManager.setWifiEnabled(mPreviousWifiMode);
    }

    private void sendFinishedNotification(boolean addButtons) {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = createCompletionNotification(
                this,
                mCurrentSession,
                addButtons
        );

        if (mPref.getNotificationVibrate()) {
            notification.vibrate = new long[]{0, 300, 700, 300};
        }

        if (SDK_INT >= LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build();
            notification.audioAttributes = audioAttributes;
        } else {
            notification.audioStreamType = STREAM_ALARM;
        }

        notification.sound = Uri.parse(mPref.getNotificationSound());
        mNotificationManager.notify(
                NOTIFICATION_TAG,
                notification
        );
    }

    public boolean isTimerRunning() {
        return mTimerState == ACTIVE;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public TimerState getTimerState() {
        return mTimerState;
    }

    public SessionType getSessionType() {
        return mCurrentSession;
    }

    public int getCurrentSessionStreak() {
        return mCurrentSessionStreak;
    }

    public int getRemainingTime() {
        return (int) (TimeUnit.MILLISECONDS.toSeconds(
                mCountDownFinishedTime - SystemClock.elapsedRealtime()));
    }

    public int getRemainingTimePaused() {
        return mRemainingTimePaused;
    }

    public void increaseCurrentSessionStreak() {
        mCurrentSessionStreak++;
    }

    public void resetCurrentSessionStreak() {
        mCurrentSessionStreak = 0;
    }

    protected void bringToForeground() {
        startForeground(
                NOTIFICATION_ID,
                createForegroundNotification(this, mCurrentSession, mTimerState, getRemainingTime())
        );
    }

    protected void sendToBackground() {
        stopForeground(true);
    }

    private void setAlarm(long countDownTime) {
        Log.w(TAG, "Alarm set.");
        mAlarmReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent _) {
                onCountdownFinished();
                context.unregisterReceiver(this);
                Intent finishedIntent = new Intent(ACTION_FINISHED_UI);
                mBroadcastManager.sendBroadcast(finishedIntent);
            }
        };

        this.registerReceiver(mAlarmReceiver, new IntentFilter(ACTION_FINISHED));

        PendingIntent intent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_FINISHED), 0);
        mAlarmManager = (AlarmManager) (this.getSystemService(Context.ALARM_SERVICE));

        if (SDK_INT >= Build.VERSION_CODES.M) {
            mAlarmManager.setExactAndAllowWhileIdle(ELAPSED_REALTIME_WAKEUP, countDownTime, intent);
        } else if (SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mAlarmManager.setExact(ELAPSED_REALTIME_WAKEUP, countDownTime, intent);
        } else {
            mAlarmManager.set(ELAPSED_REALTIME_WAKEUP, countDownTime, intent);
        }
    }

    private void cancelAlarm() {
        Log.w(TAG, "Alarm canceled.");
        PendingIntent intent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_FINISHED), 0);
        if (mAlarmManager != null) {
            mAlarmManager.cancel(intent);
        }

        try {
            this.unregisterReceiver(mAlarmReceiver);
        } catch (IllegalArgumentException e) {
            Log.w(TAG, "AlarmReceiver is already unregistered.");
        }
    }

    public class TimerBinder extends Binder {
        TimerService getService() {
            return TimerService.this;
        }
    }

}
