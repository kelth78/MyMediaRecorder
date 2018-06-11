package com.kelth.mymediarecorder;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.IOException;

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static final String [] permissions = {Manifest.permission.RECORD_AUDIO};

    private TextView mStatus;
    private MediaPlayer mMediaPlayer;
    private MediaRecorder mMediaRecorder;
    private String mFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestPermissions( permissions, REQUEST_RECORD_AUDIO_PERMISSION );

        mStatus = findViewById(R.id.textView_status);

        mFileName = getExternalCacheDir().getAbsolutePath();
        mFileName += "/recording.3gp";
        Log.d(TAG, "FileNameToUse: " + mFileName );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "Permission not granted");
                finish();
            }
        }
    }

    public void onBtnRecord(View view) {
        ToggleButton btn = (ToggleButton)view;
        if (btn.isChecked()) {
            recordStart();
        }
        else {
            recordStop();
        }
    }

    public void onBtnPlay(View view) {
        ToggleButton btn = (ToggleButton)view;
        if (btn.isChecked()) {
            playStart();
        }
        else {
            playStop();
        }
    }

    void recordStart() {

        if (mMediaRecorder != null)
            return;

        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mMediaRecorder.setOutputFile(mFileName);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mMediaRecorder.prepare();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }

        mStatus.setText("Start Recording");
        mMediaRecorder.start();
    }

    void recordStop() {

        if (mMediaRecorder == null)
            return;

        mStatus.setText("Stop Recording");
        mMediaRecorder.stop();
        mMediaRecorder.release();
        mMediaRecorder = null;
    }

    void playStart() {

        if (mMediaPlayer != null)
            return;

        mMediaPlayer = new MediaPlayer();

        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                playStop();
            }
        });

        try {
            mMediaPlayer.setDataSource(mFileName);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (IOException e) {
            Log.e(TAG, "setDataSource() failed");
        }

        mStatus.setText("Start Playing");
    }

    void playStop() {

        if (mMediaPlayer == null)
            return;

        mStatus.setText("Stop Playing");
        mMediaPlayer.stop();
        mMediaPlayer.release();
        mMediaPlayer = null;
    }
}
