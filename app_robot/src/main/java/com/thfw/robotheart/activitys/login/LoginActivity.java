package com.thfw.robotheart.activitys.login;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.thfw.base.base.IPresenter;
import com.thfw.base.net.CommonParameter;
import com.thfw.base.utils.SharePreferenceUtil;
import com.thfw.base.utils.ToastUtil;
import com.thfw.robotheart.R;
import com.thfw.robotheart.activitys.MainActivity;
import com.thfw.robotheart.constants.UIConfig;
import com.thfw.robotheart.fragments.login.LoginByFaceFragment;
import com.thfw.robotheart.fragments.login.LoginMobileCodeFragment;
import com.thfw.robotheart.fragments.login.LoginMobileFragment;
import com.thfw.robotheart.fragments.login.LoginPasswordFragment;
import com.thfw.robotheart.util.FragmentLoader;
import com.thfw.ui.base.BaseActivity;
import com.thfw.user.login.UserManager;

import org.opencv.android.Static2Helper;

public class LoginActivity extends BaseActivity {

    public static final int BY_MOBILE = 0;
    public static final int BY_PASSWORD = 1;
    public static final int BY_FORGET = 2;
    public static final int BY_MOBILE_CODE = 4;
    public static final int BY_FACE = 5;

    public static final String KEY_PHONE_NUMBER = "phone_number";
    // 登录后播放唤醒动画
    public static final String KEY_LOGIN_BEGIN = "login.begin";
    public static final String KEY_LOGIN_BEGIN_TTS = "login.begin.tts";
    public static String INPUT_PHONE = "";
    private int type;
    private FragmentLoader fragmentLoader;
    private AlertDialog mDialog;
    private boolean mOpenCvInited = false;

    public static void startActivity(Context context, int type) {
        context.startActivity(new Intent(context, LoginActivity.class).putExtra(KEY_DATA, type));
    }

    @Override
    public int getContentView() {
        return R.layout.activity_login;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initView() {
        type = getIntent().getIntExtra(KEY_DATA, BY_MOBILE);
        fragmentLoader = new FragmentLoader(getSupportFragmentManager(), R.id.fl_content);

        fragmentLoader.add(BY_MOBILE, new LoginMobileFragment());
        fragmentLoader.add(BY_PASSWORD, new LoginPasswordFragment());
        fragmentLoader.add(BY_MOBILE_CODE, new LoginMobileCodeFragment());
        fragmentLoader.add(BY_FACE, new LoginByFaceFragment());

        fragmentLoader.load(type);
        // 检查权限
        checkPermissions();
        if (!UserManager.getInstance().isLogin()) {
            SharePreferenceUtil.setBoolean(KEY_LOGIN_BEGIN, true);
            SharePreferenceUtil.setBoolean(KEY_LOGIN_BEGIN_TTS, true);
        } else {
            View view = findViewById(R.id.ll_back);
            view.setVisibility(View.VISIBLE);
            view.setOnClickListener(v -> {
                onBackPressed();
            });
        }
    }

    @Override
    public int getStatusBarColor() {
        return STATUSBAR_TRANSPARENT;
    }

    public FragmentLoader getFragmentLoader() {
        hideInput();
        return fragmentLoader;
    }

    @Override
    public void initData() {
        if (!CommonParameter.isValid()) {
            ToastUtil.show(R.string.valid_fail_organ_id);
        }
    }


    /**
     * 动态获取内存存储权限
     */
    public void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 验证是否许可权限
            for (String str : UIConfig.NEEDED_PERMISSION) {
                if (ContextCompat.checkSelfPermission(this, str) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, UIConfig.NEEDED_PERMISSION, 1);
                    break;
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) { // 选择了“始终允许”
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) {//用户选择了禁止不再询问
                        permissionDialog(true);
                    } else { // 选择禁止
                        permissionDialog(false);
                    }
                }
            }
        }
    }

    /**
     * 禁止权限后的弹框处理
     *
     * @param never
     */
    private void permissionDialog(boolean never) {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("权限申请")
                .setMessage("点击允许才可以使用相应功能哦")
                .setPositiveButton("去允许", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (mDialog != null && mDialog.isShowing()) {
                            mDialog.dismiss();
                        }
                        if (never) {// 从不询问，禁止
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            // 注意就是"package",不用改成自己的包名
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            startActivityForResult(intent, 1);
                        } else {
                            ActivityCompat.requestPermissions(LoginActivity.this, UIConfig.NEEDED_PERMISSION, 1);
                        }
                    }
                });

        mDialog = builder.create();
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();
    }

    /**
     * @return openCv 是否初始化成功
     */
    public boolean isOpenCvInited() {
        return mOpenCvInited;
    }

    @Override
    public void onResume() {
        super.onResume();
        Static2Helper.initOpenCV(true);
        MainActivity.resetInit();
    }

    @Override
    public void onDestroy() {
        INPUT_PHONE = "";
        super.onDestroy();
    }
}