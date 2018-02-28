package com.techsial.apps.simplesmsscheduler;
/*
import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.telephony.SmsManager;

public class SendSMSActivity extends Activity {
	
	private String mNumberToSend;
	private String mSMSMessage;
	private int mHour;
	private int mMinutes;
	private int mYear;
	private int mMonth;
	private int mDay;
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNumberToSend = SetuppageActivity.getNumberToSend();
        mSMSMessage = SetuppageActivity.getSMSMessage();
        mHour = SetuppageActivity.getHour();
        mMinutes = SetuppageActivity.getMinutes();
        mYear = SetuppageActivity.getYear();
        mMonth = SetuppageActivity.getMonth();
        mDay = SetuppageActivity.getDay();
        SmsManager sms = SmsManager.getDefault();
        ArrayList<String> msgStringArray = sms.divideMessage(mSMSMessage);      

	    sms.sendMultipartTextMessage(mNumberToSend, null, msgStringArray, null, null);
	}
}*/

import java.util.ArrayList;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.SmsManager;

public class SendSMSAlarmService extends Service {
    private String mNumberToSend;
    private String mSMSMessage;
    private String mFrequency;
    private int mId;
    private CancelAnAlarmActivity mCancelAnAlarmActivity;
    //SetuppageActivity mSetupPageActivity;
    private PendingIntentsDataSource mDatasource;

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        //mSetupPageActivity = SetuppageActivity.getMAinActivity();
        mCancelAnAlarmActivity = CancelAnAlarmActivity.getCancelAlarmActivity();

    }

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub

        return null;
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        //Toast.makeText(this, "MyAlarmService.onDestroy()", Toast.LENGTH_LONG).show();
        mDatasource.close();
    }

    @Override
    public int onStartCommand(Intent intent, int startId, int arg) {
        // TODO Auto-generated method stub
        super.onStartCommand(intent, startId, arg);

        // Bundle bundle = intent.getExtras();
        try {
            mNumberToSend = intent.getStringExtra("com.techsial.apps.simplesmsscheduler.number");

            mSMSMessage = intent.getStringExtra("com.techsial.apps.simplesmsscheduler.message");
            mFrequency = intent.getStringExtra("com.techsial.apps.simplesmsscheduler.frequency");
            mId = intent.getIntExtra("com.techsial.apps.simplesmsscheduler.smsscheduler.id", 0);
            SmsManager sms = SmsManager.getDefault();

            if (!mSMSMessage.equals(null)) {

                ArrayList<String> msgStringArray = sms.divideMessage(mSMSMessage);

                if (mNumberToSend != null) {
                    try {
                        sms.sendMultipartTextMessage(mNumberToSend, null, msgStringArray, null, null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            if (mFrequency != null && mFrequency.equalsIgnoreCase("Once")) {
                mDatasource = new PendingIntentsDataSource(getApplicationContext());
                mDatasource.open();
                mDatasource.deletePendingIntent(mId);
                //mDatasource.close();*/
                //mCancelAnAlarmActivity.DeleteAnForOnceAlarmEntryFromDatabase(mId);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return START_STICKY_COMPATIBILITY;


        //sms.sendTextMessage(mNumberToSend, null, mSMSMessage, null, null);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // TODO Auto-generated method stub
        // mDatasource.close();
        return super.onUnbind(intent);

    }

}