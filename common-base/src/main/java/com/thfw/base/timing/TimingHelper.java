package com.thfw.base.timing;


import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.HashSet;


/**
 * Author:pengs
 * Date: 2021/11/4 11:12
 * Describe:Todo
 */
public class TimingHelper {

    private static final String TAG = TimingHelper.class.getSimpleName();
    private Handler mHandler;
    private HashMap<WorkInt, HashSet<WorkListener>> mWorkInts;


    private TimingHelper() {
        mWorkInts = new HashMap<>();
        mHandler = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (!mWorkInts.isEmpty()) {
                    for (WorkInt workInt : mWorkInts.keySet()) {
                        workInt.addCount();
                        Log.d(TAG, "workInt.addCount() = " + workInt.toString());
                        if (workInt.arrive()) {
                            notifyWork(workInt);
                        }
                    }
                }

                sendEmptyMessageDelayed(0, 1000);
            }
        };
        mHandler.sendEmptyMessageDelayed(0, 1000);

    }

    public void notifyWork(WorkInt workInt) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "workInt.arrive() = " + workInt.toString());
                HashSet<WorkListener> workListeners = mWorkInts.get(workInt);
                for (WorkListener workListener : workListeners) {
                    workListener.onArrive();
                }
                // 非重复任务，去除
                if (!workInt.isRepeat()) {
                    removeWorkArriveListener(workInt);
                }
            }
        });
    }


    public interface WorkListener {

        void onArrive();

        WorkInt workInt();
    }

    public static class Factory {
        private static final TimingHelper instance = new TimingHelper();
    }

    public static TimingHelper getInstance() {
        return Factory.instance;
    }


    private void removeWorkInt(WorkInt workInt) {
        mWorkInts.remove(workInt);
    }

    public void addWorkArriveListener(WorkListener workListener) {
        if (mWorkInts.containsKey(workListener.workInt())) {
            mWorkInts.get(workListener.workInt()).add(workListener);
        } else {
            HashSet<WorkListener> workListeners = new HashSet<>();
            workListeners.add(workListener);
            mWorkInts.put(workListener.workInt(), workListeners);
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
        if (mWorkInts.containsKey(workInt)) {
            mWorkInts.remove(workInt);
        }
    }


}
