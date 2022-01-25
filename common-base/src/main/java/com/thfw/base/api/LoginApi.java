package com.thfw.base.api;

import com.thfw.base.models.CommonModel;
import com.thfw.base.models.TokenModel;
import com.thfw.base.net.HttpResult;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Author:pengs
 * Date: 2021/11/29 18:00
 * Describe:Todo
 */
public interface LoginApi {

    /**
     * [登录接口]
     * device_type string 目前支持 phone | pad | robot
     * device_id string 设备标识
     * identification string 账号
     * password string 密码
     * time_stamp long 时间戳
     * verification_code 验证码
     *
     * @return
     */
    @FormUrlEncoded
    @POST("login")
    Observable<HttpResult<TokenModel>> onLogin(@FieldMap Map<String, Object> params);

    /**
     * [发送验证码]
     *
     * @param phoneNumber 手机号
     * @param type        1 登录 2 找回密码 3 绑定手机号
     * @return
     */
    @FormUrlEncoded
    @POST("send_mobile_code")
    Observable<HttpResult<CommonModel>> onSendCode(@Field("phone_number") String phoneNumber,
                                                   @Field("type") int type);

    /**
     * [密码重置]
     * phone_number 手机号
     * verification_code 验证码
     * type 1 验证码 2 新旧密码
     * password 新密码 旧密码
     * old_password
     *
     * @return
     */
    @FormUrlEncoded
    @POST("reset_password")
    Observable<HttpResult<CommonModel>> onSetPassword(@FieldMap Map<String, Object> params);

    /**
     * [验证码-手机验证码验证]
     *
     * @return
     */
    @FormUrlEncoded
    @POST("mobile_code_check")
    Observable<HttpResult<CommonModel>> onCheckMobileCode(@Field("mobile") String mobile,
                                                      @Field("code") String code);


}
