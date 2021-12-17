package com.thfw.base.presenter;

import com.thfw.base.api.VideoApi;
import com.thfw.base.base.IPresenter;
import com.thfw.base.base.UI;
import com.thfw.base.models.CommonModel;
import com.thfw.base.models.VideoEtcModel;
import com.thfw.base.models.VideoModel;
import com.thfw.base.models.VideoTypeModel;
import com.thfw.base.net.HttpResult;
import com.thfw.base.net.NetParams;
import com.thfw.base.net.OkHttpUtil;

import java.util.List;

import io.reactivex.Observable;

/**
 * Author:pengs
 * Date: 2021/12/15 16:25
 * Describe:视频相关
 */
public class VideoPresenter extends IPresenter<VideoPresenter.VideoUi> {


    public VideoPresenter(VideoUi ui) {
        super(ui);
    }

    public void getVideoType() {
        Observable<HttpResult<List<VideoTypeModel>>> observable = OkHttpUtil.createService(VideoApi.class)
                .getVideoType(NetParams.crete());
        OkHttpUtil.request(observable, getUI());
    }

    public void getVideoList(int type, int page) {
        Observable<HttpResult<List<VideoEtcModel>>> observable = OkHttpUtil.createService(VideoApi.class)
                .getAudioList(type, page);
        OkHttpUtil.request(observable, getUI());
    }

    public void getVideoInfo(int id) {
        Observable<HttpResult<VideoModel>> observable = OkHttpUtil.createService(VideoApi.class)
                .getVideoInfo(id);
        OkHttpUtil.request(observable, getUI());
    }

    public void addVideoHistory(int videoId, String time) {
        Observable<HttpResult<CommonModel>> observable = OkHttpUtil.createService(VideoApi.class)
                .addVideoHistory(videoId, time);
        OkHttpUtil.request(observable, getUI());
    }


    public interface VideoUi<T> extends UI<T> {

    }
}
