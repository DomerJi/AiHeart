package com.thfw.mobileheart.activity.video;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
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
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.thfw.base.base.IPresenter;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.SharePreferenceUtil;
import com.thfw.base.utils.ToastUtil;
import com.thfw.base.utils.Util;
import com.thfw.mobileheart.MyApplication;
import com.thfw.mobileheart.adapter.VideoPlayListAdapter;
import com.thfw.mobileheart.model.VideoModel;
import com.thfw.mobileheart.model.VideoPlayListModel;
import com.thfw.mobileheart.util.ExoPlayerFactory;
import com.thfw.mobileheart.R;
import com.thfw.ui.base.BaseActivity;
import com.yhao.floatwindow.FloatWindow;
import com.yhao.floatwindow.PermissionListener;
import com.yhao.floatwindow.Screen;
import com.yhao.floatwindow.ViewStateListener;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import static android.view.View.VISIBLE;

/**
 * 悬浮窗播放，全屏播放，竖屏播放
 */
public class VideoPlayActivity extends BaseActivity implements VideoGestureHelper.VideoGestureListener {


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
    private RecyclerView mRvVideoDetail;
    private boolean isPlaying = false;
    private boolean windowPlay = false;
    private TextView mTvScreentSmall;
    private LinearLayout mRootLinearLayout;
    private ConstraintLayout mVideoPlayConstranint;
    private FrameLayout mFlVideo;
    private LinearLayout mLlTopControl;
    private ImageView mIvBack;
    private ImageView mIvMore;
    private TextView mTvScreenSmall;
    private ImageView mIvScreenAll;
    private boolean landscape;
    private Handler handler;
    private RelativeLayout mRlError;
    private TextView mTvErrorHint;
    private ImageView mExoPlay;

    public static void startActivity(Context context, VideoModel videoModel) {
        context.startActivity(new Intent(context, VideoPlayActivity.class).putExtra(KEY_DATA, videoModel));
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
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mExoPlay = findViewById(R.id.exo_play);
        mVideoLayout = findViewById(R.id.video_play_constranint);
        // test todo
        mRvVideoDetail = findViewById(R.id.rv_video_detail);
        mRvVideoDetail.setLayoutManager(new LinearLayoutManager(mContext));
        List<VideoPlayListModel> list = new ArrayList<>();
        list.add(new VideoPlayListModel(VideoPlayListModel.TYPE_TOP));
        list.add(new VideoPlayListModel(VideoPlayListModel.TYPE_GROUP).setHeadName("选集"));
        list.add(new VideoPlayListModel(VideoPlayListModel.TYPE_ECT));
        list.add(new VideoPlayListModel(VideoPlayListModel.TYPE_GROUP).setHeadName("猜你喜欢"));

        for (int i = 0; i < 20; i++) {
            list.add(new VideoPlayListModel(VideoPlayListModel.TYPE_BODY));
        }
        VideoPlayListAdapter videoPlayListAdapter = new VideoPlayListAdapter(list);
        mRvVideoDetail.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        // 添加动画
        mRvVideoDetail.setItemAnimator(new DefaultItemAnimator());
        mRvVideoDetail.setAdapter(videoPlayListAdapter);
        mIvScreenAll = findViewById(R.id.iv_screen_all);
        /**
         * 全屏按钮
         */
        mIvScreenAll.setOnClickListener(v -> {
            if (EmptyUtil.isEmpty(VideoPlayActivity.this)) {
                ToastUtil.show("VideoPlay null");
                if (FloatWindow.get() != null) {
                    FloatWindow.destroy();
                    if (mExoPlayer != null) {
                        SharePreferenceUtil.setLong(CURRENT_POSITION, mExoPlayer.getCurrentPosition());
                        mExoPlayer.release();
                        mExoPlayer = null;
                    }
                    VideoPlayActivity.startActivity(mContext, (VideoModel) mIvScreenAll.getTag());
                    return;
                }
            } else {
                // todo 恢复界面。问题 有声音无画面。
                ToastUtil.show("VideoPlay not null");
                if (FloatWindow.get() != null) {
                    if (FloatWindow.get().getView().getParent() != null) {
                        WindowManager mWindowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
                        mWindowManager.removeView(FloatWindow.get().getView());
                    }
                    if (mRootLinearLayout != null) {
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        mRootLinearLayout.addView(mVideoLayout, 0);
                        mVideoLayout.setBackgroundColor(Color.RED);
                        mVideoLayout.setLayoutParams(layoutParams);
                    }

                    windowPlay = false;
                    Intent intent = new Intent(mContext.getApplicationContext(), VideoPlayActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    getApplication().startActivity(intent);// getApplication()不可以去掉否则没用
                    FloatWindow.destroy();
                    return;
                }
            }
            /**
             * 设置为横屏
             */
            if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
            }
        });
        mTvScreentSmall = findViewById(R.id.tv_screen_small);

        mTvScreentSmall.setOnClickListener(v -> {
            showVideoWindow();
        });

        mVideoPlayConstranint = (ConstraintLayout) findViewById(R.id.video_play_constranint);
        mFlVideo = (FrameLayout) findViewById(R.id.fl_video);
        mLlTopControl = (LinearLayout) findViewById(R.id.ll_top_control);
        mIvBack = (ImageView) findViewById(R.id.iv_back);
        mIvMore = (ImageView) findViewById(R.id.iv_more);
        mTvScreenSmall = (TextView) findViewById(R.id.tv_screen_small);
        mIvBack.setOnClickListener(v -> {
            // 如果是横屏，先退出横屏模式
            if (landscape) {
                mIvScreenAll.performClick();
            } else {
                finish();
            }
        });
        mIvMore.setOnClickListener(v -> {
            ToastUtil.show("更多");
        });
        mRlError = (RelativeLayout) findViewById(R.id.rl_error);
        mTvErrorHint = (TextView) findViewById(R.id.tv_error_hint);
        // 初始化手势
        initGesture();
    }

