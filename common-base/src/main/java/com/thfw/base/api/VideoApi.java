package com.thfw.base.api;

import com.thfw.base.models.CommonModel;
import com.thfw.base.models.VideoEtcModel;
import com.thfw.base.models.VideoLastEtcModel;
import com.thfw.base.models.VideoModel;
import com.thfw.base.models.VideoTypeModel;
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
 * Date: 2021/12/15 16:18
 * Describe:视频相关api
 */
public interface VideoApi {

    /**
     * 视频分类
     *
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST("video/type")
    Observable<HttpResult<List<VideoTypeModel>>> getVideoType(@FieldMap Map<String, Object> params);

    /**
     * 视频合集列表
     *
     * @return
     */
    @FormUrlEncoded
    @POST("video/list")
    Observable<HttpResult<List<VideoEtcModel>>> getAudioList(@FieldMap Map<String, Object> params);

    /**
     * 视频详情
     *
     * @return
     */
    @FormUrlEncoded
    @POST("video/info")
    Observable<HttpResult<VideoModel>> getVideoInfo(@Field("id") int id);


    /**
     * 点击播放视频保存播放记录
     *
     * @return
     */
    @FormUrlEncoded
    @POST("video/history_time_remark")
    Observable<HttpResult<CommonModel>> addVideoHistory(@Field("video_id") int videoId,
                                                        @Field("time") String time,
                                                        @Field("duration") String duration);


    /**
     * 获取用户最后一次使用视频记录
     *
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST("video/last_use")
    Observable<HttpResult<VideoLastEtcModel>> getVideoLastHistory(@FieldMap Map<String, Object> params);


}
