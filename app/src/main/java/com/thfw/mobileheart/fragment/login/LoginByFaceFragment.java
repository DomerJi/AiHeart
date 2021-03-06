package com.thfw.mobileheart.fragment.login;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.google.common.reflect.TypeToken;
import com.makeramen.roundedimageview.RoundedImageView;
import com.thfw.base.base.IPresenter;
import com.thfw.base.face.MyAnimationListener;
import com.thfw.base.models.CommonModel;
import com.thfw.base.models.TokenModel;
import com.thfw.base.net.ApiHost;
import com.thfw.base.net.HttpResult;
import com.thfw.base.net.MultipartBodyFactory;
import com.thfw.base.net.OkHttpUtil;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.base.utils.FaceSetUtil;
import com.thfw.base.utils.FileUtil;
import com.thfw.base.utils.GsonUtil;
import com.thfw.base.utils.HourUtil;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.SharePreferenceUtil;
import com.thfw.base.utils.ToastUtil;
import com.thfw.base.utils.Util;
import com.thfw.mobileheart.MyApplication;
import com.thfw.mobileheart.R;
import com.thfw.mobileheart.activity.BaseFragment;
import com.thfw.mobileheart.activity.WebActivity;
import com.thfw.mobileheart.activity.login.LoginActivity;
import com.thfw.mobileheart.constants.AgreeOn;
import com.thfw.mobileheart.constants.UIConfig;
import com.thfw.mobileheart.fragment.MeFragment;
import com.thfw.mobileheart.util.DialogFactory;
import com.thfw.user.login.UserManager;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCamera2CircleView;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.helper.FaceUtil;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.samples.facedetect.DetectionBasedTracker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * ??????
 */
public class LoginByFaceFragment extends BaseFragment implements CameraBridgeViewBase.CvCameraViewListener2 {


    /**
     * ???????????????
     */
    public static final int JAVA_DETECTOR = 0;
    public static final int NATIVE_DETECTOR = 1;
    // ??????????????????
    private static final int FAIL_COUNT_MAX = 10;
    private static final Scalar FACE_RECT_COLOR = new Scalar(0, 255, 0, 255);
    private LinearLayout mLlLoginCenter;
    private TextView mTvLoginByPassword;
    private LinearLayout mClBottom;
    private RoundedImageView mRivWechat;
    private RoundedImageView mRivQq;
    private CheckBox mCbProduct;
    private TextView mTvProductUser;
    private TextView mTvProductMsg;
    private TextView mTvProductAgree;
    private JavaCamera2CircleView mJavaCamera2CircleView;
    private ImageView mIvBorder;
    private ImageView mIvLine;
    private ObjectAnimator borderAnimation;
    private Mat mRgba; //????????????
    private Mat mGray;
    private File mCascadeFile;
    private File mCascadeEyeFile;
    private CascadeClassifier mJavaDetector;
    private CascadeClassifier mJavaEyeDetector;
    private DetectionBasedTracker mNativeDetector;
    private DetectionBasedTracker mNativeEyeDetector;
    private ConstraintLayout mClTop;
    private RoundedImageView mRivIconBg;
    private RoundedImageView mRivIcon;
    private ConstraintLayout mClFaceIng;
    private ConstraintLayout mClFaceBegin;
    private Button mBtBegin;
    private TextView mTvOtherLogin;
    private TextView mTvProduct3g;
    private TextView mTvFaceFailHint;
    private ConstraintLayout mClFaceFail;
    private Button mBtRetry;
    // ???????????????????????????
    private boolean mLineUpAnim = true;
    // true ????????????  false ????????????
    private boolean inputFace;
    private Handler mMainHandler = new Handler(Looper.getMainLooper());
    /**
     * ????????????????????????
     */
    private volatile boolean frameHandleIng = false;
    private volatile boolean showFail = false;
    private volatile int failCount = 0;
    private volatile boolean failByNet = false;
    private volatile long beginTime;
    private volatile long changeFaceHintTime;
    // ?????????
    private String faceHint01 = "??????????????????????????????????????????";
    private String faceHint02 = "???????????????????????????????????????";
    private String faceHint03 = "????????????...";
    // ?????????
    private int mDetectorType = NATIVE_DETECTOR;
    private String[] mDetectorName;
    private float mRelativeFaceSize = 0.2f;
    private int mAbsoluteFaceSize = 0;
    private TextView mTvFaceHint;
    private Runnable changeFaceHintRunnable;
    private Mat rotateMat;
    private TextView mTvPageTitle;
    private ObjectAnimator lineAnimation;
    private boolean mGoFinish;

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

