package com.thfw.robotheart.adapter;

import android.text.TextPaint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.base.models.BookTypeModel;
import com.thfw.robotheart.R;
import com.thfw.robotheart.constants.UIConfig;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Author:pengs
 * Date: 2021/12/2 16:24
 * Describe:音频合集类型列表
 */
public class BookTypeAdapter extends BaseAdapter<BookTypeModel.BookTypeImpModel, BookTypeAdapter.BookTypeHolder> {

    private int selectedIndex = 0;

    public BookTypeAdapter(List<BookTypeModel.BookTypeImpModel> dataList) {
        super(dataList);
    }

    @NonNull
    @NotNull
    @Override
    public BookTypeHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new BookTypeHolder(inflate(R.layout.item_audio_etc_type, parent));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull BookTypeHolder holder, int position) {
        BookTypeModel.BookTypeImpModel bean = mDataList.get(position);

        holder.mTvType.setTextSize(selectedIndex == position ? UIConfig.LEFT_TAB_MAX_TEXTSIZE : UIConfig.LEFT_TAB_MIN_TEXTSIZE);
        holder.mTvType.setSelected(selectedIndex == position);
        holder.mTvType.setText(bean.value);

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
    }

    @Override
    public String getText(int position, int type) {
        switch (type) {
            case TYPE_SPEAK_TEXT:
                return mDataList.get(position).value;

        }
        return super.getText(position, type);
    }

    public class BookTypeHolder extends RecyclerView.ViewHolder {

        private final TextView mTvType;

        private final ImageView mIvFire;
        private final ConstraintLayout mClRoot;

        public BookTypeHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            mTvType = itemView.findViewById(R.id.tv_type);

            mClRoot = itemView.findViewById(R.id.cl_root);
            mIvFire = itemView.findViewById(R.id.iv_fire);
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
