package com.thfw.mobileheart.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.common.reflect.TypeToken;
import com.opensource.svgaplayer.SVGAImageView;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshHeader;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;
import com.scwang.smart.refresh.layout.simple.SimpleMultiListener;
import com.thfw.base.base.SpeechToAction;
import com.thfw.base.face.OnRvItemListener;
import com.thfw.base.models.AudioEtcModel;
import com.thfw.base.models.HomeEntity;
import com.thfw.base.models.MobileRecommendModel;
import com.thfw.base.models.TalkModel;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.MobilePresenter;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.base.utils.GsonUtil;
import com.thfw.base.utils.HandlerUtil;
import com.thfw.base.utils.SharePreferenceUtil;
import com.thfw.mobileheart.R;
import com.thfw.mobileheart.activity.BaseFragment;
import com.thfw.mobileheart.activity.SearchActivity;
import com.thfw.mobileheart.activity.audio.AudioHomeActivity;
import com.thfw.mobileheart.activity.audio.AudioPlayerActivity;
import com.thfw.mobileheart.activity.exercise.ExerciseActivity;
import com.thfw.mobileheart.activity.exercise.ExerciseDetailActivity;
import com.thfw.mobileheart.activity.me.HotPhoneActivity;
import com.thfw.mobileheart.activity.mood.MoodDetailActivity;
import com.thfw.mobileheart.activity.read.BookDetailActivity;
import com.thfw.mobileheart.activity.read.BookIdeoDetailActivity;
import com.thfw.mobileheart.activity.read.ReadHomeActivity;
import com.thfw.mobileheart.activity.read.StudyHomeActivity;
import com.thfw.mobileheart.activity.talk.ChatActivity;
import com.thfw.mobileheart.activity.talk.ThemeListActivity;
import com.thfw.mobileheart.activity.test.TestBeginActivity;
import com.thfw.mobileheart.activity.test.TestingActivity;
import com.thfw.mobileheart.activity.video.VideoHomeActivity;
import com.thfw.mobileheart.activity.video.VideoPlayActivity;
import com.thfw.mobileheart.adapter.HomeAdapter;
import com.thfw.mobileheart.constants.AnimFileName;
import com.thfw.mobileheart.lhxk.InstructScrollHelper;
import com.thfw.mobileheart.lhxk.LhXkHelper;
import com.thfw.mobileheart.util.DialogFactory;
import com.thfw.mobileheart.util.MoodLivelyHelper;
import com.thfw.ui.widget.LinearTopLayout;
import com.thfw.ui.widget.MySearchView;
import com.thfw.user.login.UserManager;
import com.thfw.user.login.UserObserver;
import com.thfw.user.models.User;
import com.trello.rxlifecycle2.LifecycleProvider;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 首页
 */
public class HomeFragment extends BaseFragment<MobilePresenter> implements MobilePresenter.MobileUi<List<MobileRecommendModel>> {

    private static final String KEY_HOME_DEFAULT = "key.home.more1.";
    private static String KEY_HOME = KEY_HOME_DEFAULT;
    private RecyclerView mRvHome;
    private SmartRefreshLayout mRefreshLayout;
    private HomeAdapter mHomeAdapter;
    private LinearTopLayout mLtlTop;
    private MySearchView mSearch;
    // 上滑渐变参数
    private int ivHeight;
    private int topHeight;
    private int maxHeight;
    private int minHeight;

    private int recommendPage = 1;


    private List<HomeEntity> mMainList = new ArrayList<>();
    private boolean isLogin;

    @Override
    public int getContentView() {
        return R.layout.fragment_home;
    }

    @Override
    public MobilePresenter onCreatePresenter() {
        return new MobilePresenter(this);
    }

    @Override
    public void initView() {
        isLogin = UserManager.getInstance().isTrueLogin();

        mRvHome = (RecyclerView) findViewById(R.id.rv_home);
        mRvHome.setHasFixedSize(true);
        mRefreshLayout = (SmartRefreshLayout) findViewById(R.id.refreshLayout);
        mLtlTop = (LinearTopLayout) findViewById(R.id.ltl_top);
        mSearch = (MySearchView) findViewById(R.id.search);
        mSearch.setOnSearchListener(new MySearchView.OnSearchListener() {
            @Override
            public void onSearch(String key, boolean clickSearch) {

            }

            @Override
            public void onClick(View view) {
                startActivity(new Intent(mContext, SearchActivity.class));
            }
        });
        mRefreshLayout.setHeaderHeight(75);


    }

