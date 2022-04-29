package com.thfw.mobileheart.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.thfw.base.base.IPresenter;
import com.thfw.base.utils.ToastUtil;
import com.thfw.mobileheart.util.DialogFactory;
import com.thfw.ui.R;
import com.thfw.ui.base.IBaseActivity;
import com.thfw.ui.dialog.TDialog;
import com.thfw.ui.dialog.base.BindViewHolder;

/**
 * 通用基础Activity
 */
public abstract class BaseActivity<T extends IPresenter> extends IBaseActivity<T> {

    // ================= 打电话权限 =======================//
    public static final int REQUEST_CALL_PERMISSION = 10111; //拨号请求码
    private String phoneCall;

    public boolean isMainThread() {
        return Thread.currentThread() == Looper.getMainLooper().getThread();
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
}