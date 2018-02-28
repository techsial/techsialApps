package com.techsial.apps.simplesmsscheduler;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class PendingIntentsArrayAdapter extends ArrayAdapter<SMSSchedulerPendingIntent> {
    private List<SMSSchedulerPendingIntent> mPendingIntentList;
    private Context mContext;
    String newL = System.getProperty("line.separator");
    private CancelAnAlarmActivity mCancelAnAlarmActivity;

    public PendingIntentsArrayAdapter(Context context, int resource, List<SMSSchedulerPendingIntent> objects) {
        super(context, R.layout.cancelanalarm, objects);
        // TODO Auto-generated constructor stub
        mContext = context;
        mPendingIntentList = objects;
        mCancelAnAlarmActivity = CancelAnAlarmActivity.getCancelAlarmActivity();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // First let's verify the convertView is not null
        if (convertView == null) {
            // This a new view we inflate the new layout
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.cancelanalarm, parent, false);
        }
        TextView tvTime = (TextView) convertView.findViewById(R.id.tvTime);
        TextView tvDate = (TextView) convertView.findViewById(R.id.tvDate);
        TextView tvReceiver = (TextView) convertView.findViewById(R.id.tvReceiver);
        TextView tvFrequency = (TextView) convertView.findViewById(R.id.tvFrequency);
        TextView tvMessage = (TextView) convertView.findViewById(R.id.tvMessage);

        SMSSchedulerPendingIntent smsSchedulerPendingIntent = mPendingIntentList.get(position);
        String receiverNumber = smsSchedulerPendingIntent.getNumberToSend();
        String receiverName = smsSchedulerPendingIntent.getReceiverName();
        String message = smsSchedulerPendingIntent.getMessage();
        String frequency = smsSchedulerPendingIntent.getFrequency();

        if (receiverName != null) {
            tvReceiver.setText(receiverName + "/" + receiverNumber);
        } else {
            tvReceiver.setText("Anonymous " + receiverNumber);
        }
        if (frequency != null) {
            tvFrequency.setText(frequency);
        } else {
            tvFrequency.setText("N/A");
        }
        tvMessage.setText("Message: " + message);

        tvTime.setText(getFormattedTime(smsSchedulerPendingIntent.getHour(), smsSchedulerPendingIntent.getMinutes()));
        tvDate.setText(getFormattedDate(smsSchedulerPendingIntent.getDay(), smsSchedulerPendingIntent.getMonth(), smsSchedulerPendingIntent.getYear()));

        return convertView;
    }

    public List<SMSSchedulerPendingIntent> getPendingIntentList() {
        return mPendingIntentList;
    }

    private String getFormattedDate(int day, int month, int year) {

        String strFormattedDate = (day < 10 ? "0" + day : day) + "/" + ((month + 1) < 10 ? "0" + (month + 1) : (month + 1)) + "/" + (year < 10 ? "0" + year : year);

        return strFormattedDate;
    }

    private String getFormattedTime(int hrs, int min) {
        String strFormattedTime = (hrs < 10 ? "0" + hrs : hrs) + ":" + (min < 10 ? "0" + min : min);

        return strFormattedTime;
    }

}