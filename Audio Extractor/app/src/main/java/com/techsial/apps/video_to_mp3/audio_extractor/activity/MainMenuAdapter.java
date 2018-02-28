package com.techsial.apps.video_to_mp3.audio_extractor.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.techsial.apps.video_to_mp3.audio_extractor.R;

/**
 * Created by DELL on 1/5/2017.
 */

public class MainMenuAdapter extends BaseAdapter {

    public static final String FLOW = "flow";
    private int[] menuItemImage, menuItemImage_hover;
    private String[] menuItemText;
    private LayoutInflater inflter;
    private Context context;

    public MainMenuAdapter(Context context) {
        menuItemImage = new int[8];
        menuItemImage[0] = R.drawable.trim;
        menuItemImage[1] = R.drawable.merge;
        menuItemImage[2] = R.drawable.compress;
        menuItemImage[3] = R.drawable.gif;
        menuItemImage[4] = R.drawable.audio;
        menuItemImage[5] = R.drawable.images;
        menuItemImage[6] = R.drawable.fast_motion;
        menuItemImage[7] = R.drawable.slow_motion;
//        menuItemImage[7] = R.drawable.fade;

        menuItemImage_hover = new int[8];
        menuItemImage_hover[0] = R.drawable.trim_hover;
        menuItemImage_hover[1] = R.drawable.merge_hover;
        menuItemImage_hover[2] = R.drawable.compress_hover;
        menuItemImage_hover[3] = R.drawable.gif_hover;
        menuItemImage_hover[4] = R.drawable.audio_hover;
        menuItemImage_hover[5] = R.drawable.images_hover;
        menuItemImage_hover[6] = R.drawable.fast_motion_hover;
        menuItemImage_hover[7] = R.drawable.slow_motion_hover;
//        menuItemImage_hover[7] = R.drawable.fade_hover;

        menuItemText = new String[8];
        menuItemText[0] = Constants.TRIM_VIDEO;
        menuItemText[1] = Constants.MERGE_VIDEOS;
        menuItemText[2] = Constants.COMPRESS_VIDEO;
        menuItemText[3] = Constants.VIDEO_TO_GIF;
        menuItemText[4] = Constants.EXTRACT_AUDIO;
        menuItemText[5] = Constants.EXTRACT_IMAGES;
        menuItemText[6] = Constants.FAST_MOTION;
        menuItemText[7] = Constants.SLOW_MOTION;
//        menuItemText[7] = Constants.FADE_IN_EFFECT;

        inflter = (LayoutInflater.from(context));
        this.context = context;
    }

    @Override
    public int getCount() {
        return menuItemText.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        view = inflter.inflate(R.layout.card_view, null);

        ImageView iv = (ImageView) view.findViewById(R.id.iv_menu);
        iv.setImageResource(menuItemImage[position]);

        TextView tv = (TextView) view.findViewById(R.id.tv_menu);
        tv.setText(menuItemText[position]);

//        final Animation fadeIn = AnimationUtils.loadAnimation(context, R.anim.fade_in);
//        view.startAnimation(fadeIn);

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    v.setBackgroundColor(ContextCompat.getColor(context, R.color.btn_menu_hover));

                    ImageView iv = (ImageView) v.findViewById(R.id.iv_menu);
                    iv.setImageResource(menuItemImage_hover[position]);

                    TextView tv = (TextView) v.findViewById(R.id.tv_menu);
                    tv.setTextColor(context.getResources().getColor(R.color.btn_menuText_hover));
                    return true;
                }
                if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                    v.setBackgroundColor(ContextCompat.getColor(context, R.color.btn_menu));

                    ImageView iv = (ImageView) v.findViewById(R.id.iv_menu);
                    iv.setImageResource(menuItemImage[position]);

                    TextView tv = (TextView) v.findViewById(R.id.tv_menu);
                    tv.setTextColor(context.getResources().getColor(R.color.btn_menuText));
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    v.setBackgroundColor(ContextCompat.getColor(context, R.color.btn_menu));

                    TextView tv = (TextView) v.findViewById(R.id.tv_menu);
                    tv.setTextColor(context.getResources().getColor(R.color.btn_menuText));

                    ImageView iv = (ImageView) v.findViewById(R.id.iv_menu);
                    iv.setImageResource(menuItemImage[position]);

                    Intent intent = new Intent(context, MainActivity.class);
                    intent.putExtra(FLOW, menuItemText[position]);
//                    switch (position) {
//                        case 0:
//                            intent.putExtra(FLOW, Constants.TRIM_VIDEO);
//                            break;
//                        case 1:
//                            intent.putExtra(FLOW, Constants.MERGE_VIDEOS);
//                            break;
//                        case 2:
//                            intent.putExtra(FLOW, Constants.EXTRACT_AUDIO);
//                            break;
//                        case 3:
//                            intent.putExtra(FLOW, Constants.COMPRESS_VIDEO);
//                            break;
//                        case 4:
//                            intent.putExtra(FLOW, Constants.EXTRACT_IMAGES);
//                            break;
//                        case 5:
//                            intent.putExtra(FLOW, Constants.FAST_MOTION);
//                            break;
//                        case 6:
//                            intent.putExtra(FLOW, Constants.SLOW_MOTION);
//                            break;
////                        case 7:
////                            intent.putExtra(FLOW, Constants.FADE_IN_EFFECT);
////                            break;
//                    }
                    context.startActivity(intent);
                    return false;
                }
                return true;
            }
        });

        return view;
    }

}
