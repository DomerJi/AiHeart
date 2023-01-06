package com.thfw.robotheart.activitys;

import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.android.material.tabs.TabItem;
import com.google.common.reflect.TypeToken;
import com.thfw.base.base.SpeechToAction;
import com.thfw.base.face.OnRvItemListener;
import com.thfw.base.models.CommonModel;
import com.thfw.base.models.SearchResultModel;
import com.thfw.base.models.SpeechModel;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.SearchPresenter;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.base.utils.GsonUtil;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.SharePreferenceUtil;
import com.thfw.base.utils.ToastUtil;
import com.thfw.robotheart.R;
import com.thfw.robotheart.adapter.SearchHistoryAdapter;
import com.thfw.robotheart.fragments.SearchFragment;
import com.thfw.robotheart.lhxk.InstructScrollHelper;
import com.thfw.robotheart.lhxk.LhXkHelper;
import com.thfw.robotheart.view.TitleRobotView;
import com.thfw.ui.dialog.LoadingDialog;
import com.thfw.ui.widget.LoadingView;
import com.thfw.ui.widget.MyRobotSearchView;
import com.thfw.user.login.UserManager;
import com.trello.rxlifecycle2.LifecycleProvider;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 搜索页面
 * Created By jishuaipeng on 2021/12/29
 */
public class SearchActivity extends RobotBaseActivity<SearchPresenter> implements SearchPresenter.SearchUi<SearchResultModel> {

    private static String KEY_HISTORY = "search.history";
    private com.thfw.robotheart.view.TitleRobotView mTitleRobotView;
    private com.thfw.ui.widget.MyRobotSearchView mMySearch;
    private androidx.constraintlayout.widget.ConstraintLayout mClHistory;
    private android.widget.TextView mTvHistoryTitle;
    private androidx.recyclerview.widget.RecyclerView mRvHistory;
    private android.widget.LinearLayout mLlClearHistory;
    private com.google.android.material.tabs.TabItem mTiVideo;
    private com.google.android.material.tabs.TabItem mTiExercise;
    private com.google.android.material.tabs.TabItem mTiBook;
    private com.google.android.material.tabs.TabItem mTiStudy;
    private com.thfw.ui.widget.LoadingView mLoadingView;
    private SearchHistoryAdapter searchHistoryAdapter;
    private List<String> mKeyHistoryList = new ArrayList<>();
    private SearchFragment searchFragment;


    @Override
    public int getContentView() {
        return R.layout.activity_search;
    }

    @Override
    public SearchPresenter onCreatePresenter() {
        return new SearchPresenter(this);
    }

