package com.thfw.mobileheart.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.thfw.base.models.HomeEntity;
import com.thfw.base.models.HomeHistoryEntity;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.Util;
import com.thfw.mobileheart.R;
import com.thfw.mobileheart.activity.audio.AudioHomeActivity;
import com.thfw.mobileheart.activity.audio.AudioPlayerActivity;
import com.thfw.mobileheart.activity.exercise.ExerciseActivity;
import com.thfw.mobileheart.activity.me.HotPhoneActivity;
import com.thfw.mobileheart.activity.read.ReadHomeActivity;
import com.thfw.mobileheart.activity.read.StudyHomeActivity;
import com.thfw.mobileheart.activity.talk.AskActivity;
import com.thfw.mobileheart.activity.talk.ChatActivity;
import com.thfw.mobileheart.activity.test.TestingActivity;
import com.thfw.mobileheart.activity.video.VideoHomeActivity;
import com.thfw.ui.utils.GlideUtil;
import com.youth.banner.Banner;
import com.youth.banner.indicator.CircleIndicator;
import com.youth.banner.listener.OnBannerListener;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Author:pengs
 * Date: 2021/7/22 10:21
 * Describe:首页适配器
 */
public class HomeAdapter extends BaseAdapter<HomeEntity, RecyclerView.ViewHolder> {
    private static final long DELAY_TIME_BANNER = 5000;
    private Banner mBanner;
    public static String imageUrl = "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fattach.bbs.miui.com%2Fforum%2F201312%2F31%2F111849zs48tpa2r1rau2id.jpg&refer=http%3A%2F%2Fattach.bbs.miui.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1629516874&t=2717a45c8f2c0237c30da28ca41e0801";

    public HomeAdapter(List<HomeEntity> dataList) {
        super(dataList);
    }

    public void setBanner(boolean resume) {
        if (this.mBanner != null) {
            if (resume) {
                this.mBanner.start();
            } else {
                this.mBanner.stop();
            }
        }
    }

