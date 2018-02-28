package com.techsial.apps.video_to_mp3.audio_extractor.activity;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.florescu.android.rangeseekbar.RangeSeekBar;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.NativeExpressAdView;
import com.techsial.apps.video_to_mp3.audio_extractor.BuildConfig;
import com.techsial.apps.video_to_mp3.audio_extractor.R;


public class MainActivity extends BaseActivity {


    private final String UPLOAD_FIRST_VIDEO = "Upload First Video";
    private final String UPLOAD_SECOND_VIDEO = "Upload Second Video";
    private static final int REQUEST_TAKE_GALLERY_VIDEO = 100;
    private VideoView videoView;
    private RangeSeekBar rangeSeekBar;
    private DiscreteSeekBar discreteSeekBar;
    private Runnable r;
    private FFmpeg ffmpeg;
    private ProgressDialog progressDialog;
    private Uri selectedImageUri, firstVideoUri, secondVideoUri;
    private static final String TAG = "AppLog";
    private static final String POSITION = "position";
    private static final String FILEPATH = "filepath";
    private int choice = 0;
    private int stopPosition;
    private ScrollView mainlayout;
    private TextView tvLeft, tvRight;
    private Button btnUploadVideo, btnAction;
    private String filePath;
    private int duration, discreteSeekbarValue;
    private Context mContext;
    private String[] lastReverseCommand;
    private String strFlow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_main);
        } catch (Exception e) {
            e.printStackTrace();
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

//        getHashKey();

//        getFacebookManger().postStatusDialog(
//                "",
//                "",
//                "",
//                "", new FacebookCallback<Sharer.Result>() {
//
//                    @Override
//                    public void onSuccess(Sharer.Result result) {
//                        Toast.makeText(MainActivity.this, "Posted successfully!", Toast.LENGTH_LONG).show();
//                    }
//
//                    @Override
//                    public void onCancel() {
//                        Toast.makeText(MainActivity.this, "Cancel", Toast.LENGTH_LONG).show();
//                    }
//
//                    @Override
//                    public void onError(FacebookException error) {
//                        Toast.makeText(MainActivity.this, "Unable to process request. Please try later!", Toast.LENGTH_LONG).show();
//
//                    }
//
//                });

        strFlow = getIntent().getExtras().getString(MainMenuAdapter.FLOW);

        mContext = this;
//        TextView cutVideo = (TextView) findViewById(R.id.cropVideo);
//        TextView compressVideo = (TextView) findViewById(R.id.compressVideo);
//        TextView extractImages = (TextView) findViewById(R.id.extractImages);
        TextView tvInstructions = (TextView) findViewById(R.id.tvInstructions);
//        TextView fadeEffect = (TextView) findViewById(R.id.fadeEffect);
//        TextView increaseSpeed = (TextView) findViewById(R.id.increaseSpeed);
//        TextView decreaseSpeed = (TextView) findViewById(R.id.decreaseSpeed);
//        final TextView reverseVideo = (TextView) findViewById(R.id.reverseVideo);

        btnUploadVideo = (Button) findViewById(R.id.btnUploadVideo);
        btnAction = (Button) findViewById(R.id.btnAction);

        tvLeft = (TextView) findViewById(R.id.tvLeft);
        tvRight = (TextView) findViewById(R.id.tvRight);

//        final TextView extractAudio = (TextView) findViewById(R.id.extractAudio);
//        if (Build.VERSION.SDK_INT == 16)
//            extractAudio.setVisibility(View.GONE);
//        else
//            extractAudio.setVisibility(View.VISIBLE);
        videoView = (VideoView) findViewById(R.id.videoView);
        rangeSeekBar = (RangeSeekBar) findViewById(R.id.rangeSeekBar);
        discreteSeekBar = (DiscreteSeekBar) findViewById(R.id.discreteSeekBar);
        mainlayout = (ScrollView) findViewById(R.id.mainlayout);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Processing");
        progressDialog.setCancelable(false);
        rangeSeekBar.setEnabled(false);
        loadFFMpegBinary();

        try {
            NativeExpressAdView adView = (NativeExpressAdView) findViewById(R.id.adViewMainScreen);
            AdRequest request = new AdRequest.Builder().build();
            adView.loadAd(request);
        } catch (Exception e) {
            e.printStackTrace();
        }

        btnAction.setText(strFlow);
        switch (strFlow) {
            case Constants.TRIM_VIDEO:
                tvInstructions.setText(getString(R.string.timeRangeTrimVideo));
                break;
            case Constants.VIDEO_TO_GIF:
                tvInstructions.setText(getString(R.string.timeRangeVideoToGif));
                discreteSeekBar.setVisibility(View.VISIBLE);
                break;
            case Constants.EXTRACT_IMAGES:
                tvInstructions.setText(getString(R.string.timeRangeExtractImages));
                break;
            case Constants.MERGE_VIDEOS:
                btnUploadVideo.setText(UPLOAD_FIRST_VIDEO);
                tvInstructions.setText(getString(R.string.instructionsMergeVideos));

                rangeSeekBar.setVisibility(View.GONE);
                tvLeft.setVisibility(View.GONE);
                tvRight.setVisibility(View.GONE);
                break;
            default:
                rangeSeekBar.setVisibility(View.GONE);
                tvLeft.setVisibility(View.GONE);
//                tvRight.setVisibility(View.GONE);
                tvInstructions.setVisibility(View.GONE);
                break;
        }


        btnUploadVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23)
                    getPermission();
                else
                    uploadVideo();

            }
        });

        btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                discreteSeekbarValue = discreteSeekBar.getProgress();
                switch (strFlow) {
                    case Constants.COMPRESS_VIDEO:
                        choice = 1;

                        if (selectedImageUri != null) {
                            executeCompressCommand();
                        } else {
                            Snackbar.make(mainlayout, "Please upload a video", 4000).show();
                        }
                        break;
                    case Constants.TRIM_VIDEO:
                        choice = 2;

                        if (selectedImageUri != null) {
                            executeCutVideoCommand(rangeSeekBar.getSelectedMinValue().intValue() * 1000, rangeSeekBar.getSelectedMaxValue().intValue() * 1000);
                        } else {
                            Snackbar.make(mainlayout, "Please upload a video", 4000).show();
                        }
                        break;
                    case Constants.EXTRACT_IMAGES:
                        choice = 3;

                        if (selectedImageUri != null) {
                            extractImagesVideo(rangeSeekBar.getSelectedMinValue().intValue() * 1000, rangeSeekBar.getSelectedMaxValue().intValue() * 1000);
                        } else
                            Snackbar.make(mainlayout, "Please upload a video", 4000).show();

                        break;
                    case Constants.EXTRACT_AUDIO:
                        choice = 4;

                        if (selectedImageUri != null) {
                            if (Build.VERSION.SDK_INT >= 23)
                                getAudioPermission();
                            else
                                extractAudioVideo();
                        } else {
                            Snackbar.make(mainlayout, "Please upload a video", 4000).show();
                        }
                        break;
                    case Constants.FADE_IN_EFFECT:
                        choice = 5;

                        if (selectedImageUri != null) {
                            executeFadeInFadeOutCommand();
                        } else
                            Snackbar.make(mainlayout, "Please upload a video", 4000).show();

                        break;
                    case Constants.FAST_MOTION:
                        choice = 6;

                        if (selectedImageUri != null) {
                            executeFastMotionVideoCommand();
                        } else
                            Snackbar.make(mainlayout, "Please upload a video", 4000).show();

                        break;
                    case Constants.SLOW_MOTION:
                        choice = 7;

                        if (selectedImageUri != null) {
                            executeSlowMotionVideoCommand();
                        } else
                            Snackbar.make(mainlayout, "Please upload a video", 4000).show();

                        break;
                    case Constants.MERGE_VIDEOS:
                        choice = 12;

                        if (firstVideoUri != null && secondVideoUri != null) {
                            mergeVideoCommand();
                        } else
                            Snackbar.make(mainlayout, "Please upload both videos", 4000).show();

                        break;
                    case Constants.VIDEO_TO_GIF:
                        choice = 13;

                        if (selectedImageUri != null) {
                            executeVideoToGifCommand(rangeSeekBar.getSelectedMinValue().intValue() * 1000, rangeSeekBar.getSelectedMaxValue().intValue() * 1000);
                        } else {
                            Snackbar.make(mainlayout, "Please upload a video", 4000).show();
                        }
                        break;
                }
            }
        });

