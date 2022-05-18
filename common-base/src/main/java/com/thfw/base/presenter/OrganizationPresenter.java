package com.thfw.base.presenter;

import android.text.TextUtils;

import com.thfw.base.api.OrganizationApi;
import com.thfw.base.base.IPresenter;
import com.thfw.base.base.UI;
import com.thfw.base.models.CommonModel;
import com.thfw.base.models.OrganizationModel;
import com.thfw.base.models.OrganizationSelectedModel;
import com.thfw.base.net.CommonParameter;
import com.thfw.base.net.HttpResult;
import com.thfw.base.net.NetParams;
import com.thfw.base.net.OkHttpUtil;

import io.reactivex.Observable;

/**
 * Author:pengs
 * Date: 2021/12/7 13:17
 * Describe:Todo
 */
public class OrganizationPresenter extends IPresenter<OrganizationPresenter.OrganizationUi> {


    public OrganizationPresenter(OrganizationUi ui) {
        super(ui);
    }

    public void onGetOrganizationList() {
        String organizationId = CommonParameter.getOrganizationId();
        Observable<HttpResult<OrganizationModel>> observable = OkHttpUtil.createService(OrganizationApi.class).onGetOrganizationList(organizationId);
        OkHttpUtil.request(observable, getUI());

    }


    public void onGetJoinedList() {
        String organizationId = CommonParameter.getOrganizationId();
        NetParams netParams = NetParams.crete();
        if (!TextUtils.isEmpty(organizationId)) {
            netParams.add("id", netParams);
        }
        Observable<HttpResult<OrganizationSelectedModel>> observable = OkHttpUtil.createService(OrganizationApi.class)
                .onGetJoinedList(netParams);
        OkHttpUtil.request(observable, getUI());

    }

    public void onSelectOrganization(String organizationId) {
        Observable<HttpResult<CommonModel>> observable = OkHttpUtil.createService(OrganizationApi.class)
                .onSelectOrganization(organizationId, CommonParameter.getDeviceType());
        OkHttpUtil.request(observable, getUI());
    }

    public interface OrganizationUi<T> extends UI<T> {

    }

}
