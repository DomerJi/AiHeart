package com.thfw.base.presenter;

import com.thfw.base.api.TaskApi;
import com.thfw.base.base.IPresenter;
import com.thfw.base.base.UI;
import com.thfw.base.models.CommonModel;
import com.thfw.base.models.PushModel;
import com.thfw.base.models.TaskDetailModel;
import com.thfw.base.models.TaskItemModel;
import com.thfw.base.models.TaskMusicEtcModel;
import com.thfw.base.net.HttpResult;
import com.thfw.base.net.OkHttpUtil;

import java.util.List;

import io.reactivex.Observable;

/**
 * Author:pengs
 * Date: 2022/2/10 15:02
 * Describe:任务
 */
public class TaskPresenter<T> extends IPresenter<TaskPresenter.TaskUi> {


    public TaskPresenter(TaskUi ui) {
        super(ui);
    }

    /**
     * 0-未完成任务列表  1-完成任务列表
     *
     * @param status
     */
    public void onGetList(int status, int page) {
        Observable<HttpResult<List<TaskItemModel>>> observable = OkHttpUtil.createService(TaskApi.class)
                .onGetList(status, page);
        OkHttpUtil.request(observable, getUI());
    }

    /**
     * 1-任务消息  2-系统消息
     *
     * @param msgType
     */
    public void onGetMsgList(int msgType, int page) {
        Observable<HttpResult<List<PushModel>>> observable = OkHttpUtil.createService(TaskApi.class)
                .onGetPushMsgList(msgType, page);
        OkHttpUtil.request(observable, getUI());
    }

    /**
     * 获取任务详情
     *
     * @param id 任务id
     */
    public void onGetInfo(int id) {
        Observable<HttpResult<TaskDetailModel>> observable = OkHttpUtil.createService(TaskApi.class)
                .onGetInfo(id);
        OkHttpUtil.request(observable, getUI());
    }

    /**
     * 音乐完成回执
     *
     * @param id 任务id
     */
    public void onFinishMusic(int id, int collectionId) {
        Observable<HttpResult<CommonModel>> observable = OkHttpUtil.createService(TaskApi.class)
                .onFinishMusic(id, collectionId);
        OkHttpUtil.request(observable, getUI());
    }


    /**
     * 任务音频详情
     *
     * @param id
     */
    public void onMusicInfo(int id) {
        Observable<HttpResult<TaskMusicEtcModel>> observable = OkHttpUtil.createService(TaskApi.class)
                .onMusicEtcInfo(id);
        OkHttpUtil.request(observable, getUI());
    }


    public interface TaskUi<T> extends UI<T> {

    }
}
