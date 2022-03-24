package com.thfw.mobileheart.util;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.thfw.base.models.CommonModel;
import com.thfw.base.models.MsgCountModel;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.TaskPresenter;
import com.thfw.base.timing.TimingHelper;
import com.thfw.base.timing.WorkInt;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.SharePreferenceUtil;
import com.thfw.user.models.User;
import com.thfw.user.login.UserManager;
import com.thfw.user.login.UserObserver;
import com.trello.rxlifecycle2.LifecycleProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Author:pengs
 * Date: 2022/3/4 10:43
 * Describe:未读消息数量管理
 */
public class MsgCountManager extends UserObserver implements TimingHelper.WorkListener {

    public static final int TYPE_ALL = 0;
    public static final int TYPE_TASK = 1;
    public static final int TYPE_SYSTEM = 2;
    private static final String TAG = MsgCountManager.class.getSimpleName();
    private static final String KEY_TASK_FINAL = "msg.count.task_";
    private static final String KEY_SYSTEM_FINAL = "msg.count.system_";
    private static String KEY_TASK;
    private static String KEY_SYSTEM;
    private static int requestCount = 0;
    private int type = TYPE_ALL;

    private int numTask;
    private int numSystem;
    private boolean isLogin;
    private List<OnCountChangeListener> onCountChangeListeners;

    private List<String> numTaskArray;
    private List<String> numSystemArray;

    private MsgCountManager() {
        onCountChangeListeners = new ArrayList<>();
        init(UserManager.getInstance().isLogin());
    }

    public static final void setTextView(TextView textView, int num) {
        LogUtil.d(TAG, "setTextView  num = " + num);
        if (textView != null) {
            if (num > 0) {
                textView.setVisibility(View.VISIBLE);
                textView.setText(num > 99 ? "99+" : String.valueOf(num));
            } else {
                textView.setVisibility(View.GONE);
            }
        }
    }

    public static MsgCountManager getInstance() {
        return Factory.instance;
    }

    private void init(boolean isLogin) {
        this.isLogin = isLogin;
        if (isLogin) {
            KEY_TASK = KEY_TASK_FINAL + UserManager.getInstance().getUID();
            KEY_SYSTEM = KEY_SYSTEM_FINAL + UserManager.getInstance().getUID();

            numTask = SharePreferenceUtil.getInt(KEY_TASK, 0);
            numSystem = SharePreferenceUtil.getInt(KEY_SYSTEM, 0);

            getMsgCount();
        } else {
            KEY_TASK = KEY_TASK_FINAL;
            KEY_SYSTEM = KEY_SYSTEM_FINAL;
            numTask = 0;
            numSystem = 0;
        }

        LogUtil.d(TAG, "KEY_TASK = " + KEY_TASK + " , KEY_SYSTEM = " + KEY_SYSTEM);
    }

    private void setNum(int numTask, int numSystem) {
        if (this.numTask != numTask || this.numSystem != numSystem) {
            this.numTask = numTask;
            this.numSystem = numSystem;
            SharePreferenceUtil.setInt(KEY_TASK, this.numTask);
            SharePreferenceUtil.setInt(KEY_SYSTEM, this.numSystem);
            onCountChange();
        }
    }

    public boolean hasSystemMsgId(String msgId) {
        if (TextUtils.isEmpty(msgId) && !EmptyUtil.isEmpty(numSystemArray)) {
            if (numSystemArray.contains(msgId)) {
                return true;
            } else {
                numSystemArray.add(msgId);
                return false;
            }
        }
        return false;
    }

    public boolean hasTaskMsgId(String msgId) {
        if (TextUtils.isEmpty(msgId) && !EmptyUtil.isEmpty(numSystemArray)) {
            if (numTaskArray.contains(msgId)) {
                return true;
            } else {
                numTaskArray.add(msgId);
                return false;
            }
        }
        return false;
    }

    public void addNumSystem() {
        setNumSystem(this.numSystem + 1);
    }

    public void addNumTask() {
        setNumTask(this.numTask + 1);
    }

    @Override
    public void onArrive() {
        getMsgCount(this.type);
    }

    @Override
    public WorkInt workInt() {
        return WorkInt.SECOND5_MSG_COUNT;
    }

    @Override
    public void onChanged(UserManager accountManager, User user) {
        if (isLogin != accountManager.isLogin()) {
            isLogin = accountManager.isLogin();
            init(isLogin);
            onCountChange();
        }
    }

    public int getNumTask() {
        return numTask;
    }

    private void setNumTask(int numTask) {
        if (this.numTask != numTask) {
            if (numTask < 0) {
                return;
            }
            this.numTask = numTask;
            SharePreferenceUtil.setInt(KEY_TASK, this.numTask);
            onCountChange();
        }
    }

    public int getNumSystem() {
        return numSystem;
    }

    private void setNumSystem(int numSystem) {
        if (this.numSystem != numSystem) {
            if (numSystem < 0) {
                return;
            }
            this.numSystem = numSystem;
            SharePreferenceUtil.setInt(KEY_SYSTEM, this.numSystem);
            onCountChange();
        }
    }

