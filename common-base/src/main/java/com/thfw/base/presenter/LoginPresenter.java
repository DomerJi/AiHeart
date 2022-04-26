package com.thfw.base.presenter;


import android.text.TextUtils;

import com.thfw.base.api.LoginApi;
import com.thfw.base.base.IPresenter;
import com.thfw.base.base.UI;
import com.thfw.base.models.CommonModel;
import com.thfw.base.models.TokenModel;
import com.thfw.base.net.CommonParameter;
import com.thfw.base.net.HttpResult;
import com.thfw.base.net.NetParams;
import com.thfw.base.net.OkHttpUtil;

import java.util.Map;

import io.reactivex.Observable;

public class LoginPresenter extends IPresenter<LoginPresenter.LoginUi> {

    public LoginPresenter(LoginUi ui) {
        super(ui);
    }

    /**
     * [手机号验证码登录]
     *
     * @param phone
     * @param code
     */
    public void loginByMobile(String phone, String code) {
        NetParams netParams = NetParams.crete().add("device_type", CommonParameter.getDeviceType())
                .add("device_id", CommonParameter.getDeviceId())
                .add("verification_code", code)
                .add("identification", phone)
                .add("time_stamp", System.currentTimeMillis());
        if (!TextUtils.isEmpty(CommonParameter.getOrganizationId())) {
            netParams.add("organization", CommonParameter.getOrganizationId());
        }
        Observable<HttpResult<TokenModel>> observable = OkHttpUtil.createService(LoginApi.class).onLogin(netParams);
        OkHttpUtil.request(observable, getUI());
    }

    /**
     * [账号密码登录]
     *
     * @param identification
     * @param password
     */
    public void loginByPassword(String identification, String password) {
        NetParams netParams = NetParams.crete().add("device_type", CommonParameter.getDeviceType())
                .add("device_id", CommonParameter.getDeviceId())
                .add("password", password)
                .add("identification", identification)
                .add("time_stamp", System.currentTimeMillis());
        if (!TextUtils.isEmpty(CommonParameter.getOrganizationId())) {
            netParams.add("organization", CommonParameter.getOrganizationId());
        }
        Observable<HttpResult<TokenModel>> observable = OkHttpUtil.createService(LoginApi.class).onLogin(netParams);
        OkHttpUtil.request(observable, getUI());
    }

    /**
     * [重置密码] 手机号验证码方式
     *
     * @param phone
     * @param code
     * @param password
     */
    public void setPasswordByCode(String phone, String code, String password) {

        /**
         * @Field("phone_number") String phoneNumber,
         * @Field("verification_code") String code,
         * @Field("type") int type,
         *  @Field("old_password") String oldPassword,
         *  @Field("password") String password
         */
        NetParams netParams = NetParams.crete()
                .add("phone_number", phone)
                .add("verification_code", code)
                .add("type", 1)
                .add("password", password);
        Observable<HttpResult<CommonModel>> observable = OkHttpUtil.createService(LoginApi.class).onSetPassword(netParams);
        OkHttpUtil.request(observable, getUI());
    }

    /**
     * [重置密码] 新旧密码方式
     *
     * @param originPassword
     * @param password
     */
    public void setPasswordByOrigin(String originPassword, String password) {
        /**
         * @Field("phone_number") String phoneNumber,
         * @Field("verification_code") String code,
         * @Field("type") int type,
         *  @Field("old_password") String oldPassword,
         *  @Field("password") String password
         */
        NetParams netParams = NetParams.crete()
                .add("old_password", originPassword)
                .add("type", 2)
                .add("password", password);
        Observable<HttpResult<CommonModel>> observable = OkHttpUtil.createService(LoginApi.class).onSetPassword(netParams);
        OkHttpUtil.request(observable, getUI());
    }

    /**
     * [发送验证码]
     * phone_number 手机号
     * type 登录使用-1 修改密码-2 绑定手机号-3
     *
     * @param phone
     */
    public void onSendCode(String phone, int type) {
        Observable<HttpResult<CommonModel>> observable = OkHttpUtil.createService(LoginApi.class).onSendCode(phone, type);
        OkHttpUtil.request(observable, getUI());
    }

    public void onBindPhone(String phone, String code) {
        Observable<HttpResult<CommonModel>> observable = OkHttpUtil.createService(LoginApi.class).onCheckMobileCode(phone, code);
        OkHttpUtil.request(observable, getUI());
    }

    public void onIsFaceOpen() {
        Observable<HttpResult<CommonModel>> observable = OkHttpUtil.createService(LoginApi.class).onIsFaceOpen(NetParams.crete());
        OkHttpUtil.request(observable, getUI());
    }

    public void loginByThirdParty(Map<String, String> map) {

    }

    public interface LoginUi<T> extends UI<T> {

    }

    public interface SendType {
        int LOGIN = 1;
        int SET_PASSWORD = 2;
        int BIND_PHONE = 3;
    }
}