    public int getBannerHeight() {
        return mBanner == null ? 0 : mBanner.getMeasuredHeight();
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

        switch (viewType) {
            case HomeEntity.TYPE_BANNER:
                return new BannerHolder(LayoutInflater.from(mContext).inflate(R.layout.item_home_banner_layout, parent, false));
            case HomeEntity.TYPE_SORT:
                return new SortHolder(LayoutInflater.from(mContext).inflate(R.layout.item_home_sort_layout, parent, false));
            case HomeEntity.TYPE_CUSTOM_MADE:
                return new MadeHolder(LayoutInflater.from(mContext).inflate(R.layout.item_home_custommade_layout, parent, false));
            case HomeEntity.TYPE_HISTORY:
                return new HistoryHolder(LayoutInflater.from(mContext).inflate(R.layout.item_home_history_layout, parent, false));
            case HomeEntity.TYPE_TAB_TITLE:
                return new TabTitleHolder(LayoutInflater.from(mContext).inflate(R.layout.item_home_tab_title_layout, parent, false));
            case HomeEntity.TYPE_BODY:
                return new BodyHolder(LayoutInflater.from(mContext).inflate(R.layout.item_home_body_layout, parent, false));
            default:
                return new Body2Holder(LayoutInflater.from(mContext).inflate(R.layout.item_home_body2_layout, parent, false));

        }
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof BodyHolder) {
            BodyHolder bodyHolder = (BodyHolder) holder;
            bodyHolder.mTvTitle.setText("Home Body " + position);
            bodyHolder.mTvTitle.setBackgroundColor(mDataList.get(position).color);
            GlideUtil.load(mContext, imageUrl, bodyHolder.mRivAvatar);
        } else if (holder instanceof BannerHolder) {
            BannerHolder bannerHolder = (BannerHolder) holder;
            bannerHolder.initData(mDataList.get(position));
        } else if (holder instanceof HistoryHolder) {
            HistoryHolder historyHolder = (HistoryHolder) holder;
            historyHolder.setData(null);
        } else if (holder instanceof TabTitleHolder) {
            TabTitleHolder tabTitleHolder = (TabTitleHolder) holder;
            tabTitleHolder.mTvTitle.setText(mDataList.get(position).tabTitle);
        } else if (holder instanceof BodyHolder) {
            BodyHolder bodyHolder = (BodyHolder) holder;
        } else if (holder instanceof Body2Holder) {
            Body2Holder body2Holder = (Body2Holder) holder;
            int max = Util.dipToPx(16, mContext);
            int min = Util.dipToPx(8, mContext);
            int middle = Util.dipToPx(6, mContext);
            if (mDataList.get(position).body2Position % 2 == 0) {
                body2Holder.itemView.setPadding(max, middle, min, middle);
            } else {
                body2Holder.itemView.setPadding(min, middle, max, min);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mDataList.get(position).type;
    }

    public class BodyHolder extends RecyclerView.ViewHolder {

        private final TextView mTvTitle;
        private final RoundedImageView mRivAvatar;
        private TextView mTvNumber;
        private TextView mTvType;

        public BodyHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            mTvTitle = itemView.findViewById(R.id.tv_title);
            mTvNumber = (TextView) itemView.findViewById(R.id.tv_number);
            mTvType = (TextView) itemView.findViewById(R.id.tv_type);
            mRivAvatar = itemView.findViewById(R.id.riv_avatar);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnRvItemListener != null) {
                        mOnRvItemListener.onItemClick(mDataList, getBindingAdapterPosition());
                    }
                }
            });
        }

    }

    public class TabTitleHolder extends RecyclerView.ViewHolder {

        private final TextView mTvTitle;

        public TabTitleHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            mTvTitle = itemView.findViewById(R.id.tv_tab);
        }
    }

    public class Body2Holder extends RecyclerView.ViewHolder {

        private final TextView mTvTitle;
        private final RoundedImageView mRivAvatar;

        public Body2Holder(@NonNull @NotNull View itemView) {
            super(itemView);
            mTvTitle = itemView.findViewById(R.id.tv_title);
            mRivAvatar = itemView.findViewById(R.id.riv_avatar);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnRvItemListener != null) {
                        mOnRvItemListener.onItemClick(mDataList, getBindingAdapterPosition());
                    }
                }
            });
        }
    }

    public class HistoryHolder extends RecyclerView.ViewHolder {

        private RecyclerView mRvHistory;
        private HomeHistoryAdapter homeHistoryAdapter;

        public HistoryHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            initView(itemView);
        }

        private void initView(View itemView) {
            mRvHistory = (RecyclerView) itemView.findViewById(R.id.rv_history);
            mRvHistory.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        }

        private void setData(List<HomeHistoryEntity> list) {
            if (homeHistoryAdapter == null) {
                homeHistoryAdapter = new HomeHistoryAdapter(list);
                mRvHistory.setAdapter(homeHistoryAdapter);
            } else {
                homeHistoryAdapter.setDataList(list);
            }
        }
    }

    public class SortHolder extends RecyclerView.ViewHolder {

        public SortHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            View.OnClickListener clickViewListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()) {
                        case R.id.rl_tab_01:
                            ChatActivity.startActivity(mContext);
                            break;
                        case R.id.rl_tab_02:
                            AskActivity.startActivity(mContext);
                            break;
                        case R.id.rl_tab_03:
                            TestingActivity.startActivity(mContext);
                            break;
                        case R.id.rl_tab_04:
                            ExerciseActivity.startActivity(mContext);
                            break;
                        case R.id.rl_tab_05:
                            AudioHomeActivity.startActivity(mContext);
                            break;
                        case R.id.rl_tab_06:
                            VideoHomeActivity.startActivity(mContext);
                            break;
                        case R.id.rl_tab_07:
                            ReadHomeActivity.startActivity(mContext);
                            break;
                        case R.id.rl_tab_08:
                            StudyHomeActivity.startActivity(mContext);
                            break;
                    }
                }
            };

            itemView.findViewById(R.id.rl_tab_01).setOnClickListener(clickViewListener);
            itemView.findViewById(R.id.rl_tab_02).setOnClickListener(clickViewListener);
            itemView.findViewById(R.id.rl_tab_03).setOnClickListener(clickViewListener);
            itemView.findViewById(R.id.rl_tab_04).setOnClickListener(clickViewListener);
            itemView.findViewById(R.id.rl_tab_05).setOnClickListener(clickViewListener);
            itemView.findViewById(R.id.rl_tab_06).setOnClickListener(clickViewListener);
            itemView.findViewById(R.id.rl_tab_07).setOnClickListener(clickViewListener);
            itemView.findViewById(R.id.rl_tab_08).setOnClickListener(clickViewListener);
        }
    }

    public class MadeHolder extends RecyclerView.ViewHolder {

        public MadeHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            itemView.findViewById(R.id.root_hotline).setOnClickListener(v -> {
                mContext.startActivity(new Intent(mContext, HotPhoneActivity.class));
            });
        }
    }

    public class BannerHolder extends RecyclerView.ViewHolder {


        public BannerHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            mBanner = itemView.findViewById(R.id.banner);
        }

        public void initData(HomeEntity homeEntity) {
            if (mBanner.getAdapter() != null) {
                return;
            }
            mBanner.setIndicator(new CircleIndicator(mContext));
            mBanner.setLoopTime(DELAY_TIME_BANNER);
            // mBanner.addPageTransformer(new ScaleInTransformer());
            mBanner.setAdapter(new ImageAdapter(homeEntity.imageUrls));
            mBanner.setOnBannerListener(new OnBannerListener() {
                @Override
                public void OnBannerClick(Object data, int position) {
                    LogUtil.d("OnBannerClick -> " + position);
                    mContext.startActivity(new Intent(mContext, AudioPlayerActivity.class));
                }
            });
        }

    }

    public class HomeHistoryAdapter extends BaseAdapter<HomeHistoryEntity, HomeHistoryHolder> {

        public HomeHistoryAdapter(List<HomeHistoryEntity> dataList) {
            super(dataList);
        }

        @NonNull
        @NotNull
        @Override
        public HomeHistoryHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
            return new HomeHistoryHolder(LayoutInflater.from(mContext).inflate(R.layout.item_home_history_children_layout, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull @NotNull HomeHistoryHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 10;
        }
    }

    public class HomeHistoryHolder extends RecyclerView.ViewHolder {

        public HomeHistoryHolder(@NonNull @NotNull View itemView) {
            super(itemView);
        }
    }
}
