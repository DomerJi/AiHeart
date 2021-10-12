package com.thfw.mobileheart.fragment.exercise;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.thfw.base.base.IPresenter;
import com.thfw.mobileheart.adapter.BaseAdapter;
import com.thfw.robotheart.R;
import com.thfw.ui.base.BaseFragment;

import org.jetbrains.annotations.NotNull;

/**
 * Author:pengs
 * Date: 2021/10/8 17:26
 * Describe:Todo
 */
public class ExerciseLogcataFragment extends BaseFragment {
    private SmartRefreshLayout mRefreshLayout;
    private RecyclerView mRvExercise;

    @Override
    public int getContentView() {
        return R.layout.fragment_exerciselogcata_layout;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initView() {

        mRefreshLayout = (SmartRefreshLayout) findViewById(R.id.refreshLayout);
        mRvExercise = (RecyclerView) findViewById(R.id.rv_exercise);
    }

    @Override
    public void initData() {

        mRvExercise.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        mRvExercise.setAdapter(new BaseAdapter(null) {
            @NonNull
            @NotNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                return new LogcataHolder(LayoutInflater.from(mContext).inflate(R.layout.item_exercise_logcata_layout, parent, false));
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
