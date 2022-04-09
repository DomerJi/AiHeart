package com.thfw.mobileheart.activity.audio;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.common.reflect.TypeToken;
import com.thfw.base.models.AudioLastEtcModel;
import com.thfw.base.models.AudioTypeModel;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.AudioPresenter;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.base.utils.GsonUtil;
import com.thfw.base.utils.SharePreferenceUtil;
import com.thfw.mobileheart.R;
import com.thfw.mobileheart.activity.BaseActivity;
import com.thfw.mobileheart.fragment.list.AudioListFragment;
import com.thfw.mobileheart.view.LastTextView;
import com.thfw.ui.widget.LoadingView;
import com.thfw.ui.widget.TitleView;
import com.trello.rxlifecycle2.LifecycleProvider;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class AudioHomeActivity extends BaseActivity<AudioPresenter> implements AudioPresenter.AudioUi<List<AudioTypeModel>> {

    private static final String KEY_TYPE_LIST = "audio.type.list";
    private com.thfw.ui.widget.TitleView mTitleView;
    private com.google.android.material.tabs.TabLayout mTabLayout;
    private androidx.viewpager.widget.ViewPager mViewPager;

    private List<Fragment> tabFragmentList = new ArrayList<>();
    private com.thfw.ui.widget.LoadingView mLoadingView;
    private com.thfw.mobileheart.view.LastTextView mTvLastAudio;

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, AudioHomeActivity.class));
    }

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

        mTitleView = (TitleView) findViewById(R.id.titleView);
        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mLoadingView = (LoadingView) findViewById(R.id.loadingView);
        mTvLastAudio = (LastTextView) findViewById(R.id.tv_last_audio);
    }


    @Override
    public void initData() {
        Type type = new TypeToken<List<AudioTypeModel>>() {
        }.getType();
        List<AudioTypeModel> cacheModel = SharePreferenceUtil.getObject(KEY_TYPE_LIST, type);
        if (!EmptyUtil.isEmpty(cacheModel)) {
            mLoadingView.hide();
            setViewPager(cacheModel);
        } else {
            mPresenter.getAudioType();
        }
    }

    @Override
    public LifecycleProvider getLifecycleProvider() {
        return this;
    }

    @Override
    public void onSuccess(List<AudioTypeModel> data) {
        mLoadingView.hide();
        if (data != null) {
            data.add(0, new AudioTypeModel("全部", 0));
            SharePreferenceUtil.setString(KEY_TYPE_LIST, GsonUtil.toJson(data));
            if (mViewPager.getAdapter() == null) {
                setViewPager(data);
            }
        }
    }

    private void setViewPager(List<AudioTypeModel> audioTypeModels) {
        int size = audioTypeModels.size();
        // 添加tab
        for (int i = 0; i < size; i++) {
            mTabLayout.addTab(mTabLayout.newTab().setText(audioTypeModels.get(i).getName()));
            tabFragmentList.add(new AudioListFragment(audioTypeModels.get(i).getKey()));
        }

        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return tabFragmentList.get(position);
            }

            @Override
            public int getCount() {
                return tabFragmentList.size();
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return audioTypeModels.get(position).getName();
            }
        });

        // 设置TabLayout和ViewPager联动
        mTabLayout.setupWithViewPager(mViewPager, false);
    }


    @Override
    public void onFail(ResponeThrowable throwable) {
        if (mViewPager.getAdapter() == null) {
            mLoadingView.showFail(v -> {
                mPresenter.getAudioType();
            });
        }
    }

    @Override
    public void finish() {
        setResult(RESULT_OK);
        super.finish();
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
            mTvLastAudio.setVisibility(View.VISIBLE);
            mTvLastAudio.setText("上次播放：" + data.getTitle() + "     " + data.getAddTime());
            mTvLastAudio.setOnClickListener(v -> {
                AudioPlayerActivity.startActivity(mContext, data.toAudioEtcModel());
            });
        }
    }


    public void setRecyclerView(RecyclerView recyclerView) {
        if (mTvLastAudio != null) {
            mTvLastAudio.onAttached(recyclerView);
        }
    }


}