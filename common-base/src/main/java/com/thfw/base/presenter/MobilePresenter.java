package com.thfw.base.presenter;

import com.thfw.base.api.MobileApi;
import com.thfw.base.base.IPresenter;
import com.thfw.base.base.UI;
import com.thfw.base.models.CommonModel;
import com.thfw.base.models.HomeEntity;
import com.thfw.base.models.MobileRecommendModel;
import com.thfw.base.models.MoodActiveModel;
import com.thfw.base.models.MoodLivelyModel;
import com.thfw.base.models.MoodModel;
import com.thfw.base.net.HttpResult;
import com.thfw.base.net.NetParams;
import com.thfw.base.net.OkHttpUtil;

import java.util.List;

import io.reactivex.Observable;

/**
 * Author:pengs
 * Date: 2022/4/15 10:38
 * Describe:手机独有的接口， 心情、首页、活跃、轮播
 */
public class MobilePresenter extends IPresenter<MobilePresenter.MobileUi> {


    public MobilePresenter(MobileUi ui) {
        super(ui);
    }


    public void onGetRecommendList(int page) {
        Observable<HttpResult<List<MobileRecommendModel>>> observable = OkHttpUtil.createService(MobileApi.class)
                .getMobileRecommend(NetParams.crete()
                        .add("page_size", 10)
                        .add("page", page));
        OkHttpUtil.request(observable, getUI());
    }


    public void onGetMoodList() {
        Observable<HttpResult<List<MoodModel>>> observable = OkHttpUtil.createService(MobileApi.class)
                .getMoodList(NetParams.crete());
        OkHttpUtil.request(observable, getUI());
    }

    public void onSavedMood(NetParams netParams) {
        Observable<HttpResult<CommonModel>> observable = OkHttpUtil.createService(MobileApi.class)
                .onSavedMood(netParams);
        OkHttpUtil.request(observable, getUI());
    }

    public void onSaveActiveTime(NetParams netParams) {
        Observable<HttpResult<CommonModel>> observable = OkHttpUtil.createService(MobileApi.class)
                .onSaveActiveTime(netParams);
        OkHttpUtil.request(observable, getUI());
    }

    public void onGetMoodLivelyDetail() {
        Observable<HttpResult<MoodLivelyModel>> observable = OkHttpUtil.createService(MobileApi.class)
                .getMoodLivelyDetail(NetParams.crete());
        OkHttpUtil.request(observable, getUI());
    }


    public void onGetBannerDetail() {
        Observable<HttpResult<List<HomeEntity.BannerModel>>> observable = OkHttpUtil.createService(MobileApi.class)
                .getHomeBanner(NetParams.crete());
        OkHttpUtil.request(observable, getUI());
    }


    public void onGetUserList(NetParams netParams) {
        Observable<HttpResult<List<MoodActiveModel>>> observable = OkHttpUtil.createService(MobileApi.class)
                .getHistoryMoodList(netParams);
        OkHttpUtil.request(observable, getUI());
    }


    public interface MobileUi<T> extends UI<T> {

    }
}
