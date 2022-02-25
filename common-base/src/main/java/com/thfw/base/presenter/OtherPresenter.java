package com.thfw.base.presenter;

import com.thfw.base.api.OtherApi;
import com.thfw.base.base.IPresenter;
import com.thfw.base.base.UI;
import com.thfw.base.models.AboutUsModel;
import com.thfw.base.models.CommonProblemModel;
import com.thfw.base.models.HotCallModel;
import com.thfw.base.models.VoiceInstructionModel;
import com.thfw.base.net.HttpResult;
import com.thfw.base.net.NetParams;
import com.thfw.base.net.OkHttpUtil;

import java.util.List;

import io.reactivex.Observable;

/**
 * Author:pengs
 * Date: 2022/1/17 10:52
 * Describe:Todo
 */
public class OtherPresenter extends IPresenter<OtherPresenter.OtherUi> {

    public OtherPresenter(OtherPresenter.OtherUi ui) {
        super(ui);
    }


    public void onGetHotPhoneList() {
        Observable<HttpResult<List<HotCallModel>>> observable = OkHttpUtil.createService(OtherApi.class)
                .getHotPhoneList(NetParams.crete());
        OkHttpUtil.request(observable, getUI());
    }

    public void onGetCommonProblem(int page) {
        Observable<HttpResult<List<CommonProblemModel>>> observable = OkHttpUtil.createService(OtherApi.class)
                .getQuestionAnswer(NetParams.crete().add("page", page));
        OkHttpUtil.request(observable, getUI());
    }


    public void onGetVoiceInstruction(int page) {
        Observable<HttpResult<List<VoiceInstructionModel>>> observable = OkHttpUtil.createService(OtherApi.class)
                .getVoiceInstruction(NetParams.crete().add("page", page));
        OkHttpUtil.request(observable, getUI());
    }

    public void onGetAboutUs() {
        Observable<HttpResult<AboutUsModel>> observable = OkHttpUtil.createService(OtherApi.class)
                .getAboutUs(NetParams.crete());
        OkHttpUtil.request(observable, getUI());
    }


    public interface OtherUi<T> extends UI<T> {

    }
}
