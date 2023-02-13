package com.thfw.mobileheart.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.base.face.OnSpeakTextListener;
import com.thfw.base.models.OrganizationModel;
import com.thfw.mobileheart.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Author:pengs
 * Date: 2021/12/7 15:33
 * Describe:Todo
 */
public class OrganSelectedAdapter extends BaseAdapter<OrganizationModel.OrganizationBean, OrganSelectedAdapter.OranSelectedHolder> {

    public OrganSelectedAdapter(List<OrganizationModel.OrganizationBean> dataList) {
        super(dataList);
    }

    @NonNull
    @NotNull
    @Override
    public OranSelectedHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new OranSelectedHolder(inflate(R.layout.item_organ_select, parent));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull OranSelectedHolder holder, int position) {
        holder.mTvName.setText(mDataList.get(position).getName());
        holder.mTvName.setSelected(position == getItemCount() - 1);
    }

    public class OranSelectedHolder extends RecyclerView.ViewHolder {

        private final TextView mTvName;

        public OranSelectedHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            mTvName = itemView.findViewById(R.id.tv_name);
            itemView.setOnClickListener(v -> {
                if (mOnRvItemListener != null) {
                    mOnRvItemListener.onItemClick(mDataList, getBindingAdapterPosition());
                }
            });
        }
    }


    @Override
    public String getText(int position, int type) {
        switch (type){
            case OnSpeakTextListener.TYPE_SPEAK_TEXT:
                return mDataList.get(position).getName();
        }

        return super.getText(position, type);
    }
}
