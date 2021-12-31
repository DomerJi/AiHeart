package com.thfw.base.api;

import com.thfw.base.models.TestDetailModel;
import com.thfw.base.models.TestModel;
import com.thfw.base.models.TestResultModel;
import com.thfw.base.net.HttpResult;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Author:pengs
 * Date: 2021/12/8 10:09
 * Describe:Todo
 */
public interface TestApi {

    /**
     * [获取测评列表]
     *
     * @return
     */
    @FormUrlEncoded
    @POST("testing/list")
    Observable<HttpResult<List<TestModel>>> onGetList(@Field("field") String field);

    /**
     * [获取测评详情]
     *
     * @return
     */
    @FormUrlEncoded
    @POST("testing/info")
    Observable<HttpResult<TestDetailModel>> onGetInfo(@Field("id") int id);

    /**
     * [获取测评详情]
     *
     * @return
     */
    @FormUrlEncoded
    @POST("testing/getResult")
    Observable<HttpResult<TestResultModel>> onSubmit(@Field("rid") int id,
                                                     @Field("opts") String opts,
                                                     @Field("spend_time") int spendTime);

    /**
     * [获取测评详情]
     *
     * @return
     */
    @FormUrlEncoded
    @POST("testing/getResult")
    Observable<HttpResult<TestResultModel>> onGetResult(@Field("result_id") int id);

}
