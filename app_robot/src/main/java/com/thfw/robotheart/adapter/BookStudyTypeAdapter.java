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
 * Describe:学习强军两级类型
 */
public class BookStudyTypeAdapter extends BaseAdapter<BookStudyTypeModel, BookStudyTypeAdapter.BookStudyTypeHolder> {

    private boolean expand = false;
    private int selectedIndex = 0;
    private int childSelectedIndex = -1;

    public BookStudyTypeAdapter(List<BookStudyTypeModel> dataList) {
        super(dataList);
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
        holder.mTvType.setSelected(selectedIndex == position);
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
                BookStudyChildTypeAdapter childAdapter = new BookStudyChildTypeAdapter(bean.list);
                childAdapter.setSelectedIndex(childSelectedIndex);
                childAdapter.setOnRvItemListener(new OnRvItemListener<BookStudyTypeModel>() {
                    @Override
                    public void onItemClick(List<BookStudyTypeModel> list, int position) {
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

    public class BookStudyTypeHolder extends RecyclerView.ViewHolder {

        private final TextView mTvType;
        private final RecyclerView mRvChild;
        private final ImageView mIvFire;
        private final ConstraintLayout mClRoot;

        public BookStudyTypeHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            mTvType = itemView.findViewById(R.id.tv_type);
            mRvChild = itemView.findViewById(R.id.rv_child);
            mClRoot = itemView.findViewById(R.id.cl_root);
            mIvFire = itemView.findViewById(R.id.iv_fire);
            mRvChild.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
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
