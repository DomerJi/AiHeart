package com.thfw.robotheart.activitys.talk;

import android.content.Context;
import android.content.Intent;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.base.base.IPresenter;
import com.thfw.base.models.TalkModel;
import com.thfw.robotheart.R;
import com.thfw.robotheart.view.TitleRobotView;
import com.thfw.ui.base.RobotBaseActivity;

public class AiTalkActivity extends RobotBaseActivity {


    private com.thfw.robotheart.view.TitleRobotView mTitleRobotView;
    private androidx.constraintlayout.widget.ConstraintLayout mClAnim;
    private androidx.recyclerview.widget.RecyclerView mRvList;

    public static void startActivity(Context context, TalkModel talkModel) {
        context.startActivity(new Intent(context, AiTalkActivity.class).putExtra(KEY_DATA, talkModel));
    }


    @Override
    public int getContentView() {
        return R.layout.activity_ai_talk;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initView() {

        mTitleRobotView = (TitleRobotView) findViewById(R.id.titleRobotView);
        mClAnim = (ConstraintLayout) findViewById(R.id.cl_anim);
        mRvList = (RecyclerView) findViewById(R.id.rv_list);
    }

    @Override
    public void initData() {

        TalkModel talkModel = (TalkModel) getIntent().getSerializableExtra(KEY_DATA);

        mTitleRobotView.setCenterText(talkModel.getTitle());

    }
}