package com.thfw.base.api;

import com.thfw.base.models.CommonModel;
import com.thfw.base.models.HomeEntity;
import com.thfw.base.models.MobileRecommendModel;
import com.thfw.base.models.MoodLivelyModel;
import com.thfw.base.models.MoodModel;
import com.thfw.base.net.HttpResult;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Author:pengs
 * Date: 2021/11/29 18:00
 * Describe:Todo
 */
public interface MobileApi {


    /**
     * 手机端-首页-小密推荐
     *
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST("home_recommend")
    Observable<HttpResult<List<MobileRecommendModel>>> getMobileRecommend(@FieldMap Map<String, Object> params);


    /**
     * 手机端-心情日记-用户心情记录
     *
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST("user_mood_save")
    Observable<HttpResult<CommonModel>> onSavedMood(@FieldMap Map<String, Object> params);

    /**
     * 手机端-心情日记-用户心情历史列表
     *
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST("user_mood_list")
    Observable<HttpResult<List<MoodModel>>> getHistoryMoodList(@FieldMap Map<String, Object> params);

    /**
     * 手机端-【心情】与【活跃】详情
     *
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST("user_mood_list")
    Observable<HttpResult<MoodLivelyModel>> getMoodLivelyDetail(@FieldMap Map<String, Object> params);

    /**
     * 手机端-【心情】与【活跃】详情
     *
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST("user_mood_list")
    Observable<HttpResult<List<HomeEntity.BannerModel>>> getHomeBanner(@FieldMap Map<String, Object> params);


    /**
     * 手机端-心情日记-情绪列表
     *
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST("daily_mood_list")
    Observable<HttpResult<List<MoodModel>>> getMoodList(@FieldMap Map<String, Object> params);


    /**
     * 手机端-心情日记-情绪列表
     *
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST("active_time_save")
    Observable<HttpResult<CommonModel>> onSaveActiveTime(@FieldMap Map<String, Object> params);


}
