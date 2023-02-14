package com.thfw.mobileheart.fragment.list;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;
import com.thfw.base.face.OnRvItemListener;
import com.thfw.base.models.BookStudyItemModel;
import com.thfw.base.models.BookStudyTypeModel;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.BookPresenter;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.SharePreferenceUtil;
import com.thfw.mobileheart.R;
import com.thfw.mobileheart.activity.BaseFragment;
import com.thfw.mobileheart.activity.read.BookIdeoDetailActivity;
import com.thfw.mobileheart.activity.read.StudyHomeActivity;
import com.thfw.mobileheart.adapter.StudyChildTypeAdapter;
import com.thfw.mobileheart.adapter.StudyListAdapter;
import com.thfw.mobileheart.lhxk.InstructScrollHelper;
import com.thfw.mobileheart.util.FragmentLoader;
import com.thfw.mobileheart.util.PageHelper;
import com.thfw.ui.widget.LoadingView;
import com.trello.rxlifecycle2.LifecycleProvider;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class StudyListFragment extends BaseFragment<BookPresenter> implements BookPresenter.BookUi<List<BookStudyItemModel>> {

    public static final String KEY_CHILD_TYPE = "key.study.child.type";
    public static final String KEY_INDEX = "key.index.";
    private SmartRefreshLayout mRefreshLayout;
    private RecyclerView mRvList;
    private LoadingView mLoadingView;
    private int type;
    private PageHelper<BookStudyItemModel> mPageHelper;
    private RecyclerView mRvChildren;
    private StudyChildTypeAdapter studyChildTypeAdapter;
    private FragmentLoader mLoader;

    private StudyListFragment mStudyListFragment;

    public StudyListFragment(int type) {
        super();
        this.type = type;
    }

    public void onReSelected() {
        if (studyChildTypeAdapter != null) {
            studyChildTypeAdapter.resetSelectedIndex();
        }
    }

    @Override
    public int getContentView() {
        return R.layout.fragment_study_list;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        SharePreferenceUtil.setInt(KEY_INDEX + type, -1);
    }

    @Override
    public BookPresenter onCreatePresenter() {
        return new BookPresenter(this);
    }

    @Override
    public void initView() {

        mRefreshLayout = (SmartRefreshLayout) findViewById(R.id.refreshLayout);
        mRvList = (RecyclerView) findViewById(R.id.rv_list);
        mLoadingView = (LoadingView) findViewById(R.id.loadingView);
        mRvList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));

        mRvChildren = (RecyclerView) findViewById(R.id.rv_children);
        mRvChildren.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        setChildType();
    }

    @Override
    protected void onReCreateView() {
        super.onReCreateView();
        if (mLoadingView != null) {
            mLoadingView.showLoading();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onVisible(boolean isVisible) {
        super.onVisible(isVisible);
        if (!isVisible) {
            if (studyChildTypeAdapter != null) {
                int index = studyChildTypeAdapter.getmSelectedIndex();
                SharePreferenceUtil.setInt(KEY_INDEX + type, index);
                LogUtil.d("JSP_" + type, "index = " + index);
            }

        } else {
            if (studyChildTypeAdapter != null) {
                int cacheIndex = SharePreferenceUtil.getInt(KEY_INDEX + type, -1);
                studyChildTypeAdapter.setSelectedIndex(cacheIndex);
                studyChildTypeAdapter.notifyDataSetChanged();
                LogUtil.d("JSP_" + type, "index cache = " + cacheIndex);
            }
        }
    }

    private void setChildType() {
        if (getArguments() != null && !getArguments().isEmpty()) {
            ArrayList<BookStudyTypeModel> studyTypeModels = (ArrayList<BookStudyTypeModel>) getArguments().getSerializable(KEY_CHILD_TYPE);
            studyChildTypeAdapter = new StudyChildTypeAdapter(studyTypeModels);
            mLoader = new FragmentLoader(getChildFragmentManager(), R.id.fl_content01);
            studyChildTypeAdapter.setOnRvItemListener(new OnRvItemListener<BookStudyTypeModel>() {


                @Override
                public void onItemClick(List<BookStudyTypeModel> list, int position) {
                    LogUtil.d(TAG, "onItemClick -> " + position);
                    if (position == -1) {
                        mLoader.hide();
                        return;
                    }
                    int childType = list.get(position).id;
                    Fragment fragment = mLoader.load(childType);
                    if (fragment == null) {
                        mLoader.add(childType, new StudyListFragment(childType));
                    }
                    mStudyListFragment = (StudyListFragment) mLoader.load(childType);
                    LogUtil.d(TAG, "onItemClick -> " + position + mStudyListFragment);
                }
            });
            mRvChildren.setAdapter(studyChildTypeAdapter);
        }
    }

    @Override
    public void initData() {

        StudyListAdapter studyListAdapter = new StudyListAdapter(null);
        studyListAdapter.setOnRvItemListener(new OnRvItemListener<BookStudyItemModel>() {
            @Override
            public void onItemClick(List<BookStudyItemModel> list, int position) {
                BookIdeoDetailActivity.startActivity(mContext, list.get(position).getId());
            }
        });
        mRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull @NotNull RefreshLayout refreshLayout) {
                mPresenter.getIdeologyArticleList(type, mPageHelper.getPage());
            }

            @Override
            public void onRefresh(@NonNull @NotNull RefreshLayout refreshLayout) {
                mPageHelper.onRefresh();
                mPresenter.getIdeologyArticleList(type, mPageHelper.getPage());
            }
        });

        mRvList.setAdapter(studyListAdapter);

        mPageHelper = new PageHelper<>(mLoadingView, mRefreshLayout, studyListAdapter);

        mPresenter.getIdeologyArticleList(type, mPageHelper.getPage());
    }

    @Override
    public LifecycleProvider getLifecycleProvider() {
        return this;
    }

    @Override
    public void onSuccess(List<BookStudyItemModel> data) {
        mPageHelper.onSuccess(data);
    }

    @Override
    public void onDestroy() {
        SharePreferenceUtil.setInt(KEY_INDEX + type, -1);
        super.onDestroy();
    }

    @Override
    public void onFail(ResponeThrowable throwable) {
        mPageHelper.onFail(v -> {
            mPresenter.getIdeologyArticleList(type, mPageHelper.getPage());
        });
    }

    @Override
    protected void initLocalVoice(int type) {
        super.initLocalVoice(type);
        new InstructScrollHelper(StudyListFragment.class, mRvList);
        new InstructScrollHelper(StudyHomeActivity.class, mRvChildren);
    }

}