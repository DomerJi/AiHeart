package com.thfw.robotheart.activitys.video;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.thfw.base.ContextApp;
import com.thfw.base.base.IPresenter;
import com.thfw.base.models.VideoModel;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.SharePreferenceUtil;
import com.thfw.base.utils.ToastUtil;
import com.thfw.base.utils.Util;
import com.thfw.robotheart.R;
import com.thfw.ui.base.BaseActivity;
import com.thfw.ui.widget.BrightnessHelper;
import com.thfw.ui.widget.ShowChangeLayout;
import com.thfw.ui.widget.VideoGestureHelper;
import com.yhao.floatwindow.FloatWindow;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.UnknownHostException;

import static android.view.View.VISIBLE;

public class VideoPlayerActivity extends BaseActivity implements VideoGestureHelper.VideoGestureListener {


    private ImageView mIvBg;
    private PlayerView mMPlayerView;
    private ProgressBar mPbBottom;
    private ProgressBar mPbLoading;
    private TextView mTvTitle;
    private SimpleExoPlayer mExoPlayer;
    private VideoModel mVideoModel;
    private PlayerListener mPlayerListener;

    private static final String CURRENT_POSITION = "current_position";
    private static final String PLAY_STATE = "play_state";
    private long playPosition;
    private ConstraintLayout mVideoLayout;
    private boolean playState;
    private boolean isPlaying = false;
    private boolean windowPlay = false;
    private ConstraintLayout mVideoPlayConstranint;
    private FrameLayout mFlVideo;
    private Handler handler;
    private RelativeLayout mRlError;
    private TextView mTvErrorHint;
    private ImageView mExoPlay;
    private ImageView mIvScreenAll;
    private boolean screenFull;
    private ImageView mIvForward;
    private LinearLayout mLlTopControl;
    private ImageView mIvBack;
    private float speed;

    public static void startActivity(Context context, VideoModel videoModel) {
        context.startActivity(new Intent(context, VideoPlayerActivity.class).putExtra(KEY_DATA, videoModel));
    }

