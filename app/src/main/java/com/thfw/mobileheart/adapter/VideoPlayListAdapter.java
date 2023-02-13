package com.thfw.mobileheart.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.thfw.base.api.HistoryApi;
import com.thfw.base.face.OnSpeakTextListener;
import com.thfw.base.models.CommonModel;
import com.thfw.base.models.VideoPlayListModel;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.HistoryPresenter;
import com.thfw.base.utils.ToastUtil;
import com.thfw.mobileheart.R;
import com.thfw.mobileheart.activity.video.VideoPlayActivity;
import com.thfw.mobileheart.constants.UIConfig;
import com.thfw.ui.utils.GlideUtil;
import com.trello.rxlifecycle2.LifecycleProvider;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Author:pengs
 * Date: 2021/10/14 11:44
 * Describe:Todo
 */
public class VideoPlayListAdapter extends BaseAdapter<VideoPlayListModel, RecyclerView.ViewHolder> {


    private ImageView mIvCollectTop;
    private boolean requestIng;
    private int mVideoId;
    private VideoDetailsHolder holder;

    private int playPosition;

    public VideoPlayListAdapter(List<VideoPlayListModel> dataList) {
        super(dataList);
    }

    public void setPlayPosition(int playPosition) {
        this.playPosition = playPosition;
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VideoPlayListModel.TYPE_TOP:
                return new VideoTopHolder(LayoutInflater.from(mContext).inflate(R.layout.item_video_details_head_layout, parent, false));
            case VideoPlayListModel.TYPE_GROUP:
                return new VideoGroupHolder(LayoutInflater.from(mContext).inflate(R.layout.item_video_details_tabtitle_layout, parent, false));
            default:
                return new VideoDetailsHolder(LayoutInflater.from(mContext).inflate(R.layout.item_video_details_layout, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        VideoPlayListModel model = mDataList.get(position);
        if (holder instanceof VideoTopHolder) {
            mVideoId = model.videoModel.getId();
            VideoTopHolder topHolder = (VideoTopHolder) holder;
            topHolder.mTvVideoName.setText(model.videoModel.getTitle());
            topHolder.mTvHint.setText(model.videoModel.getDes());
            topHolder.mTvHint.post(new Runnable() {
                @Override
                public void run() {
                    int ellipsisCount = topHolder.mTvHint.getLayout().getEllipsisCount(topHolder.mTvHint.getLineCount() - 1);
                    //ellipsisCount>0说明没有显示全部，存在省略部分。
                    if (ellipsisCount > 0) {
                        topHolder.mIvExpandArrow.setVisibility(View.VISIBLE);
                    } else {
                        topHolder.mIvExpandArrow.setVisibility(View.GONE);
                    }
                    if (topHolder.mIvExpandArrow.getVisibility() == View.VISIBLE) {
                        topHolder.mLlHintExpand.setOnClickListener(v -> {
                            if (topHolder.mTvHint.getMaxLines() == Integer.MAX_VALUE) {
                                topHolder.mTvHint.setMaxLines(4);
                                topHolder.mIvExpandArrow.setRotation(0);
                            } else {
                                topHolder.mTvHint.setMaxLines(Integer.MAX_VALUE);
                                topHolder.mIvExpandArrow.setRotation(180);
                            }
                        });
                    }
                }
            });
            topHolder.mIvCollect.setSelected(model.videoModel.getCollected() == 1);
        } else if (holder instanceof VideoGroupHolder) {
            VideoGroupHolder etcHolder = (VideoGroupHolder) holder;
        } else if (holder instanceof VideoDetailsHolder) {
            VideoDetailsHolder itemHolder = (VideoDetailsHolder) holder;
            if ((playPosition + 2) == position) {
                itemHolder.mTvTitle.setTextColor(mContext.getResources().getColor(R.color.text_green));
                itemHolder.mIvPlay.setVisibility(View.VISIBLE);
            } else {
                itemHolder.mTvTitle.setTextColor(mContext.getResources().getColor(R.color.text_common));
                itemHolder.mIvPlay.setVisibility(View.GONE);
            }

            itemHolder.mTvTitle.setText(model.videoEtcModel.getTitle());
            GlideUtil.loadThumbnail(mContext, model.videoEtcModel.getImg(), itemHolder.mRivBg);

        }

    }

    @Override
    public int getItemViewType(int position) {
        return mDataList.get(position).type;
    }

    public int getmVideoId() {
        return mVideoId;
    }

    public ImageView getmIvCollectTop() {
        return mIvCollectTop;
    }

    public class VideoDetailsHolder extends RecyclerView.ViewHolder {

        private RoundedImageView mRivBg;
        private TextView mTvTitle;
        private ImageView mIvPlay;

        public VideoDetailsHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            initView(itemView);
        }

        private void initView(View itemView) {
            VideoPlayListAdapter.this.holder = this;
            mRivBg = (RoundedImageView) itemView.findViewById(R.id.riv_bg);
            mTvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            mIvPlay = (ImageView) itemView.findViewById(R.id.iv_play);
        }
    }


    public void onItemCLick(int position) {
        if (mOnRvItemListener != null && position >= 2) {
            playPosition = position - 2;
            notifyDataSetChanged();
            mOnRvItemListener.onItemClick(mDataList, playPosition);
        }
    }

    public class VideoGroupHolder extends RecyclerView.ViewHolder {

        private final TextView mTvTab;

        public VideoGroupHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            mTvTab = itemView.findViewById(R.id.tv_tab);
        }
    }

