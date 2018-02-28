package com.ghuman.apps.torch;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;
import java.util.List;

public class TorchActivity extends Activity implements View.OnClickListener {

    private Camera cam;
    private Button btnShareOrinvite, btnRateApp, btnViewMore, btnScreenLight,
            btnOnOff, btnPress, btnSilentMode, btnTimer, btnTick, btnCross;
    private boolean isTorchOn, isSilentMode, isFlashLightSupported, isBtnPressed;
    private TextView tvBatteryPercentage, tvCountDown;
    private EditText etTimeInput;
    private MediaPlayer mediaPlayer;
    private LinearLayout layoutTimer;
    private long minutes, miliSeconds;
    private CountDownTimer timer;
    private int bgCount = 0;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    private Dialog rateAppDialog;
    private boolean doubleBackToExitPressedOnce = false;

    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context ctxt, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            tvBatteryPercentage.setText(String.valueOf(level) + " %");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_torch);


        int MyVersion = Build.VERSION.SDK_INT;
        if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (!checkIfAlreadyhavePermission()) {
                requestForSpecificPermission();
            }
        }

        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this, getString(R.string.app_id_admob));

//        showInterstitialAd();
        showBannerAd();

        setUI();

        this.registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnShareOrInvite:
                shareOrInvite();
                break;
            case R.id.btnRateApp:
                openPlayStorePage();
                break;
            case R.id.btnViewMore:
                openMarketUrl();
                break;
            case R.id.btnScreenLight:
                if (isTorchOn) {
                    flashLightOff();
//                    btnOnOff.setBackgroundResource(R.drawable.btn_off);
//                    viewIndicator.setBackgroundResource(R.color.red_dark);
                }
                Intent intent = new Intent(TorchActivity.this, ScreenLightActivity.class);
                intent.putExtra("IS_SILENT_MODE", isSilentMode);
                startActivity(intent);
                break;
            case R.id.btnOnOff:
                showBannerAd();
//                showInterstitialAd();
                if (!isTorchOn) {
                    playSound(R.raw.switch_sound);
                    flashLightOn();
                    if (isFlashSupported()) {
//                        btnOnOff.setBackgroundResource(R.drawable.btn_on);
//                        viewIndicator.setBackgroundResource(R.color.green_light);
                    }

                } else {
//                    btnOnOff.setBackgroundResource(R.drawable.btn_off);
//                    viewIndicator.setBackgroundResource(R.color.red_dark);
                    playSound(R.raw.switch_sound);
                    flashLightOff();
                }
                break;

            case R.id.btnSilentMode:
                showBannerAd();
//                showInterstitialAd();
                if (!isSilentMode) {
                    btnSilentMode.setBackgroundResource(R.drawable.btn_silent_on);
                    isSilentMode = true;
                } else {
                    btnSilentMode.setBackgroundResource(R.drawable.btn_silent_off);
                    isSilentMode = false;
                    playSound(R.raw.button_click);
                }
                break;
            case R.id.btnTimer:
                showBannerAd();
//                showInterstitialAd();
                layoutTimer.setVisibility(View.VISIBLE);
                tvCountDown.setVisibility(View.GONE);
                break;
            case R.id.btnCross:
                showBannerAd();
//                showInterstitialAd();
                if (timer != null) {
                    timer.cancel();
                }
                layoutTimer.setVisibility(View.GONE);
                tvCountDown.setVisibility(View.GONE);
                break;
            case R.id.btnTick:
                showBannerAd();
