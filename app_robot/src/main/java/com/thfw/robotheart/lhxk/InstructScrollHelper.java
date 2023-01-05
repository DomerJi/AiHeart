package com.thfw.robotheart.lhxk;

import android.text.TextUtils;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.webkit.WebView;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.base.base.SpeechToAction;
import com.thfw.base.face.OnSpeakTextListener;
import com.thfw.base.utils.HandlerUtil;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.NumberUtil;
import com.thfw.base.utils.Util;
import com.thfw.robotheart.MyApplication;
import com.thfw.robotheart.adapter.BaseAdapter;

/**
 * Author:pengs
 * Date: 2021/9/6 14:01
 * Describe:语音指令滑动列表
 */
public class InstructScrollHelper {
    private static final String TAG = InstructScrollHelper.class.getSimpleName();
    private int screenItemCount = -1;
    private RecyclerView recyclerView;
    private WebView webView;
    private ScrollView scrollView;
    private Class classes;
    private boolean onChildViewAttachedToWindowed;

    private static final String LAST = "向上滑动,向上滚动,往上看，向上看,向前看";
    private static final String NEXT = "向下滑动,向下滚动,往下看，向下看,向后看";


    public InstructScrollHelper(Class classes, WebView webView) {
        Log.i(TAG, "webView = " + webView);
        this.webView = webView;
        this.classes = classes;
        LhXkHelper.putAction(classes, new SpeechToAction(NEXT, () -> {
            Log.i(TAG, "webView = " + webView);
            if (webView == null) {
                return;
            }
            webView.scrollTo(0, webView.getScrollY() + Util.getScreenHeight(MyApplication.getApp()));
        }));

        LhXkHelper.putAction(classes, new SpeechToAction(LAST, () -> {
            Log.i(TAG, "webView = " + webView);
            if (webView == null) {
                return;
            }
            webView.scrollTo(0, webView.getScrollY() - Util.getScreenHeight(MyApplication.getApp()));
        }));

        LhXkHelper.putAction(classes, new SpeechToAction("滑动到顶部,滚动到顶部", () -> {
            Log.i(TAG, "webView = " + webView);
            if (webView == null) {
                return;
            }
            webView.scrollTo(0, 0);
        }));

        LhXkHelper.putAction(classes, new SpeechToAction("滑动到底部, 滚动到底部", () -> {
            Log.i(TAG, "webView = " + webView);
            if (webView == null) {
                return;
            }
            webView.scrollTo(0, 100000);
        }));
    }

    public InstructScrollHelper(Class classes, ScrollView scrollView) {
        Log.i(TAG, "webView = " + scrollView);
        this.scrollView = scrollView;
        this.classes = classes;
        LhXkHelper.putAction(classes, new SpeechToAction(NEXT, () -> {
            Log.i(TAG, "scrollView = " + scrollView);
            if (scrollView == null) {
                return;
            }
            scrollView.smoothScrollTo(0, scrollView.getScrollY() + Util.getScreenHeight(MyApplication.getApp()));
        }));

        LhXkHelper.putAction(classes, new SpeechToAction(LAST, () -> {
            Log.i(TAG, "scrollView = " + scrollView);
            if (scrollView == null) {
                return;
            }
            scrollView.smoothScrollTo(0, scrollView.getScrollY() - Util.getScreenHeight(MyApplication.getApp()));
        }));

        LhXkHelper.putAction(classes, new SpeechToAction("滑动到顶部,滚动到顶部", () -> {
            Log.i(TAG, "scrollView = " + scrollView);
            if (scrollView == null) {
                return;
            }
            scrollView.smoothScrollTo(0, 0);
        }));

        LhXkHelper.putAction(classes, new SpeechToAction("滑动到底部, 滚动到底部", () -> {
            Log.i(TAG, "scrollView = " + scrollView);
            if (scrollView == null) {
                return;
            }
            scrollView.smoothScrollTo(0, 1000000);
        }));
    }

