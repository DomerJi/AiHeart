package com.thfw.mobileheart.adapter;

import android.content.Intent;
import android.os.Build;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.opensource.svgaplayer.SVGAImageView;
import com.thfw.base.models.HomeEntity;
import com.thfw.base.models.HomeHistoryEntity;
import com.thfw.base.models.MoodLivelyModel;
import com.thfw.base.models.PageStateModel;
import com.thfw.base.models.TalkModel;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.base.utils.FunctionType;
import com.thfw.base.utils.LogUtil;
import com.thfw.mobileheart.MyApplication;
import com.thfw.mobileheart.R;
import com.thfw.mobileheart.activity.audio.AudioHomeActivity;
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
import com.thfw.mobileheart.constants.AnimFileName;
import com.thfw.mobileheart.push.helper.PushHandle;
import com.thfw.mobileheart.util.DialogFactory;
import com.thfw.mobileheart.util.FunctionDurationUtil;
import com.thfw.mobileheart.util.MoodLivelyHelper;
import com.thfw.ui.utils.GlideUtil;
import com.thfw.ui.utils.PageStateViewModel;
import com.thfw.user.login.UserManager;
import com.youth.banner.Banner;
import com.youth.banner.indicator.RectangleIndicator;
import com.youth.banner.listener.OnBannerListener;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Author:pengs
 * Date: 2021/7/22 10:21
 * Describe:首页适配器
 */
public class HomeAdapter extends BaseAdapter<HomeEntity, RecyclerView.ViewHolder> implements MyApplication.OnMinuteListener, MoodLivelyHelper.MoodLivelyListener {
    private static final long DELAY_TIME_BANNER = 5000;
    private PageStateViewModel pageStateViewModel;


    public HomeAdapter(List<HomeEntity> dataList) {
        super(dataList);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull @NotNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        pageStateViewModel = new ViewModelProvider((AppCompatActivity) mContext).get(PageStateViewModel.class);
        pageStateViewModel.getPageStateLive().observe((AppCompatActivity) mContext, new Observer<PageStateModel>() {
            @Override
            public void onChanged(PageStateModel pageStateModel) {

                RecyclerView.ViewHolder viewHolderSort = mRecyclerView.findViewHolderForLayoutPosition(1);
                if (viewHolderSort instanceof SortHolder) {
                    ((SortHolder) viewHolderSort).setData();
                }
                RecyclerView.ViewHolder viewHolderMade = mRecyclerView.findViewHolderForLayoutPosition(2);
                if (viewHolderMade instanceof MadeHolder) {
                    ((MadeHolder) viewHolderMade).setFlag();
                }
            }
        });
    }

    public void setBanner(boolean resume) {
        if (resume) {
            onChanged();
        }
        RecyclerView.ViewHolder viewHolder = mRecyclerView.findViewHolderForLayoutPosition(0);
        if (viewHolder instanceof BannerHolder) {
            BannerHolder bannerHolder = (BannerHolder) viewHolder;
            if (bannerHolder.mBanner != null) {
                if (resume) {
                    bannerHolder.mBanner.start();
                } else {
                    bannerHolder.mBanner.stop();
                }
            }
        }
    }

    public void notifyBanner() {
        RecyclerView.ViewHolder viewHolder = mRecyclerView.findViewHolderForLayoutPosition(0);
        if (viewHolder instanceof BannerHolder) {
            BannerHolder bannerHolder = (BannerHolder) viewHolder;
            if (bannerHolder.mBanner != null && bannerHolder.mBanner.getAdapter() != null) {
                bannerHolder.mBanner.setDatas(mDataList.get(0).bannerModels);
            }
        }
    }

    @Override
    public void onMoodLively(MoodLivelyModel data) {
        LogUtil.i("onMoodLively 01");
        if (getItemCount() > 2) {
            RecyclerView.ViewHolder viewHolder = mRecyclerView.findViewHolderForLayoutPosition(2);
            LogUtil.i("onMoodLively 02");
            if (viewHolder instanceof MadeHolder) {
                LogUtil.i("onMoodLively 03");
                ((MadeHolder) viewHolder).setMood(data);
                ((MadeHolder) viewHolder).notifyTodayTime();
            }
        }
    }

    /**
     * 时间更新/活跃时长
     */
    @Override
    public void onChanged() {
        LogUtil.d("===========================时间更新/活跃时长================================");
        if (getItemCount() > 2) {
            RecyclerView.ViewHolder viewHolder = mRecyclerView.findViewHolderForLayoutPosition(2);
            if (viewHolder instanceof MadeHolder) {
                ((MadeHolder) viewHolder).notifyTodayTime();
            }
        }

    }

