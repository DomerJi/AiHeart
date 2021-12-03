package com.thfw.robotheart.fragments.login;

import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.thfw.base.base.IPresenter;
import com.thfw.base.utils.ToastUtil;
import com.thfw.robotheart.R;
import com.thfw.robotheart.activitys.login.LoginActivity;
import com.thfw.robotheart.view.TitleRobotView;
import com.thfw.ui.base.BaseFragment;
import com.thfw.ui.widget.InputBoxSquareView;

public class LoginMobileCodeFragment extends BaseFragment {

    private TitleRobotView mTitleRobotView;
    private InputBoxSquareView mIbvCode;
    private Button mBtGetCode;
    private TextView mTvRegetCode;
    private LinearLayout mClBottom;
    private RoundedImageView mRivWechat;
    private RoundedImageView mRivQq;
    private CheckBox mCbProduct;
    private TextView mTvProduct3g;
    private TextView mTvProductUser;
    private TextView mTvProductMsg;
    private TextView mTvProductAgree;

    public LoginMobileCodeFragment() {
        // Required empty public constructor
    }

    @Override
    public int getContentView() {
        return R.layout.fragment_login_mobile_code;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initView() {
        mTitleRobotView = (TitleRobotView) findViewById(R.id.titleRobotView);
        mIbvCode = (InputBoxSquareView) findViewById(R.id.ibv_code);
        mBtGetCode = (Button) findViewById(R.id.bt_get_code);
        mTvRegetCode = (TextView) findViewById(R.id.tv_reget_code);
        mClBottom = (LinearLayout) findViewById(R.id.cl_bottom);
        mRivWechat = (RoundedImageView) findViewById(R.id.riv_wechat);
        mRivQq = (RoundedImageView) findViewById(R.id.riv_qq);
        mCbProduct = (CheckBox) findViewById(R.id.cb_product);
        mTvProduct3g = (TextView) findViewById(R.id.tv_product_3g);
        mTvProductUser = (TextView) findViewById(R.id.tv_product_user);
        mTvProductMsg = (TextView) findViewById(R.id.tv_product_msg);
        mTvProductAgree = (TextView) findViewById(R.id.tv_product_agree);
    }

    @Override
    public void initData() {
        mTitleRobotView.getIvBack().setOnClickListener(v -> {
            LoginActivity loginActivity = (LoginActivity) getActivity();
            loginActivity.getFragmentLoader().load(LoginActivity.BY_MOBILE);
        });

        mIbvCode.setOnInputCompleteListener(new InputBoxSquareView.OnInputCompleteListener() {
            @Override
            public void onComplete(String text) {
                ToastUtil.show(text);
            }
        });

        mTitleRobotView.setRightOnClickListener(v -> {
            LoginActivity loginActivity = (LoginActivity) getActivity();
            loginActivity.getFragmentLoader().load(LoginActivity.BY_PASSWORD);
        });

    }
}