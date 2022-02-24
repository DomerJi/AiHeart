package com.thfw.robotheart.util;

import android.content.Context;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.thfw.robotheart.constants.UIConfig;

import static com.google.android.exoplayer2.DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS;
import static com.google.android.exoplayer2.DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_MS;
import static com.google.android.exoplayer2.DefaultLoadControl.DEFAULT_MAX_BUFFER_MS;
import static com.google.android.exoplayer2.DefaultLoadControl.DEFAULT_MIN_BUFFER_MS;

public class ExoPlayerFactory {
    public static final int EXO_AUDIO = 0;
    public static final int EXO_VIDEO = 1;
    private static SimpleExoPlayer exoPlayer;
    private static int type;

    private ExoPlayerFactory() {
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

    public static boolean isPlaying() {
        return exoPlayer != null && exoPlayer.isPlaying();
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
            DefaultBandwidthMeter mDefaultBandwidthMeter = new DefaultBandwidthMeter
                    .Builder(mContext)
                    .build();
            DefaultDataSourceFactory upstreamFactory = new DefaultDataSourceFactory(mContext, mDefaultBandwidthMeter,
                    new DefaultHttpDataSourceFactory(UIConfig.getUserAgent(), null, 15000, 15000, true));

            switch (type) {
                case EXO_AUDIO:

                    exoPlayer = new SimpleExoPlayer.Builder(mContext)
                            .setMediaSourceFactory(new ProgressiveMediaSource.Factory(upstreamFactory))
                            .setAudioAttributes(AudioAttributes.DEFAULT, true)
                            .build();
                    break;
                case EXO_VIDEO:

                    LoadControl loadControl = new DefaultLoadControl.Builder()
                            .setBufferDurationsMs(DEFAULT_MIN_BUFFER_MS * 2,
                                    DEFAULT_MAX_BUFFER_MS * 10,
                                    DEFAULT_BUFFER_FOR_PLAYBACK_MS * 2,
                                    DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS * 3).build();

                    exoPlayer = new SimpleExoPlayer.Builder(mContext)
                            .setMediaSourceFactory(new ProgressiveMediaSource.Factory(upstreamFactory))
                            .setBandwidthMeter(mDefaultBandwidthMeter)
                            .setLoadControl(loadControl).build();
                    break;
            }
        }
    }
}
