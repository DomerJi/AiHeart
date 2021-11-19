package com.example.zbar_code;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;

/**
 * zxing 识别成功率高，识别速度慢
 */
public class ZxingScanActivity extends AppCompatActivity implements QRCodeView.Delegate {
    public static final int REQUEST_CODE = 9;
    public static final String CODE_DATA = "code_data";
    private static final String TAG = ZbarScanActivity.class.getSimpleName();
    private static final int REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY = 666;
    private ZXingView mZXingView;

    public static void startActivityForResult(Activity context) {
        context.startActivityForResult(new Intent(context, ZxingScanActivity.class), 9);
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

        if (Utils.isCongCongRobot()) {
            setContentView(R.layout.activity_zxing_scan);
            Log.i(TAG, "聪聪机器人");
        } else if (Utils.isPad(ZxingScanActivity.this) || Utils.isHuaweiPad()) {
            setContentView(R.layout.activity_zxing_scan_pad);
        } else {
            setContentView(R.layout.activity_zxing_scan_ex);
            Log.i(TAG, "普通手机设备");
        }
        mZXingView = findViewById(R.id.zxingview);
        mZXingView.setDelegate(this);
        ImageView mIvClose = findViewById(R.id.iv_close);
        if (mIvClose != null) {
            mIvClose.setOnClickListener(v -> {
                finish();
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

//        mZXingView.startCamera(); // 打开后置摄像头开始预览，但是并未开始识别
        mZXingView.startCamera(Camera.CameraInfo.CAMERA_FACING_FRONT); // 打开前置摄像头开始预览，但是并未开始识别

        mZXingView.startSpotAndShowRect(); // 显示扫描框，并开始识别
    }

    @Override
    protected void onStop() {
        mZXingView.stopCamera(); // 关闭摄像头预览，并且隐藏扫描框
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mZXingView.onDestroy(); // 销毁二维码扫描控件
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
        String tipText = mZXingView.getScanBoxView().getTipText();
        String ambientBrightnessTip = "\n环境过暗，请打开闪光灯";
        if (isDark) {
            if (!tipText.contains(ambientBrightnessTip)) {
                mZXingView.getScanBoxView().setTipText(tipText + ambientBrightnessTip);
            }
        } else {
            if (tipText.contains(ambientBrightnessTip)) {
                tipText = tipText.substring(0, tipText.indexOf(ambientBrightnessTip));
                mZXingView.getScanBoxView().setTipText(tipText);
            }
        }
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        Log.e(TAG, "打开相机出错");
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }
}