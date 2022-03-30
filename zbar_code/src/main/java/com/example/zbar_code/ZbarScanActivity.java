package com.example.zbar_code;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.listener.OnResultCallbackListener;
import com.luck.picture.lib.tools.ToastUtils;

import java.util.List;

import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zbar.ZBarView;
import cn.bingoogolapple.qrcode.zxing.QRCodeDecoder;

/**
 * zbar识别快识别成功率低， 非常规二维码识别失败
 */
public class ZbarScanActivity extends AppCompatActivity implements QRCodeView.Delegate {
    public static final int REQUEST_CODE = 87;
    public static final String CODE_DATA = "code_data";
    public static final String CAMERA_FRONT_BACK = "front_back";
    private static final String TAG = ZbarScanActivity.class.getSimpleName();
    private static final int REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY = 666;
    private ZBarView mZBarView;
    private ImageView mIvSelectAlbum;

    public static void startActivityForResult(Activity context) {
        context.startActivityForResult(new Intent(context, ZbarScanActivity.class), REQUEST_CODE);
    }

    public static void startActivityForResult(Activity context, boolean front) {
        context.startActivityForResult(new Intent(context, ZbarScanActivity.class)
                .putExtra(CAMERA_FRONT_BACK, front), REQUEST_CODE);
    }

    public void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION //这里删除的话  可以解决华为虚拟按键的覆盖
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);//这里删除的话
        }
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_test_scan_ex);
        Log.i(TAG, "普通手机设备");
        mZBarView = findViewById(R.id.zbarview);
        mZBarView.setDelegate(this);
        mIvSelectAlbum = findViewById(R.id.iv_select_album);
        mIvSelectAlbum.setOnClickListener(v -> {
            showAlbum();
        });

        ImageView mIvClose = findViewById(R.id.iv_close);
        if (mIvClose != null) {
            mIvClose.setOnClickListener(v -> {
                finish();
            });
        }
        initFlash();
    }

    private void initFlash() {
        ImageView mIvFlash = findViewById(R.id.iv_flash);
        if (mIvFlash == null) {
            return;
        }
        mIvFlash.setVisibility(Utils.hasFlash(mIvFlash.getContext()) ? View.VISIBLE : View.GONE);
        mIvFlash.setOnClickListener(v -> {
            mIvFlash.setSelected(!mIvFlash.isSelected());
            if (mIvFlash.isSelected()) {
                mZBarView.openFlashlight();
            } else {
                mZBarView.closeFlashlight();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        boolean front = getIntent().getBooleanExtra(CAMERA_FRONT_BACK, false);
        if (front) {
            // 打开前置摄像头开始预览，但是并未开始识别
            mZBarView.startCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
        } else {
            // 打开后置摄像头开始预览，但是并未开始识别
            mZBarView.startCamera();
        }
        mZBarView.startSpotAndShowRect(); // 显示扫描框，并开始识别
    }

    @Override
    protected void onStop() {
        mZBarView.stopCamera(); // 关闭摄像头预览，并且隐藏扫描框
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mZBarView.onDestroy(); // 销毁二维码扫描控件
        super.onDestroy();
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        Log.i(TAG, "result:" + result);
        setTitle("扫描结果为：" + result);
        vibrate();

        setResult(RESULT_OK, new Intent().putExtra(CODE_DATA, result));
        finish();
    }

    @Override
    public void onCameraAmbientBrightnessChanged(boolean isDark) {
        // 这里是通过修改提示文案来展示环境是否过暗的状态，接入方也可以根据 isDark 的值来实现其他交互效果
        String tipText = mZBarView.getScanBoxView().getTipText();
        String ambientBrightnessTip = "\n环境过暗，请打开闪光灯";
        if (isDark) {
            if (!tipText.contains(ambientBrightnessTip)) {
                mZBarView.getScanBoxView().setTipText(tipText + ambientBrightnessTip);
            }
        } else {
            if (tipText.contains(ambientBrightnessTip)) {
                tipText = tipText.substring(0, tipText.indexOf(ambientBrightnessTip));
                mZBarView.getScanBoxView().setTipText(tipText);
            }
        }
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        Log.e(TAG, "打开相机出错");
    }

    /**
     * 选择图片
     */
    private void showAlbum() {
        //参数很多，根据需要添加
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                .imageEngine(new GlideImageEngine())
                .maxSelectNum(1)// 最大图片选择数量
                .minSelectNum(1) // 最小选择数量
                .imageSpanCount(4)// 每行显示个数
                .selectionMode(PictureConfig.SINGLE)// 多选 or 单选PictureConfig.MULTIPLE : PictureConfig.SINGLE
                .isPreviewImage(true)// 是否可预览图片
                .isCamera(false)// 是否显示拍照按钮
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                .isCompress(false)// 是否压缩
                .withAspectRatio(1, 1)// 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                .isEnableCrop(false)
                .videoMaxSecond(15) // 过滤掉15秒以上的视频
                .videoMinSecond(2) // 过滤掉2秒以下的视频
                .rotateEnabled(true) // 裁剪是否可旋转图片
                .forResult(new OnResultCallbackListener<LocalMedia>() {
                    @Override
                    public void onResult(List<LocalMedia> result) {
                        Log.e(TAG, "onResult = " + result.size());
                        String avatarUrl = result.get(0).getRealPath();
                        Log.e(TAG, "avatarUrl = " + avatarUrl + "_" + result.get(0).toString());
                        String qRCodeStr = QRCodeDecoder.syncDecodeQRCode(avatarUrl);
                        Log.e(TAG, "qRCodeStr = " + qRCodeStr);
                        if (TextUtils.isEmpty(qRCodeStr)) {
                            ToastUtils.s(ZbarScanActivity.this, "没有检测到二维码");
                        } else {
                            onScanQRCodeSuccess(qRCodeStr);
                        }
                    }

                    @Override
                    public void onCancel() {
                    }
                });

    }

}