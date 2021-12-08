package com.thfw.robotheart.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.base.face.OnRvItemListener;
import com.thfw.base.models.AudioTypeModel;
import com.thfw.base.models.ExerciseModel;
import com.thfw.robotheart.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Author:pengs
 * Date: 2021/12/6 11:30
 * Describe:Todo
 */
public class ExerciseIngAdapter extends BaseAdapter<ExerciseModel, ExerciseIngAdapter.ExerciseHolder> {


    public ExerciseIngAdapter(List<ExerciseModel> dataList) {
        super(dataList);
    }

    @NonNull
    @NotNull
    @Override
    public ExerciseHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new ExerciseHolder(inflate(R.layout.item_exercise_ing, parent));
    }

    @Override
    public int getItemCount() {
        return 12;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ExerciseHolder holder, int position) {
        holder.mTvPageCurrent.setText(String.valueOf(position + 1));
        holder.mTvPageSum.setText("/" + String.valueOf(getItemCount()));

        ExerciseSelectAdapter selectAdapter = new ExerciseSelectAdapter(null);
        holder.mBtConfirm.setEnabled(selectAdapter.getSelectedIndex() != -1);
        selectAdapter.setOnRvItemListener(new OnRvItemListener<AudioTypeModel>() {
            @Override
            public void onItemClick(List<AudioTypeModel> list, int position) {
                holder.mBtConfirm.setEnabled(true);
            }
        });
        holder.mRvSelect.setAdapter(selectAdapter);
    }

    public class ExerciseHolder extends RecyclerView.ViewHolder {

        private TextView mTvSelected;
        private TextView mTvPageCurrent;
        private TextView mTvPageSum;
        private View mVLine;
        private TextView mTvTitle;
        private Button mBtConfirm;
        private RecyclerView mRvSelect;

        public ExerciseHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            initView(itemView);
        }

        private void initView(View itemView) {
            mTvSelected = (TextView) itemView.findViewById(R.id.tv_selected);
            mTvPageCurrent = (TextView) itemView.findViewById(R.id.tv_page_current);
            mTvPageSum = (TextView) itemView.findViewById(R.id.tv_page_sum);
            mVLine = (View) itemView.findViewById(R.id.v_line);
            mTvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            mBtConfirm = (Button) itemView.findViewById(R.id.bt_confirm);
            mRvSelect = (RecyclerView) itemView.findViewById(R.id.rvSelect);
            mRvSelect.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
            mBtConfirm.setOnClickListener(v -> {
                if (mOnRvItemListener != null) {
                    mOnRvItemListener.onItemClick(getDataList(), getBindingAdapterPosition());
                }
            });
        }
    }
}
