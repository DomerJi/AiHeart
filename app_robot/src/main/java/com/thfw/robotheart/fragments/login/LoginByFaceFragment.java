package com.thfw.robotheart.fragments.login;

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
import com.thfw.base.utils.ToastUtil;
import com.thfw.base.utils.Util;
import com.thfw.robotheart.MyApplication;
import com.thfw.robotheart.R;
import com.thfw.robotheart.activitys.RobotBaseFragment;
import com.thfw.robotheart.activitys.WebActivity;
import com.thfw.robotheart.activitys.login.LoginActivity;
import com.thfw.robotheart.constants.AgreeOn;
import com.thfw.robotheart.constants.UIConfig;
import com.thfw.robotheart.util.Dormant;
import com.thfw.user.login.UserManager;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCamera2CircleView;
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
 * 登录
 */
public class LoginByFaceFragment extends RobotBaseFragment implements CameraBridgeViewBase.CvCameraViewListener2 {


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

    private Mat mRgba; //图像容器
    private Mat mGray;
    private File mCascadeFile;
    private File mCascadeEyeFile;
    private CascadeClassifier mJavaDetector;
    private CascadeClassifier mJavaEyeDetector;
    private DetectionBasedTracker mNativeDetector;
    private DetectionBasedTracker mNativeEyeDetector;


    private TextView mTvLoginByFace;
    private TextView mTvLoginByMobile;
    private TextView mTvFaceHint;
    private ConstraintLayout mClLoginCenter;
    private TextView mTvPageTitle;
    private LinearLayout mClFail;
    private TextView mTvFaceFailHint;
    private Button mBtRetry;
    private ConstraintLayout mClFace;
    private Runnable changeFaceHintRunnable;
    /**
     * 检测器类型
     */
    public static final int JAVA_DETECTOR = 0;
    public static final int NATIVE_DETECTOR = 1;
    // 最大失败数量
    private static final int FAIL_COUNT_MAX = 10;
    private static final Scalar FACE_RECT_COLOR = new Scalar(0, 255, 0, 255);
    // 动画扫描线上下标识
    private boolean mLineUpAnim = true;
    // true 录入人脸  false 人脸识别
    private boolean inputFace;
    private Handler mMainHandler = new Handler(Looper.getMainLooper());
    /**
     * 正在处理图像数据
     */
    private volatile boolean frameHandleIng = false;
    private volatile int failCount = 0;
    private volatile boolean failByNet = false;
    private volatile long beginTime;
    private volatile long changeFaceHintTime;
    // 提示语
    private String faceHint01 = "请正对屏幕，保持脸在取景框内";
    private String faceHint02 = "请靠近摄像头，保持双眼睁开";
    private String faceHint03 = "正在识别...";
    // 检测器
    private int mDetectorType = NATIVE_DETECTOR;
    private String[] mDetectorName;
    private float mRelativeFaceSize = 0.2f;
    private int mAbsoluteFaceSize = 0;

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


        mClBottom = (LinearLayout) findViewById(R.id.cl_bottom);
        mRivWechat = (RoundedImageView) findViewById(R.id.riv_wechat);
        mRivQq = (RoundedImageView) findViewById(R.id.riv_qq);
        mCbProduct = (CheckBox) findViewById(R.id.cb_product);
        mTvProductUser = (TextView) findViewById(R.id.tv_product_user);
        mTvProductMsg = (TextView) findViewById(R.id.tv_product_msg);
        mTvProductAgree = (TextView) findViewById(R.id.tv_product_agree);
        mTvFaceHint = (TextView) findViewById(R.id.tv_face_hint);

        mJavaCamera2CircleView = (JavaCamera2CircleView) findViewById(R.id.javaCamera2CircleView);
        mIvBorder = (ImageView) findViewById(R.id.iv_border);
        mIvLine = (ImageView) findViewById(R.id.iv_line);

        mTvLoginByPassword = (TextView) findViewById(R.id.tv_login_by_password);
        mTvLoginByFace = (TextView) findViewById(R.id.tv_login_by_face);
        mTvLoginByMobile = (TextView) findViewById(R.id.tv_login_by_mobile);
        mTvLoginByFace.setVisibility(View.GONE);
        mTvLoginByPassword.setOnClickListener(v -> {
            LoginActivity loginActivity = (LoginActivity) getActivity();
            loginActivity.getFragmentLoader().load(LoginActivity.BY_PASSWORD);
        });

