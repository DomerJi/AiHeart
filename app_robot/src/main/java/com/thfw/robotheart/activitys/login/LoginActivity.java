package com.thfw.robotheart.activitys.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.luck.picture.lib.tools.PictureFileUtils;
import com.opensource.svgaplayer.SVGAImageView;
import com.thfw.base.ContextApp;
import com.thfw.base.base.IPresenter;
import com.thfw.base.models.TokenModel;
import com.thfw.base.net.CommonParameter;
import com.thfw.base.utils.ClickCountUtils;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.PermissionUtil;
import com.thfw.base.utils.RegularUtil;
import com.thfw.base.utils.SharePreferenceUtil;
import com.thfw.base.utils.ToastUtil;
import com.thfw.robotheart.MyApplication;
import com.thfw.robotheart.R;
import com.thfw.robotheart.activitys.MainActivity;
import com.thfw.robotheart.activitys.RobotBaseActivity;
import com.thfw.robotheart.activitys.me.InfoActivity;
import com.thfw.robotheart.activitys.me.MeActivity;
import com.thfw.robotheart.activitys.me.PrivateSetActivity;
import com.thfw.robotheart.activitys.me.SelectOrganizationActivity;
import com.thfw.robotheart.activitys.set.SettingActivity;
import com.thfw.robotheart.constants.AnimFileName;
import com.thfw.robotheart.constants.UIConfig;
import com.thfw.robotheart.fragments.login.LoginByFaceFragment;
import com.thfw.robotheart.fragments.login.LoginMobileCodeFragment;
import com.thfw.robotheart.fragments.login.LoginMobileFragment;
import com.thfw.robotheart.fragments.login.LoginPasswordFragment;
import com.thfw.robotheart.receiver.BootCompleteReceiver;
import com.thfw.robotheart.robot.RobotUtil;
import com.thfw.robotheart.util.DialogRobotFactory;
import com.thfw.robotheart.util.FragmentLoader;
import com.thfw.ui.dialog.TDialog;
import com.thfw.ui.dialog.base.BindViewHolder;
import com.thfw.ui.voice.tts.TtsHelper;
import com.thfw.ui.voice.tts.TtsModel;
import com.thfw.ui.voice.wakeup.WakeupHelper;
import com.thfw.user.login.LoginStatus;
import com.thfw.user.login.UserManager;
import com.thfw.user.models.User;

import org.opencv.android.Static2Helper;

import java.util.ArrayList;

public class LoginActivity extends RobotBaseActivity {

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
    private TDialog mPermissionDialog;
    private boolean checkPermissionFirst = true;

    public static void startActivity(Context context, int type) {
        context.startActivity(new Intent(context, LoginActivity.class).putExtra(KEY_DATA, type));
    }

    public static boolean login(Activity activity, TokenModel data, String mobile) {
        if (data != null && !TextUtils.isEmpty(data.token)) {
            User user = new User();
            user.setToken(data.token);
            if (RegularUtil.isPhone(mobile)) {
                user.setMobile(mobile);
            }
            user.setSetUserInfo(data.isSetUserInfo());
            user.setOrganization(data.organization);
            user.setAuthTypeList(data.getAuthType());
            if (!data.isNoOrganization()) {
                if (EmptyUtil.isEmpty(data.getAuthType())
                        || !data.getAuthType().contains(ContextApp.getDeviceTypeStr())) {
                    DialogRobotFactory.createSimple((FragmentActivity) activity,
                            MyApplication.getApp().getResources().getString(R.string.this_device_no_auth_login));
                    return false;
                }
            }
            LogUtil.d("UserManager.getInstance().isLogin() = " + UserManager.getInstance().isLogin());
            if (data.isNoOrganization()) {
                user.setLoginStatus(LoginStatus.LOGOUT_HIDE);
                UserManager.getInstance().login(user);
                SelectOrganizationActivity.startActivity(activity, true);
            } else if (data.isNoSetUserInfo()) {
                user.setLoginStatus(LoginStatus.LOGOUT_HIDE);
                UserManager.getInstance().login(user);
                InfoActivity.startActivityFirst(activity);
            } else {
                user.setLoginStatus(LoginStatus.LOGINED);
                UserManager.getInstance().login(user);
            }
            activity.finish();
            return true;
        } else {
            ToastUtil.show("token 参数错误");
            return false;
        }
    }

    /**
     * 检测不到有效的机构编码，请联系管理员解决
     *
     * @param activity
     */
    public static void showOrganIdNoValid(FragmentActivity activity) {
        String organId = CommonParameter.getOrganizationId();
        DialogRobotFactory.createSimple(activity, "设备状态", "无效的机构编码，请联系管理员解决！"
                + "\n当前机构编码：" + (TextUtils.isEmpty(organId) ? "空" : organId));
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
        MeActivity.resetInitFaceState();
        type = getIntent().getIntExtra(KEY_DATA, BY_MOBILE);

        findViewById(R.id.titleBarView).setOnClickListener(v -> {
            if (ClickCountUtils.click(10)) {
                startActivity(new Intent(mContext, PrivateSetActivity.class));
            }
        });
        findViewById(R.id.fl_content).setOnClickListener(v -> {
            if (ClickCountUtils.click(10)) {
                if (LogUtil.switchLogEnable()) {
                    ToastUtil.show("Log调试 -> 开启");
                } else {
                    ToastUtil.show("Log调试 -> 关闭");
                }
            }
        });
        fragmentLoader = new FragmentLoader(getSupportFragmentManager(), R.id.fl_content);

        fragmentLoader.add(BY_MOBILE, new LoginMobileFragment());
        fragmentLoader.add(BY_PASSWORD, new LoginPasswordFragment());
        fragmentLoader.add(BY_MOBILE_CODE, new LoginMobileCodeFragment());
        fragmentLoader.add(BY_FACE, new LoginByFaceFragment());
        PictureFileUtils.deleteAllCacheDirFile(mContext);
        fragmentLoader.load(type);
        if (UserManager.getInstance().isTrueLogin()) {
            View view = findViewById(R.id.ll_back);
            view.setVisibility(View.VISIBLE);
            view.setOnClickListener(v -> {
                onBackPressed();
            });
        } else {
            BootCompleteReceiver.checkBootCompleteAnim(this);
            MainActivity.setShowLoginAnim(true);
            SharePreferenceUtil.setBoolean(KEY_LOGIN_BEGIN, true);
            SharePreferenceUtil.setBoolean(KEY_LOGIN_BEGIN_TTS, true);
            // 机器人登录页面显示设置按钮
            if (RobotUtil.isInstallRobot()) {
                ImageView mIvSet = findViewById(R.id.iv_set);
                mIvSet.setVisibility(View.VISIBLE);
                mIvSet.setOnClickListener(v -> {
                    startActivity(new Intent(mContext, SettingActivity.class));
                });
            }
        }
    }

