package com.thfw.robotheart.activitys.video;

import com.thfw.base.models.CommonModel;
import com.thfw.base.models.VideoEtcModel;
import com.thfw.base.models.VideoModel;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.VideoPresenter;
import com.thfw.base.utils.LogUtil;
import com.trello.rxlifecycle2.LifecycleProvider;

import java.util.HashMap;

/**
 * Author:pengs
 * Date: 2021/12/17 15:30
 * Describe:视频历史记录管理
 */
public class VideoHistoryHelper {
    // 视频开始，结尾小于2500毫秒，记录为从0开始
    public static final long MIN_TIME_MS = 2500;
    private static final String TAG = VideoHistoryHelper.class.getSimpleName();
    private static HashMap<Integer, Long> positionMap = new HashMap<>();
    private static VideoModel lastVideoModel;
    private static VideoEtcModel lastVideoEtcModel;

    public static void clearAll() {
        lastVideoModel = null;
        positionMap.clear();
    }

    public static void addHistory(VideoEtcModel videoEtcModel) {
        lastVideoEtcModel = videoEtcModel;
    }

    public static void addHistory(VideoModel videoModel, long currentPosition, long duration) {
        if (videoModel == null) {
            return;
        }

        lastVideoModel = videoModel;
        positionMap.put(videoModel.getId(), currentPosition);

        LogUtil.d(TAG, "addHistory+++++++++++++++++++++++++++++++begin");
        new VideoPresenter(new VideoPresenter.VideoUi<CommonModel>() {
            @Override
            public LifecycleProvider getLifecycleProvider() {
                return null;
            }

            @Override
            public void onSuccess(CommonModel data) {
                LogUtil.d(TAG, "addHistory+++++++++++++++++++++++++++++++onSuccess");
            }

            @Override
            public void onFail(ResponeThrowable throwable) {
                LogUtil.d(TAG, "addHistory+++++++++++++++++++++++++++++++onFail");
            }
        }).addVideoHistory(videoModel.getId(), String.valueOf(currentPosition), String.valueOf(duration));
    }

    public static long getPosition(int videoId) {
        if (positionMap.containsKey(videoId)) {
            return positionMap.get(videoId);
        }
        return 0;
    }

}
