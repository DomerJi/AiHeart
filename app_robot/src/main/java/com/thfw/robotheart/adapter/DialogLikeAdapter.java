package com.thfw.robotheart.adapter;

import android.text.Editable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.base.face.MyTextWatcher;
import com.thfw.base.models.PickerData;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.base.utils.StringUtil;
import com.thfw.robotheart.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Author:pengs
 * Date: 2022/1/21 13:45
 * Describe:Todo
 */
public class DialogLikeAdapter extends BaseAdapter<PickerData, DialogLikeAdapter.DialogHolder> {

    private boolean singleCheck;
    private int checkPosition;

    public DialogLikeAdapter(List<PickerData> dataList) {
        super(dataList);
    }

    public void OpenSingleCheck() {
        singleCheck = true;
    }

    @NonNull
    @NotNull
    @Override
    public DialogHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new DialogHolder(inflate(com.thfw.robotheart.R.layout.item_custom_select_input_layout, parent));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull DialogHolder holder, int position) {


        if (EmptyUtil.isEmpty(mDataList)) {
            holder.mTvName.setVisibility(View.VISIBLE);
            holder.mEtName.setVisibility(View.GONE);
            holder.mTvName.setText("+自定义");
            holder.mTvName.setSelected(true);
            holder.mTvName.setBackgroundResource(R.drawable.yellow_radius_bg);
        } else {
            if (mDataList.size() == position) {
                holder.mTvName.setVisibility(View.VISIBLE);
                holder.mEtName.setVisibility(View.GONE);
                holder.mTvName.setText("+自定义");
                holder.mTvName.setSelected(true);
                holder.mTvName.setBackgroundResource(R.drawable.yellow_radius_bg);
            } else {
                holder.mTvName.setBackgroundResource(R.drawable.common_dialog_item_selector);
                PickerData pickerData = mDataList.get(position);
                if (pickerData.getType() == -1) {
                    holder.mTvName.setVisibility(View.GONE);
                    holder.mEtName.setVisibility(View.VISIBLE);
                    holder.mEtName.setText(pickerData.getPickerViewText());
                    holder.mEtName.addTextChangedListener(new MyTextWatcher() {
                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {


                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            super.afterTextChanged(s);
                            if (singleCheck && !StringUtil.isSpace(s.toString()) && s.length() >= 2) {
                                int size = mDataList.size();
                                for (int i = 0; i < size; i++) {
                                    if (mDataList.get(i).isCheck()) {
                                        mDataList.get(i).setCheck(false);
                                        notifyItemChanged(i);
                                    }

                                }
                            }
                            pickerData.setName(s.toString());
                        }
                    });
                } else {
                    holder.mTvName.setVisibility(View.VISIBLE);
                    holder.mEtName.setVisibility(View.GONE);
                    holder.mTvName.setText(pickerData.getPickerViewText());
                }
                checkPosition = -1;
                if (singleCheck && pickerData.isCheck()) {
                    checkPosition = position;
                }
                holder.mTvName.setSelected(pickerData.isCheck());

            }
        }
    }

    @Override
    public int getItemCount() {
        if (EmptyUtil.isEmpty(mDataList)) {
            return 1;
        } else {
            int customCount = 0;
            for (PickerData pickerData : mDataList) {
                if (pickerData.getType() == -1) {
                    if (singleCheck) {
                        return mDataList.size();
                    }
                    if (++customCount > 1) {
                        return mDataList.size();
                    }
                }
            }
            return mDataList.size() + 1;
        }
    }


    public class DialogHolder extends RecyclerView.ViewHolder {

        private TextView mTvName;
        private EditText mEtName;

        public DialogHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            mTvName = itemView.findViewById(com.thfw.robotheart.R.id.tv_name);
            mEtName = itemView.findViewById(com.thfw.robotheart.R.id.et_name);

            itemView.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                // 添加
                if (position >= mDataList.size()) {
                    mDataList.add(new PickerData("", -1));
                    notifyDataSetChanged();
                    return;
                }
                if (singleCheck) {
                    for (PickerData data : mDataList) {
                        data.setCheck(false);
                        if (data.getType() == -1) {
                            data.setName("");
                        }
                    }
                }
                PickerData pickerData = mDataList.get(position);
                pickerData.setCheck(!pickerData.isCheck());
                notifyDataSetChanged();
                if (mOnRvItemListener != null) {
                    mOnRvItemListener.onItemClick(mDataList, getBindingAdapterPosition());
                }
            });
        }
    }


}
