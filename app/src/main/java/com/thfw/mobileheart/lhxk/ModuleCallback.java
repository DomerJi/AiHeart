package com.thfw.mobileheart.lhxk;

import android.os.RemoteException;
import android.util.Log;

import com.ainirobot.coreservice.client.Definition;
import com.ainirobot.coreservice.client.RobotApi;
import com.ainirobot.coreservice.client.listener.ActionListener;
import com.ainirobot.coreservice.client.module.ModuleCallbackApi;
import com.thfw.base.utils.HandlerUtil;

public class ModuleCallback extends ModuleCallbackApi {

    private final static String TAG = "ModuleCallback";

    @Override
    public boolean onSendRequest(int reqId, String reqType, String reqText, String reqParam) throws RemoteException {
        // 接收语音指令,
        // reqTyp : 语音指令类型
        // reqText : 语音识别内容
        // reqParam : 语音指令参数
        Log.i(TAG, "onSendRequest ->>>>>>>>>>>>> " + "reqId = " + reqId + " ; reqType = " + reqType + " ; reqText = " + reqText + " ; reqParam = " + reqParam);
        if (Definition.REQ_SPEECH_WAKEUP.equals(reqType)) {
            HandlerUtil.getMainHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    RobotApi.getInstance().finishModuleParser(reqId, false, "ok");
                    LhXkHelper.stopFocusFollow();
                    float angle = -1;
                    try {
                        angle = Float.parseFloat(reqParam);
                    } catch (Exception e) {
                        angle = -1;
                    }
                    if (angle < 0) {
                        return;
                    }
                    RobotApi.getInstance().wakeUp(reqId, angle, new ActionListener() {
                        @Override
                        public void onResult(int status, String responseString) throws RemoteException {
                            Log.i(TAG, "onSendRequest ->>>>>>>>>>>>>  onResult = " + status + " ; responseString = " + responseString);
                            switch (status) {
                                case Definition.RESULT_OK:
                                    //唤醒完成
                                    break;
                                case Definition.ACTION_RESPONSE_STOP_SUCCESS:
                                    //在唤醒过程中，主动调用stopWakeUp，停止唤醒
                                    break;
                            }
                        }

                        @Override
                        public void onError(int errorCode, String errorString) throws RemoteException {

                            Log.i(TAG, "onSendRequest ->>>>>>>>>>>>>  onError = " + errorCode + " ; responseString = " + errorString);
                            switch (errorCode) {
                                case Definition.ERROR_MOVE_HEAD_FAILED:
                                    //头部云台移动失败
                                    break;
                                case Definition.ACTION_RESPONSE_ALREADY_RUN:
                                    //当前正在唤醒中，需要先停止上次唤醒
                                    break;
                                case Definition.ACTION_RESPONSE_REQUEST_RES_ERROR:
                                    //已经有需要控制底盘的接口调用(例如：引领、导航等)，请先停止，才能继续调用
                                    break;
                            }
                        }
                    });
                }
            }, 300);
        }
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