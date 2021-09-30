package com.thfw.mobileheart.util;

import android.content.Context;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.audio.AudioAttributes;

public class ExoPlayerFactory {
    private static SimpleExoPlayer exoPlayer;
    public static final int EXO_AUDIO = 0;
    public static final int EXO_VIDEO = 1;
    private static int type;

    private ExoPlayerFactory() {
    }

    public static class Builder {

        private Context mContext;

        public Builder(Context mContext) {
            this.mContext = mContext;
        }

        public void builder(int type) {
            if (ExoPlayerFactory.type == type && exoPlayer != null) {
                return;
            }

            release();
            ExoPlayerFactory.type = type;
            switch (type) {
                case EXO_AUDIO:
                    exoPlayer = new SimpleExoPlayer.Builder(mContext)
                            .setAudioAttributes(AudioAttributes.DEFAULT, true)
                            .build();
                    break;
                case EXO_VIDEO:
                    exoPlayer = new SimpleExoPlayer.Builder(mContext).build();
                    break;
            }
        }
    }


    public static Builder with(Context context) {
        return new Builder(context);
    }

    public static void release() {
        type = -1;
        if (exoPlayer != null) {
            synchronized (ExoPlayerFactory.class) {
                if (exoPlayer != null) {
                    exoPlayer.release();
                    exoPlayer = null;
                }
            }
        }
    }

    public static SimpleExoPlayer getExoPlayer() {
        return exoPlayer;
    }
}
