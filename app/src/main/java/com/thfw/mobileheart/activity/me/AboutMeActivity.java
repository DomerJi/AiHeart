package com.thfw.mobileheart.activity.me;

import android.content.Context;
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
import com.thfw.mobileheart.MyApplication;
import com.thfw.mobileheart.R;
import com.thfw.mobileheart.activity.BaseActivity;
import com.thfw.mobileheart.activity.WebActivity;
import com.thfw.mobileheart.activity.settings.SystemAppActivity;
import com.thfw.ui.dialog.LoadingDialog;
import com.trello.rxlifecycle2.LifecycleProvider;

public class AboutMeActivity extends BaseActivity<OtherPresenter> implements OtherPresenter.OtherUi<AboutUsModel> {

    private TextView mTvHint;
    private TextView mTvVersion;
    private LinearLayout mLlCheckVersion;
    private TextView mTvCheckVersion;
    private TextView mTvCompanyAddress;

    private static final String KEY_ABOUT = "key.about.me";
    private TextView mTvCopy;

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, AboutMeActivity.class));
    }

    @Override
    public int getContentView() {
        return R.layout.activity_about_me;
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
        mTvCopy = (TextView) findViewById(R.id.tv_copy);
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
        String hint = "<font color='#0000FF'>产品概述：</font>"
                + data.getContent();
        mTvHint.setText(HtmlCompat.fromHtml(hint, HtmlCompat.FROM_HTML_MODE_LEGACY));

        String companyHttp = "<font color='#0000FF'>公司官网：</font>"
                + "<u>" + data.getCompanyUrl() + "</u>";

        mTvCompanyAddress.setText(HtmlCompat.fromHtml(companyHttp, HtmlCompat.FROM_HTML_MODE_LEGACY));
        mTvCompanyAddress.setOnClickListener(v -> {
            WebActivity.startActivity(mContext, data.getCompanyUrl(), "公司官网");
        });
        mTvCopy.setOnClickListener(v -> {
            MyApplication.copy(data.getCompanyUrl());
        });
    }

    /**
     * 检查版本更新
     */
    private void checkVersion() {
        LoadingDialog.show(AboutMeActivity.this, "正在检查");
        BuglyUtil.requestNewVersion(new SimpleUpgradeStateListener() {
            @Override
            public void onVersion(boolean hasNewVersion) {
                super.onVersion(hasNewVersion);
                LoadingDialog.hide();
                Log.d("requestNewVersion", "hasNewVersion = " + hasNewVersion);
                if (EmptyUtil.isEmpty(AboutMeActivity.this)) {
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