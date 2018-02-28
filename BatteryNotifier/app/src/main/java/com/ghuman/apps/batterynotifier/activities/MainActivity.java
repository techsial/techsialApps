package com.ghuman.apps.batterynotifier.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.ghuman.apps.batterynotifier.BuildConfig;
import com.ghuman.apps.batterynotifier.R;
import com.ghuman.apps.batterynotifier.services.BatteryService;
import com.ghuman.apps.batterynotifier.util.DeviceInfo;
import com.ghuman.apps.batterynotifier.util.PopupDialogs;
import com.ghuman.apps.batterynotifier.util.PreferenceUtility;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.NativeExpressAdView;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private CheckBox checkBoxTwentyPercent, checkBoxThirtyPercent, checkBoxFortyPercent,
            checkBoxFiftyPercent, checkBoxSixtyPercent, checkBoxSeventyPercent,
            checkBoxEightyPercent, checkBoxNintyPercent;
    private Button btnSaveChanges, btnActivateDeactivate;
    private TextView tvBatteryInfo;
    private InterstitialAd mInterstitialAd;
    private Dialog rateAppDialog;
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setIcon(R.drawable.app_icon_bar);

        setUI();
        MobileAds.initialize(this, getString(R.string.app_id_admob));

        showInterstitialAd();

        try {
            if (checkInternet(MainActivity.this)) {
                NativeExpressAdView adView = (NativeExpressAdView) findViewById(R.id.adViewNativeMainScreen);
                adView.setVisibility(View.VISIBLE);
                AdRequest request = new AdRequest.Builder().build();
                adView.loadAd(request);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        startService(new Intent(MainActivity.this, BatteryService.class));
        startBatteryServiceAgainAndAgain();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (!PreferenceUtility.readBoolean(MainActivity.this, PreferenceUtility.IS_RATED, false)) {
            rateAppDialog = PopupDialogs.createRateAppDialog(MainActivity.this, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rateAppDialog.dismiss();
                    finish();
                }
            }, new PopupDialogs.OnCustomClickListener() {
                @Override
                public void onClick(View v, float rating) {
                    if (rating >= 4.8) {
                        openPlayStorePage();
                    } else {
                        Toast.makeText(MainActivity.this, "Thank you for rating Battery Level Alerts!", Toast.LENGTH_LONG).show();
                    }
                    PreferenceUtility.writeBoolean(MainActivity.this, PreferenceUtility.IS_RATED, true);
                    rateAppDialog.dismiss();
                }
            });
        } else {
            if (doubleBackToExitPressedOnce) {
                showInterstitialAd();
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
//        if (doubleBackToExitPressedOnce) {
//            showInterstitialAd();
//            super.onBackPressed();
//            return;
//        }
//
//        this.doubleBackToExitPressedOnce = true;
//        Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();
//
//        new Handler().postDelayed(new Runnable() {
//
//            @Override
//            public void run() {
//                doubleBackToExitPressedOnce = false;
//            }
//        }, 2000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_help:
                Intent aboutIntent = new Intent(this, HelpActivity.class);
                startActivity(aboutIntent);
                showInterstitialAd();
                break;
            case R.id.menu_share_invite:
                shareOrInvite();
                break;
            case R.id.menu_video_editor_tools:
                openVideoEditorToolsPage();
                break;
            case R.id.menu_time_zone_converter:
                openTimeZoneConverterPage();
                break;
            case R.id.menu_time_watcher:
                openTimeWatcherPage();
                break;
            case R.id.menu_sms_scheduler:
                openSmsSchedulerPage();
                break;
//            case R.id.menu_view_more:
//                viewMoreApps();
//                break;
            case R.id.menu_send_feedback:
                sendFeedback();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setUI() {
        btnSaveChanges = (Button) findViewById(R.id.btnSaveChanges);
        btnActivateDeactivate = (Button) findViewById(R.id.btnActivteDeactivate);

        if (PreferenceUtility.readBoolean(MainActivity.this, PreferenceUtility.IS_DEACTIVATED_NOTIFICATIONS, false)) {
            btnActivateDeactivate.setText("ACTIVATE NOTIFICATIONS");
            PreferenceUtility.writeBoolean(MainActivity.this, PreferenceUtility.IS_DEACTIVATED_NOTIFICATIONS, true);
        } else {
            btnActivateDeactivate.setText("DEACTIVATE NOTIFICATIONS");
            PreferenceUtility.writeBoolean(MainActivity.this, PreferenceUtility.IS_DEACTIVATED_NOTIFICATIONS, false);
        }

        btnSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInterstitialAd();
                if (checkBoxTwentyPercent.isChecked()) {
                    PreferenceUtility.writeBoolean(MainActivity.this, PreferenceUtility.CHECK_TWENTY_PERCENT, true);
                } else {
                    PreferenceUtility.writeBoolean(MainActivity.this, PreferenceUtility.CHECK_TWENTY_PERCENT, false);
                }
                if (checkBoxThirtyPercent.isChecked()) {
                    PreferenceUtility.writeBoolean(MainActivity.this, PreferenceUtility.CHECK_THIRTY_PERCENT, true);
                } else {
                    PreferenceUtility.writeBoolean(MainActivity.this, PreferenceUtility.CHECK_THIRTY_PERCENT, false);
                }
                if (checkBoxFortyPercent.isChecked()) {
                    PreferenceUtility.writeBoolean(MainActivity.this, PreferenceUtility.CHECK_FORTY_PERCENT, true);
                } else {
                    PreferenceUtility.writeBoolean(MainActivity.this, PreferenceUtility.CHECK_FORTY_PERCENT, false);
                }
                if (checkBoxFiftyPercent.isChecked()) {
                    PreferenceUtility.writeBoolean(MainActivity.this, PreferenceUtility.CHECK_FIFTY_PERCENT, true);
                } else {
                    PreferenceUtility.writeBoolean(MainActivity.this, PreferenceUtility.CHECK_FIFTY_PERCENT, false);
                }
                if (checkBoxSixtyPercent.isChecked()) {
                    PreferenceUtility.writeBoolean(MainActivity.this, PreferenceUtility.CHECK_SIXTY_PERCENT, true);
                } else {
                    PreferenceUtility.writeBoolean(MainActivity.this, PreferenceUtility.CHECK_SIXTY_PERCENT, false);
                }
                if (checkBoxSeventyPercent.isChecked()) {
                    PreferenceUtility.writeBoolean(MainActivity.this, PreferenceUtility.CHECK_SEVENTY_PERCENT, true);
                } else {
                    PreferenceUtility.writeBoolean(MainActivity.this, PreferenceUtility.CHECK_SEVENTY_PERCENT, false);
                }
                if (checkBoxEightyPercent.isChecked()) {
                    PreferenceUtility.writeBoolean(MainActivity.this, PreferenceUtility.CHECK_EIGHTY_PERCENT, true);
                } else {
                    PreferenceUtility.writeBoolean(MainActivity.this, PreferenceUtility.CHECK_EIGHTY_PERCENT, false);
                }
                if (checkBoxNintyPercent.isChecked()) {
                    PreferenceUtility.writeBoolean(MainActivity.this, PreferenceUtility.CHECK_NINTY_PERCENT, true);
                } else {
                    PreferenceUtility.writeBoolean(MainActivity.this, PreferenceUtility.CHECK_NINTY_PERCENT, false);
                }
                Toast.makeText(MainActivity.this, "Changes Saved Successfully.", Toast.LENGTH_LONG).show();
            }
        });

        btnActivateDeactivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInterstitialAd();
                if (PreferenceUtility.readBoolean(MainActivity.this, PreferenceUtility.IS_DEACTIVATED_NOTIFICATIONS, false)) {
                    btnActivateDeactivate.setText("DEACTIVATE NOTIFICATIONS");
                    PreferenceUtility.writeBoolean(MainActivity.this, PreferenceUtility.IS_DEACTIVATED_NOTIFICATIONS, false);
                    Toast.makeText(MainActivity.this, "Battery Notifications Activated.", Toast.LENGTH_SHORT).show();
                } else {
                    btnActivateDeactivate.setText("ACTIVATE NOTIFICATIONS");
                    PreferenceUtility.writeBoolean(MainActivity.this, PreferenceUtility.IS_DEACTIVATED_NOTIFICATIONS, true);
                    Toast.makeText(MainActivity.this, "Battery Notifications Deactivated.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        checkBoxTwentyPercent = (CheckBox) findViewById(R.id.checkTwentyPercent);
        checkBoxThirtyPercent = (CheckBox) findViewById(R.id.checkThirtyPercent);
        checkBoxFortyPercent = (CheckBox) findViewById(R.id.checkFortyPercent);
        checkBoxFiftyPercent = (CheckBox) findViewById(R.id.checkFiftyPercent);
        checkBoxSixtyPercent = (CheckBox) findViewById(R.id.checkSixtyPercent);
        checkBoxSeventyPercent = (CheckBox) findViewById(R.id.checkSeventyPercent);
        checkBoxEightyPercent = (CheckBox) findViewById(R.id.checkEightyPercent);
        checkBoxNintyPercent = (CheckBox) findViewById(R.id.checkNintyPercent);

        checkBoxTwentyPercent.setChecked(PreferenceUtility.readBoolean(MainActivity.this, PreferenceUtility.CHECK_TWENTY_PERCENT, true));
        checkBoxThirtyPercent.setChecked(PreferenceUtility.readBoolean(MainActivity.this, PreferenceUtility.CHECK_THIRTY_PERCENT, true));
        checkBoxFortyPercent.setChecked(PreferenceUtility.readBoolean(MainActivity.this, PreferenceUtility.CHECK_FORTY_PERCENT, true));
        checkBoxFiftyPercent.setChecked(PreferenceUtility.readBoolean(MainActivity.this, PreferenceUtility.CHECK_FIFTY_PERCENT, true));
        checkBoxSixtyPercent.setChecked(PreferenceUtility.readBoolean(MainActivity.this, PreferenceUtility.CHECK_SIXTY_PERCENT, true));
        checkBoxSeventyPercent.setChecked(PreferenceUtility.readBoolean(MainActivity.this, PreferenceUtility.CHECK_SEVENTY_PERCENT, true));
        checkBoxEightyPercent.setChecked(PreferenceUtility.readBoolean(MainActivity.this, PreferenceUtility.CHECK_EIGHTY_PERCENT, true));
        checkBoxNintyPercent.setChecked(PreferenceUtility.readBoolean(MainActivity.this, PreferenceUtility.CHECK_NINTY_PERCENT, true));

        tvBatteryInfo = (TextView) findViewById(R.id.tvBatteryLevel);

        registerBatteryLevelReceiver();
    }

    private void startBatteryServiceAgainAndAgain() {

        new Handler().postDelayed(new Runnable() {
            public void run() {
//                Toast.makeText(MainActivity.this, "Service ALARM...", Toast.LENGTH_SHORT).show();
                startService(new Intent(MainActivity.this, BatteryService.class));
                startBatteryServiceAgainAndAgain();
            }
        }, 20000);
    }


    private void registerBatteryLevelReceiver() {
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);

        registerReceiver(battery_receiver, filter);
    }

    private BroadcastReceiver battery_receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isPresent = intent.getBooleanExtra("present", false);
            String technology = intent.getStringExtra("technology");
            int plugged = intent.getIntExtra("plugged", -1);
            int scale = intent.getIntExtra("scale", -1);
            int health = intent.getIntExtra("health", 0);
            int status = intent.getIntExtra("status", 0);
            int rawlevel = intent.getIntExtra("level", -1);
            float voltage = intent.getIntExtra("voltage", 0);
            float temperature = intent.getIntExtra("temperature", 0);
            int level = 0;

            Bundle bundle = intent.getExtras();

            Log.i("BatteryLevel", bundle.toString());

            if (isPresent) {
                if (rawlevel >= 0 && scale > 0) {
                    level = (rawlevel * 100) / scale;
                }

                String info = "Battery Level:     " + level + "%\n";
                info += ("Technology:     " + technology + "\n");
                info += ("Plugged:     " + getPlugTypeString(plugged) + "\n");
                info += ("Health:     " + getHealthString(health) + "\n");
                info += ("Status:     " + getStatusString(status) + "\n");
                info += ("Voltage:     " + voltage / 1000 + " V" + "\n");
                info += ("Temperature:     " + temperature / 9 + " C");

                setBatteryLevelText(info);
//                + "\n\n" + bundle.toString()
            } else {
                setBatteryLevelText("Battery not present!!!");
            }
        }
    };

    private String getPlugTypeString(int plugged) {
        String plugType = "Unknown";

        switch (plugged) {
            case BatteryManager.BATTERY_PLUGGED_AC:
                plugType = "AC";
                break;
            case BatteryManager.BATTERY_PLUGGED_USB:
                plugType = "USB";
                break;
        }

        return plugType;
    }

    private String getHealthString(int health) {
        String healthString = "Unknown";

        switch (health) {
            case BatteryManager.BATTERY_HEALTH_DEAD:
                healthString = "Dead";
                break;
            case BatteryManager.BATTERY_HEALTH_GOOD:
                healthString = "Good";
                break;
            case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                healthString = "Over Voltage";
                break;
            case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                healthString = "Over Heat";
                break;
            case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                healthString = "Failure";
                break;
        }

        return healthString;
    }

    private String getStatusString(int status) {
        String statusString = "Unknown";

        switch (status) {
            case BatteryManager.BATTERY_STATUS_CHARGING:
                statusString = "Charging";
                break;
            case BatteryManager.BATTERY_STATUS_DISCHARGING:
                statusString = "Discharging";
                break;
            case BatteryManager.BATTERY_STATUS_FULL:
                statusString = "Full";
                break;
            case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                statusString = "Not Charging";
                break;
        }

        return statusString;
    }

    private void setBatteryLevelText(String text) {
        tvBatteryInfo.setText(text);
    }

    private void showInterstitialAd() {
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_id_main_screen));

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

    private void shareOrInvite() {

        final String appPackageName = getPackageName();

        ShareCompat.IntentBuilder
                .from(this) // getActivity() or activity field if within Fragment
                .setText(getString(R.string.invitation_message) + "\n\nPlease download/install from:\nhttps://play.google.com/store/apps/details?id=" + appPackageName)
                .setType("text/plain") // most general text sharing MIME type
                .setChooserTitle("Share " + getString(R.string.app_name))
                .startChooser();
    }

    private void openVideoEditorToolsPage() {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.techsial.apps.video_to_mp3.audio_extractor")));

    }

    private void openTimeZoneConverterPage() {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.techsial.apps.timezones")));

    }

    private void openTimeWatcherPage() {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.techsial.apps.timewatcher.productivity.timer")));

    }

    private void openSmsSchedulerPage() {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.techsial.apps.simplesmsscheduler")));

    }

    public void viewMoreApps() {
        Intent viewMoreIntent = new Intent(Intent.ACTION_VIEW);
        viewMoreIntent.setData(Uri.parse("https://play.google.com/store/search?q=techsial&c=apps"));
        startActivity(viewMoreIntent);
    }

    private void sendFeedback() {
        Intent email = new Intent(Intent.ACTION_SENDTO);
        email.setData(new Uri.Builder().scheme("mailto").build());
        email.putExtra(Intent.EXTRA_EMAIL, new String[]{"techsial16@gmail.com"});
        email.putExtra(Intent.EXTRA_SUBJECT, "Feedback for " + getString(R.string.app_name));
        email.putExtra(Intent.EXTRA_TEXT, "\nMy device info: \n" + DeviceInfo.getDeviceInfo()
                + "\nApp version: " + BuildConfig.VERSION_NAME
                + "\nFeedback:" + "\n");
        try {
            startActivity(Intent.createChooser(email, "Send feedback"));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, R.string.about_no_email, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkInternet(Context con) {

        ConnectivityManager connectivity = (ConnectivityManager) con
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
        }
        return false;
    }

    private void openPlayStorePage() {
        final String appPackageName = getPackageName();
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));

    }
}
