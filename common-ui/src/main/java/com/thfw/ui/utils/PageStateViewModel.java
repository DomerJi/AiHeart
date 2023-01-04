package com.thfw.ui.utils;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.thfw.base.models.PageStateModel;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.OtherPresenter;
import com.thfw.base.utils.GsonUtil;
import com.thfw.base.utils.SharePreferenceUtil;
import com.trello.rxlifecycle2.LifecycleProvider;

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
