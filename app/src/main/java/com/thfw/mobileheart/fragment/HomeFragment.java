package com.thfw.mobileheart.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshHeader;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;
import com.scwang.smart.refresh.layout.simple.SimpleMultiListener;
import com.thfw.base.base.IPresenter;
import com.thfw.base.face.OnRvItemListener;
import com.thfw.base.utils.LogUtil;
import com.thfw.mobileheart.R;
import com.thfw.mobileheart.activity.ExoPlayerActivity;
import com.thfw.mobileheart.adapter.HomeAdapter;
import com.thfw.base.models.HomeEntity;
import com.thfw.mobileheart.util.PageHelper;
import com.thfw.ui.base.BaseFragment;
import com.thfw.ui.widget.LinearTopLayout;
import com.thfw.ui.widget.LoadingView;
import com.thfw.ui.widget.MySearchView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 首页
 */
public class HomeFragment extends BaseFragment {

    private RecyclerView mRvHome;
    private LoadingView mLoadingView;
    private SmartRefreshLayout mRefreshLayout;
    private PageHelper<HomeEntity> pageHelper;
    private HomeAdapter mHomeAdapter;
    private LinearTopLayout mLtlTop;
    private MySearchView mSearch;
    // 上滑渐变参数
    private int ivHeight;
    private int topHeight;
    private int maxHeight;
    private int minHeight;

    private boolean isFirst = true;

