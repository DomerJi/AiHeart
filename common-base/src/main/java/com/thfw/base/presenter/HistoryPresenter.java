package com.thfw.base.presenter;

import com.thfw.base.api.HistoryApi;
import com.thfw.base.base.IPresenter;
import com.thfw.base.base.UI;
import com.thfw.base.models.CommonModel;
import com.thfw.base.models.HistoryModel;
import com.thfw.base.net.HttpResult;
import com.thfw.base.net.NetParams;
import com.thfw.base.net.OkHttpUtil;
import com.thfw.base.utils.LogUtil;

import java.util.List;

import io.reactivex.Observable;

/**
 * Author:pengs
 * Date: 2021/12/9 15:45
 * Describe:收藏/历史记录
 */
public class HistoryPresenter extends IPresenter<HistoryPresenter.HistoryUi> {

    public HistoryPresenter(HistoryUi ui) {
        super(ui);
    }

    /**
     * type 1-测评 2-音频  3-视频  4-文章
     *
     * @param type
     */
    public void getUserHistoryList(int type, int page) {
        NetParams netParams = NetParams.crete().add("type", type);
        netParams.add("page", page);
        switch (type) {
            case HistoryApi.TYPE_TEST:
                Observable<HttpResult<List<HistoryModel.HistoryTestModel>>> observableTest = OkHttpUtil.createService(HistoryApi.class)
                        .getHistoryTest(netParams);
                OkHttpUtil.request(observableTest, getUI());
                break;
            case HistoryApi.TYPE_BOOK:
                Observable<HttpResult<List<HistoryModel.HistoryBookModel>>> observableBook = OkHttpUtil.createService(HistoryApi.class)
                        .getHistoryBook(netParams);
                OkHttpUtil.request(observableBook, getUI());
                break;

            case HistoryApi.TYPE_AUDIO:
                Observable<HttpResult<List<HistoryModel.HistoryAudioModel>>> observableAudio = OkHttpUtil.createService(HistoryApi.class)
                        .getHistoryAudio(netParams);
                OkHttpUtil.request(observableAudio, getUI());
                break;
            case HistoryApi.TYPE_VIDEO:
                Observable<HttpResult<List<HistoryModel.HistoryVideoModel>>> observableVideo = OkHttpUtil.createService(HistoryApi.class)
                        .getHistoryVideo(netParams);
                OkHttpUtil.request(observableVideo, getUI());
                break;
            case HistoryApi.TYPE_EXERCISE:
                Observable<HttpResult<List<HistoryModel.HistoryExerciseModel>>> observableExercise = OkHttpUtil.createService(HistoryApi.class)
                        .getHistoryExercise(netParams);
                OkHttpUtil.request(observableExercise, getUI());
                break;
            case HistoryApi.TYPE_STUDY:
                Observable<HttpResult<List<HistoryModel.HistoryStudyModel>>> observableStudy = OkHttpUtil.createService(HistoryApi.class)
                        .getHistoryStudy(netParams);
                OkHttpUtil.request(observableStudy, getUI());
                break;
            default:
                LogUtil.e("getUserHistoryList not this " + type);
                break;
        }

    }

    /**
     * 1测试 2文章
     *
     * @param type
     * @param id
     */
    public void addCollect(int type, int id) {
        Observable<HttpResult<CommonModel>> observable = OkHttpUtil.createService(HistoryApi.class)
                .addCollect(type, id);
        OkHttpUtil.request(observable, getUI());
    }

    public interface HistoryUi<T> extends UI<T> {

    }
}
