package com.thfw.robotheart.adapter;

import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.thfw.base.models.ChatEntity;
import com.thfw.base.models.DialogTalkModel;
import com.thfw.robotheart.R;
import com.thfw.ui.utils.GlideUtil;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ChatAdapter extends BaseAdapter<ChatEntity, ChatAdapter.ChatHolder> {


    public ChatAdapter(List<ChatEntity> dataList) {
        super(dataList);
    }

    @NonNull
    @NotNull
    @Override
    public ChatHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case ChatEntity.TYPE_FROM_SELECT:// 【对方】 对话 - 单选题
            case ChatEntity.TYPE_FROM_NORMAL:// 【对方】 对话 - 普通
            case ChatEntity.TYPE_INPUT:// 【对方】 对话 - 自由输入
                return new ChatFromHolder(inflate(R.layout.chat_from_to_layout, parent));
            case ChatEntity.TYPE_TO: // 【我】对话
                return new ChatToHolder(inflate(R.layout.chat_to_from_layout, parent));
            case ChatEntity.TYPE_RECOMMEND_TEST: // 测评
            case ChatEntity.TYPE_RECOMMEND_VIDEO: // 视频
            case ChatEntity.TYPE_RECOMMEND_TEXT: // 文章
            case ChatEntity.TYPE_RECOMMEND_AUDIO: // 音频
            case ChatEntity.TYPE_RECOMMEND_AUDIO_ETC: // 音频合集
                return new RecommendHolder(inflate(R.layout.chat_from_to_link_layout, parent));
            case ChatEntity.TYPE_TIME: // 时间
                return new ChatHolder(inflate(R.layout.chat_time_layout, parent));
            case ChatEntity.TYPE_END_SERVICE: // 您已结束本次服务
                return new ChatHolder(inflate(R.layout.chat_end_service_layout, parent));
            case ChatEntity.TYPE_FEEDBACK: // 您对本次回答满意吗？
                return new ChatHolder(inflate(R.layout.chat_freeback_layout, parent));
            default:
                return new ChatHolder(inflate(R.layout.chat_from_to_layout, parent));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ChatHolder holder, int position) {
        ChatEntity chatEntity = mDataList.get(position);

        switch (chatEntity.type) {
            case ChatEntity.TYPE_FROM_NORMAL:
            case ChatEntity.TYPE_FROM_SELECT:
            case ChatEntity.TYPE_INPUT:
                if (holder instanceof ChatFromHolder) {
                    ChatFromHolder chatFromHolder = (ChatFromHolder) holder;
                    chatFromHolder.mTvTalk.setText(Html.fromHtml(chatEntity.getTalk()));
                }
                break;
            case ChatEntity.TYPE_TO:
                if (holder instanceof ChatToHolder) {
                    ChatToHolder chatToHolder = (ChatToHolder) holder;
                    chatToHolder.mTvTalk.setText(Html.fromHtml(chatEntity.getTalk()));
                    if (position == getItemCount() - 1) {
                        chatToHolder.mPbToTalk.setVisibility(View.VISIBLE);
                    } else {
                        chatToHolder.mPbToTalk.setVisibility(View.GONE);
                    }
                }
                break;
            case ChatEntity.TYPE_RECOMMEND_TEST: // 测评
            case ChatEntity.TYPE_RECOMMEND_VIDEO: // 视频
            case ChatEntity.TYPE_RECOMMEND_TEXT: // 文章
            case ChatEntity.TYPE_RECOMMEND_AUDIO: // 音频
            case ChatEntity.TYPE_RECOMMEND_AUDIO_ETC:
                if (holder instanceof RecommendHolder) {
                    RecommendHolder recommendHolder = (RecommendHolder) holder;
                    DialogTalkModel.RecommendInfoBean infoBean = chatEntity.getTalkModel().getRecommendInfo();
                    GlideUtil.load(mContext, infoBean.getImg(), recommendHolder.mRivLinkBg);
                    recommendHolder.mTvTitle.setText(infoBean.getTitle());
                    recommendHolder.mTvType.setText(chatEntity.getRecommendType());
                }
                break;
            case ChatEntity.TYPE_TIME:
                if (holder instanceof TimeHolder) {
                    TimeHolder timeHolder = (TimeHolder) holder;
                    timeHolder.mTvTime.setText(chatEntity.getTalk());
                }
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mDataList.get(position).type;
    }

    public class ChatHolder extends RecyclerView.ViewHolder {
        public ChatHolder(@NonNull @NotNull View itemView) {
            super(itemView);
        }
    }

    public class ChatFromHolder extends ChatHolder {

        private final TextView mTvTalk;

        public ChatFromHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            mTvTalk = itemView.findViewById(R.id.tv_talk);
        }
    }

    public class ChatToHolder extends ChatHolder {

        private final TextView mTvTalk;
        private final ProgressBar mPbToTalk;

        public ChatToHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            mTvTalk = itemView.findViewById(R.id.tv_talk);
            mPbToTalk = itemView.findViewById(R.id.pb_to_talk);
        }
    }

    public class RecommendHolder extends ChatHolder {

        private RoundedImageView mRivLinkBg;
        private TextView mTvType;
        private TextView mTvTitle;

        public RecommendHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            mRivLinkBg = (RoundedImageView) itemView.findViewById(R.id.riv_link_bg);
            mTvType = (TextView) itemView.findViewById(R.id.tv_type);
            mTvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            itemView.setOnClickListener(v -> {
                if (mRecommendListener != null) {
                    int position = getBindingAdapterPosition();
                    mRecommendListener.onRecommend(mDataList.get(position).type, mDataList.get(position).getTalkModel().getRecommendInfo());
                }
            });

        }

    }

    public class TimeHolder extends ChatHolder {
        private TextView mTvTime;

        public TimeHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            mTvTime = itemView.findViewById(R.id.tv_time);
        }

    }

    OnRecommendListener mRecommendListener;

    public void setRecommendListener(OnRecommendListener recommendListener) {
        this.mRecommendListener = recommendListener;
    }

    public interface OnRecommendListener {
        void onRecommend(int type, DialogTalkModel.RecommendInfoBean recommendInfoBean);
    }
}