package com.thfw.robotheart.fragments.login;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.makeramen.roundedimageview.RoundedImageView;
import com.thfw.base.base.IPresenter;
import com.thfw.base.face.MyAnimationListener;
import com.thfw.base.room.face.Face;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.base.utils.GsonUtil;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.StringUtil;
import com.thfw.base.utils.ToastUtil;
import com.thfw.base.utils.Util;
import com.thfw.robotheart.MyApplication;
import com.thfw.robotheart.R;
import com.thfw.robotheart.activitys.RobotBaseFragment;
import com.thfw.robotheart.activitys.WebActivity;
import com.thfw.robotheart.activitys.login.LoginActivity;
import com.thfw.robotheart.constants.AgreeOn;
import com.thfw.robotheart.constants.UIConfig;
import com.thfw.user.login.LoginStatus;
import com.thfw.user.login.User;
import com.thfw.user.login.UserManager;

import org.bytedeco.opencv.opencv_face.FisherFaceRecognizer;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCamera2CircleView;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.helper.FaceUtil;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.samples.facedetect.DetectionBasedTracker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 登录
 */
public class LoginByFaceFragment extends RobotBaseFragment implements CameraBridgeViewBase.CvCameraViewListener2 {

    public static final int JAVA_DETECTOR = 0;
    public static final int NATIVE_DETECTOR = 1;
    private static final int FAIL_COUNT_MAX = 200;
    private static final Scalar FACE_RECT_COLOR = new Scalar(0, 255, 0, 255);
    private LinearLayout mLlLoginCenter;
    private TextView mTvLoginByPassword;
    private TextView mTvLoginByCode;
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
    private boolean mLineUpAnim = true;
    // true 录入人脸  false 人脸识别
    private boolean inputFace;
    /**
     * 正在处理图像数据
     */
    private boolean frameHandleIng = false;
    private int failCount = 0;
    private Mat mRgba; //图像容器
    private Mat mGray;
    private File mCascadeFile;
    private File mCascadeEyeFile;
    private CascadeClassifier mJavaDetector;
    private CascadeClassifier mJavaEyeDetector;
    private DetectionBasedTracker mNativeDetector;
    private DetectionBasedTracker mNativeEyeDetector;
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