        inputFace = UserManager.getInstance().isLogin();
        mClBottom = (LinearLayout) findViewById(R.id.cl_bottom);
        mRivWechat = (RoundedImageView) findViewById(R.id.riv_wechat);
        mRivQq = (RoundedImageView) findViewById(R.id.riv_qq);

        mCbProduct = (CheckBox) findViewById(R.id.cb_product);
        mTvProductUser = (TextView) findViewById(R.id.tv_product_user);
        mTvProductMsg = (TextView) findViewById(R.id.tv_product_msg);
        mTvProductAgree = (TextView) findViewById(R.id.tv_product_agree);
        mTvPageTitle = (TextView) findViewById(R.id.tv_page_title);

        mJavaCamera2CircleView = (JavaCamera2CircleView) findViewById(R.id.javaCamera2CircleView);
        mIvBorder = (ImageView) findViewById(R.id.iv_border);
        mIvLine = (ImageView) findViewById(R.id.iv_line);
        Util.addUnderLine(mTvProductUser, mTvProductMsg, mTvProductAgree);

        mClTop = (ConstraintLayout) findViewById(R.id.cl_top);
        mRivIconBg = (RoundedImageView) findViewById(R.id.riv_icon_bg);
        mRivIcon = (RoundedImageView) findViewById(R.id.riv_icon);
        mClFaceIng = (ConstraintLayout) findViewById(R.id.cl_face_ing);
        mClFaceBegin = (ConstraintLayout) findViewById(R.id.cl_face_begin);
        mBtBegin = (Button) findViewById(R.id.bt_begin);
        mTvOtherLogin = (TextView) findViewById(R.id.tv_other_login);
        mTvProduct3g = (TextView) findViewById(R.id.tv_product_3g);

        mTvOtherLogin.setOnClickListener(v -> {
            LoginActivity loginActivity = (LoginActivity) getActivity();
            loginActivity.getFragmentLoader().load(LoginActivity.BY_OTHER);
            hideInput();
        });
        mTvFaceFailHint = (TextView) findViewById(R.id.tv_face_fail_hint);
        mClFaceFail = (ConstraintLayout) findViewById(R.id.cl_face_fail);
        mBtRetry = (Button) findViewById(R.id.bt_retry);
        mTvFaceHint = (TextView) findViewById(R.id.tv_face_hint);

