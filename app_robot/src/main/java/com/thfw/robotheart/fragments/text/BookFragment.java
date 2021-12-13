package com.thfw.robotheart.fragments.text;

import android.os.Handler;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.thfw.base.base.IPresenter;
import com.thfw.base.face.OnRvItemListener;
import com.thfw.base.models.BookStudyItemModel;
import com.thfw.base.models.VideoModel;
import com.thfw.robotheart.R;
import com.thfw.robotheart.activitys.video.VideoPlayerActivity;
import com.thfw.robotheart.adapter.BookListAdapter;
import com.thfw.ui.base.RobotBaseFragment;
import com.thfw.ui.widget.LoadingView;

import java.util.List;

/**
 * Author:pengs
 * Date: 2021/12/2 16:15
 * Describe:Todo
 */
public class BookFragment extends RobotBaseFragment {

    private String type;
    private SmartRefreshLayout mRefreshLayout;
    private RecyclerView mRvList;
    private LoadingView mLoadingView;
    private BookListAdapter mBookListAdapter;

    public BookFragment(String type) {
        super();
        this.type = type;
    }

    @Override
    public int getContentView() {
        return R.layout.fragment_book_list;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initView() {

        mRefreshLayout = (SmartRefreshLayout) findViewById(R.id.refreshLayout);
        mRvList = (RecyclerView) findViewById(R.id.rvList);
        mRvList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mLoadingView = (LoadingView) findViewById(R.id.loadingView);
    }

    @Override
    public void initData() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mLoadingView.hide();
                mBookListAdapter = new BookListAdapter(null);
                mBookListAdapter.setOnRvItemListener(new OnRvItemListener<BookStudyItemModel>() {
                    @Override
                    public void onItemClick(List<BookStudyItemModel> list, int position) {
                        VideoPlayerActivity.startActivity(mContext, VideoModel.getVideoUrl().get(1));
                    }
                });
                mRvList.setAdapter(mBookListAdapter);
            }
        }, 500);
    }
}