//        compressVideo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                choice = 1;
//
//                if (selectedImageUri != null) {
//                    executeCompressCommand();
//                } else
//                    Snackbar.make(mainlayout, "Please upload a video", 4000).show();
//
//            }
//        });
//        cutVideo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                choice = 2;
//
//                if (selectedImageUri != null) {
//                    executeCutVideoCommand(rangeSeekBar.getSelectedMinValue().intValue() * 1000, rangeSeekBar.getSelectedMaxValue().intValue() * 1000);
//                } else
//                    Snackbar.make(mainlayout, "Please upload a video", 4000).show();
//            }
//        });

//        extractImages.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                choice = 3;
//
//                if (selectedImageUri != null) {
//                    extractImagesVideo(rangeSeekBar.getSelectedMinValue().intValue() * 1000, rangeSeekBar.getSelectedMaxValue().intValue() * 1000);
//                } else
//                    Snackbar.make(mainlayout, "Please upload a video", 4000).show();
//
//            }
//        });
//        extractAudio.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                choice = 4;
//
//                if (selectedImageUri != null) {
//                    if (Build.VERSION.SDK_INT >= 23)
//                        getAudioPermission();
//                    else
//                        extractAudioVideo();
//                } else
//                    Snackbar.make(mainlayout, "Please upload a video", 4000).show();
//            }
//        });
//        fadeEffect.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                choice = 5;
//                if (selectedImageUri != null) {
//                    executeFadeInFadeOutCommand();
//                } else
//                    Snackbar.make(mainlayout, "Please upload a video", 4000).show();
//            }
//        });
//
//        increaseSpeed.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                choice = 6;
//                if (selectedImageUri != null) {
//                    executeFastMotionVideoCommand();
//                } else
//                    Snackbar.make(mainlayout, "Please upload a video", 4000).show();
//            }
//        });
//        decreaseSpeed.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                choice = 7;
//                if (selectedImageUri != null) {
//                    executeSlowMotionVideoCommand();
//                } else
//                    Snackbar.make(mainlayout, "Please upload a video", 4000).show();
//            }
//        });
//
//        reverseVideo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (selectedImageUri != null) {
//                    choice = 8;
//                    final Dialog dialog = showSingleOptionTextDialog(mContext);
//                    TextView tvDialogHeading = (TextView) dialog.findViewById(R.id.tvDialogHeading);
//                    TextView tvDialogText = (TextView) dialog.findViewById(R.id.tvDialogText);
//                    TextView tvDialogSubmit = (TextView) dialog.findViewById(R.id.tvDialogSubmit);
//                    tvDialogHeading.setText("Process in Progress");
//                    tvDialogText.setText(R.string.dialogMessage);
//                    tvDialogSubmit.setText("Okay");
//                    tvDialogSubmit.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            String yourRealPath = getPath(MainActivity.this, selectedImageUri);
//                            splitVideoCommand(yourRealPath);
//                            dialog.dismiss();
//                        }
//
//                    });
//                    dialog.show();
//
//                } else
//                    Snackbar.make(mainlayout, "Please upload a video", 4000).show();
//            }
//        });
    }

    private void getPermission() {
        String[] params = null;
        String writeExternalStorage = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        String readExternalStorage = Manifest.permission.READ_EXTERNAL_STORAGE;

        int hasWriteExternalStoragePermission = ActivityCompat.checkSelfPermission(this, writeExternalStorage);
        int hasReadExternalStoragePermission = ActivityCompat.checkSelfPermission(this, readExternalStorage);
        List<String> permissions = new ArrayList<String>();

        if (hasWriteExternalStoragePermission != PackageManager.PERMISSION_GRANTED)
            permissions.add(writeExternalStorage);
        if (hasReadExternalStoragePermission != PackageManager.PERMISSION_GRANTED)
            permissions.add(readExternalStorage);

        if (!permissions.isEmpty()) {
            params = permissions.toArray(new String[permissions.size()]);
        }
        if (params != null && params.length > 0) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    params,
                    100);
        } else
            uploadVideo();
    }

    private void getAudioPermission() {
        String[] params = null;
        String recordAudio = Manifest.permission.RECORD_AUDIO;
        String modifyAudio = Manifest.permission.MODIFY_AUDIO_SETTINGS;

        int hasRecordAudioPermission = ActivityCompat.checkSelfPermission(this, recordAudio);
        int hasModifyAudioPermission = ActivityCompat.checkSelfPermission(this, modifyAudio);
        List<String> permissions = new ArrayList<String>();

        if (hasRecordAudioPermission != PackageManager.PERMISSION_GRANTED)
            permissions.add(recordAudio);
        if (hasModifyAudioPermission != PackageManager.PERMISSION_GRANTED)
            permissions.add(modifyAudio);

        if (!permissions.isEmpty()) {
            params = permissions.toArray(new String[permissions.size()]);
        }
        if (params != null && params.length > 0) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    params,
                    200);
        } else
            extractAudioVideo();
    }

    /**
     * Handling response for permission request
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 100: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    uploadVideo();
                }
            }
            break;
            case 200: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    extractAudioVideo();
                }
            }


        }
    }

    /**
     * Opening gallery for uploading video
     */
    private void uploadVideo() {
        try {
            Intent intent = new Intent();
            intent.setType("video/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivityForResult(Intent.createChooser(intent, "Select Video"), REQUEST_TAKE_GALLERY_VIDEO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopPosition = videoView.getCurrentPosition(); //stopPosition is an int
        videoView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoView.seekTo(stopPosition);
        videoView.start();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_TAKE_GALLERY_VIDEO) {
                if (btnUploadVideo.getText().toString().equals(UPLOAD_FIRST_VIDEO)) {
                    firstVideoUri = data.getData();
                    videoView.setVideoURI(firstVideoUri);
                    videoView.start();
                    btnUploadVideo.setText(UPLOAD_SECOND_VIDEO);
                } else if (btnUploadVideo.getText().toString().equals(UPLOAD_SECOND_VIDEO)) {
                    secondVideoUri = data.getData();
                    videoView.setVideoURI(secondVideoUri);
                    videoView.start();
                } else {
                    selectedImageUri = data.getData();
                    videoView.setVideoURI(selectedImageUri);
                    videoView.start();
                }


                videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        // TODO Auto-generated method stub
                        duration = mp.getDuration() / 1000;
                        tvLeft.setText("00:00:00");

                        tvRight.setText(getTime(mp.getDuration() / 1000));
                        mp.setLooping(true);
                        rangeSeekBar.setRangeValues(0, duration);
                        rangeSeekBar.setSelectedMinValue(0);
                        rangeSeekBar.setSelectedMaxValue(duration);
                        rangeSeekBar.setEnabled(true);

                        rangeSeekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener() {
                            @Override
                            public void onRangeSeekBarValuesChanged(RangeSeekBar bar, Number minValue, Number maxValue) {
                                videoView.seekTo((int) minValue * 1000);

                                tvLeft.setText(getTime((int) bar.getSelectedMinValue()));

                                tvRight.setText(getTime((int) bar.getSelectedMaxValue()));
                            }
                        });
//                        rangeSeekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener() {
//                            @Override
//                            public void onRangeSeekBarValuesChanged(RangeSeekBar bar, Object minValue, Object maxValue) {
//                                videoView.seekTo((int) minValue * 1000);
//
//                                tvLeft.setText(getTime((int) bar.getSelectedMinValue()));
//
//                                tvRight.setText(getTime((int) bar.getSelectedMaxValue()));
//
//                            }
//                        });

                        final Handler handler = new Handler();
                        handler.postDelayed(r = new Runnable() {
                            @Override
                            public void run() {

                                if (videoView.getCurrentPosition() >= rangeSeekBar.getSelectedMaxValue().intValue() * 1000)
                                    videoView.seekTo(rangeSeekBar.getSelectedMinValue().intValue() * 1000);
                                handler.postDelayed(r, 1000);
                            }
                        }, 1000);

                    }
                });

