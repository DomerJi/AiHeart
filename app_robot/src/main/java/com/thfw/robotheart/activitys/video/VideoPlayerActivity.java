package com.thfw.robotheart.activitys.video;

import android.animation.Animator;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.thfw.base.ContextApp;
import com.thfw.base.api.HistoryApi;
import com.thfw.base.face.MyAnimationListener;
import com.thfw.base.face.OnRvItemListener;
import com.thfw.base.models.ChatEntity;
import com.thfw.base.models.CommonModel;
import com.thfw.base.models.VideoEtcModel;
import com.thfw.base.models.VideoModel;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.HistoryPresenter;
import com.thfw.base.presenter.VideoPresenter;
import com.thfw.base.timing.TimingHelper;
import com.thfw.base.timing.WorkInt;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.NetworkUtil;
import com.thfw.base.utils.SharePreferenceUtil;
import com.thfw.base.utils.ToastUtil;
import com.thfw.base.utils.Util;
import com.thfw.robotheart.R;
import com.thfw.robotheart.adapter.VideoItemAdapter;
import com.thfw.robotheart.view.TitleBarView;
import com.thfw.ui.base.RobotBaseActivity;
import com.thfw.ui.widget.BrightnessHelper;
import com.thfw.ui.widget.LoadingView;
import com.thfw.ui.widget.ShowChangeLayout;
import com.thfw.ui.widget.VideoGestureHelper;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.yhao.floatwindow.FloatWindow;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import static android.view.View.VISIBLE;

