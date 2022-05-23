package com.thfw.robotheart.fragments.help;

import android.content.Intent;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.text.HtmlCompat;

import com.thfw.base.face.SimpleUpgradeStateListener;
import com.thfw.base.models.AboutUsModel;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.OtherPresenter;
import com.thfw.base.utils.BuglyUtil;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.base.utils.GsonUtil;
import com.thfw.base.utils.SharePreferenceUtil;
import com.thfw.base.utils.ToastUtil;
import com.thfw.base.utils.Util;
import com.thfw.robotheart.R;
import com.thfw.robotheart.activitys.RobotBaseFragment;
import com.thfw.robotheart.activitys.set.SystemAppActivity;
import com.thfw.ui.dialog.LoadingDialog;
import com.trello.rxlifecycle2.LifecycleProvider;

public class AboutMeFragment extends RobotBaseFragment<OtherPresenter> implements OtherPresenter.OtherUi<AboutUsModel> {

    private static final String KEY_ABOUT = "key.about.me";
    private TextView mTvHint;
    private TextView mTvVersion;
    private LinearLayout mLlCheckVersion;
    private TextView mTvCheckVersion;
    private TextView mTvCompanyAddress;

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
        mTvCompanyAddress = (TextView) findViewById(R.id.tv_company_net_address);
        mTvVersion = (TextView) findViewById(R.id.tv_version);
        mLlCheckVersion = (LinearLayout) findViewById(R.id.ll_check_version);
        mTvCheckVersion = (TextView) findViewById(R.id.tv_check_version);
    }

    @Override
    public void initData() {

        mTvVersion.setText("V" + Util.getAppVersion(mContext));
        mTvCheckVersion.setOnClickListener(v -> {
            checkVersion();
        });
        AboutUsModel aboutUsModel = SharePreferenceUtil.getObject(KEY_ABOUT, AboutUsModel.class);
        onSuccess(aboutUsModel);
        mPresenter.onGetAboutUs();
    }

    @Override
    public LifecycleProvider getLifecycleProvider() {
        return this;
    }

    @Override
    public void onSuccess(AboutUsModel data) {
        if (data == null) {
            return;
        }
        SharePreferenceUtil.setString(KEY_ABOUT, GsonUtil.toJson(data));
        String hint = "<font color='#91dff3'>产品概述：</font>"
                + data.getContent();
        mTvHint.setText(HtmlCompat.fromHtml(hint, HtmlCompat.FROM_HTML_MODE_LEGACY));

        String companyHttp = "<font color='#91dff3'>公司官网：</font>"
                + data.getCompanyUrl();
        mTvCompanyAddress.setText(HtmlCompat.fromHtml(companyHttp, HtmlCompat.FROM_HTML_MODE_LEGACY));

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
                LoadingDialog.hide();
                Log.d("requestNewVersion", "hasNewVersion = " + hasNewVersion);
                if (EmptyUtil.isEmpty(AboutMeFragment.this)) {
                    return;
                }
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
        BuglyUtil.requestNewVersion(null);
        LoadingDialog.hide();
    }
}