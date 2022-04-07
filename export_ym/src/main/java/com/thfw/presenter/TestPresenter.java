package com.thfw.presenter;


import com.thfw.api.TestApi;
import com.thfw.base.IPresenter;
import com.thfw.base.UI;
import com.thfw.models.HttpResult;
import com.thfw.models.ReportTestModel;
import com.thfw.models.TestDetailModel;
import com.thfw.models.TestModel;
import com.thfw.models.TestResultModel;
import com.thfw.net.NetParams;
import com.thfw.net.OkHttpUtil;

import java.util.List;

import io.reactivex.Observable;

/**
 * Author:pengs
 * Date: 2021/12/8 10:15
 * Describe:Todo
 */
public class TestPresenter extends IPresenter<TestPresenter.TestUi> {


    public TestPresenter(TestUi ui) {
        super(ui);
    }

    public void onGetList() {
        Observable<HttpResult<List<TestModel>>> observable = OkHttpUtil.createService(TestApi.class).onGetList("");
        OkHttpUtil.request(observable, getUI());
    }

    public void onGetInfo(int id) {
        Observable<HttpResult<TestDetailModel>> observable = OkHttpUtil.createService(TestApi.class).onGetInfo(id);
        OkHttpUtil.request(observable, getUI());
    }

    public void onSubmit(int id, String opts, int spendTime) {
        Observable<HttpResult<TestResultModel>> observable = OkHttpUtil.createService(TestApi.class).onSubmit(id, opts, spendTime);
        OkHttpUtil.request(observable, getUI());
    }

    public void onGetResult(int id) {
        Observable<HttpResult<TestResultModel>> observable = OkHttpUtil.createService(TestApi.class).onGetResult(id);
        OkHttpUtil.request(observable, getUI());
    }

    public void onResultHistory(int id, int page) {
        NetParams netParams = NetParams.crete().add("page", page);
        if (id != -1) {
            netParams.add("rid", id);
        }
        Observable<HttpResult<List<ReportTestModel>>> observable = OkHttpUtil.createService(TestApi.class).onResultHistory(netParams);
        OkHttpUtil.request(observable, getUI());
    }


    public interface TestUi<T> extends UI<T> {

    }
}