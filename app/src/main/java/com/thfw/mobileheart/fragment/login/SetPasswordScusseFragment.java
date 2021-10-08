package com.thfw.mobileheart.fragment.login;

import android.widget.Button;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.makeramen.roundedimageview.RoundedImageView;
import com.thfw.base.base.IPresenter;
import com.thfw.robotheart.R;
import com.thfw.ui.base.BaseFragment;

/**
 * Author:pengs
 * Date: 2021/8/4 16:19
 * Describe:重置密码成功
 */
public class SetPasswordScusseFragment extends BaseFragment {

    private ConstraintLayout mClTop;
    private RoundedImageView mRivIconBg;
    private RoundedImageView mRivIcon;
    private Button mBtConfirm;

    @Override
    public int getContentView() {
        return R.layout.fragment_forget_set_password_succes;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initView() {
        mClTop = (ConstraintLayout) findViewById(R.id.cl_top);
        mRivIconBg = (RoundedImageView) findViewById(R.id.riv_icon_bg);
        mRivIcon = (RoundedImageView) findViewById(R.id.riv_icon);
        mBtConfirm = (Button) findViewById(R.id.bt_login);

        mBtConfirm.setOnClickListener(v -> {
            getActivity().finish();
        });
    }

    @Override
    public void initData() {

    }
}
