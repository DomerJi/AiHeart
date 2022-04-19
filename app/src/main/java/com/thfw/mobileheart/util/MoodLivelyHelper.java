package com.thfw.mobileheart.util;

import android.os.Handler;
import android.os.Looper;

import com.thfw.base.models.MoodLivelyModel;
import com.thfw.base.models.MoodModel;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.MobilePresenter;
import com.thfw.base.timing.TimingHelper;
import com.thfw.base.timing.WorkInt;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.base.utils.FunctionType;
import com.trello.rxlifecycle2.LifecycleProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Author:pengs
 * Date: 2022/4/16 13:03
 * Describe:Todo
 */
public class MoodLivelyHelper {

    private static MoodLivelyModel model;
    private static List<MoodLivelyListener> livelyListeners = new ArrayList<>();

    public static MoodLivelyModel getModel() {
        return model;
    }

    public static void clearModel() {
        model = null;
    }

    public static void notifyMood(MoodModel moodModel) {
        if (model != null) {
            model.setUserMood(moodModel);
            change();
        }

    }

    private synchronized static void change() {
        if (!EmptyUtil.isEmpty(livelyListeners)) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    int size = livelyListeners.size();
                    for (int i = size - 1; i >= 0; i--) {
                        if (livelyListeners.get(i) != null) {
                            livelyListeners.get(i).onMoodLively(model);
                        }
                    }
                }
            });

        }
    }

    public static void removeListener(MoodLivelyListener livelyListener) {
        if (livelyListeners.contains(livelyListener)) {
            livelyListeners.remove(livelyListener);
        }
    }

    public static void addListener(MoodLivelyListener livelyListener) {
        if (!livelyListeners.contains(livelyListener)) {
            livelyListeners.add(livelyListener);
        }
        if (model != null) {
            if (livelyListener != null) {
                livelyListener.onMoodLively(model);
            }
            return;
        }
        new MobilePresenter(new MobilePresenter.MobileUi<MoodLivelyModel>() {
            @Override
            public LifecycleProvider getLifecycleProvider() {
                return null;
            }

            @Override
            public void onSuccess(MoodLivelyModel data) {
                if (data != null) {
                    model = data;
                    FunctionDurationUtil.setFunctionTime(FunctionType.FUNCTION_APP, model.getTodayActiveTime());
                    change();
                }
                if(workListener!=null) {
                    TimingHelper.getInstance().removeWorkArriveListener(workListener);
                }
            }

            @Override
            public void onFail(ResponeThrowable throwable) {
                initWorkListener();
                TimingHelper.getInstance().addWorkArriveListener(workListener);
            }
        }).onGetMoodLivelyDetail();
    }

    public interface MoodLivelyListener {
        void onMoodLively(MoodLivelyModel data);
    }

    private static TimingHelper.WorkListener workListener;

    private static void initWorkListener() {
        if (workListener == null) {
            workListener = new TimingHelper.WorkListener() {
                @Override
                public void onArrive() {
                    addListener(null);
                }

                @Override
                public WorkInt workInt() {
                    return WorkInt.SECOND5_MSG_COUNT2;
                }
            };
        }
    }
}
