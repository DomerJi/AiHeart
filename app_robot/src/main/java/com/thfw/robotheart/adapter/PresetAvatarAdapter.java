package com.thfw.robotheart.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.thfw.base.models.PresetAvatarModel;
import com.thfw.robotheart.R;
import com.thfw.ui.utils.GlideUtil;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Author:pengs
 * Date: 2021/12/9 16:13
 * Describe:Todo
 */
public class PresetAvatarAdapter extends BaseAdapter<PresetAvatarModel, PresetAvatarAdapter.CollectHolder> {


    public PresetAvatarAdapter(List<PresetAvatarModel> dataList) {
        super(dataList);
    }

    @NonNull
    @NotNull
    @Override
    public CollectHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new CollectHolder(inflate(R.layout.item_preset_avatar, parent));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull PresetAvatarAdapter.CollectHolder holder, int position) {
        PresetAvatarModel avatarModel = mDataList.get(position);
        GlideUtil.load(mContext, avatarModel.getPic(), holder.mRivPresetAvatar);
    }

    public class CollectHolder extends RecyclerView.ViewHolder {

        private RoundedImageView mRivPresetAvatar;

        public CollectHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            initView(itemView);
            itemView.setOnClickListener(v -> {
                if (mOnRvItemListener != null) {
                    mOnRvItemListener.onItemClick(mDataList, getBindingAdapterPosition());
                }
            });
        }

        private void initView(View itemView) {
            mRivPresetAvatar = (RoundedImageView) itemView.findViewById(R.id.riv_preset_avatar);
        }
    }
}
