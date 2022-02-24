package com.thfw.robotheart.activitys.audio;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.common.reflect.TypeToken;
import com.thfw.base.face.OnRvItemListener;
import com.thfw.base.models.AudioLastEtcModel;
import com.thfw.base.models.AudioTypeModel;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.AudioPresenter;
import com.thfw.base.utils.GsonUtil;
import com.thfw.base.utils.SharePreferenceUtil;
import com.thfw.robotheart.R;
import com.thfw.robotheart.adapter.AudioEtcTypeAdapter;
import com.thfw.robotheart.fragments.media.AudioEtcListFragment;
import com.thfw.robotheart.util.FragmentLoader;
import com.thfw.robotheart.view.TitleRobotView;
import com.thfw.robotheart.activitys.RobotBaseActivity;
import com.thfw.ui.widget.LoadingView;
import com.trello.rxlifecycle2.LifecycleProvider;

import java.lang.reflect.Type;
import java.util.List;

public class AudioHomeActivity extends RobotBaseActivity<AudioPresenter> implements AudioPresenter.AudioUi<List<AudioTypeModel>> {

    private static final String KEY_TYPE_LIST = "key.audio.type.list";
    private static final String KEY_HAS_AUDIO = "key.audio.has";
    private com.thfw.robotheart.view.TitleRobotView mTitleRobotView;
    private androidx.recyclerview.widget.RecyclerView mRvList;
    private android.widget.LinearLayout mLlTop;
    private android.widget.FrameLayout mFlContent;
    private AudioEtcListFragment mAudioEtcListFragment;
    private android.widget.TextView mTvLastAudio;
    private LinearLayout mLlHistory;
    private AudioEtcTypeAdapter mAudioEtcTypeAdapter;
    private com.thfw.ui.widget.LoadingView mLoadingView;

    @Override
    public int getContentView() {
        return R.layout.activity_audio_home;
    }

    @Override
    public AudioPresenter onCreatePresenter() {
        return new AudioPresenter(this);
    }

    @Override
    public void initView() {

        mTitleRobotView = (TitleRobotView) findViewById(R.id.titleRobotView);
        mRvList = (RecyclerView) findViewById(R.id.rvList);
        mLlTop = (LinearLayout) findViewById(R.id.ll_top);
        mFlContent = (FrameLayout) findViewById(R.id.fl_content);
        mRvList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        if (!SharePreferenceUtil.getBoolean(KEY_HAS_AUDIO, false)) {
            mLlTop.setVisibility(View.GONE);
        }
        mTvLastAudio = (TextView) findViewById(R.id.tv_last_audio);

        mLlHistory = (LinearLayout) findViewById(R.id.ll_history);
        mLoadingView = (LoadingView) findViewById(R.id.loadingView);
    }

    @Override
    public void initData() {
        mAudioEtcTypeAdapter = new AudioEtcTypeAdapter(null);
        mRvList.setAdapter(mAudioEtcTypeAdapter);

        FragmentLoader mLoader = new FragmentLoader(getSupportFragmentManager(), R.id.fl_content);
        mAudioEtcTypeAdapter.setOnRvItemListener(new OnRvItemListener<AudioTypeModel>() {
            @Override
            public void onItemClick(List<AudioTypeModel> list, int position) {
//                // todo type
                int id = list.get(position).getKey();
                Fragment fragment = mLoader.load(id);
                if (fragment == null) {
                    mLoader.add(id, new AudioEtcListFragment(id));
                }
                mAudioEtcListFragment = (AudioEtcListFragment) mLoader.load(id);
            }
        });
        Type type = new TypeToken<List<AudioTypeModel>>() {
        }.getType();
        List<AudioTypeModel> cacheModel = SharePreferenceUtil.getObject(KEY_TYPE_LIST, type);
        if (cacheModel != null) {
            mAudioEtcTypeAdapter.setDataListNotify(cacheModel);
            if (mAudioEtcTypeAdapter.getItemCount() > 0) {
                mLoadingView.hide();
                mAudioEtcTypeAdapter.getOnRvItemListener().onItemClick(mAudioEtcTypeAdapter.getDataList(), 0);
            }
        }
        mPresenter.getAudioType();
    }

    @Override
    public LifecycleProvider getLifecycleProvider() {
        return this;
    }

    @Override
    public void onSuccess(List<AudioTypeModel> data) {
        if (data != null) {
            data.add(0, new AudioTypeModel("全部", 0));
        }
        SharePreferenceUtil.setString(KEY_TYPE_LIST, GsonUtil.toJson(data));
        mLoadingView.hide();

        boolean isSetEmpty = false;
        if (mAudioEtcTypeAdapter.getItemCount() == 0) {
            isSetEmpty = true;
        }
        mAudioEtcTypeAdapter.setDataListNotify(data);
        if (isSetEmpty) {
            mAudioEtcTypeAdapter.getOnRvItemListener().onItemClick(mAudioEtcTypeAdapter.getDataList(), 0);
        }
    }

    /**
     * 获得最后一次播放记录
     */
    private void getLastAudio() {
        new AudioPresenter(new AudioPresenter.AudioUi<AudioLastEtcModel>() {
            @Override
            public LifecycleProvider getLifecycleProvider() {
                return AudioHomeActivity.this;
            }

            @Override
            public void onSuccess(AudioLastEtcModel data) {
                notifyLastAudioData(data);
            }

            @Override
            public void onFail(ResponeThrowable throwable) {

            }
        }).getAudioLastHistory();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getLastAudio();
    }

    private void notifyLastAudioData(AudioLastEtcModel data) {
        if (data != null) {
            SharePreferenceUtil.setBoolean(KEY_HAS_AUDIO, true);
            mLlTop.setVisibility(View.VISIBLE);
            mTvLastAudio.setText("上次播放：" + data.getTitle() + "     " + data.getAddTime());
            mTvLastAudio.setOnClickListener(v -> {
                AudioPlayerActivity.startActivity(mContext, data.toAudioEtcModel());
            });
        }
    }

    @Override
    public void onFail(ResponeThrowable throwable) {
        if (mAudioEtcTypeAdapter.getItemCount() == 0) {
            mLoadingView.showFail(v -> {
                mPresenter.getAudioType();
            });
        }
    }

}