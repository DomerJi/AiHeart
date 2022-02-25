package com.thfw.base.api;

import com.thfw.base.models.AboutUsModel;
import com.thfw.base.models.CommonProblemModel;
import com.thfw.base.models.HotCallModel;
import com.thfw.base.models.VoiceInstructionModel;
import com.thfw.base.net.HttpResult;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

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

}