        if (inputFace) {
            mTvPageTitle.setText("????????????");
            mBtBegin.setText("????????????");
            mTvOtherLogin.setVisibility(View.INVISIBLE);
            mClBottom.setVisibility(View.INVISIBLE);
        } else {
            initAgreeClick();
        }
    }

    private void initAgreeClick() {
        mTvProductAgree.setOnClickListener(v -> {
            WebActivity.startActivity(mContext, AgreeOn.AGREE_AGREE);
        });
        mTvProductUser.setOnClickListener(v -> {
            WebActivity.startActivity(mContext, AgreeOn.AGREE_USER);
        });
        mTvProductMsg.setOnClickListener(v -> {
            WebActivity.startActivity(mContext, AgreeOn.AGREE_MSG);
        });
    }

    @Override
    public void initData() {

        mBtBegin.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(mContext, UIConfig.NEEDED_PERMISSION[0])
                    != PackageManager.PERMISSION_GRANTED) {
                ToastUtil.show("????????????????????????");
                return;
            }
            mClFaceBegin.setVisibility(View.GONE);
            mClFaceIng.setVisibility(View.VISIBLE);
            if (mNativeEyeDetector == null || mNativeDetector == null) {
                // opencv
                initializeOpenCVDependencies();
                initializeDetectEye();
            }
            mJavaCamera2CircleView.setCvCameraViewListener(this);
            mJavaCamera2CircleView.setVisibility(SurfaceView.VISIBLE);
            mJavaCamera2CircleView.enableView();
            startAnim();
        });


    }

    public void startAnim() {
        mIvBorder.post(new Runnable() {
            @Override
            public void run() {
                startLineAnim();
                startBorderAnim();
            }
        });
    }

    public void startBorderAnim() {
        cancelBorderAnim();
        borderAnimation = ObjectAnimator.ofFloat(mIvBorder, "rotation", 0, 360);
        borderAnimation.setRepeatCount(ObjectAnimator.INFINITE);
        borderAnimation.setRepeatMode(ObjectAnimator.RESTART);
        borderAnimation.setDuration(10000);
        borderAnimation.setInterpolator(new LinearInterpolator());
        borderAnimation.start();

    }


    /**
     * ??????????????????
     */
    private void resetFaceFlag() {
        failCount = 0;
        beginTime = 0;
        failByNet = false;
        frameHandleIng = false;
        showFail = false;
    }

    @Override
    public void onVisible(boolean isVisible) {
        super.onVisible(isVisible);
        // ????????????????????????????????????????????????????????????
        if (!mGoFinish) {
            if (mClFaceIng != null) {
                mClFaceIng.setVisibility(View.GONE);
            }
            if (mClFaceBegin != null) {
                mClFaceBegin.setVisibility(View.VISIBLE);
            }
            if (mClFaceFail != null) {
                mClFaceFail.setVisibility(View.GONE);
            }
        }
        if (isVisible) {
            resetFaceFlag();
        } else {
            cancelLineAnim();
            cancelBorderAnim();
            if (mJavaCamera2CircleView != null) {
                mJavaCamera2CircleView.disableView();
            }
        }

    }

    public void cancelLineAnim() {
        if (lineAnimation != null) {
            lineAnimation.removeAllListeners();
            lineAnimation.cancel();
            lineAnimation = null;
        }
        if (mIvLine != null) {
            mIvLine.clearAnimation();
        }
    }

    public void cancelBorderAnim() {
        if (borderAnimation != null) {
            borderAnimation.removeAllListeners();
            borderAnimation.cancel();
            borderAnimation = null;
        }
        if (mIvBorder != null) {
            mIvBorder.clearAnimation();
        }
    }

    public void startLineAnim() {
        cancelLineAnim();

        int height = mJavaCamera2CircleView.getHeight() - mIvLine.getHeight();
        int mRCircle = mJavaCamera2CircleView.getHeight() / 2;

        lineAnimation = ObjectAnimator.ofFloat(mIvLine, "translationY",
                mLineUpAnim ? 0 : height, mLineUpAnim ? height : 0);
        lineAnimation.setDuration(3000);
        lineAnimation.setInterpolator(new LinearInterpolator());
        lineAnimation.addListener(new MyAnimationListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (isVisible()) {
                    mLineUpAnim = !mLineUpAnim;
                    startLineAnim();
                }
            }
        });
        lineAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                value = value + mIvLine.getHeight() / 2f;
                // ???????????????R??????????????????????????????d???
                // ?????????????????????????????????????????????????????????
                // L=2???(R??-d??)
                int r = mRCircle;
                int d = (int) Math.abs(value - mRCircle);
                int l = (int) (2 * Math.sqrt(r * r - d * d));
