package com.thfw.mobileheart.activity.settings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.thfw.base.base.IPresenter;
import com.thfw.base.face.MyTextWatcher;
import com.thfw.mobileheart.R;
import com.thfw.mobileheart.activity.BaseActivity;
import com.thfw.ui.utils.EditTextUtil;
import com.thfw.ui.widget.TitleView;

public class EditInfoActivity extends BaseActivity {

    public static final String KEY_RESULT = "key.result";
    private static EditType tempEditType;
    private TitleView mTitleRobotView;
    private EditText mEtInput;
    private Button mBtConfirm;
    private EditType editType;

    public static void startActivity(Context context, EditType editType) {
        tempEditType = editType;
        ((Activity) context).startActivityForResult(new Intent(context, EditInfoActivity.class), editType.type);
    }

    public static void startActivity(Context context, EditType editType, String name) {
        tempEditType = editType;
        ((Activity) context).startActivityForResult(new Intent(context, EditInfoActivity.class)
                .putExtra(KEY_DATA, name), editType.type);
    }

    @Override
    public int getContentView() {
        return R.layout.activity_edit_info;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initView() {
        editType = tempEditType;

        mTitleRobotView = (TitleView) findViewById(R.id.titleRobotView);
        mTitleRobotView.setCenterText(editType.title);
        mEtInput = (EditText) findViewById(R.id.et_input);
        if (editType != null && editType.type == EditType.NAME_PINYIN.type) {
            EditTextUtil.setEditTextOnlyAbc(mEtInput);
        } else if (editType != null && editType.type == EditType.NAME.type) {
            EditTextUtil.setEditTextOnlyAbcAndChina(mEtInput);
        } else if (editType != null && editType.type == EditType.NICKNAME.type) {
            EditTextUtil.setEditTextOnlyAbc123AndChina(mEtInput);
        } else {
            EditTextUtil.setEditTextInhibitInputSpace(mEtInput);
        }
        mBtConfirm = (Button) findViewById(R.id.bt_confirm);
        mEtInput.setMaxEms(tempEditType.textSizeMax);
        if (editType == null || editType.type != 4) {
            findViewById(R.id.tv_hint).setVisibility(View.GONE);
        }
        mBtConfirm.setEnabled(false);
        mEtInput.addTextChangedListener(new MyTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mBtConfirm.setEnabled(!TextUtils.isEmpty(s) && s.length() >= editType.textSizeMin);
            }
        });
        mBtConfirm.setOnClickListener(v -> {
            setResult(RESULT_OK, getIntent().putExtra(KEY_RESULT, mEtInput.getText().toString()));
            finish();
        });

        String name = getIntent().getStringExtra(KEY_DATA);
        if (!TextUtils.isEmpty(name)) {
            mEtInput.setText(name);
        }
    }

    @Override
    public void initData() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public enum EditType {

        NICKNAME(0, "填写昵称", 2, 15),
        NAME(1, "填写真实姓名", 2, 15),
        NAME_PINYIN(4, "填写姓名全拼", 3, 30),
        CLASSES(2, "填写部门", 2, 15),
        PHONE_NUMBER(3, "填写手机号", 11, 11);

        private int type;
        private int textSizeMin;
        private int textSizeMax;
        private String title;

        private EditType(int type, String title, int min, int max) {
            this.type = type;
            this.title = title;
            this.textSizeMin = min;
            this.textSizeMax = max;
        }

        public int getType() {
            return type;
        }
    }
}