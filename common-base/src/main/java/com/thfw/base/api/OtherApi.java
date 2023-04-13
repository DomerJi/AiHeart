package com.thfw.base.api;

import com.thfw.base.models.AboutUsModel;
import com.thfw.base.models.CommonProblemModel;
import com.thfw.base.models.HotCallModel;
import com.thfw.base.models.PageStateModel;
import com.thfw.base.models.VersionModel;
import com.thfw.base.models.VoiceInstructionModel;
import com.thfw.base.net.HttpResult;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

/**
 * Author:pengs
 * Date: 2022/2/21 15:25
 * Describe:热线电话/常见问题
 */
public interface OtherApi {

    /**
     * 【热线电话】
     *
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST("commonweal")
    Observable<HttpResult<List<HotCallModel>>> getHotPhoneList(@FieldMap Map<String, Object> params);


    /**
     * 【常见问题】
     *
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST("question_answer")
    Observable<HttpResult<List<CommonProblemModel>>> getQuestionAnswer(@FieldMap Map<String, Object> params);


    /**
     * 【语音指令】
     *
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST("voice_instruction")
    Observable<HttpResult<List<VoiceInstructionModel>>> getVoiceInstruction(@FieldMap Map<String, Object> params);


    /**
     * 【关于我们】
     *
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST("about_us")
    Observable<HttpResult<AboutUsModel>> getAboutUs(@FieldMap Map<String, Object> params);


    /**
     * 【检查版本】
     *
     * “https://cos.pgyer.com/7bb7d47f86ad8d258db85abd00b5c928.apk?sign=9f994f27c7bfe29b4aa9d713088fe54e&t=1668755455&response-content-disposition=attachment%3Bfilename%3DAI%E5%92%A8%E8%AF%A2%E5%B8%88_2.0.0.apk”
     * type phone
     *
     * size
     * version
     * des
     * update_time
     * download_url
     */
    @FormUrlEncoded
    @POST("version/latest")
    Observable<HttpResult<VersionModel>> getVersion(@FieldMap Map<String, Object> params);


    /**
     * 【页面状态】 红旗角标、黑白悼念
     *
     * “https://cos.pgyer.com/7bb7d47f86ad8d258db85abd00b5c928.apk?sign=9f994f27c7bfe29b4aa9d713088fe54e&t=1668755455&response-content-disposition=attachment%3Bfilename%3DAI%E5%92%A8%E8%AF%A2%E5%B8%88_2.0.0.apk”
     * type phone
     *
     * size
     * version
     * des
     * update_time
     * download_url
     */
    @GET("app/configure")
    Observable<HttpResult<PageStateModel>> pageState(@QueryMap Map<String, Object> params);


}
