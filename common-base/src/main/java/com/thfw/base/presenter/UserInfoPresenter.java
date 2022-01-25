package com.thfw.base.presenter;

import com.thfw.base.api.UserInfoApi;
import com.thfw.base.base.IPresenter;
import com.thfw.base.base.UI;
import com.thfw.base.models.CommonModel;
import com.thfw.base.net.HttpResult;
import com.thfw.base.net.NetParams;
import com.thfw.base.net.OkHttpUtil;
import com.thfw.user.login.User;

import io.reactivex.Observable;

/**
 * Author:pengs
 * Date: 2022/1/21 16:42
 * Describe:Todo
 */
public class UserInfoPresenter extends IPresenter<UserInfoPresenter.UserInfoUi> {


    public UserInfoPresenter(UserInfoUi ui) {
        super(ui);
    }

    public void onGetUserInfo() {
        Observable<HttpResult<User.UserInfo>> observable = OkHttpUtil.createService(UserInfoApi.class)
                .getUserInfo(NetParams.crete());
        OkHttpUtil.request(observable, getUI());
    }

    public void onUpdate(NetParams netParams) {
        Observable<HttpResult<CommonModel>> observable = OkHttpUtil.createService(UserInfoApi.class)
                .onUserUpdate(netParams);
        OkHttpUtil.request(observable, getUI());
    }


    public interface UserInfoUi<T> extends UI<T> {

    }

}