    public InstructScrollHelper(Class classes, RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        this.classes = classes;
        LhXkHelper.putAction(classes, new SpeechToAction(LAST, () -> {
            onScrollLeft();
        }));

        LhXkHelper.putAction(classes, new SpeechToAction(NEXT, () -> {
            onScrollRight();
        }));

        LhXkHelper.putAction(classes, new SpeechToAction("滑动到顶部,滚动到顶部", () -> {
            onScrollMostLeft();
        }));

        LhXkHelper.putAction(classes, new SpeechToAction("滑动到底部, 滚动到底部", () -> {
            onScrollMostRight();
        }));
        if (recyclerView.isAttachedToWindow()) {
            init();
        }
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    init();
                }
            }
        });
        if (recyclerView.getAdapter() instanceof BaseAdapter) {
            if (((BaseAdapter) recyclerView.getAdapter()).getItemCount() > 0) {
                initData();
            } else {
                ((BaseAdapter) recyclerView.getAdapter()).registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                    @Override
                    public void onChanged() {
                        super.onChanged();
                        initData();
                    }
                });
            }
        }
        ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                initData();
            }
        };
        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);
    }

    private void initData() {
        if (!onChildViewAttachedToWindowed) {
            onChildViewAttachedToWindowed = true;
            LogUtil.i(TAG, "onChildViewAttachedToWindow init");
            HandlerUtil.getMainHandler().postDelayed(() -> {
                init();
            }, 500);

        }
    }

    public static String speakNumber(int order) {
        return "第" + order + "个,第" + NumberUtil.convert(order) + "个";
    }

    private void init() {
        RecyclerView.LayoutManager manager = getOnScroll().getLayoutManager();
        int first = 0;
        int last = 0;
        if (manager instanceof GridLayoutManager) {
            GridLayoutManager gridLayoutManager = (GridLayoutManager) manager;
            first = gridLayoutManager.findFirstVisibleItemPosition();
            last = gridLayoutManager.findLastVisibleItemPosition();
            if (first == RecyclerView.NO_POSITION || last == RecyclerView.NO_POSITION) {
                return;
            }
        } else if (manager instanceof LinearLayoutManager) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) manager;
            first = linearLayoutManager.findFirstVisibleItemPosition();
            last = linearLayoutManager.findLastVisibleItemPosition();
            if (first == RecyclerView.NO_POSITION || last == RecyclerView.NO_POSITION) {
                return;
            }

        }

        if (recyclerView.getAdapter() instanceof OnSpeakTextListener) {
            OnSpeakTextListener onSpeakTextListener = (OnSpeakTextListener) recyclerView.getAdapter();
            for (int i = first; i <= last; i++) {
                String text = onSpeakTextListener.getText(i, OnSpeakTextListener.TYPE_SPEAK_TEXT);
                String order = onSpeakTextListener.getText(i, OnSpeakTextListener.TYPE_SPEAK_ORDER);
                if (!TextUtils.isEmpty(text)) {
                    String actionText = text;
                    if (!TextUtils.isEmpty(order)) {
                        actionText = actionText + "," + order;
                    }
                    int finalI = i;
                    LogUtil.i(TAG, "putAction - actionText (" + actionText + ")");
                    LhXkHelper.putAction(classes, new SpeechToAction(actionText, () -> {
                        onSpeakTextListener.onSpeakItemClick(finalI);
                        LogUtil.i(TAG, "onSpeakItemClick(" + finalI + ")");
                    }));
                }


            }
        }
    }


    protected RecyclerView getOnScroll() {
        return recyclerView;
    }

    /**
     * 左滑 - 语音操作事件
     */
    public void onScrollLeft() {
        LinearLayoutManager manager = (LinearLayoutManager) getOnScroll().getLayoutManager();
        int position = manager.findFirstVisibleItemPosition();
        position = position - getScrollItemCount();
        if (position < 0) {
            position = 0;
        }
        getOnScroll().smoothScrollToPosition(position);
    }

    /**
     * 每次翻页item数量
     *
     * @return
     */
    public int getScrollItemCount() {
        if (screenItemCount != -1) {
            return screenItemCount;
        }
        // 根据屏幕显示数量进行滚动距离判断
        LinearLayoutManager manager = (LinearLayoutManager) getOnScroll().getLayoutManager();
        int first = manager.findFirstVisibleItemPosition();
        int last = manager.findLastVisibleItemPosition();
        screenItemCount = Math.max(last - first, screenItemCount);
        return screenItemCount;
    }

    /**
     * 滑到最左 - 语音操作事件
     */
    public void onScrollMostLeft() {
        getOnScroll().smoothScrollToPosition(0);
    }

    /**
     * 右滑 - 语音操作事件
     */
    public void onScrollRight() {
        LinearLayoutManager manager = (LinearLayoutManager) getOnScroll().getLayoutManager();
        int position = manager.findLastVisibleItemPosition();
        position = position + getScrollItemCount();
        if (position >= manager.getItemCount()) {
            position = manager.getItemCount() - 1;
        }
        getOnScroll().smoothScrollToPosition(position);
    }

    /**
     * 滑到最右 - 语音操作事件
     */
    public void onScrollMostRight() {
        getOnScroll().smoothScrollToPosition(getOnScroll().getLayoutManager().getItemCount() - 1);
    }
}
