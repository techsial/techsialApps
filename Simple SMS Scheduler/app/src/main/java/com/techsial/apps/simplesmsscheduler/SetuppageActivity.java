package com.techsial.apps.simplesmsscheduler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Set;

import android.Manifest;
import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.ShareCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TimePicker;
import android.widget.Toast;
import android.view.View.OnClickListener;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.NativeExpressAdView;
import com.techsial.apps.simplesmsscheduler.utils.PopupDialogs;
import com.techsial.apps.simplesmsscheduler.utils.PreferenceManager;

import static android.support.v4.app.ActivityCompat.requestPermissions;
import static android.support.v4.content.PermissionChecker.checkSelfPermission;

public class SetuppageActivity extends Activity implements View.OnClickListener {

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 20;
    private String mNumber;
    EditText mEditTextNumber;
    EditText mEditTextMessage;
    private String mContactdisplayName;
    private long mInterval;
    private String mMessage;
    private String mFrequency;
    Button mButtonContact;
    Button mDatePickUp;
    Button mTimePickUp;
    Button mConfirm;
    RadioButton mOneTime, mFifteenMinuets, mHalfHour, mHourR, mHalfDay, mDaily, mWeekly, mMonthly, mYearly;
    private int mHour;
    private int mMinutes;
    private int mSeconds;

    private int mYear;
    private int mMonth;
    private int mDay;

    private int mCurrentYear;
    private int mCurrentMonth;
    private int mCurrentDay;
    private int mCurrentHour;
    private int mCurrentMinute;

    DatePickerDialog mDatePickerDialog;
    TimePickerDialog mTimePickerDialog;
    Calendar c;
    private static SetuppageActivity mMainActivity;
    private int mId;
    private boolean isOneTime = false;
    private PendingIntentsDataSource mDatasource;
    private String SELECT_DATE = "00/00/0000";
    private String SELECT_TIME = "00:00";

    private boolean doubleBackToExitPressedOnce = false;

