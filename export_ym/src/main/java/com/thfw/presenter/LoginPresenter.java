package com.thfw.presenter;


import android.text.TextUtils;

import com.thfw.api.LoginApi;
import com.thfw.base.IPresenter;
import com.thfw.base.UI;
import com.thfw.models.HttpResult;
import com.thfw.models.TokenModel;
import com.thfw.net.NetParams;
import com.thfw.net.OkHttpUtil;

import io.reactivex.Observable;

public class LoginPresenter extends IPresenter<LoginPresenter.LoginUi> {

    public LoginPresenter(LoginUi ui) {
        super(ui);
    }

    public void loginByOpenId(String phone, String organization, String userName, String openId) {
        NetParams netParams = NetParams.crete()
                .add("open_id", "wxa81b50236fdd4a1d_50_1547549652797177858")
                .add("user_name", userName)
                .add("organization", organization);
        if (!TextUtils.isEmpty(phone)) {
            netParams.add("phone", phone);
        }
        Observable<HttpResult<TokenModel>> observable = OkHttpUtil.createService(LoginApi.class).onLoginByOpenId(netParams);
        OkHttpUtil.request(observable, getUI());

    }


    public interface LoginUi<T> extends UI<T> {

    }

    public interface SendType {
        int LOGIN = 1;
        int SET_PASSWORD = 2;
        int BIND_PHONE = 3;
    }
}
