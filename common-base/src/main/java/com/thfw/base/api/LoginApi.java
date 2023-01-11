package com.thfw.base.api;

import com.thfw.base.models.CommonModel;
import com.thfw.base.models.TokenModel;
import com.thfw.base.net.HttpResult;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

/**
 * Author:pengs
 * Date: 2021/11/29 18:00
 * Describe:登录相关
 */
public interface LoginApi {

    String KEY_DELETE_TIME = "account_delete_time";

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
     * 第三方登录/免账号/免手机号+验证吗登录
     * <p>
     * phone 手机号，通过手机号来定位用户是否存在（非必传）
     * organization 机构ID
     * user_name 用户昵称
     * open_id  三方渠道用户唯一标识ID（必传）
     *
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST("platform_login")
    Observable<HttpResult<TokenModel>> onLoginByOpenId(@FieldMap Map<String, Object> params);

    /**
     * [发送验证码]
     *
     * @param phoneNumber 手机号
     * @param type        1 登录 2 找回密码 3 绑定手机号
     * @return
     */
    @FormUrlEncoded
    @POST("send_mobile_code")
    Observable<HttpResult<CommonModel>> onSendCode(@Field("phone_number") String phoneNumber, @Field("type") int type);

    /**
     * [发送验证码]
     *
     * @return
     */
    @FormUrlEncoded
    @POST("face_login/query_user")
    Observable<HttpResult<CommonModel>> onIsFaceOpen(@FieldMap Map<String, Object> params);

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
    @POST("user/changePassword")
    Observable<HttpResult<CommonModel>> onSetPasswordFirst(@FieldMap Map<String, Object> params);

    /**
     * [验证码-手机验证码验证]
     *
     * @return
     */
    @FormUrlEncoded
    @POST("mobile_code_check")
    Observable<HttpResult<CommonModel>> onCheckMobileCode(@Field("mobile") String mobile, @Field("code") String code);

    /**
     * [用户注销]
     *
     * @return
     */
    @GET("user/unRegister")
    Observable<HttpResult<String>> onDeleteAccount(@QueryMap Map<String, Object> params);


}