    private Dialog rateAppDialog;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setuppage);
        mMainActivity = this;

        ActionBar actionBar = getActionBar();
        actionBar.setIcon(R.drawable.sms_scheduler);
        actionBar.setTitle("  SMS Scheduler");

        MobileAds.initialize(this, getString(R.string.app_id_admob));

        showInterstitialAd();

        try {
            if (checkInternet(SetuppageActivity.this)) {
                NativeExpressAdView adView = (NativeExpressAdView) findViewById(R.id.adViewNativeMainScreen);
                adView.setVisibility(View.VISIBLE);
                AdRequest request = new AdRequest.Builder().build();
                adView.loadAd(request);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mButtonContact = (Button) findViewById(R.id.buttonContact);
        mEditTextNumber = (EditText) findViewById(R.id.editTextNumber);
        mDatePickUp = (Button) findViewById(R.id.buttonDatePickup);
        mTimePickUp = (Button) findViewById(R.id.buttonTimePickup);
        mConfirm = (Button) findViewById(R.id.buttonConfirm);
        mEditTextMessage = (EditText) findViewById(R.id.editTextMessage);
        mOneTime = (RadioButton) findViewById(R.id.oneTime);
        mOneTime.setOnClickListener(this);
        mOneTime.performClick();
        mFifteenMinuets = (RadioButton) findViewById(R.id.fifteenMinutes);
        mFifteenMinuets.setOnClickListener(this);
        mHalfHour = (RadioButton) findViewById(R.id.halfHour);
        mHalfHour.setOnClickListener(this);
        mHourR = (RadioButton) findViewById(R.id.hour);
        mHourR.setOnClickListener(this);
        mHalfDay = (RadioButton) findViewById(R.id.halfDay);
        mHalfDay.setOnClickListener(this);
        mDaily = (RadioButton) findViewById(R.id.daily);
        mDaily.setOnClickListener(this);
        mWeekly = (RadioButton) findViewById(R.id.weekly);
        mWeekly.setOnClickListener(this);
        mMonthly = (RadioButton) findViewById(R.id.monthly);
        mMonthly.setOnClickListener(this);
        mYearly = (RadioButton) findViewById(R.id.Yearly);
        mEditTextNumber.setText("");
        mEditTextMessage.setText("");

        //create/open the database
        mDatasource = new PendingIntentsDataSource(this);
        mDatasource.open();

        c = Calendar.getInstance();
        mCurrentYear = c.get(Calendar.YEAR);
        mCurrentMonth = c.get(Calendar.MONTH);
        mCurrentDay = c.get(Calendar.DAY_OF_MONTH);
        mCurrentHour = c.get(Calendar.HOUR_OF_DAY);
        mCurrentMinute = c.get(Calendar.MINUTE);

        mTimePickUp.setText(SELECT_TIME);
        mDatePickUp.setText(SELECT_DATE);
        mButtonContact.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                startActivityForResult(intent, 1);
            }
        });
        mDatePickerDialog = new DatePickerDialog(this, mDateSetListener, mCurrentYear, mCurrentMonth,
                mCurrentDay);
        mDatePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
        mTimePickerDialog = new TimePickerDialog(this, mTimeSetListener, mCurrentHour, mCurrentMinute, true);
        // add a click listener to the button
        mDatePickUp.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                mDatePickerDialog.show();
            }
        });

        mTimePickUp.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                mTimePickerDialog.show();
            }
        });

        mConfirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                checkPermissions();
                if (isEmptyField(mEditTextNumber) || isEmptyField(mEditTextMessage)) {
                    return;
                }

                if (mTimePickUp.getText().equals(SELECT_TIME)) {
                    Toast.makeText(SetuppageActivity.this, "Please select a valid Time.", Toast.LENGTH_LONG).show();
                    return;
                }

                if (mDatePickUp.getText().equals(SELECT_DATE)) {
                    Toast.makeText(SetuppageActivity.this, "Please select a valid Date.", Toast.LENGTH_LONG).show();
                    return;
                }
                mMessage = mEditTextMessage.getText().toString();
                mNumber = mEditTextNumber.getText().toString();

                c.set(Calendar.HOUR_OF_DAY, mHour);
                c.set(Calendar.MINUTE, mMinutes);
                c.set(Calendar.SECOND, 0);
                c.set(mYear, mMonth, mDay);

                Intent i = new Intent(getApplicationContext(), SendSMSAlarmService.class);
                i.putExtra("com.techsial.apps.simplesmsscheduler.number", mNumber);
                i.putExtra("com.techsial.apps.simplesmsscheduler.message", mMessage);
                i.putExtra("com.techsial.apps.simplesmsscheduler.frequency", mFrequency);

                mId = (int) System.currentTimeMillis();
                i.putExtra("com.techsial.apps.simplesmsscheduler.id", mId);
                AlarmManager am = (AlarmManager) getApplicationContext().getSystemService(getApplicationContext().ALARM_SERVICE);
                PendingIntent pi = PendingIntent.getService(getApplicationContext(), mId, i, PendingIntent.FLAG_UPDATE_CURRENT);

                if (isOneTime == false) {
                    am.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), mInterval, pi);
                    addToDatabase(true);
                }
                if (isOneTime == true) {
                    am.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi);
                    addToDatabase(false);
                }


                Toast.makeText(getApplicationContext(), "Your SMS scheduled successfully.", Toast.LENGTH_LONG).show();
                showInterstitialAd();

                resetFields();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.setuppage, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menuCancelAnAlarm:
                Intent i = new Intent();
                i.setClassName("com.techsial.apps.simplesmsscheduler", "com.techsial.apps.simplesmsscheduler.CancelAnAlarmActivity");
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
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
            case R.id.menu_send_feedback:
                sendFeedback();
                break;
            case R.id.menu_help:
                Intent aboutIntent = new Intent(this, HelpActivity.class);
                startActivity(aboutIntent);
                showInterstitialAd();
                break;
