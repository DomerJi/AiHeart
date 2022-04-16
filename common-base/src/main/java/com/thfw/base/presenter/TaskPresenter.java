package com.thfw.base.presenter;

import com.thfw.base.api.TaskApi;
import com.thfw.base.base.IPresenter;
import com.thfw.base.base.UI;
import com.thfw.base.models.CommonModel;
import com.thfw.base.models.MsgCountModel;
import com.thfw.base.models.PushModel;
import com.thfw.base.models.SystemDetailModel;
import com.thfw.base.models.TaskDetailModel;
import com.thfw.base.models.TaskItemModel;
import com.thfw.base.models.TaskMusicEtcModel;
import com.thfw.base.net.CommonParameter;
import com.thfw.base.net.HttpResult;
import com.thfw.base.net.NetParams;
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
        // device_type 设备类型 区分 手机端和机器人消息不同的问题
        NetParams netParams = NetParams.crete().add("device_type", CommonParameter.getDeviceType())
                .add("msg_type", msgType)
                .add("page", page);
        if (msgType == 1) {
            Observable<HttpResult<List<TaskItemModel>>> observable = OkHttpUtil.createService(TaskApi.class)
                    .onGetPushMsgList(netParams);
            OkHttpUtil.request(observable, getUI());
        } else {
            Observable<HttpResult<List<PushModel>>> observable = OkHttpUtil.createService(TaskApi.class)
                    .onGetPushMsgList2(netParams);
            OkHttpUtil.request(observable, getUI());
        }


    }

    /**
     * 1-任务消息  2-系统消息
     *
     * @param id 消息id
     */
    public void onReadStated(int id) {
        Observable<HttpResult<CommonModel>> observable = OkHttpUtil.createService(TaskApi.class)
                .onReadStated(id);
        OkHttpUtil.request(observable, getUI());
    }


    /**
     * 一键已读
     *
     * @param type 0 全部 1-任务消息  2-系统消息
     */
    public void onReadStatedAll(int type) {
        Observable<HttpResult<CommonModel>> observable = OkHttpUtil.createService(TaskApi.class)
                .onReadStatedAll(type);
        OkHttpUtil.request(observable, getUI());
    }

    /**
     * 1-任务消息  2-系统消息
     *
     * @param msgId 友盟消息id
     */
    public void onReadStated(String msgId) {
        Observable<HttpResult<CommonModel>> observable = OkHttpUtil.createService(TaskApi.class)
                .onReadStated(msgId);
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

    /**
     * 任务音频详情
     *
     * @param type 1-任务消息 2-系统消息 0或不传为所有类型
     */
    public void onNewMsgCount(int type) {
        Observable<HttpResult<MsgCountModel>> observable = OkHttpUtil.createService(TaskApi.class)
                .onNewMsgCount(NetParams.crete().add("device_type", CommonParameter.getDeviceType())
                        .add("type", type));
        OkHttpUtil.request(observable, getUI());
    }

    /**
     * 推送消息详情详情
     *
     * @param netParams 推送友盟返回的msg_id字段 id
     */
    public void getPushModel(NetParams netParams) {
        Observable<HttpResult<SystemDetailModel>> observable = OkHttpUtil.createService(TaskApi.class)
                .getPushModel(netParams);
        OkHttpUtil.request(observable, getUI());
    }


    public interface TaskUi<T> extends UI<T> {

    }
}
