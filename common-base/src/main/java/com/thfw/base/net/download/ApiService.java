package com.thfw.base.net.download;


import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface ApiService {

    @Streaming
    @GET
    Observable<ResponseBody> download(@Url String url);
}