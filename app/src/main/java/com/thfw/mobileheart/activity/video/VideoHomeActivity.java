package com.thfw.mobileheart.activity.video;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.common.reflect.TypeToken;
import com.thfw.base.models.VideoLastEtcModel;
import com.thfw.base.models.VideoTypeModel;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.VideoPresenter;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.base.utils.GsonUtil;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.SharePreferenceUtil;
import com.thfw.mobileheart.R;
import com.thfw.mobileheart.fragment.list.VideoListFragment;
import com.thfw.mobileheart.view.LastTextView;
import com.thfw.ui.base.BaseActivity;
import com.thfw.ui.widget.LoadingView;
import com.thfw.ui.widget.TitleView;
import com.trello.rxlifecycle2.LifecycleProvider;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class VideoHomeActivity extends BaseActivity<VideoPresenter> implements VideoPresenter.VideoUi<List<VideoTypeModel>> {

    private static final String KEY_TYPE_LIST = "key.video.type.list";
    private static final String KEY_HAS_VIDEO = "key.video.has";
    private List<Fragment> tabFragmentList = new ArrayList<>();
    private com.thfw.ui.widget.TitleView mTitleView;
    private com.google.android.material.tabs.TabLayout mTabLayout;
    private androidx.viewpager.widget.ViewPager mViewPager;
    private com.thfw.ui.widget.LoadingView mLoadingView;
    private com.thfw.mobileheart.view.LastTextView mTvLastAudio;

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, VideoHomeActivity.class));
    }

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

        mTitleView = (TitleView) findViewById(R.id.titleView);
        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mLoadingView = (LoadingView) findViewById(R.id.loadingView);
        mTvLastAudio = (LastTextView) findViewById(R.id.tv_last_audio);
    }

    @Override
    public void initData() {


        //设置TabLayout和ViewPager联动
        mTabLayout.setupWithViewPager(mViewPager, false);

        Type type = new TypeToken<List<VideoTypeModel>>() {
        }.getType();
        List<VideoTypeModel> cacheModel = SharePreferenceUtil.getObject(KEY_TYPE_LIST, type);
        if (!EmptyUtil.isEmpty(cacheModel)) {
            mLoadingView.hide();
            setAdapter(cacheModel);
        }
        mPresenter.getVideoType();

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                ((VideoListFragment) tabFragmentList.get(tab.getPosition())).onReSelected();
            }
        });
    }

    private void setAdapter(List<VideoTypeModel> cacheModel) {
        int size = cacheModel.size();
        //添加tab
        for (int i = 0; i < size; i++) {
            mTabLayout.addTab(mTabLayout.newTab().setText(cacheModel.get(i).name));
            int rootType = cacheModel.get(i).rootType;
            LogUtil.d("onItemClick rootType = " + rootType);
            int id = cacheModel.get(i).id;
            int contentType = id > 0 ? id : rootType;
            VideoListFragment videoListFragment = new VideoListFragment(contentType);
            Bundle bundle = new Bundle();
            bundle.putSerializable(VideoListFragment.KEY_CHILD_TYPE, (ArrayList) cacheModel.get(i).getList());
            videoListFragment.setArguments(bundle);
            tabFragmentList.add(videoListFragment);

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
                return cacheModel.get(position).name;
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                getSupportFragmentManager().beginTransaction()
                        .setMaxLifecycle(tabFragmentList.get(position), Lifecycle.State.RESUMED).commit();

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
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
        if (mViewPager.getAdapter() == null) {
            setAdapter(data);
        }
    }


    @Override
    public void onFail(ResponeThrowable throwable) {
        if (mViewPager.getAdapter() == null) {
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

            mTvLastAudio.setVisibility(View.VISIBLE);
            mTvLastAudio.setText("上次播放：" + data.getTitle() + "  播放至" + data.getPercentTime() + "%  " + data.getAddTime());
            mTvLastAudio.setOnClickListener(v -> {
                VideoPlayActivity.startActivity(mContext, data.getId(), false);
            });
        }
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        if (mTvLastAudio != null) {
            mTvLastAudio.onAttached(recyclerView);
        }
    }
}