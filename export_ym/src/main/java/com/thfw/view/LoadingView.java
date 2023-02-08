package com.thfw.view;

import static android.view.Gravity.CENTER;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.thfw.export_ym.R;

/**
 * 页面加载状态
 * Created By jishuaipeng on 2021/4/27
 */
public class LoadingView extends LinearLayout implements ILoading {

    private int iconWidth;
    private int iconHeight;
    // loading
    private ProgressBar mProgressBar;
    private TextView mTvLoading;
    private View mLayoutLoading;
    // fail
    private ImageView mIvFail;
    private TextView mTvFail;
    private View mLayoutFail;
    // empty
    private ImageView mIvEmpty;
    private TextView mTvEmpty;
    private View mLayoutEmpty;

    public LoadingView(@NonNull Context context) {
        super(context, null);
    }

    public LoadingView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public LoadingView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.LoadingView);
            iconWidth = ta.getDimensionPixelSize(R.styleable.LoadingView_iconWidth, -1);
            iconHeight = ta.getDimensionPixelSize(R.styleable.LoadingView_iconHeight, -1);
        }
        init();
    }

    private void init() {
        setGravity(CENTER);
        showLoading();
    }

    /**
     * 显示loading，开始加载数据时调用
     */
    @Override
    public void showLoading() {
        setVisibility(VISIBLE);
        if (mLayoutLoading == null) {
            mLayoutLoading = LayoutInflater.from(getContext()).inflate(R.layout.layout_loading_view_ym, this);
            mProgressBar = findViewById(R.id.cl_pb);
            mTvLoading = findViewById(R.id.tv_loading);
        }
        hideFail();
        hideEmpty();
        mProgressBar.setVisibility(VISIBLE);
        mTvLoading.setVisibility(VISIBLE);
        mLayoutLoading.setVisibility(VISIBLE);
    }

    /**
     * 显示loading，开始加载数据时调用
     */
    public void showLoadingNoText() {
        showLoading();
        mTvLoading.setVisibility(GONE);
    }

    /**
     * 展示空布局，通常在没有数据可展示之后
     */
    @Override
    public void showEmpty() {
        setVisibility(VISIBLE);
        if (mLayoutEmpty == null) {
            mLayoutEmpty = LayoutInflater.from(getContext()).inflate(R.layout.layout_load_empty_view_ym, this);
            mIvEmpty = findViewById(R.id.iv_load_empty);
            mTvEmpty = findViewById(R.id.tv_load_empty);
        }
        hideLoading();
        hideFail();
        mIvEmpty.setVisibility(VISIBLE);
        mTvEmpty.setVisibility(VISIBLE);
        mLayoutEmpty.setVisibility(VISIBLE);
    }

    /**
     * 展示加载失败状态
     *
     * @param onClickListener 失败，点击重试的回调
     */
    @Override
    public void showFail(OnClickListener onClickListener) {
        showFail(Level.ERROR_NET, onClickListener);
    }

    @Override
    public void showFail(Level level, OnClickListener onClickListener) {
        setVisibility(VISIBLE);
        if (mLayoutFail == null) {
            mLayoutFail = LayoutInflater.from(getContext()).inflate(R.layout.layout_load_fail_view_ym, this);
            mIvFail = findViewById(R.id.iv_load_fail);
            mTvFail = findViewById(R.id.tv_load_fail);
        }
        hideLoading();
        hideEmpty();
        mLayoutFail.setOnClickListener(view -> {
            showLoading();
            onClickListener.onClick(mLayoutFail);
        });
        mTvFail.setText(level.getHint());
        mIvFail.setVisibility(VISIBLE);
        mTvFail.setVisibility(VISIBLE);
        mLayoutFail.setVisibility(VISIBLE);
    }

    public boolean isLoadFail() {
        return mLayoutFail != null && mLayoutFail.getVisibility() == VISIBLE;
    }

    public void reTry() {
        if (mLayoutFail != null) {
            mLayoutFail.performClick();
        }
    }

    private void hideLoading() {
        if (mLayoutLoading != null) {
            mProgressBar.setVisibility(GONE);
            mTvLoading.setVisibility(GONE);
            mLayoutLoading.setVisibility(GONE);
        }
    }

    private void hideFail() {
        if (mLayoutFail != null) {
            mIvFail.setVisibility(GONE);
            mTvFail.setVisibility(GONE);
            mLayoutFail.setVisibility(GONE);
        }
    }

    private void hideEmpty() {
        if (mLayoutEmpty != null) {
            mIvEmpty.setVisibility(GONE);
            mTvEmpty.setVisibility(GONE);
            mLayoutEmpty.setVisibility(GONE);
        }
    }

    /**
     * 隐藏所有，通常在数据正常加载后调用
     */
    public void hide() {
        setVisibility(GONE);
        hideLoading();
        hideFail();
        hideEmpty();
    }

}