//            case R.id.menuViewMore:
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.setData(Uri.parse("https://play.google.com/store/search?q=techsial&c=apps"));
//                startActivity(intent);
//                break;
        }
        return true;
    }

    @Override
    public void onPause() {
        super.onPause();
        mDatasource.close();
    }

    @Override
    public void onResume() {
        super.onResume();
        mDatasource.open();
        c = Calendar.getInstance();
        mCurrentYear = c.get(Calendar.YEAR);
        mCurrentMonth = c.get(Calendar.MONTH);
        mCurrentDay = c.get(Calendar.DAY_OF_MONTH);
        mCurrentHour = c.get(Calendar.HOUR_OF_DAY);
        mCurrentMinute = c.get(Calendar.MINUTE);
    }

    @Override
    public void onStart() {
        super.onStart();
        mDatasource.open();
        c = Calendar.getInstance();
        mCurrentYear = c.get(Calendar.YEAR);
        mCurrentMonth = c.get(Calendar.MONTH);
        mCurrentDay = c.get(Calendar.DAY_OF_MONTH);
        mCurrentHour = c.get(Calendar.HOUR_OF_DAY);
        mCurrentMinute = c.get(Calendar.MINUTE);

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
        if (!PreferenceManager.readBoolean(SetuppageActivity.this, PreferenceManager.IS_RATED, false)) {
            rateAppDialog = PopupDialogs.createRateAppDialog(SetuppageActivity.this, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rateAppDialog.dismiss();
                    mDatasource.close();
                    finish();
                }
            }, new PopupDialogs.OnCustomClickListener() {
                @Override
                public void onClick(View v, float rating) {
                    if (rating >= 4.8) {
                        openPlayStorePage();
                    } else {
                        Toast.makeText(SetuppageActivity.this, "Thank you for rating SMS Scheduler!", Toast.LENGTH_LONG).show();
                    }
                    PreferenceManager.writeBoolean(SetuppageActivity.this, PreferenceManager.IS_RATED, true);
                    mDatasource.close();
                    rateAppDialog.dismiss();
                }
            });
        } else {
            if (doubleBackToExitPressedOnce) {
                showInterstitialAd();

                mDatasource.close();
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
//
//            //exit the app
//            mDatasource.close();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            Uri uri = data.getData();

            if (uri != null) {
                Cursor c = null;
                try {
                    c = getContentResolver().query(uri, new String[]{
                                    ContactsContract.CommonDataKinds.Phone.NUMBER,
                                    ContactsContract.CommonDataKinds.Phone.TYPE,
                                    ContactsContract.CommonDataKinds.Identity.DISPLAY_NAME},
                            null, null, null);

                    if (c != null && c.moveToFirst()) {
                        int type = c.getInt(1);
                        mEditTextNumber.setText(c.getString(0));
                        mContactdisplayName = c.getString(2);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (c != null) {
                        c.close();
                    }
                }
            }
        }
    }

    // the callback received when the user "sets" the date in the dialog
    private DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {

                public void onDateSet(DatePicker view, int year,
                                      int monthOfYear, int dayOfMonth) {
                    mYear = year;
                    mMonth = monthOfYear;
                    mDay = dayOfMonth;

                    mDatePickUp.setText(getFormattedDate(mDay, mMonth, mYear));

                }
            };

    // the callback received when the user "sets" the time in the dialog
    private TimePickerDialog.OnTimeSetListener mTimeSetListener =
            new TimePickerDialog.OnTimeSetListener() {
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    mHour = hourOfDay;
                    mMinutes = minute;
                    mTimePickUp.setText(getFormattedTime(mHour, mMinutes));
                }
            };

    public static SetuppageActivity getMAinActivity() {
        return mMainActivity;
    }

    public String getNumberToSend() {
        return mNumber;
    }

    public String getSMSMessage() {
        return mMessage;
    }

    public int getDay() {
        return mDay;
    }

    public int getMonth() {
        return mMonth;
    }

    public int getYear() {
        return mYear;
    }

    public int getHour() {
        return mHour;
    }

    public int getMinutes() {
        return mMinutes;
    }

    public int getSeconds() {
        return mSeconds;
    }

    public PendingIntentsDataSource getDataSource() {
        //create/open the database
        return mDatasource;
    }

    @Override
    public void onClick(View v) {

        // TODO Auto-generated method stub


        if (v.equals(mOneTime)) {
            isOneTime = true;
            mFrequency = "One Time";
        }
        if (v.equals(mFifteenMinuets)) {
            mInterval = AlarmManager.INTERVAL_FIFTEEN_MINUTES;
            mFrequency = "15 Minutes";
            isOneTime = false;
        }
        if (v.equals(mHalfHour)) {
            mInterval = AlarmManager.INTERVAL_HALF_HOUR;
            mFrequency = "30 Minutess";
            isOneTime = false;
        }
        if (v.equals(mHourR)) {
            mInterval = AlarmManager.INTERVAL_HOUR;
            mFrequency = "Hourly";
            isOneTime = false;
        }
        if (v.equals(mHalfDay)) {
            mInterval = AlarmManager.INTERVAL_HALF_DAY;
            mFrequency = "Half a Day";
            isOneTime = false;
        }
        if (v.equals(mDaily)) {
            mInterval = AlarmManager.INTERVAL_DAY;
            mFrequency = "Daily";
            isOneTime = false;
        }
        if (v.equals(mWeekly)) {
            mInterval = 7 * 24 * 60 * 60 * 1000;
            mFrequency = "Weekly";
            isOneTime = false;

        }
        if (v.equals(mMonthly)) {
            isOneTime = false;
            mFrequency = "Monthly";
            int month = c.get(Calendar.MONTH);
            if ((month == Calendar.JANUARY) || (month == Calendar.MARCH) || (month == Calendar.MAY) || (month == Calendar.JULY) || (month == Calendar.AUGUST) || (month == Calendar.OCTOBER) || (month == Calendar.DECEMBER)) {
                mInterval = 31 * 24 * 60 * 60 * 1000;
            }
            if ((month == Calendar.APRIL) || (month == Calendar.JUNE) || (month == Calendar.SEPTEMBER) || (month == Calendar.NOVEMBER)) {
                mInterval = 30 * 24 * 60 * 60 * 1000;
            }
            if (month == Calendar.FEBRUARY) {
                int year = c.get(Calendar.YEAR);
                if ((year % 4) == 0) {
                    mInterval = 29 * 24 * 60 * 60 * 1000;
                }
                if ((year % 4) != 0) {
                    mInterval = 28 * 24 * 60 * 60 * 1000;
                }

            }

        }
        if (v.equals(mYearly)) {
            mInterval = 365 * 24 * 60 * 60 * 1000;
            mFrequency = "Yearly";
            isOneTime = false;
        }
    }

    private void addToDatabase(boolean isRepeated) {
        if (isRepeated == true) {

            SMSSchedulerPendingIntent newDatabeseEntry = mDatasource.createPendingIntents(mId, mHour, mMinutes, mSeconds, mYear, mMonth, mDay, mFrequency, mNumber, mContactdisplayName, mMessage);
        }
        if (isRepeated == false) {
            SMSSchedulerPendingIntent newDatabeseEntry = mDatasource.createPendingIntents(mId, mHour, mMinutes, mSeconds, mYear, mMonth, mDay, mFrequency, mNumber, mContactdisplayName, mMessage);
        }
    }

    public boolean isEmptyField(EditText etInput) {
        if (TextUtils.isEmpty(etInput.getText())) {
            etInput.setError("Input must not be empty");
            return true;
        } else {
            return false;
        }
    }

    private String getFormattedDate(int day, int month, int year) {

        String strFormattedDate = (day < 10 ? "0" + day : day) + "/" + ((month + 1) < 10 ? "0" + (month + 1) : (month + 1)) + "/" + (year < 10 ? "0" + year : year);

        return strFormattedDate;
    }

    private String getFormattedTime(int hrs, int min) {
        String strFormattedTime = (hrs < 10 ? "0" + hrs : hrs) + ":" + (min < 10 ? "0" + min : min);

        return strFormattedTime;
    }

    private void checkPermissions() {
        if (checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST_SEND_SMS);
            return;
        }


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

    private void resetFields() {
        mEditTextNumber.setText("");
        mEditTextMessage.setText("");
        mOneTime.performClick();
        mDatePickUp.setText(SELECT_DATE);
        mTimePickUp.setText(SELECT_TIME);

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