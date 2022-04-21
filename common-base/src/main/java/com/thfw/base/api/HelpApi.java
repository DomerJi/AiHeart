package com.thfw.base.api;

import com.thfw.base.models.FeedBackModel;
import com.thfw.base.net.HttpResult;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Author:pengs
 * Date: 2022/4/21 16:18
 * Describe:Todo
 */
public interface HelpApi {

    /**
     * 音频分类
     *
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST("feedback_list")
    Observable<HttpResult<List<FeedBackModel>>> getFeedBackList(@FieldMap Map<String, Object> params);

}