        mTvLoginByMobile.setOnClickListener(v -> {
            LoginActivity loginActivity = (LoginActivity) getActivity();
            loginActivity.getFragmentLoader().load(LoginActivity.BY_MOBILE);
        });
        Util.addUnderLine(mTvProductUser, mTvProductMsg, mTvProductAgree);
        initAgreeClick();
        mClLoginCenter = (ConstraintLayout) findViewById(R.id.cl_login_center);
        mTvPageTitle = (TextView) findViewById(R.id.tv_page_title);
        mClFail = (LinearLayout) findViewById(R.id.cl_fail);
        mTvFaceFailHint = (TextView) findViewById(R.id.tv_face_fail_hint);
        mBtRetry = (Button) findViewById(R.id.bt_retry);
        mClFace = (ConstraintLayout) findViewById(R.id.cl_face);
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
        if (ContextCompat.checkSelfPermission(mContext, UIConfig.NEEDED_PERMISSION[0])
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        // opencv
        initializeOpenCVDependencies();
        initializeDetectEye();
        mJavaCamera2CircleView.setCvCameraViewListener(this);
        mJavaCamera2CircleView.setVisibility(SurfaceView.VISIBLE);
        mJavaCamera2CircleView.enableView();

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
        inputFace = UserManager.getInstance().isLogin();
        if (inputFace) {
            mTvLoginByMobile.setVisibility(View.INVISIBLE);
            mTvLoginByPassword.setVisibility(View.INVISIBLE);
            mTvLoginByPassword.setEnabled(false);
            mTvLoginByMobile.setEnabled(false);
            TextView mTvPageTitle = (TextView) findViewById(R.id.tv_page_title);
            mTvPageTitle.setText("人脸录入");
            mClBottom.setVisibility(View.GONE);

        }

    }

    /**
     * 重置刷脸标识
     */
    private void resetFaceFlag() {
        failCount = 0;
        beginTime = 0;
        failByNet = false;
        frameHandleIng = false;
    }

    @Override
    public void onVisible(boolean isVisible) {
        super.onVisible(isVisible);
        if (isVisible) {
            resetFaceFlag();
        }
        if (mJavaCamera2CircleView != null) {
            if (isVisible) {
                mIvBorder.post(new Runnable() {
                    @Override
                    public void run() {
                        startLineAnim();
                        startBorderAnim();
                    }
                });
                mJavaCamera2CircleView.enableView();
            } else {
                mJavaCamera2CircleView.disableView();
            }
        }
    }

    public void startLineAnim() {
        mIvLine.clearAnimation();
        int height = mJavaCamera2CircleView.getHeight() - mIvLine.getHeight();
        int mRCircle = mJavaCamera2CircleView.getHeight() / 2;

        ObjectAnimator lineAnimation = ObjectAnimator.ofFloat(mIvLine, "translationY",
                mLineUpAnim ? 0 : height, mLineUpAnim ? height : 0);
        lineAnimation.setDuration(3000);
        lineAnimation.setInterpolator(new LinearInterpolator());
        lineAnimation.addListener(new MyAnimationListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mLineUpAnim = !mLineUpAnim;
                if (isVisible()) {
                    startLineAnim();
                }

            }
        });
        lineAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                value = value + mIvLine.getHeight() / 2f;
                // 知道半径是R，圆心到直线的距离是d，
                // 那么这条直线的长度计算可以利用勾股定理
                // L=2√(R²-d²)
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
     * 初始化人脸检测
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
     * 初始化人眼检测
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
        Dormant.reset();
        if (beginTime == 0) {
            beginTime = System.currentTimeMillis();
        }
        if (frameHandleIng) {
            return inputFrame.rgba();
        } else if (failByNet) {
            showFaceFail(inputFace ? "人脸录入失败，当前网络异常，请检查网络状态。" :
                    "刷脸登录失败，当前网络异常，请检查网络状态。");
            return inputFrame.rgba();
        } else if (failCount >= FAIL_COUNT_MAX) {
            showFaceFail(inputFace ? "人脸录入失败，已经为您重试多次，要求面部无遮挡，光照好的环境。" :
                    "刷脸登录失败，已为您重试多次，您可以选择重新尝试或其他登录方式");
            return inputFrame.rgba();
        } else if (System.currentTimeMillis() - beginTime > HourUtil.LEN_MINUTE) {
            showFaceFail(inputFace ? "人脸录入超时，要求面部无遮挡，光照好的环境。" :
                    "刷脸登录超时，您可以选择重新尝试或其他登录方式");
            return inputFrame.rgba();
        }
        frameHandleIng = true;
        mRgba = inputFrame.rgba();
        mGray = inputFrame.gray();

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
            //  画眼睛
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
                // 保存失败 重新开始处理
                frameHandleIng = false;
            }
        } else {
            setFaceHint(faceHint01);
            frameHandleIng = false;
        }
        // 画脸型
