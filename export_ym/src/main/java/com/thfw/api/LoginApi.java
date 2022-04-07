package com.thfw.api;


import com.thfw.models.HttpResult;
import com.thfw.models.TokenModel;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Author:pengs
 * Date: 2021/11/29 18:00
 * Describe:登录相关
 */
public interface LoginApi {


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


}
