package com.thfw.base.presenter;

import com.thfw.base.api.OtherApi;
import com.thfw.base.base.IPresenter;
import com.thfw.base.base.UI;
import com.thfw.base.models.HotCallModel;
import com.thfw.base.net.HttpResult;
import com.thfw.base.net.NetParams;
import com.thfw.base.net.OkHttpUtil;

import java.util.List;

import io.reactivex.Observable;

/**
 * Author:pengs
 * Date: 2022/1/17 10:52
 * Describe:Todo
 */
public class OtherPresenter extends IPresenter<OtherPresenter.OtherUi> {

    public OtherPresenter(OtherPresenter.OtherUi ui) {
        super(ui);
    }


    public void onGetHotPhoneList() {
        Observable<HttpResult<List<HotCallModel>>> observable = OkHttpUtil.createService(OtherApi.class)
                .getHotPhoneList(NetParams.crete());
        OkHttpUtil.request(observable, getUI());
    }


    public interface OtherUi<T> extends UI<T> {

    }
}