//                }
            }
        }
    }

    private String getTime(int seconds) {
        int hr = seconds / 3600;
        int rem = seconds % 3600;
        int mn = rem / 60;
        int sec = rem % 60;
        return String.format("%02d", hr) + ":" + String.format("%02d", mn) + ":" + String.format("%02d", sec);
    }

    /**
     * Load FFmpeg binary
     */
    private void loadFFMpegBinary() {
        try {
            if (ffmpeg == null) {
                Log.d(TAG, "ffmpeg : era nulo");
                ffmpeg = FFmpeg.getInstance(this);
            }
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {
                @Override
                public void onFailure() {
                    showUnsupportedExceptionDialog();
                }

                @Override
                public void onSuccess() {
                    Log.d(TAG, "ffmpeg : correct Loaded");
                }
            });
        } catch (FFmpegNotSupportedException e) {
            showUnsupportedExceptionDialog();
        } catch (Exception e) {
            Log.d(TAG, "EXception no controlada : " + e);
        }
    }

    private void showUnsupportedExceptionDialog() {
        new AlertDialog.Builder(MainActivity.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Not Supported")
                .setMessage("Device Not Supported")
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.this.finish();
                    }
                })
                .create()
                .show();

    }

    /**
     * Command for cutting video created here
     */
    private void executeCutVideoCommand(int startMs, int endMs) {
        File moviesDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MOVIES
        );

        String filePrefix = "trimed_video";
        String fileExtn = ".mp4";
        String yourRealPath = getPathWithExceptionHandling(selectedImageUri);
        File dest = new File(moviesDir, filePrefix + fileExtn);
        int fileNo = 0;
        while (dest.exists()) {
            fileNo++;
            dest = new File(moviesDir, filePrefix + fileNo + fileExtn);
        }

        Log.d(TAG, "startTrim: src: " + yourRealPath);
        Log.d(TAG, "startTrim: dest: " + dest.getAbsolutePath());
        Log.d(TAG, "startTrim: startMs: " + startMs);
        Log.d(TAG, "startTrim: endMs: " + endMs);
        filePath = dest.getAbsolutePath();
        String[] complexCommand = {"-ss", "" + startMs / 1000, "-y", "-i", yourRealPath, "-t", "" + (endMs - startMs) / 1000, "-vcodec", "mpeg4", "-b:v", "2097152", "-b:a", "48000", "-ar", "22050", filePath};

        execFFmpegBinary(complexCommand);

    }


    private void executeVideoToGifCommand(int startMs, int endMs) {
        File moviesDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MOVIES
        );

        String filePrefix = "video_to_gif";
        String fileExtn = ".gif";
        String yourRealPath = getPathWithExceptionHandling(selectedImageUri);


        File dest = new File(moviesDir, filePrefix + fileExtn);
        int fileNo = 0;
        while (dest.exists()) {
            fileNo++;
            dest = new File(moviesDir, filePrefix + fileNo + fileExtn);
        }

        Log.d(TAG, "startTrim: src: " + yourRealPath);
        Log.d(TAG, "startTrim: dest: " + dest.getAbsolutePath());
        filePath = dest.getAbsolutePath();
        String[] complexCommand = {"-ss", "" + startMs / 1000, "-y", "-i", yourRealPath, "-t", "" + (endMs - startMs) / 1000, "-pix_fmt", "rgb24", "-r", "" + discreteSeekbarValue, filePath};
        execFFmpegBinary(complexCommand);

    }

    private void mergeVideoCommand() {

        String[] arrVideosRealPaths = new String[2];
        arrVideosRealPaths[0] = getPathWithExceptionHandling(firstVideoUri);
        arrVideosRealPaths[1] = getPathWithExceptionHandling(secondVideoUri);
        File firstVideoFile = new File(firstVideoUri.getPath());
        File secondVideoFile = new File(secondVideoUri.getPath());
        File[] files = new File[2];
        files[0] = firstVideoFile;
        files[1] = secondVideoFile;

        StringBuilder stringBuilder = new StringBuilder();
        StringBuilder filterComplex = new StringBuilder();
        filterComplex.append("-filter_complex,");
        for (int i = 0; i < files.length; i++) {
            stringBuilder.append("-i" + "," + arrVideosRealPaths[i] + ",");
            filterComplex.append("[").append(i).append(":v").append(i).append("] [").append(i).append(":a").append(i).append("] ");

        }
        filterComplex.append("concat=n=").append(files.length).append(":v=1:a=1 [v] [a]");
        String[] inputCommand = stringBuilder.toString().split(",");
        String[] filterCommand = filterComplex.toString().split(",");


        File moviesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);

        String filePrefix = "merged_video";
        String fileExtn = ".mp4";
        File dest = new File(moviesDir, filePrefix + fileExtn);
        int fileNo = 0;
        while (dest.exists()) {
            fileNo++;
            dest = new File(moviesDir, filePrefix + fileNo + fileExtn);
        }
        filePath = dest.getAbsolutePath();
        String[] destinationCommand = {"-map", "[v]", "-map", "[a]", dest.getAbsolutePath()};
        execFFmpegBinary(combine(inputCommand, filterCommand, destinationCommand));
    }

    /**
     * Command for compressing video created here
     */
    private void executeCompressCommand() {
        File moviesDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MOVIES
        );

        String filePrefix = "compressed_video";
        String fileExtn = ".mp4";
        String yourRealPath = getPathWithExceptionHandling(selectedImageUri);


        File dest = new File(moviesDir, filePrefix + fileExtn);
        int fileNo = 0;
        while (dest.exists()) {
            fileNo++;
            dest = new File(moviesDir, filePrefix + fileNo + fileExtn);
        }

        Log.d(TAG, "startTrim: src: " + yourRealPath);
        Log.d(TAG, "startTrim: dest: " + dest.getAbsolutePath());
        filePath = dest.getAbsolutePath();
        String[] complexCommand = {"-y", "-i", yourRealPath, "-r", "25", "-vcodec", "mpeg4", "-b:v", "150k", "-b:a", "48000", "-ac", "2", "-ar", "22050", filePath};
        execFFmpegBinary(complexCommand);

    }

    /**
     * Command for extracting images from video created here
     */
    private void extractImagesVideo(int startMs, int endMs) {
        File moviesDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES
        );

        String filePrefix = "extract_picture";
        String fileExtn = ".jpg";
        String yourRealPath = getPathWithExceptionHandling(selectedImageUri);

        File dir = new File(moviesDir, getString(R.string.app_name));
        int fileNo = 0;
        while (dir.exists()) {
            fileNo++;
            dir = new File(moviesDir, getString(R.string.app_name) + fileNo);

        }
        dir.mkdir();
        filePath = dir.getAbsolutePath();
        File dest = new File(dir, filePrefix + "%03d" + fileExtn);


        Log.d(TAG, "startTrim: src: " + yourRealPath);
        Log.d(TAG, "startTrim: dest: " + dest.getAbsolutePath());

        String[] complexCommand = {"-y", "-i", yourRealPath, "-an", "-r", "1/2", "-ss", "" + startMs / 1000, "-t", "" + (endMs - startMs) / 1000, dest.getAbsolutePath()};

        execFFmpegBinary(complexCommand);

    }

    /**
     * Command for adding fade in fade out effect at start and end of video created here
     */
    private void executeFadeInFadeOutCommand() {
        File moviesDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MOVIES
        );

        String filePrefix = "fade_video";
        String fileExtn = ".mp4";
