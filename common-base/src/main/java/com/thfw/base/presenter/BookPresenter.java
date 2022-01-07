package com.thfw.base.presenter;

import com.thfw.base.api.BookApi;
import com.thfw.base.base.IPresenter;
import com.thfw.base.base.UI;
import com.thfw.base.models.BookDetailModel;
import com.thfw.base.models.BookIdeoDetailModel;
import com.thfw.base.models.BookItemModel;
import com.thfw.base.models.BookStudyItemModel;
import com.thfw.base.models.BookStudyTypeModel;
import com.thfw.base.models.BookTypeModel;
import com.thfw.base.net.HttpResult;
import com.thfw.base.net.NetParams;
import com.thfw.base.net.OkHttpUtil;

import java.util.List;

import io.reactivex.Observable;

/**
 * Author:pengs
 * Date: 2021/12/14 9:42
 * Describe:Todo
 */
public class BookPresenter extends IPresenter<BookPresenter.BookUi> {


    public BookPresenter(BookUi ui) {
        super(ui);
    }

    public void getArticleType() {
        Observable<HttpResult<BookTypeModel>> observable = OkHttpUtil.createService(BookApi.class)
                .getArticleType(NetParams.crete().add("type", 1));
        OkHttpUtil.request(observable, getUI());
    }

    public void getIdeologyArticleType() {
        Observable<HttpResult<List<BookStudyTypeModel>>> observable = OkHttpUtil.createService(BookApi.class)
                .getIdeologyArticleType(NetParams.crete().add("type", 2));
        OkHttpUtil.request(observable, getUI());
    }

    public void getArticleList(int type, int page) {
        Observable<HttpResult<List<BookItemModel>>> observable = OkHttpUtil.createService(BookApi.class)
                .getArticleList(type, page);
        OkHttpUtil.request(observable, getUI());
    }

    public void getIdeologyArticleList(int type, int page) {
        Observable<HttpResult<List<BookStudyItemModel>>> observable = OkHttpUtil.createService(BookApi.class)
                .getIdeologyArticleList(type, page);
        OkHttpUtil.request(observable, getUI());
    }

    public void getArticleInfo(int id) {
        Observable<HttpResult<BookDetailModel>> observable = OkHttpUtil.createService(BookApi.class)
                .getArticleInfo(id);
        OkHttpUtil.request(observable, getUI());
    }

    public void getIdeologyArticleInfo(int id) {
        Observable<HttpResult<BookIdeoDetailModel>> observable = OkHttpUtil.createService(BookApi.class)
                .getIdeologyArticleInfo(id);
        OkHttpUtil.request(observable, getUI());
    }

    public interface BookUi<T> extends UI<T> {

    }
}
