package com.thfw.base.presenter;

import com.thfw.base.api.SearchApi;
import com.thfw.base.base.IPresenter;
import com.thfw.base.base.UI;
import com.thfw.base.models.CommonModel;
import com.thfw.base.models.SearchHistoryModel;
import com.thfw.base.models.SearchResultModel;
import com.thfw.base.net.HttpResult;
import com.thfw.base.net.NetParams;
import com.thfw.base.net.OkHttpUtil;

import java.util.List;

import io.reactivex.Observable;

/**
 * Author:pengs
 * Date: 2021/12/29 11:25
 * Describe:Todo
 */
public class SearchPresenter extends IPresenter<SearchPresenter.SearchUi> {

    public SearchPresenter(SearchUi ui) {
        super(ui);
    }

    public void getHistory() {
        Observable<HttpResult<List<String>>> observable = OkHttpUtil.createService(SearchApi.class)
                .getHistory(NetParams.crete());
        OkHttpUtil.request(observable, getUI());
    }

    public void onDeleteHistory() {
        Observable<HttpResult<CommonModel>> observable = OkHttpUtil.createService(SearchApi.class)
                .onDeleteHistory(NetParams.crete());
        OkHttpUtil.request(observable, getUI());
    }

    public void onSearch(String words) {
        Observable<HttpResult<SearchResultModel>> observable = OkHttpUtil.createService(SearchApi.class)
                .onSearch(words);
        OkHttpUtil.request(observable, getUI());
    }

    public interface SearchUi<T> extends UI<T> {

    }
}