    public class VideoEtcHolder extends RecyclerView.ViewHolder {

        private final RecyclerView mRvList;

        public VideoEtcHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            mRvList = itemView.findViewById(R.id.rv_list);
        }
    }

    public class VideoTopHolder extends RecyclerView.ViewHolder {

        private TextView mTvVideoName;
        private LinearLayout mLlHintExpand;
        private ImageView mIvExpandArrow;
        private TextView mTvHint;
        private ImageView mIvCollect;

        public VideoTopHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            initView(itemView);
        }

        private void initView(View itemView) {
            mTvVideoName = (TextView) itemView.findViewById(R.id.tv_video_name);
            mLlHintExpand = (LinearLayout) itemView.findViewById(R.id.ll_hint_expand);
            mIvExpandArrow = (ImageView) itemView.findViewById(R.id.iv_expand_arrow);
            mTvHint = (TextView) itemView.findViewById(R.id.tv_hint);
            mIvCollect = (ImageView) itemView.findViewById(R.id.iv_collect);
            mIvCollectTop = mIvCollect;
            mIvCollect.setOnClickListener(v -> {
                addCollect();
            });
        }

        /**
         * 添加收藏取消收藏
         */
        public void addCollect() {
            if (requestIng) {
                return;
            }
            requestIng = true;
            mIvCollect.setSelected(!mIvCollect.isSelected());
            new HistoryPresenter(new HistoryPresenter.HistoryUi<CommonModel>() {
                @Override
                public LifecycleProvider getLifecycleProvider() {
                    if (mContext instanceof VideoPlayActivity) {
                        return (VideoPlayActivity) mContext;
                    } else {
                        return null;
                    }
                }

                @Override
                public void onSuccess(CommonModel data) {
                    if (mIvCollect != null) {
                        requestIng = false;
                        ToastUtil.show(mIvCollect.isSelected() ? UIConfig.COLLECTED : UIConfig.COLLECTED_UN);
                    }
                }

                @Override
                public void onFail(ResponeThrowable throwable) {
                    if (mIvCollect != null) {
                        requestIng = false;
                        ToastUtil.show(mIvCollect.isSelected() ? "收藏失败" : "取消收藏失败");
                        mIvCollect.setSelected(!mIvCollect.isSelected());
                    }
                }
            }).addCollect(HistoryApi.TYPE_COLLECT_VIDEO, mVideoId);
        }
    }

    @Override
    public String getText(int position, int type) {
        switch (type) {
            case OnSpeakTextListener.TYPE_SPEAK_TEXT:
                if (getItemViewType(position) == VideoPlayListModel.TYPE_BODY) {
                    return mDataList.get(position).title;
                }
        }

        return super.getText(position, type);
    }
}
