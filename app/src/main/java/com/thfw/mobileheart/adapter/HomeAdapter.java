package com.thfw.mobileheart.adapter;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.thfw.base.models.HomeEntity;
import com.thfw.base.models.HomeHistoryEntity;
import com.thfw.base.models.TalkModel;
import com.thfw.base.utils.HourUtil;
import com.thfw.base.utils.LogUtil;
import com.thfw.mobileheart.MyApplication;
import com.thfw.mobileheart.R;
import com.thfw.mobileheart.activity.audio.AudioHomeActivity;
import com.thfw.mobileheart.activity.audio.AudioPlayerActivity;
import com.thfw.mobileheart.activity.exercise.ExerciseActivity;
import com.thfw.mobileheart.activity.me.HotPhoneActivity;
import com.thfw.mobileheart.activity.mood.MoodDetailActivity;
import com.thfw.mobileheart.activity.mood.StatusActivity;
import com.thfw.mobileheart.activity.read.ReadHomeActivity;
import com.thfw.mobileheart.activity.read.StudyHomeActivity;
import com.thfw.mobileheart.activity.talk.ChatActivity;
import com.thfw.mobileheart.activity.talk.ThemeListActivity;
import com.thfw.mobileheart.activity.test.TestingActivity;
import com.thfw.mobileheart.activity.video.VideoHomeActivity;
import com.thfw.mobileheart.util.FunctionDurationUtil;
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
public class HomeAdapter extends BaseAdapter<HomeEntity, RecyclerView.ViewHolder> implements MyApplication.OnMinuteListener {
    private static final long DELAY_TIME_BANNER = 5000;
    private static long lastChange;
    private Banner mBanner;

    public HomeAdapter(List<HomeEntity> dataList) {
        super(dataList);
    }

    public void setBanner(boolean resume) {
        if (this.mBanner != null) {
            if (resume) {
                this.mBanner.start();
                MyApplication.getApp().addOnMinuteListener(this);
                onChanged();
            } else {
                this.mBanner.stop();
                MyApplication.getApp().onRemoveOnMinuteListener(this);
            }
        }
    }


    /**
     * 时间更新/活跃时长
     */
    @Override
    public void onChanged() {
        if (System.currentTimeMillis() - lastChange > HourUtil.LEN_MINUTE) {
            lastChange = System.currentTimeMillis();
            LogUtil.d("===========================时间更新/活跃时长================================");
            if (getItemCount() > 2) {
                notifyItemChanged(2);
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
                return new BannerHolder(inflate(R.layout.item_home_banner_layout, parent));
            case HomeEntity.TYPE_SORT:
                return new SortHolder(inflate(R.layout.item_home_sort_layout, parent));
            case HomeEntity.TYPE_CUSTOM_MADE:
                MyApplication.getApp().addOnMinuteListener(this);
                return new MadeHolder(inflate(R.layout.item_home_custommade_layout, parent));
            case HomeEntity.TYPE_HISTORY:// 去除
                return new HistoryHolder(inflate(R.layout.item_home_history_layout, parent));
            case HomeEntity.TYPE_TAB_TITLE:
                return new TabTitleHolder(inflate(R.layout.item_home_tab_title_layout, parent));
            case HomeEntity.TYPE_BODY:
                return new BodyHolder(inflate(R.layout.item_home_body_layout, parent));
            default:
                return new Body2Holder(inflate(R.layout.item_home_body2_layout, parent));

        }
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        HomeEntity entity = mDataList.get(position);
        getItemViewType(position);
        switch (entity.type) {
            case HomeEntity.TYPE_BODY:
                BodyHolder bodyHolder = (BodyHolder) holder;
                bodyHolder.mTvTitle.setText("Home Body " + position);
                GlideUtil.load(mContext, R.mipmap.cat, bodyHolder.mRivAvatar);
                break;
            case HomeEntity.TYPE_BANNER:
                BannerHolder bannerHolder = (BannerHolder) holder;
                bannerHolder.initData(entity);
                break;
            case HomeEntity.TYPE_CUSTOM_MADE:
                MadeHolder madeHolder = (MadeHolder) holder;
                madeHolder.mTvTodayActivityValue.setText(FunctionDurationUtil.getFunctionTimeHour(FunctionDurationUtil.FUNCTION_APP));
                break;
            case HomeEntity.TYPE_TAB_TITLE:
                TabTitleHolder tabTitleHolder = (TabTitleHolder) holder;
                tabTitleHolder.mTvTitle.setText(mDataList.get(position).tabTitle);
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mDataList.get(position).type;
    }

    @Override
    public void onViewRecycled(@NonNull @NotNull RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
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
                            ThemeListActivity.startActivity(mContext);
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

        private ConstraintLayout mClLeft;
        private ConstraintLayout mClRightTop;
        private TextView mTvMoodTitle;
        private LinearLayout mLlMood;
        private RoundedImageView mRivEmoji;
        private TextView mTvMoodValue;
        private TextView mTvActiveTitle;
        private ConstraintLayout mLlActive;
        private TextView mTvTodayActivityTitle;
        private TextView mTvSumActivityTitle;
        private TextView mTvTodayActivityValue;
        private TextView mTvSumActivityValue;
        private ConstraintLayout mRootHotline;
        private TextView mTvTitle;
        private TextView mTvContent;
        private LinearLayout mLlMore;

        public MadeHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            initView(itemView);
            mRootHotline.setOnClickListener(v -> {
                mContext.startActivity(new Intent(mContext, HotPhoneActivity.class));
            });
            mClLeft.setOnClickListener(v -> {
                ChatActivity.startActivity(mContext, new TalkModel(TalkModel.TYPE_AI));
            });

            mLlActive.setOnClickListener(v -> {
                MoodDetailActivity.startActivity(mContext);
            });

            mLlMood.setOnClickListener(v -> {
                mContext.startActivity(new Intent(mContext, StatusActivity.class));
            });
            mTvMoodTitle.setOnClickListener(v -> {
                mContext.startActivity(new Intent(mContext, StatusActivity.class));
            });

        }

        private void initView(View itemView) {
            mClLeft = (ConstraintLayout) itemView.findViewById(R.id.cl_left);
            mClRightTop = (ConstraintLayout) itemView.findViewById(R.id.cl_right_top);
            mTvMoodTitle = (TextView) itemView.findViewById(R.id.tv_mood_title);
            mLlMood = (LinearLayout) itemView.findViewById(R.id.ll_mood);
            mRivEmoji = (RoundedImageView) itemView.findViewById(R.id.riv_emoji);
            mTvMoodValue = (TextView) itemView.findViewById(R.id.tv_mood_value);
            mTvActiveTitle = (TextView) itemView.findViewById(R.id.tv_active_title);
            mLlActive = (ConstraintLayout) itemView.findViewById(R.id.ll_active);
            mTvTodayActivityTitle = (TextView) itemView.findViewById(R.id.tv_today_activity_title);
            mTvSumActivityTitle = (TextView) itemView.findViewById(R.id.tv_sum_activity_title);
            mTvTodayActivityValue = (TextView) itemView.findViewById(R.id.tv_today_activity_value);
            mTvSumActivityValue = (TextView) itemView.findViewById(R.id.tv_sum_activity_value);
            mRootHotline = (ConstraintLayout) itemView.findViewById(R.id.root_hotline);
            mTvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            mTvContent = (TextView) itemView.findViewById(R.id.tv_content);
            mLlMore = (LinearLayout) itemView.findViewById(R.id.ll_more);
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
            return new HomeHistoryHolder(inflate(R.layout.item_home_history_children_layout, parent));
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
