package com.thfw.mobileheart.lhxk;

import android.content.Intent;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.ainirobot.coreservice.client.ApiListener;
import com.ainirobot.coreservice.client.Definition;
import com.ainirobot.coreservice.client.RobotApi;
import com.ainirobot.coreservice.client.StatusListener;
import com.ainirobot.coreservice.client.actionbean.FaceTrackBean;
import com.ainirobot.coreservice.client.listener.ActionListener;
import com.ainirobot.coreservice.client.listener.CommandListener;
import com.ainirobot.coreservice.client.listener.Person;
import com.ainirobot.coreservice.client.person.PersonApi;
import com.ainirobot.coreservice.client.person.PersonListener;
import com.ainirobot.coreservice.client.speech.SkillApi;
import com.ainirobot.coreservice.client.speech.SkillCallback;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.base.utils.HandlerUtil;
import com.thfw.base.utils.HourUtil;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.ToastUtil;
import com.thfw.mobileheart.MyApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Author:pengs
 * Date: 2022/10/14 14:31
 * Describe:Todo
 */
public class LhXkHelper {

    private static final String TAG = "LhXkHelper";
    private static SkillApi skillApi;
    private static String mSpeechParResult;
    private static int mReqId = -1;
    private static int mPersonId = -1;

    private static LinkedHashMap<Integer, List<SpeechToAction>> mSpeechToActionMap = new LinkedHashMap<>();
    private static PersonListener listener;
    private static int cancelPersonId;
    private static long cancelPersonIdTime;

    public static void putAction(Class classes, SpeechToAction speechToAction) {
        putAction(classes.hashCode(), speechToAction);
    }

    public static void putAction(int code, SpeechToAction speechToAction) {
        List<SpeechToAction> actions = mSpeechToActionMap.get(code);
        if (actions == null) {
            actions = new ArrayList<>();
        }
        actions.add(speechToAction);
        mSpeechToActionMap.put(code, actions);
    }

    public static void removeAction(Class classes) {
        removeAction(classes.hashCode());
    }

    public static void removeAction(int code) {
        mSpeechToActionMap.remove(code);
    }

    public static boolean onAction(String word) {
        word = word.replaceAll("(打开|点击)", "");
        Set<Map.Entry<Integer, List<SpeechToAction>>> entrySet = mSpeechToActionMap.entrySet();
        for (Map.Entry<Integer, List<SpeechToAction>> map : entrySet) {
            List<SpeechToAction> list = map.getValue();
            for (SpeechToAction speechToAction : list) {
                LogUtil.i("onAcion", "speechToAction.text.contains(,) = " + speechToAction.text.contains("|"));
                LogUtil.i("onAcion", "speechToAction.text.split(,) = "
                        + Arrays.toString(speechToAction.text.split(",")));

                if (speechToAction.text.contains(",")) {
                    if (Arrays.asList(speechToAction.text.split(",")).contains(word)) {
                        return speechToAction.run();
                    }
                } else if (speechToAction.like) {
                    float lv = speechToAction.text.length() * 1f / word.length();
                    if (lv >= 1 && lv < 2.5f && speechToAction.text.contains(word)) {
                        return speechToAction.run();
                    }
                } else if (speechToAction.text.equals(word)) {
                    return speechToAction.run();
                }
            }
        }
        return false;
    }

    public static class SpeechToAction {
        public String text;
        public int type;
        public int code;
        public boolean like;
        public Intent intent;
        public Runnable runnable;

        public SpeechToAction(String text, Runnable runnable) {
            this.text = text;
            this.runnable = runnable;
        }

        public SpeechToAction setLike(boolean like) {
            this.like = like;
            return this;
        }

        public boolean run() {
            if (runnable != null) {
                try {
                    runnable.run();
                    LogUtil.i(TAG, "SpeechToAction -> text = " + text);
                    return true;
                } catch (Exception e) {
                    LogUtil.i(TAG, "SpeechToAction -> Exception e " + e.getMessage() + " text = " + text);
                    return false;
                }
            } else {
                LogUtil.i(TAG, "SpeechToAction -> runnable is null text = " + text);
                return false;
            }
        }
    }