    @Override
    public int getContentView() {
        return R.layout.activity_video_play;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initView() {

        mIvBg = (ImageView) findViewById(R.id.iv_bg);
        mMPlayerView = (PlayerView) findViewById(R.id.mPlayerView);
        mPbBottom = (ProgressBar) findViewById(R.id.pb_bottom);
        mPbLoading = (ProgressBar) findViewById(R.id.pb_loading);
        mTvTitle = (TextView) findViewById(R.id.tv_video_top_title);
        mExoPlay = findViewById(R.id.exo_play);
        mVideoLayout = findViewById(R.id.video_play_constranint);

        mLlTopControl = findViewById(R.id.ll_top_control);
        mIvBack = findViewById(R.id.iv_back);
        mIvBack.setOnClickListener(v -> {
            if (screenFull) {
                screenFull();
            } else {
                finish();
            }
        });

        mVideoPlayConstranint = (ConstraintLayout) findViewById(R.id.video_play_constranint);
        mFlVideo = (FrameLayout) findViewById(R.id.fl_video);

        mRlError = (RelativeLayout) findViewById(R.id.rl_error);
        mTvErrorHint = (TextView) findViewById(R.id.tv_error_hint);
        mIvScreenAll = (ImageView) findViewById(R.id.iv_screen_all);
        // 初始化手势
        initGesture();

        mIvScreenAll.setOnClickListener(v -> {
            screenFull();
        });
    }

    private void screenFull() {
        // 视频【横屏】设置
        if (screenFull) {
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) mMPlayerView.getLayoutParams();
            layoutParams.dimensionRatio = "H,16:9";
            layoutParams.height = 0;
            layoutParams.width = Util.dipToPx(700, ContextApp.get());
            mVideoLayout.setLayoutParams(layoutParams);
            screenFull = false;
        } else {
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) mMPlayerView.getLayoutParams();
            layoutParams.height = ConstraintLayout.LayoutParams.MATCH_PARENT;
            layoutParams.width = ConstraintLayout.LayoutParams.MATCH_PARENT;
            layoutParams.dimensionRatio = null;
            mVideoLayout.setLayoutParams(layoutParams);
            screenFull = true;
        }
    }


    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle
                                    savedInstanceState) {
        super.onCreate(savedInstanceState);

        mVideoModel = (VideoModel) getIntent().getSerializableExtra(KEY_DATA);
        if (mVideoModel == null) {
            ToastUtil.show("视频参数为空");
            finish();
            return;
        }
        mTvTitle.setText(mVideoModel.title);
        mExoPlayer = new SimpleExoPlayer.Builder(this).build();

        mPbLoading.setVisibility(VISIBLE);
        if (mPlayerListener != null) {
            mExoPlayer.removeListener(mPlayerListener);
        }
        mPlayerListener = new PlayerListener();
        mPlayerListener.setPbBar(mPbLoading);
        mExoPlayer.addListener(mPlayerListener);
        mMPlayerView.setPlayer(mExoPlayer);
        MediaItem mediaItem = MediaItem.fromUri(mVideoModel.url);
//            MediaSource mediaSource = new DefaultMediaSourceFactory(mContext).createMediaSource(mediaItem);
        mExoPlayer.setMediaItem(mediaItem);

        LogUtil.d("savedInstanceState = " + (savedInstanceState == null));
        if (savedInstanceState != null) {
            playPosition = savedInstanceState.getLong(CURRENT_POSITION, 0);
            mExoPlayer.seekTo(playPosition);
            mExoPlayer.prepare();
            LogUtil.d(TAG, "playPosition = " + playPosition);
            playState = savedInstanceState.getBoolean(PLAY_STATE, playState);
            if (playState) {
                mExoPlayer.play();
            } else {
                mExoPlayer.setPlayWhenReady(playState);
            }
        } else {
            playPosition = SharePreferenceUtil.getLong(CURRENT_POSITION, 0);
            mExoPlayer.seekTo(playPosition);
            mExoPlayer.prepare();
            mExoPlayer.setPlayWhenReady(true);
        }
        mMPlayerView.setControllerVisibilityListener(new PlayerControlView.VisibilityListener() {
            @Override
            public void onVisibilityChange(int visibility) {
                mLlTopControl.setVisibility(visibility);
                onBottomProgressBar(mPbBottom, visibility == VISIBLE);
            }
        });

        initView();
    }


    @Override
    public int getStatusBarColor() {
        return STATUSBAR_TRANSPARENT;
    }

    @Override
    public void initData() {

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
        public void onPlayerError(ExoPlaybackException error) {
            ProgressBar mPbBar = getLoading();
            if (mPbBar == null) {
                return;
            }
            mPbBar.setTag("error");
            mPbBar.setVisibility(View.GONE);
            String errorHint = null;
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
                        errorHint = "视频资源错误";
                    } else {
                        // Try calling httpError.getCause() to retrieve the underlying cause,
                        // although note that it may be null.

                        if (httpError.getCause() instanceof UnknownHostException) {
                            errorHint = "网络异常，请检查网络";
                        } else {
                            errorHint = "未知错误" + httpError.getCause();
                        }
                    }
                }
            } else if (error.type == ExoPlaybackException.TYPE_RENDERER) {
                errorHint = "渲染错误 - UNEXPECTED";
            } else if (error.type == ExoPlaybackException.TYPE_UNEXPECTED) {
                errorHint = "未知错误 - UNEXPECTED";
            } else if (error.type == ExoPlaybackException.TYPE_REMOTE) {
                errorHint = "网络错误 - REMOTE";
            }
            showError(errorHint);
        }
    }

    private void showError(String error) {
        if (TextUtils.isEmpty(error)) {
            mRlError.setVisibility(View.GONE);
            mTvErrorHint.setText("");
        } else {
            ToastUtil.show(error);
            mRlError.setVisibility(VISIBLE);
            mRlError.setOnClickListener(v -> {
                mRlError.setVisibility(View.GONE);
                mExoPlay.performClick();
            });
            mTvErrorHint.setText(error);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mExoPlayer != null) {
            LogUtil.d(TAG, "put playPosition = " + mExoPlayer.getCurrentPosition());
            outState.putLong(CURRENT_POSITION, mExoPlayer.getCurrentPosition());
            outState.putBoolean(PLAY_STATE, mExoPlayer.getPlayWhenReady());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (windowPlay) {
            return;
        }

        if (mExoPlayer != null) {
            isPlaying = mExoPlayer.isPlaying();
            mExoPlayer.setPlayWhenReady(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (windowPlay) {
            if (FloatWindow.get() == null) {
                windowPlay = false;
            }
            return;
        }
        if (mMPlayerView != null) {
            mMPlayerView.setUseController(true);
            mMPlayerView.onResume();
        }
        if (mExoPlayer != null) {
            if (isPlaying) {
                mExoPlayer.setPlayWhenReady(true);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (windowPlay) {
            return;
        }
        if (mExoPlayer != null) {
            mExoPlayer.release();
            mExoPlayer = null;
        }
        SharePreferenceUtil.setLong(CURRENT_POSITION, 0);
    }

    private void onBottomProgressBar(ProgressBar mPbBar, boolean hide) {
        mPbBar.setVisibility(hide ? View.GONE : VISIBLE);
        if (!hide) {
            ExoPlayer player = mExoPlayer;
            if (handler != null) {
                handler.removeCallbacksAndMessages(null);
                handler = null;
            }
            handler = new Handler(Looper.myLooper()) {

                @Override
                public void handleMessage(@NonNull Message msg) {
                    super.handleMessage(msg);
                    int progress = (int) (player.getContentPosition() * 1000 / player.getContentDuration());
                    int secondaryProgress = (int) (player.getContentBufferedPosition() * 1000 / player.getContentDuration());
                    mPbBar.setProgress(progress);
                    mPbBar.setSecondaryProgress(secondaryProgress);
                    handler.sendEmptyMessageDelayed(0, 1000);
                }

            };

            handler.sendEmptyMessageDelayed(0, 0);


        } else {
            if (handler != null) {
                handler.removeCallbacksAndMessages(null);
                handler = null;
            }
        }
    }

    @Override
    public void finish() {
        overridePendingTransition(0, 0);
        super.finish();
    }

    //=============== 开始 视频手势 =======================//

    private VideoGestureHelper ly_VG;
    private ShowChangeLayout scl;
    private AudioManager mAudioManager;
    private int maxVolume = 0;
    private int oldVolume = 0;
    private int newProgress = 0, oldProgress = 0;
    private BrightnessHelper mBrightnessHelper;
    private float brightness = 1;
    private Window mWindow;
    private WindowManager.LayoutParams mLayoutParams;

    private void initGesture() {
        ly_VG = new VideoGestureHelper(mContext, mMPlayerView);
        ly_VG.setVideoGestureListener(this);

        scl = (ShowChangeLayout) findViewById(R.id.scl);

        //初始化获取音量属性
        mAudioManager = (AudioManager) getSystemService(Service.AUDIO_SERVICE);
        maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        //初始化亮度调节
        mBrightnessHelper = new BrightnessHelper(this);

        //下面这是设置当前APP亮度的方法配置
        mWindow = getWindow();
        mLayoutParams = mWindow.getAttributes();
        brightness = mLayoutParams.screenBrightness;

    }


    @Override
    public void onDown(MotionEvent e) {
        //每次按下的时候更新当前亮度和音量，还有进度
        oldProgress = newProgress;
        oldVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        brightness = mLayoutParams.screenBrightness;
        if (brightness == -1) {
            //一开始是默认亮度的时候，获取系统亮度，计算比例值
            brightness = mBrightnessHelper.getBrightness() / 255f;
        }
    }

    @Override
    public void onEndFF_REW(MotionEvent e) {
        setSpeed(1);
        ToastUtil.show("设置进度为" + newProgress);
    }

    @Override
    public void onVolumeGesture(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        setSpeed(1);
        Log.d(TAG, "onVolumeGesture: oldVolume " + oldVolume);
        int value = ly_VG.getHeight() / maxVolume;
        int newVolume = (int) ((e1.getY() - e2.getY()) / value + oldVolume);

        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newVolume, AudioManager.FLAG_PLAY_SOUND);


        Log.d(TAG, "onVolumeGesture: value" + value);

        Log.d(TAG, "onVolumeGesture: newVolume " + newVolume);

        //要强行转Float类型才能算出小数点，不然结果一直为0
        int volumeProgress = (int) (newVolume / Float.valueOf(maxVolume) * 100);
        if (volumeProgress >= 50) {
            scl.setImageResource(R.drawable.ic_volume_higher_w);
        } else if (volumeProgress > 0) {
            scl.setImageResource(R.drawable.ic_volume_lower_w);
        } else {
            scl.setImageResource(R.drawable.ic_volume_off_w);
        }
        scl.setProgress(volumeProgress);
        scl.show();
    }

    @Override
    public void onBrightnessGesture(MotionEvent e1, MotionEvent e2, float distanceX,
                                    float distanceY) {
        setSpeed(1);
        //下面这是设置当前APP亮度的方法
        Log.d(TAG, "onBrightnessGesture: old" + brightness);
        float newBrightness = (e1.getY() - e2.getY()) / ly_VG.getHeight();
        newBrightness += brightness;

        Log.d(TAG, "onBrightnessGesture: new" + newBrightness);
        if (newBrightness < 0) {
            newBrightness = 0;
        } else if (newBrightness > 1) {
            newBrightness = 1;
        }
        mLayoutParams.screenBrightness = newBrightness;
        mWindow.setAttributes(mLayoutParams);
        scl.setProgress((int) (newBrightness * 100));
        scl.setImageResource(R.drawable.ic_brightness_w);
        scl.show();
    }

    @Override
    public void onFF_REWGesture(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        setSpeed(1);
        float offset = e2.getX() - e1.getX();
        Log.d(TAG, "onFF_REWGesture: offset " + offset);
        Log.d(TAG, "onFF_REWGesture: ly_VG.getWidth()" + ly_VG.getWidth());
        //根据移动的正负决定快进还是快退
        if (offset > 0) {
            scl.setImageResource(R.drawable.ic_video_ff);
            newProgress = (int) (oldProgress + offset / ly_VG.getWidth() * 100);
            if (newProgress > 100) {
                newProgress = 100;
            }
        } else {
            scl.setImageResource(R.drawable.ic_video_fr);
            newProgress = (int) (oldProgress + offset / ly_VG.getWidth() * 100);
            if (newProgress < 0) {
                newProgress = 0;
            }
        }
        scl.setProgress(newProgress);
        scl.show();
    }

    @Override
    public void onSingleTapGesture(MotionEvent e) {
        Log.d(TAG, "onSingleTapGesture: ");
        setSpeed(1);
        if (mMPlayerView != null) {
            if (mMPlayerView.isControllerVisible()) {
                mMPlayerView.hideController();
            } else {
                mMPlayerView.showController();
            }
        }
    }

    @Override
    public void onDoubleTapGesture(MotionEvent e) {
        Log.d(TAG, "onDoubleTapGesture: ");
        screenFull();
        setSpeed(1);
    }

    @Override
    public void onLongPress(MotionEvent e) {
        setSpeed(3);
        if (scl != null) {
            scl.setOnVisibilityListener(new ShowChangeLayout.OnVisibilityListener() {
                @Override
                public void onVisibility(int visibility) {
                    if (visibility != VISIBLE) {
                        setSpeed(1);
                    }
                }
            });
        }
    }

    private Handler mHandler = new Handler(Looper.getMainLooper());
    private Runnable speechRunnable = new Runnable() {
        @Override
        public void run() {
            if (mIvForward == null) {
                mIvForward = findViewById(R.id.iv_show_forward);
            }
            mIvForward.setVisibility(speed > 1 ? VISIBLE : View.GONE);
            if (mExoPlayer != null) {
                PlaybackParameters playbackParameters = new PlaybackParameters(speed, 1.0F);
                mExoPlayer.setPlaybackParameters(playbackParameters);
            }
        }
    };

    private void setSpeed(float s) {
        speed = s;
        if (speed > 1f) {
            mHandler.removeCallbacks(speechRunnable);
            mHandler.postDelayed(speechRunnable, 300);
        } else {
            mHandler.removeCallbacks(speechRunnable);
            mHandler.post(speechRunnable);
        }
    }


    //=============== 结束 视频手势 =======================//

}