package com.thfw.robotheart.activitys.video;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.common.reflect.TypeToken;
import com.thfw.base.face.OnRvItemListener;
import com.thfw.base.models.VideoLastEtcModel;
import com.thfw.base.models.VideoTypeModel;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.VideoPresenter;
import com.thfw.base.utils.GsonUtil;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.SharePreferenceUtil;
import com.thfw.robotheart.R;
import com.thfw.robotheart.adapter.VideoEtcTypeAdapter;
import com.thfw.robotheart.fragments.media.VideoEtcListFragment;
import com.thfw.robotheart.util.FragmentLoader;
import com.thfw.robotheart.view.TitleRobotView;
import com.thfw.ui.base.RobotBaseActivity;
import com.thfw.ui.widget.LoadingView;
import com.trello.rxlifecycle2.LifecycleProvider;

import java.lang.reflect.Type;
import java.util.List;

public class VideoHomeActivity extends RobotBaseActivity<VideoPresenter> implements VideoPresenter.VideoUi<List<VideoTypeModel>> {

    private static final String KEY_TYPE_LIST = "key.video.type.list";
    private static final String KEY_HAS_VIDEO = "key.video.has";
    private TitleRobotView mTitleRobotView;
    private RecyclerView mRvList;
    private LinearLayout mLlTop;
    private FrameLayout mFlContent;
    private VideoEtcListFragment mVideoEtcListFragment;
    private TextView mTvLastAudio;
    private LinearLayout mLlHistory;
    private com.thfw.ui.widget.LoadingView mLoadingView;
    private VideoEtcTypeAdapter mVideoEtcTypeAdapter;

    @Override
    public int getContentView() {
        return R.layout.activity_video_home;
    }

    @Override
    public VideoPresenter onCreatePresenter() {
        return new VideoPresenter(this);
    }

    @Override
    public void initView() {

        mTitleRobotView = (TitleRobotView) findViewById(R.id.titleRobotView);
        mRvList = (RecyclerView) findViewById(R.id.rvList);
        mLlTop = (LinearLayout) findViewById(R.id.ll_top);
        if (!SharePreferenceUtil.getBoolean(KEY_HAS_VIDEO, false)) {
            mLlTop.setVisibility(View.GONE);
        }
        mFlContent = (FrameLayout) findViewById(R.id.fl_content);
        mRvList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));

        mTvLastAudio = (TextView) findViewById(R.id.tv_last_audio);

        mLlHistory = (LinearLayout) findViewById(R.id.ll_history);
        mLoadingView = (LoadingView) findViewById(R.id.loadingView);
    }

    @Override
    public void initData() {
        mVideoEtcTypeAdapter = new VideoEtcTypeAdapter(null);
        mRvList.setAdapter(mVideoEtcTypeAdapter);
        FragmentLoader mLoader = new FragmentLoader(getSupportFragmentManager(), R.id.fl_content);


        mVideoEtcTypeAdapter.setOnRvItemListener(new OnRvItemListener<VideoTypeModel>() {
            @Override
            public void onItemClick(List<VideoTypeModel> list, int position) {
                // todo type
                int rootType = list.get(position).rootType;
                LogUtil.d("onItemClick rootType = " + rootType);
                int id = list.get(position).id;
                int type = id > 0 ? id : rootType;
                LogUtil.d("onItemClick id = " + type);
                Fragment fragment = mLoader.load(type);
                if (fragment == null) {
                    mLoader.add(type, new VideoEtcListFragment(type));
                }
                mVideoEtcListFragment = (VideoEtcListFragment) mLoader.load(type);
            }
        });

        Type type = new TypeToken<List<VideoTypeModel>>() {
        }.getType();
        List<VideoTypeModel> cacheModel = SharePreferenceUtil.getObject(KEY_TYPE_LIST, type);
        if (cacheModel != null) {
            mVideoEtcTypeAdapter.setDataListNotify(cacheModel);
            if (mVideoEtcTypeAdapter.getItemCount() > 0) {
                mLoadingView.hide();
                mVideoEtcTypeAdapter.getOnRvItemListener().onItemClick(mVideoEtcTypeAdapter.getDataList(), 0);
            }
        }
        mPresenter.getVideoType();
    }

    @Override
    public LifecycleProvider getLifecycleProvider() {
        return this;
    }

    @Override
    public void onSuccess(List<VideoTypeModel> data) {
        if (data != null) {
            data.add(0, new VideoTypeModel("全部", 0));
        }
        SharePreferenceUtil.setString(KEY_TYPE_LIST, GsonUtil.toJson(data));
        mLoadingView.hide();
        boolean isSetEmpty = false;
        if (mVideoEtcTypeAdapter.getItemCount() == 0) {
            isSetEmpty = true;
        }
        mVideoEtcTypeAdapter.setDataListNotify(data);
        if (isSetEmpty) {
            mVideoEtcTypeAdapter.getOnRvItemListener().onItemClick(mVideoEtcTypeAdapter.getDataList(), 0);
        }
    }


    @Override
    public void onFail(ResponeThrowable throwable) {
        if (mVideoEtcTypeAdapter.getItemCount() == 0) {
            mLoadingView.showFail(v -> {
                mPresenter.getVideoType();
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getLastVideo();
    }

    /**
     * 获得最后一次播放记录
     */
    private void getLastVideo() {
        new VideoPresenter(new VideoPresenter.VideoUi<VideoLastEtcModel>() {
            @Override
            public LifecycleProvider getLifecycleProvider() {
                return VideoHomeActivity.this;
            }

            @Override
            public void onSuccess(VideoLastEtcModel data) {
                notifyLastData(data);
            }

            @Override
            public void onFail(ResponeThrowable throwable) {

            }
        }).getVideoLastHistory();
    }

    /**
     * 最后一次播放记录信息展示
     *
     * @param data
     */
    private void notifyLastData(VideoLastEtcModel data) {
        if (data != null) {
            SharePreferenceUtil.setBoolean(KEY_HAS_VIDEO, true);
            mLlTop.setVisibility(View.VISIBLE);
            mTvLastAudio.setText("上次播放：" + data.getTitle() + "  播放至" + data.getPercentTime() + "%  " + data.getAddTime());
            mTvLastAudio.setOnClickListener(v -> {
                VideoPlayerActivity.startActivity(mContext, data.toVideoModel(), 0);
            });
        }
    }
}