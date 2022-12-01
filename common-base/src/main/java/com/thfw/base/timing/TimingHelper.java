package com.thfw.base.timing;


import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.thfw.base.utils.HandlerUtil;
import com.thfw.base.utils.ToastUtil;

import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Author:pengs
 * Date: 2021/11/4 11:12
 * Describe:Todo
 */
public class TimingHelper {

    private static final String TAG = TimingHelper.class.getSimpleName();
    private Handler mHandler;
    private ConcurrentHashMap<WorkInt, HashSet<WorkListener>> mWorkInts;
    private static final String S_COUNT = "handleMessage count ===========================";

    private TimingHelper() {
        mWorkInts = new ConcurrentHashMap();
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                Log.d(TAG, S_COUNT);

                if (!mWorkInts.isEmpty()) {
                    try {
                        for (WorkInt workInt : mWorkInts.keySet()) {
                            workInt.addCount();
                            if (workInt.arrive()) {
                                notifyWork(workInt);

                            }
                        }
                    } catch (Exception e) {
                    }
                    if (!mWorkInts.isEmpty()) {
                        removeMessages(0);
                        sendEmptyMessageDelayed(0, 999);
                    }
                }


            }
        };

    }

    public static TimingHelper getInstance() {
        return Factory.instance;
    }

    public void notifyWork(WorkInt workInt) {
        if (ToastUtil.isMainThread()) {
            HashSet<WorkListener> workListeners = mWorkInts.get(workInt);
            if (workListeners != null) {
                for (WorkListener workListener : workListeners) {
                    workListener.onArrive();
                }
                // 非重复任务，去除
                if (!workInt.isRepeat()) {
                    removeWorkArriveListener(workInt);
                }
            }
            return;
        }
        HandlerUtil.getMainHandler().post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "workInt.arrive() = " + workInt.toString());
                HashSet<WorkListener> workListeners = mWorkInts.get(workInt);
                if (workListeners != null) {
                    for (WorkListener workListener : workListeners) {
                        workListener.onArrive();
                    }
                    // 非重复任务，去除
                    if (!workInt.isRepeat()) {
                        removeWorkArriveListener(workInt);
                    }
                }
            }
        });
    }

    private void removeWorkInt(WorkInt workInt) {
        mWorkInts.remove(workInt);
    }

    public void addWorkArriveListener(WorkListener workListener) {
        boolean isEmpty = mWorkInts.isEmpty();
        if (mWorkInts.containsKey(workListener.workInt())) {
            mWorkInts.get(workListener.workInt()).add(workListener);
        } else {
            HashSet<WorkListener> workListeners = new HashSet<>();
            workListeners.add(workListener);
            mWorkInts.put(workListener.workInt(), workListeners);
        }
        if (isEmpty && !mWorkInts.isEmpty()) {
            mHandler.removeMessages(0);
            mHandler.sendEmptyMessageDelayed(0, 1000);
        }
    }

    public void removeWorkArriveListener(WorkListener workListener) {
        if (mWorkInts.containsKey(workListener.workInt())) {
            HashSet<WorkListener> workListeners = mWorkInts.get(workListener.workInt());
            if (workListeners != null && workListeners.contains(workListener)) {
                workListeners.remove(workListener);
                if (workListeners.isEmpty()) {
                    mWorkInts.remove(workListener.workInt());
                }
            }
        }
    }

    public void removeWorkArriveListener(WorkInt workInt) {
        try {
            if (mWorkInts.containsKey(workInt)) {
                mWorkInts.remove(workInt);
            }
        } catch (Exception e) {
        }
    }

    public interface WorkListener {

        void onArrive();

        WorkInt workInt();
    }

    public static class Factory {
        private static final TimingHelper instance = new TimingHelper();
    }


}
