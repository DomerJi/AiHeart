package com.thfw.robotheart.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.base.face.OnRvItemListener;
import com.thfw.base.models.BookStudyTypeModel;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.robotheart.R;
import com.thfw.robotheart.constants.UIConfig;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Author:pengs
 * Date: 2021/12/2 16:24
 * Describe:音频合集类型列表
 */
public class BookStudyTypeAdapter extends BaseAdapter<BookStudyTypeModel, BookStudyTypeAdapter.BookStudyTypeHolder> {

    private int selectedIndex = -1;
    private int childSelectedIndex = -1;

    public BookStudyTypeAdapter(List<BookStudyTypeModel> dataList) {
        super(dataList);
        if (getItemCount() > 0) {
            selectedIndex = 0;
        }
    }

    @NonNull
    @NotNull
    @Override
    public BookStudyTypeHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

        return new BookStudyTypeHolder(inflate(R.layout.item_book_study_type, parent));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull BookStudyTypeHolder holder, int position) {
        BookStudyTypeModel bean = mDataList.get(position);
        holder.mTvType.setTextSize(selectedIndex == position ? UIConfig.LEFT_TAB_MAX_TEXTSIZE : UIConfig.LEFT_TAB_MIN_TEXTSIZE);

        holder.mTvType.setText(bean.name);
        if (selectedIndex == position) {
            if (!EmptyUtil.isEmpty(bean.list)) {
                BookStudyChildTypeAdapter childAdapter = new BookStudyChildTypeAdapter(bean.list);
                childAdapter.setSelectedIndex(childSelectedIndex);
                childAdapter.setOnRvItemListener(new OnRvItemListener<BookStudyTypeModel>() {
                    @Override
                    public void onItemClick(List<BookStudyTypeModel> list, int position) {
                        childSelectedIndex = position;
                        if (mOnRvItemListener != null) {
                            mOnRvItemListener.onItemClick(list, position);
                        }
                        holder.mTvType.setSelected(false);
                    }
                });
                holder.mRvChild.setAdapter(childAdapter);
                holder.mRvChild.setVisibility(View.VISIBLE);
                if (!childAdapter.isSelected()) {
                    holder.mTvType.setSelected(selectedIndex == position);
                } else {
                    holder.mTvType.setSelected(false);
                }

            } else {
                holder.mTvType.setSelected(selectedIndex == position);
                holder.mRvChild.setVisibility(View.GONE);
            }
        } else {
            holder.mTvType.setSelected(false);
            holder.mRvChild.removeAllViews();
            holder.mRvChild.setVisibility(View.GONE);
        }
    }

    public class BookStudyTypeHolder extends RecyclerView.ViewHolder {

        private final TextView mTvType;
        private final RecyclerView mRvChild;

        public BookStudyTypeHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            mTvType = itemView.findViewById(R.id.tv_type);
            mRvChild = itemView.findViewById(R.id.rv_child);
            mRvChild.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
            itemView.setOnClickListener(v -> {
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
