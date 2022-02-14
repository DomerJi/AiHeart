package com.thfw.base.api;

import com.thfw.base.models.CommonModel;
import com.thfw.base.models.TaskDetailModel;
import com.thfw.base.models.TaskItemModel;
import com.thfw.base.models.TaskMusicEtcModel;
import com.thfw.base.net.HttpResult;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Author:pengs
 * Date: 2022/2/10 14:25
 * Describe:任务
 */
public interface TaskApi {

    /**
     * 任务-用户任务列表
     */
    @FormUrlEncoded
    @POST("task/list")
    Observable<HttpResult<List<TaskItemModel>>> onGetList(@Field("status") int status, @Field("page") int page);

    /**
     * 任务-用户任务详情
     */
    @FormUrlEncoded
    @POST("task/info")
    Observable<HttpResult<TaskDetailModel>> onGetInfo(@Field("id") int id);


    /**
     * 任务-完成音频收听
     */
    @FormUrlEncoded
    @POST("music/finish")
    Observable<HttpResult<CommonModel>> onFinishMusic(@Field("music_id") int musicId, @Field("collection_id") int collectionId);


    /**
     * 任务-返回合集任务内所有音频完成情况
     */
    @FormUrlEncoded
    @POST("task/collection_info")
    Observable<HttpResult<TaskMusicEtcModel>> onMusicEtcInfo(@Field("id") int id);

}
