package com.thfw.mobileheart.fragment.list;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.thfw.base.face.OnRvItemListener;
import com.thfw.base.models.BookItemModel;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.BookPresenter;
import com.thfw.mobileheart.R;
import com.thfw.mobileheart.activity.BaseFragment;
import com.thfw.mobileheart.activity.read.BookDetailActivity;
import com.thfw.mobileheart.adapter.BookListAdapter;
import com.thfw.mobileheart.lhxk.InstructScrollHelper;
import com.thfw.ui.widget.LoadingView;
import com.trello.rxlifecycle2.LifecycleProvider;

import org.jetbrains.annotations.NotNull;

import java.util.List;


public class ReadListFragment extends BaseFragment<BookPresenter> implements BookPresenter.BookUi<List<BookItemModel>> {

    private int type;
    private SmartRefreshLayout mRefreshLayout;
    private RecyclerView mRvList;
    private LoadingView mLoadingView;
    private BookListAdapter mBookListAdapter;
    private int page = 1;

    public ReadListFragment(int type) {
        super();
        this.type = type;
    }

    @Override
    public int getContentView() {
        return R.layout.fragment_book_list;
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
        mRefreshLayout.setEnableLoadMore(false);
        mRefreshLayout.setEnableRefresh(false);
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull @NotNull RefreshLayout refreshLayout) {
                initData();
            }
        });
    }

    @Override
    public void initData() {
        mPresenter.getArticleList(type, page);
    }

    @Override
    public LifecycleProvider getLifecycleProvider() {
        return this;
    }

    @Override
    public void onSuccess(List<BookItemModel> data) {
        if (page == 1) {
            mLoadingView.hide();
            mBookListAdapter = new BookListAdapter(data);
            mBookListAdapter.setOnRvItemListener(new OnRvItemListener<BookItemModel>() {
                @Override
                public void onItemClick(List<BookItemModel> list, int position) {
                    BookDetailActivity.startActivity(mContext, list.get(position).getId());
                }
            });
            mRefreshLayout.setEnableLoadMore(true);
            mRvList.setAdapter(mBookListAdapter);
        } else {
            mRefreshLayout.finishLoadMore(true);
            mBookListAdapter.addDataListNotify(data);
        }
        page++;

    }

    public void onReSelected() {
        // todo noting
    }

    @Override
    public void onFail(ResponeThrowable throwable) {
        if (page == 1) {
            mLoadingView.showFail(v -> {
                initData();
            });
        } else {
            mRefreshLayout.finishLoadMore(false);
        }
    }

    @Override
    protected void initLocalVoice(int type) {
        super.initLocalVoice(type);
        new InstructScrollHelper(ReadListFragment.class, mRvList);
    }
}