package com.thfw.mobileheart.activity.task;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;

import com.just.agentweb.AgentWeb;
import com.just.agentweb.AgentWebConfig;
import com.just.agentweb.DefaultWebClient;
import com.thfw.base.models.SystemDetailModel;
import com.thfw.base.net.NetParams;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.TaskPresenter;
import com.thfw.base.utils.ToastUtil;
import com.thfw.mobileheart.R;
import com.thfw.mobileheart.activity.BaseActivity;
import com.thfw.mobileheart.util.WebSizeUtil;
import com.thfw.ui.widget.LoadingView;
import com.thfw.ui.widget.TitleView;
import com.trello.rxlifecycle2.LifecycleProvider;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Iterator;

public class SystemDetailActivity extends BaseActivity<TaskPresenter> implements TaskPresenter.TaskUi<SystemDetailModel> {

    private static final String KEY_DATA_STR = "key.data.str";
    private AgentWeb mAgentWeb;
    private String contentHtml;
    private String fontSize;
    private String titleHtml;
    private String title;
    private String url;
    private TitleView mTitleView;
    private FrameLayout mFlWebContent;
    private LoadingView mLoadingView;
    private boolean requestIng = false;
    private int bookId;

    public static void startActivity(Context context, int id) {
        ((Activity) context).startActivity(new Intent(context, SystemDetailActivity.class)
                .putExtra(KEY_DATA, id));
    }

    public static void startActivity(Context context, String msgId) {
        ((Activity) context).startActivity(new Intent(context, SystemDetailActivity.class)
                .putExtra(KEY_DATA_STR, msgId));
    }

    @Override
    public int getContentView() {
        return R.layout.activity_system_detail;
    }

    @Override
    public TaskPresenter onCreatePresenter() {
        return new TaskPresenter(this);
    }

    @Override
    public void initView() {

        mTitleView = (TitleView) findViewById(R.id.titleRobotView);
        mFlWebContent = (FrameLayout) findViewById(R.id.fl_web_content);
        mLoadingView = (LoadingView) findViewById(R.id.loadingView);

    }

    @Override
    public void initData() {
        int mBookId = getIntent().getIntExtra(KEY_DATA, 0);
        String mBookMsgId = getIntent().getStringExtra(KEY_DATA_STR);
        if (mBookId <= 0 && TextUtils.isEmpty(mBookMsgId)) {
            ToastUtil.show("????????????");
            finish();
            return;
        }
        if (mBookId > 0) {
            mPresenter.getPushModel(NetParams.crete().add("id", mBookId));
        } else {
            mPresenter.getPushModel(NetParams.crete().add("msg_id", mBookMsgId));
        }

    }


    public void initHtmlData() {
        /**
         * <h2 align="center">??????</h2>
         * <p style="text-align:center; color:#999;">000000000000000????????????</p>
         */

        fontSize = "font-size:20px";
//        titleHtml = "<h2 align=\"center\">" + title + "</h2>"
//                + "<p style=\"text-align:center; color:#666;\">??????:????????????</p>";
        titleHtml = "<h2 align=\"center\">" + title + "</h2>";
        if (TextUtils.isEmpty(contentHtml)) {
            return;
        }

        if (!contentHtml.startsWith("<html>")) {
//            contentHtml = newHtml();
            contentHtml = "<html><head><style>"
                    + "img{max-width: 80%;height: auto;object-fit: scale-down;}"
                    + "body{padding-top:20px; padding-bottom:20px; padding-left:20px;padding-right:20px;}"
//                    + "span{" + fontSize + "}"
//                    + "p{" + fontSize + "}"
                    + "</style><title>"
                    + title + "</title></head><body>" + titleHtml
                    + contentHtml + "</body></html>";
        }
        initAgentWeb((FrameLayout) findViewById(R.id.fl_web_content));
    }

