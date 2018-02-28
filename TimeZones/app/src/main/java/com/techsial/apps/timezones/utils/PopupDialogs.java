package com.techsial.apps.timezones.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.WindowManager.BadTokenException;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;

import com.techsial.apps.timezones.R;

public class PopupDialogs {
    private static Dialog dialog;
    private static Dialog parentDialog;

    public static Dialog createRateAppDialog(final Context context,
                                             View.OnClickListener listenerLater,
                                             final OnCustomClickListener listenerRateApp) {

        dialog = createDialog(context, false, R.layout.dialog_rate_app);


        final RatingBar ratingBar = (RatingBar) dialog.findViewById(R.id.ratingBar);
        Button btnRateApp = (Button) dialog.findViewById(R.id.btnRateNow);
        Button btnLater = (Button) dialog.findViewById(R.id.btnLater);

        btnLater.setOnClickListener(listenerLater);
        btnRateApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float rating = ratingBar.getRating();
                listenerRateApp.onClick(v, rating);
            }
        });
        return dialog;
    }

    public interface OnCustomClickListener {
        void onClick(View v, float rating);
    }

    private static Dialog createDialog(final Context context,
                                       Boolean cancel, int dialogId) {
        dialog = new Dialog(context,
                android.R.style.Theme_Translucent_NoTitleBar);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(R.color.colorPrimary));
        dialog.setContentView(dialogId);
        dialog.setCancelable(cancel);

        ImageView headerImage = (ImageView) dialog
                .findViewById(R.id.imageViewAlert);

//        switch (status) {
//            case INFO:
////                headerImage.setImageResource(R.drawable.info_popup_icon);
//                break;
//
//            case SUCCESS:
////                headerImage.setImageResource(R.drawable.success_popup_icon);
//                break;
//
//            case ERROR:
////                headerImage.setImageResource(R.drawable.failure_popup_icon);
//                break;
//
//            case ALERT:
////                headerImage.setImageResource(R.drawable.alert_popup_icon);
//                break;
//
//            case LOCK:
////                headerImage.setImageResource(R.drawable.mpin_popup_icon);
//                break;
//
//            case FINGERPRINT:
////                headerImage.setImageResource(R.drawable.fingerprint_popup_icon);
//                break;
//
//            default:
//                break;
//        }

        try {
            dialog.show();
        } catch (BadTokenException btexp) {
            btexp.printStackTrace();
        }

        return dialog;
    }

    public enum Status {
        INFO, SUCCESS, ERROR, ALERT, LOCATOR, DISTANCE, LOCK, FINGERPRINT
    }

    public static void dismissDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}