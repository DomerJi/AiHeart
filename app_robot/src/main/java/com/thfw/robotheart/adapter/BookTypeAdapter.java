package com.thfw.robotheart.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
public class BookTypeAdapter extends BaseAdapter<BookTypeModel, BookTypeAdapter.BookTypeHolder> {

    private int selectedIndex = -1;

    public BookTypeAdapter(List<BookTypeModel> dataList) {
        super(dataList);
        if (getItemCount() > 0) {
            selectedIndex = 0;
        }
    }

    @NonNull
    @NotNull
    @Override
    public BookTypeHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new BookTypeHolder(inflate(R.layout.item_audio_etc_type, parent));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull BookTypeHolder holder, int position) {
        holder.mTvType.setTextSize(selectedIndex == position ? UIConfig.LEFT_TAB_MAX_TEXTSIZE : UIConfig.LEFT_TAB_MIN_TEXTSIZE);
        holder.mTvType.setSelected(selectedIndex == position);
        holder.mTvType.setText("Title_" + position);
    }

    @Override
    public int getItemCount() {
        return 8;
    }

    public class BookTypeHolder extends RecyclerView.ViewHolder {

        private final TextView mTvType;

        public BookTypeHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            mTvType = itemView.findViewById(R.id.tv_type);
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
