package com.thfw.base.api;

import com.thfw.base.models.BookDetailModel;
import com.thfw.base.models.BookItemModel;
import com.thfw.base.models.BookTypeModel;
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
 * Date: 2021/12/14 9:37
 * Describe:文章接口
 */
public interface BookApi {

    /**
     * 文章分类
     *
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST("article/type")
    Observable<HttpResult<BookTypeModel>> getArticleType(@FieldMap Map<String, Object> params);

    /**
     * 文章列表
     *
     * @param type
     * @return
     */
    @FormUrlEncoded
    @POST("article/list")
    Observable<HttpResult<List<BookItemModel>>> getArticleList(@Field("type") int type, @Field("page") int page);

    /**
     * 文章详情
     *
     * @param id
     * @return
     */
    @FormUrlEncoded
    @POST("article/info")
    Observable<HttpResult<BookDetailModel>> getArticleInfo(@Field("id") int id);

}
