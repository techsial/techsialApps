package com.techsial.apps.simplesmsscheduler;

import java.util.ArrayList;
import java.util.List;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.NativeExpressAdView;

public class CancelAnAlarmActivity extends ListActivity implements OnItemClickListener {
    private PendingIntentsDataSource mDatasource;
    private ListView mListView;
    private List<SMSSchedulerPendingIntent> mPendingIntentList;
    private String mNumberToSend;
    private String mReceiverName;
    private String mMessage;
    private String mFrequency;
    private static CancelAnAlarmActivity mCancelAlarmActivity;
    private int mIdOfAnPendingIntent;
    //String newLine;
    String newL = System.getProperty("line.separator");
    private InterstitialAd interstitialAdCancelAlarm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.main);
            mCancelAlarmActivity = this;
            mListView = getListView();

            //mDetailsButton = (Button)(findViewById(R.id.buttonDetails));
            mDatasource = new PendingIntentsDataSource(this);
            mDatasource.open();


            List<SMSSchedulerPendingIntent> values = mDatasource.getAllPendingIntents();

            final PendingIntentsArrayAdapter pendingIntentAdapter = new PendingIntentsArrayAdapter(this, R.layout.cancelanalarm, values);
            //setListAdapter(new PendingIntentsArrayAdapter(this, R.layout.cancelanalarm, values));
            setListAdapter(pendingIntentAdapter);
            //mDetailsButton = (Button)findViewById(R.id.buttonDetails);
            mPendingIntentList = pendingIntentAdapter.getPendingIntentList();

            mListView.setOnItemClickListener(this);

            MobileAds.initialize(this, getString(R.string.app_id_admob));

//        showInterstitialAd();

            if (checkInternet(CancelAnAlarmActivity.this)) {
                NativeExpressAdView adView = (NativeExpressAdView) findViewById(R.id.adViewNativeCancelScreen);
                adView.setVisibility(View.VISIBLE);
                AdRequest request = new AdRequest.Builder().build();
                adView.loadAd(request);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mDatasource.close();
        /*mEditTextMessage.setText("");
        mEditTextNumber.setText("");*/
    }

    @Override
    public void onResume() {
        super.onResume();
        mDatasource.open();
    }

    @Override
    public void onStart() {
        super.onStart();
        /*mEditTextMessage.setText("");
        mEditTextNumber.setText("");	*/
        mDatasource.open();

    }

    @Override
    public void onStop() {
        super.onStop();
        mDatasource.close();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDatasource.close();
    }

    @Override
    public void onBackPressed() {
        mDatasource.close();
//        Intent i = new Intent();
//        i.setClassName("com.techsial.apps.simplesmsscheduler", "com.techsial.apps.simplesmsscheduler.SetuppageActivity");
//        startActivity(i);
        finish();
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        // TODO Auto-generated method stub
        SMSSchedulerPendingIntent selectedsmsSchedulerPendingIntent = (SMSSchedulerPendingIntent) (getListAdapter().getItem(arg2));
        mIdOfAnPendingIntent = (int) selectedsmsSchedulerPendingIntent.getId();
        mNumberToSend = selectedsmsSchedulerPendingIntent.getNumberToSend();
        mReceiverName = selectedsmsSchedulerPendingIntent.getReceiverName();
        mMessage = selectedsmsSchedulerPendingIntent.getMessage();
        mFrequency = selectedsmsSchedulerPendingIntent.getFrequency();
        String detailsTest = "Do you want to cancel this scheduled SMS?";
        ShowCancelAlarmDetails(detailsTest);

    }

    public static CancelAnAlarmActivity getCancelAlarmActivity() {
        return mCancelAlarmActivity;
    }

    @SuppressWarnings("deprecation")
    public void ShowCancelAlarmDetails(String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Cancel scheduled SMS");
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // TODO Add your code for the button here.
                CancelAnAlarm(mIdOfAnPendingIntent);
            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // TODO Add your code for the button here.
            }
        });
        alertDialog.show();
    }

    public void CancelAnAlarm(int id) {
        try {
            AlarmManager am = (AlarmManager) getApplicationContext().getSystemService(getApplicationContext().ALARM_SERVICE);
            Intent i = new Intent(getApplicationContext(), SendSMSAlarmService.class);
            PendingIntent pi = PendingIntent.getService(getApplicationContext(), id, i, PendingIntent.FLAG_UPDATE_CURRENT);
            am.cancel(pi);
            mDatasource.deletePendingIntent(id);
            Intent refresh = new Intent();
            refresh.setClassName("com.techsial.apps.simplesmsscheduler", "com.techsial.apps.simplesmsscheduler.CancelAnAlarmActivity");
            startActivity(refresh);
        } catch (Exception e) {
            e.printStackTrace();
        }
        finish();

    }

    public void DeleteAnForOnceAlarmEntryFromDatabase(int id) {
        mDatasource.deletePendingIntent(id);
    }

    private void showInterstitialAd() {
        interstitialAdCancelAlarm = new InterstitialAd(this);
        interstitialAdCancelAlarm.setAdUnitId(getString(R.string.interstitial_id_cancel_alarm_screen));

        interstitialAdCancelAlarm.loadAd(new AdRequest.Builder()
//                .addTestDevice("D823DFBE13477D4EF2A8C8EAF73F2E87")
                .build());

        interstitialAdCancelAlarm.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
//                requestNewInterstitial();
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                interstitialAdCancelAlarm.show();
            }
        });


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
}