        mLlLoginCenter = (LinearLayout) findViewById(R.id.ll_login_center);
        mTvLoginByPassword = (TextView) findViewById(R.id.tv_login_by_password);
        mTvLoginByCode = (TextView) findViewById(R.id.tv_login_by_code);
        mClBottom = (LinearLayout) findViewById(R.id.cl_bottom);
        mRivWechat = (RoundedImageView) findViewById(R.id.riv_wechat);
        mRivQq = (RoundedImageView) findViewById(R.id.riv_qq);
        mCbProduct = (CheckBox) findViewById(R.id.cb_product);
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
        mJavaCamera2CircleView = (JavaCamera2CircleView) findViewById(R.id.javaCamera2CircleView);
        mIvBorder = (ImageView) findViewById(R.id.iv_border);
        mIvLine = (ImageView) findViewById(R.id.iv_line);
        Util.addUnderLine(mTvProductUser, mTvProductMsg, mTvProductAgree);
        initAgreeClick();
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
            mTvLoginByCode.setVisibility(View.INVISIBLE);
            mTvLoginByPassword.setVisibility(View.INVISIBLE);
            mTvLoginByPassword.setEnabled(false);
            mTvLoginByCode.setEnabled(false);
            TextView mTvPageTitle = (TextView) findViewById(R.id.tv_page_title);
            mTvPageTitle.setText("人脸录入");
            mClBottom.setVisibility(View.GONE);

        }
        mIvBorder.post(new Runnable() {
            @Override
            public void run() {
                startLineAnim();
            }
        });
    }

    @Override
    public void onVisible(boolean isVisible) {
        super.onVisible(isVisible);
        if (mJavaCamera2CircleView != null) {
            if (isVisible) {
                mJavaCamera2CircleView.enableView();
            } else {
                mJavaCamera2CircleView.disableView();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mJavaCamera2CircleView != null) {
            mJavaCamera2CircleView.disableView();
        }
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
                // L=2√(R²-d²)
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
        LogUtil.d(TAG, "frameHandleIng = " + frameHandleIng);
        LogUtil.d(TAG, "failCount >= FAIL_COUNT_MAX " + (failCount >= FAIL_COUNT_MAX));

        if (frameHandleIng || failCount >= FAIL_COUNT_MAX) {
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
                        new org.opencv.core.Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new org.opencv.core.Size());

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
            onEyeDraw(mGray);
            if (!inputFace || onEyeCheck2(mGray)) {
                String fileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                boolean saveImage = FaceUtil.saveImage(getContext(), mRgba, facesArray[0], fileName, true);
                Log.d(TAG, "saveImage = " + saveImage);
                frameHandleIng = saveImage;
                if (saveImage) {
                    loginOrInput(fileName);
                } else {
                    // 保存失败 重新开始处理
                    frameHandleIng = false;
                }
            } else {
                frameHandleIng = false;
            }
        } else {
            frameHandleIng = false;
        }
        // 画脸型
        for (int i = 0; i < facesArray.length; i++) {
            Imgproc.rectangle(mRgba, facesArray[i].tl(), facesArray[i].br(), FACE_RECT_COLOR, 3);
        }
        return mRgba;
    }

    /**
     * 人脸登录或者人脸录入
     *
     * @param fileName
     */
    private void loginOrInput(String fileName) {
        LogUtil.d(TAG, "loginOrInput begin ======================================================");
        if (inputFace) {
            User user = UserManager.getInstance().getUser();
            Face face = new Face(user.getMobile(), user.getToken(), FaceUtil.getFilePath(getContext(), fileName));
            MyApplication.getDatabase().faceDao().insert(face);
            List<Face> faceList = MyApplication.getDatabase().faceDao().getAll();
            for (Face f : faceList) {
                if (StringUtil.contentEquals(face.getUid(), f.getUid())) {
                    ToastUtil.show("人脸录入成功!");
                    LogUtil.d(TAG, "人脸录入成功!======================================================");
                    LogUtil.d(TAG, "人脸录入成功!======================================================");
                    LogUtil.d(TAG, "人脸录入成功!======================================================");
                    break;
                }
            }

//            List<Mat> mats = new ArrayList<>();
//            mats.add(Imgcodecs.imread(fileName));
//            Mat lables = new Mat(1, 1, CV_32SC1);//对应20个标签值
//            FisherFaceRecognizer faceRecognizer = FisherFaceRecognizer.create();
//            faceRecognizer.train(mats, lables);
//            faceRecognizer.save(FaceUtil.getFilePath(mContext, "FisherRecognize.xml"));
            frameHandleIng = false;
        } else {
            List<Face> faceList = MyApplication.getDatabase().faceDao().getAll();
            LogUtil.d(TAG, "faceList[] = " + GsonUtil.toJson(faceList));
            FisherFaceRecognizer faceRecognizer = FisherFaceRecognizer.create();
            String filename = "FisherRecognize.xml";

            File file = new File(mContext.getApplicationContext().getFilesDir(), fileName);

            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            LogUtil.d(TAG, "filename = " + filename + "____" + file.exists());
            LogUtil.d(TAG, "filename = " + file.getAbsolutePath() + File.separator + filename);
//            faceRecognizer.read(file.getAbsolutePath() + File.separator + filename);
//            faceRecognizer.read(filename);
//            int lable = faceRecognizer.predict_label(Imgcodecs.imread(fileName));
//            ToastUtil.show("lable = " + lable);
//            LogUtil.d(TAG, "lable = " + lable);

            if (EmptyUtil.isEmpty(faceList)) {
                frameHandleIng = false;
                ToastUtil.show("本地无人脸数据！！！！！");
                LogUtil.d(TAG, "本地无人脸数据！！！！！!======================================================");
                LogUtil.d(TAG, "本地无人脸数据！！！！！!======================================================");
                LogUtil.d(TAG, "本地无人脸数据！！！！！!======================================================");
                return;
            }

            Face loginFace = null;
            for (Face f : faceList) {
//                double lv = com.kongqw.util.FaceUtil.compare(mContext, f.getFileName(), FaceUtil.getFilePath(getContext(), fileName));
                double lv = FaceUtil.CmpPic(f.getFileName(), FaceUtil.getFilePath(getContext(), fileName));
                Log.d("FaceUtil", "user -> mobile = " + f.getUid());
                if (lv > 0.5) {
                    loginFace = f;
                    break;
                }
            }
            if (loginFace != null) {
                ToastUtil.show("人脸【登录】成功！->" + loginFace.getUid());
                LogUtil.d(TAG, "人脸【登录】成功！！！！！!======================================================" + loginFace.getUid());
                LogUtil.d(TAG, "人脸【登录】成功！！！！！!======================================================" + loginFace.getUid());
                LogUtil.d(TAG, "人脸【登录】成功！！！！！!======================================================" + loginFace.getUid());
                loginByFace(loginFace);
                frameHandleIng = false;
            } else {
                failCount++;
                frameHandleIng = false;
                if (failCount >= FAIL_COUNT_MAX) {
                    ToastUtil.show("人脸【登录】失败！！！！！");
                }
                LogUtil.d(TAG, "人脸【登录】失败！！！！！!======================================================");
                LogUtil.d(TAG, "人脸【登录】失败！！！！！!======================================================");
                LogUtil.d(TAG, "人脸【登录】失败！！！！！!======================================================");

            }
        }
    }

    private void loginByFace(Face loginFace) {
        User user = new User();
        user.setToken(loginFace.getToken());
        user.setMobile(loginFace.getUid());
        user.setLoginStatus(LoginStatus.LOGINED);
        UserManager.getInstance().login(user);
//        if (data.isNoOrganization()) {
//            SelectOrganizationActivity.startActivity(mContext, true);
//        }
        LogUtil.d(TAG, "UserManager.getInstance().isLogin() = " + UserManager.getInstance().isLogin());
        getActivity().finish();
    }

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
                        new org.opencv.core.Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new org.opencv.core.Size());
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
                        new org.opencv.core.Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new org.opencv.core.Size());
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
}