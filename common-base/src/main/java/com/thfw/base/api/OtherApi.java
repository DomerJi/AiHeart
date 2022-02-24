package com.thfw.base.api;

import com.thfw.base.models.HotCallModel;
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
     * type 1-获取心理文章分类     2-获取思政文章分类
     *
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST("commonweal")
    Observable<HttpResult<List<HotCallModel>>> getHotPhoneList(@FieldMap Map<String, Object> params);


}
