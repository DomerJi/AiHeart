package com.thfw.mobileheart.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.thfw.base.base.IPresenter;
import com.thfw.base.face.PermissionListener;
import com.thfw.base.models.UrgedMsgModel;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.base.utils.PermissionUtil;
import com.thfw.base.utils.RegularUtil;
import com.thfw.base.utils.ToastUtil;
import com.thfw.mobileheart.R;
import com.thfw.mobileheart.activity.task.MeTaskActivity;
import com.thfw.mobileheart.lhxk.LhXkHelper;
import com.thfw.mobileheart.util.DialogFactory;
import com.thfw.ui.base.IBaseActivity;
import com.thfw.ui.dialog.TDialog;
import com.thfw.ui.dialog.base.BindViewHolder;
import com.thfw.ui.utils.UrgeUtil;
import com.thfw.ui.widget.DeviceUtil;

import java.util.ArrayList;

/**
 * 通用基础Activity
 */
public abstract class BaseActivity<T extends IPresenter> extends IBaseActivity<T> {

    // ================= 打电话权限 =======================//
    public static final int REQUEST_CALL_PERMISSION = 10111; // 拨号请求码
    public static final int REQUEST_CUSTOM_PERMISSION = 1010; // 需要时再请求
    public static final int VOICE_STATIC = 0;
    public static final int VOICE_CHANGED = 1;
    private String phoneCall;

    private String[] NEEDED_PERMISSION;
    private PermissionListener permissionListener;

    public boolean isMainThread() {
        return Thread.currentThread() == Looper.getMainLooper().getThread();
    }

