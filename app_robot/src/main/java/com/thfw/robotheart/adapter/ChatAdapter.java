package com.thfw.robotheart.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.opensource.svgaplayer.SVGAImageView;
import com.thfw.base.models.ChatEntity;
import com.thfw.base.models.DialogTalkModel;
import com.thfw.base.utils.HourUtil;
import com.thfw.robotheart.R;
import com.thfw.robotheart.constants.AnimFileName;
import com.thfw.robotheart.util.SVGAHelper;
import com.thfw.ui.utils.GlideUtil;
import com.thfw.user.login.UserManager;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ChatAdapter extends BaseAdapter<ChatEntity, ChatAdapter.ChatHolder> {


    private final Object visibleAvatar;
    OnRecommendListener mRecommendListener;
    onSendStateChangeListener mOnSendStateChangeListener;

    public ChatAdapter(List<ChatEntity> dataList) {
        super(dataList);
        visibleAvatar = UserManager.getInstance().getUser().getVisibleAvatar();
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
            case ChatEntity.TYPE_EMOJI:
                return new ChatFromEmojiHolder(inflate(R.layout.chat_from_to_emoji_layout, parent));
            case ChatEntity.TYPE_TO: // 【我】对话
                return new ChatToHolder(inflate(R.layout.chat_to_from_layout, parent));
            case ChatEntity.TYPE_RECOMMEND_TEST: // 测评
            case ChatEntity.TYPE_RECOMMEND_VIDEO: // 视频
            case ChatEntity.TYPE_RECOMMEND_TEXT: // 文章
            case ChatEntity.TYPE_RECOMMEND_AUDIO: // 音频
            case ChatEntity.TYPE_RECOMMEND_AUDIO_ETC: // 音频合集
                return new RecommendHolder(inflate(R.layout.chat_from_to_link_layout, parent));
            case ChatEntity.TYPE_TIME: // 时间
                return new TimeHolder(inflate(R.layout.chat_time_layout, parent));
            case ChatEntity.TYPE_HINT: // 提醒已到最前时间 或 *******
                return new HintHolder(inflate(R.layout.chat_hint_layout, parent));
            case ChatEntity.TYPE_JOIN_PAGE: // 提醒加入某个页面
                return new JoinPageHolder(inflate(R.layout.chat_change_page_layout, parent));
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
                    chatFromHolder.mTvTalk.setText(HtmlCompat.fromHtml(chatEntity.getNotPTalk(), HtmlCompat.FROM_HTML_MODE_LEGACY));
                }
                break;
            case ChatEntity.TYPE_EMOJI:
                if (holder instanceof ChatFromEmojiHolder) {
                    ChatFromEmojiHolder chatFromEmojiHolder = (ChatFromEmojiHolder) holder;
                    String fileName = AnimFileName.getTalkEmojiBySentiment(chatEntity.getTalk());
                    SVGAHelper.playSVGA(chatFromEmojiHolder.svgaImageView, SVGAHelper.SVGAModel
                            .create(fileName), null);
                }
                break;
            case ChatEntity.TYPE_TO:
                if (holder instanceof ChatToHolder) {
                    ChatToHolder chatToHolder = (ChatToHolder) holder;
                    chatToHolder.mTvTalk.setText(HtmlCompat.fromHtml(chatEntity.getTalk(), HtmlCompat.FROM_HTML_MODE_LEGACY));
                    if (position == getItemCount() - 1) {
                        if (chatEntity.loading == -1) {
                            chatToHolder.mPbToTalk.setVisibility(View.GONE);
                            chatToHolder.mIvSendError.setVisibility(View.VISIBLE);
                        } else {
                            chatToHolder.mPbToTalk.setVisibility(View.VISIBLE);
                            chatToHolder.mIvSendError.setVisibility(View.GONE);
                        }
                    } else {
                        chatToHolder.mPbToTalk.setVisibility(View.GONE);
                        chatToHolder.mIvSendError.setVisibility(View.GONE);
                    }
                    GlideUtil.load(mContext, visibleAvatar, chatToHolder.mRivToAvatar);
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
                    timeHolder.mTvTime.setText(HourUtil.getYYMMDD_HHMMSS(chatEntity.time));
                }
                break;
            case ChatEntity.TYPE_HINT:
                if (holder instanceof HintHolder) {
                    HintHolder hintHolder = (HintHolder) holder;
                    hintHolder.mTvHint.setText(chatEntity.getTalk());
                }
                break;
            case ChatEntity.TYPE_JOIN_PAGE:
                if (holder instanceof JoinPageHolder) {
                    JoinPageHolder hintHolder = (JoinPageHolder) holder;
                    hintHolder.mTvHint.setText(chatEntity.getTalk());
                }
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mDataList.get(position).type;
    }

    public void setRecommendListener(OnRecommendListener recommendListener) {
        this.mRecommendListener = recommendListener;
    }

    public void setOnSendStateChangeListener(ChatAdapter.onSendStateChangeListener onSendStateChangeListener) {
        this.mOnSendStateChangeListener = onSendStateChangeListener;
    }

    public interface OnRecommendListener {
        void onRecommend(int type, DialogTalkModel.RecommendInfoBean recommendInfoBean);
    }

    public interface onSendStateChangeListener {
        void onErrorResend();
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

    public class ChatFromEmojiHolder extends ChatHolder {

        private final SVGAImageView svgaImageView;

        public ChatFromEmojiHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            svgaImageView = itemView.findViewById(R.id.mSVGAImageView);
        }
    }

    public class ChatToHolder extends ChatHolder {

        private final TextView mTvTalk;
        private final ProgressBar mPbToTalk;
        private final ImageView mRivToAvatar;
        private final ImageView mIvSendError;

        public ChatToHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            mRivToAvatar = itemView.findViewById(R.id.riv_to_avatar);
            mTvTalk = itemView.findViewById(R.id.tv_talk);
            mPbToTalk = itemView.findViewById(R.id.pb_to_talk);
            mIvSendError = itemView.findViewById(R.id.iv_send_error);
            mIvSendError.setOnClickListener(v -> {
                if (mOnSendStateChangeListener != null) {
                    mOnSendStateChangeListener.onErrorResend();
                }
            });
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

    public class HintHolder extends ChatHolder {
        private TextView mTvHint;

        public HintHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            mTvHint = itemView.findViewById(R.id.tv_hint);
        }

    }

    public class JoinPageHolder extends ChatHolder {
        private TextView mTvHint;

        public JoinPageHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            mTvHint = itemView.findViewById(R.id.tv_hint);
        }

    }
}