//        String yourRealPath = getPath(MainActivity.this, selectedImageUri);
        String yourRealPath = getPathWithExceptionHandling(selectedImageUri);


        File dest = new File(moviesDir, filePrefix + fileExtn);
        int fileNo = 0;
        while (dest.exists()) {
            fileNo++;
            dest = new File(moviesDir, filePrefix + fileNo + fileExtn);
        }


        Log.d(TAG, "startTrim: src: " + yourRealPath);
        Log.d(TAG, "startTrim: dest: " + dest.getAbsolutePath());
        filePath = dest.getAbsolutePath();
        String[] complexCommand = {"-y", "-i", yourRealPath, "-acodec", "copy", "-vf", "fade=t=in:st=0:d=2,fade=t=out:st=" + String.valueOf(duration - 2) + ":d=2", filePath};
        execFFmpegBinary(complexCommand);

    }

    /**
     * Command for creating fast motion video
     */
    private void executeFastMotionVideoCommand() {
        File moviesDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MOVIES
        );

        String filePrefix = "fast_motion_video";
        String fileExtn = ".mp4";
//        String yourRealPath = getPath(MainActivity.this, selectedImageUri);
        String yourRealPath = getPathWithExceptionHandling(selectedImageUri);


        File dest = new File(moviesDir, filePrefix + fileExtn);
        int fileNo = 0;
        while (dest.exists()) {
            fileNo++;
            dest = new File(moviesDir, filePrefix + fileNo + fileExtn);
        }


        Log.d(TAG, "startTrim: src: " + yourRealPath);
        Log.d(TAG, "startTrim: dest: " + dest.getAbsolutePath());
        filePath = dest.getAbsolutePath();
        String[] complexCommand = {"-y", "-i", yourRealPath, "-filter_complex", "[0:v]setpts=0.5*PTS[v];[0:a]atempo=2.0[a]", "-map", "[v]", "-map", "[a]", "-b:v", "2097k", "-r", "60", "-vcodec", "mpeg4", filePath};
        execFFmpegBinary(complexCommand);

    }

    /**
     * Command for creating slow motion video
     */
    private void executeSlowMotionVideoCommand() {
        File moviesDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MOVIES
        );

        String filePrefix = "slow_motion_video";
        String fileExtn = ".mp4";
