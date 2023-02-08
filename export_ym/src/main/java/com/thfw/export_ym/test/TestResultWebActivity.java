package com.thfw.export_ym.test;

import android.content.Context;
import android.content.Intent;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;

import com.thfw.base.YmBaseActivity;
import com.thfw.export_ym.R;
import com.thfw.models.TestResultModel;
import com.thfw.net.ResponeThrowable;
import com.thfw.presenter.TestPresenter;
import com.thfw.view.TitleView;
import com.trello.rxlifecycle2.LifecycleProvider;

/**
 * 测评结果
 * Created By jishuaipeng on 2021/12/29
 */
public class TestResultWebActivity extends YmBaseActivity<TestPresenter> implements TestPresenter.TestUi<TestResultModel> {

    private static final String KEY_URL = "key.url";
    private static final String KEY_TITLE = "key.title";
    private String url;
    private String title;
    private TitleView tvTitleView;
    private TestResultModel testResultModel;
    private TitleView mTitleView;
    private FrameLayout mFlWebContent;
    private Button mBtTryOne;
    private Button mBtHistory;
    private int mTestId;


    public static void startActivity(Context context, TestResultModel testResultModel) {
        context.startActivity(new Intent(context, TestResultWebActivity.class).putExtra(KEY_DATA, testResultModel).putExtra(KEY_URL, "https://resource.soulbuddy.cn/public/soul_the_land/depth_result.html?id=" + testResultModel.getResultId()).putExtra(KEY_TITLE, "测评结果"));
    }

    @Override
    public int getContentView() {
        return R.layout.activity_test_result_webview_ym;
    }

    @Override
    public TestPresenter onCreatePresenter() {
        return new TestPresenter(this);
    }

    @Override
    public void initView() {
        tvTitleView = findViewById(R.id.titleView);

        mTitleView = (TitleView) findViewById(R.id.titleView);
        mFlWebContent = (FrameLayout) findViewById(R.id.fl_web_content);
        mBtTryOne = (Button) findViewById(R.id.bt_try_one);
        mBtHistory = (Button) findViewById(R.id.bt_history);
    }

    @Override
    public void initData() {
        url = getIntent().getStringExtra(KEY_URL);
        title = getIntent().getStringExtra(KEY_TITLE);
        testResultModel = (TestResultModel) getIntent().getSerializableExtra(KEY_DATA);
        mTestId = testResultModel.getTestId();
        mBtTryOne.setOnClickListener(v -> {
            TestBeginActivity.startActivity(mContext, mTestId);
            finish();
        });
        mBtHistory.setOnClickListener(v -> {
            TestReportActivity.startActivity(mContext, mTestId);
            finish();
        });
        tvTitleView.setCenterText(title);

        initAgentWeb();
    }

    private void initAgentWeb() {
        WebView webView = findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (testResultModel != null) {
                    if (testResultModel.getRecommendInfo() != null) {
                        setRecommend();
                    } else {
                        mPresenter.onGetResult(testResultModel.getResultId());
                    }
                }
            }
        });
        webView.loadUrl(url);
    }


    @Override
    public LifecycleProvider getLifecycleProvider() {
        return this;
    }

    @Override
    public void onSuccess(TestResultModel data) {
        if (data != null) {
            testResultModel = data;
            setRecommend();
        }
    }

    private void setRecommend() {
        if (testResultModel == null) {
            return;
        }
    }

    @Override
    public void onFail(ResponeThrowable throwable) {

    }
}