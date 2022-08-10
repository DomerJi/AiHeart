package com.thfw.robotheart.push;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.thfw.base.models.OrganizationModel;
import com.thfw.base.net.CommonParameter;
import com.thfw.robotheart.MyApplication;
import com.thfw.user.login.UserManager;

import java.util.List;

/**
 * Author:pengs
 * Date: 2022/8/8 16:00
 * Describe:Todo
 */
public class AidlHelper {

    public static final String TAG = "AidlHelper:IPushAidlImp";
    private static IPushAidlInterface ipcAidl;

    private static int userId = -1;
    private static int organId = -1;

    public static void resetId() {
        userId = -1;
        organId = -1;
    }

    public static void setUserId(int userId) {
        AidlHelper.userId = userId;
        Log.d(TAG, "setUserId - > organId = " + organId + " ; userId = " + userId);
        if (organId != -1 && userId != -1) {
            bindService(MyApplication.getApp());
        }
    }

    public static void setOrganId(int organId) {
        AidlHelper.organId = organId;
        Log.d(TAG, "setOrganId - > organId = " + organId + " ; userId = " + userId);
        if (organId != -1 && userId != -1) {
            bindService(MyApplication.getApp());
        }
    }

    public static void bindService(Context context) {
        boolean pushExits = checkPackInfo("com.thfw.robotheart.push", context);

        if (isBind()) {
            if (organId != -1 && userId != -1) {
                try {
                    ipcAidl.onRegisterMsg(userId, organId);
                } catch (RemoteException e) {
                    Log.d(TAG, "e2 - > " + e.getMessage());
                }
            }
            Log.d(TAG, "bindService - > " + isBind());
            Log.d(TAG, "pushExits - > " + pushExits);
            return;
        }
        Log.d(TAG, "pushExits - > " + pushExits);
        String action = "com.thfw.robotheart.push.PushService";
        String packageName = "com.thfw.robotheart.push";
        Intent intent = new Intent();
        ComponentName componentName = new ComponentName(packageName, action);
        intent.setComponent(componentName);
        context.bindService(intent, mConnect, Context.BIND_AUTO_CREATE);
        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isBind()) {
                    try {
                        pullUpByPackage(context, "com.thfw.robotheart.push",
                                "com.thfw.robotheart.push.MainActivity");
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                bindService(context);
                            }
                        }, 200);
                        mHandler.postDelayed(this, 1500);
                    } catch (Exception e) {
                        Log.d(TAG, "pullUpByPackage e = " + e.getMessage());
                    }
                }
            }
        }, 1000);
        Log.d(TAG, "bindService - > " + packageName + "_" + isBind());
    }

    /**
     * 检查应用packageName 是否存在
     *
     * @return true:存在 ，false:不存在
     */
    private static boolean checkPackInfo(String packageName, Context context) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packageInfo != null;
    }


    private static IPushCallBack iCallbackInterface = new IPushCallBack();

    public static class IPushCallBack extends ICallbackInterface.Stub {

        @Override
        public void onMsgReciver(String msgJson) throws RemoteException {
            Log.d(TAG, "onMsgReciver - > " + msgJson);
            new MyPushCustomService().onMessage(MyApplication.getApp(), msgJson);

        }

        @Override
        public IBinder asBinder() {
            return (IBinder) iCallbackInterface;
        }

    }

    private static ServiceConnection mConnect = new ServiceConnection() {


        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected");
            ipcAidl = IPushAidlInterface.Stub.asInterface(service);
            if (ipcAidl != null) {
                try {
                    ipcAidl.registerCallback(iCallbackInterface);
                    ipcAidl.onRegisterMsg(userId, organId);
                    Log.d(TAG, "registerCallback");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            ipcAidl = null;
            Log.d(TAG, "onServiceDisconnected");
        }
    };

    public static boolean isBind() {
        return ipcAidl != null;
    }

    /**
     * 根据包名、Activit来拉起App
     * 此方式拉起的App在原程序运行，不会再单独开启一个app应用
     *
     * @param context context
     * @param pkgName 包名
     * @param acName  要拉起的Activity名,
     */
    private static void pullUpByPackage(Context context, String pkgName, String acName) {
        if (context instanceof Activity) {
            ((Activity) context).overridePendingTransition(0, 0);
        }
        Intent intent = new Intent(Intent.ACTION_MAIN);
        // 知道要跳转应用的包命与目标Activity
        ComponentName componentName = new ComponentName(pkgName, acName);
        intent.setComponent(componentName);
        // 这里Intent传值
        intent.putExtra("key", "value");
        context.startActivity(intent);
    }

    public static void checkBind() {
        if (isBind()) {
            return;
        }
        if (!UserManager.getInstance().isLogin()) {
            return;
        }
        if (organId != -1 && userId != -1) {
            bindService(MyApplication.getApp());
        } else {
            List<OrganizationModel.OrganizationBean> mSelecteds = CommonParameter.getOrganizationSelected();
            if (mSelecteds == null) {
                return;
            }

            organId = mSelecteds.get(mSelecteds.size() - 1).getId();
            if (UserManager.getInstance().getUser() != null && UserManager.getInstance().getUser().getUserInfo() != null) {
                userId = UserManager.getInstance().getUser().getUserInfo().id;
            }
            if (organId != -1 && userId != -1) {
                bindService(MyApplication.getApp());
            }
        }
    }
}