    public void showUrgedDialog() {
        DialogFactory.createUrgedDialog(this, new UrgedMsgModel(), new DialogFactory.OnUrgedBack() {
            @Override
            public void onClick(View view, UrgedMsgModel urgedMsgModel) {
                MeTaskActivity.startActivity(view.getContext());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        UrgeUtil.setListener(map -> {
            if (DialogFactory.getUrgedDialog() != null) {
                return;
            }
            showUrgedDialog();
        });

        if (DeviceUtil.isLhXk_CM_GB03D()) {
            initLocalVoice(VOICE_STATIC);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        UrgeUtil.setListener(null);
        if (DialogFactory.getUrgedDialog() != null) {
            DialogFactory.getUrgedDialog().dismiss();
        }
        if (DeviceUtil.isLhXk_CM_GB03D()) {
            clearLocalVoice(VOICE_STATIC);
        }
    }

    protected void initLocalVoice(int type) {
        LhXkHelper.putAction(BaseActivity.class, new LhXkHelper.SpeechToAction("返回", () -> {
            onBackPressed();
        }));
    }

    protected void clearLocalVoice(int type) {
        LhXkHelper.removeAction(BaseActivity.class);
        LhXkHelper.removeAction(this.getClass());
    }

    /**
     * 判断是否有某项权限
     *
     * @param string_permission 权限
     * @param request_code      请求码
     * @return
     */
    public boolean checkReadPermission(String string_permission, int request_code) {
        boolean flag = false;
        if (ContextCompat.checkSelfPermission(this, string_permission) == PackageManager.PERMISSION_GRANTED) {//已有权限
            flag = true;
        } else {//申请权限
            ActivityCompat.requestPermissions(this, new String[]{string_permission}, request_code);
        }
        return flag;
    }

    /**
     * 检查权限后的回调
     *
     * @param requestCode  请求码
     * @param permissions  权限
     * @param grantResults 结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CALL_PERMISSION: //拨打电话
                if (permissions.length != 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {//失败
                    Toast.makeText(this, "请允许电话权限后再试", Toast.LENGTH_SHORT).show();
                } else {
                    // 成功
                    if (!TextUtils.isEmpty(phoneCall)) {
                        call(phoneCall);
                    }
                }
                break;
            case REQUEST_CUSTOM_PERMISSION:
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
                if (!EmptyUtil.isEmpty(NEEDED_PERMISSION) && permissionListener != null) {
                    boolean has = checkPermissionsNoRequest(NEEDED_PERMISSION);
                    if (has && mPermissionDialog != null) {
                        mPermissionDialog.dismiss();
                    }
                    permissionListener.onPermission(has);
                }
                break;
        }
    }

    /**
     * 拨打电话（直接拨打）
     *
     * @param phone 电话
     */
    public void call(String phone) {

        if (TextUtils.isEmpty(phone)) {
            ToastUtil.show("手机号码为空");
            return;
        }
        if (RegularUtil.isContainChinese(phone)) {
            ToastUtil.show("不是有效电话");
            return;
        }
        phoneCall = phone;
        DialogFactory.createCustomDialog(this, new DialogFactory.OnViewCallBack() {
            @Override
            public void callBack(TextView mTvTitle, TextView mTvHint, TextView mTvLeft, TextView mTvRight, View mVLineVertical) {
                mTvTitle.setText("提示");
                mTvHint.setText("确定要打电话给：" + phone + " 吗？");
            }

            @Override
            public void onViewClick(BindViewHolder viewHolder, View view, TDialog tDialog) {
                if (view.getId() == R.id.tv_right) {
                    String finalPhone = phone;
                    if (!finalPhone.startsWith("tel:")) {
                        finalPhone = "tel:" + phone;
                    }
                    if (checkReadPermission(Manifest.permission.CALL_PHONE, REQUEST_CALL_PERMISSION)) {
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(finalPhone));
                        startActivity(intent);
                    }
                }
                tDialog.dismiss();
            }
        });

    }
    // ================= 打电话权限 =======================//


    private TDialog mPermissionDialog;

    public void requestCallPermission(String[] NEEDED_PERMISSION, PermissionListener permissionListener) {
        this.NEEDED_PERMISSION = NEEDED_PERMISSION;
        this.permissionListener = permissionListener;
        if (!checkPermissionsNoRequest(NEEDED_PERMISSION)) {
            checkPermissions(NEEDED_PERMISSION);
        } else {
            permissionListener.onPermission(true);
        }
    }

    /**
     * 动态获取内存存储权限
     */
    private boolean checkPermissions(String[] NEEDED_PERMISSION) {
        boolean has = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 验证是否许可权限
            for (String str : NEEDED_PERMISSION) {
                if (ContextCompat.checkSelfPermission(this, str) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, NEEDED_PERMISSION, REQUEST_CUSTOM_PERMISSION);
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
    protected boolean checkPermissionsNoRequest(String[] NEEDED_PERMISSION) {
        boolean has = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 验证是否许可权限
            for (String str : NEEDED_PERMISSION) {
                if (ContextCompat.checkSelfPermission(this, str) != PackageManager.PERMISSION_GRANTED) {
                    has = false;
                    break;
                }
            }
        }
        return has;
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
        mPermissionDialog = DialogFactory.createCustomDialog(this, new DialogFactory.OnViewCallBack() {
            @Override
            public void callBack(TextView mTvTitle, TextView mTvHint, TextView mTvLeft, TextView mTvRight, View mVLineVertical) {
                mTvTitle.setText("权限申请");
                mTvLeft.setText("不使用此功能");
                mTvRight.setText("去允许");
                mTvRight.setBackgroundResource(R.drawable.dialog_button_selector);
                ArrayList<String> noGrantedPermission = new ArrayList<>();
                for (String str : NEEDED_PERMISSION) {
                    if (ContextCompat.checkSelfPermission(BaseActivity.this, str)
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
                if (view.getId() == com.thfw.ui.R.id.tv_left) {
                    ToastUtil.showLong("您放弃了使用此功能");
                } else {
                    if (never) {// 从不询问，禁止
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        // 注意就是"package",不用改成自己的包名
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, REQUEST_CUSTOM_PERMISSION);
                    } else {
                        ActivityCompat.requestPermissions(BaseActivity.this, NEEDED_PERMISSION, REQUEST_CUSTOM_PERMISSION);
                    }
                }
            }
        }, false);
    }
}