public class VideoPlayerActivity extends RobotBaseActivity<VideoPresenter>
        implements TimingHelper.WorkListener, VideoGestureHelper.VideoGestureListener, VideoPresenter.VideoUi<VideoModel> {


    private ImageView mIvBg;
    private PlayerView mMPlayerView;
    private ProgressBar mPbBottom;
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
    private Handler handler;
    private ImageView mExoPlay;
    private boolean screenFull;
    private View mLLShowForWard;
    private LinearLayout mLlTopControl;
    private ImageView mIvBack;
    private float speed;
    private static List<VideoEtcModel> mStaticVideoList;
    private List<VideoEtcModel> mVideoList;
    public static final String KEY_PLAY_POSITION = "key.position";
    public static final String KEY_PLAY_ROOTTYPE = "key.roottype";
    private int mPlayPosition;
    private ShowChangeLayout mScl;
    private boolean flDurationEnd;
    private LoadingView mLoadingView;
    private ImageView mIvShowForward;
    private TextView mTvVideoTopTitle;
    private TitleBarView mTitleBarView;
    private TextView mTvVideoContent;
    private ConstraintLayout mClContent;
    private TextView mTvEtcTitleLogcate;
    private RecyclerView mRvList;
    private LinearLayout mLlCollect;
    private LinearLayout mLlHint;
    private LinearLayout mLlLogcate;
    private ImageView mExoNext;
    private boolean requestIng;

    private ImageView mIvCollect;
    private ConstraintLayout mClHint;
    private int rootType;

    public static void startActivity(Context context, VideoModel videoModel) {
        context.startActivity(new Intent(context, VideoPlayerActivity.class).putExtra(KEY_DATA, videoModel));
    }

    public static void startActivity(Context context, List<VideoEtcModel> list, int playPosition) {
        mStaticVideoList = list;
        ((Activity) context).startActivityForResult(new Intent(context, VideoPlayerActivity.class)
                .putExtra(KEY_PLAY_POSITION, playPosition), ChatEntity.TYPE_RECOMMEND_VIDEO);
    }

    @Override
    public int getContentView() {
        return R.layout.activity_video_play;
    }

    @Override
    public VideoPresenter onCreatePresenter() {
        return new VideoPresenter(this);
    }

    @Override
    public void initView() {

        rootType = getIntent().getIntExtra(KEY_PLAY_ROOTTYPE, 1);
        mIvBg = (ImageView) findViewById(R.id.iv_bg);
        mMPlayerView = (PlayerView) findViewById(R.id.mPlayerView);
        mPbBottom = (ProgressBar) findViewById(R.id.pb_bottom);
        mTvTitle = (TextView) findViewById(R.id.tv_video_top_title);
        mExoPlay = findViewById(R.id.exo_play);
        mVideoLayout = findViewById(R.id.video_play_constranint);

        mLlTopControl = findViewById(R.id.ll_top_control);
        mIvBack = findViewById(R.id.iv_back);
        mIvBack.setOnClickListener(v -> {
            finish();
        });

        mVideoPlayConstranint = (ConstraintLayout) findViewById(R.id.video_play_constranint);

        mScl = (ShowChangeLayout) findViewById(R.id.scl);
        mLoadingView = (LoadingView) findViewById(R.id.loadingView);
        mLoadingView.showLoadingNoText();
        mTvVideoTopTitle = (TextView) findViewById(R.id.tv_video_top_title);
        mTitleBarView = (TitleBarView) findViewById(R.id.titleBarView);
        mTvVideoContent = (TextView) findViewById(R.id.tv_video_content);
        mClContent = (ConstraintLayout) findViewById(R.id.cl_content);
        mTvEtcTitleLogcate = (TextView) findViewById(R.id.tv_etc_title_logcate);
        mRvList = (RecyclerView) findViewById(R.id.rv_list);
        mRvList.setLayoutManager(new LinearLayoutManager(mContext));
        mLlCollect = (LinearLayout) findViewById(R.id.ll_collect);
        mLlHint = (LinearLayout) findViewById(R.id.ll_hint);
        mLlLogcate = (LinearLayout) findViewById(R.id.ll_logcate);
        mExoNext = (ImageView) findViewById(R.id.ic_exo_next);
        mIvCollect = (ImageView) findViewById(R.id.iv_collect);
        mClHint = (ConstraintLayout) findViewById(R.id.cl_hint);
        // 防止误触关闭控制栏
        findViewById(R.id.ll_video_control_root).setOnClickListener(v -> {
        });

        mLlLogcate.setOnClickListener(v -> {
            videoLogcatue();
        });
        mClContent.setOnClickListener(v -> {
            videoLogcatue();
        });
        mLlCollect.setOnClickListener(v -> {
            addCollect();
        });
        mLlHint.setOnClickListener(v -> {
            mClHint.setVisibility(VISIBLE);
        });
        mClHint.setOnClickListener(v -> {
            mClHint.setVisibility(View.GONE);
        });
        mExoNext.setOnClickListener(v -> {
            videoChanged(mPlayPosition + 1);
        });
        mMPlayerView.setControllerVisibilityListener(new PlayerControlView.VisibilityListener() {
            @Override
            public void onVisibilityChange(int visibility) {
                mLlTopControl.setVisibility(visibility);
                onBottomProgressBar(mPbBottom, visibility == VISIBLE);
            }
        });
        // 初始化手势
        initGesture();

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

    private void initExoPlayer() {
        long positionMs = -1;
        // 初始化ExoPlayer
        if (mExoPlayer == null) {
            mExoPlayer = new SimpleExoPlayer.Builder(this).build();
            if (mPlayerListener != null) {
                mExoPlayer.removeListener(mPlayerListener);
            }
            mPlayerListener = new PlayerListener();
            mExoPlayer.addListener(mPlayerListener);
            mMPlayerView.setPlayer(mExoPlayer);
        }
        mTvVideoContent.setText("简介：" + mVideoModel.getDes());
        mIvCollect.setSelected(mVideoModel.getCollected() == 1);
        mExoPlayer.setMediaSource(buildMediaSource(Uri.parse(mVideoModel.getUrl())));
        mExoPlayer.prepare();
        positionMs = VideoHistoryHelper.getPosition(mVideoModel.getId());
        if (positionMs <= 0) {
            positionMs = mVideoModel.getHistoryTime();
        }
        if (positionMs > 0) {
            continuePlay(positionMs);
        }
        mExoPlayer.setPlayWhenReady(true);
    }

    private void continuePlay(long positionMs) {
        mExoPlayer.seekTo(positionMs);
        TextView mTvContinuePlay = findViewById(R.id.tv_continue_play);
        mTvContinuePlay.setVisibility(VISIBLE);
        mTvContinuePlay.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (EmptyUtil.isEmpty(VideoPlayerActivity.this)) {
                    return;
                }
                mTvContinuePlay.setVisibility(View.GONE);
            }
        }, 3500);
    }

    private MediaSource buildMediaSource(Uri uri) {
        DefaultBandwidthMeter mDefaultBandwidthMeter = new DefaultBandwidthMeter();
        // 重定向 301 302 http 2 https
        DefaultDataSourceFactory upstreamFactory = new DefaultDataSourceFactory(this, mDefaultBandwidthMeter, new DefaultHttpDataSourceFactory("exoplayer-codelab", null, 15000, 15000, true));

        return new ProgressiveMediaSource.Factory(upstreamFactory).createMediaSource(uri);
        // 已弃用，无相关类
//        return new ExtractorMediaSource.Factory(upstreamFactory). createMediaSource(uri); }
    }

    @Override
    public void initData() {
        if (mVideoList == null && mStaticVideoList != null) {
            mVideoList = new ArrayList<>();
            mVideoList.addAll(mStaticVideoList);
            mStaticVideoList = null;
            int mPlayPosition = getIntent().getIntExtra(KEY_PLAY_POSITION, 0);
            videoChanged(mPlayPosition);
        }
    }


    @Override
    public LifecycleProvider getLifecycleProvider() {
        return this;
    }

    /**
     * 目录显示隐藏
     */
    private void videoLogcatue() {
        if (mClContent.getVisibility() == VISIBLE) {

            mClContent.animate().alpha(0f).setListener(new MyAnimationListener() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mClContent.setVisibility(View.GONE);
                }
            }).start();
        } else {
            if (mRvList.getAdapter() == null) {
                mTvEtcTitleLogcate.setText("相关推荐");
                VideoItemAdapter videoItemAdapter = new VideoItemAdapter(mVideoList);
                videoItemAdapter.setCurrentIndex(mPlayPosition);
                videoItemAdapter.setOnRvItemListener(new OnRvItemListener<VideoEtcModel>() {
                    @Override
                    public void onItemClick(List<VideoEtcModel> list, int position) {
                        videoChanged(position);
                        videoLogcatue();
                    }
                });
                mRvList.setAdapter(videoItemAdapter);
            } else {
                if (mExoPlayer != null) {
                    ((VideoItemAdapter) mRvList.getAdapter()).setCurrentIndex(mPlayPosition);
                }
            }
            mClContent.setAlpha(0f);
            mClContent.setVisibility(VISIBLE);
            flDurationEnd = false;
            mClContent.animate().alpha(1f).setListener(new MyAnimationListener() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    flDurationEnd = true;
                }
            });
        }
    }

    /**
     * 切换视频
     *
     * @param playPosition
     */
    private void videoChanged(int playPosition) {
        this.mPlayPosition = playPosition;
        if (mExoPlayer != null) {
            addHistory();
            mMPlayerView.hideController();
            mMPlayerView.onPause();
            mExoPlayer.setPlayWhenReady(false);
        }

        if (mPlayPosition >= mVideoList.size() - 1) {
            mPlayPosition = mVideoList.size() - 1;
            mExoNext.setAlpha(0.4f);
            mExoNext.setEnabled(false);
        } else {
            mExoNext.setAlpha(1f);
            mExoNext.setEnabled(true);
        }

        if (mClContent.getVisibility() == VISIBLE && mRvList.getAdapter() != null) {
            if (mExoPlayer != null) {
                ((VideoItemAdapter) mRvList.getAdapter()).setCurrentIndex(mPlayPosition);
            }
        }
        mTvTitle.setText(mVideoList.get(mPlayPosition).getTitle());
        mPresenter.getVideoInfo(mVideoList.get(playPosition).getId());
        mLoadingView.showLoadingNoText();
    }

    @Override
    public void onSuccess(VideoModel data) {
        mVideoModel = data;
        mLoadingView.hide();
        initExoPlayer();
    }

    @Override
    public void onFail(ResponeThrowable throwable) {
        mLoadingView.showFail(v -> {
            videoChanged(mPlayPosition);
        });
    }

    /**
     * 视频事件监听
     */
    public class PlayerListener implements Player.Listener {

        @Override
        public void onPlaybackStateChanged(int state) {
            if (state == Player.STATE_READY || state == Player.STATE_ENDED) {
                mLoadingView.hide();
                // 视频播放完成
                if (state == Player.STATE_ENDED) {
                    if (mPlayPosition < mVideoList.size() - 1) {
                        videoChanged(mPlayPosition + 1);
                    }
                }
            } else {
                if (mExoPlayer.getPlayerError() == null) {
                    mLoadingView.showLoadingNoText();
                } else {
                    videoError();
                }
            }
        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {
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
            videoError();
        }
    }

    /**
     * 视频播放错误处理
     */
    private void videoError() {
        mLoadingView.showFail(v -> {
            if (NetworkUtil.isNetConnected(mContext)) {
                mExoPlay.performClick();
                TimingHelper.getInstance().removeWorkArriveListener(this);
            } else {
                videoError();
                ToastUtil.show("网络异常");
            }
        });
        TimingHelper.getInstance().addWorkArriveListener(this);
    }

    @Override
    public void onArrive() {
        LogUtil.d(TAG, "WorkInt.SECOND2++++++++++++++++++++++++++++");
        if (NetworkUtil.isNetConnected(mContext)) {
            if (!mExoPlayer.isPlaying()) {
                mExoPlay.performClick();
            }
            TimingHelper.getInstance().removeWorkArriveListener(this);
        }
    }

    @Override
    public WorkInt workInt() {
        return WorkInt.SECOND2;
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
        addHistory();
        super.onPause();

        if (windowPlay) {
            return;
        }

        if (mExoPlayer != null) {
            isPlaying = mExoPlayer.isPlaying();
            mExoPlayer.setPlayWhenReady(false);
        }
    }

    /**
     * 增加历史记录
     */
    private void addHistory() {
        LogUtil.d(TAG, "addHistory+++++++++++++++++++++++++++++++");
        if (mVideoModel == null || mExoPlayer == null) {
            return;
        }
        long currentPosition = mExoPlayer.getContentPosition();
        long duration = mExoPlayer.getDuration();
        if (duration - currentPosition < VideoHistoryHelper.MIN_TIME_MS) {
            currentPosition = 0;
        } else if (currentPosition < VideoHistoryHelper.MIN_TIME_MS) {
            currentPosition = 0;
        }
        VideoHistoryHelper.addHistory(mVideoModel, currentPosition, duration);
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

        TimingHelper.getInstance().removeWorkArriveListener(this);

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
        setResult(RESULT_OK);
        overridePendingTransition(0, 0);
        super.finish();
    }

    public void addCollect() {
        if (requestIng) {
            return;
        }
        requestIng = true;
        mIvCollect.setSelected(!mIvCollect.isSelected());
        new HistoryPresenter(new HistoryPresenter.HistoryUi<CommonModel>() {
            @Override
            public LifecycleProvider getLifecycleProvider() {
                return VideoPlayerActivity.this;
            }

            @Override
            public void onSuccess(CommonModel data) {
                requestIng = false;
                ToastUtil.show(mIvCollect.isSelected() ? "收藏成功" : "取消收藏成功");
            }

            @Override
            public void onFail(ResponeThrowable throwable) {
                requestIng = false;
                ToastUtil.show(mIvCollect.isSelected() ? "收藏失败" : "取消收藏失败");
                mIvCollect.setSelected(!mIvCollect.isSelected());
            }
        }).addCollect(HistoryApi.TYPE_COLLECT_VIDEO, mVideoModel.getId());
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
//        ToastUtil.show("设置进度为" + newProgress);
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
//        float offset = e2.getX() - e1.getX();
//        Log.d(TAG, "onFF_REWGesture: offset " + offset);
//        Log.d(TAG, "onFF_REWGesture: ly_VG.getWidth()" + ly_VG.getWidth());
//        //根据移动的正负决定快进还是快退
//        if (offset > 0) {
//            scl.setImageResource(R.drawable.ic_video_ff);
//            newProgress = (int) (oldProgress + offset / ly_VG.getWidth() * 100);
//            if (newProgress > 100) {
//                newProgress = 100;
//            }
//        } else {
//            scl.setImageResource(R.drawable.ic_video_fr);
//            newProgress = (int) (oldProgress + offset / ly_VG.getWidth() * 100);
//            if (newProgress < 0) {
//                newProgress = 0;
//            }
//        }
//        scl.setProgress(newProgress);
//        scl.show();
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
            if (mLLShowForWard == null) {
                mLLShowForWard = findViewById(R.id.ll_show_forward);
            }
            mLLShowForWard.setVisibility(speed > 1 ? VISIBLE : View.GONE);
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