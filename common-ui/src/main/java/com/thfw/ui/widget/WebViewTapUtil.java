package com.thfw.ui.widget;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.thfw.base.utils.EmptyUtil;
import com.thfw.base.utils.GsonUtil;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.ToastUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

/**
 * Author:pengs
 * Date: 2022/11/21 17:24
 * Describe:Todo
 */
public class WebViewTapUtil {

    private static final String FACE_NAME = "jsCallJavaObj";
    private static final String TAG = "WebViewTapUtil";

    private WebViewTapUtil() {

    }

    private static ArrayList<String> getImageUrls(String html) {
        Document document = Jsoup.parse(html);
        ArrayList<String> mImageList = new ArrayList<>();
        /* 自定义规则 */
        Elements elements = document.getElementsByTag("img"); // 找到所有img标签
        for (Element element : elements) {
            String alt = element.attr("alt");
            String src = element.attr("src");
            if (!TextUtils.isEmpty(src)) {
                System.out.println(element.html());
                System.out.println(element.attr("src"));
                LogUtil.i(TAG, "getImageUrls alt -> " + alt + " ; src -> " + src);
                mImageList.add(src);
            }
        }

        return mImageList;
    }

    public static void initWebView(WebView webView, String html) {

        final ArrayList<String> images = getImageUrls(html);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new JavaScriptImageFace() {
            @JavascriptInterface
            @Override
            public void showBigImg(String img) {
                int index = -1;
                if (!EmptyUtil.isEmpty(images)) {
                    index = images.indexOf(img);
                }
                LogUtil.i(TAG, "img = " + img + " ; index = " + index + GsonUtil.toJson(images));
                ToastUtil.show("img = " + img + " ; index = " + index);
            }
        }, FACE_NAME);
        LogUtil.i(TAG, "setWebViewClient==============");
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                LogUtil.i(TAG, "setWebViewClient onPageFinished==============");

            }

            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
                LogUtil.i(TAG, "setWebViewClient onLoadResource==============");
                setWebImageClick(view);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                LogUtil.i(TAG, "setWebViewClient onPageStarted==============");
            }

        });
    }

    /**
     * 設置網頁中圖片的點擊事件
     *
     * @param view
     */
    private static void setWebImageClick(WebView view) {
        String jsCode = "javascript:(function(){" +
                "var imgs=document.getElementsByTagName(\"img\");" +
                "for(var i=0;i<imgs.length;i++){" +
                "imgs[i].onclick=function(){" +
                "window." + FACE_NAME + ".showBigImg(this.src);" +
                "}}})()";
        view.loadUrl(jsCode);
    }


    public interface JavaScriptImageFace {
        public void showBigImg(String img);
    }
}