    /**
     * 开启悬浮窗
     */
    private void showVideoWindow() {
        LogUtil.d(TAG, "showFlow()");
        if (FloatWindow.get() != null && FloatWindow.get().isShowing()) {
            return;
        }

        if (mVideoLayout.getParent() != null) {
            mRootLinearLayout = (LinearLayout) mVideoLayout.getParent();
            mRootLinearLayout.removeView(mVideoLayout);
            mMPlayerView.setUseController(false);
            mTvScreentSmall.setVisibility(View.GONE);
        }
        FloatWindow
                .with(MyApplication.getApp())
                .setView(mVideoLayout)
                .setWidth(Util.dipToPx(160, mContext))                               //设置控件宽高
                .setHeight(Util.dipToPx(90, mContext))
                .setX(100)                                   //设置控件初始位置
                .setY(Screen.height, 0.3f)
                .setDesktopShow(true)                        //桌面显示
                .setViewStateListener(new ViewStateListener() {
                    @Override
                    public void onPositionUpdate(int x, int y) {

                    }

                    @Override
                    public void onShow() {

                    }

                    @Override
                    public void onHide() {

                    }

                    @Override
                    public void onDismiss() {

                    }

                    @Override
                    public void onMoveAnimStart() {

                    }

                    @Override
                    public void onMoveAnimEnd() {

                    }

                    @Override
                    public void onBackToDesktop() {

                    }
                })    //监听悬浮控件状态改变
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onFail() {

                    }
                })  //监听权限申请结果
                .build();
        //手动控制
        FloatWindow.get().show();
        windowPlay = true;
        finish();
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
        mIvScreenAll.setTag(mVideoModel);
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

        initView();
    }

// 隐藏 activity singleInstance 有效
//    @Override
//    public void onBackPressed() {
//        if (windowPlay) {
//            moveTaskToBack(true);
//        } else {
//            super.onBackPressed();
//        }
//    }
//
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            moveTaskToBack(true);
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }

    @Override
    public void onConfigurationChanged(@NonNull @NotNull Configuration newConfig) {
        int angle = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay().getRotation();
        LogUtil.d(TAG, "angle = " + angle);
        mMPlayerView.dispatchConfigurationChanged(newConfig);
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.i("LANDSCAPE = ", String.valueOf(Configuration.ORIENTATION_LANDSCAPE));
            //获得 WindowManager.LayoutParams 属性对象
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            //直接对它flags变量操作   LayoutParams.FLAG_FULLSCREEN 表示设置全屏
            lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            //设置属性
            getWindow().setAttributes(lp);
            //意思大致就是  允许窗口扩展到屏幕之外
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            // 视频【横屏】设置
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) mMPlayerView.getLayoutParams();
            layoutParams.height = ConstraintLayout.LayoutParams.MATCH_PARENT;
            layoutParams.width = ConstraintLayout.LayoutParams.MATCH_PARENT;
            layoutParams.dimensionRatio = null;
            mMPlayerView.setLayoutParams(layoutParams);
            LinearLayout.LayoutParams layoutParams1 = (LinearLayout.LayoutParams) mVideoLayout.getLayoutParams();
            layoutParams1.height = ConstraintLayout.LayoutParams.MATCH_PARENT;
            layoutParams1.width = ConstraintLayout.LayoutParams.MATCH_PARENT;
            mVideoLayout.setLayoutParams(layoutParams1);
            mTvTitle.setVisibility(VISIBLE);
            landscape = true;
        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            Log.i("LANDSCAPE = ", String.valueOf(Configuration.ORIENTATION_PORTRAIT));
            //获得 WindowManager.LayoutParams 属性对象
            WindowManager.LayoutParams lp2 = getWindow().getAttributes();
            //LayoutParams.FLAG_FULLSCREEN 强制屏幕状态条栏弹出
            lp2.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            //设置属性
            getWindow().setAttributes(lp2);
            //不允许窗口扩展到屏幕之外  clear掉了
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            // 视频【竖屏】设置
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) mMPlayerView.getLayoutParams();
            layoutParams.dimensionRatio = "H,16:9";
            layoutParams.height = 0;
            layoutParams.width = ConstraintLayout.LayoutParams.MATCH_PARENT;
            mMPlayerView.setLayoutParams(layoutParams);

            LinearLayout.LayoutParams layoutParams1 = (LinearLayout.LayoutParams) mVideoLayout.getLayoutParams();
            layoutParams1.height = ConstraintLayout.LayoutParams.WRAP_CONTENT;
            layoutParams1.width = ConstraintLayout.LayoutParams.MATCH_PARENT;
            mVideoLayout.setLayoutParams(layoutParams1);
            mTvTitle.setVisibility(View.GONE);
            landscape = false;
        } else {
            landscape = false;
        }
        super.onConfigurationChanged(newConfig);

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
            ExoPlayer player = ExoPlayerFactory.getExoPlayer();
            if (handler != null) {
                handler.removeCallbacksAndMessages(null);
                handler = null;
            }
            handler = new Handler(Looper.myLooper()) {

                @Override
                public void handleMessage(@NonNull Message msg) {
                    super.handleMessage(msg);
                    int progress = (int) (player.getContentPosition() * 1000 / player.getContentDuration());
                    mPbBar.setProgress(progress);
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
        ToastUtil.show("设置进度为" + newProgress);
    }

    @Override
    public void onVolumeGesture(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

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
        ToastUtil.show("SingleTap");
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
        if (!landscape && mIvScreenAll != null) {
            mIvScreenAll.performClick();
        }
    }

    //=============== 结束 视频手势 =======================//

}