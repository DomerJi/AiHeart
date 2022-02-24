package com.thfw.base.api;

import com.thfw.base.models.CommonModel;
import com.thfw.base.models.PresetAvatarModel;
import com.thfw.base.net.HttpResult;
import com.thfw.user.login.User;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Author:pengs
 * Date: 2022/1/21 16:04
 * Describe:用户信息
 */
public interface UserInfoApi {

    /**
     * [获取主题对话列表]
     *
     * @return
     */
    @FormUrlEncoded
    @POST("user/info")
    Observable<HttpResult<User.UserInfo>> getUserInfo(@FieldMap Map<String, Object> params);


    /**
     * [修改个人信息]
     *
     * @return
     */
    @FormUrlEncoded
    @POST("user/update")
    Observable<HttpResult<CommonModel>> onUserUpdate(@FieldMap Map<String, Object> params);

    /**
     * [预置头像库]
     *
     * @return
     */
    @FormUrlEncoded
    @POST("user/pic/list")
    Observable<HttpResult<List<PresetAvatarModel>>> onPresetAvatarList(@FieldMap Map<String, Object> params);

    /**
     * 【个人信息字段罗列】
     *
     user_name string 用户昵称

     pic string 用户头像路径

     true_name string 真实姓名

     birth string 出生日期 格式：yyyy-mm-dd

     sex int 性别 1-男 2-女 0-未选择

     education int 学历 0-未选择 1-中专 2-大专 3-高中 4-本科 5-硕士 6-博士

     marital_status int 婚姻状况 1-单身 2-已婚  3-离异 4-丧偶 0-未选择

     child_status int 子女状况 0-未选择 1-有 2-无

     political_outlook int 政治面貌 0-未选择 1-党员  2-团员 3-群众 4-其他

     rank string 职级 字符串（因为存在用户自定义） 可选项：普通/中层/高层

     hobby array 兴趣爱好列表

     support array 所需要支持列表

     department string 部门

     native string 籍贯

     nation string 民族

     */


}
