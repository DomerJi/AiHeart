package com.thfw.mobileheart.util;

import com.thfw.base.models.MoodLivelyModel;
import com.thfw.base.models.MoodModel;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.MobilePresenter;
import com.thfw.base.timing.TimingHelper;
import com.thfw.base.timing.WorkInt;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.base.utils.FunctionType;
import com.thfw.base.utils.HandlerUtil;
import com.thfw.user.login.UserManager;
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
    private static TimingHelper.WorkListener workListener;

    public static long getTodayActiveTime() {
        return model == null ? 0 : model.getTodayActiveTime();
    }

    public static MoodLivelyModel getModel() {
        return model;
    }

    public static void clearModel() {
        model = null;
        change();
    }

    public static void notifyMood(MoodModel moodModel) {
        if (model != null) {
            model.setUserMood(moodModel);
            change();
        }

    }

    private synchronized static void change() {
        if (!EmptyUtil.isEmpty(livelyListeners)) {
            HandlerUtil.getMainHandler().post(new Runnable() {
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
        if (!UserManager.getInstance().isTrueLogin()) {
            initWorkListener();
            TimingHelper.getInstance().addWorkArriveListener(workListener);
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
                    if (workListener != null) {
                        TimingHelper.getInstance().removeWorkArriveListener(workListener);
                    }
                } else {
                    initWorkListener();
                    TimingHelper.getInstance().addWorkArriveListener(workListener);
                }

            }

            @Override
            public void onFail(ResponeThrowable throwable) {
                initWorkListener();
                TimingHelper.getInstance().addWorkArriveListener(workListener);
            }
        }).onGetMoodLivelyDetail();
    }

    private static void initWorkListener() {
        if (workListener == null) {
            workListener = new TimingHelper.WorkListener() {
                @Override
                public void onArrive() {
                    if (UserManager.getInstance().isTrueLogin()) {
                        addListener(null);
                    }
                }

                @Override
                public WorkInt workInt() {
                    return WorkInt.SECOND5_MSG_COUNT2;
                }
            };
        }
    }

    public interface MoodLivelyListener {
        void onMoodLively(MoodLivelyModel data);
    }
}
