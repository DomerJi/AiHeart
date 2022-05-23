package com.thfw.base.api;

import com.thfw.base.models.CommonModel;
import com.thfw.base.models.OrganizationModel;
import com.thfw.base.models.OrganizationSelectedModel;
import com.thfw.base.net.HttpResult;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Author:pengs
 * Date: 2021/12/7 13:17
 * Describe:组织
 */
public interface OrganizationApi {

    /**
     * [获取组织层级列表]
     *
     * @return
     */
    @FormUrlEncoded
    @POST("organization_list")
    Observable<HttpResult<OrganizationModel>> onGetOrganizationList(@Field("id") String organizationId);

    /**
     * [获取组织层级列表]
     *
     * @return
     */
    @FormUrlEncoded
    @POST("get_organization_pwd")
    Observable<HttpResult<OrganizationSelectedModel>> onGetJoinedList(@FieldMap Map<String, Object> map);


    /**
     * [选择加入组织]
     *
     * @return
     */
    @FormUrlEncoded
    @POST("select_organization")
    Observable<HttpResult<CommonModel>> onSelectOrganization(@Field("id") String organizationId, @Field("device_type") String deviceType);

}
