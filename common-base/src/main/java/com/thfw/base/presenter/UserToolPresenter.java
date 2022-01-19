package com.thfw.base.presenter;

import com.thfw.base.api.TalkApi;
import com.thfw.base.api.UserToolApi;
import com.thfw.base.base.IPresenter;
import com.thfw.base.base.UI;
import com.thfw.base.models.CommonModel;
import com.thfw.base.models.DialogTalkModel;
import com.thfw.base.models.ExerciseModel;
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
public class UserToolPresenter extends IPresenter<UserToolPresenter.UserToolUi> {

    public UserToolPresenter(UserToolPresenter.UserToolUi ui) {
        super(ui);
    }


    public void onGetList() {
        Observable<HttpResult<List<ExerciseModel>>> observable = OkHttpUtil.createService(UserToolApi.class)
                .getList(NetParams.crete());
        OkHttpUtil.request(observable, getUI());
    }

    public void onGetInfo(int id) {
        Observable<HttpResult<ExerciseModel>> observable = OkHttpUtil.createService(UserToolApi.class)
                .getPackageInfo(id);
        OkHttpUtil.request(observable, getUI());
    }


    public void onDialogTool(NetParams params) {
        Observable<HttpResult<List<CommonModel>>> observable = OkHttpUtil.createService(UserToolApi.class)
                .onDialogTool(params);
        OkHttpUtil.request(observable, getUI());
    }

    public void onJoinDialog(int enter_type, int id) {
        NetParams netParams = NetParams.crete().add("enter_type", enter_type);
        if (enter_type == TalkApi.JOIN_TYPE_SPEECH_CRAFT || enter_type == TalkApi.JOIN_TYPE_TOOL) {
            netParams.add("id", id);
        }

        Observable<HttpResult<List<DialogTalkModel>>> observable = OkHttpUtil.createService(TalkApi.class)
                .onJoinDialog(netParams);
        OkHttpUtil.request(observable, getUI());
    }


    public interface UserToolUi<T> extends UI<T> {

    }
}