    @Override
    public int getContentView() {
        return R.layout.fragment_home;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initView() {

        mRvHome = (RecyclerView) findViewById(R.id.rv_home);
        mRvHome.setHasFixedSize(true);
//        mRvHome.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        mLoadingView = (LoadingView) findViewById(R.id.loadingView);
        mRefreshLayout = (SmartRefreshLayout) findViewById(R.id.refreshLayout);
        mLtlTop = (LinearTopLayout) findViewById(R.id.ltl_top);
        mSearch = (MySearchView) findViewById(R.id.search);
        mRefreshLayout.setHeaderHeight(75);
    }

    private void searchViewScroll() {
        mRefreshLayout.setOnMultiListener(new SimpleMultiListener() {
            @Override
            public void onHeaderMoving(RefreshHeader header, boolean isDragging, float percent, int offset, int headerHeight, int maxDragHeight) {
                // 底部搜索淡入淡出
                if (mLtlTop.getHeight() > 0) {
                    float f = 1 - (offset * 1.0f / mLtlTop.getHeight());
                    if (f < 0) {
                        mLtlTop.setAlpha(0f);
                    } else if (f > 1) {
                        mLtlTop.setAlpha(1f);
                    } else {
                        mLtlTop.setAlpha(f);
                    }
                    LogUtil.d("searchViewScroll -> " + f);
                }

            }
        });
        mRvHome.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int dyAll = 0;

            @Override
            public void onScrolled(@NonNull @NotNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                dyAll += dy;
                onScroll(dyAll);
            }
        });
    }

    /**
     * 顶部搜索导航区域背景渐变
     *
     * @param y
     */
    public void onScroll(int y) {
        LogUtil.d("onScroll(y)" + y);
        if (ivHeight == 0 || topHeight == 0) {
            ivHeight = mHomeAdapter.getBannerHeight();
            topHeight = mLtlTop.getMeasuredHeight();
            maxHeight = ivHeight - topHeight;
            minHeight = maxHeight - topHeight;
            return;
        }
        // LogUtil.d("ivHeight = " + ivHeight + ";topHeight = " + topHeight);

        if (y > maxHeight) {
            mLtlTop.setBackgroundColor(Color.argb(255, 89, 198, 193));
        } else if (y < minHeight) {
            mLtlTop.setBackgroundColor(Color.TRANSPARENT);
        } else {
            float rate = (y - minHeight) * 1.0f / topHeight;
            int a = (int) (rate * 255);
            mLtlTop.setBackgroundColor(Color.argb(a, 89, 198, 193));
        }
    }

    @Override
    public void initData() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 2);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return mHomeAdapter.getItemViewType(position) == HomeEntity.TYPE_BODY2 ? 1 : 2;
            }
        });
        mRvHome.setLayoutManager(gridLayoutManager);

        mHomeAdapter = new HomeAdapter(null);
        mHomeAdapter.setOnRvItemListener(new OnRvItemListener<HomeEntity>() {
            @Override
            public void onItemClick(List<HomeEntity> list, int position) {
                startActivity(new Intent(mContext, ExoPlayerActivity.class));
            }
        });
        // 添加动画
        mRvHome.setItemAnimator(new DefaultItemAnimator());
        mRvHome.setAdapter(mHomeAdapter);
        pageHelper = new PageHelper<>(mLoadingView, mRefreshLayout, mHomeAdapter);
        mRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull @NotNull RefreshLayout refreshLayout) {
                loadData();
            }

            @Override
            public void onRefresh(@NonNull @NotNull RefreshLayout refreshLayout) {
                pageHelper.onRefresh();
                loadData();
            }
        });
        searchViewScroll();
        loadData();

    }

    private void loadData() {
        new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (new Random().nextInt(100) == 1) {
                    pageHelper.onFail(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            loadData();
                        }
                    });
                } else {
                    pageHelper.onSuccess(getList());
                }
                isFirst = false;
            }
        }.sendEmptyMessageDelayed(0, isFirst ? 0 : 300);
    }

    public List<HomeEntity> getList() {
        List<HomeEntity> list = new ArrayList<>();
        if (pageHelper.isFirstPage()) {
            HomeEntity homeEntity = new HomeEntity();
            homeEntity.type = HomeEntity.TYPE_BANNER;
            homeEntity.imageUrls = new ArrayList<>();
            homeEntity.imageUrls.add("https://t7.baidu.com/it/u=4198287529,2774471735&fm=193&f=GIF");
            homeEntity.imageUrls.add("https://t7.baidu.com/it/u=1595072465,3644073269&fm=193&f=GIF");
            homeEntity.imageUrls.add("https://img03.sogoucdn.com/app/a/07/e58d89131f3a0882b804313208e0e983");
            homeEntity.imageUrls.add("https://img03.sogoucdn.com/app/a/07/b4f5a091fb5c7d40d7b74893392a705b");
            homeEntity.imageUrls.add("https://p0.ssl.qhimgs4.com/t019d96f95af289aafe.jpg");
            homeEntity.imageUrls.add("http://p1.qhimgs4.com/t01bf942682a3b9fc7b.jpg");
            homeEntity.imageUrls.add("https://desk-fd.zol-img.com.cn/t_s960x600c5/g5/M00/02/02/ChMkJlbKxWOIRxKoAAVChq3hCgEAALHVQBJ8IsABUKe444.jpg");
            list.add(homeEntity);

            list.add(new HomeEntity().setType(HomeEntity.TYPE_SORT));
            list.add(new HomeEntity().setType(HomeEntity.TYPE_CUSTOM_MADE));
            list.add(new HomeEntity().setType(HomeEntity.TYPE_TAB_TITLE).setTabTitle("最近浏览"));
            list.add(new HomeEntity().setType(HomeEntity.TYPE_HISTORY));
        }

        list.add(new HomeEntity().setType(HomeEntity.TYPE_TAB_TITLE).setTabTitle("小天推荐"));
        for (int i = 0; i < 18; i++) {
            list.add(new HomeEntity());
        }
        list.add(new HomeEntity().setType(HomeEntity.TYPE_TAB_TITLE).setTabTitle("猜你喜欢"));
        for (int i = 0; i < 18; i++) {
            list.add(new HomeEntity().setType(HomeEntity.TYPE_BODY2).setBody2Position(i));
        }

        return list;
    }

    @Override
    public void onVisible(boolean isVisible) {
        super.onVisible(isVisible);
        if (mHomeAdapter != null) {
            mHomeAdapter.setBanner(isVisible);
        }
    }
}