package com.thfw.mobileheart.activity.video;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.thfw.base.face.OnRvItemListener;
import com.thfw.base.models.ChatEntity;
import com.thfw.base.models.VideoModel;
import com.thfw.base.models.VideoPlayListModel;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.VideoPresenter;
import com.thfw.base.timing.TimingHelper;
import com.thfw.base.timing.WorkInt;
import com.thfw.base.utils.DataChangeHelper;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.NetworkUtil;
import com.thfw.base.utils.ToastUtil;
import com.thfw.mobileheart.R;
import com.thfw.mobileheart.activity.BaseActivity;
import com.thfw.mobileheart.activity.talk.TalkItemJumpHelper;
import com.thfw.mobileheart.adapter.VideoPlayListAdapter;
import com.thfw.mobileheart.util.ExoPlayerFactory;
import com.thfw.ui.base.AVResource;
import com.thfw.ui.utils.BrightnessHelper;
import com.thfw.ui.utils.VideoGestureHelper;
import com.thfw.ui.widget.LoadingView;
import com.thfw.ui.widget.ShowChangeLayout;
import com.trello.rxlifecycle2.LifecycleProvider;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import static android.view.View.VISIBLE;

/**
 * 悬浮窗播放，全屏播放，竖屏播放
 */
public class VideoPlayActivity extends BaseActivity<VideoPresenter>
        implements TimingHelper.WorkListener, VideoGestureHelper.VideoGestureListener, VideoPresenter.VideoUi<VideoModel> {


    public static final String KEY_PLAY_POSITION = "key.position";
    public static final String KEY_AUTO_FINISH = "key.auto.finish";
    public static final String KEY_FROM_TYPE = "key.from.type";
    private PlayerView mMPlayerView;
    private ProgressBar mPbBottom;
    private TextView mTvTitle;
    private SimpleExoPlayer mExoPlayer;
    private VideoModel mVideoModel;
    private PlayerListener mPlayerListener;
    private ConstraintLayout mVideoLayout;
    private RecyclerView mRvVideoDetail;
    private boolean isPlaying = false;
    private ConstraintLayout mVideoPlayConstranint;
    private LinearLayout mLlTopControl;
    private ImageView mIvBack;
    private ImageView mIvScreenAll;
    private boolean landscape;
    private Handler handler;
    private ImageView mExoPlay;
    private ImageView mExoNext;
    private ArrayList<VideoModel.RecommendModel> mVideoList;
    private boolean autoFinished;
    private int fromType;
    private int mPlayPosition;
    private LoadingView mLoadingView;
    private VideoPlayListAdapter mVideoPlayListAdapter;
    private int mVideoId;
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
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private View mLLShowForWard;
    private float speed;
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

    public static void startActivity(Context context, VideoModel videoModel) {
        context.startActivity(new Intent(context, VideoPlayActivity.class).putExtra(KEY_DATA, videoModel));
    }

    public static void startActivity(Context context, int videoId, boolean autoFinished) {
        ((Activity) context).startActivityForResult(new Intent(context, VideoPlayActivity.class)
                .putExtra(KEY_DATA, videoId)
                .putExtra(KEY_AUTO_FINISH, autoFinished), ChatEntity.TYPE_RECOMMEND_VIDEO);
    }

    /**
     * @param context
     * @param videoId
     * @param autoFinished 自动关闭
     * @param fromType     来自哪里 如果是工具包禁止相关推荐
     */
    public static void startActivity(Context context, int videoId, boolean autoFinished, int fromType) {
        ((Activity) context).startActivityForResult(new Intent(context, VideoPlayActivity.class)
                .putExtra(KEY_DATA, videoId)
                .putExtra(KEY_AUTO_FINISH, autoFinished)
                .putExtra(KEY_FROM_TYPE, fromType), ChatEntity.TYPE_RECOMMEND_VIDEO);
    }


    @Override
    public int getContentView() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        return R.layout.activity_video_play;
    }

    @Override
    public VideoPresenter onCreatePresenter() {
        return new VideoPresenter(this);
    }


    //=============== 开始 视频手势 =======================//

    @Override
    public void initView() {
        mMPlayerView = (PlayerView) findViewById(R.id.mPlayerView);
        mPbBottom = (ProgressBar) findViewById(R.id.pb_bottom);
        mLoadingView = (LoadingView) findViewById(R.id.loadingView);
        mLoadingView.showLoadingNoText();
        mTvTitle = (TextView) findViewById(R.id.tv_video_title);
        mExoPlay = findViewById(R.id.exo_play);
        mExoNext = findViewById(R.id.exo_next);
        mVideoLayout = findViewById(R.id.video_play_constranint);
        mRvVideoDetail = findViewById(R.id.rv_video_detail);
        mRvVideoDetail.setLayoutManager(new LinearLayoutManager(mContext));

        mIvScreenAll = findViewById(R.id.iv_screen_all);
        /**
         * 全屏按钮
         */
        mIvScreenAll.setOnClickListener(v -> {
            /**
             * 设置为横屏
             */
            if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
            }
        });

        mVideoPlayConstranint = (ConstraintLayout) findViewById(R.id.video_play_constranint);
        mLlTopControl = (LinearLayout) findViewById(R.id.ll_top_control);
        mIvBack = (ImageView) findViewById(R.id.iv_back);
        mIvBack.setOnClickListener(v -> {
            // 如果是横屏，先退出横屏模式
            if (landscape) {
                mIvScreenAll.performClick();
            } else {
                finish();
            }
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

    @Override
    public void initData() {
        autoFinished = getIntent().getBooleanExtra(KEY_AUTO_FINISH, false);
        fromType = getIntent().getIntExtra(KEY_FROM_TYPE, -1);
        mVideoId = getIntent().getIntExtra(KEY_DATA, -1);
        if (mVideoId <= 0) {
            ToastUtil.show("参数错误");
            finish();
            return;
        }

        videoChanged(0);
    }

    private void setVideoList() {
        if (mVideoList == null) {
            mVideoList = new ArrayList<>();
            List<VideoModel.RecommendModel> recommendModels = mVideoModel.getRecommendModels();
            if (recommendModels != null) {
                int listHasPosition = -1;
                int len = recommendModels.size();
                for (int i = 0; i < len; i++) {
                    if (recommendModels.get(i).getId() == mVideoId) {
                        listHasPosition = i;
                        mPlayPosition = listHasPosition;
                        break;
                    }
                }
                if (listHasPosition == -1) {
                    listHasPosition = 0;
                    mPlayPosition = listHasPosition;
                    VideoModel.RecommendModel recommendModel = new VideoModel.RecommendModel();
                    recommendModel.setId(mVideoId);
                    recommendModel.setTitle(mVideoModel.title);
                    recommendModel.setImg(mVideoModel.getImg());
                    mVideoList.add(recommendModel);
                    if (fromType == TalkItemJumpHelper.FromType.TOOL) {
                        return;
                    }
                } else {
                    if (fromType == TalkItemJumpHelper.FromType.TOOL) {
                        mVideoList.add(recommendModels.get(listHasPosition));
                        mPlayPosition = 0;
                        return;
                    }
                }
                mVideoList.addAll(recommendModels);

            }

            if (mPlayPosition >= mVideoList.size() - 1) {
                mPlayPosition = mVideoList.size() - 1;
                mExoNext.setAlpha(0.4f);
                mExoNext.setEnabled(false);
            } else {
                mExoNext.setAlpha(1f);
                mExoNext.setEnabled(true);
            }
        }

    }

    @Override
    public void onBackPressed() {
        // 全屏（横屏）下点击返回，变竖屏
        if (landscape) {
            mIvScreenAll.performClick();
        } else {
            super.onBackPressed();
        }
    }

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
            ConstraintLayout.LayoutParams layoutParams1 = (ConstraintLayout.LayoutParams) mVideoLayout.getLayoutParams();
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

            ConstraintLayout.LayoutParams layoutParams1 = (ConstraintLayout.LayoutParams) mVideoLayout.getLayoutParams();
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

        if (mVideoList == null) {
            mPresenter.getVideoInfo(mVideoId);
            mLoadingView.showLoadingNoText();
            mExoNext.setAlpha(0.4f);
            mExoNext.setEnabled(false);
        } else {
            if (mPlayPosition >= mVideoList.size() - 1) {
                mPlayPosition = mVideoList.size() - 1;
                mExoNext.setAlpha(0.4f);
                mExoNext.setEnabled(false);
            } else {
                mExoNext.setAlpha(1f);
                mExoNext.setEnabled(true);
            }
            mPresenter.getVideoInfo(mVideoList.get(playPosition).getId());
            mLoadingView.showLoadingNoText();
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
    public LifecycleProvider getLifecycleProvider() {
        return VideoPlayActivity.this;
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

    private void initExoPlayer() {
        long positionMs = -1;
        // 初始化ExoPlayer
        if (mExoPlayer == null) {
            ExoPlayerFactory.with(mContext).builder(ExoPlayerFactory.EXO_VIDEO);
            mExoPlayer = ExoPlayerFactory.getExoPlayer();
            if (mPlayerListener != null) {
                mExoPlayer.removeListener(mPlayerListener);
            }
            mPlayerListener = new PlayerListener();
            mExoPlayer.addListener(mPlayerListener);
            mMPlayerView.setPlayer(mExoPlayer);
        }
        mTvTitle.setText(mVideoModel.title);
        setVideoList();
        setListAdapter();
//        mExoPlayer.setMediaItem(MediaItem.fromUri(Uri.parse(mVideoModel.getUrl())));
        AVResource.setSource(mExoPlayer, mVideoModel);
        mExoPlayer.prepare();
        // 先获取本地播放断电记录
        positionMs = VideoHistoryHelper.getPosition(mVideoModel.getId());
        if (positionMs <= 0) {
            positionMs = mVideoModel.getHistoryTime();
        }
        if (positionMs > 0) {
            continuePlay(positionMs);
        }
        mExoPlayer.setPlayWhenReady(true);
    }

    private void setListAdapter() {
        if (mVideoPlayListAdapter != null) {
            mVideoPlayListAdapter.setPlayPosition(mPlayPosition);
            VideoPlayListModel topModel = new VideoPlayListModel(VideoPlayListModel.TYPE_TOP);
            topModel.videoModel = mVideoModel;
            mVideoPlayListAdapter.getDataList().set(0, topModel);
            mVideoPlayListAdapter.notifyDataSetChanged();
            return;
        }
        List<VideoPlayListModel> list = new ArrayList<>();
        VideoPlayListModel topModel = new VideoPlayListModel(VideoPlayListModel.TYPE_TOP);
        topModel.videoModel = mVideoModel;
        list.add(topModel);
        list.add(new VideoPlayListModel(VideoPlayListModel.TYPE_GROUP).setHeadName("相关视频"));
        int size = mVideoList.size();
        for (int i = 0; i < size; i++) {
            VideoPlayListModel videoPlayListModel = new VideoPlayListModel(VideoPlayListModel.TYPE_BODY);
            videoPlayListModel.videoEtcModel = mVideoList.get(i);
            list.add(videoPlayListModel);
        }
        mVideoPlayListAdapter = new VideoPlayListAdapter(list);
        mVideoPlayListAdapter.setPlayPosition(mPlayPosition);
        mVideoPlayListAdapter.setOnRvItemListener(new OnRvItemListener<VideoPlayListModel>() {
            @Override
            public void onItemClick(List<VideoPlayListModel> list, int position) {
                videoChanged(position);
            }
        });
        // 添加动画
        mRvVideoDetail.setItemAnimator(new DefaultItemAnimator());
        mRvVideoDetail.setAdapter(mVideoPlayListAdapter);
    }

    /**
     * 续播UI提醒
     *
     * @param positionMs
     */
    private void continuePlay(long positionMs) {
        mExoPlayer.seekTo(positionMs);
        TextView mTvContinuePlay = findViewById(R.id.tv_continue_play);
        mTvContinuePlay.setVisibility(VISIBLE);
        mTvContinuePlay.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (EmptyUtil.isEmpty(VideoPlayActivity.this)) {
                    return;
                }
                mTvContinuePlay.setVisibility(View.GONE);
            }
        }, 3500);
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
    protected void onPause() {
        addHistory();
        super.onPause();
        if (mExoPlayer != null) {
            isPlaying = mExoPlayer.isPlaying();
            mExoPlayer.setPlayWhenReady(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

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
        if (mVideoPlayListAdapter != null) {
            if (mVideoId > 0) {
                DataChangeHelper.collectChange(mVideoPlayListAdapter.getmIvCollectTop(), mVideoId);
            }
        }
        super.onDestroy();
        TimingHelper.getInstance().removeWorkArriveListener(this);

        if (mExoPlayer != null) {
            mExoPlayer.release();
            mExoPlayer = null;
        }

        ExoPlayerFactory.release();

    }

    /**
     * 底部贴边进度条显示逻辑
     *
     * @param mPbBar
     * @param hide
     */
    private void onBottomProgressBar(ProgressBar mPbBar, boolean hide) {
        mPbBar.setVisibility(hide ? View.GONE : VISIBLE);
        if (!hide) {
            ExoPlayer player = mExoPlayer;
            if (handler != null) {
                handler.removeCallbacksAndMessages(null);
                handler = null;
            }
            handler = new Handler(Looper.getMainLooper()) {

                @Override
                public void handleMessage(@NonNull Message msg) {
                    super.handleMessage(msg);
                    if (player != null && player.getContentDuration() > 0) {
                        int progress = (int) (player.getContentPosition() * 1000 / player.getContentDuration());
                        int secondaryProgress = (int) (player.getContentBufferedPosition() * 1000 / player.getContentDuration());
                        mPbBar.setProgress(progress);
                        mPbBar.setSecondaryProgress(secondaryProgress);
                    }
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
            brightness = mBrightnessHelper.getBrightness();
        }
    }

    @Override
    public void onEndFF_REW(MotionEvent e) {
        setSpeed(1);
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
    }

    @Override
    public void onSingleTapGesture(MotionEvent e) {
        Log.d(TAG, "onSingleTapGesture: ");
        setSpeed(1);
        if (!(mLLShowForWard != null && mLLShowForWard.getVisibility() == VISIBLE)) {
            if (mMPlayerView != null) {
                if (mMPlayerView.isControllerVisible()) {
                    mMPlayerView.hideController();
                } else {
                    mMPlayerView.showController();
                }
            }
        }
    }

    @Override
    public void onDoubleTapGesture(MotionEvent e) {
        Log.d(TAG, "onDoubleTapGesture: ");

        if (mExoPlayer != null) {
            if (mExoPlayer.isPlaying()) {
                mExoPlayer.pause();
            } else {
                mExoPlayer.play();
            }
        }

    }

    @Override
    public void onDoubleTapUp(MotionEvent e) {
        Log.d(TAG, "onDoubleTapUp: ");
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
                    if (autoFinished) {
                        finish();
                        return;
                    }
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
        public void onMediaItemTransition(@Nullable @org.jetbrains.annotations.Nullable MediaItem mediaItem, int reason) {
            if (autoFinished && reason == Player.MEDIA_ITEM_TRANSITION_REASON_AUTO) {
                finish();
                return;
            }

//            if (mVideoPlayListAdapter != null && mExoPlayer != null && mRvVideoDetail != null) {
//                mRvVideoDetail.scrollToPosition(mPlayPosition + 2);
//            }
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


    //=============== 结束 视频手势 =======================//

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MEDIA_NEXT) {
            // 下一首按键
        } else if (keyCode == KeyEvent.KEYCODE_MEDIA_PREVIOUS) {
            // 上一首按键
        } else if (keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) {
            // 播放/暂停按键
            if (mExoPlayer!= null) {
                if (mExoPlayer.isPlaying()) {
                    mExoPlayer.pause();
                } else {
                    mExoPlayer.play();
                }
                return true;
            }
        }
        // 还可以添加更多按键操作，可以参阅 KeyEvent 类
        return super.onKeyDown(keyCode, event);
    }


}