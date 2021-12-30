package com.thfw.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.thfw.base.utils.LogUtil;
import com.thfw.ui.R;


/**
 * 搜索控件
 * Created By jishuaipeng on 2021/5/19
 */
public class MyRobotSearchView extends FrameLayout {

    private boolean clearIcon = true;
    private boolean isEdit = true;
    private ImageView mIvSearch;
    private EditText mEtSearch;
    private ImageView mIvClear;
    private LinearLayout mLlSerach;
    private String hint;
    private int hintColor;
    private OnSearchListener onSearchListener;

    public MyRobotSearchView(@NonNull Context context) {
        this(context, null);
    }

    public MyRobotSearchView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public MyRobotSearchView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.layout_search_robot_view, this);
        initView();
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.MyRobotSearchView);
            clearIcon = ta.getBoolean(R.styleable.MyRobotSearchView_rsv_clearIcon, true);
            isEdit = ta.getBoolean(R.styleable.MyRobotSearchView_rsv_isEdit, true);
            hint = ta.getString(R.styleable.MyRobotSearchView_rsv_hint);
            int hintColor = ta.getColor(R.styleable.MyRobotSearchView_rsv_hintColor, Color.WHITE);
            Drawable drawable = ta.getDrawable(R.styleable.MyRobotSearchView_rsv_background);
            if (!TextUtils.isEmpty(hint)) {
                mEtSearch.setHint(hint);
            }
            Log.d("hintColor", "hintColor = " + hintColor);
            mEtSearch.setHintTextColor(hintColor);
            if (drawable != null) {
                mLlSerach.setBackground(drawable);
            }
            if (!isEdit) {
                mEtSearch.setFocusable(false);
                mEtSearch.setCursorVisible(false);
            }
            ta.recycle();
        }
        if (clearIcon) {
            mIvClear.setOnClickListener(v -> {
                mEtSearch.setText("");
            });
        }
    }

    private void initView() {

        mIvSearch = findViewById(R.id.iv_search);
        mEtSearch = findViewById(R.id.et_search);
        mIvClear = findViewById(R.id.iv_clear);
        onSearchKey(null, false);
        mLlSerach = findViewById(R.id.ll_search);
        if (!isEdit) {
            return;
        }
        mEtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_SEARCH:// 按下搜索键
                        onSearchKey(mEtSearch.getText().toString(), true);
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

        mEtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                onSearchKey(s.toString(), false);
            }
        });

        mIvSearch.setOnClickListener(v -> {
            String key = mEtSearch.getText().toString();
            if (!TextUtils.isEmpty(key)) {
                onSearchKey(mEtSearch.getText().toString(), true);
            }
        });

    }

    public EditText getEditeText() {
        return mEtSearch;
    }

    public void setOnSearchListener(OnSearchListener onSearchListener) {
        if (onSearchListener == null) {
            return;
        }
        this.onSearchListener = onSearchListener;
        setOnClickListener(v -> {
            if (this.onSearchListener != null) {
                LogUtil.d(" this.onSearchListener.onClick()");
                this.onSearchListener.onClick(v);
            }
        });
        mEtSearch.setOnClickListener(v -> {
            if (this.onSearchListener != null) {
                LogUtil.d(" this.onSearchListener.onClick()");
                this.onSearchListener.onClick(v);
            }
        });
    }

    public void onSearchKey(String key, boolean clickSearch) {
        LogUtil.d("mIvClear isShow = " + (!TextUtils.isEmpty(key) && clearIcon));
        mIvClear.setVisibility(!TextUtils.isEmpty(key) && clearIcon ? VISIBLE : GONE);
        if (this.onSearchListener != null) {
            LogUtil.d("onSearch key = " + key + " ; clickSearch = " + clickSearch);
            onSearchListener.onSearch(key, clickSearch);
        }
    }

    public interface OnSearchListener {

        /**
         * @param key         输入内容
         * @param clickSearch 是否按下搜索键
         */
        void onSearch(String key, boolean clickSearch);

        void onClick(View view);
    }
}