    @Override
    public void initView() {

        mTitleRobotView = (TitleRobotView) findViewById(R.id.titleRobotView);
        mMySearch = (MyRobotSearchView) findViewById(R.id.my_search);
        mClHistory = (ConstraintLayout) findViewById(R.id.cl_history);
        mTvHistoryTitle = (TextView) findViewById(R.id.tv_history_title);
        mRvHistory = (RecyclerView) findViewById(R.id.rv_history);


        mLlClearHistory = (LinearLayout) findViewById(R.id.ll_clear_history);
        mLlClearHistory.setOnClickListener(v -> {
            LogUtil.d(TAG, "mLlClearHistory++++++++++++++++++++++++++++++++++++++++_________________");
            onDeleteHistory();
        });


        mTiVideo = (TabItem) findViewById(R.id.ti_video);
        mTiExercise = (TabItem) findViewById(R.id.ti_exercise);
        mTiBook = (TabItem) findViewById(R.id.ti_book);
        mTiStudy = (TabItem) findViewById(R.id.ti_study);
        mLoadingView = (LoadingView) findViewById(R.id.loadingView);
        mLoadingView.hide();
        // 设置布局管理器
        FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(mContext);
        // flexDirection 属性决定主轴的方向（即项目的排列方向）。类似 LinearLayout 的 vertical 和 horizontal。
        flexboxLayoutManager.setFlexDirection(FlexDirection.ROW);
        // 主轴为水平方向，起点在左端。
        // flexWrap 默认情况下 Flex 跟 LinearLayout 一样，都是不带换行排列的，但是flexWrap属性可以支持换行排列。
        flexboxLayoutManager.setFlexWrap(FlexWrap.WRAP);
        // 按正常方向换行
        // justifyContent 属性定义了项目在主轴上的对齐方式。
        flexboxLayoutManager.setJustifyContent(JustifyContent.FLEX_START);//交叉轴的起点对齐。
        mRvHistory.setLayoutManager(flexboxLayoutManager);
        searchHistoryAdapter = new SearchHistoryAdapter(mKeyHistoryList);
        searchHistoryAdapter.setOnRvItemListener(new OnRvItemListener<String>() {
            @Override
            public void onItemClick(List<String> list, int position) {
                mMySearch.getEditeText().setText(list.get(position));
                mMySearch.getEditeText().setSelection(list.get(position).length());
                onGoSearch(list.get(position));
            }
        });
        searchHistoryAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                mLlClearHistory.setVisibility(searchHistoryAdapter.getItemCount() > 0 ? View.VISIBLE : View.INVISIBLE);
                mLlClearHistory.setEnabled(searchHistoryAdapter.getItemCount() > 0 ? true : false);
            }
        });
        mRvHistory.setAdapter(searchHistoryAdapter);
        mMySearch.setOnSearchListener(new MyRobotSearchView.OnSearchListener() {
            @Override
            public void onSearch(String key, boolean clickSearch) {
                if (EmptyUtil.isEmpty(key)) {
                    hideResult();
                    mClHistory.setVisibility(View.VISIBLE);
                    mLoadingView.hide();
                    return;
                }
                if (clickSearch) {
                    onGoSearch(key);
                }
            }

            @Override
            public void onClick(View view) {

            }
        });
    }

    /**
     * 开始搜索
     *
     * @param key
     */
    private void onGoSearch(String key) {
        // 搜索关键词列表去重
        if (mKeyHistoryList.contains(key)) {
            mKeyHistoryList.remove(key);
        }
        // 更新历史搜索关键词列表
        mKeyHistoryList.add(0, key);
        SharePreferenceUtil.setString(KEY_HISTORY, GsonUtil.toJson(mKeyHistoryList));
        searchHistoryAdapter.notifyDataSetChanged();
        // 隐藏历史搜索页面
        mClHistory.setVisibility(View.GONE);
        hideResult();
        mLoadingView.showLoading();
        // 开始搜索
        mPresenter.onSearch(key);
        hideInput();
    }

    @Override
    public void initData() {
        KEY_HISTORY = "search.history." + UserManager.getInstance().getUID();

        Type type = new TypeToken<List<String>>() {
        }.getType();

        List<String> keyHistory = SharePreferenceUtil.getObject(KEY_HISTORY, type);
        if (!EmptyUtil.isEmpty(keyHistory)) {
            setHistoryList(keyHistory);
            mLoadingView.hide();
        } else {
            mLoadingView.showLoading();
        }

        // 历史搜索数据加载
        new SearchPresenter(new SearchPresenter.SearchUi<List<String>>() {
            @Override
            public LifecycleProvider getLifecycleProvider() {
                return SearchActivity.this;
            }

            @Override
            public void onSuccess(List<String> list) {
                mLoadingView.hide();
                if (list == null) {
                    mRvHistory.removeAllViews();
                    return;
                }
                SharePreferenceUtil.setString(KEY_HISTORY, GsonUtil.toJson(list));
                setHistoryList(list);
            }

            @Override
            public void onFail(ResponeThrowable throwable) {
                mLoadingView.hide();
            }
        }).getHistory();

    }

    // 重置历史搜索列表数据，并更新列表
    private void setHistoryList(List<String> list) {
        mKeyHistoryList.clear();
        mKeyHistoryList.addAll(list);
        searchHistoryAdapter.setDataListNotify(mKeyHistoryList);
    }

    @Override
    protected void initLocalVoice(int type) {
        super.initLocalVoice(type);
        new InstructScrollHelper(SearchActivity.class, mRvHistory, false);
        SpeechToAction.Instruction instruction = new SpeechToAction.Instruction() {
            @Override
            public SpeechModel matching(String speechText) {
                String weather = ".{0,2}(搜索).{1,8}";
                if (speechText.matches(weather)) {
                    String key = speechText.replaceAll(".{0,2}(搜索)", "");
                    if (!TextUtils.isEmpty(key)) {
                        return SpeechModel.create(speechText).setMatches(true).setOutText(key);
                    }
                }
                return super.matching(speechText);
            }
        };
        LhXkHelper.putAction(SearchActivity.class, new SpeechToAction(instruction, () -> {
            String key = instruction.speechModel.getOutText();
            mMySearch.getEditeText().setText(key);
            mMySearch.getEditeText().setSelection(key.length());
            onGoSearch(key);
        }));
        LhXkHelper.putAction(SearchActivity.class, new SpeechToAction("清除记录,清空记录", () -> {
            mLlClearHistory.performClick();
        }));
    }

    @Override
    public LifecycleProvider getLifecycleProvider() {
        return this;
    }

    /**
     * 清除历史搜索记录
     */
    private void onDeleteHistory() {
        LoadingDialog.show(this, "清除中...");
        new SearchPresenter(new SearchPresenter.SearchUi<CommonModel>() {
            @Override
            public LifecycleProvider getLifecycleProvider() {
                return SearchActivity.this;
            }

            @Override
            public void onSuccess(CommonModel data) {
                LoadingDialog.hide();
                mKeyHistoryList.clear();
                searchHistoryAdapter.notifyDataSetChanged();
                ToastUtil.show("清除成功");
            }

            @Override
            public void onFail(ResponeThrowable throwable) {
                LoadingDialog.hide();
                ToastUtil.show("清除失败");
            }
        }).onDeleteHistory();
    }

    @Override
    public void onSuccess(SearchResultModel data) {
        initResultView(data);
    }

    private void initResultView(SearchResultModel data) {
        if (mMySearch == null || mMySearch.isKeyEmpty()) {
            return;
        }

        if (data == null || EmptyUtil.isEmpty(data.getAllList())) {
            mLoadingView.showEmpty();
            return;
        }
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        searchFragment = new SearchFragment(data);
        fragmentTransaction.replace(R.id.fl_content, searchFragment).commit();
        mLoadingView.hide();
        mClHistory.setVisibility(View.GONE);
    }

    private void hideResult() {
        if (searchFragment != null) {
            getSupportFragmentManager().beginTransaction().remove(searchFragment).commit();
            searchFragment = null;
        }
    }

    @Override
    public void onFail(ResponeThrowable throwable) {
        mLoadingView.showFail(v -> {
            onGoSearch(mMySearch.getEditeText().getText().toString());
        });
    }

    public void onViewPagerNext() {
        if (searchFragment != null) {
            searchFragment.onViewPagerNext();
        }
    }


}