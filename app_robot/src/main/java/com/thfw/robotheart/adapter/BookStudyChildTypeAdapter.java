package com.thfw.robotheart.adapter;

import android.graphics.Color;
import android.text.TextPaint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.base.models.BookStudyTypeModel;
import com.thfw.robotheart.R;
import com.thfw.robotheart.constants.UIConfig;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Author:pengs
 * Date: 2021/12/2 16:24
 * Describe:音频合集类型列表
 */
public class BookStudyChildTypeAdapter extends BaseAdapter<BookStudyTypeModel, BookStudyChildTypeAdapter.BookStudyTypeHolder> {

    private int selectedIndex = -1;

    public BookStudyChildTypeAdapter(List<BookStudyTypeModel> dataList) {
        super(dataList);
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }

    public boolean isSelected() {
        return selectedIndex != -1;
    }

    @NonNull
    @NotNull
    @Override
    public BookStudyTypeHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new BookStudyTypeHolder(inflate(R.layout.item_book_study_child_type, parent));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull BookStudyTypeHolder holder, int position) {
        BookStudyTypeModel bean = mDataList.get(position);
        holder.mTvType.setTextSize(selectedIndex == position ? UIConfig.LEFT_TAB_CHILD_MAX_TEXTSIZE : UIConfig.LEFT_TAB_CHILD_MIN_TEXTSIZE);
        holder.mTvType.setSelected(selectedIndex == position);
        holder.mTvType.setText(bean.name);
//        holder.mTvType.setTextSize(holder.mTvType.getText().length() > 8 ? 15 : 18);
        holder.mVLine.setVisibility(position == getItemCount() - 1 ? View.INVISIBLE : View.VISIBLE);

        if (bean.fire == 0) {
            holder.mIvFire.setVisibility(View.GONE);
        } else {
            holder.mIvFire.setVisibility(View.VISIBLE);
            holder.mIvFire.setImageLevel(bean.fire);
        }

        // 二十大标红
        if (selectedIndex == position) {
            if (bean.isSelectedChange()) {
                holder.mTvType.setTextColor(bean.getSelectedColor());
                TextPaint paint = holder.mTvType.getPaint();
                if (bean.isUnSelectedChange()) {
                    paint.setFakeBoldText(bean.getUnSelectedColor() == bean.getSelectedColor());
                } else {
                    paint.setFakeBoldText(false);
                }
            } else {
                holder.mTvType.setTextColor(mContext.getResources().getColor(R.color.colorRobotFore));
            }

        } else {
            if (bean.isUnSelectedChange()) {
                holder.mTvType.setTextColor(bean.getUnSelectedColor());
            } else {
                holder.mTvType.setTextColor(Color.WHITE);
            }
            TextPaint paint = holder.mTvType.getPaint();
            paint.setFakeBoldText(false);
        }

    }

    public class BookStudyTypeHolder extends RecyclerView.ViewHolder {

        private final TextView mTvType;
        private final RecyclerView mRvChild;
        private final View mVLine;
        private final ImageView mIvFire;
        private final ConstraintLayout mClRoot;

        public BookStudyTypeHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            mTvType = itemView.findViewById(R.id.tv_type);
            mRvChild = itemView.findViewById(R.id.rv_child);
            mClRoot = itemView.findViewById(R.id.cl_root);
            mIvFire = itemView.findViewById(R.id.iv_fire);
            mVLine = itemView.findViewById(R.id.v_line);
            itemView.setOnClickListener(v -> {
                selectedIndex = getBindingAdapterPosition();
                notifyDataSetChanged();
                if (mOnRvItemListener != null) {
                    mOnRvItemListener.onItemClick(getDataList(), selectedIndex);
                }
            });
        }
    }

}