//                LogUtil.d("onAnimationUpdate", "d = " + d + " ; r = " + r + " ; l = " + l);
                ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) mIvLine.getLayoutParams();
                lp.width = l;
                mIvLine.setLayoutParams(lp);
            }
        });
        lineAnimation.start();


    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        mGray = new Mat();
        mRgba = new Mat();
    }

    @Override
    public void onCameraViewStopped() {
        mGray.release();
        mRgba.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        return onCameraFrameHandle(inputFrame);
    }

    /**
     * ?????????????????????
     */
    private void initializeOpenCVDependencies() {
        mDetectorName = new String[2];
        mDetectorName[JAVA_DETECTOR] = "Java";
        mDetectorName[NATIVE_DETECTOR] = "Native (tracking)";

        System.loadLibrary("detection_based_tracker");

        try {

            // load cascade file from application resources
            InputStream is = getResources().openRawResource(R.raw.lbpcascade_frontalface);
            File cascadeDir = getActivity().getDir("cascade", Context.MODE_PRIVATE);
            mCascadeFile = new File(cascadeDir, "lbpcascade_frontalface.xml");
            FileOutputStream os = new FileOutputStream(mCascadeFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();

            mJavaDetector = new CascadeClassifier(mCascadeFile.getAbsolutePath());
            if (mJavaDetector.empty()) {
                Log.e(TAG, "Failed to load cascade classifier");
                mJavaDetector = null;
            } else
                Log.i(TAG, "Loaded cascade classifier from " + mCascadeFile.getAbsolutePath());

            mNativeDetector = new DetectionBasedTracker(mCascadeFile.getAbsolutePath(), 0);

            cascadeDir.delete();

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Failed to load cascade. Exception thrown: " + e);
        }
    }

    /**
     * ?????????????????????
     */
    private void initializeDetectEye() {

        try {

            // load cascade file from application resources
            InputStream is = getResources().openRawResource(R.raw.haarcascade_eye);
            File cascadeDir = getActivity().getDir("cascade", Context.MODE_PRIVATE);
            mCascadeEyeFile = new File(cascadeDir, "haarcascade_eye.xml");
            FileOutputStream os = new FileOutputStream(mCascadeEyeFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();

            mJavaEyeDetector = new CascadeClassifier(mCascadeEyeFile.getAbsolutePath());
            if (mJavaEyeDetector.empty()) {
                Log.e(TAG, "Failed to load cascade classifier");
                mJavaEyeDetector = null;
            } else
                Log.i(TAG, "Loaded cascade classifier from " + mCascadeEyeFile.getAbsolutePath());

            mNativeEyeDetector = new DetectionBasedTracker(mCascadeEyeFile.getAbsolutePath(), 0);

            cascadeDir.delete();

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Failed to load cascade. Exception thrown: " + e);
        }
    }

    public Mat onCameraFrameHandle(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        Mat rgba = inputFrame.rgba();
        // ??????????????????
        if (rotateMat == null) {
            rotateMat = Imgproc.getRotationMatrix2D(new Point(rgba.cols() / 2, rgba.rows() / 2), 90, 1);
        }
        Mat dstRgba = new Mat();
        Imgproc.warpAffine(rgba, dstRgba, rotateMat, dstRgba.size());
        // ????????????
        Mat flipMap = new Mat();
        Core.flip(dstRgba, flipMap, 1);
        mRgba = flipMap;

        if (beginTime == 0) {
            beginTime = System.currentTimeMillis();
        }
        if (frameHandleIng || showFail) {
            return mRgba;
        } else if (failByNet) {
            showFaceFail(inputFace ? "??????????????????????????????????????????????????????????????????" :
                    "????????????????????????????????????????????????????????????");
            return mRgba;
        } else if (failCount >= FAIL_COUNT_MAX) {
            showFaceFail(inputFace ? "?????????????????????????????????????????????????????????????????????????????????????????????" :
                    "??????????????????????????????????????????????????????????????????????????????");
            return mRgba;
        } else if (System.currentTimeMillis() - beginTime > HourUtil.LEN_MINUTE) {
            showFaceFail(inputFace ? "??????????????????????????????????????????????????????????????????" :
                    "???????????????????????????????????????????????????????????????????????????????????????");
            return mRgba;
        }
        frameHandleIng = true;
        Mat dstGray = new Mat();
        Mat gray = inputFrame.gray();
        Imgproc.warpAffine(gray, dstGray, rotateMat, dstGray.size());
        // ????????????
        Mat flipGrayMap = new Mat();
        Core.flip(dstGray, flipGrayMap, 1);
        mGray = flipGrayMap;

        if (mAbsoluteFaceSize == 0) {
            int height = mGray.rows();
            if (Math.round(height * mRelativeFaceSize) > 0) {
                mAbsoluteFaceSize = Math.round(height * mRelativeFaceSize);
            }
            mNativeDetector.setMinFaceSize(mAbsoluteFaceSize);
        }

        MatOfRect faces = new MatOfRect();

        if (mDetectorType == JAVA_DETECTOR) {
            if (mJavaDetector != null)
                mJavaDetector.detectMultiScale(mGray, faces, 1.1, 2, 2, // TODO: objdetect.CV_HAAR_SCALE_IMAGE
                        new Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new Size());

        } else if (mDetectorType == NATIVE_DETECTOR) {
            if (mNativeDetector != null)
                mNativeDetector.detect(mGray, faces);
        } else {
            Log.e(TAG, "Detection method is not selected!");
        }

        Rect[] facesArray = faces.toArray();
        Log.d(TAG, "facesArray.len = " + facesArray.length);
        if (facesArray.length == 1) {
            //  ?????????
//            onEyeDraw(mGray);
            setFaceHint(faceHint02);
            String fileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + "_" + System.currentTimeMillis();
            FaceUtil.setMaxWH(inputFace ? FaceSetUtil.INPUT_MAX : FaceSetUtil.LOGIN_MAX);
            boolean saveImage = FaceUtil.saveImageRgba(getContext(), mRgba, facesArray[0], fileName);
            Log.d(TAG, "saveImage = " + saveImage);
            if (saveImage) {
                loginOrInput(FaceUtil.getFilePath(mContext, fileName));
                setFaceHint(faceHint03);
            } else {
                // ???????????? ??????????????????
                frameHandleIng = false;
            }
        } else {
            setFaceHint(faceHint01);
            frameHandleIng = false;
        }
        // ?????????
//        for (int i = 0; i < facesArray.length; i++) {
//            Imgproc.rectangle(mRgba, facesArray[i].tl(), facesArray[i].br(), FACE_RECT_COLOR, 3);
//        }
        return mRgba;
    }

    /**
     * ????????????????????????
     *
     * @param s
     */
    private void showFaceFail(String s) {
        if (!showFail && mClFaceFail != null && mClFaceFail.getVisibility() != View.VISIBLE) {
            showFail = true;
            mMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mTvFaceFailHint != null) {
                        mTvFaceFailHint.setText(s);
                    }
                    mClFaceFail.setVisibility(View.VISIBLE);
                    mClFaceIng.setVisibility(View.INVISIBLE);
                    cancelLineAnim();
                    cancelBorderAnim();
                    mBtRetry.setOnClickListener(v -> {
                        resetFaceFlag();
                        mClFaceFail.setVisibility(View.GONE);
                        mClFaceIng.setVisibility(View.VISIBLE);
                        startAnim();
                    });
                }
            });
        }
    }

    /**
     * ??????????????????????????????????????????????????????
     *
     * @param text
     */
    private void setFaceHint(String text) {
        if (mTvFaceHint != null) {
            if (mTvFaceHint.getText().toString().equals(text)) {
                return;
            }
            if (System.currentTimeMillis() - changeFaceHintTime < 500) {
                changeFaceHintRunnable = () -> {
                    mTvFaceHint.setText(text);
                };
                mMainHandler.postDelayed(changeFaceHintRunnable, 500);
                return;
            }
            changeFaceHintTime = System.currentTimeMillis();
            if (changeFaceHintRunnable != null) {
                mMainHandler.removeCallbacks(changeFaceHintRunnable);
            }
            changeFaceHintRunnable = () -> {
                mTvFaceHint.setText(text);
            };
            mMainHandler.post(changeFaceHintRunnable);
        }
    }

    /**
     * ??????????????????????????????
     *
     * @param fileName
     */
    private void loginOrInput(String fileName) {
        LogUtil.d(TAG, "loginOrInput begin ====================================================== fileName = " + fileName);
        if (inputFace) {
            requestInput(fileName);
        } else {
            requestLogin(fileName);
        }
    }

    /**
     * ??????????????????
     *
     * @param avatarUrl
     */
    private void requestInput(String avatarUrl) {
        MultipartBodyFactory factory = MultipartBodyFactory.crete();
        // file
        if (!TextUtils.isEmpty(avatarUrl)) {
            factory.addImage("pic", avatarUrl);
        }
        LogUtil.d(TAG, "????????????????????????---------------------------------");
        failByNet = false;
        OkHttpUtil.request(ApiHost.getHost() + "face_login/face_enter", factory.build(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                FileUtil.deleteFile(avatarUrl);
                if (!isVisible() || EmptyUtil.isEmpty(getActivity())) {
                    return;
                }
                failByNet = true;
                failCount++;
                frameHandleIng = false;
                LogUtil.d(TAG, "????????????????????????---------------------------------failCount = " + failCount);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                FileUtil.deleteFile(avatarUrl);
                if (!isVisible() || EmptyUtil.isEmpty(getActivity())) {
                    return;
                }
                String json = response.body().string();
                LogUtil.d(TAG, "?????????????????? --- " + json);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (EmptyUtil.isEmpty(getActivity())) {
                            return;
                        }
                        try {
                            Type type = new TypeToken<HttpResult<CommonModel>>() {
                            }.getType();
                            HttpResult<CommonModel> result = GsonUtil.fromJson(json, type);
                            if (result != null && result.isSuccessful()) {
                                SharePreferenceUtil.setInt(MeFragment.KEY_INPUT_FACE + UserManager.getInstance().getUID(), 1);
                                ToastUtil.showLong("????????????");
                                mGoFinish = true;
                                getActivity().finish();
                                LogUtil.d(TAG, "????????????????????????---------------------------------");
                            } else {
                                LogUtil.d(TAG, "????????????????????????_01--------------------------------- failCount = " + failCount);
                                failCount++;
                                frameHandleIng = false;
                            }
                        } catch (Exception e) {
                            failCount++;
                            frameHandleIng = false;
                        }
                    }
                });
            }
        });
    }

    /**
     * ??????????????????
     *
     * @param avatarUrl
     */
    private void requestLogin(String avatarUrl) {
        MultipartBodyFactory factory = MultipartBodyFactory.crete();
        // file
        if (!TextUtils.isEmpty(avatarUrl)) {
            factory.addImage("pic", avatarUrl);
        }
        LogUtil.d(TAG, "????????????????????????---------------------------------");
        failByNet = false;
        OkHttpUtil.request(ApiHost.getHost() + "face_login/face_recognition", factory.build(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                FileUtil.deleteFile(avatarUrl);
                if (!isVisible() || EmptyUtil.isEmpty(getActivity())) {
                    return;
                }
                failByNet = true;
                failCount++;
                frameHandleIng = false;
                LogUtil.d(TAG, "???????????????????????? onFailure--------------------------------- failCount = " + failCount);
                LogUtil.d(TAG, "???????????????????????? onFailure--------------------------------- e = " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                FileUtil.deleteFile(avatarUrl);
                if (!isVisible() || EmptyUtil.isEmpty(getActivity())) {
                    return;
                }
                String json = response.body().string();
                LogUtil.d(TAG, "?????????????????? --- " + json);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (EmptyUtil.isEmpty(getActivity())) {
                            return;
                        }
                        try {
                            Type type = new TypeToken<HttpResult<TokenModel>>() {
                            }.getType();
                            HttpResult<TokenModel> result = GsonUtil.fromJson(json, type);
                            if (result != null && result.isSuccessful()) {
                                boolean loginSuccessful = LoginActivity.login(getActivity(), result.getData(), "");
                                if (loginSuccessful) {
                                    mGoFinish = true;
                                    LogUtil.d(TAG, "????????????????????????---------------------------------");
                                } else {
                                    showFaceFail("??????????????????????????????" + MyApplication.getApp().getResources().getString(R.string.this_device_no_auth_login) + "");
                                }
                            } else {
                                if (result != null) {
                                    if (HttpResult.isServerTimeNoValid(result.getCode())) {
                                        DialogFactory.createSimple(getActivity(), result.getMsg());
                                    } else {
                                        ToastUtil.showLong(result.getMsg());
                                    }
                                }
                                LogUtil.d(TAG, "????????????????????????--------------------------------- failCount = " + failCount);
                                failCount++;
                                frameHandleIng = false;
                            }
                        } catch (Exception e) {
                            failCount++;
                            frameHandleIng = false;
                        }
                    }
                });
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mJavaCamera2CircleView != null) {
            mJavaCamera2CircleView.disableView();
        }
    }

    @Override
    public void onDestroy() {
        if (mJavaCamera2CircleView != null) {
            mJavaCamera2CircleView.surfaceDestroyed(null);
        }
        cancelBorderAnim();
        cancelLineAnim();
        mMainHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    /**
     * ??????????????????
     *
     * @param image
     * @return
     */
    private boolean onEyeCheck2(Mat image) {
        // ????????????????????????
        MatOfRect faceDetections = new MatOfRect();

        if (mDetectorType == JAVA_DETECTOR) {
            if (mJavaEyeDetector != null)
                mJavaEyeDetector.detectMultiScale(image, faceDetections, 1.1, 2, 2, // TODO: objdetect.CV_HAAR_SCALE_IMAGE
                        new Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new Size());
        } else if (mDetectorType == NATIVE_DETECTOR) {
            if (mNativeEyeDetector != null)
                mNativeEyeDetector.detect(image, faceDetections);
        } else {
            Log.e(TAG, "Detection method is not selected!");
        }

        Log.d(TAG, String.format("Detected %s eyes", faceDetections.toArray().length));

        Rect[] rects = faceDetections.toArray();
        if (rects == null || rects.length < 2) {
            Log.d(TAG, "rects == null || rects.length < 1");
            return false;
        }

        return true;

    }

    private void onEyeDraw(Mat image) {
        // ????????????????????????
        MatOfRect faceDetections = new MatOfRect();
        // TODO: objdetect.CV_HAAR_SCALE_IMAGE
        if (mDetectorType == JAVA_DETECTOR) {
            if (mJavaEyeDetector != null)
                mJavaEyeDetector.detectMultiScale(image, faceDetections, 1.1, 2, 2,
                        new Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new Size());
        } else if (mDetectorType == NATIVE_DETECTOR) {
            if (mNativeEyeDetector != null)
                mNativeEyeDetector.detect(image, faceDetections);
        } else {
            Log.e(TAG, "Detection method is not selected!");
        }

        Log.d(TAG, String.format("Detected %s eyes", faceDetections.toArray().length));

        Rect[] rects = faceDetections.toArray();
        if (rects == null || rects.length < 2) {
            Log.d(TAG, "rects == null || rects.length < 2");
            return;
        }

        Rect eyea = rects[0];
        Rect eyeb = rects[1];

        Log.d(TAG, "a-???????????? " + eyea.x + " and " + eyea.y);
        Log.d(TAG, "b-???????????? " + eyeb.x + " and " + eyeb.y);

        //???????????????????????????
        double dy = (eyeb.y - eyea.y);
        double dx = (eyeb.x - eyea.x);
        double len = Math.sqrt(dx * dx + dy * dy);
        Log.d(TAG, "dx is " + dx);
        Log.d(TAG, "dy is " + dy);
        Log.d(TAG, "len is " + len);

        double angle = Math.atan2(Math.abs(dy), Math.abs(dx)) * 180.0 / Math.PI;
        Log.d(TAG, "angle is " + angle);

        for (Rect rect : rects) {
            Imgproc.rectangle(mRgba, new Point(rect.x, rect.y), new Point(rect.x
                    + rect.width, rect.y + rect.height), new Scalar(0, 255, 0));
        }
    }
}