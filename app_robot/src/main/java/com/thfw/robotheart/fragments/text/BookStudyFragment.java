package com.thfw.robotheart.fragments.text;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.thfw.base.face.OnRvItemListener;
import com.thfw.base.models.BookStudyItemModel;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.BookPresenter;
import com.thfw.robotheart.R;
import com.thfw.robotheart.activitys.text.BookIdeoDetailActivity;
import com.thfw.robotheart.adapter.BookStudyListAdapter;
import com.thfw.robotheart.util.PageHelper;
import com.thfw.robotheart.activitys.RobotBaseFragment;
import com.thfw.ui.widget.LoadingView;
import com.trello.rxlifecycle2.LifecycleProvider;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Author:pengs
 * Date: 2021/12/2 16:15
 * Describe:Todo
 */
public class BookStudyFragment extends RobotBaseFragment<BookPresenter> implements BookPresenter.BookUi<List<BookStudyItemModel>> {

    private int type;
    private SmartRefreshLayout mRefreshLayout;
    private RecyclerView mRvList;
    private LoadingView mLoadingView;
    private BookStudyListAdapter mBookStudyListAdapter;
    private PageHelper<BookStudyItemModel> pageHelper;

    public BookStudyFragment(int type) {
        super();
        this.type = type;
    }

    @Override
    public int getContentView() {
        return R.layout.fragment_book_study_list;
    }

    @Override
    public BookPresenter onCreatePresenter() {
        return new BookPresenter(this);
    }

    @Override
    public void initView() {

        mRefreshLayout = (SmartRefreshLayout) findViewById(R.id.refreshLayout);
        mRvList = (RecyclerView) findViewById(R.id.rvList);
        mRvList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mLoadingView = (LoadingView) findViewById(R.id.loadingView);
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull @NotNull RefreshLayout refreshLayout) {
                mPresenter.getIdeologyArticleList(type, pageHelper.getPage());
            }
        });

    }

    @Override
    public void initData() {
        mBookStudyListAdapter = new BookStudyListAdapter(null);
        mBookStudyListAdapter.setOnRvItemListener(new OnRvItemListener<BookStudyItemModel>() {
            @Override
            public void onItemClick(List<BookStudyItemModel> list, int position) {
                BookIdeoDetailActivity.startActivity(mContext, list.get(position).getId());
            }
        });
        mRvList.setAdapter(mBookStudyListAdapter);
        pageHelper = new PageHelper<>(mLoadingView, mRefreshLayout, mBookStudyListAdapter);
        pageHelper.setRefreshEnable(false);
        mPresenter.getIdeologyArticleList(type, pageHelper.getPage());
    }

    @Override
    public LifecycleProvider getLifecycleProvider() {
        return this;
    }

    @Override
    public void onSuccess(List<BookStudyItemModel> data) {
        pageHelper.onSuccess(data);
    }

    @Override
    public void onFail(ResponeThrowable throwable) {
        pageHelper.onFail(v -> {
            mPresenter.getIdeologyArticleList(type, pageHelper.getPage());
        });
    }
}