//        for (int i = 0; i < facesArray.length; i++) {
//            Imgproc.rectangle(mRgba, facesArray[i].tl(), facesArray[i].br(), FACE_RECT_COLOR, 3);
//        }
        return mRgba;
    }

    /**
     * 显示刷脸失败弹框
     *
     * @param s
     */
    private void showFaceFail(String s) {
        if (mClFail != null && mClFail.getVisibility() != View.VISIBLE) {
            mMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mTvFaceFailHint != null) {
                        mTvFaceFailHint.setText(s);
                    }
                    mClFail.setVisibility(View.VISIBLE);
                    mClFace.setVisibility(View.INVISIBLE);
                    mTvFaceHint.setVisibility(View.INVISIBLE);
                    mBtRetry.setOnClickListener(v -> {
                        resetFaceFlag();
                        mClFail.setVisibility(View.GONE);
                        mClFace.setVisibility(View.VISIBLE);
                        mTvFaceHint.setVisibility(View.VISIBLE);
                    });
                }
            });
        }
    }

    /**
     * 提示用户当前刷脸或录入状态，引导用户
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
     * 人脸登录或者人脸录入
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
     * 请求录入人脸
     *
     * @param avatarUrl
     */
    private void requestInput(String avatarUrl) {
        MultipartBodyFactory factory = MultipartBodyFactory.crete();
        // file
        if (!TextUtils.isEmpty(avatarUrl)) {
            factory.addImage("pic", avatarUrl);
        }
        LogUtil.d(TAG, "人脸【录入】开始---------------------------------");
        failByNet = false;
        OkHttpUtil.request(ApiHost.getHost() + "face_login/face_enter", factory.build(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                FileUtil.deleteFile(avatarUrl);
                if (EmptyUtil.isEmpty(getActivity())) {
                    return;
                }
                failByNet = true;
                failCount++;
                frameHandleIng = false;
                LogUtil.d(TAG, "人脸【录入】失败---------------------------------failCount = " + failCount);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                FileUtil.deleteFile(avatarUrl);
                if (EmptyUtil.isEmpty(getActivity())) {
                    return;
                }
                String json = response.body().string();
                LogUtil.d(TAG, "人脸【录入】 --- " + json);
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
                                ToastUtil.showLong("录入成功");
                                getActivity().finish();
                                LogUtil.d(TAG, "人脸【录入】成功---------------------------------");
                            } else {
                                LogUtil.d(TAG, "人脸【录入】失败_01--------------------------------- failCount = " + failCount);
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
     * 请求人脸登录
     *
     * @param avatarUrl
     */
    private void requestLogin(String avatarUrl) {
        MultipartBodyFactory factory = MultipartBodyFactory.crete();
        // file
        if (!TextUtils.isEmpty(avatarUrl)) {
            factory.addImage("pic", avatarUrl);
        }
        LogUtil.d(TAG, "人脸【登录】开始---------------------------------");
        failByNet = false;
        OkHttpUtil.request(ApiHost.getHost() + "face_login/face_recognition", factory.build(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                FileUtil.deleteFile(avatarUrl);
                if (EmptyUtil.isEmpty(getActivity())) {
                    return;
                }
                failByNet = true;
                failCount++;
                frameHandleIng = false;

                LogUtil.d(TAG, "人脸【登录】失败 onFailure--------------------------------- failCount = " + failCount);
                LogUtil.d(TAG, "人脸【登录】失败 onFailure--------------------------------- e = " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                FileUtil.deleteFile(avatarUrl);
                if (EmptyUtil.isEmpty(getActivity())) {
                    return;
                }
                String json = response.body().string();
                LogUtil.d(TAG, "人脸【登录】 --- " + json);
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
                                    LogUtil.d(TAG, "人脸【登录】成功---------------------------------");
                                } else {
                                    showFaceFail("刷脸登录失败，原因：" + MyApplication.getApp().getResources().getString(R.string.this_device_no_auth_login) + "");
                                }
                            } else {
                                LogUtil.d(TAG, "人脸【登录】失败--------------------------------- failCount = " + failCount);
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
        if (mIvLine != null) {
            mIvLine.clearAnimation();
        }
        if (mIvBorder != null) {
            mIvBorder.clearAnimation();
        }
        if (borderAnimation != null) {
            borderAnimation.cancel();
        }
        mMainHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }


    /**
     * ======================== 检测画框 ================================
     */

    /**
     * 检测人脸眼睛
     *
     * @param image
     * @return
     */
    private boolean onEyeCheck2(Mat image) {
        // 在图片中检测人脸
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
        // 在图片中检测人脸
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

        Log.d(TAG, "a-中心坐标 " + eyea.x + " and " + eyea.y);
        Log.d(TAG, "b-中心坐标 " + eyeb.x + " and " + eyeb.y);

        //获取两个人眼的角度
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

    /**
     * ======================== 检测画框 ================================ 结束
     */

}