package com.thfw.mobileheart.lhxk;

import android.content.Intent;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.ainirobot.coreservice.client.ApiListener;
import com.ainirobot.coreservice.client.Definition;
import com.ainirobot.coreservice.client.RobotApi;
import com.ainirobot.coreservice.client.StatusListener;
import com.ainirobot.coreservice.client.listener.ActionListener;
import com.ainirobot.coreservice.client.listener.Person;
import com.ainirobot.coreservice.client.person.PersonApi;
import com.ainirobot.coreservice.client.person.PersonListener;
import com.ainirobot.coreservice.client.speech.SkillApi;
import com.ainirobot.coreservice.client.speech.SkillCallback;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.base.utils.HandlerUtil;
import com.thfw.base.utils.ToastUtil;
import com.thfw.mobileheart.MyApplication;
import com.thfw.mobileheart.activity.MainActivity;
import com.thfw.ui.widget.DeviceUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Author:pengs
 * Date: 2022/10/14 14:31
 * Describe:Todo
 */
public class LhXkHelper {

    private static final String TAG = "LhXkHelper";
    private static SkillApi skillApi;
    private static String mSpeechParResult;

    private static HashMap<Integer, List<SpeechToAction>> mSpeechToActionMap;
    private static PersonListener listener;

    {
        if (DeviceUtil.isLhXk_CM_GB03D()) {
            mSpeechToActionMap = new HashMap();
            putAction(MainActivity.class.getSimpleName().hashCode(), new SpeechToAction());
        }
    }

    public static void putAction(int code, SpeechToAction speechToAction) {
        List<SpeechToAction> actions = mSpeechToActionMap.get(code);
        if (actions == null) {
            actions = new ArrayList<>();
        }
        actions.add(speechToAction);
        mSpeechToActionMap.put(code, actions);
    }

    public static class SpeechToAction {
        public String text;
        public int code;
        public Intent intent;
    }

    public static void init() {
        RobotApi.getInstance().connectServer(MyApplication.getApp(), new ApiListener() {
            @Override
            public void handleApiDisabled() {
                Log.i(TAG, "handleApiDisabled ->>>>>>>> ");
            }

            @Override
            public void handleApiConnected() {
                Log.i(TAG, "handleApiConnected ->>>>>>>>  Server已连接 ");
                // Server已连接，设置接收请求的回调，包含语音指令、系统事件等
                RobotApi.getInstance().setCallback(new ModuleCallback());
                initState();
                if (listener != null) {
                    PersonApi.getInstance().unregisterPersonListener(listener);
                }
                initFace();
                initSpeech();

            }

            @Override
            public void handleApiDisconnected() {
                Log.i(TAG, "handleApiDisconnected ->>>>>>>>  连接已断开");
                // 连接已断开
                releaseSpeech();
                // 取消注册人员监听
                if (listener != null) {
                    PersonApi.getInstance().unregisterPersonListener(listener);
                }
            }
        });


    }

    private static void initState() {
        StatusListener statusListener = new StatusListener() {
            @Override
            public void onStatusUpdate(String type, String data) throws RemoteException {
                Log.i(TAG, "type = " + type + " ; data = " + data);
            }
        };
        /**
         * type为需要监听的状态类型，type支持以下几种：
         *
         * Definition.STATUS_POSE：机器人当前的坐标，持续上报
         * Definition.STATUS_POSE_ESTIMATE：当前定位状态，定位状态发生改变时上报
         * Definition.STATUS_BATTERY：当前电池状态信息，包括是否在充电、电量多少、是否低电量报警等
         */
        // 注册状态监听
        RobotApi.getInstance().registerStatusListener(Definition.STATUS_BATTERY, statusListener);
        // 解除状态监听
//        RobotApi.getInstance().unregisterStatusListener(statusListener);
    }

    private static void initFace() {
        listener = new PersonListener() {
            @Override
            public void personChanged() {
                super.personChanged();
                // 人员变化时，可以调用获取当前人员列表接口获取机器人视野内所有人员
                // 获取机器人视野内全部人员信息
//                List<Person> personList = PersonApi.getInstance().getAllPersons();
                // 获取机器人视野内1m范围内所有的人员信息
                List<Person> personList = PersonApi.getInstance().getAllPersons(1);
                if (!EmptyUtil.isEmpty(personList)) {
                    Person person = personList.get(0);
                    startFocusFollow(-1, person.getId());
                }
            }
        };
        // 注册人员监听
        PersonApi.getInstance().registerPersonListener(listener);
    }

    public static void startFocusFollow(int reqId, int personId) {
        startFocusFollow(reqId, personId, 2000, 2);
    }

