package com.thfw.mobileheart.lhxk;

import android.app.Activity;
import android.content.Intent;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;

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
import com.thfw.base.face.LhXkListener;
import com.thfw.base.models.SpeechModel;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.base.utils.HandlerUtil;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.ToastUtil;
import com.thfw.mobileheart.MyApplication;
import com.thfw.mobileheart.constants.UIConfig;
import com.thfw.mobileheart.util.AppLifeHelper;
import com.thfw.mobileheart.util.DialogFactory;
import com.thfw.ui.common.LhXkSet;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
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

    // 别看我 的 人员id 和 别看我时间
    private static HashMap<Integer, Long> cancelPersonIdOrTime = new HashMap<>();


    static {
        putAction(LhXkHelper.class, new SpeechToAction("别看我,看别人,看其他人,看别的人,看别的地方,不准看我,不要看我", () -> {
            LogUtil.i(TAG, "============================别看我============================");
            if (mPersonId != -1) {
                if (LhXkSet.focusNoseeModel == 0) {
                    cancelPersonIdOrTime.clear();
                }
                cancelPersonIdOrTime.put(mPersonId, System.currentTimeMillis());
                stopFocusFollow();
                List<Person> personList = PersonApi.getInstance().getAllPersons(LhXkSet.focusM);
                // 如果有其他人在视野内，看向其他人
                if (!EmptyUtil.isEmpty(personList)) {
                    int tempPersonId = getFocusPersonId(personList);
                    if (tempPersonId != -1) {
                        startFocusFollowPerson(tempPersonId);
                        return;
                    }
                }
            }
            // 恢复云台初始位置
            reset();
        }).setLike(true));
        LhXkSet.init();
        LhXkSet.setLhXkListener(new LhXkListener() {
            @Override
            public void onChange(int type, Object obj) {
                if (type == 0) {
                    if (LhXkSet.voiceOpen == 1) {
                        onSpeechStart();
                    } else {
                        onSpeechStop();
                    }

                    if (LhXkSet.focusOpen == 1) {
                        if (listener == null) {
                            initFace();
                        }
                    } else {
                        stopFocusFollow();
                        if (listener != null) {
                            PersonApi.getInstance().unregisterPersonListener(listener);
                        }

                    }
                }
            }
        });
    }

    private static SkillCallback mSkillCallback;

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

    public static SpeechModel onActionText(String word, boolean end) {
        String oldWord = word;
        String newWord = word.replaceAll("(打开|点击)", "");
        Set<Map.Entry<Integer, List<SpeechToAction>>> entrySet = mSpeechToActionMap.entrySet();
        for (Map.Entry<Integer, List<SpeechToAction>> map : entrySet) {
            List<SpeechToAction> list = map.getValue();
            for (SpeechToAction speechToAction : list) {
                String regex;
                String matchesWord;
                int len;
                if (speechToAction.text.contains(",")) {
                    matchesWord = speechToAction.text.replaceAll(",", "|");
                    String[] allWords = speechToAction.text.split(",");
                    len = allWords[0].length();
                    for (int i = 1; i < allWords.length; i++) {
                        if (len > allWords[i].length()) {
                            len = allWords[i].length();
                        }
                    }
                } else {
                    matchesWord = speechToAction.text;
                    len = matchesWord.length();
                }
                if (speechToAction.like) {
                    int likeLen = Math.max(2, len / 2);
                    regex = ".{0," + likeLen + "}(" + matchesWord + ").{0," + likeLen + "}";
                } else {
                    if (len >= 3) {
                        int likeLen = Math.max(1, len / 3);
                        regex = ".{0," + likeLen + "}(" + matchesWord + ").{0," + likeLen + "}";
                    } else {
                        regex = "(" + matchesWord + ")";
                    }

                }

                LogUtil.i(TAG, "regex -> " + regex);
                if (newWord.matches(regex)) {
                    String[] words = speechToAction.text.split(",");
                    for (String key : words) {
                        int start = oldWord.indexOf(key);
                        if (start != -1) {
                            oldWord = oldWord.substring(0, start) + "<font color='" + UIConfig.COLOR_AGREE + "'>" + key +
                                    "</font>";
                        }
                    }
                    if (end) {
                        LogUtil.i(TAG, "regex true*********** -> " + regex);
                        HandlerUtil.getMainHandler().postDelayed(() -> {
                            speechToAction.run();
                        }, 500);
                    }
                    return SpeechModel.create(oldWord).setMatches(true);
                }
            }
        }
        return SpeechModel.create(oldWord).

                setMatches(false);
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
                if (AppLifeHelper.ifForeground()) {
                    HandlerUtil.getMainHandler().postDelayed(() -> init(), 5000);
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

                if (LhXkSet.focusOpen == 1) {
                    List<Person> personList = PersonApi.getInstance().getAllPersons(LhXkSet.focusM);
                    if (!EmptyUtil.isEmpty(personList)) {
                        startFocusFollowPerson(getFocusPersonId(personList));
                    } else {
                        stopFocusFollow();
                    }
                } else {
                    stopFocusFollow();
                }
            }
        };
        // 注册人员监听
        PersonApi.getInstance().registerPersonListener(listener);
    }


    private static int getFocusPersonId(List<Person> personList) {
        int tempPersonId = -1;
        double tempDistance = Double.MAX_VALUE;
        for (Person p : personList) {
            // 别看我了。十秒内 不进行此人脸的跟随。
            if (cancelPersonIdOrTime.containsKey(p.getId())) {
                if (System.currentTimeMillis() - cancelPersonIdOrTime.get(p.getId()) < LhXkSet.focusNoseeTime * 1000) {
                    continue;
                } else {
                    cancelPersonIdOrTime.remove(p.getId());
                }
            }
            if (p.getDistance() < tempDistance) {
                tempDistance = p.getDistance();
                tempPersonId = p.getId();
            }
        }
        return tempPersonId;
    }

    /**
     * 非法参数
     *
     * @param reqId
     */
    @Deprecated
    public static void startFocusFollow(int reqId) {
        startFocusFollow(reqId, -1, 5000, 2.5f);
    }


    public static void startFocusFollowPerson(int personId) {
        if (personId == -1) {
            LogUtil.i(TAG, "personId == -1");
        } else {
            startFocusFollow(-1, personId, 5000, 2.5f);
        }
    }

    public static void stopFocusFollow() {
        mPersonId = -1;
        mReqId = -1;
        RobotApi.getInstance().stopFaceTrack(mReqId);
    }

    private static void startFocusFollow(int reqId, int faceId, long lostTimeout, float maxDistance) {
        mPersonId = faceId;
        mReqId = -1;
        FaceTrackBean faceTrackBean = new FaceTrackBean();
        faceTrackBean.setPersonId(mPersonId);
        faceTrackBean.setAllowMoveBody(true);
        faceTrackBean.setLostTimer(lostTimeout);
        faceTrackBean.setMaxDistance(maxDistance);
        RobotApi.getInstance().startFaceTrack(reqId, faceTrackBean, new ActionListener() {

            @Override
            public void onStatusUpdate(int status, String data, String extraData) {
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
            public void onError(int errorCode, String errorString, String extraData) {
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

    private static Runnable runnable;

    private static void initSpeech() {
        if (mSkillCallback == null) {
            mSkillCallback = new SkillCallback() {


                @Override
                public void onSpeechParResult(String s) throws RemoteException {
                    mSpeechParResult = s;
                    Log.i(TAG, "onSpeechParResult ->>>>>>>>>>> s = " + s);
                    if (LhXkSet.voiceOpen == 1) {
                        if (LhXkSet.voiceTextOpen == 1) {
                            showSpeechText(onActionText(mSpeechParResult, false));
                        }
                    }

                }

                @Override
                public void onStart() throws RemoteException {
                    //开始识别
                    Log.i(TAG, "onStart ->>>>>>>>>>> s = ");
                    DialogFactory.dismissSpeech();
                }

                @Override
                public void onStop() throws RemoteException {
                    //识别结束
                    Log.i(TAG, "onStop ->>>>>>>>>>> s = " + mSpeechParResult);
                }

                @Override
                public void onVolumeChange(int volume) throws RemoteException {
                    //识别的声音大小变化
                }

                /**
                 * @param status 0 : 正常返回
                 *               1 : other返回
                 *               2 : 噪音或single_other返回
                 *               3 : 超时
                 *               4 : 被强制取消
                 *               5 : asr结果提前结束，未经过NLU
                 *               6 : 全双工语意相同情况下，other返回
                 */
                @Override
                public void onQueryEnded(int status) throws RemoteException {
                    // status状态
                    Log.i(TAG, "onQueryEnded ->>>>>>>>>>> status = " + status);
                }

                @Override
                public void onQueryAsrResult(String asrResult) throws RemoteException {
                    // asrResult ：最终识别结果
                    Log.i(TAG, "onQueryAsrResult ->>>>>>>>>>> asrResult = " + asrResult);
                    if (TextUtils.isEmpty(asrResult)) {
                        return;
                    }
                    if (LhXkSet.voiceOpen == 1) {
                        if (LhXkSet.voiceTextOpen == 1) {
                            SpeechModel speechModel = onActionText(asrResult, true);
                            speechModel.setType(speechModel.matches ? SpeechModel.Type.SUCCESS : SpeechModel.Type.FAIL);
                            showSpeechText(speechModel);
                            if (speechResult != null) {
                                if (ToastUtil.isMainThread()) {
                                    if (speechResult != null) {
                                        speechResult.onResult(asrResult);
                                    }
                                } else {
                                    HandlerUtil.getMainHandler().post(() -> {
                                        if (speechResult != null) {
                                            speechResult.onResult(asrResult);
                                        }
                                    });
                                }
                            }
                        }
                    }
                }
            };

        }
        skillApi = new SkillApi();
        skillApi.connectApi(MyApplication.getApp(), new ApiListener() {
            @Override
            public void handleApiDisabled() {
                Log.i(TAG, "handleApiDisabled ->>>>>>>>>>>");
            }

            @Override
            public void handleApiConnected() {
                Log.i(TAG, "handleApiConnected ->>>>>   >>>>>>");
                //语音服务连接成功，注册语音回调
                skillApi.registerCallBack(mSkillCallback);
                skillApi.setRecognizeMode(true);
                if (LhXkSet.voiceOpen == 1) {
                    onSpeechStart();
                }
            }

            @Override
            public void handleApiDisconnected() {
                onSpeechStop();
                //语音服务已断开
                skillApi.unregisterCallBack(mSkillCallback);
                Log.i(TAG, "handleApiDisconnected ->>>>>>>>>>>");
            }
        });

    }

    public static class SpeechRunnable implements Runnable {
        String text;

        public SpeechRunnable(String text) {
            this.text = text;
        }

        @Override
        public void run() {
            // ===================================
            Log.i(TAG, "SpeechRunnable v runnable = " + text);
            if (!TextUtils.isEmpty(text)) {

                if (speechResult != null) {
                    if (ToastUtil.isMainThread()) {
                        if (speechResult != null) {
                            speechResult.onResult(text);
                        }
                    } else {
                        HandlerUtil.getMainHandler().post(() -> {
                            if (speechResult != null) {
                                speechResult.onResult(text);
                            }
                        });
                    }
                }
            }
            // ================================
        }
    }


    static SpeechResult speechResult;

    public static void setSpeechResult(SpeechResult speechResult) {
        LhXkHelper.speechResult = speechResult;
    }

    public interface SpeechResult {
        void onResult(String result);

    }

    private static void showSpeechText(SpeechModel model) {
        Activity fragmentActivity = AppLifeHelper.getTopActivity();
        LogUtil.i(TAG, "show text -> " + model.text);
        if (fragmentActivity instanceof FragmentActivity) {
            DialogFactory.createSpeechDialog((FragmentActivity) fragmentActivity, model);
        } else {
            DialogFactory.dismissSpeech();
        }
    }
}