    public void getMsgCount() {
        getMsgCount(TYPE_ALL);
    }

    /**
     * 需要登录情况下请求
     *
     * @param type
     */
    public void getMsgCount(int type) {
        this.type = type;
        LogUtil.d(TAG, "getMsgCount() requestCount = " + requestCount);
        TimingHelper.getInstance().removeWorkArriveListener(MsgCountManager.this);
        new TaskPresenter<>(new TaskPresenter.TaskUi<MsgCountModel>() {
            @Override
            public LifecycleProvider getLifecycleProvider() {
                return null;
            }

            @Override
            public void onSuccess(MsgCountModel data) {

                if (data != null) {
                    if (type == TYPE_ALL) {
                        numSystemArray = data.system_msg_list;
                        numTaskArray = data.task_msg_list;
                        setNum(data.numTask, data.numSystem);
                    } else if (type == TYPE_TASK) {
                        setNumTask(data.numTask);
                        numTaskArray = data.task_msg_list;
                    } else {
                        setNumSystem(data.numSystem);
                        numSystemArray = data.system_msg_list;
                    }
                    LogUtil.d(TAG, data.toString());
                } else {
                    LogUtil.d(TAG, "data == null");
                }
            }

            @Override
            public void onFail(ResponeThrowable throwable) {
                TimingHelper.getInstance().addWorkArriveListener(MsgCountManager.this);
            }
        }).onNewMsgCount(type);
    }

    private void onItemStateChange(int id) {
        if (EmptyUtil.isEmpty(onCountChangeListeners)) {
            return;
        }
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                for (OnCountChangeListener listener : onCountChangeListeners) {
                    if (listener != null) {
                        listener.onItemState(id, true);
                    }
                }
            }
        });
    }

    private void onReadAll(int type) {
        if (EmptyUtil.isEmpty(onCountChangeListeners)) {
            return;
        }
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                for (OnCountChangeListener listener : onCountChangeListeners) {
                    if (listener != null) {
                        listener.onReadAll(type);
                    }
                }
            }
        });
    }

    private void onCountChange() {
        if (EmptyUtil.isEmpty(onCountChangeListeners)) {
            return;
        }
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                for (OnCountChangeListener listener : onCountChangeListeners) {
                    if (listener != null) {
                        listener.onCount(numTask, numSystem);
                    }
                }
            }
        });
    }

    public void readMsg(int type, String msgId) {
        new TaskPresenter<>(new TaskPresenter.TaskUi<MsgCountModel>() {
            @Override
            public LifecycleProvider getLifecycleProvider() {
                return null;
            }

            @Override
            public void onSuccess(MsgCountModel data) {
                if (type == TYPE_TASK) {
                    setNumTask(numTask - 1);
                } else {
                    setNumSystem(numSystem - 1);
                }
            }

            @Override
            public void onFail(ResponeThrowable throwable) {
                TimingHelper.getInstance().addWorkArriveListener(MsgCountManager.this);
            }
        }).onReadStated(msgId);
    }

    /**
     * 一键已读
     *
     * @param type 0 全部 1任务 2系统
     */
    public void readMsg(int type) {
        new TaskPresenter<>(new TaskPresenter.TaskUi<CommonModel>() {
            @Override
            public LifecycleProvider getLifecycleProvider() {
                return null;
            }

            @Override
            public void onSuccess(CommonModel data) {
                if (type == TYPE_ALL) {
                    setNum(0, 0);
                } else if (type == TYPE_TASK) {
                    setNumTask(0);
                } else {
                    setNumSystem(0);
                }
                onReadAll(type);
            }

            @Override
            public void onFail(ResponeThrowable throwable) {
                onReadAll(-1);
            }
        }).onReadStatedAll(type);
    }

    public void readMsg(int type, int id) {
        new TaskPresenter<>(new TaskPresenter.TaskUi<MsgCountModel>() {
            @Override
            public LifecycleProvider getLifecycleProvider() {
                return null;
            }

            @Override
            public void onSuccess(MsgCountModel data) {
                onItemStateChange(id);
                if (type == TYPE_TASK) {
                    setNumTask(numTask - 1);
                } else {
                    setNumSystem(numSystem - 1);
                }
            }

            @Override
            public void onFail(ResponeThrowable throwable) {
            }
        }).onReadStated(id);
    }

    public void addOnCountChangeListener(OnCountChangeListener onCountChangeListener) {
        if (!onCountChangeListeners.contains(onCountChangeListener)) {
            this.onCountChangeListeners.add(onCountChangeListener);
            if (onCountChangeListener != null) {
                onCountChangeListener.onCount(numTask, numSystem);
            }
        }
    }

    public void removeOnCountChangeListener(OnCountChangeListener onCountChangeListener) {
        if (onCountChangeListeners.contains(onCountChangeListener)) {
            this.onCountChangeListeners.remove(onCountChangeListener);
        }
    }

    public interface OnCountChangeListener {

        void onCount(int numTask, int numSystem);

        void onItemState(int id, boolean read);

        void onReadAll(int type);
    }

    private static class Factory {
        private static MsgCountManager instance = new MsgCountManager();
    }
}
