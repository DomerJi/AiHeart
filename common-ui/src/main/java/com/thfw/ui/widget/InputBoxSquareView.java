package com.thfw.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.thfw.base.utils.LogUtil;
import com.thfw.ui.R;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Created By jishuaipeng on 2021/5/15
 */
public class InputBoxSquareView extends FrameLayout {

    private static final int DEFAULT_CODECOUNT = 6;
    TextView[] textViews;
    private LinearLayout mLlCode;
    private EditText mEtCode;
    private boolean encryption = true;
    private boolean softKeyboardAuto = true;
    private int codeCount = DEFAULT_CODECOUNT;
    private OnInputCompleteListener onInputCompleteListener;
    private int len;

    public InputBoxSquareView(@NonNull Context context) {
        this(context, null);
    }

    public InputBoxSquareView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public InputBoxSquareView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.InputBoxSquareView);
            codeCount = ta.getInteger(R.styleable.InputBoxSquareView_ibs_codeCount, DEFAULT_CODECOUNT);
            encryption = ta.getBoolean(R.styleable.InputBoxSquareView_ibs_encryption, true);
            softKeyboardAuto = ta.getBoolean(R.styleable.InputBoxSquareView_ibs_softKeyboardAuto, true);
        }
        if (codeCount < 1) {
            codeCount = 1;
        } else if (codeCount > DEFAULT_CODECOUNT) {
            codeCount = DEFAULT_CODECOUNT;
        }
        textViews = new TextView[codeCount];
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_input_box_square_view, this);
        mLlCode = findViewById(R.id.ll_code);
        mLlCode.setOnClickListener(v -> {
            showSoftInput(mEtCode);
        });
        int[] tvIds = new int[]{R.id.tv_code01, R.id.tv_code02,
                R.id.tv_code03, R.id.tv_code04, R.id.tv_code05, R.id.tv_code06};
        for (int i = 0; i < DEFAULT_CODECOUNT; i++) {
            if (i < codeCount) {
                textViews[i] = findViewById(tvIds[i]);
            } else {
                findViewById(tvIds[i]).setVisibility(GONE);
            }
        }
        mEtCode = findViewById(R.id.et_code);
        // 设置最大长度
        InputFilter[] filters = {new InputFilter.LengthFilter(codeCount)};
        mEtCode.setFilters(filters);

        mEtCode.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    setTextViews(mEtCode.getText().toString());
                }
            }
        });

        mEtCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable text) {
                len = text.length();
                setTextViews(text.toString());
                if (onInputCompleteListener != null) {
                    if (len == codeCount) {
                        LogUtil.e("InputBox", "code = " + text.toString());
                        onInputCompleteListener.onComplete(text.toString());
                        onInputCompleteListener.onChanged(text.toString());
                    } else {
                        onInputCompleteListener.onChanged(text.toString());
                    }
                }
            }
        });

    }

    public boolean inputCodeSuccess() {
        return len == codeCount;
    }

    private void setTextViews(String text) {
        int len = text.length();
        for (int i = 0; i < codeCount; i++) {
            if (i < len) {
                textViews[i].setText(encryption ? "●" : String.valueOf(text.charAt(i)));
                textViews[i].setTextColor(getResources().getColor(R.color.text_common));
            } else {
                textViews[i].setText(i == len ? "|" : "");
                textViews[i].setTextColor(getResources().getColor(R.color.dialog_code_corlor));
            }

            textViews[i].setSelected(i == len);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (hasWindowFocus && softKeyboardAuto) {
            showSoftInput(mEtCode);
        }
    }

    /**
     * EditText获取焦点并显示软键盘
     */
    public void showSoftInput(EditText editText) {
        LogUtil.d("showSoftInputFromWindow ->");
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) editText.getContext().getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(editText, 0);
    }

    public void setOnInputCompleteListener(OnInputCompleteListener onInputCompleteListener) {
        this.onInputCompleteListener = onInputCompleteListener;
    }

    @Override
    protected void onDetachedFromWindow() {
        if (mEtCode != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(mEtCode.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        super.onDetachedFromWindow();
    }

    public interface OnInputCompleteListener {
        void onComplete(String text);

        void onChanged(String text);
    }
}
