package com.thfw.mobileheart.activity.test;

import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.just.agentweb.AgentWeb;
import com.just.agentweb.AgentWebConfig;
import com.just.agentweb.DefaultWebClient;
import com.just.agentweb.WebViewClient;
import com.thfw.base.face.OnRvItemListener;
import com.thfw.base.models.AudioEtcModel;
import com.thfw.base.models.TalkModel;
import com.thfw.base.models.TestResultModel;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.TestPresenter;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.base.utils.HandlerUtil;
import com.thfw.base.utils.ToastUtil;
import com.thfw.mobileheart.R;
import com.thfw.mobileheart.activity.BaseActivity;
import com.thfw.mobileheart.activity.audio.AudioPlayerActivity;
import com.thfw.mobileheart.activity.read.BookDetailActivity;
import com.thfw.mobileheart.activity.talk.ChatActivity;
import com.thfw.mobileheart.activity.video.VideoPlayActivity;
import com.thfw.mobileheart.adapter.TestRecommendAdapter;
import com.thfw.ui.widget.TitleView;
import com.trello.rxlifecycle2.LifecycleProvider;

import java.util.List;

/**
 * 测评结果
 * Created By jishuaipeng on 2021/12/29
 */
public class TestResultWebActivity extends BaseActivity<TestPresenter> implements TestPresenter.TestUi<TestResultModel> {

    private static final String KEY_URL = "key.url";
    private static final String KEY_TITLE = "key.title";
    public AgentWeb mAgentWeb;
    private String url;
    private String title;
    private TitleView tvTitleView;
    private RecyclerView mRvCommend;
    private TestResultModel testResultModel;
    private TitleView mTitleView;
    private FrameLayout mFlWebContent;
    private android.widget.TextView mTvRecommendTitle;
    private RecyclerView mRvRecommend;
    private android.widget.Button mBtTryOne;
    private android.widget.Button mBtHistory;
    private int mTestId;


    public static void startActivity(Context context, TestResultModel testResultModel) {
        context.startActivity(new Intent(context, TestResultWebActivity.class)
                .putExtra(KEY_DATA, testResultModel)
                .putExtra(KEY_URL, "https://resource.soulbuddy.cn/public/soul_the_land/depth_result.html?id=" + testResultModel.getResultId())
                .putExtra(KEY_TITLE, "测评结果"));
    }

    @Override
    public int getContentView() {
        return R.layout.activity_test_result_webview;
    }

    @Override
    public TestPresenter onCreatePresenter() {
        return new TestPresenter(this);
    }