    /**
     * Jsoup????????????
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
     * ??????????????????
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
     * ?????????WebView
     *
     * @param frameLayout
     */
    private void initAgentWeb(FrameLayout frameLayout) {
        AgentWeb.PreAgentWeb preAgentWeb = AgentWeb.with(this)//
                .setAgentWebParent(frameLayout, -1, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))// ??????AgentWeb???????????????
                .useDefaultIndicator(-1, 3)// ?????????????????????????????????-1????????????????????????2????????????dp???
                .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK) // ???????????? Android 4.2.2 ??????????????????????????? ?????????AgentWebView????????????
                .setMainFrameErrorView(R.layout.agentweb_error_page, -1) // ??????1?????????????????????????????????2??????????????????ID -1???????????????????????????????????? AgentWeb 3.0.0 ?????????
                .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.DISALLOW)// ???????????????????????????????????????????????????????????? AgentWeb 3.0.0 ?????????
                .interceptUnkownUrl() // ??????????????????????????????Url AgentWeb 3.0.0 ?????????
                .createAgentWeb()// ??????AgentWeb???
                .ready();// ?????? WebSettings???
        if (!TextUtils.isEmpty(contentHtml)) {
            mAgentWeb = preAgentWeb.get();
            Log.d("contentHtml", "contentHtml = " + contentHtml);
            WebView webView = mAgentWeb.getWebCreator().getWebView();

            webView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_INSET);
            webView.getSettings().setDefaultTextEncodingName("UTF-8");//???????????????utf-8
            WebSizeUtil.setSize(mContext, webView.getSettings());
            webView.loadDataWithBaseURL(null, contentHtml, "text/html", "UTF-8", null);//???????????? ??
        } else {
            mAgentWeb = preAgentWeb.go(url); // WebView?????????url???????????????????????????
        }
        AgentWebConfig.debug();

        // AgentWeb 4.0 ??????????????????????????????????????????API
        //  DefaultMsgConfig.DownloadMsgConfig mDownloadMsgConfig = mAgentWeb.getDefaultMsgConfig().getDownloadMsgConfig();
        //  mDownloadMsgConfig.setCancel("??????");  // ???????????????????????????????????????????????????

        // AgentWeb ?????????WebView????????????????????? ????????????????????? AgentWeb ???????????? ??? ??????WebView?????????????????????
        mAgentWeb.getWebCreator().getWebView().setOverScrollMode(WebView.OVER_SCROLL_NEVER);
        // mAgentWeb.getWebCreator().getWebView()  ??????WebView .

        // mAgentWeb.getWebCreator().getWebView().setOnLongClickListener();


        mAgentWeb.getWebCreator().getWebView().getSettings().setJavaScriptEnabled(true);
        //  webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        // ??????????????????
        mAgentWeb.getWebCreator().getWebView().getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        // ????????????????????????webview?????????
        mAgentWeb.getWebCreator().getWebView().getSettings().setUseWideViewPort(true);
        // ????????????????????????
        mAgentWeb.getWebCreator().getWebView().getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        // ????????????????????????
        mAgentWeb.getWebCreator().getWebView().getSettings().setLoadsImagesAutomatically(true);
        // ???webview??????requestFocus??????webview????????????
        mAgentWeb.getWebCreator().getWebView().getSettings().setNeedInitialFocus(true);
        // ???????????????
        mAgentWeb.getWebCreator().getWebView().getSettings().setUseWideViewPort(true);
        mAgentWeb.getWebCreator().getWebView().getSettings().setLoadWithOverviewMode(true);
        // ??????DOM storage API?????????HTML5 ????????????????????????????????????????????????????????????????????????????????????????????????????????? javascript ???????????????????????????
        mAgentWeb.getWebCreator().getWebView().getSettings().setDomStorageEnabled(true);
        // ????????????
        mAgentWeb.getWebCreator().getWebView().getSettings().setBuiltInZoomControls(false);
        mAgentWeb.getWebCreator().getWebView().getSettings().setSupportZoom(false);

        // ??????webview??????????????????
        mAgentWeb.getWebCreator().getWebView().getSettings().setAllowFileAccess(true);
        mAgentWeb.getWebCreator().getWebView().getSettings().setAllowFileAccessFromFileURLs(true);
        mAgentWeb.getWebCreator().getWebView().getSettings().setAllowUniversalAccessFromFileURLs(true);
        mAgentWeb.getWebCreator().getWebView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK && mAgentWeb.getWebCreator().getWebView().canGoBack()) { // ??????????????????????????????
                        mAgentWeb.getWebCreator().getWebView().goBack(); // ??????
                        // webview.goForward();// ??????
                        return true; // ?????????
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
        if (mAgentWeb != null) {
            mAgentWeb.getWebLifeCycle().onResume();// ??????
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        if (mAgentWeb != null) {
            mAgentWeb.getWebLifeCycle().onPause(); // ?????????????????????WebView,??????mWebView.resumeTimers();/mAgentWeb.getWebLifeCycle().onResume(); ?????????

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
    public void onSuccess(SystemDetailModel data) {
        if (data != null) {
            mLoadingView.hide();
            contentHtml = data.getLongContent();
            title = data.getTitle();
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
}