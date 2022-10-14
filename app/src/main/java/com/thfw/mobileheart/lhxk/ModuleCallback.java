package com.thfw.mobileheart.lhxk;

import android.os.RemoteException;
import android.util.Log;

import com.ainirobot.coreservice.client.RobotApi;
import com.ainirobot.coreservice.client.module.ModuleCallbackApi;
import com.thfw.base.utils.HandlerUtil;

public class ModuleCallback extends ModuleCallbackApi {

    private final static String TAG = "ModuleCallback";

    @Override
    public boolean onSendRequest(int reqId, String reqType, String reqText, String reqParam)
            throws RemoteException {
        // 接收语音指令,
        // reqTyp : 语音指令类型
        // reqText : 语音识别内容
        // reqParam : 语音指令参数
        Log.i(TAG, "onSendRequest ->>>>>>>>>>>>> " +
                "reqId = " + reqId
                + " ; reqType = " + reqType
                + " ; reqText = " + reqText
                + " ; reqParam = " + reqParam);
        HandlerUtil.getMainHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                RobotApi.getInstance().finishModuleParser(reqId, false, "ok");
            }
        }, 500);
        LhXkHelper.startFocusFollow(reqId, -1);
        return true;
    }

    @Override
    public void onRecovery() throws RemoteException {
        // 控制权恢复，收到该事件后，重新恢复对机器人的控制
        Log.i(TAG, "onRecovery() ->>>>>>>>>>>>>>>> ");
    }

    @Override
    public void onSuspend() throws RemoteException {
        // 控制权被系统剥夺，收到该事件后，所有Api调用无效
        Log.i(TAG, "onSuspend() ->>>>>>>>>>>>>>>> ");
    }
}