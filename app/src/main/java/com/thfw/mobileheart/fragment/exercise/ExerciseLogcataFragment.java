package com.thfw.mobileheart.fragment.exercise;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.thfw.base.base.IPresenter;
import com.thfw.base.face.OnRvItemListener;
import com.thfw.base.models.ExerciseModel;
import com.thfw.base.utils.ToastUtil;
import com.thfw.mobileheart.R;
import com.thfw.mobileheart.activity.BaseFragment;
import com.thfw.mobileheart.activity.exercise.ExerciseIngActivity;
import com.thfw.mobileheart.adapter.ExerciseLogcateAdapter;
import com.thfw.mobileheart.lhxk.InstructScrollHelper;
import com.thfw.ui.widget.DeviceUtil;

import java.util.List;

/**
 * Author:pengs
 * Date: 2021/10/8 17:26
 * Describe:Todo
 */
public class ExerciseLogcataFragment extends BaseFragment {
    ExerciseModel exerciseModel;
    private SmartRefreshLayout mRefreshLayout;
    private RecyclerView mRvExercise;

    public void setExerciseModel(ExerciseModel exerciseModel) {
        this.exerciseModel = exerciseModel;
        initAdapter();
    }

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
        mRefreshLayout.setEnableRefresh(false);
        mRefreshLayout.setEnableLoadMore(false);

        mRvExercise = (RecyclerView) findViewById(R.id.rv_exercise);
        mRvExercise.setLayoutManager(new LinearLayoutManager(mContext));
    }

    @Override
    public void initData() {
        initAdapter();

    }

    private void initAdapter() {
        if (exerciseModel != null && mRvExercise != null) {
            ExerciseLogcateAdapter exerciseLogcateAdapter = new ExerciseLogcateAdapter(exerciseModel.getLinkList());
            exerciseLogcateAdapter.setOnRvItemListener(new OnRvItemListener<ExerciseModel.LinkModel>() {
                @Override
                public void onItemClick(List<ExerciseModel.LinkModel> list, int position) {
                    if (list.get(position).getStatus() == -1) {
                        ToastUtil.show("需要按顺序完成后，方可解锁");
                        return;
                    }
                    ExerciseIngActivity.startActivity(mContext, list.get(position).getDialogId(), list.get(position).isUsed());
                }
            });
            mRvExercise.setAdapter(exerciseLogcateAdapter);
            if (DeviceUtil.isLhXk_CM_GB03D()) {
                new InstructScrollHelper(ExerciseLogcataFragment.class, mRvExercise);
            }
        }
    }

}
