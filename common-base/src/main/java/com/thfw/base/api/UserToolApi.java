package com.thfw.base.api;

import com.thfw.base.models.DialogTalkModel;
import com.thfw.base.models.ExerciseModel;
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
 * Date: 2022/1/17 9:52
 * Describe:用户工具包
 */
public interface UserToolApi {

    /**
     * 工具包列表
     *
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST("tool_package/list")
    Observable<HttpResult<List<ExerciseModel>>> getList(@FieldMap Map<String, Object> params);


    /**
     * 工具包-详情
     *
     * @param id
     * @return
     */
    @FormUrlEncoded
    @POST("tool_package/info")
    Observable<HttpResult<ExerciseModel>> getPackageInfo(@Field("id") int id);


    /**
     * 工具包-对话
     *
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST("dialog/tool_package")
    Observable<HttpResult<List<DialogTalkModel>>> onDialogTool(@FieldMap Map<String, Object> params);


}