//        String yourRealPath = getPath(MainActivity.this, selectedImageUri);
        String yourRealPath = getPathWithExceptionHandling(selectedImageUri);


        File dest = new File(moviesDir, filePrefix + fileExtn);
        int fileNo = 0;
        while (dest.exists()) {
            fileNo++;
            dest = new File(moviesDir, filePrefix + fileNo + fileExtn);
        }


        Log.d(TAG, "startTrim: src: " + yourRealPath);
        Log.d(TAG, "startTrim: dest: " + dest.getAbsolutePath());
        filePath = dest.getAbsolutePath();
        String[] complexCommand = {"-y", "-i", yourRealPath, "-filter_complex", "[0:v]setpts=2.0*PTS[v];[0:a]atempo=0.5[a]", "-map", "[v]", "-map", "[a]", "-b:v", "2097k", "-r", "60", "-vcodec", "mpeg4", filePath};
        execFFmpegBinary(complexCommand);

    }

    /**
     * Command for extracting audio from video
     */
    private void extractAudioVideo() {
        File moviesDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MUSIC
        );

        String filePrefix = "extracted_audio";
        String fileExtn = ".mp3";

        String yourRealPath = getPathWithExceptionHandling(selectedImageUri);
        File dest = new File(moviesDir, filePrefix + fileExtn);

        int fileNo = 0;
        while (dest.exists()) {
            fileNo++;
            dest = new File(moviesDir, filePrefix + fileNo + fileExtn);
        }
        Log.d(TAG, "startTrim: src: " + yourRealPath);
        Log.d(TAG, "startTrim: dest: " + dest.getAbsolutePath());
        filePath = dest.getAbsolutePath();

        String[] complexCommand = {"-y", "-i", yourRealPath, "-vn", "-ar", "44100", "-ac", "2", "-b:a", "256k", "-f", "mp3", filePath};

        execFFmpegBinary(complexCommand);

    }

    private void splitVideoCommand(String path) {
        File moviesDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MOVIES
        );
        String filePrefix = "split_video";
        String fileExtn = ".mp4";
        String yourRealPath = path;

        File dir = new File(moviesDir, ".VideoSplit");
        if (dir.exists())
            deleteDir(dir);
        dir.mkdir();
        File dest = new File(dir, filePrefix + "%03d" + fileExtn);

        String[] complexCommand = {"-i", yourRealPath, "-c:v", "libx264", "-crf", "22", "-map", "0", "-segment_time", "6", "-g", "9", "-sc_threshold", "0", "-force_key_frames", "expr:gte(t,n_forced*9)", "-f", "segment", dest.getAbsolutePath()};
        execFFmpegBinary(complexCommand);
    }

    private void reverseVideoCommand() {
        File moviesDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MOVIES
        );
        File srcDir = new File(moviesDir, ".VideoSplit");
        File[] files = srcDir.listFiles();
        String filePrefix = "reverse_video";
        String fileExtn = ".mp4";
        File destDir = new File(moviesDir, ".VideoPartsReverse");
        if (destDir.exists())
            deleteDir(destDir);
        destDir.mkdir();
        for (int i = 0; i < files.length; i++) {
            File dest = new File(destDir, filePrefix + i + fileExtn);
            String command[] = {"-i", files[i].getAbsolutePath(), "-vf", "reverse", "-af", "areverse", dest.getAbsolutePath()};
            if (i == files.length - 1)
                lastReverseCommand = command;
            execFFmpegBinary(command);
        }
    }

    private String getPathWithExceptionHandling(Uri uri) {

        String yourActualPath;
        try {
            yourActualPath = getPath(MainActivity.this, uri);
        } catch (Exception e) {
            String strUri = uri.toString();
            strUri = strUri.substring(strUri.indexOf("/storage"));
            yourActualPath = strUri;
        }
        return yourActualPath;
    }

    private void concatVideoCommand() {
        File moviesDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MOVIES
        );
        File srcDir = new File(moviesDir, ".VideoPartsReverse");
        File[] files = srcDir.listFiles();
        if (files != null && files.length > 1) {
            Arrays.sort(files, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
        }
        StringBuilder stringBuilder = new StringBuilder();
        StringBuilder filterComplex = new StringBuilder();
        filterComplex.append("-filter_complex,");
        for (int i = 0; i < files.length; i++) {
            stringBuilder.append("-i" + "," + files[i].getAbsolutePath() + ",");
            filterComplex.append("[").append(i).append(":v").append(i).append("] [").append(i).append(":a").append(i).append("] ");

        }
        filterComplex.append("concat=n=").append(files.length).append(":v=1:a=1 [v] [a]");
        String[] inputCommand = stringBuilder.toString().split(",");
        String[] filterCommand = filterComplex.toString().split(",");

        String filePrefix = "reverse_video";
        String fileExtn = ".mp4";
        File dest = new File(moviesDir, filePrefix + fileExtn);
        int fileNo = 0;
        while (dest.exists()) {
            fileNo++;
            dest = new File(moviesDir, filePrefix + fileNo + fileExtn);
        }
        filePath = dest.getAbsolutePath();
        String[] destinationCommand = {"-map", "[v]", "-map", "[a]", dest.getAbsolutePath()};
        execFFmpegBinary(combine(inputCommand, filterCommand, destinationCommand));
    }

    public static String[] combine(String[] arg1, String[] arg2, String[] arg3) {
        String[] result = new String[arg1.length + arg2.length + arg3.length];
        System.arraycopy(arg1, 0, result, 0, arg1.length);
        System.arraycopy(arg2, 0, result, arg1.length, arg2.length);
        System.arraycopy(arg3, 0, result, arg1.length + arg2.length, arg3.length);
        return result;
    }

    /**
     * Executing ffmpeg binary
     */
    private void execFFmpegBinary(final String[] command) {
        try {
            ffmpeg.execute(command, new ExecuteBinaryResponseHandler() {
                @Override
                public void onFailure(String s) {
                    Log.d(TAG, "FAILED with output : " + s);
                }

                @Override
                public void onSuccess(String s) {
                    Log.d(TAG, "SUCCESS with output : " + s);
                    if (choice == 1 || choice == 2 || choice == 5 || choice == 6 || choice == 7 || choice == 12) {
                        Intent intent = new Intent(MainActivity.this, PreviewActivity.class);
                        intent.putExtra(FILEPATH, filePath);
                        startActivity(intent);
                        finish();
                    } else if (choice == 3) {
                        Intent intent = new Intent(MainActivity.this, PreviewImageActivity.class);
                        intent.putExtra(FILEPATH, filePath);
                        startActivity(intent);
                        finish();
                    } else if (choice == 4) {
                        Intent intent = new Intent(MainActivity.this, AudioPreviewActivity.class);
                        intent.putExtra(FILEPATH, filePath);
                        startActivity(intent);
                        finish();
                    } else if (choice == 8) {
                        choice = 9;
                        reverseVideoCommand();
                    } else if (Arrays.equals(command, lastReverseCommand)) {
                        choice = 10;
                        concatVideoCommand();
                    } else if (choice == 10) {
                        File moviesDir = Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_MOVIES
                        );
                        File destDir = new File(moviesDir, ".VideoPartsReverse");
                        File dir = new File(moviesDir, ".VideoSplit");
                        if (dir.exists())
                            deleteDir(dir);
                        if (destDir.exists())
                            deleteDir(destDir);
                        choice = 11;
                        Intent intent = new Intent(MainActivity.this, PreviewActivity.class);
                        intent.putExtra(FILEPATH, filePath);
                        startActivity(intent);
                        finish();
                    } else if (choice == 13) {
                        Intent intent = new Intent(MainActivity.this, PreviewGifActivity.class);
                        intent.putExtra(FILEPATH, filePath);
                        startActivity(intent);
                        finish();
                    }
                }

                @Override
                public void onProgress(String s) {
//                    Log.d(TAG, "Started command : ffmpeg " + command);
//                    if (choice == 8)
//                        progressDialog.setMessage("progress : splitting video " + s);
//                    else if (choice == 9)
//                        progressDialog.setMessage("progress : reversing splitted videos " + s);
//                    else if (choice == 10)
//                        progressDialog.setMessage("progress : concatenating reversed videos " + s);
//                    else
//                        progressDialog.setMessage("progress : " + s);
//                    Log.d(TAG, "progress : " + s);
                }

                @Override
                public void onStart() {
                    Log.d(TAG, "Started command : ffmpeg " + command);
                    progressDialog.setMessage("Please wait...");
                    progressDialog.show();
                }

                @Override
                public void onFinish() {
                    Log.d(TAG, "Finished command : ffmpeg " + command);
                    if (choice != 8 && choice != 9 && choice != 10) {
                        progressDialog.dismiss();
                    }

                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            // do nothing for now

            e.printStackTrace();
        }
    }


    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            if (children != null) {
                for (int i = 0; i < children.length; i++) {
                    boolean success = deleteDir(new File(dir, children[i]));
                    if (!success) {
                        return false;
                    }
                }
            }
        }
        return dir.delete();
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     */
    private String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri.
     */
    private String getDataColumn(Context context, Uri uri, String selection,
                                 String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private Dialog showSingleOptionTextDialog(Context mContext) {
        Dialog textDialog = new Dialog(mContext, R.style.DialogAnimation);
        textDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        textDialog.setContentView(R.layout.dialog_singleoption_text);
        textDialog.setCancelable(false);
        return textDialog;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    private void getHashKey() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(BuildConfig.APPLICATION_ID, PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String sign = Base64.encodeToString(md.digest(), Base64.DEFAULT);
                Log.e("hash key:", sign);
                Toast.makeText(getApplicationContext(), sign, Toast.LENGTH_LONG).show();
            }
        } catch (PackageManager.NameNotFoundException e) {
        } catch (NoSuchAlgorithmException e) {
        }
    }

}
