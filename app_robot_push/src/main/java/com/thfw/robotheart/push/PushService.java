package com.thfw.robotheart.push;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.Nullable;

import com.thfw.robotheart.push.tester.UPushAlias;

/**
 * Author:pengs
 * Date: 2022/8/8 15:15
 * Describe:Todo
 */
public class PushService extends Service {

    public static final String TAG = "Push:IPushAidlImp";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("IPushAidlImp", "onBind - Process.myPid() = " + Process.myPid());
        return myBinder;
    }

    public IBinder myBinder = new IPushAidlImp();

    public class IPushAidlImp extends IPushAidlInterface.Stub {


        public static final String TAG = "Push:IPushAidlImp";

        @Override
        public int onRegisterMsg(int userId, int organId) throws RemoteException {
            Log.d(TAG, "onRegisterMsg -> " + userId + "_organId = " + organId);
            UPushAlias.set(MyApplication.getApp(), "user_" + userId, "user");
            UPushAlias.setTag(organId);
            return 1;
        }

        @Override
        public void registerCallback(ICallbackInterface callback) throws RemoteException {
            if (!CallBack.getCallbackInterfaces().contains(callback)) {
                CallBack.getCallbackInterfaces().add(callback);
            }
            Log.d(TAG, "registerCallback callbackInterfaces size = "
                    + (CallBack.getCallbackInterfaces() == null ? -1 : CallBack.getCallbackInterfaces().size()));
        }

        @Override
        public void unregisterCallback(ICallbackInterface callback) throws RemoteException {
            if (CallBack.getCallbackInterfaces().contains(callback)) {
                CallBack.getCallbackInterfaces().remove(callback);
            }
            Log.d(TAG, "unregisterCallback callbackInterfaces size = "
                    + (CallBack.getCallbackInterfaces() == null ? -1 : CallBack.getCallbackInterfaces().size()));
        }
    }


}
