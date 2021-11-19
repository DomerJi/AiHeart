package com.thfw.mobileheart.fragment.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.thfw.base.base.IPresenter;
import com.thfw.mobileheart.adapter.BaseAdapter;
import com.thfw.mobileheart.R;
import com.thfw.ui.base.BaseFragment;

import org.jetbrains.annotations.NotNull;


public class StudyListFragment extends BaseFragment {

    private SmartRefreshLayout mRefreshLayout;
    private RecyclerView mRvList;

    public StudyListFragment(String data) {
        super();
    }

    @Override
    public int getContentView() {
        return R.layout.fragment_study_list;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initView() {

        mRefreshLayout = (SmartRefreshLayout) findViewById(R.id.refreshLayout);
        mRvList = (RecyclerView) findViewById(R.id.rv_list);
    }

    @Override
    public void initData() {

        mRvList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        mRvList.setAdapter(new BaseAdapter(null) {
            @NonNull
            @NotNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                return new StudyListFragment.LogcataHolder(LayoutInflater.from(mContext).inflate(R.layout.item_study_list_layout, parent, false));
            }

            @Override
            public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {

            }

            @Override
            public int getItemCount() {
                return 30;
            }
        });
    }

    public class LogcataHolder extends RecyclerView.ViewHolder {

        public LogcataHolder(@NonNull @NotNull View itemView) {
            super(itemView);
        }
    }

}