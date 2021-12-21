package com.thfw.robotheart.fragments.login;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.view.animation.LinearInterpolator;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.github.cliven.circlecamerapreview.CircleCameraPreview;
import com.makeramen.roundedimageview.RoundedImageView;
import com.thfw.base.base.IPresenter;
import com.thfw.base.face.MyAnimationListener;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.Util;
import com.thfw.robotheart.R;
import com.thfw.robotheart.activitys.login.LoginActivity;
import com.thfw.robotheart.constants.UIConfig;
import com.thfw.ui.base.RobotBaseFragment;

/**
 * 登录
 */
public class LoginByFaceFragment extends RobotBaseFragment {

    private LinearLayout mLlLoginCenter;
    private TextView mTvLoginByPassword;
    private TextView mTvLoginByCode;
    private LinearLayout mClBottom;
    private RoundedImageView mRivWechat;
    private RoundedImageView mRivQq;
    private CheckBox mCbProduct;
    private TextView mTvProduct3g;
    private TextView mTvProductUser;
    private TextView mTvProductMsg;
    private TextView mTvProductAgree;
    private CircleCameraPreview mCircleCameraPreview;
    private ImageView mIvBorder;
    private ImageView mIvLine;
    private ObjectAnimator borderAnimation;
    private boolean mLineUpAnim = true;

    @Override
    public int getContentView() {
        return R.layout.fragment_login_by_face;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initView() {

        mLlLoginCenter = (LinearLayout) findViewById(R.id.ll_login_center);
        mTvLoginByPassword = (TextView) findViewById(R.id.tv_login_by_password);
        mTvLoginByCode = (TextView) findViewById(R.id.tv_login_by_code);
        mClBottom = (LinearLayout) findViewById(R.id.cl_bottom);
        mRivWechat = (RoundedImageView) findViewById(R.id.riv_wechat);
        mRivQq = (RoundedImageView) findViewById(R.id.riv_qq);
        mCbProduct = (CheckBox) findViewById(R.id.cb_product);
        mTvProduct3g = (TextView) findViewById(R.id.tv_product_3g);
        mTvProductUser = (TextView) findViewById(R.id.tv_product_user);
        mTvProductMsg = (TextView) findViewById(R.id.tv_product_msg);
        mTvProductAgree = (TextView) findViewById(R.id.tv_product_agree);
        mTvLoginByPassword.setOnClickListener(v -> {
            LoginActivity loginActivity = (LoginActivity) getActivity();
            loginActivity.getFragmentLoader().load(LoginActivity.BY_PASSWORD);
        });

        mTvLoginByCode.setOnClickListener(v -> {
            LoginActivity loginActivity = (LoginActivity) getActivity();
            loginActivity.getFragmentLoader().load(LoginActivity.BY_MOBILE);
        });
        mCircleCameraPreview = (CircleCameraPreview) findViewById(R.id.circleCameraPreview);
        mCircleCameraPreview.setOnPreview(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                LogUtil.d(TAG, EmptyUtil.isEmpty(data) ? "null" : "len = " + data.length);
            }
        });
        mIvBorder = (ImageView) findViewById(R.id.iv_border);
        mIvLine = (ImageView) findViewById(R.id.iv_line);
    }

    @Override
    public void initData() {
        if (ContextCompat.checkSelfPermission(mContext, UIConfig.NEEDED_PERMISSION[0])
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

//        mCircleCameraPreview.pause(true);

        mCircleCameraPreview.setOnClickListener(v -> {
            // 点击暂停预览，用于模拟识别中情景
            mCircleCameraPreview.pause();
        });
        startBorderAnim();

    }


    public void startBorderAnim() {
        mIvBorder.clearAnimation();
        borderAnimation = ObjectAnimator.ofFloat(mIvBorder, "rotation", 0, 360);
        borderAnimation.setRepeatCount(ObjectAnimator.INFINITE);
        borderAnimation.setRepeatMode(ObjectAnimator.RESTART);
        borderAnimation.setDuration(10000);
        borderAnimation.setInterpolator(new LinearInterpolator());
        borderAnimation.start();

    }

    @Override
    public void onResume() {
        super.onResume();
        mIvBorder.post(new Runnable() {
            @Override
            public void run() {
                startLineAnim();
            }
        });
    }

    public void startLineAnim() {
        mIvLine.clearAnimation();

        int margin = Util.dipToPx(12, mContext);
        int height = mIvBorder.getHeight() - 2 * margin - mIvLine.getHeight();
        int mRCircle = height / 2;

        ObjectAnimator lineAnimation = ObjectAnimator.ofFloat(mIvLine, "translationY",
                mLineUpAnim ? 0 : height, mLineUpAnim ? height : 0);
        lineAnimation.setDuration(3000);
        lineAnimation.setInterpolator(new LinearInterpolator());
        lineAnimation.addListener(new MyAnimationListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mLineUpAnim = !mLineUpAnim;
                startLineAnim();

            }
        });
        lineAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                // 知道半径是R，圆心到直线的距离是d，
                // 那么这条直线的长度计算可以利用勾股定理
                // L=2√(R²+d²)
                int r = mRCircle;
                int d = (int) Math.abs(value - mRCircle);
                int l = (int) (2 * Math.sqrt(r * r - d * d));
                LogUtil.d("onAnimationUpdate", "d = " + d + " ; r = " + r + " ; l = " + l);
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mIvLine.getLayoutParams();
                lp.width = l;
                lp.topMargin = margin;
                lp.rightMargin = margin;
                lp.leftMargin = margin;
                lp.bottomMargin = margin;
                mIvLine.setLayoutParams(lp);
            }
        });
        lineAnimation.start();


    }
}