    @Override
    protected void initLocalVoice(int type) {
        super.initLocalVoice(type);

        LhXkHelper.putAction(HomeFragment.class, new SpeechToAction("主题对话,聊一聊", () -> onAction(AnimFileName.TRANSITION_THEME)));
        LhXkHelper.putAction(HomeFragment.class, new SpeechToAction("倾诉吐槽", () -> onAction(AnimFileName.TRANSITION_TALK)));
        LhXkHelper.putAction(HomeFragment.class, new SpeechToAction("测一测", () -> onAction(AnimFileName.TRANSITION_TEST)));
        LhXkHelper.putAction(HomeFragment.class, new SpeechToAction("练一练", () -> onAction(AnimFileName.TRANSITION_TOOL)));
        LhXkHelper.putAction(HomeFragment.class, new SpeechToAction("听一听", () -> onAction(AnimFileName.TRANSITION_AUDIO)));
        LhXkHelper.putAction(HomeFragment.class, new SpeechToAction("看一看", () -> onAction(AnimFileName.TRANSITION_VIDEO)));
        LhXkHelper.putAction(HomeFragment.class, new SpeechToAction("读一读", () -> onAction(AnimFileName.TRANSITION_BOOK)));
        LhXkHelper.putAction(HomeFragment.class, new SpeechToAction("学一学", () -> onAction(AnimFileName.TRANSITION_IDEO)));
        LhXkHelper.putAction(HomeFragment.class, new SpeechToAction("心情日记", () -> MoodDetailActivity.startActivity(mContext)));
        LhXkHelper.putAction(HomeFragment.class, new SpeechToAction("活跃", () -> MoodDetailActivity.startActivity(mContext)).setLike(true));
        LhXkHelper.putAction(HomeFragment.class, new SpeechToAction("全国心理热线,心理热线,了解更多", () -> mContext.startActivity(new Intent(mContext, HotPhoneActivity.class))).setLike(true));

        LhXkHelper.putAction(HomeFragment.class, new SpeechToAction("搜索", () -> startActivity(new Intent(mContext, SearchActivity.class))));
        new InstructScrollHelper(HomeFragment.class, mRvHome);
    }

