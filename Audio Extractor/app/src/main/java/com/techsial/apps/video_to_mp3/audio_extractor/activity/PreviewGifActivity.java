package com.techsial.apps.video_to_mp3.audio_extractor.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.NativeExpressAdView;
import com.techsial.apps.video_to_mp3.audio_extractor.R;

import java.io.File;
import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by Bhuvnesh on 09-03-2017.
 */

public class PreviewGifActivity extends BaseActivity {

    private static final String FILEPATH = "filepath";
    private GifImageView ivGif;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_gif);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        TextView tvInstruction = (TextView) findViewById(R.id.tvInstruction);
        ivGif = (GifImageView) findViewById(R.id.ivGif);

        String filePath = getIntent().getStringExtra(FILEPATH);
        tvInstruction.setText("GIF is stored at path: " + filePath);
        try {
            GifDrawable gifFromPath = new GifDrawable(filePath);
            ivGif.setImageDrawable(gifFromPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        showInterstitialAd();
        try {
            NativeExpressAdView adView = (NativeExpressAdView) findViewById(R.id.adViewGifPreviewScreen);
            AdRequest request = new AdRequest.Builder().build();
            adView.loadAd(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

}