    public static void disconnectApi() {
        RobotApi.getInstance().disconnectApi();
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
                LogUtil.i(TAG, "personChanged ->>>>>>>>>>>>>>");
                // 人员变化时，可以调用获取当前人员列表接口获取机器人视野内所有人员
                // 获取机器人视野内全部人员信息
//                List<Person> personList = PersonApi.getInstance().getAllPersons();
                // 获取机器人视野内1m范围内所有的人员信息
                List<Person> personList = PersonApi.getInstance().getAllPersons(2);
                if (!EmptyUtil.isEmpty(personList)) {
                    if (personList.size() > 1) {
                        double tempDistance = personList.get(0).getDistance();
                        mPersonId = personList.get(0).getId();
                        for (Person p : personList) {
                            // 别看我了。十秒内 不进行此人脸的跟随。
                            if (cancelPersonId == p.getId() && System.currentTimeMillis() - cancelPersonIdTime
                                    < HourUtil.LEN_SECOND10) {
                                continue;
                            }
                            if (p.getDistance() < tempDistance) {
                                tempDistance = p.getDistance();
                                mPersonId = p.getId();
                            }
                        }
                    } else {
                        mPersonId = personList.get(0).getId();
                    }
                    startFocusFollowPerson(mPersonId);
                } else {
                    mPersonId = -1;
                }
            }
        };
        // 注册人员监听
        PersonApi.getInstance().registerPersonListener(listener);
    }

    public static void startFocusFollow(int reqId) {
        startFocusFollow(reqId, -1, 5000, 2.5f);
    }


    public static void startFocusFollowPerson(int personId) {
        startFocusFollow(-1, personId, 5000, 2.5f);
    }

    public static void stopFocusFollow() {
        RobotApi.getInstance().stopFaceTrack(-1);
    }

    private static void startFocusFollow(int reqId, int faceId, long lostTimeout, float maxDistance) {
        if (reqId == -1) {
            reqId = faceId;
        }
        mPersonId = faceId;
        FaceTrackBean faceTrackBean = new FaceTrackBean();
        faceTrackBean.setPersonId(mPersonId);
        faceTrackBean.setAllowMoveBody(true);
        faceTrackBean.setLostTimer(lostTimeout);
        faceTrackBean.setMaxDistance(maxDistance);
        mReqId = RobotApi.getInstance().startFaceTrack(reqId, faceTrackBean, new ActionListener() {
            //        RobotApi.getInstance().startFocusFollow(reqId, faceId, lostTimeout, maxDistance, new ActionListener() {
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
        Log.i(TAG, "mReqId = " + mReqId);
    }

    public static void reset() {
        LogUtil.i(TAG, "reset +++++++++++++");
        RobotApi.getInstance().resetHead(-1, new CommandListener() {
            @Override
            public void onResult(int result, String message) {
                try {
                    JSONObject json = new JSONObject(message);
                    String status = json.getString("status");
                    if (Definition.CMD_STATUS_OK.equals(status)) {
                        if (LogUtil.isLogEnabled()) {
                            ToastUtil.show("resetHead");
                        }
                        //操作成功
                    }
                } catch (JSONException | NullPointerException e) {
                    e.printStackTrace();
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
                            if (LogUtil.isLogEnabled()) {
                                Toast.makeText(MyApplication.getApp(), result, Toast.LENGTH_SHORT).show();
                            }
                            onAction(result);
                            // 语音触发取消跟随
                            if (result.length() < 8 && result.contains("别看我")) {
                                // 取消跟随语音
                                if (mReqId != -1) {
                                    RobotApi.getInstance().stopFocusFollow(mReqId);
                                    reset();
                                    // 取消跟随人脸。 如果有其他人脸则跟随其他人脸没有其他人脸 则不跟随
                                } else if (mPersonId != -1) {
                                    cancelPersonId = mPersonId;
                                    cancelPersonIdTime = System.currentTimeMillis();

                                    RobotApi.getInstance().stopFocusFollow(mPersonId);

                                    List<Person> personList = PersonApi.getInstance().getAllPersons(1);
                                    if (!EmptyUtil.isEmpty(personList)) {

                                        double tempDistance = personList.get(0).getDistance();
                                        int tempPersonId = personList.get(0).getId();
                                        for (Person p : personList) {
                                            if (p.getId() != mPersonId && p.getDistance() < tempDistance) {
                                                tempDistance = p.getDistance();
                                                tempPersonId = p.getId();
                                            }
                                        }
                                        if (tempPersonId != mPersonId) {
                                            mPersonId = tempPersonId;
                                            startFocusFollow(tempPersonId);
                                        } else {
                                            reset();
                                        }
                                    } else {
                                        reset();
                                    }
                                } else {
                                    reset();
                                }
                            }
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