    private void onAction(String animName) {
        DialogFactory.createSvgaDialog((FragmentActivity) mContext, animName, new DialogFactory.OnSVGACallBack() {
            @Override
            public void callBack(SVGAImageView svgaImageView) {
                switch (animName) {
                    case AnimFileName.TRANSITION_THEME:
                        ThemeListActivity.startActivity(mContext);
                        break;
                    case AnimFileName.TRANSITION_TALK:
                        ChatActivity.startActivity(mContext, new TalkModel(TalkModel.TYPE_AI));
                        break;
                    case AnimFileName.TRANSITION_TEST:
                        TestingActivity.startActivity(mContext);
                        break;
                    case AnimFileName.TRANSITION_TOOL:
                        ExerciseActivity.startActivity(mContext);
                        break;
                    case AnimFileName.TRANSITION_AUDIO:
                        AudioHomeActivity.startActivity(mContext);
                        break;
                    case AnimFileName.TRANSITION_VIDEO:
                        VideoHomeActivity.startActivity(mContext);
                        break;
                    case AnimFileName.TRANSITION_BOOK:
                        ReadHomeActivity.startActivity(mContext);
                        break;
                    case AnimFileName.TRANSITION_IDEO:
                        StudyHomeActivity.startActivity(mContext);
                        break;
                }
            }
        });

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
        mRvHome.setLayoutManager(new LinearLayoutManager(mContext));
        mHomeAdapter = new HomeAdapter(mMainList);
        mHomeAdapter.setOnRvItemListener(new OnRvItemListener<HomeEntity>() {
            @Override
            public void onItemClick(List<HomeEntity> list, int position) {
                MobileRecommendModel model = list.get(position).recommendModel;
                // type 1-测评 2-文章 3-音频  4-视频 5 话术 6-思政文章 7-工具包
                switch (model.getType()) {
                    case 1:
                        TestBeginActivity.startActivity(mContext, model.getContentId());
                        break;
                    case 2:
                        BookDetailActivity.startActivity(mContext, model.getContentId());
                        break;
                    case 3:
                        AudioEtcModel audioEtcModel = new AudioEtcModel();
                        audioEtcModel.setId(model.getContentId());
                        audioEtcModel.setTitle(model.getTitle());
                        audioEtcModel.setImg(model.getPic());
                        AudioPlayerActivity.startActivity(mContext, audioEtcModel);
                        break;
                    case 4:
                        VideoPlayActivity.startActivity(mContext, model.getContentId(), false);
                        break;
                    case 5:
                        ChatActivity.startActivity(mContext, new TalkModel(TalkModel.TYPE_SPEECH_CRAFT).setId(model.getContentId()));
                        break;
                    case 6:
                        BookIdeoDetailActivity.startActivity(mContext, model.getContentId());
                        break;
                    case 7:
                        ExerciseDetailActivity.startActivity(mContext, model.getContentId());
                        break;
                }
            }
        });
        // 添加动画
        mRvHome.setItemAnimator(new DefaultItemAnimator());
        initList();
        mRvHome.setAdapter(mHomeAdapter);
        HandlerUtil.getMainHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (EmptyUtil.isEmpty(HomeFragment.this)) {
                    return;
                }
                MoodLivelyHelper.addListener(mHomeAdapter);
            }
        }, 1000);
        mRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull @NotNull RefreshLayout refreshLayout) {
                loadData(false);
            }

            @Override
            public void onRefresh(@NonNull @NotNull RefreshLayout refreshLayout) {
                recommendPage = 1;
                loadData(true);
            }
        });
        searchViewScroll();
        loadData(true);

    }

    private void loadData(boolean refresh) {
        if (refresh) {
            requestBanner();
        }
        mPresenter.onGetRecommendList(recommendPage);
    }

    private void requestBanner() {
        new MobilePresenter(new MobilePresenter.MobileUi<List<HomeEntity.BannerModel>>() {
            @Override
            public LifecycleProvider getLifecycleProvider() {
                return HomeFragment.this;
            }

            @Override
            public void onSuccess(List<HomeEntity.BannerModel> data) {
                mMainList.get(0).setBannerModels(data);
                mHomeAdapter.notifyBanner();
            }

            @Override
            public void onFail(ResponeThrowable throwable) {
                if (UserManager.getInstance().isTrueLogin()) {
                    new Handler().postDelayed(() -> {
                        if (!EmptyUtil.isEmpty(getActivity())) {
                            requestBanner();
                        }
                    }, 3000);
                }
            }
        }).onGetBannerDetail();
    }

    public void initList() {
        if (UserManager.getInstance().isLogin()) {
            KEY_HOME = KEY_HOME_DEFAULT + UserManager.getInstance().getUID();
        } else {
            KEY_HOME = KEY_HOME_DEFAULT;
        }

        Type type = new TypeToken<List<HomeEntity>>() {
        }.getType();
        List<HomeEntity> cacheModel = SharePreferenceUtil.getObject(KEY_HOME, type);
        if (!EmptyUtil.isEmpty(cacheModel)) {
            mMainList.addAll(cacheModel);
        } else {
            mMainList.add(new HomeEntity().setType(HomeEntity.TYPE_BANNER));
            mMainList.add(new HomeEntity().setType(HomeEntity.TYPE_SORT));
            mMainList.add(new HomeEntity().setType(HomeEntity.TYPE_CUSTOM_MADE));
            mMainList.add(new HomeEntity().setType(HomeEntity.TYPE_TAB_TITLE).setTabTitle("小密推荐"));
        }
    }

    @Override
    public void onVisible(boolean isVisible) {
        super.onVisible(isVisible);
        if (mHomeAdapter != null) {
            mHomeAdapter.setBanner(isVisible);
        }
    }

    @Override
    public LifecycleProvider getLifecycleProvider() {
        return HomeFragment.this;
    }

    @Override
    public void onSuccess(List<MobileRecommendModel> data) {

        if (!EmptyUtil.isEmpty(data)) {
            if (recommendPage == 1) {
                int size = mMainList.size();
                if (size > 4) {
                    for (int i = size - 1; i > 3; i--) {
                        mMainList.remove(i);
                    }
                }
            }
            for (MobileRecommendModel model : data) {
                mMainList.add(new HomeEntity().setRecommendModel(model));
            }
            if (recommendPage == 1) {
                SharePreferenceUtil.setString(KEY_HOME, GsonUtil.toJson(mMainList));
            }
            mRefreshLayout.finishRefresh(true);
            mRefreshLayout.finishLoadMore(true);
            mHomeAdapter.notifyDataSetChanged();
            recommendPage++;
        } else {
            mRefreshLayout.finishRefresh(true);
            mRefreshLayout.finishLoadMore(true);
            if (recommendPage >= 1) {
                mRefreshLayout.setNoMoreData(true);
            }
        }
    }

    @Override
    public void onFail(ResponeThrowable throwable) {
        if (mRefreshLayout != null) {
            mRefreshLayout.finishLoadMore(false);
            mRefreshLayout.finishRefresh(false);
        }
    }


    @Override
    public UserObserver addObserver() {
        return new UserObserver() {
            @Override
            public void onChanged(UserManager accountManager, User user) {
                postDelayedLogin();
            }
        };
    }

    private void postDelayedLogin() {
        HandlerUtil.getMainHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (EmptyUtil.isEmpty(HomeFragment.this)) {
                    return;
                }
                if (isLogin != UserManager.getInstance().isTrueLogin()) {
                    isLogin = UserManager.getInstance().isTrueLogin();
                    if (isLogin) {
                        loadData(true);
                        MoodLivelyHelper.addListener(mHomeAdapter);
                    } else {
                        if (mHomeAdapter != null) {
                            mHomeAdapter.notifyDataSetChanged();
                        }
                        MoodLivelyHelper.clearModel();
                    }
                }
            }
        }, 1000);
    }
}