package com.thfw.mobileheart.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.mobileheart.model.VideoEtcOrderModel;
import com.thfw.mobileheart.model.VideoPlayListModel;
import com.thfw.mobileheart.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Author:pengs
 * Date: 2021/10/14 11:44
 * Describe:Todo
 */
public class VideoPlayListAdapter extends BaseAdapter<VideoPlayListModel, RecyclerView.ViewHolder> {


    public VideoPlayListAdapter(List<VideoPlayListModel> dataList) {
        super(dataList);
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VideoPlayListModel.TYPE_TOP:
                return new VideoTopHolder(LayoutInflater.from(mContext).inflate(R.layout.item_video_details_head_layout, parent, false));
            case VideoPlayListModel.TYPE_ECT:
                return new VideoEtcHolder(LayoutInflater.from(mContext).inflate(R.layout.item_video_details_etc_layout, parent, false));
            case VideoPlayListModel.TYPE_GROUP:
                return new VideoGroupHolder(LayoutInflater.from(mContext).inflate(R.layout.item_video_details_tabtitle_layout, parent, false));
            default:
                return new VideoDetailsHolder(LayoutInflater.from(mContext).inflate(R.layout.item_video_details_layout, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof VideoGroupHolder) {
            VideoGroupHolder groupHolder = (VideoGroupHolder) holder;
            groupHolder.mTvTab.setText(mDataList.get(position).headName);
        } else if (holder instanceof VideoEtcHolder) {
            VideoEtcHolder etcHolder = (VideoEtcHolder) holder;
            if (etcHolder.mRvList.getAdapter() == null) {
                etcHolder.mRvList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
                List<VideoEtcOrderModel> orders = new ArrayList<>();
                for (int i = 0; i < 20; i++) {
                    VideoEtcOrderModel model = new VideoEtcOrderModel();
                    model.order = String.valueOf((i + 1));
                    orders.add(model);
                }
                etcHolder.mRvList.setAdapter(new VideoEtcOrderAdapter(orders));
            }
        }

    }

    @Override
    public int getItemViewType(int position) {
        return mDataList.get(position).type;
    }

    public class VideoDetailsHolder extends RecyclerView.ViewHolder {

        public VideoDetailsHolder(@NonNull @NotNull View itemView) {
            super(itemView);
        }
    }

    public class VideoGroupHolder extends RecyclerView.ViewHolder {

        private final TextView mTvTab;
        private final TextView mTvMore;

        public VideoGroupHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            mTvTab = itemView.findViewById(R.id.tv_tab);
            mTvMore = itemView.findViewById(R.id.tv_more);
        }
    }

    public class VideoEtcHolder extends RecyclerView.ViewHolder {

        private final RecyclerView mRvList;

        public VideoEtcHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            mRvList = itemView.findViewById(R.id.rv_list);
        }
    }

    public class VideoTopHolder extends RecyclerView.ViewHolder {

        public VideoTopHolder(@NonNull @NotNull View itemView) {
            super(itemView);
        }
    }
}
