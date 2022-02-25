package com.thfw.robotheart.activitys.text;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.just.agentweb.AgentWeb;
import com.just.agentweb.AgentWebConfig;
import com.just.agentweb.DefaultWebClient;
import com.thfw.base.api.HistoryApi;
import com.thfw.base.models.BookDetailModel;
import com.thfw.base.models.ChatEntity;
import com.thfw.base.models.CommonModel;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.BookPresenter;
import com.thfw.base.presenter.HistoryPresenter;
import com.thfw.base.utils.ToastUtil;
import com.thfw.robotheart.R;
import com.thfw.robotheart.activitys.RobotBaseActivity;
import com.thfw.robotheart.constants.UIConfig;
import com.thfw.robotheart.util.WebSizeUtil;
import com.thfw.robotheart.view.TitleRobotView;
import com.thfw.ui.widget.LoadingView;
import com.trello.rxlifecycle2.LifecycleProvider;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Iterator;

public class BookDetailActivity extends RobotBaseActivity<BookPresenter> implements BookPresenter.BookUi<BookDetailModel> {

    private AgentWeb mAgentWeb;
    private String contentHtml;
    private String fontSize;
    private String titleHtml;
    private String title;
    private String url;
    private com.thfw.robotheart.view.TitleRobotView mTitleRobotView;
    private FrameLayout mFlWebContent;
    private com.thfw.ui.widget.LoadingView mLoadingView;
    private android.widget.LinearLayout mLlCollect;
    private android.widget.ImageView mIvCollect;
    private boolean requestIng = false;
    private int bookId;

    public static void startActivity(Context context, int id) {
        ((Activity) context).startActivityForResult(new Intent(context, BookDetailActivity.class)
                .putExtra(KEY_DATA, id), ChatEntity.TYPE_RECOMMEND_TEXT);
    }

    @Override
    public int getContentView() {
        return R.layout.activity_book_detail;
    }

    @Override
    public BookPresenter onCreatePresenter() {
        return new BookPresenter(this);
    }

    @Override
    public void initView() {

        mTitleRobotView = (TitleRobotView) findViewById(R.id.titleRobotView);
        mFlWebContent = (FrameLayout) findViewById(R.id.fl_web_content);
        mLoadingView = (LoadingView) findViewById(R.id.loadingView);
        mLlCollect = (LinearLayout) findViewById(R.id.ll_collect);
        mIvCollect = (ImageView) findViewById(R.id.iv_collect);
    }

    @Override
    public void initData() {
        int mBookId = getIntent().getIntExtra(KEY_DATA, -1);
        if (mBookId == -1) {
            ToastUtil.show("参数错误");
            finish();
            return;
        }

        mPresenter.getArticleInfo(mBookId);
    }


    public void initHtmlData() {
        /**
         * <h2 align="center">标题</h2>
         * <p style="text-align:center; color:#999;">000000000000000见到大家</p>
         */

        fontSize = "font-size:20px";
//        titleHtml = "<h2 align=\"center\">" + title + "</h2>"
//                + "<p style=\"text-align:center; color:#666;\">作者:强军密友</p>";
        titleHtml = "<h2 align=\"center\">" + title + "</h2>";
        if (TextUtils.isEmpty(contentHtml)) {
            return;
        }

        if (!contentHtml.startsWith("<html>")) {
//            contentHtml = newHtml();
            contentHtml = "<html><head>" + "<style>img {\n" +
                    "  display: block;\n" +
                    "  margin-left: auto;\n" +
                    "  margin-right: auto;\n" +
                    "}" + "body{padding-top:20px; padding-bottom:20px; padding-left:20px;padding-right:20px;}"
//                    + "span{" + fontSize + "}"
//                    + "p{" + fontSize + "}"
                    + "</style>" + "<title>"
                    + title + "</title></head><body>" + titleHtml
                    + contentHtml + "</body></html>";
        }
        mLlCollect.setOnClickListener(v -> {
            addCollect();
        });
        initAgentWeb((FrameLayout) findViewById(R.id.fl_web_content));
    }

    /**
     * Jsoup更改样式
     *
     * @return
     */
    private String newHtml() {
        Document document = Jsoup.parse(contentHtml);

        Elements esd = document.select("[style]");
        Iterator<Element> iterator = esd.iterator();
        while (iterator.hasNext()) {
            Element etemp = iterator.next();
            String styleStr = etemp.attr("style");
            etemp.removeAttr("style");
            etemp.attr("style", cssStr(styleStr));
        }

        return document.toString();
    }

    /**
     * 字体大小修改
     *
     * @param str
     * @return
     */
    public String cssStr(String str) {
        if (!str.contains("font-size")) {
            return str;
        }
        String s1 = str.substring(0, str.indexOf("font-size"));
        String s2 = str.substring(str.indexOf("font-size"), str.length());
        if (s2.indexOf(";") != -1) {
            String s3 = s2.substring(s2.indexOf(";"));
            return s1 + fontSize + s3;
        }

        return str;
    }

