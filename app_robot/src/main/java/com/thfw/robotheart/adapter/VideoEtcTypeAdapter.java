package com.thfw.robotheart.adapter;

import android.text.TextPaint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.base.base.SpeechToAction;
import com.thfw.base.face.OnRvItemListener;
import com.thfw.base.models.VideoTypeModel;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.base.utils.LogUtil;
import com.thfw.robotheart.R;
import com.thfw.robotheart.activitys.video.VideoHomeActivity;
import com.thfw.robotheart.constants.UIConfig;
import com.thfw.robotheart.lhxk.LhXkHelper;
import com.thfw.ui.widget.DeviceUtil;

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

    public void setSelectedIndex(int selectedIndex) {
        expand = !expand;
        this.selectedIndex = selectedIndex;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull AudioEctTypeHolder holder, int position) {

        VideoTypeModel bean = mDataList.get(position);
        holder.mTvType.setTextSize(selectedIndex == position ? UIConfig.LEFT_TAB_MAX_TEXTSIZE : UIConfig.LEFT_TAB_MIN_TEXTSIZE);
        holder.mTvType.setSelected(selectedIndex == position);
        holder.mTvType.setText(bean.name);
        LogUtil.d("VideoEtcTypeAdapter", "bean.list = " + !EmptyUtil.isEmpty(bean.list));

        holder.mClRoot.setSelected(selectedIndex == position);

        if (bean.fire == 0) {
            holder.mIvFire.setVisibility(View.GONE);
        } else {
            holder.mIvFire.setVisibility(View.VISIBLE);
            holder.mIvFire.setImageLevel(bean.fire);
        }

        // 二十大标红
        if (selectedIndex == position) {
            holder.mTvType.setTextColor(bean.getSelectedColor());
            TextPaint paint = holder.mTvType.getPaint();
            paint.setFakeBoldText(bean.getSelectedColor() == bean.getUnSelectedColor());
        } else {
            holder.mTvType.setTextColor(bean.getUnSelectedColor());
        }

        if (selectedIndex == position && expand) {
            if (!EmptyUtil.isEmpty(bean.list)) {
                final VideoChildTypeAdapter childAdapter = new VideoChildTypeAdapter(bean.list);
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

                if (DeviceUtil.isLhXk_OS_R_SD01B()) {
                    int len = bean.list.size();
                    for (int i = 0; i < len; i++) {
                        String name = bean.list.get(i).name;
                        final int index = i;
                        LhXkHelper.putAction(VideoHomeActivity.class, new SpeechToAction(name, () -> {
                            childAdapter.setSelectedIndex(index);
                            childAdapter.notifyDataSetChanged();
                            childAdapter.getOnRvItemListener().onItemClick(childAdapter.getDataList(), index);
                        }));
                    }
                }

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
        private final ImageView mIvFire;
        private final ConstraintLayout mClRoot;

        public AudioEctTypeHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            mTvType = itemView.findViewById(R.id.tv_type);
            mRvChild = itemView.findViewById(R.id.rv_child);
            mClRoot = itemView.findViewById(R.id.cl_root);
            mIvFire = itemView.findViewById(R.id.iv_fire);
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
