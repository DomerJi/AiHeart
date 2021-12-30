package com.thfw.base.api;

import com.thfw.base.models.DialogDetailModel;
import com.thfw.base.models.DialogTalkModel;
import com.thfw.base.models.ThemeTalkModel;
import com.thfw.base.net.HttpResult;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Author:pengs
 * Date: 2021/12/23 16:34
 * Describe:Todo
 */
public interface TalkApi {

    /**
     * 进入话术类型
     */
    // 1-话术列表点击进入
    int JOIN_TYPE_SPEECH_CRAFT = 1;
    // 2-咨询助理点击进入
    int JOIN_TYPE_GUIDANCE = 2;
    // 3-密友树洞点击进入
    int JOIN_TYPE_AI = 3;

    /**
     * [获取主题对话列表]
     *
     * @return
     */
    @FormUrlEncoded
    @POST("dialog/list")
    Observable<HttpResult<List<ThemeTalkModel>>> getDialogList(@FieldMap Map<String, Object> params);

    /**
     * [主题对话详情]
     *
     * @return
     */
    @FormUrlEncoded
    @POST("dialog/info")
    Observable<HttpResult<DialogDetailModel>> getDialogInfo(@Field("id") int id);


    /**
     * [进入对话]
     * enter_type 进入话术类型 1-话术列表点击进入 2-咨询助理点击进入 3-密友树洞点击进入
     * id 当 enter_type=1 时 需传入点击话术的ID
     *
     * @return
     */
    @FormUrlEncoded
    @POST("dialog/enter_dialog")
    Observable<HttpResult<List<DialogTalkModel>>> onJoinDialog(@FieldMap Map<String, Object> params);


    /**
     * [内容-话术-专业对话场景话术进行请求接口]
     * id int 所选单选题的所在话术id
     * value int 单选题所选选项KEY
     * question String 自由输入题输入的回复
     *
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST("dialog/pro_dialog")
    Observable<HttpResult<List<DialogTalkModel>>> onThemeDialog(@FieldMap Map<String, Object> params);

    /**
     * [内容-对话-树洞对话]
     * id int 所选单选题的所在话术id
     * value int 单选题所选选项KEY
     * question String 自由输入题输入的回复
     *
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST("dialog/tree_hole")
    Observable<HttpResult<DialogDetailModel>> onAIDialog(@FieldMap Map<String, Object> params);


}