    private static void startFocusFollow(int reqId, int faceId, long lostTimeout, long maxDistance) {

        RobotApi.getInstance().startFocusFollow(reqId, faceId, lostTimeout, maxDistance, new ActionListener() {
            @Override
            public void onStatusUpdate(int status, String data) {
                Log.d(TAG, "onStatusUpdate onResult status: " + status + " ; data = " + data);
                switch (status) {
                    case Definition.STATUS_TRACK_TARGET_SUCCEED:
                        //跟随目标成功
                        break;
                    case Definition.STATUS_GUEST_LOST:
                        //跟随目标丢失
                        break;
                    case Definition.STATUS_GUEST_FARAWAY:
                        //跟随目标距离已大于设置的最大距离
                        break;
                    case Definition.STATUS_GUEST_APPEAR:
                        //跟随目标重新进入设置的最大距离内
                        break;
                }
            }

            @Override
            public void onError(int errorCode, String errorString) {
                Log.d(TAG, "onError onResult errorCode: " + errorCode + " ; errorString = " + errorString);
                switch (errorCode) {
                    case Definition.ERROR_SET_TRACK_FAILED:
                    case Definition.ERROR_TARGET_NOT_FOUND:
                        //跟随目标未找到
                        break;
                    case Definition.ACTION_RESPONSE_ALREADY_RUN:
                        //正在跟随中，请先停止上次跟随，才能重新执行
                        break;
                    case Definition.ACTION_RESPONSE_REQUEST_RES_ERROR:
                        //已经有需要控制底盘的接口调用(例如：引领、导航)，请先停止，才能继续调用
                        break;
                }
            }

            @Override
            public void onResult(int status, String responseString) {
                Log.d(TAG, "startTrackPerson onResult status: " + status);
                switch (status) {
                    case Definition.ACTION_RESPONSE_STOP_SUCCESS:
                        //在焦点跟随过程中，主动调用stopFocusFollow，成功停止跟随
                        break;
                }
            }
        });
    }

    public static void releaseSpeech() {
        if (skillApi != null) {
            skillApi.disconnectApi();
        }
    }

    public static void onSpeechStart() {
        if (skillApi != null && skillApi.isApiConnectedService()) {
            skillApi.setRecognizable(true);
        }
    }

    public static void onSpeechStop() {
        if (skillApi != null && skillApi.isApiConnectedService()) {
            skillApi.setRecognizable(false);
        }
    }

    private static void initSpeech() {
        SkillCallback mSkillCallback = new SkillCallback() {

            private Runnable runnable;

            @Override
            public void onSpeechParResult(String s) throws RemoteException {
                mSpeechParResult = s;
                Log.i(TAG, "onSpeechParResult ->>>>>>>>>>> s = " + s);
                //语音临时识别结果
            }

            @Override
            public void onStart() throws RemoteException {
                //开始识别
                Log.i(TAG, "onStart ->>>>>>>>>>> s = ");
            }

            @Override
            public void onStop() throws RemoteException {
                if (runnable != null) {
                    HandlerUtil.getMainHandler().removeCallbacks(runnable);
                }
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        if (!TextUtils.isEmpty(mSpeechParResult)) {
                            String result = mSpeechParResult;
                            mSpeechParResult = null;
                            if (ToastUtil.isMainThread()) {
                                if (speechResult != null) {
                                    speechResult.onResult(result);
                                }
                            } else {
                                HandlerUtil.getMainHandler().post(() -> {
                                    if (speechResult != null) {
                                        speechResult.onResult(result);
                                    }
                                });
                            }
                        }
                    }
                };

                HandlerUtil.getMainHandler().postDelayed(runnable, 500);
                //识别结束
                Log.i(TAG, "onStop ->>>>>>>>>>> s = " + mSpeechParResult);
            }

            @Override
            public void onVolumeChange(int volume) throws RemoteException {
                //识别的声音大小变化
                Log.i(TAG, "onVolumeChange ->>>>>>>>>>> volume = " + volume);
            }

            /**
             @param status 0 : 正常返回
             1 : other返回
             2 : 噪音或single_other返回
             3 : 超时
             4 : 被强制取消
             5 : asr结果提前结束，未经过NLU
             6 : 全双工语意相同情况下，other返回
             */
            @Override
            public void onQueryEnded(int status) throws RemoteException {
                //status状态
            }

            @Override
            public void onQueryAsrResult(String asrResult) throws RemoteException {
                //asrResult ：最终识别结果
            }
        };


        skillApi = new SkillApi();
        skillApi.connectApi(MyApplication.getApp(), new ApiListener() {
            @Override
            public void handleApiDisabled() {
                Log.i(TAG, "handleApiDisabled ->>>>>>>>>>>");
            }

            @Override
            public void handleApiConnected() {
                Log.i(TAG, "handleApiConnected ->>>>>>>>>>>");
                //语音服务连接成功，注册语音回调
                skillApi.registerCallBack(mSkillCallback);
                skillApi.setRecognizeMode(true);
                onSpeechStart();
            }

            @Override
            public void handleApiDisconnected() {
                //语音服务已断开
                Log.i(TAG, "handleApiDisconnected ->>>>>>>>>>>");
            }
        });

    }

    static SpeechResult speechResult;

    public static void setSpeechResult(SpeechResult speechResult) {
        LhXkHelper.speechResult = speechResult;
    }

    public interface SpeechResult {
        void onResult(String result);
    }
}
