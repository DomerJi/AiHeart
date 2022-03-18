package com.thfw.mobileheart.push.helper;

import android.content.Context;

import com.thfw.base.base.MsgType;
import com.thfw.base.models.AudioEtcModel;
import com.thfw.base.models.PushModel;
import com.thfw.mobileheart.R;
import com.thfw.mobileheart.activity.WebActivity;
import com.thfw.mobileheart.activity.audio.AudioPlayerActivity;
import com.thfw.mobileheart.activity.read.BookDetailActivity;
import com.thfw.mobileheart.activity.read.BookIdeoDetailActivity;
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
                break;
            case MsgType.TESTING:
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
                break;
            case MsgType.H5:
                WebActivity.startActivity(mContext, pushModel.getTurnPage(), pushModel.getTitle());
                break;
            case MsgType.SYSTEM:
                break;
            case MsgType.COMMON_PROBLEM:
                break;
            case MsgType.VOICE_COMMAND:
                break;
            case MsgType.ABOUT_US:
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
                return R.mipmap.ic_launcher;
            case 2:
                return R.mipmap.ic_launcher;
            default:
                return R.mipmap.ic_launcher;
        }
    }

    public static final Object getIcon(int type) {
        Object icon = null;
        switch (type) {
            case MsgType.TASK:
                icon = R.mipmap.ic_launcher;
                break;
            case MsgType.TESTING:
                icon = R.mipmap.ic_launcher;
                break;
            case MsgType.TOOL_PACKAGE:
                icon = R.mipmap.ic_launcher;
                break;
            case MsgType.TOPIC_DIALOG:
                icon = R.mipmap.ic_launcher;
                break;
            case MsgType.MUSIC:
                icon = R.mipmap.ic_launcher;
                break;
            case MsgType.VIDEO:
                icon = R.mipmap.ic_launcher;
                break;
            case MsgType.H5:
                icon = R.mipmap.ic_launcher;
                break;
            case MsgType.SYSTEM:
                icon = R.mipmap.ic_launcher;
                break;
            case MsgType.COMMON_PROBLEM:
                icon = R.mipmap.ic_launcher;
                break;
            case MsgType.VOICE_COMMAND:
                icon = R.mipmap.ic_launcher;
                break;
            case MsgType.ABOUT_US:
                icon = R.mipmap.ic_launcher;
                break;
            case MsgType.BOOK:
                icon = R.mipmap.ic_launcher;
                break;
            case MsgType.IDEO_BOOK:
                icon = R.mipmap.ic_launcher;
                break;
            default:
                icon = R.mipmap.ic_launcher;
                break;
        }
        return icon;
    }


}
