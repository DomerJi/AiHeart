package com.thfw.mobileheart.activity.audio;

import android.animation.Animator;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.thfw.base.base.IPresenter;
import com.thfw.base.utils.BitmapUtil;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.PaletteUtil;
import com.thfw.base.utils.ToastUtil;
import com.thfw.mobileheart.AudioService;
import com.thfw.mobileheart.util.AudioModel;
import com.thfw.mobileheart.util.ExoPlayerFactory;
import com.thfw.robotheart.R;
import com.thfw.ui.base.BaseActivity;
import com.thfw.ui.widget.TitleView;

import java.io.IOException;
import java.net.UnknownHostException;

import static android.view.View.VISIBLE;

public class AudioPlayerActivity extends BaseActivity {


    private com.google.android.exoplayer2.ui.StyledPlayerView mAudioView;
    private ImageView mIvBlurBg;
    private ProgressBar mPbBar;
    private View btPlay;
    private View btPause;
    private ProgressBar mPbLoading;
    private com.thfw.ui.widget.TitleView mTitleView;
    private ImageView mIvShare;
    private View[] views;

    @Override
    public int getContentView() {
        return R.layout.activity_audio_player;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initView() {

        mAudioView = (StyledPlayerView) findViewById(R.id.audio_view);
        mIvBlurBg = (ImageView) findViewById(R.id.iv_blur_bg);
        mPbBar = findViewById(R.id.pb_loading);
        Bitmap bitmap = BitmapUtil.getResourceBitmap(mContext, R.mipmap.cat);
        mIvBlurBg.setImageBitmap(PaletteUtil.doBlur(bitmap, 2, true));
        btPlay = findViewById(R.id.exo_play);
        btPause = findViewById(R.id.exo_pause);


        mPbLoading = (ProgressBar) findViewById(R.id.pb_loading);
        mTitleView = (TitleView) findViewById(R.id.titleView);
        mTitleView.getIvBack().setPadding(4, 4, 4, 4);
        mIvShare = (ImageView) findViewById(R.id.iv_share);
        View mVborder01 = findViewById(R.id.v_border_01);
        View mVborder02 = findViewById(R.id.v_border_02);
        View mVborder03 = findViewById(R.id.v_border_03);
        views = new View[]{mVborder01, mVborder02, mVborder03};
        startAnimateBorder(mVborder03, 100);
        startAnimateBorder(mVborder02, 900);
    }


    private void startAnimateBorder(View view, long delay) {

        view.animate().scaleY(2).scaleX(2).alpha(0).setDuration(1600)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setStartDelay(delay)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (EmptyUtil.isEmpty(AudioPlayerActivity.this)) {
                            return;
                        }
                        view.setScaleX(1);
                        view.setScaleY(1);
                        view.setAlpha(1);
                        startAnimateBorder(view, 0);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                }).start();
    }

    @Override
    public void initData() {
        ExoPlayerFactory.with(mContext).builder(ExoPlayerFactory.EXO_AUDIO);
        SimpleExoPlayer player = ExoPlayerFactory.getExoPlayer();
        MediaItem mediaItem = MediaItem.fromUri("http://music.163.com/song/media/outer/url?id=1299844911.mp3");
        MediaItem mediaItem01 = MediaItem.fromUri("https://sharefs.ali.kugou.com/202108030909/85abc1bb7135869570b0817338f49ccf/KGTX/CLTX001/cc8ce4b622f911f2f46babd6f26af082.mp3");
        PlayerListener playerListener = new PlayerListener();
        playerListener.setPbBar(mPbBar);
        player.addListener(playerListener);
        mAudioView.setPlayer(player);


        player.addMediaItem(mediaItem);
        player.addMediaItem(mediaItem01);
        player.addMediaItem(mediaItem);
        player.addMediaItem(mediaItem01);

        player.prepare();
        // Start the playback.
        player.play();

        btPlay.setOnClickListener(v -> {
            ExoPlayerFactory.getExoPlayer().play();
        });


        btPause.setOnClickListener(v -> {
            ExoPlayerFactory.getExoPlayer().pause();
        });

        btPlay.setVisibility(View.GONE);
        btPause.setVisibility(View.VISIBLE);

        Intent mServiceIntent = new Intent(mContext, AudioService.class);
        mContext.bindService(mServiceIntent, connection, Context.BIND_AUTO_CREATE);


    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            AudioService.MyBinder mMyBinder = (AudioService.MyBinder) service;
            AudioService mMyService = mMyBinder.getService();
            mMyBinder.setMusic(new AudioModel());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        mAudioView.setUseController(true);
        mAudioView.setControllerHideOnTouch(false);
        mAudioView.setControllerShowTimeoutMs(0);
        mAudioView.showController();
    }

    @Override
    public int getStatusBarColor() {
        return STATUSBAR_TRANSPARENT;
    }


    public class PlayerListener implements Player.Listener {
        private ProgressBar mPbBar;

        public void setPbBar(ProgressBar mPbBar) {
            this.mPbBar = mPbBar;
        }

        public ProgressBar getLoading() {
            return mPbBar;
        }

        @Override
        public void onPlaybackStateChanged(int state) {
            ProgressBar mPbBar = getLoading();
            if (mPbBar == null) {
                return;
            }

            if (state == Player.STATE_READY || state == Player.STATE_ENDED) {
                mPbBar.setVisibility(View.GONE);
            } else {
                mPbBar.setVisibility(VISIBLE);
            }
            if ("error".equals(String.valueOf(mPbBar.getTag()))) {
                mPbBar.setVisibility(View.GONE);
                mPbBar.setTag("");
            }

        }

        @Override
        public void onPlayWhenReadyChanged(boolean playWhenReady, int reason) {
            btPause.setVisibility(!playWhenReady ? View.GONE : View.VISIBLE);
            btPlay.setVisibility(playWhenReady ? View.GONE : View.VISIBLE);
        }

        @Override
        public void onMediaItemTransition(@Nullable @org.jetbrains.annotations.Nullable MediaItem mediaItem, int reason) {
            // 列表播放曲目切换监听
            LogUtil.d("onMediaItemTransition -> " + mediaItem.playbackProperties.uri);
        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {
            ProgressBar mPbBar = getLoading();
            if (mPbBar == null) {
                return;
            }
            mPbBar.setTag("error");
            mPbBar.setVisibility(View.GONE);

            if (error.type == ExoPlaybackException.TYPE_SOURCE) {
                IOException cause = error.getSourceException();
                if (cause instanceof HttpDataSource.HttpDataSourceException) {
                    // An HTTP error occurred.
                    HttpDataSource.HttpDataSourceException httpError = (HttpDataSource.HttpDataSourceException) cause;
                    // This is the request for which the error occurred.
                    DataSpec requestDataSpec = httpError.dataSpec;
                    // It's possible to find out more about the error both by casting and by
                    // querying the cause.
                    if (httpError instanceof HttpDataSource.InvalidResponseCodeException) {
                        // Cast to InvalidResponseCodeException and retrieve the response code,
                        // message and headers.
                        ToastUtil.show("视频资源错误");
                    } else {
                        // Try calling httpError.getCause() to retrieve the underlying cause,
                        // although note that it may be null.

                        if (httpError.getCause() instanceof UnknownHostException) {
                            ToastUtil.show("网络异常，请检查网络");
                        } else {
                            ToastUtil.show("未知错误" + httpError.getCause());
                        }
                    }
                }
            } else if (error.type == ExoPlaybackException.TYPE_RENDERER) {
                ToastUtil.show("渲染错误 - TYPE_UNEXPECTED");
            } else if (error.type == ExoPlaybackException.TYPE_UNEXPECTED) {
                ToastUtil.show("未知错误 - TYPE_UNEXPECTED");
            } else if (error.type == ExoPlaybackException.TYPE_REMOTE) {
                ToastUtil.show("网络错误");
            }

        }
    }
}