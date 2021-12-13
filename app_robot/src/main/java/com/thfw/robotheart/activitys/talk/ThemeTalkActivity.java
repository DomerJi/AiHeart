package com.thfw.robotheart.activitys.talk;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.base.base.IPresenter;
import com.thfw.base.face.OnRvItemListener;
import com.thfw.base.models.TalkModel;
import com.thfw.base.models.ThemeTalkModel;
import com.thfw.robotheart.R;
import com.thfw.robotheart.adapter.ThemeTalkAdapter;
import com.thfw.robotheart.view.TitleRobotView;
import com.thfw.ui.base.RobotBaseActivity;

import java.util.List;

public class ThemeTalkActivity extends RobotBaseActivity {

    private com.thfw.robotheart.view.TitleRobotView mTitleRobotView;
    private androidx.constraintlayout.widget.ConstraintLayout mClTheme;
    private androidx.recyclerview.widget.RecyclerView mRvList;
    private ThemeTalkAdapter talkAdapter;

    @Override
    public int getContentView() {
        return R.layout.activity_theme_talk;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initView() {

        mTitleRobotView = (TitleRobotView) findViewById(R.id.titleRobotView);
        mClTheme = (ConstraintLayout) findViewById(R.id.cl_theme);
        mRvList = (RecyclerView) findViewById(R.id.rv_list);
        mRvList.setLayoutManager(new GridLayoutManager(mContext, 3));
    }

    @Override
    public void initData() {
        talkAdapter = new ThemeTalkAdapter(null);
        talkAdapter.setOnRvItemListener(new OnRvItemListener<ThemeTalkModel>() {
            @Override
            public void onItemClick(List<ThemeTalkModel> list, int position) {

            }
        });
        mRvList.setAdapter(talkAdapter);
        mClTheme.setOnClickListener(v -> {
            AiTalkActivity.startActivity(mContext, new TalkModel(TalkModel.TYPE_THEME));
        });
    }
}