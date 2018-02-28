package com.techsial.apps.video_to_mp3.audio_extractor.activity;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.techsial.apps.video_to_mp3.audio_extractor.BuildConfig;
import com.techsial.apps.video_to_mp3.audio_extractor.R;
import com.techsial.apps.video_to_mp3.audio_extractor.utils.PopupDialogs;
import com.techsial.apps.video_to_mp3.audio_extractor.utils.PreferenceManager;


public class MainMenuActivity extends BaseActivity {

    private GridView gridView;
    private MainMenuAdapter mainMenuAdapter;
    private ImageButton info;
    private Dialog rateAppDialog;
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

//        info = (ImageButton) findViewById(R.id.ib_info);
//        info.setVisibility(View.VISIBLE);
//        info.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showInterstitialAd();
//                Intent intent = new Intent(MainMenuActivity.this, HelpActivity.class);
//                startActivity(intent);
//            }
//        });

//        showInterstitialAd();
        gridView = (GridView) findViewById(R.id.gridview);
        mainMenuAdapter = new MainMenuAdapter(MainMenuActivity.this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    synchronized (this) {
                        wait(500);
                    }
                } catch (InterruptedException ex) {

                }
                gridView.post(new Runnable() {
                    @Override
                    public void run() {
                        gridView.setAdapter(mainMenuAdapter);
                    }
                });
            }
        }).start();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (!PreferenceManager.readBoolean(MainMenuActivity.this, PreferenceManager.IS_RATED, false)) {
            rateAppDialog = PopupDialogs.createRateAppDialog(MainMenuActivity.this, new View.OnClickListener() {
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
                        Toast.makeText(MainMenuActivity.this, "Thank you for rating Video Editor Tools!", Toast.LENGTH_LONG).show();
                    }
                    PreferenceManager.writeBoolean(MainMenuActivity.this, PreferenceManager.IS_RATED, true);
                    rateAppDialog.dismiss();
                }
            });
        } else {
            if (doubleBackToExitPressedOnce) {
//                showInterstitialAd();
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
            case R.id.menu_time_watcher:
                openTimeWatcherPage();
                break;
            case R.id.menu_share_invite:
                shareOrInvite();
                break;
//            case R.id.menu_rate_app:
//                openPlayStorePage();
//                break;
            case R.id.menu_view_more:
                viewMoreApps();
                break;
            case R.id.menu_send_feedback:
                sendFeedback();
                break;
        }

        return super.onOptionsItemSelected(item);
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

    private void openTimeWatcherPage() {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.techsial.apps.timewatcher.productivity.timer")));

    }

    private void openPlayStorePage() {
        final String appPackageName = getPackageName();
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));

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
}