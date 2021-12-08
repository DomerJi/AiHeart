package com.thfw.mobileheart.activity.settings;

import android.content.Intent;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.base.base.IPresenter;
import com.thfw.base.face.OnRvItemListener;
import com.thfw.mobileheart.adapter.HelpBackAdapter;
import com.thfw.base.models.HelpBackModel;
import com.thfw.mobileheart.R;
import com.thfw.ui.base.BaseActivity;
import com.thfw.ui.widget.TitleView;

import java.util.List;

public class HelpBackActivity extends BaseActivity {

    private com.thfw.ui.widget.TitleView mTitleView;
    private android.widget.TextView mTvMeBack;
    private androidx.recyclerview.widget.RecyclerView mRvHelpHints;

    @Override
    public int getContentView() {
        return R.layout.activity_help_back;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initView() {

        mTitleView = (TitleView) findViewById(R.id.titleView);
        mTvMeBack = (TextView) findViewById(R.id.tv_me_back);
        mRvHelpHints = (RecyclerView) findViewById(R.id.rv_help_hints);
        mTvMeBack.setOnClickListener(v -> {
            startActivity(new Intent(mContext, MeWillHelpBackActivity.class));
        });
    }

    @Override
    public void initData() {
        mRvHelpHints.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        HelpBackAdapter helpBackAdapter = new HelpBackAdapter(null);
        helpBackAdapter.setOnRvItemListener(new OnRvItemListener<HelpBackModel>() {
            @Override
            public void onItemClick(List<HelpBackModel> list, int position) {
                startActivity(new Intent(mContext, HelpHintsActivity.class));
            }
        });
        mRvHelpHints.setAdapter(helpBackAdapter);
    }
}