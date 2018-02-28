package com.ghuman.apps.torch;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

public class ScreenLightActivity extends Activity implements View.OnClickListener {

    private Button btnPowerOnOff, btnColorLight, btnColorTransition;
    private boolean isScreenLightOn, isSilentMode;
    private MediaPlayer mediaPlayer;
    private RelativeLayout layoutParent;
    private AdView mAdViewTop, mAdViewBottom;
    private InterstitialAd mInterstitialAd;

    private int lightNo, fromColor, toColor;
    private int colors[] = {R.color.screenLightOne, R.color.screenLightTwo, R.color.screenLightThree
            , R.color.screenLightFour, R.color.screenLightFive, R.color.screenLightSix, R.color.screenLightSeven
            , R.color.screenLightEight, R.color.screenLightNine, R.color.screenLightTen, R.color.screenLightEleven
            , R.color.screenLightTwelve, R.color.screenLightThirteen, R.color.screenLightForteen, R.color.screenLightFifteen
            , R.color.screenLightSixteen, R.color.screenLightSeventeen, R.color.screenLightEighteen, R.color.screenLightNinteen
            , R.color.screenLightTwenty, R.color.screenLightTwentyOne, R.color.screenLightTwentyTwo, R.color.screenLightTwentyThree
            , R.color.screenLightTwentyFour};
    private String strColors[] = {"#ff00ff80", "#ff00ffbf", "#ff00ffff", "#ff00bfff", "#ff0080ff"
            , "#ff0040ff", "#ff0000ff", "#ff4000ff", "#ff8000ff", "#ffbf00ff"
            , "#ffff00ff", "#ffff00bf", "#ffff0080", "#ffff0040", "#ffff0000"
            , "#ffff4000", "#ffff8000", "#ffffbf00", "#ffffff00", "#ffbfff00"
            , "#ff80ff00", "#ff40ff00", "#ff00ff00", "#ff00ff40"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_light);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setUI();

        isSilentMode = getIntent().getBooleanExtra("IS_SILENT_MODE", true);
        isScreenLightOn = false;

        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this, getString(R.string.app_id_admob));

        showBottomBanner();
//        showInterstitialAd();
    }

    @Override
    public void onClick(View v) {
        showBottomBanner();
//        showInterstitialAd();

        switch (v.getId()) {
            case R.id.btnColorTransition:
                btnColorLight.setVisibility(View.GONE);
                playSound();
                isScreenLightOn = true;
                fromColor = 0;
                toColor = 1;

                animateColors();
                break;
            case R.id.btnPowerOnOff:
                if (!isScreenLightOn) {
                    btnColorTransition.setVisibility(View.GONE);
                    btnColorLight.setVisibility(View.GONE);
//                    btnPowerOnOff.setBackgroundResource(R.drawable.btn_power_off);
                    layoutParent.setBackgroundResource(R.color.white);
                    isScreenLightOn = true;
                    playSound();

                } else {
                    btnColorTransition.setVisibility(View.VISIBLE);
                    btnColorLight.setVisibility(View.VISIBLE);
//                    btnPowerOnOff.setBackgroundResource(R.drawable.btn_power_on);
                    layoutParent.setBackgroundResource(R.drawable.repeat_bg);
                    isScreenLightOn = false;
                    playSound();
                }
                break;
            case R.id.btnColorLight:
                btnColorTransition.setVisibility(View.GONE);
                layoutParent.setBackgroundResource(colors[lightNo]);
                if (lightNo < 23) {
                    lightNo++;
                } else {
                    lightNo = 0;
                }
                isScreenLightOn = true;
                playSound();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        showInterstitialAd();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        if (mAdViewTop != null) {
            mAdViewTop.destroy();
        }
        if (mAdViewBottom != null) {
            mAdViewBottom.destroy();
        }
        super.onDestroy();
    }

    private void setUI() {
        btnColorTransition = (Button) findViewById(R.id.btnColorTransition);
        btnPowerOnOff = (Button) findViewById(R.id.btnPowerOnOff);
        btnColorLight = (Button) findViewById(R.id.btnColorLight);
        layoutParent = (RelativeLayout) findViewById(R.id.layoutParent);

        WindowManager.LayoutParams layout = getWindow().getAttributes();
        layout.screenBrightness = 1F;
        getWindow().setAttributes(layout);
        btnColorTransition.setOnClickListener(ScreenLightActivity.this);
        btnPowerOnOff.setOnClickListener(ScreenLightActivity.this);
        btnColorLight.setOnClickListener(ScreenLightActivity.this);
        lightNo = 0;
    }

    private void playSound() {
        if (!isSilentMode) {
            mediaPlayer = MediaPlayer.create(ScreenLightActivity.this, R.raw.button_click);

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

    private void animateColors() {
        if (isScreenLightOn) {
            final float[] from = new float[3],
                    to = new float[3];

            Color.colorToHSV(Color.parseColor(strColors[fromColor]), from);   // from white
            Color.colorToHSV(Color.parseColor(strColors[toColor]), to);     // to red

            final ValueAnimator anim = ValueAnimator.ofFloat(0, 1);   // animate from 0 to 1
            anim.setDuration(1000);                              // for 300 ms

            final float[] hsv = new float[3];                  // transition color
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    // Transition along each axis of HSV (hue, saturation, value)
                    hsv[0] = from[0] + (to[0] - from[0]) * animation.getAnimatedFraction();
                    hsv[1] = from[1] + (to[1] - from[1]) * animation.getAnimatedFraction();
                    hsv[2] = from[2] + (to[2] - from[2]) * animation.getAnimatedFraction();

                    layoutParent.setBackgroundColor(Color.HSVToColor(hsv));
                }
            });

            anim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    if (fromColor < 23) {
                        fromColor++;
                    } else {
                        fromColor = 0;
                    }

                    if (toColor < 23) {
                        toColor++;
                    } else {
                        toColor = 0;
                    }

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    animateColors();
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });

            anim.start();
        } else {
            layoutParent.setBackgroundResource(R.drawable.repeat_bg);
            isScreenLightOn = false;
            playSound();
        }
    }

    private void showBottomBanner() {
        mAdViewBottom = (AdView) findViewById(R.id.ad_view_banner_bottom);

        AdRequest adRequest = new AdRequest.Builder().build();
        // Start loading the ad in the background.
        mAdViewBottom.loadAd(adRequest);

    }

    private void showInterstitialAd() {
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_id_screen_light));

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