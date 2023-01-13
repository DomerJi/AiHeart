package com.thfw.mobileheart.activity.talk;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.base.face.OnRvItemListener;
import com.thfw.base.models.TalkModel;
import com.thfw.base.models.ThemeTalkModel;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.TalkPresenter;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.PaletteUtil;
import com.thfw.mobileheart.R;
import com.thfw.mobileheart.activity.BaseActivity;
import com.thfw.mobileheart.adapter.ThemeTalkAdapter;
import com.thfw.ui.widget.LoadingView;
import com.thfw.ui.widget.TitleView;
import com.trello.rxlifecycle2.LifecycleProvider;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ThemeListActivity extends BaseActivity<TalkPresenter> implements TalkPresenter.TalkUi<List<ThemeTalkModel>> {

    private com.thfw.ui.widget.TitleView mTitleView;
    private androidx.recyclerview.widget.RecyclerView mRvList;
    private com.thfw.ui.widget.LoadingView mLoadingView;
    private ThemeTalkAdapter talkAdapter;
    private ConstraintLayout mClTalkTop;
    private int bannerHeight;
    private int bannerWidth;
    private ImageView mIvTalkBanner;
    private Bitmap blurBitmap;
    private int minHeight;
    private TextView mTvDeepTalk;
    private TextView mTvJustTalk;

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, ThemeListActivity.class));
    }

    @Override
    public int getContentView() {
        return R.layout.activity_theme_list;
    }

    @Override
    public TalkPresenter onCreatePresenter() {
        return new TalkPresenter(this);
    }

    @Override
    public void initView() {

        mTitleView = (TitleView) findViewById(R.id.titleView);
        mRvList = (RecyclerView) findViewById(R.id.rv_list);
        mRvList.setLayoutManager(new LinearLayoutManager(mContext));
        mLoadingView = (LoadingView) findViewById(R.id.loadingView);
        mIvTalkBanner = findViewById(R.id.iv_talk_banner);
        mClTalkTop = findViewById(R.id.i_talk_top);
        mTvDeepTalk = findViewById(R.id.tv_deep_talk);
        mTvJustTalk = findViewById(R.id.tv_just_talk);
    }

    @Override
    public void initData() {
        mPresenter.getDialogList();
        talkAdapter = new ThemeTalkAdapter(null);
        talkAdapter.setOnRvItemListener(new OnRvItemListener<ThemeTalkModel>() {
            @Override
            public void onItemClick(List<ThemeTalkModel> list, int position) {
                if (position == 0) {
                    // 主题对话
                    ChatActivity.startActivity(mContext, new TalkModel(TalkModel.TYPE_THEME));
                    return;
                }
                ChatActivity.startActivity(mContext, new TalkModel(TalkModel.TYPE_SPEECH_CRAFT).setId(list.get(position).getId()).setCollected(list.get(position).getCollected() == 1).setTitle(list.get(position).getTitle()));
            }
        });
        mRvList.setAdapter(talkAdapter);

    }

    @Override
    public LifecycleProvider getLifecycleProvider() {
        return ThemeListActivity.this;
    }

    @Override
    public void onSuccess(List<ThemeTalkModel> data) {
        if (EmptyUtil.isEmpty(data)) {
            mLoadingView.showEmpty();
        }
        mLoadingView.hide();
        ThemeTalkModel topModel = new ThemeTalkModel();
        topModel.setItemType(ThemeTalkModel.TYPE_TOP);
        data.add(0, topModel);
        talkAdapter.setDataListNotify(data);

        mClTalkTop.setVisibility(View.VISIBLE);
        mClTalkTop.post(new Runnable() {
            @Override
            public void run() {
                bannerHeight = mClTalkTop.getHeight();
                bannerWidth = mClTalkTop.getWidth();
            }
        });
        mRvList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int dyAll = 0;

            @Override
            public void onScrolled(@NonNull @NotNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                dyAll += dy;
                if (bannerHeight > 0) {
                    onScroll(dyAll);
                }
            }
        });
