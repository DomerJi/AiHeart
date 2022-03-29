package com.thfw.mobileheart.push.helper;

import android.content.Context;
import android.content.Intent;

import com.thfw.base.base.MsgType;
import com.thfw.base.models.AudioEtcModel;
import com.thfw.base.models.PushModel;
import com.thfw.mobileheart.R;
import com.thfw.mobileheart.activity.WebActivity;
import com.thfw.mobileheart.activity.audio.AudioPlayerActivity;
import com.thfw.mobileheart.activity.task.MeTaskActivity;
import com.thfw.mobileheart.activity.task.SystemDetailActivity;
import com.thfw.mobileheart.activity.read.BookDetailActivity;
import com.thfw.mobileheart.activity.read.BookIdeoDetailActivity;
import com.thfw.mobileheart.activity.settings.HelpBackActivity;
import com.thfw.mobileheart.activity.test.TestBeginActivity;
import com.thfw.mobileheart.activity.video.VideoPlayActivity;
import com.thfw.mobileheart.constants.AgreeOn;
import com.thfw.mobileheart.util.MsgCountManager;

/**
 * Author:pengs
 * Date: 2022/3/8 18:04
 * Describe:Todo
 */
public class PushHandle {

    public static final void handleMessage(Context mContext, PushModel pushModel) {
        int msgType = pushModel.getMsgType();
        int contentId = pushModel.getContentId();
        String longTextId = null;
        if (pushModel.getReadStatus() == 0) {
            if (pushModel.getId() > 0) {
                MsgCountManager.getInstance().readMsg(msgType, pushModel.getId());
            } else {
                longTextId = String.valueOf(pushModel.getMsgId());
                MsgCountManager.getInstance().readMsg(msgType, pushModel.getMsgId());
            }
        }
        switch (msgType) {
            case MsgType.TASK:
                mContext.startActivity(new Intent(mContext, MeTaskActivity.class));
                break;
            case MsgType.TESTING:
                TestBeginActivity.startActivity(mContext, contentId);
                break;
            case MsgType.TOOL_PACKAGE:
                break;
            case MsgType.TOPIC_DIALOG:
                break;
            case MsgType.MUSIC:
                AudioEtcModel audioEtcModel = new AudioEtcModel();
                audioEtcModel.setId(contentId);
                AudioPlayerActivity.startActivity(mContext, audioEtcModel);
                break;
            case MsgType.VIDEO:
                VideoPlayActivity.startActivity(mContext, contentId, false);
                break;
            case MsgType.H5:
                WebActivity.startActivity(mContext, pushModel.getTurnPage(), pushModel.getTitle());
                break;
            case MsgType.SYSTEM:
                if (pushModel.getId() > 0) {
                    SystemDetailActivity.startActivity(mContext, pushModel.getId());
                } else {
                    SystemDetailActivity.startActivity(mContext, pushModel.getMsgId());
                }
                break;
            case MsgType.COMMON_PROBLEM:
                HelpBackActivity.startActivity(mContext);
                break;
            case MsgType.MOOD:
                // todo 心情签到
                break;
            case MsgType.VOICE_COMMAND:
                break;
            case MsgType.ABOUT_US:
                WebActivity.startActivity(mContext, AgreeOn.AGREE_ABOUT);
                break;
            case MsgType.BOOK:
                BookDetailActivity.startActivity(mContext, contentId);
                break;
            case MsgType.IDEO_BOOK:
                BookIdeoDetailActivity.startActivity(mContext, contentId);
                break;
        }

    }

    public static final Object getTaskIcon(int taskType) {
        /**
         * 1-测评 2-音频 3-话术
         */
        switch (taskType) {
            case 1:
                return R.mipmap.ic_msg_type_test;
            case 2:
                return R.mipmap.ic_msg_type_audio;
            default:
                return R.mipmap.ic_msg_type_talk;
        }
    }

    public static final Object getIcon(int type) {
        Object icon = null;
        switch (type) {
            case MsgType.TASK:
                icon = R.mipmap.ic_launcher;
                break;
            case MsgType.TESTING:
                icon = R.mipmap.ic_msg_type_test;
                break;
            case MsgType.TOOL_PACKAGE:
                icon = R.mipmap.ic_msg_type_tool;
                break;
            case MsgType.TOPIC_DIALOG:
                icon = R.mipmap.ic_msg_type_talk;
                break;
            case MsgType.MUSIC:
                icon = R.mipmap.ic_msg_type_audio;
                break;
            case MsgType.VIDEO:
                icon = R.mipmap.ic_msg_type_video;
                break;
            case MsgType.H5:
                icon = R.drawable.ic_msg_type_link;
                break;
            case MsgType.SYSTEM:
                icon = R.mipmap.ic_msg_type_system;
                break;
            case MsgType.COMMON_PROBLEM:
                icon = R.mipmap.ic_msg_type_problem;
                break;
            case MsgType.VOICE_COMMAND:
                icon = R.mipmap.ic_msg_type_voice;
                break;
            case MsgType.ABOUT_US:
                icon = R.mipmap.ic_msg_type_abut;
                break;
            case MsgType.BOOK:
                icon = R.mipmap.ic_msg_type_book;
                break;
            case MsgType.IDEO_BOOK:
                icon = R.mipmap.ic_msg_type_ideo;
                break;
            default:
                icon = R.mipmap.ic_msg_type_ideo;
                break;
        }
        return icon;
    }


}
