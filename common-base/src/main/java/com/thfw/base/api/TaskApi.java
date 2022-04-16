package com.thfw.base.api;

import com.thfw.base.models.CommonModel;
import com.thfw.base.models.MsgCountModel;
import com.thfw.base.models.PushModel;
import com.thfw.base.models.SystemDetailModel;
import com.thfw.base.models.TaskDetailModel;
import com.thfw.base.models.TaskItemModel;
import com.thfw.base.models.TaskMusicEtcModel;
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
 * Date: 2022/2/10 14:25
 * Describe:任务/消息/系统消息
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

    /**
     * 推送消息列表
     *
     * device_type msgType  page
     * @return
     */
    @FormUrlEncoded
    @POST("push_msg_list")
    Observable<HttpResult<List<TaskItemModel>>> onGetPushMsgList(@FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("push_msg_list")
    Observable<HttpResult<List<PushModel>>> onGetPushMsgList2(@FieldMap Map<String, Object> params);

    /**
     * 已读状态修改
     *
     * @param id
     * @return
     */
    @FormUrlEncoded
    @POST("read_push_msg")
    Observable<HttpResult<CommonModel>> onReadStated(@Field("id") int id);

    @FormUrlEncoded
    @POST("read_push_msg")
    Observable<HttpResult<CommonModel>> onReadStated(@Field("msg_id") String msgId);

    @FormUrlEncoded
    @POST("read_all_msg")
    Observable<HttpResult<CommonModel>> onReadStatedAll(@Field("type") int type);

    /**
     * 新消息数量查询接口
     * <p>
     * type 1-任务消息 2-系统消息 0或不传为所有类型
     *
     * @return
     */
    @FormUrlEncoded
    @POST("new_msg_count")
    Observable<HttpResult<MsgCountModel>> onNewMsgCount(@FieldMap Map<String, Object> params);


    /**
     * 新消息数量查询接口
     * <p>
     * type 1-任务消息 2-系统消息 0或不传为所有类型
     *
     * @return
     */
    @FormUrlEncoded
    @POST("get_push_msg_by_msg_id")
    Observable<HttpResult<SystemDetailModel>> getPushModel(@FieldMap Map<String, Object> params);


}
