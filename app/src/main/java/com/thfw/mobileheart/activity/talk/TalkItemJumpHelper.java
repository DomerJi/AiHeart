package com.thfw.mobileheart.activity.talk;

import android.content.Context;

import com.thfw.base.models.AudioEtcDetailModel;
import com.thfw.base.models.AudioEtcModel;
import com.thfw.base.models.ChatEntity;
import com.thfw.base.models.DialogTalkModel;
import com.thfw.base.utils.ToastUtil;
import com.thfw.mobileheart.activity.audio.AudioPlayerActivity;
import com.thfw.mobileheart.activity.read.BookDetailActivity;
import com.thfw.mobileheart.activity.read.BookIdeoDetailActivity;
import com.thfw.mobileheart.activity.test.TestBeginActivity;
import com.thfw.mobileheart.activity.video.VideoPlayActivity;

/**
 * Author:pengs
 * Date: 2022/1/14 17:11
 * Describe:Todo
 */
public class TalkItemJumpHelper {


    public static void onItemClick(Context mContext, int type, DialogTalkModel.RecommendInfoBean recommendInfoBean) {
        onItemClick(mContext, type, recommendInfoBean, FromType.AI_CHAT);
    }

    public static void onItemClick(Context mContext, int type, DialogTalkModel.RecommendInfoBean recommendInfoBean, int fromType) {

        switch (type) {
            case ChatEntity.TYPE_RECOMMEND_TEXT:
                if (recommendInfoBean.getId() < 1000000) {
                    // 心理文库
                    BookDetailActivity.startActivity(mContext, recommendInfoBean.getId());
                } else {
                    // 思政文库
                    BookIdeoDetailActivity.startActivity(mContext, recommendInfoBean.getId());
                }
                break;
            case ChatEntity.TYPE_RECOMMEND_VIDEO:
                VideoPlayActivity.startActivity(mContext, recommendInfoBean.getId(), true, fromType);
                break;
            case ChatEntity.TYPE_RECOMMEND_AUDIO:
                AudioEtcDetailModel.AudioItemModel audioItemModel = new AudioEtcDetailModel.AudioItemModel();
                audioItemModel.setId(recommendInfoBean.getId());
                audioItemModel.setMusicId(recommendInfoBean.getId());
                audioItemModel.setSfile(recommendInfoBean.getFile());
                audioItemModel.setImg(recommendInfoBean.getImg());
                audioItemModel.setTitle(recommendInfoBean.getTitle());
                audioItemModel.setAutoFinished(true);
                AudioPlayerActivity.startActivity(mContext, audioItemModel);
                break;
            case ChatEntity.TYPE_RECOMMEND_AUDIO_ETC:
                AudioEtcModel audioEtcModel = new AudioEtcModel();
                audioEtcModel.setTitle(recommendInfoBean.getTitle());
                audioEtcModel.setImg(recommendInfoBean.getImg());
                audioEtcModel.setId(recommendInfoBean.getId());
                audioEtcModel.setAutoFinished(true);
                AudioPlayerActivity.startActivity(mContext, audioEtcModel);
                break;
            case ChatEntity.TYPE_RECOMMEND_TEST:
                TestBeginActivity.startActivity(mContext, recommendInfoBean.getId());
                break;
            default:
                ToastUtil.show("未处理该类型跳转 ->" + type);
                break;
        }
    }

    public interface FromType {
        int TOOL = 1;
        int AI_CHAT = 2;
    }
}
