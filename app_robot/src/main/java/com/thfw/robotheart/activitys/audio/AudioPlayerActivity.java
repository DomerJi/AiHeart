package com.thfw.robotheart.activitys.audio;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.makeramen.roundedimageview.RoundedImageView;
import com.thfw.base.base.IPresenter;
import com.thfw.base.face.MyAnimationListener;
import com.thfw.base.face.OnRvItemListener;
import com.thfw.base.models.AudioItemModel;
import com.thfw.base.utils.BitmapUtil;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.PaletteUtil;
import com.thfw.base.utils.ToastUtil;
import com.thfw.robotheart.AudioService;
import com.thfw.robotheart.R;
import com.thfw.robotheart.adapter.AudioItemAdapter;
import com.thfw.robotheart.util.AudioModel;
import com.thfw.robotheart.util.ExoPlayerFactory;
import com.thfw.robotheart.view.TitleRobotView;
import com.thfw.ui.base.BaseActivity;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class AudioPlayerActivity extends BaseActivity {


    private StyledPlayerView mAudioView;
    private ImageView mIvBlurBg;
    private ProgressBar mPbBar;
    private View btPlay;
    private View btPause;
    private ProgressBar mPbLoading;
    private TitleRobotView mTitleView;
    private RoundedImageView mRivEtc;
    private ObjectAnimator animation;
    private ArrayList<AudioItemModel> mAudios = new ArrayList<>();
    private SimpleExoPlayer player;
    private ImageView mIvPlayCateLogue;
    private RecyclerView mRvList;
    boolean flDurationEnd = true;
    private ConstraintLayout mFlContent;

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
        mRivEtc = (RoundedImageView) findViewById(R.id.riv_etc);
        mIvPlayCateLogue = (ImageView) findViewById(R.id.iv_play_catelogue);
        mPbBar = findViewById(R.id.pb_loading);
        Bitmap bitmap = BitmapUtil.getResourceBitmap(mContext, R.mipmap.cat);
        mIvBlurBg.setImageBitmap(PaletteUtil.doBlur(bitmap, 2, true));
        btPlay = findViewById(R.id.exo_play);
        btPause = findViewById(R.id.exo_pause);

        mPbLoading = (ProgressBar) findViewById(R.id.pb_loading);
        mTitleView = (TitleRobotView) findViewById(R.id.titleRobotView);
        mRvList = (RecyclerView) findViewById(R.id.rv_list);
        mFlContent = (ConstraintLayout) findViewById(R.id.cl_content);
        mRvList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));

        mIvPlayCateLogue.setOnClickListener(v -> {
            audioLogcatue();
        });
        mFlContent.setOnClickListener(v -> {
            audioLogcatue();
        });


        AudioItemModel audioItemModel = new AudioItemModel();
        audioItemModel.url = "http://music.163.com/song/media/outer/url?id=1299844911.mp3";
        AudioItemModel audioItemModel2 = new AudioItemModel();
        audioItemModel2.url = "http://music.163.com/song/media/outer/url?id=186345.mp3";
        AudioItemModel audioItemModel3 = new AudioItemModel();
        audioItemModel3.url = "http://music.163.com/song/media/outer/url?id=33599439.mp3";
        AudioItemModel audioItemModel4 = new AudioItemModel();
        audioItemModel4.url = "http://music.163.com/song/media/outer/url?id=346576.mp3";

        mAudios.add(audioItemModel);
        mAudios.add(audioItemModel2);
        mAudios.add(audioItemModel3);
        mAudios.add(audioItemModel4);

    }

    private void audioLogcatue() {
        if (mFlContent.getVisibility() == View.VISIBLE) {

            mFlContent.animate().alpha(0f).setListener(new MyAnimationListener() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mFlContent.setVisibility(View.GONE);
                }
            }).start();
        } else {
            if (mRvList.getAdapter() == null) {
                TextView mTvEtcTitle = findViewById(R.id.tv_etc_title);
                mTvEtcTitle.setText("dijiajidoaj");
                AudioItemAdapter audioItemAdapter = new AudioItemAdapter(mAudios);
                audioItemAdapter.setOnRvItemListener(new OnRvItemListener<AudioItemModel>() {
                    @Override
                    public void onItemClick(List<AudioItemModel> list, int position) {
                        player.seekTo(position, 0);
                    }
                });
                mRvList.setAdapter(audioItemAdapter);
            }
            mFlContent.setAlpha(0f);
            mFlContent.setVisibility(View.VISIBLE);
            flDurationEnd = false;
            mFlContent.animate().alpha(1f).setListener(new MyAnimationListener() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    flDurationEnd = true;
                }
            });
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    private void etcAnimState(boolean play) {
        if (play) {
            if (animation == null) {
                animation = ObjectAnimator.ofFloat(mRivEtc, "rotation", 0, 360);
                animation.setRepeatCount(ObjectAnimator.INFINITE);
                animation.setRepeatMode(ObjectAnimator.RESTART);
                animation.setDuration(10000);
                animation.setInterpolator(new LinearInterpolator());
                animation.start();
            } else {
                animation.resume();
            }
        } else {
            if (animation != null) {
                animation.pause();
            }
        }

    }

    @Override
    public void initData() {
        ExoPlayerFactory.with(mContext).builder(ExoPlayerFactory.EXO_AUDIO);
        player = ExoPlayerFactory.getExoPlayer();

        PlayerListener playerListener = new PlayerListener();
        playerListener.setPbBar(mPbBar);
        player.addListener(playerListener);
        mAudioView.setPlayer(player);
        for (AudioItemModel mediaItem : mAudios) {
            player.addMediaItem(MediaItem.fromUri(mediaItem.url));
        }
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

        private void uiPlayOrPause(boolean play) {
            ProgressBar mPbBar = getLoading();
            if (mPbBar == null) {
                return;
            }
            mPbBar.setVisibility(play ? View.GONE : View.VISIBLE);
            if (play) {
                etcAnimState(true);
            }
        }

        /**
         * 音频切换
         *
         * @param audioSessionId
         */
        @Override
        public void onAudioSessionIdChanged(int audioSessionId) {
            LogUtil.d(TAG, "audioSessionId = " + audioSessionId);
            if (player == null) {
                return;
            }

            findViewById(R.id.exo_pause).setAlpha(player.hasNext() ? 1f : 0.5f);
            findViewById(R.id.exo_prev).setAlpha(player.hasPrevious() ? 1f : 0.5f);

        }

        @Override
        public void onPlaybackStateChanged(int state) {

            if (state == Player.STATE_READY || state == Player.STATE_ENDED) {
                uiPlayOrPause(true);
            } else {
                uiPlayOrPause(false);
            }
            ProgressBar mPbBar = getLoading();
            if (mPbBar == null) {
                return;
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
            etcAnimState(playWhenReady);
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
            uiPlayOrPause(false);

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