package com.thfw.mobileheart.activity.video;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ExoPlaybackException;
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
import com.thfw.robotheart.R;
import com.thfw.mobileheart.adapter.HomeAdapter;
import com.thfw.mobileheart.model.HomeEntity;
import com.thfw.mobileheart.model.VideoModel;
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
public class VideoPlayActivity extends BaseActivity {

    private FrameLayout mFlVideo;
    private ImageView mIvBg;
    private PlayerView mMPlayerView;
    private ProgressBar mPbBottom;
    private Button mBtnPlay;
    private ProgressBar mPbLoading;
    private TextView mTvTitle;
    private TextView mTvScreenAll;
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

        mFlVideo = (FrameLayout) findViewById(R.id.fl_video);
        mIvBg = (ImageView) findViewById(R.id.iv_bg);
        mMPlayerView = (PlayerView) findViewById(R.id.mPlayerView);
        mPbBottom = (ProgressBar) findViewById(R.id.pb_bottom);
        mBtnPlay = (Button) findViewById(R.id.btn_play);
        mPbLoading = (ProgressBar) findViewById(R.id.pb_loading);
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mTvScreenAll = (TextView) findViewById(R.id.tv_screen_all);
        mVideoLayout = findViewById(R.id.video_play_constranint);
        // test todo
        mRvVideoDetail = findViewById(R.id.rv_video_detail);
        mRvVideoDetail.setLayoutManager(new LinearLayoutManager(mContext));
        List<HomeEntity> list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            list.add(new HomeEntity());
        }
        HomeAdapter mHomeAdapter = new HomeAdapter(list);
        mRvVideoDetail.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        // 添加动画
        mRvVideoDetail.setItemAnimator(new DefaultItemAnimator());
        mRvVideoDetail.setAdapter(mHomeAdapter);
        /**
         * 全屏按钮
         */
        mTvScreenAll.setOnClickListener(v -> {
            if (EmptyUtil.isEmpty(VideoPlayActivity.this)) {
                ToastUtil.show("VideoPlay null");
                if (FloatWindow.get() != null) {
                    FloatWindow.destroy();
                    if (mExoPlayer != null) {
                        SharePreferenceUtil.setLong(CURRENT_POSITION, mExoPlayer.getCurrentPosition());
                        mExoPlayer.release();
                        mExoPlayer = null;
                    }
                    VideoPlayActivity.startActivity(mContext, (VideoModel) mTvScreenAll.getTag());
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

    }

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
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mVideoModel = (VideoModel) getIntent().getSerializableExtra(KEY_DATA);
        if (mVideoModel == null) {
            ToastUtil.show("视频参数为空");
            finish();
            return;
        }
        mTvScreenAll.setTag(mVideoModel);
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
            mMPlayerView.setLayoutParams(layoutParams);
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

    @Override
    public void finish() {
        overridePendingTransition(0, 0);
        super.finish();
    }
}