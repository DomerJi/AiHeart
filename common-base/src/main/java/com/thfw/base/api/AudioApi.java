package com.thfw.base.api;

import com.thfw.base.models.AudioEtcDetailModel;
import com.thfw.base.models.AudioEtcModel;
import com.thfw.base.models.AudioLastEtcModel;
import com.thfw.base.models.AudioTypeModel;
import com.thfw.base.models.CommonModel;
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
 * Describe:音频相关api
 */
public interface AudioApi {

    /**
     * 音频分类
     *
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST("music/type")
    Observable<HttpResult<List<AudioTypeModel>>> getAudioType(@FieldMap Map<String, Object> params);

    /**
     * 音频合集列表
     *
     * @return
     */
    @FormUrlEncoded
    @POST("music/list")
    Observable<HttpResult<List<AudioEtcModel>>> getAudioEtcList(@Field("type") int type, @Field("page") int page);

    /**
     * 音频合集详情
     *
     * @return
     */
    @FormUrlEncoded
    @POST("music/info")
    Observable<HttpResult<AudioEtcDetailModel>> getAudioEtcInfo(@Field("id") int id);

    /**
     * 点击播放音频保存播放记录
     *
     * @return
     */
    @FormUrlEncoded
    @POST("music/history_remark")
    Observable<HttpResult<CommonModel>> addAudioHistory(@Field("music_id") int musicId, @Field("collection_id") int collectId);


    @FormUrlEncoded
    @POST("music/last_use")
    Observable<HttpResult<AudioLastEtcModel>> getAudioLastHistory(@FieldMap Map<String, Object> params);


}