//                showInterstitialAd();
                layoutTimer.setVisibility(View.GONE);
                if (!etTimeInput.getText().toString().equals("")) {
                    tvCountDown.setVisibility(View.VISIBLE);
                    minutes = Integer.parseInt(etTimeInput.getText().toString());
                    miliSeconds = minutes * 60 * 1000;
                    if (timer != null) {
                        timer.cancel();
                    }
                    timer = new CountDownTimer(miliSeconds, 1000) {

                        public void onTick(long millisUntilFinished) {
                            long hrs, mins, secs;
                            String strHrs, strMins, strSecs;
                            hrs = millisUntilFinished / 3600000;
                            mins = (millisUntilFinished % 3600000) / 60000;
                            secs = (millisUntilFinished % 60000) / 1000;

                            strHrs = "" + hrs;
                            strMins = "" + mins;
                            strSecs = "" + secs;

                            if (hrs < 10) {
                                strHrs = "0" + hrs;
                            }
                            if (mins < 10) {
                                strMins = "0" + mins;
                            }
                            if (secs < 10) {
                                strSecs = "0" + secs;
                            }
                            tvCountDown.setText(strHrs + ":" + strMins + ":" + strSecs);
                            //here you can have your logic to set text to edittext
                        }

                        public void onFinish() {
                            tvCountDown.setText("done!");
                            if (isTorchOn) {
                                flashLightOff();
//                                btnOnOff.setBackgroundResource(R.drawable.btn_off);
//                                viewIndicator.setBackgroundResource(R.color.red_dark);
                            }
                        }

                    };
                    timer.start();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {

        if (!PreferenceManager.readBoolean(TorchActivity.this, PreferenceManager.IS_RATED, false)) {
            rateAppDialog = PopupDialogs.createRateAppDialog(TorchActivity.this, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rateAppDialog.dismiss();
                    finish();
                }
            }, new PopupDialogs.OnCustomClickListener() {
                @Override
                public void onClick(View v, float rating) {
                    if (rating >= 4.5) {
                        openPlayStorePage();
                    } else {
                        Toast.makeText(TorchActivity.this, "Thank you for rating Timer Torch!", Toast.LENGTH_LONG).show();
                    }
                    PreferenceManager.writeBoolean(TorchActivity.this, PreferenceManager.IS_RATED, true);
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    protected void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        if (bgCount == 0) {
//            loadFullscreen();
        }
        if (bgCount == 3) {
            bgCount = 0;
        } else {
            bgCount++;
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

    private void setUI() {
        btnShareOrinvite = (Button) findViewById(R.id.btnShareOrInvite);
        btnRateApp = (Button) findViewById(R.id.btnRateApp);
        btnViewMore = (Button) findViewById(R.id.btnViewMore);
        btnScreenLight = (Button) findViewById(R.id.btnScreenLight);
        btnOnOff = (Button) findViewById(R.id.btnOnOff);
        btnPress = (Button) findViewById(R.id.btnPress);
        btnSilentMode = (Button) findViewById(R.id.btnSilentMode);
        btnTimer = (Button) findViewById(R.id.btnTimer);
        btnCross = (Button) findViewById(R.id.btnCross);
        btnTick = (Button) findViewById(R.id.btnTick);
//        viewIndicator = (View) findViewById(R.id.viewIndicator);
        tvBatteryPercentage = (TextView) findViewById(R.id.tvBatteryPercentage);
        tvCountDown = (TextView) findViewById(R.id.tvCountDown);
        etTimeInput = (EditText) findViewById(R.id.etTimeInput);
        layoutTimer = (LinearLayout) findViewById(R.id.layoutTimer);

        btnShareOrinvite.setOnClickListener(this);
        btnRateApp.setOnClickListener(this);
        btnViewMore.setOnClickListener(this);
        btnScreenLight.setOnClickListener(this);
        btnOnOff.setOnClickListener(this);
        btnSilentMode.setOnClickListener(this);
        btnTimer.setOnClickListener(this);
        btnCross.setOnClickListener(this);
        btnTick.setOnClickListener(this);

        btnPress.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (!isTorchOn) {
                        flashLightOn();
                        if (isFlashSupported()) {
//                            viewIndicator.setBackgroundResource(R.color.green_light);
                        }
                        isBtnPressed = true;
                    }
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (isBtnPressed) {
                        flashLightOff();
//                        viewIndicator.setBackgroundResource(R.color.red_dark);
                        isBtnPressed = false;
                    }
                }
                return false;
            }
        });

        isTorchOn = false;
        isSilentMode = false;
        isFlashLightSupported = true;
        isBtnPressed = false;

    }

    public void flashLightOn() {
        try {

            if (isFlashSupported()) {
                isFlashLightSupported = false;

                cam = Camera.open();
                Camera.Parameters p = cam.getParameters();
                List<String> flashModes = p.getSupportedFlashModes();
                if (flashModes != null) {
                    for (int i = 0; i < flashModes.size(); i++) {
                        if (flashModes.get(i).equals("torch")) {
                            p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                            cam.setParameters(p);
                            cam.startPreview();
                            isTorchOn = true;
                            isFlashLightSupported = true;
                        }
                    }
                }
                if (!isFlashLightSupported) {
                    showNoFlashAlert();
                }
            } else {
                showNoFlashAlert();
            }
        } catch (Exception e) {
            e.printStackTrace();
//            Toast.makeText(getBaseContext(), "Exception flashLightOn()",
//                    Toast.LENGTH_SHORT).show();
        }

    }

    public void flashLightOff() {
        try {
            if (isFlashSupported()) {
                cam.stopPreview();
                cam.release();
                isTorchOn = false;
                cam = null;
            } else {
                showNoFlashAlert();
            }
        } catch (Exception e) {
            e.printStackTrace();
//            Toast.makeText(getBaseContext(), "Exception flashLightOff",
//                    Toast.LENGTH_SHORT).show();
        }
    }

    private void showNoFlashAlert() {
        new AlertDialog.Builder(this)
                .setMessage("Flashlight is not supported by the device hardware.")
                .setIcon(android.R.drawable.ic_dialog_alert).setTitle("Alert Notification")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    private boolean isFlashSupported() {
//        PackageManager pm = getPackageManager()
        return isFlashLightSupported;
    }

    private void playSound(int id) {
        if (!isSilentMode) {
            mediaPlayer = MediaPlayer.create(TorchActivity.this, id);

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    // TODO Auto-generated method stub
                    mp.release();
                }
            });
            mediaPlayer.start();
        }
    }

    public void openMarketUrl() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://play.google.com/store/search?q=techsial&c=apps"));
        startActivity(intent);
    }

    private boolean checkIfAlreadyhavePermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestForSpecificPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 101);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //granted
                } else {
                    Toast.makeText(TorchActivity.this, "Camera Permission is Required", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void showBannerAd() {
        mAdView = (AdView) findViewById(R.id.ad_view_banner);

        AdRequest adRequest = new AdRequest.Builder().build();

        // Start loading the ad in the background.
        mAdView.loadAd(adRequest);

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

    private void openPlayStorePage() {
        final String appPackageName = getPackageName();
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));

    }

}