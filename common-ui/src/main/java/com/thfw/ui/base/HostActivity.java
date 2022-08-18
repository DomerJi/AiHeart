package com.thfw.ui.base;

import android.graphics.Color;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thfw.base.base.IPresenter;
import com.thfw.base.net.ApiHost;
import com.thfw.base.utils.SharePreferenceUtil;
import com.thfw.ui.R;
import com.thfw.ui.widget.TitleView;

import static com.thfw.base.net.ApiHost.KEY_CUSTOM_HOST;
import static com.thfw.base.net.ApiHost.KEY_SELECTED_HOST;

public class HostActivity extends IBaseActivity {

    private com.thfw.ui.widget.TitleView mTitleView;
    private android.widget.LinearLayout mRgHost;
    private android.widget.TextView mTvOnline;
    private android.widget.TextView mTvTest;
    private android.widget.EditText mEtCustom;




    @Override
    public int getContentView() {
        return R.layout.activity_host;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initView() {

        mTitleView = (TitleView) findViewById(R.id.titleView);
        mRgHost = (LinearLayout) findViewById(R.id.rg_host);
        mTvOnline = (TextView) findViewById(R.id.tv_online);
        mTvTest = (TextView) findViewById(R.id.tv_test);
        mEtCustom = (EditText) findViewById(R.id.et_custom);
    }

    @Override
    public void initData() {

        mTvOnline.setText(ApiHost.ONLINE_HOST);
        mTvTest.setText(ApiHost.TEST_HOST);
        onHostChange(true);
        mTvTest.setOnClickListener(v -> {
            SharePreferenceUtil.setString(KEY_SELECTED_HOST, ApiHost.TEST_HOST);
            onHostChange(false);
        });

        mTvOnline.setOnClickListener(v -> {
            SharePreferenceUtil.setString(KEY_SELECTED_HOST, ApiHost.ONLINE_HOST);
            onHostChange(false);
        });

        mEtCustom.setOnClickListener(v -> {
            String host = mEtCustom.getText().toString();
            if (!TextUtils.isEmpty(host) && host.startsWith("http") && host.length() > 10) {
                SharePreferenceUtil.setString(KEY_SELECTED_HOST, host);
            } else {
                SharePreferenceUtil.setString(KEY_SELECTED_HOST, ApiHost.ONLINE_HOST);
            }
            onHostChange(false);
        });
        mEtCustom.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String host = s.toString();
                if (!TextUtils.isEmpty(host) && host.startsWith("http") && host.length() > 10) {
                    SharePreferenceUtil.setString(KEY_SELECTED_HOST, host);
                    SharePreferenceUtil.setString(KEY_CUSTOM_HOST, host);
                } else {
                    SharePreferenceUtil.setString(KEY_SELECTED_HOST, ApiHost.ONLINE_HOST);
                }
                onHostChange(false);
            }
        });

    }

    private void onHostChange(boolean setEt) {
        String currentHost = SharePreferenceUtil.getString(KEY_SELECTED_HOST, ApiHost.ONLINE_HOST);

        if (TextUtils.isEmpty(currentHost)) {
            SharePreferenceUtil.setString(KEY_SELECTED_HOST, ApiHost.ONLINE_HOST);
            currentHost = ApiHost.ONLINE_HOST;
        }
        ApiHost.setCurrentHost(currentHost);
        if (setEt) {
            String customHost = SharePreferenceUtil.getString(KEY_CUSTOM_HOST, "");
            if (!TextUtils.isEmpty(customHost)) {
                mEtCustom.setText(customHost);
            }
        }
        if (ApiHost.ONLINE_HOST.equals(currentHost)) {
            mTvOnline.setBackgroundColor(Color.GRAY);
            mTvTest.setBackgroundColor(Color.WHITE);
            mEtCustom.setBackgroundColor(Color.WHITE);
        } else if (ApiHost.TEST_HOST.equals(currentHost)) {
            mTvTest.setBackgroundColor(Color.GRAY);
            mTvOnline.setBackgroundColor(Color.WHITE);
            mEtCustom.setBackgroundColor(Color.WHITE);
        } else {
            mTvOnline.setBackgroundColor(Color.WHITE);
            mTvTest.setBackgroundColor(Color.WHITE);
            mEtCustom.setBackgroundColor(Color.GRAY);
            if (setEt) {
                mEtCustom.setText(currentHost);
            }
        }
    }
}