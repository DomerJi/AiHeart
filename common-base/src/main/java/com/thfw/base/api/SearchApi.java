package com.thfw.base.api;

import com.thfw.base.models.CommonModel;
import com.thfw.base.models.SearchResultModel;
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
 * Date: 2021/12/29 11:21
 * Describe:搜索
 */
public interface SearchApi {

    /**
     * 搜索历史
     *
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST("search/history")
    Observable<HttpResult<List<String>>> getHistory(@FieldMap Map<String, Object> params);

    /**
     * 清除历史
     *
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST("search/history_delete")
    Observable<HttpResult<CommonModel>> onDeleteHistory(@FieldMap Map<String, Object> params);

    /**
     * 搜索
     *
     * @param words
     * @return
     */
    @FormUrlEncoded
    @POST("search")
    Observable<HttpResult<SearchResultModel>> onSearch(@Field("words") String words);

}
