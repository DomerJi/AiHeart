package com.thfw.robotheart.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.base.face.OnRvItemListener;
import com.thfw.base.models.VideoTypeModel;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.base.utils.LogUtil;
import com.thfw.robotheart.R;
import com.thfw.robotheart.constants.UIConfig;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Author:pengs
 * Date: 2021/12/2 16:24
 * Describe:音频合集类型列表
 */
public class VideoEtcTypeAdapter extends BaseAdapter<VideoTypeModel, VideoEtcTypeAdapter.AudioEctTypeHolder> {

    private int selectedIndex = 0;
    private boolean expand = true;
    private int childSelectedIndex = -1;

    public VideoEtcTypeAdapter(List<VideoTypeModel> dataList) {
        super(dataList);
    }

    @NonNull
    @NotNull
    @Override
    public AudioEctTypeHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new AudioEctTypeHolder(inflate(R.layout.item_video_etc_type, parent));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull AudioEctTypeHolder holder, int position) {

        VideoTypeModel bean = mDataList.get(position);
        holder.mTvType.setTextSize(selectedIndex == position ? UIConfig.LEFT_TAB_MAX_TEXTSIZE : UIConfig.LEFT_TAB_MIN_TEXTSIZE);
        holder.mTvType.setSelected(selectedIndex == position);
        holder.mTvType.setText(bean.name);
        LogUtil.d("VideoEtcTypeAdapter", "bean.list = " + !EmptyUtil.isEmpty(bean.list));
        if (selectedIndex == position && expand) {
            if (!EmptyUtil.isEmpty(bean.list)) {
                VideoChildTypeAdapter childAdapter = new VideoChildTypeAdapter(bean.list);
                childAdapter.setSelectedIndex(childSelectedIndex);
                childAdapter.setOnRvItemListener(new OnRvItemListener<VideoTypeModel>() {
                    @Override
                    public void onItemClick(List<VideoTypeModel> list, int position) {
                        childSelectedIndex = position;
                        if (mOnRvItemListener != null) {
                            mOnRvItemListener.onItemClick(list, position);
                        }
                    }
                });
                holder.mRvChild.setAdapter(childAdapter);
                holder.mRvChild.setVisibility(View.VISIBLE);
            } else {
                holder.mRvChild.setVisibility(View.GONE);
            }
        } else {
            holder.mRvChild.removeAllViews();
            holder.mRvChild.setVisibility(View.GONE);
        }

    }


    public class AudioEctTypeHolder extends RecyclerView.ViewHolder {

        private final TextView mTvType;
        private final RecyclerView mRvChild;

        public AudioEctTypeHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            mTvType = itemView.findViewById(R.id.tv_type);
            mRvChild = itemView.findViewById(R.id.rv_child);
            mRvChild.setLayoutManager(new LinearLayoutManager(mContext));
            itemView.setOnClickListener(v -> {
                if (getBindingAdapterPosition() == selectedIndex) {
                    expand = !expand;
                } else {
                    expand = true;
                }
                selectedIndex = getBindingAdapterPosition();
                childSelectedIndex = -1;
                notifyDataSetChanged();
                if (mOnRvItemListener != null) {
                    mOnRvItemListener.onItemClick(getDataList(), selectedIndex);
                }
            });
        }
    }

}
