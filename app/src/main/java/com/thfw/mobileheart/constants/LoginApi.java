package com.thfw.mobileheart.constants;

import com.thfw.base.net.HttpResult;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

public interface LoginApi {

    @FormUrlEncoded
    @POST("url/postlist")
    Observable<HttpResult<Object>> onTestPost(@FieldMap Map<String, String> params);

    @GET("url/getlist")
    Observable<HttpResult<Object>> onTestGet(@QueryMap Map<String, String> params);

}
