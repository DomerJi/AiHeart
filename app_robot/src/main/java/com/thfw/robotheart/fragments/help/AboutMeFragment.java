package com.thfw.robotheart.fragments.help;

import android.content.Intent;
import android.text.Html;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thfw.base.face.SimpleUpgradeStateListener;
import com.thfw.base.models.AboutUsModel;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.OtherPresenter;
import com.thfw.base.utils.BuglyUtil;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.base.utils.ToastUtil;
import com.thfw.base.utils.Util;
import com.thfw.robotheart.R;
import com.thfw.robotheart.activitys.RobotBaseFragment;
import com.thfw.robotheart.activitys.set.SystemAppActivity;
import com.thfw.ui.dialog.LoadingDialog;
import com.trello.rxlifecycle2.LifecycleProvider;

public class AboutMeFragment extends RobotBaseFragment<OtherPresenter> implements OtherPresenter.OtherUi<AboutUsModel> {

    private TextView mTvHint;
    private TextView mTvVersion;
    private LinearLayout mLlCheckVersion;
    private TextView mTvCheckVersion;

    @Override
    public int getContentView() {
        return R.layout.fragment_about_me;
    }

    @Override
    public OtherPresenter onCreatePresenter() {
        return new OtherPresenter(this);
    }

    @Override
    public void initView() {

        mTvHint = (TextView) findViewById(R.id.tv_hint);
        mTvVersion = (TextView) findViewById(R.id.tv_version);
        mLlCheckVersion = (LinearLayout) findViewById(R.id.ll_check_version);
        mTvCheckVersion = (TextView) findViewById(R.id.tv_check_version);
    }

    @Override
    public void initData() {

        mPresenter.onGetAboutUs();
        mTvVersion.setText("V" + Util.getAppVersion(mContext));
        mTvCheckVersion.setOnClickListener(v -> {
            checkVersion();
        });
    }

    @Override
    public LifecycleProvider getLifecycleProvider() {
        return this;
    }

    @Override
    public void onSuccess(AboutUsModel data) {
        String hint = "<font color='#91dff3'>产品概述：</font>"
                + data.getContent();
        mTvHint.setText(Html.fromHtml(hint));
    }

    /**
     * 检查版本更新
     */
    private void checkVersion() {
        LoadingDialog.show(getActivity(), "正在检查");
        BuglyUtil.requestNewVersion(new SimpleUpgradeStateListener() {
            @Override
            public void onVersion(boolean hasNewVersion) {
                super.onVersion(hasNewVersion);
                Log.d("requestNewVersion", "hasNewVersion = " + hasNewVersion);
                if (EmptyUtil.isEmpty(AboutMeFragment.this)) {
                    return;
                }
                LoadingDialog.hide();
                if (hasNewVersion) {
                    startActivity(new Intent(mContext, SystemAppActivity.class));
                } else {
                    ToastUtil.show("已经是最新版本");
                }
            }
        });
    }

    @Override
    public void onFail(ResponeThrowable throwable) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LoadingDialog.hide();
    }
}