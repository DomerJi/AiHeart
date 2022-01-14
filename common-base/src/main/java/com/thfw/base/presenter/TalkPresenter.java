package com.thfw.base.presenter;

import com.thfw.base.api.TalkApi;
import com.thfw.base.base.IPresenter;
import com.thfw.base.base.UI;
import com.thfw.base.models.ChosenModel;
import com.thfw.base.models.DialogDetailModel;
import com.thfw.base.models.DialogTalkModel;
import com.thfw.base.models.ThemeTalkModel;
import com.thfw.base.net.HttpResult;
import com.thfw.base.net.NetParams;
import com.thfw.base.net.OkHttpUtil;
import com.thfw.base.utils.GsonUtil;

import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;

/**
 * Author:pengs
 * Date: 2021/12/23 16:37
 * Describe:Todo
 */
public class TalkPresenter<T> extends IPresenter<TalkPresenter.TalkUi> {


    public TalkPresenter(TalkUi ui) {
        super(ui);
    }

    public void getDialogList() {
        Observable<HttpResult<List<ThemeTalkModel>>> observable = OkHttpUtil.createService(TalkApi.class)
                .getDialogList(NetParams.crete());
        OkHttpUtil.request(observable, getUI());
    }

    public void getDialogInfo(int id) {
        Observable<HttpResult<DialogDetailModel>> observable = OkHttpUtil.createService(TalkApi.class)
                .getDialogInfo(id);
        OkHttpUtil.request(observable, getUI());
    }


    public void onJoinDialog(int enter_type, int id) {
        NetParams netParams = NetParams.crete().add("enter_type", enter_type);
        if (enter_type == TalkApi.JOIN_TYPE_SPEECH_CRAFT) {
            netParams.add("id", id);
        }

        Observable<HttpResult<List<DialogTalkModel>>> observable = OkHttpUtil.createService(TalkApi.class)
                .onJoinDialog(netParams);
        OkHttpUtil.request(observable, getUI());
    }

    public void onChooseOption(String inputText, HashMap<String, String> radio) {
        Observable<HttpResult<ChosenModel>> observable = OkHttpUtil.createService(TalkApi.class)
                .onChooseOption(inputText, GsonUtil.toJson(radio));
        OkHttpUtil.request(observable, getUI());
    }

    public void onDialogHistory(int scene, String data, int id, String type) {
        Observable<HttpResult<List<DialogTalkModel>>> observable = OkHttpUtil.createService(TalkApi.class)
                .onDialogHistory(scene, data, id, type);
        OkHttpUtil.request(observable, getUI());
    }

    public void onJoinDialog(int enter_type) {
        onJoinDialog(enter_type, -1);
    }

    public void onDialog(int scene, NetParams netParams) {
        if (scene == 1) {
            onAIDialog(netParams);
        } else {
            onThemeDialog(netParams);
        }
    }

    public void onThemeDialog(NetParams netParams) {
        Observable<HttpResult<List<DialogTalkModel>>> observable = OkHttpUtil.createService(TalkApi.class)
                .onThemeDialog(netParams);
        OkHttpUtil.request(observable, getUI());
    }

    public void onAIDialog(NetParams netParams) {
        Observable<HttpResult<List<DialogTalkModel>>> observable = OkHttpUtil.createService(TalkApi.class)
                .onAIDialog(netParams);
        OkHttpUtil.request(observable, getUI());
    }

    public void onMonthHasDay(int scene, String month) {
        Observable<HttpResult<List<String>>> observable = OkHttpUtil.createService(TalkApi.class)
                .onMonthHasDay(scene, month);
        OkHttpUtil.request(observable, getUI());
    }


    public interface TalkUi<T> extends UI<T> {

    }
}
