package com.thfw.robotheart.activitys.exercise;

import androidx.viewpager2.widget.ViewPager2;

import com.thfw.base.base.IPresenter;
import com.thfw.base.face.OnRvItemListener;
import com.thfw.base.models.ExerciseModel;
import com.thfw.base.utils.ToastUtil;
import com.thfw.robotheart.R;
import com.thfw.robotheart.adapter.ExerciseIngAdapter;
import com.thfw.robotheart.view.TitleRobotView;
import com.thfw.ui.base.RobotBaseActivity;

import java.util.List;

public class ExerciseIngActivity extends RobotBaseActivity {


    private com.thfw.robotheart.view.TitleRobotView mTitleRobotView;
    private androidx.viewpager2.widget.ViewPager2 mVpList;

    @Override
    public int getContentView() {
        return R.layout.activity_exercise_ing;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initView() {

        mTitleRobotView = (TitleRobotView) findViewById(R.id.titleRobotView);
        mVpList = (ViewPager2) findViewById(R.id.vp_list);
    }

    @Override
    public void initData() {
        ExerciseIngAdapter exerciseIngAdapter = new ExerciseIngAdapter(null);
        mVpList.setAdapter(exerciseIngAdapter);
        exerciseIngAdapter.setOnRvItemListener(new OnRvItemListener<ExerciseModel>() {
            @Override
            public void onItemClick(List<ExerciseModel> list, int position) {
                mVpList.setUserInputEnabled(true);
                // 结果
                if (mVpList.getCurrentItem() == exerciseIngAdapter.getItemCount() - 1) {
                    ToastUtil.show("答完了");
                } else {
                    mVpList.setCurrentItem(position + 1, true);
                }
            }
        });
        mVpList.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                mVpList.setUserInputEnabled(false);
            }
        });


    }
}