    /**
     * 初始化WebView
     *
     * @param frameLayout
     */
    private void initAgentWeb(FrameLayout frameLayout) {
        AgentWeb.PreAgentWeb preAgentWeb = AgentWeb.with(this)//
                .setAgentWebParent(frameLayout, -1, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))// 传入AgentWeb的父控件。
                .useDefaultIndicator(-1, 3)// 设置进度条颜色与高度，-1为默认值，高度为2，单位为dp。
                .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK) // 严格模式 Android 4.2.2 以下会放弃注入对象 ，使用AgentWebView没影响。
                .setMainFrameErrorView(R.layout.agentweb_error_page, -1) // 参数1是错误显示的布局，参数2点击刷新控件ID -1表示点击整个布局都刷新， AgentWeb 3.0.0 加入。
                .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.DISALLOW)// 打开其他页面时，弹窗质询用户前往其他应用 AgentWeb 3.0.0 加入。
                .interceptUnkownUrl() // 拦截找不到相关页面的Url AgentWeb 3.0.0 加入。
                .createAgentWeb()// 创建AgentWeb。
                .ready();// 设置 WebSettings。
        if (!TextUtils.isEmpty(contentHtml)) {
            mAgentWeb = preAgentWeb.get();
            Log.d("contentHtml", "contentHtml = " + contentHtml);
            WebView webView = mAgentWeb.getWebCreator().getWebView();
            frameLayout.setBackgroundColor(Color.WHITE);
            webView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_INSET);
            webView.getSettings().setDefaultTextEncodingName("UTF-8");//设置默认为utf-8
            WebSizeUtil.setSize(mContext, webView.getSettings());
            webView.loadDataWithBaseURL(null, contentHtml, "text/html", "UTF-8", null);//中文解码  
        } else {
            mAgentWeb = preAgentWeb.go(url); // WebView载入该url地址的页面并显示。
        }
        AgentWebConfig.debug();

        // AgentWeb 4.0 开始，删除该类以及删除相关的API
        //  DefaultMsgConfig.DownloadMsgConfig mDownloadMsgConfig = mAgentWeb.getDefaultMsgConfig().getDownloadMsgConfig();
        //  mDownloadMsgConfig.setCancel("放弃");  // 修改下载提示信息，这里可以语言切换

        // AgentWeb 没有把WebView的功能全面覆盖 ，所以某些设置 AgentWeb 没有提供 ， 请从WebView方面入手设置。
        mAgentWeb.getWebCreator().getWebView().setOverScrollMode(WebView.OVER_SCROLL_NEVER);
        // mAgentWeb.getWebCreator().getWebView()  获取WebView .

        // mAgentWeb.getWebCreator().getWebView().setOnLongClickListener();


        mAgentWeb.getWebCreator().getWebView().getSettings().setJavaScriptEnabled(true);
        //  webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
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
        mAgentWeb.getWebCreator().getWebView().getSettings().setBuiltInZoomControls(false);
        mAgentWeb.getWebCreator().getWebView().getSettings().setSupportZoom(false);

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
                        moveTaskToBack(true);
                    }
                }
                return false;
            }
        });
    }

    @Override
    public void onResume() {
        if (mAgentWeb != null) {
            mAgentWeb.getWebLifeCycle().onResume();// 恢复
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        if (mAgentWeb != null) {
            mAgentWeb.getWebLifeCycle().onPause(); // 暂停应用内所有WebView,调用mWebView.resumeTimers();/mAgentWeb.getWebLifeCycle().onResume(); 恢复。

        }
        super.onPause();
    }

    @Override
    public void finish() {
        setResult(RESULT_OK);
        super.finish();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAgentWeb != null) {
            mAgentWeb.getWebLifeCycle().onDestroy();
        }
    }

    @Override
    public LifecycleProvider getLifecycleProvider() {
        return this;
    }

    @Override
    public void onSuccess(BookDetailModel data) {
        if (data != null) {
            mLoadingView.hide();
            title = data.getTitle();
            contentHtml = data.getContent();
            bookId = data.getId();
            mIvCollect.setSelected(data.isCollected());
            initHtmlData();
        } else {
            mLoadingView.showFail(v -> {
                initData();
            });
        }

    }

    @Override
    public void onFail(ResponeThrowable throwable) {
        mLoadingView.showFail(v -> {
            initData();
        });
    }

    public void addCollect() {
        if (requestIng) {
            return;
        }
        requestIng = true;
        mIvCollect.setSelected(!mIvCollect.isSelected());
        new HistoryPresenter(new HistoryPresenter.HistoryUi<CommonModel>() {
            @Override
            public LifecycleProvider getLifecycleProvider() {
                return BookDetailActivity.this;
            }

            @Override
            public void onSuccess(CommonModel data) {
                requestIng = false;
                ToastUtil.show(mIvCollect.isSelected() ? UIConfig.COLLECTED : UIConfig.COLLECTED_UN);
            }

            @Override
            public void onFail(ResponeThrowable throwable) {
                requestIng = false;
                ToastUtil.show(mIvCollect.isSelected() ? "收藏失败" : "取消收藏失败");
                mIvCollect.setSelected(!mIvCollect.isSelected());
            }
        }).addCollect(HistoryApi.TYPE_COLLECT_BOOK, bookId);
    }
}