    public int getBannerHeight() {
        RecyclerView.ViewHolder viewHolder = mRecyclerView.findViewHolderForLayoutPosition(0);
        if (viewHolder instanceof BannerHolder) {
            BannerHolder bannerHolder = (BannerHolder) viewHolder;
            return bannerHolder.mBanner == null ? 0 : bannerHolder.mBanner.getMeasuredHeight();
        } else {
            return 0;
        }
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
    public void onViewAttachedToWindow(@NonNull @NotNull RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (holder instanceof MadeHolder) {
            MoodLivelyHelper.addListener(this);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        HomeEntity entity = mDataList.get(position);
        switch (entity.type) {
            case HomeEntity.TYPE_BODY:
                BodyHolder bodyHolder = (BodyHolder) holder;
                bodyHolder.mTvTitle.setText(entity.recommendModel.getTitle());
                GlideUtil.loadThumbnail(mContext, entity.recommendModel.getPic(), bodyHolder.mRivAvatar);
                bodyHolder.mTvType.setText(entity.recommendModel.getTypeStr());
                break;
            case HomeEntity.TYPE_BANNER:
                BannerHolder bannerHolder = (BannerHolder) holder;
                bannerHolder.initData(entity);
                break;
            case HomeEntity.TYPE_CUSTOM_MADE:
                MadeHolder madeHolder = (MadeHolder) holder;
                madeHolder.notifyTodayTime();
                madeHolder.setMood(MoodLivelyHelper.getModel());
                madeHolder.setFlag();
                break;
            case HomeEntity.TYPE_TAB_TITLE:
                TabTitleHolder tabTitleHolder = (TabTitleHolder) holder;
                tabTitleHolder.mTvTitle.setText(mDataList.get(position).tabTitle);
                break;
            case HomeEntity.TYPE_SORT:
                SortHolder sortHolder = (SortHolder) holder;
                sortHolder.setData();
                break;
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

                SparseArray<String> sparseArray = new SparseArray<>();

                @Override
                public void onClick(View v) {
                    if (sparseArray.size() == 0) {
                        sparseArray.put(R.id.cl_tab_01, AnimFileName.TRANSITION_THEME);
                        sparseArray.put(R.id.rl_tab_03, AnimFileName.TRANSITION_TEST);
                        sparseArray.put(R.id.rl_tab_04, AnimFileName.TRANSITION_TOOL);
                        sparseArray.put(R.id.rl_tab_05, AnimFileName.TRANSITION_AUDIO);
                        sparseArray.put(R.id.rl_tab_06, AnimFileName.TRANSITION_VIDEO);
                        sparseArray.put(R.id.rl_tab_07, AnimFileName.TRANSITION_BOOK);
                        sparseArray.put(R.id.rl_tab_08, AnimFileName.TRANSITION_IDEO);
                    }
                    DialogFactory.createSvgaDialog((FragmentActivity) mContext, sparseArray.get(v.getId()), new DialogFactory.OnSVGACallBack() {
                        @Override
                        public void callBack(SVGAImageView svgaImageView) {
                            switch (v.getId()) {
                                case R.id.cl_tab_01:
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
                    });

                }
            };

            itemView.findViewById(R.id.cl_tab_01).setOnClickListener(clickViewListener);
            itemView.findViewById(R.id.rl_tab_03).setOnClickListener(clickViewListener);
            itemView.findViewById(R.id.rl_tab_04).setOnClickListener(clickViewListener);
            itemView.findViewById(R.id.rl_tab_05).setOnClickListener(clickViewListener);
            itemView.findViewById(R.id.rl_tab_06).setOnClickListener(clickViewListener);
            itemView.findViewById(R.id.rl_tab_07).setOnClickListener(clickViewListener);
            itemView.findViewById(R.id.rl_tab_08).setOnClickListener(clickViewListener);
            setData();
        }

        public void setData() {
            boolean showFlag = (pageStateViewModel == null || pageStateViewModel.getPageStateLive().getValue() == null || !pageStateViewModel.getPageStateLive().getValue().isHideRedFlag());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                int[] handleRedIds = new int[]{R.id.iv_tab_04, R.id.iv_tab_06, R.id.iv_tab_08};
                try {
                    if (showFlag) {
                        for (int id : handleRedIds) {
                            itemView.findViewById(id).setForeground(mContext.getResources().getDrawable(R.mipmap.ic_red_flag_small));
                        }
                    } else {
                        for (int id : handleRedIds) {
                            itemView.findViewById(id).setForeground(null);
                        }
                    }
                } catch (Exception e) {

                }
            }
        }
    }

    public class MadeHolder extends RecyclerView.ViewHolder {

        private ConstraintLayout mClLeft;
        private ConstraintLayout mClRightTop;
        private TextView mTvMoodTitle;
        private LinearLayout mLlMood;
        private ImageView mRivEmoji;
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
        private ImageView mRivRedFlag;

        public MadeHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            initView(itemView);
            mRootHotline.setOnClickListener(v -> {
                mContext.startActivity(new Intent(mContext, HotPhoneActivity.class));
            });
            mClLeft.setOnClickListener(v -> {
                if (mContext instanceof FragmentActivity) {
                    DialogFactory.createSvgaDialog((FragmentActivity) mContext, AnimFileName.TRANSITION_TALK, new DialogFactory.OnSVGACallBack() {
                        @Override
                        public void callBack(SVGAImageView svgaImageView) {
                            ChatActivity.startActivity(mContext, new TalkModel(TalkModel.TYPE_AI));
                        }
                    });
                }
            });

            mLlActive.setOnClickListener(v -> {
                MoodDetailActivity.startActivity(mContext);
            });

            mTvActiveTitle.setOnClickListener(v -> {
                MoodDetailActivity.startActivity(mContext);
            });

            mLlMood.setOnClickListener(v -> {
                StatusActivity.startActivity(mContext);
            });
            mTvMoodTitle.setOnClickListener(v -> {
                StatusActivity.startActivity(mContext);
            });

        }

        public void setFlag() {
            boolean showFlag = (pageStateViewModel == null || pageStateViewModel.getPageStateLive().getValue() == null || !pageStateViewModel.getPageStateLive().getValue().isHideRedFlag());
            mRivRedFlag.setVisibility(showFlag ? View.VISIBLE : View.GONE);
        }

        public void setMood(MoodLivelyModel mood) {

            if (mTvMoodValue == null || mRivEmoji == null) {
                return;
            }

            LogUtil.i("onMoodLively 04");
            if (UserManager.getInstance().isTrueLogin()) {
                if (mood != null) {
                    if (mood.getUserMood() != null) {
                        if (!EmptyUtil.isEmpty(mContext)) {
                            GlideUtil.load(mContext, mood.getUserMood().getPath(), mRivEmoji);
                        }
                        mTvMoodValue.setText(mood.getUserMood().getName());
                    }
                    mTvSumActivityValue.setText(String.valueOf(mood.getLoginDays()));
                    LogUtil.i("onMoodLively 06");
                } else {
                    LogUtil.i("onMoodLively 07");
                    mTvSumActivityValue.setText("");
                    if (!EmptyUtil.isEmpty(mContext)) {
                        GlideUtil.load(mContext, R.drawable.gray_cirlle_bg, mRivEmoji);
                    }
                    mTvMoodValue.setText(mContext.getResources().getString(R.string.mood_defalut_hint));
                }
            } else {
                mTvSumActivityValue.setText("");
                if (!EmptyUtil.isEmpty(mContext)) {
                    GlideUtil.load(mContext, R.drawable.gray_cirlle_bg, mRivEmoji);
                }
                mTvMoodValue.setText(mContext.getResources().getString(R.string.mood_defalut_hint));
            }

        }


        public void notifyTodayTime() {
            if (UserManager.getInstance().isTrueLogin()) {
                if (mTvTodayActivityValue != null) {
                    mTvTodayActivityValue.setText(FunctionDurationUtil.getFunctionTimeHour(FunctionType.FUNCTION_APP));
                }
                LogUtil.i("onMoodLively 05");
            } else {
                if (mTvTodayActivityValue != null) {
                    mTvTodayActivityValue.setText("");
                }
            }

        }

        private void initView(View itemView) {
            mClLeft = (ConstraintLayout) itemView.findViewById(R.id.cl_left);
            mClRightTop = (ConstraintLayout) itemView.findViewById(R.id.cl_right_top);
            mTvMoodTitle = (TextView) itemView.findViewById(R.id.tv_mood_title);
            mLlMood = (LinearLayout) itemView.findViewById(R.id.ll_mood);
            mRivEmoji = (ImageView) itemView.findViewById(R.id.iv_emoji);
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
            mRivRedFlag = itemView.findViewById(R.id.riv_red_flag);
        }
    }

    public class BannerHolder extends RecyclerView.ViewHolder {

        private Banner mBanner;

        public BannerHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            mBanner = itemView.findViewById(R.id.banner);
            mBanner.setIndicator(new RectangleIndicator(mContext));
            mBanner.setLoopTime(DELAY_TIME_BANNER);
        }

        public void initData(HomeEntity homeEntity) {
            if (mBanner.getAdapter() != null) {
                return;
            }
            // mBanner.addPageTransformer(new ScaleInTransformer());
            mBanner.setAdapter(new ImageAdapter(homeEntity.bannerModels));
            mBanner.setOnBannerListener(new OnBannerListener<HomeEntity.BannerModel>() {

                @Override
                public void OnBannerClick(HomeEntity.BannerModel bannerModel, int position) {
                    LogUtil.d("OnBannerClick", "position = " + position);
                    PushHandle.handleMessage(mContext, bannerModel);
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
