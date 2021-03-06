package com.thfw.api;


import com.thfw.models.CollectModel;
import com.thfw.models.CommonModel;
import com.thfw.models.HistoryModel;
import com.thfw.models.HttpResult;

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

    // 1-测评 2-文章 3-音频  4-视频 6-思政文章 7-工具包
    int TYPE_TEST = 1;
    int TYPE_AUDIO = 3;
    int TYPE_VIDEO = 4;
    int TYPE_BOOK = 2;
    int TYPE_EXERCISE = 7;
    int TYPE_STUDY = 6;
    // 1-测评  2-文章 3-音频 4-视频 5-话术 6-思政文章 7-思政视频
    int TYPE_COLLECT_TEST = 1;
    int TYPE_COLLECT_BOOK = 2;
    int TYPE_COLLECT_AUDIO = 3;
    int TYPE_COLLECT_VIDEO = 4;
    int TYPE_COLLECT_DIALOG = 5;
    int TYPE_COLLECT_IDEO_BOOK = 6;
    int TYPE_COLLECT_TOOL = 7;

    /**
     * [获取历史记录]
     * rid 测评ID 如果传入测评ID则仅获取该测评的历史记录
     * type 1-测评 2-文章 3-音频  4-视频 6-思政文章 7-工具包
     *
     * @return
     */
    @FormUrlEncoded
    @POST("getUserHistoryList")
    Observable<HttpResult<List<HistoryModel.HistoryTestModel>>> getHistoryTest(@FieldMap Map<String, Object> params);

    /**
     * [获取历史测评结果]
     * rid 测评ID 如果传入测评ID则仅获取该测评的历史记录
     * type 1-测评 2-音频  3-视频  4-文章
     *
     * @return
     */
    @FormUrlEncoded
    @POST("testingResultHistory")
    Observable<HttpResult<List<HistoryModel.HistoryTestModel>>> getHistoryTestResult(@FieldMap Map<String, Object> params);

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

    /**
     * [收藏]
     * rid 测评ID 如果传入测评ID则仅获取该测评的历史记录
     * type 1-测评 2-音频  3-视频  4-文章
     *
     * @return
     */
    @FormUrlEncoded
    @POST("content_collect/list")
    Observable<HttpResult<List<CollectModel>>> getCollectList(@Field("type") int type, @Field("page") int page);


}