//        mClTalkTop.setOnClickListener(v -> {
//            // todo 主题对话
//            ChatActivity.startActivity(mContext, new TalkModel(TalkModel.TYPE_THEME));
//        });

        mRvList.setOnTouchListener(new View.OnTouchListener() {

            boolean topPull;
            float topPullDownY = 0;

            float downY = 0;
            long downTime;
            boolean noMove = true;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        boolean flag = !mRvList.canScrollVertically(-1);
                        if (flag != topPull) {
                            topPull = flag;
                        }

                        downTime = System.currentTimeMillis();
                        noMove = true;
                        downY = topPullDownY = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:

                        int moveY = Math.round(event.getY() - downY);
                        if (noMove && Math.abs(moveY) > 25) {
                            noMove = false;
                        }

                        boolean flag1 = !mRvList.canScrollVertically(-1);
                        if (flag1 != topPull) {
                            topPull = flag1;
                            topPullDownY = event.getY();

                            if (!topPull) {
                                Log.i("topPullDownY", "bannerHeight = " + bannerHeight);
                                mIvTalkBanner.getLayoutParams().height = bannerHeight;
                                mIvTalkBanner.setLayoutParams(mIvTalkBanner.getLayoutParams());
                                mClTalkTop.getLayoutParams().height = bannerHeight;
                                mClTalkTop.setLayoutParams(mClTalkTop.getLayoutParams());
                                talkAdapter.setTopBannerHeight(bannerHeight);
                            }
                        }

                        if (!topPull) {
                            return false;
                        }
                        if (event.getY() < topPullDownY) {
                            return false;
                        }
                        if (bannerHeight == 0 || mIvTalkBanner == null || mClTalkTop == null) {
                            return false;
                        }

                        Log.i("topPullDownY", " originHeight = " + bannerHeight + " ; topPullDownY = " + topPullDownY + " ; event.getY() " + event.getY());
                        int newHeight = (int) (bannerHeight + (event.getY() - topPullDownY));
                        if (newHeight < bannerHeight) {
                            return false;
                        }
                        mIvTalkBanner.getLayoutParams().height = newHeight;
                        mIvTalkBanner.setLayoutParams(mIvTalkBanner.getLayoutParams());
                        mClTalkTop.getLayoutParams().height = newHeight;
                        mClTalkTop.setLayoutParams(mClTalkTop.getLayoutParams());
                        talkAdapter.setTopBannerHeight(newHeight);
                        return true;

                    case MotionEvent.ACTION_UP:
                        clickItem(event);
                        if (bannerHeight == 0 || mIvTalkBanner == null || mIvTalkBanner.getLayoutParams().height <= bannerHeight) {
                            return false;
                        }
                        ValueAnimator animator = ObjectAnimator.ofInt(mIvTalkBanner.getLayoutParams().height, bannerHeight);
                        animator.setDuration(300);
                        animator.setInterpolator(new DecelerateInterpolator());
                        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                int height = (int) animation.getAnimatedValue();
                                mIvTalkBanner.getLayoutParams().height = height;
                                mIvTalkBanner.setLayoutParams(mIvTalkBanner.getLayoutParams());
                                mClTalkTop.getLayoutParams().height = height;
                                mClTalkTop.setLayoutParams(mClTalkTop.getLayoutParams());
                                talkAdapter.setTopBannerHeight(height);
                            }
                        });
                        animator.start();
                        break;
                }


                return false;
            }

            private void clickItem(MotionEvent event) {
                Log.d("TvDrag", "clickItem = ");
                if (noMove && Math.abs(event.getY() - downY) < 25 && System.currentTimeMillis() - downTime < 200) {
                    View childView = mRvList.findChildViewUnder(event.getX(), event.getY());
                    Log.d("TvDrag", "childView = " + childView);
                    if (childView != null) {
                        RecyclerView.ViewHolder viewHolder = mRvList.findContainingViewHolder(childView);
                        if (viewHolder != null) {
                            Log.d("TvDrag", "viewHolder = " + viewHolder);
                            talkAdapter.onItemCLick(viewHolder.getBindingAdapterPosition());
                            Log.d("TvDrag", "position = " + viewHolder.getBindingAdapterPosition());
                        }
                    }

                }
            }
        });

        minHeight = (int) getResources().getDimension(R.dimen.navigation_height);
    }


    /**
     * 顶部搜索导航区域背景渐变
     *
     * @param y
     */
    public void onScroll(int y) {

        int newHeight = bannerHeight - y;

        if (newHeight < minHeight) {
            newHeight = minHeight;
        } else if (newHeight > bannerHeight) {
            newHeight = bannerHeight;
        }


        float alpha = (newHeight - minHeight) * 1.0f / (bannerHeight - minHeight);
        LogUtil.d("onScroll alpha = " + alpha);
        if (blurBitmap == null) {
            blurBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_theme_banner);
        }
        mTvDeepTalk.setAlpha(alpha);
        if (newHeight < bannerHeight) {
            int radius = (int) (20 * (1 - alpha));
            if (radius < 4) {
                radius = 4;
            }
            LogUtil.d("onScroll radius = " + radius);
            mIvTalkBanner.setImageBitmap(PaletteUtil.doBlur(blurBitmap, radius, true));
        } else {
            mIvTalkBanner.setImageBitmap(blurBitmap);
        }
        ViewGroup.LayoutParams lp = mClTalkTop.getLayoutParams();
        lp.height = newHeight;
        mClTalkTop.setLayoutParams(lp);
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(mClTalkTop);

//        constraintSet.setHorizontalBias(R.id.tv_deep_talk, 0.2f + 0.7f * alpha);
        constraintSet.setVerticalBias(R.id.tv_deep_talk, 0.5f - 0.25f * alpha);

        constraintSet.setHorizontalBias(R.id.tv_just_talk, 0.5f + 0.4f * alpha);
        constraintSet.setVerticalBias(R.id.tv_just_talk, 0.5f + 0.1f * alpha);
        constraintSet.applyTo(mClTalkTop);

    }


    @Override
    public void onFail(ResponeThrowable throwable) {
        mLoadingView.showFail(v -> {
            mPresenter.getDialogList();
        });
    }

    @Override
    public void finish() {
        setResult(RESULT_OK);
        super.finish();
    }
}