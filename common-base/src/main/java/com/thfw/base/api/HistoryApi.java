package com.thfw.base.api;

import com.thfw.base.models.CommonModel;
import com.thfw.base.models.HistoryModel;
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
 * Date: 2021/12/9 15:34
 * Describe:收藏记录/历史记录
 */
public interface HistoryApi {

    int TYPE_TEST = 1;
    int TYPE_AUDIO = 2;
    int TYPE_VIDEO = 3;
    int TYPE_BOOK = 4;
    int TYPE_EXERCISE = 5;
    int TYPE_STUDY = 6;

    int TYPE_COLLECT_TEST = 1;
    int TYPE_COLLECT_BOOK = 2;
    int TYPE_COLLECT_AUDIO = 3;
    int TYPE_COLLECT_VIDEO = 4;
    int TYPE_COLLECT_EXERCISE = 5;
    int TYPE_COLLECT_STUDY = 6;

    /**
     * [获取历史记录]
     * rid 测评ID 如果传入测评ID则仅获取该测评的历史记录
     * type 1-测评 2-音频  3-视频  4-文章
     *
     * @return
     */
    @FormUrlEncoded
    @POST("getUserHistoryList")
    Observable<HttpResult<List<HistoryModel.HistoryTestModel>>> getHistoryTest(@FieldMap Map<String, Object> params);

    /**
     * [获取历史记录]
     * rid 测评ID 如果传入测评ID则仅获取该测评的历史记录
     * type 1-测评 2-音频  3-视频  4-文章
     *
     * @return
     */
    @FormUrlEncoded
    @POST("getUserHistoryList")
    Observable<HttpResult<List<HistoryModel.HistoryAudioModel>>> getHistoryAudio(@FieldMap Map<String, Object> params);


    /**
     * [获取历史记录]
     * rid 测评ID 如果传入测评ID则仅获取该测评的历史记录
     * type 1-测评 2-音频  3-视频  4-文章
     *
     * @return
     */
    @FormUrlEncoded
    @POST("getUserHistoryList")
    Observable<HttpResult<List<HistoryModel.HistoryVideoModel>>> getHistoryVideo(@FieldMap Map<String, Object> params);


    /**
     * [获取历史记录]
     * rid 测评ID 如果传入测评ID则仅获取该测评的历史记录
     * type 1-测评 2-音频  3-视频  4-文章
     *
     * @return
     */
    @FormUrlEncoded
    @POST("getUserHistoryList")
    Observable<HttpResult<List<HistoryModel.HistoryBookModel>>> getHistoryBook(@FieldMap Map<String, Object> params);

    /**
     * [获取历史记录]
     * rid 测评ID 如果传入测评ID则仅获取该测评的历史记录
     * type 1-测评 2-音频  3-视频  4-文章
     *
     * @return
     */
    @FormUrlEncoded
    @POST("getUserHistoryList")
    Observable<HttpResult<List<HistoryModel.HistoryStudyModel>>> getHistoryStudy(@FieldMap Map<String, Object> params);

    /**
     * [获取历史记录]
     * rid 测评ID 如果传入测评ID则仅获取该测评的历史记录
     * type 1-测评 2-音频  3-视频  4-文章
     *
     * @return
     */
    @FormUrlEncoded
    @POST("getUserHistoryList")
    Observable<HttpResult<List<HistoryModel.HistoryExerciseModel>>> getHistoryExercise(@FieldMap Map<String, Object> params);


    /**
     * [收藏]
     * rid 测评ID 如果传入测评ID则仅获取该测评的历史记录
     * type 1-测评 2-音频  3-视频  4-文章
     *
     * @return
     */
    @FormUrlEncoded
    @POST("content_collect")
    Observable<HttpResult<CommonModel>> addCollect(@Field("type") int type, @Field("id") int id);


}
