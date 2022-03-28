package com.thfw.mobileheart.activity.login;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.thfw.base.base.IPresenter;
import com.thfw.base.models.TokenModel;
import com.thfw.base.net.CommonParameter;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.ToastUtil;
import com.thfw.mobileheart.R;
import com.thfw.mobileheart.activity.MainActivity;
import com.thfw.mobileheart.activity.organ.AskForSelectActivity;
import com.thfw.mobileheart.activity.settings.InfoActivity;
import com.thfw.mobileheart.constants.UIConfig;
import com.thfw.mobileheart.fragment.login.LoginByFaceFragment;
import com.thfw.mobileheart.fragment.login.MobileFragment;
import com.thfw.mobileheart.fragment.login.OtherLoginFragment;
import com.thfw.mobileheart.fragment.login.PasswordFragment;
import com.thfw.mobileheart.util.FragmentLoader;
import com.thfw.ui.base.BaseActivity;
import com.thfw.user.login.LoginStatus;
import com.thfw.user.login.UserManager;
import com.thfw.user.models.User;

import org.opencv.android.Static2Helper;

public class LoginActivity extends BaseActivity {

    public static final int BY_MOBILE = 0;
    public static final int BY_PASSWORD = 1;
    public static final int BY_FORGET = 2;
    public static final int BY_FACE = 3;
    public static final int BY_OTHER = 4;
    private int type;
    private FragmentLoader fragmentLoader;
    private AlertDialog mDialog;
    // 登录后播放唤醒动画
    public static final String KEY_LOGIN_BEGIN = "login.begin";
    public static final String KEY_LOGIN_BEGIN_TTS = "login.begin.tts";
    public static String INPUT_PHONE = "";

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
        // todo test
        CommonParameter.setOrganizationId("1");
        MainActivity.resetInit();
        type = getIntent().getIntExtra(KEY_DATA, BY_MOBILE);
        fragmentLoader = new FragmentLoader(getSupportFragmentManager(), R.id.fl_content);

        fragmentLoader.add(BY_MOBILE, new MobileFragment());
        fragmentLoader.add(BY_PASSWORD, new PasswordFragment());
        fragmentLoader.add(BY_FACE, new LoginByFaceFragment());
        fragmentLoader.add(BY_OTHER, new OtherLoginFragment());

        fragmentLoader.load(type);

        checkPermissions();
    }

    @Override
    public int getStatusBarColor() {
        return STATUSBAR_TRANSPARENT;
    }

    public FragmentLoader getFragmentLoader() {
        return fragmentLoader;
    }

    @Override
    public void initData() {
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

    @Override
    public void onResume() {
        super.onResume();
        Static2Helper.initOpenCV(true);
    }

    @Override
    public void onDestroy() {
        INPUT_PHONE = "";
        super.onDestroy();
    }


    public static void login(Activity activity, TokenModel data, String mobile) {
        if (data != null && !TextUtils.isEmpty(data.token)) {
            User user = new User();
            user.setToken(data.token);
            user.setMobile(mobile);
            user.setSetUserInfo(data.isSetUserInfo());
            user.setOrganization(data.organization);
            LogUtil.d("UserManager.getInstance().isLogin() = " + UserManager.getInstance().isLogin());
            if (data.isNoOrganization()) {
                // todo 手机加入组织机构比较复杂
                user.setLoginStatus(LoginStatus.LOGOUT_HIDE);
                UserManager.getInstance().login(user);
                AskForSelectActivity.startForResult(activity, true);
            } else if (data.isNoSetUserInfo()) {
                user.setLoginStatus(LoginStatus.LOGOUT_HIDE);
                UserManager.getInstance().login(user);
                InfoActivity.startActivityFirst(activity);
            } else {
                user.setLoginStatus(LoginStatus.LOGINED);
                UserManager.getInstance().login(user);
            }
            activity.finish();
        } else {
            ToastUtil.show("token 参数错误");
        }
    }
}