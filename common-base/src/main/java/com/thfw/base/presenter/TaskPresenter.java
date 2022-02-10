package com.thfw.base.presenter;

import com.thfw.base.api.TaskApi;
import com.thfw.base.base.IPresenter;
import com.thfw.base.base.UI;
import com.thfw.base.models.TaskItemModel;
import com.thfw.base.net.HttpResult;
import com.thfw.base.net.OkHttpUtil;

import java.util.List;

import io.reactivex.Observable;

/**
 * Author:pengs
 * Date: 2022/2/10 15:02
 * Describe:任务
 */
public class TaskPresenter extends IPresenter<TaskPresenter.TaskUi> {


    public TaskPresenter(TaskUi ui) {
        super(ui);
    }

    /**
     * 0-未完成任务列表  1-完成任务列表
     *
     * @param status
     */
    public void onGetList(int status,int page) {
        Observable<HttpResult<List<TaskItemModel>>> observable = OkHttpUtil.createService(TaskApi.class)
                .onGetList(status,page);
        OkHttpUtil.request(observable, getUI());
    }

    public interface TaskUi<T> extends UI<T> {

    }
}
