package com.thfw.aiui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Build;

import java.io.IOException;

public class WarningVoice implements MediaPlayer.OnCompletionListener {
    private static final String TAG = "MediaHelper";
    private Context mContext;
    MediaPlayer mediaPlayer;
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public WarningVoice(Context context, int playStream) {
        mContext = context.getApplicationContext();
        mediaPlayer = new MediaPlayer();
        AudioAttributes.Builder aa = new AudioAttributes.Builder();
        aa.setUsage(playStream);
        mediaPlayer.setAudioAttributes(aa.build());
        mediaPlayer.setOnCompletionListener(this);
        AssetFileDescriptor fileDescriptor;
        try {
            fileDescriptor = mContext.getAssets().openFd("warning.ogg");
            mediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(),
                    fileDescriptor.getStartOffset(),
                    fileDescriptor.getLength());
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void playWarning() {
//        if (mediaPlayer.isPlaying()) {
//            mediaPlayer.reset();
//        }
        mediaPlayer.start();
    }
        @Override
        public void onCompletion(MediaPlayer mp) {
            mediaPlayer.reset();
            AssetFileDescriptor fileDescriptor;
            try {
                fileDescriptor = mContext.getAssets().openFd("warning.ogg");
                mediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(),
                        fileDescriptor.getStartOffset(),
                        fileDescriptor.getLength());
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
//            playWarning();
        }
    }
