package com.thfw.base.presenter;

import com.thfw.base.api.AudioApi;
import com.thfw.base.base.IPresenter;
import com.thfw.base.base.UI;
import com.thfw.base.models.AudioEtcDetailModel;
import com.thfw.base.models.AudioEtcModel;
import com.thfw.base.models.AudioLastEtcModel;
import com.thfw.base.models.AudioTypeModel;
import com.thfw.base.models.CommonModel;
import com.thfw.base.net.HttpResult;
import com.thfw.base.net.NetParams;
import com.thfw.base.net.OkHttpUtil;

import java.util.List;

import io.reactivex.Observable;

/**
 * Author:pengs
 * Date: 2021/12/15 16:25
 * Describe:Todo
 */
public class AudioPresenter extends IPresenter<AudioPresenter.AudioUi> {


    public AudioPresenter(AudioUi ui) {
        super(ui);
    }

    public void getAudioType() {
        Observable<HttpResult<List<AudioTypeModel>>> observable = OkHttpUtil.createService(AudioApi.class)
                .getAudioType(NetParams.crete());
        OkHttpUtil.request(observable, getUI());
    }

    public void getAudioEtcList(int type, int page) {
        Observable<HttpResult<List<AudioEtcModel>>> observable = OkHttpUtil.createService(AudioApi.class)
                .getAudioEtcList(type, page);
        OkHttpUtil.request(observable, getUI());
    }

    public void getAudioEtcInfo(int id) {
        Observable<HttpResult<AudioEtcDetailModel>> observable = OkHttpUtil.createService(AudioApi.class)
                .getAudioEtcInfo(id);
        OkHttpUtil.request(observable, getUI());
    }

    public void addAudioHistory(int musicId,int collectId) {
        Observable<HttpResult<CommonModel>> observable = OkHttpUtil.createService(AudioApi.class)
                .addAudioHistory(musicId,collectId);
        OkHttpUtil.request(observable, getUI());
    }

    public void getAudioLastHistory() {
        Observable<HttpResult<AudioLastEtcModel>> observable = OkHttpUtil.createService(AudioApi.class)
                .getAudioLastHistory(NetParams.crete());
        OkHttpUtil.request(observable, getUI());
    }



    public interface AudioUi<T> extends UI<T> {

    }
}
