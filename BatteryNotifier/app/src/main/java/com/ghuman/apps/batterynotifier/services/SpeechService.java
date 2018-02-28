package com.ghuman.apps.batterynotifier.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

/**
 * Created by Zaigham on 1/1/2017.
 */

public class SpeechService extends Service implements TextToSpeech.OnInitListener {
    public SpeechService() {
    }

    private TextToSpeech mTts;
    private String strSpeech;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        try {
            strSpeech = intent.getExtras().getString("SPEECH");
            // TODO Auto-generated method stub
            mTts = new TextToSpeech(this, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onStart(intent, startId);
    }

    @Override
    public void onInit(int status) {
        // TODO Auto-generated method stub
        try {
            if (status == TextToSpeech.SUCCESS) {
                if (mTts.isLanguageAvailable(Locale.UK) >= 0)

                    mTts.setLanguage(Locale.ENGLISH);
                mTts.speak(strSpeech, TextToSpeech.QUEUE_FLUSH, null);

            } else if (status == TextToSpeech.ERROR) {
//            Toast.makeText(SpeechService.this,
//                    "Unable to initialize Text-To-Speech engine",
//                    Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
