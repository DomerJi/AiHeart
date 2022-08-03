package com.example.zbar_code;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Camera;
import android.net.Uri;
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

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import cn.bingoogolapple.qrcode.core.BGAQRCodeUtil;
import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.QRCodeDecoder;
import cn.bingoogolapple.qrcode.zxing.ZXingView;

/**
 * zxing 识别成功率高，识别速度慢
 */
public class ZxingScanActivity extends AppCompatActivity implements QRCodeView.Delegate {
    public static final int REQUEST_CODE = 87;
    public static final String CAMERA_FRONT_BACK = "front_back";
    public static final String CODE_DATA = "code_data";
    private static final String TAG = ZxingScanActivity.class.getSimpleName();
    private static final int REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY = 666;
    private ZXingView mZXingView;
    private ImageView mIvSelectAlbum;

    public static void startActivityForResult(Activity context) {
        context.startActivityForResult(new Intent(context, ZxingScanActivity.class), REQUEST_CODE);
    }

    public static void startActivityForResult(Activity context, boolean front) {
        context.startActivityForResult(new Intent(context, ZxingScanActivity.class)
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

        setContentView(R.layout.activity_zxing_scan_ex);
        mZXingView = findViewById(R.id.zxingview);
        mZXingView.setDelegate(this);
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
                mZXingView.openFlashlight();
            } else {
                mZXingView.closeFlashlight();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        boolean front = getIntent().getBooleanExtra(CAMERA_FRONT_BACK, false);
        if (front) {
            // 打开前置摄像头开始预览，但是并未开始识别
            mZXingView.startCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
        } else {
            // 打开后置摄像头开始预览，但是并未开始识别
            mZXingView.startCamera();
        }
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
                        Log.e(TAG, "onResult.size = " + result.size());
                        Log.e(TAG, "onResult = " + result.get(0).toString());
                        String avatarUrl = result.get(0).getRealPath();
                        Log.e(TAG, "avatarUrl = " + avatarUrl);

                        Bitmap bitmap = BGAQRCodeUtil.getDecodeAbleBitmap(avatarUrl);
                        Log.e(TAG, "bitmap = " + bitmap);
                        if (bitmap != null) {
                            String url = QRCodeDecoder.syncDecodeQRCode(bitmap);
                            Log.e(TAG, "bitmap2 url = " + url + "jsp");
                            if (!TextUtils.isEmpty(url)) {
                                onScanQRCodeSuccess(url);
                            } else {
                                ToastUtils.s(ZxingScanActivity.this, "没有检测到二维码");
                            }
                            return;
                        }
                        InputStream imageStream = null;
                        try {
                            Uri uri = Uri.parse(result.get(0).getPath());
                            imageStream = getContentResolver().openInputStream(uri);
                            Bitmap bitmap2 = BitmapFactory.decodeStream(imageStream);
                            Log.e(TAG, "bitmap2 = " + bitmap2);
                            String url = QRCodeDecoder.syncDecodeQRCode(bitmap2);
                            Log.e(TAG, "bitmap2 url = " + url + "jsp");
                            if (!TextUtils.isEmpty(url)) {
                                onScanQRCodeSuccess(url);
                            } else {
                                ToastUtils.s(ZxingScanActivity.this, "没有检测到二维码");
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "bitmap2 ee = " + e.getMessage());
                        } finally {
                            if (imageStream != null) {
                                try {
                                    imageStream.close();
                                } catch (IOException e) {
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancel() {
                    }
                });

    }
}