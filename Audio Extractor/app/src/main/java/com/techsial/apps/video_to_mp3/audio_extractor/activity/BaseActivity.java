package com.techsial.apps.video_to_mp3.audio_extractor.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.techsial.apps.video_to_mp3.audio_extractor.R;
import com.techsial.apps.video_to_mp3.audio_extractor.managers.FacebookManager;

/**
 * Created by Zaigham on 5/1/2017.
 */

public class BaseActivity extends AppCompatActivity {

    private FacebookManager facebookManger;
    protected InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MobileAds.initialize(this, getString(R.string.app_id_admob));
        this.setFacebookManger(new FacebookManager(this));

//        showInterstitialAd();

    }

    protected void showInterstitialAd() {
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.main_screen_interstatial_id));

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

    public FacebookManager getFacebookManger() {
        return facebookManger;
    }
    public void setFacebookManger(FacebookManager facebookManger) {
        this.facebookManger = facebookManger;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        facebookManger.onActivityResult(requestCode, resultCode, data);

    }
}