    @Override
    public void initView() {
        tvTitleView = findViewById(R.id.titleView);
        mRvCommend = findViewById(R.id.rv_recommend);
        mRvCommend.setHasFixedSize(true);
        mRvCommend.setLayoutManager(new GridLayoutManager(mContext, 2));

        mTitleView = (TitleView) findViewById(R.id.titleView);
        mFlWebContent = (FrameLayout) findViewById(R.id.fl_web_content);
        mTvRecommendTitle = (TextView) findViewById(R.id.tv_recommend_title);
        mRvRecommend = (RecyclerView) findViewById(R.id.rv_recommend);
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
        });
        mBtHistory.setOnClickListener(v -> {
            TestReportActivity.startActivity(mContext, mTestId);
        });
        initAgentWeb(findViewById(R.id.fl_web_content));
        tvTitleView.setCenterText(title);


    }

    private void initAgentWeb(FrameLayout frameLayout) {
        mAgentWeb = AgentWeb.with(this)//
                .setAgentWebParent(frameLayout, -1, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))// 传入AgentWeb的父控件。
                .useDefaultIndicator(-1, 3)// 设置进度条颜色与高度，-1为默认值，高度为2，单位为dp。
                .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK) // 严格模式 Android 4.2.2 以下会放弃注入对象 ，使用AgentWebView没影响。
                .setMainFrameErrorView(R.layout.agentweb_error_page, -1) // 参数1是错误显示的布局，参数2点击刷新控件ID -1表示点击整个布局都刷新， AgentWeb 3.0.0 加入。
                .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.DISALLOW)// 打开其他页面时，弹窗质询用户前往其他应用 AgentWeb 3.0.0 加入。
                .interceptUnkownUrl() // 拦截找不到相关页面的Url AgentWeb 3.0.0 加入。
                .setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        if (testResultModel != null) {
                            findViewById(R.id.sv_view).scrollTo(0, 0);
                            // 解决 推荐内容 先出现闪烁的问题
                            HandlerUtil.getMainHandler().postDelayed(() -> {
                                if (testResultModel.getRecommendInfo() != null) {
                                    setRecommend();
                                } else {
                                    mPresenter.onGetResult(testResultModel.getResultId());
                                }
                            }, 500);
                        }
                    }
                })
                .createAgentWeb()// 创建AgentWeb。
                .ready()// 设置 WebSettings。
                .go(url); // WebView载入该url地址的页面并显示。

        AgentWebConfig.debug();

        // AgentWeb 4.0 开始，删除该类以及删除相关的API
        //  DefaultMsgConfig.DownloadMsgConfig mDownloadMsgConfig = mAgentWeb.getDefaultMsgConfig().getDownloadMsgConfig();
        //  mDownloadMsgConfig.setCancel("放弃");  // 修改下载提示信息，这里可以语言切换

        // AgentWeb 没有把WebView的功能全面覆盖 ，所以某些设置 AgentWeb 没有提供 ， 请从WebView方面入手设置。
        mAgentWeb.getWebCreator().getWebView().setOverScrollMode(WebView.OVER_SCROLL_NEVER);
        // mAgentWeb.getWebCreator().getWebView()  获取WebView .

        // mAgentWeb.getWebCreator().getWebView().setOnLongClickListener();


        mAgentWeb.getWebCreator().getWebView().getSettings().setJavaScriptEnabled(true);
        mAgentWeb.getWebCreator().getWebView().setLayerType(View.LAYER_TYPE_NONE, null);
        // 优先使用网络
        mAgentWeb.getWebCreator().getWebView().getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        // 将图片调整到适合webview的大小
        mAgentWeb.getWebCreator().getWebView().getSettings().setUseWideViewPort(true);
        // 支持内容重新布局
        mAgentWeb.getWebCreator().getWebView().getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        // 支持自动加载图片
        mAgentWeb.getWebCreator().getWebView().getSettings().setLoadsImagesAutomatically(true);
        // 当webview调用requestFocus时为webview设置节点
        mAgentWeb.getWebCreator().getWebView().getSettings().setNeedInitialFocus(true);
        // 自适应屏幕
        mAgentWeb.getWebCreator().getWebView().getSettings().setUseWideViewPort(true);
        mAgentWeb.getWebCreator().getWebView().getSettings().setLoadWithOverviewMode(true);
        // 开启DOM storage API功能（HTML5 提供的一种标准的接口，主要将键值对存储在本地，在页面加载完毕后可以通过 javascript 来操作这些数据。）
        mAgentWeb.getWebCreator().getWebView().getSettings().setDomStorageEnabled(true);
        // 支持缩放
        mAgentWeb.getWebCreator().getWebView().getSettings().setBuiltInZoomControls(true);
        mAgentWeb.getWebCreator().getWebView().getSettings().setSupportZoom(true);
        // 允许webview对文件的操作
        mAgentWeb.getWebCreator().getWebView().getSettings().setAllowFileAccess(true);
        mAgentWeb.getWebCreator().getWebView().getSettings().setAllowFileAccessFromFileURLs(true);
        mAgentWeb.getWebCreator().getWebView().getSettings().setAllowUniversalAccessFromFileURLs(true);
        mAgentWeb.getWebCreator().getWebView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK && mAgentWeb.getWebCreator().getWebView().canGoBack()) { // 表示按返回键时的操作
                        mAgentWeb.getWebCreator().getWebView().goBack(); // 后退
                        // webview.goForward();// 前进
                        return true; // 已处理
                    } else if (keyCode == KeyEvent.KEYCODE_BACK) {
                        finish();
                    }
                }
                return false;
            }
        });


    }

    @Override
    public void onResume() {
        mAgentWeb.getWebLifeCycle().onResume();// 恢复
        super.onResume();
    }

    @Override
    public void onPause() {
        mAgentWeb.getWebLifeCycle().onPause(); // 暂停应用内所有WebView,调用mWebView.resumeTimers();/mAgentWeb.getWebLifeCycle().onResume(); 恢复。
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAgentWeb.getWebLifeCycle().onDestroy();
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
        if (testResultModel == null || mRvCommend.getAdapter() != null) {
            return;
        }
        if (!EmptyUtil.isEmpty(testResultModel.getRecommendInfo())) {
            findViewById(R.id.tv_recommend_title).setVisibility(View.VISIBLE);
            mRvCommend.setFocusable(false);
            mRvCommend.setVisibility(View.VISIBLE);
        }
        TestRecommendAdapter recommendAdapter = new TestRecommendAdapter(testResultModel.getRecommendInfo());
        recommendAdapter.setOnRvItemListener(new OnRvItemListener<TestResultModel.RecommendInfoBean>() {
            @Override
            public void onItemClick(List<TestResultModel.RecommendInfoBean> list, int position) {
                TestResultModel.RecommendInfoBean infoBean = list.get(position);
                if (infoBean == null || infoBean.getInfo() == null) {
                    ToastUtil.show("数据异常");
                    return;
                }
                switch (infoBean.getType()) {
                    case 1: // 测评
                        TestBeginActivity.startActivity(mContext, infoBean.getInfo().getId());
                        break;
                    case 2: // 主题对话
                        ChatActivity.startActivity(mContext, new TalkModel(TalkModel.TYPE_SPEECH_CRAFT)
                                .setId(infoBean.getInfo().getId()));
                        break;
                    case 3: // 音频
                        AudioEtcModel audioEtcModel = new AudioEtcModel();
                        audioEtcModel.setTitle(infoBean.getInfo().getTitle());
                        audioEtcModel.setImg(infoBean.getInfo().getPic());
                        audioEtcModel.setId(infoBean.getInfo().getId());
                        AudioPlayerActivity.startActivity(mContext, audioEtcModel);
                        break;
                    case 4: // 视频
                        VideoPlayActivity.startActivity(mContext, infoBean.getInfo().getId(), false);
                        break;
                    case 5: // 科普文章
                        BookDetailActivity.startActivity(mContext, infoBean.getInfo().getId());
                        break;

                }
            }
        });
        mRvCommend.setAdapter(recommendAdapter);
    }

    @Override
    public void onFail(ResponeThrowable throwable) {

    }
}