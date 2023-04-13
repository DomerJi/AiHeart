package com.thfw.ui.utils;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.thfw.base.models.PageStateModel;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.OtherPresenter;
import com.thfw.base.utils.GsonUtil;
import com.thfw.base.utils.SharePreferenceUtil;
import com.trello.rxlifecycle2.LifecycleProvider;

import java.util.Random;

/**
 * Author:pengs
 * Date: 2022/12/1 15:34
 * Describe:Todo
 */
public class PageStateViewModel extends ViewModel {

    private MutableLiveData<PageStateModel> pageStateLive;
    private static final String KEY_STATE = "key.state01";

    public MutableLiveData<PageStateModel> getPageStateLive() {
        if (pageStateLive == null) {
            pageStateLive = new MutableLiveData<>();
            check();
        }
        return pageStateLive;
    }


    public void check() {
        if (getPageStateLive().getValue() == null) {
            PageStateModel pageStateModel = SharePreferenceUtil.getObject(KEY_STATE, PageStateModel.class);
            if (pageStateModel != null) {

                getPageStateLive().setValue(pageStateModel);
            } else {
                getPageStateLive().setValue(new PageStateModel());
            }
        }

        new OtherPresenter(new OtherPresenter.OtherUi<PageStateModel>() {
            @Override
            public LifecycleProvider getLifecycleProvider() {
                return null;
            }

            @Override
            public void onSuccess(PageStateModel data) {
                if (data != null) {
//                    data.blackColor = new Random().nextBoolean();
//                    data.redStar = new Random().nextBoolean();
//
                    if (data.holidays != null) {
                        boolean isset = new Random().nextBoolean();
                        String time = isset ? "2023-04-13" : "";
                        String endtime = isset ? "2023-04-14" : "";
                        data.holidays.start = time;
                        data.holidays.end = endtime;
                    }
                    SharePreferenceUtil.setString(KEY_STATE, GsonUtil.toJson(data));
                    if (getPageStateLive().getValue() == null || !getPageStateLive().getValue().equals(data)) {
                        getPageStateLive().setValue(data);
                    }
                }
            }

            @Override
            public void onFail(ResponeThrowable throwable) {
            }
        }).onGetPageState();
    }

}