    @Override
    public void initData() {

    }

    @Override
    public int getStatusBarColor() {
        return STATUSBAR_TRANSPARENT;
    }

    public FragmentLoader getFragmentLoader() {
        hideInput();
        return fragmentLoader;
    }

    /**
     * 动态获取内存存储权限
     */
    public boolean checkPermissions() {
        boolean has = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 验证是否许可权限
            for (String str : UIConfig.NEEDED_PERMISSION) {
                if (ContextCompat.checkSelfPermission(this, str) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, UIConfig.NEEDED_PERMISSION, 1);
                    has = false;
                    break;
                }
            }
        }
        return has;
    }

    /**
     * 动态获取内存存储权限
     */
    public boolean checkPermissionsNoRequest() {
        boolean has = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 验证是否许可权限
            for (String str : UIConfig.NEEDED_PERMISSION) {
                if (ContextCompat.checkSelfPermission(this, str) != PackageManager.PERMISSION_GRANTED) {
                    has = false;
                    break;
                }
            }
        }
        return has;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) { // 选择了“始终允许”
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) {//用户选择了禁止不再询问
                        permissionDialog(true);
                        break;
                    } else { // 选择禁止
                        permissionDialog(false);
                        break;
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
        if (mPermissionDialog != null) {
            mPermissionDialog.dismiss();
        }
        mPermissionDialog = DialogRobotFactory.createCustomDialog(this, new DialogRobotFactory.OnViewCallBack() {
            @Override
            public void callBack(TextView mTvTitle, TextView mTvHint, TextView mTvLeft, TextView mTvRight, View mVLineVertical) {
                mTvTitle.setText("权限申请");
                mTvLeft.setVisibility(View.GONE);
                mVLineVertical.setVisibility(View.GONE);
                mTvRight.setText("去允许");
                mTvRight.setBackgroundResource(R.drawable.dialog_button_selector);
                ArrayList<String> noGrantedPermission = new ArrayList<>();
                for (String str : UIConfig.NEEDED_PERMISSION) {
                    if (ContextCompat.checkSelfPermission(LoginActivity.this, str)
                            != PackageManager.PERMISSION_GRANTED) {
                        noGrantedPermission.add(str);
                    }
                }
                mTvHint.setText(PermissionUtil.getInfo(noGrantedPermission.toArray(new String[noGrantedPermission.size()])));
                mTvHint.setGravity(Gravity.LEFT);
            }

            @Override
            public void onViewClick(BindViewHolder viewHolder, View view, TDialog tDialog) {
                if (mPermissionDialog != null) {
                    mPermissionDialog.dismiss();
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
        }, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        Static2Helper.initOpenCV(true);
        MainActivity.resetInit();
        if (!checkPermissionsNoRequest()) {
            if (checkPermissionFirst) {
                checkPermissions();
                checkPermissionFirst = false;
            } else {
                permissionDialog(true);
            }
        } else {
            checkOrganDialog();
        }
        wakeup();
    }

    /**
     * 测试
     */
    private void wakeup() {
        if (RobotUtil.isInstallRobot()) {
            if (WakeupHelper.getInstance().isIng()) {
                return;
            }
            WakeupHelper.getInstance().setWakeUpListener(new WakeupHelper.OnWakeUpListener() {
                @Override
                public void onWakeup(int angle, int beam) {
                    RobotUtil.wakeup(angle, beam);
                }

                @Override
                public void onError() {

                }
            });
            WakeupHelper.getInstance().start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (RobotUtil.isInstallRobot() && WakeupHelper.getInstance().isIng()) {
            WakeupHelper.getInstance().stop();
        }
    }

    @Override
    public void onDestroy() {
        INPUT_PHONE = "";
        super.onDestroy();
    }

    private void checkOrganDialog() {
        if (!CommonParameter.isValid() && checkPermissionsNoRequest()) {
            showOrganIdNoValid(LoginActivity.this);
        }
    }


    public static void onLoginFail(FragmentActivity activity) {
        if (EmptyUtil.isEmpty(activity)) {
            return;
        }
        DialogRobotFactory.createFullSvgaDialog(activity, AnimFileName.EMOJI_SHIWANG, new DialogRobotFactory.OnSVGACallBack() {
            @Override
            public void callBack(SVGAImageView svgaImageView) {

            }
        });
        TtsHelper.getInstance().start(new TtsModel("请重新登录哦"), null);
    }
}