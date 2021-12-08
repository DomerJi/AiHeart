package com.thfw.robotheart.activitys.me;

import android.widget.EditText;

import com.thfw.base.base.IPresenter;
import com.thfw.base.face.MyTextWatcher;
import com.thfw.base.net.CommonParameter;
import com.thfw.robotheart.R;
import com.thfw.robotheart.view.TitleRobotView;
import com.thfw.ui.base.RobotBaseActivity;

public class PrivateSetActivity extends RobotBaseActivity {

    private com.thfw.robotheart.view.TitleRobotView mTitleRobotView;
    private android.widget.EditText mEtOrganzitionId;

    @Override
    public int getContentView() {
        return R.layout.activity_private_set;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initView() {

        mTitleRobotView = (TitleRobotView) findViewById(R.id.titleRobotView);

        mEtOrganzitionId = (EditText) findViewById(R.id.et_organzition_id);
        mEtOrganzitionId.setText(CommonParameter.getOrganizationId());
    }

    @Override
    public void initData() {
        mEtOrganzitionId.addTextChangedListener(new MyTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                CommonParameter.setOrganizationId(s.toString());
            }
        });
    }
}