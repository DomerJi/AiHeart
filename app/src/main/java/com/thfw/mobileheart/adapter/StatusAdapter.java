package com.thfw.mobileheart.adapter;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.thfw.base.models.StatusEntity;
import com.thfw.base.utils.Util;
import com.thfw.mobileheart.R;
import com.thfw.ui.utils.GlideUtil;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Author:pengs
 * Date: 2021/7/29 15:22
 * Describe:选择状态列表适配
 */
public class StatusAdapter extends BaseAdapter<StatusEntity, RecyclerView.ViewHolder> {

    private View mTopItemView;
    private ImageView mIvTopBanner;

    private int mSelectedIndex = -1;
    private int oldIndex = -1;

    public StatusAdapter(List<StatusEntity> dataList) {
        super(dataList);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {

            boolean topPull;
            float topPullDownY = 0;
            int originHeight = 0;

            float downY = 0;
            long downTime;
            boolean noMove = true;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        boolean flag = !mRecyclerView.canScrollVertically(-1);
                        if (flag != topPull) {
                            topPull = flag;
                        }

                        downTime = System.currentTimeMillis();
                        noMove = true;
                        downY = topPullDownY = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        boolean flag1 = !mRecyclerView.canScrollVertically(-1);
                        if (flag1 != topPull) {
                            topPull = flag1;
                            topPullDownY = event.getY();
                        }

                        int moveY = Math.round(event.getY() - downY);
                        if (noMove && Math.abs(moveY) > 25) {
                            noMove = false;
                        }

                        if (!topPull) {
                            return false;
                        }
                        if (event.getY() < topPullDownY) {
                            return false;
                        }
                        if (originHeight == 0) {
                            originHeight = getTopBanner();
                        }
                        if (originHeight == 0 || mIvTopBanner == null) {
                            return false;
                        }

                        Log.i("topPullDownY", " originHeight = " + originHeight + " ; topPullDownY = " + topPullDownY + " ; event.getY() " + event.getY());
                        int newHeight = (int) (originHeight + (event.getY() - topPullDownY));
                        if (newHeight < originHeight) {
                            return false;
                        }
                        mIvTopBanner.getLayoutParams().height = newHeight;
                        mIvTopBanner.setLayoutParams(mIvTopBanner.getLayoutParams());
                        return true;

                    case MotionEvent.ACTION_UP:
                        clickItem(event);
                        if (originHeight == 0 || mIvTopBanner == null || mIvTopBanner.getLayoutParams().height < originHeight) {
                            return false;
                        }
                        ValueAnimator animator = ObjectAnimator.ofInt(mIvTopBanner.getLayoutParams().height, originHeight);
                        animator.setDuration(300);
                        animator.setInterpolator(new DecelerateInterpolator());
                        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                int height = (int) animation.getAnimatedValue();
                                mIvTopBanner.getLayoutParams().height = height;
                                mIvTopBanner.setLayoutParams(mIvTopBanner.getLayoutParams());
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
                    View childView = mRecyclerView.findChildViewUnder(event.getX(), event.getY());
                    Log.d("TvDrag", "childView = " + childView);
                    if (childView != null) {
                        RecyclerView.ViewHolder viewHolder = mRecyclerView.findContainingViewHolder(childView);
                        if (viewHolder != null) {
                            Log.d("TvDrag", "viewHolder = " + viewHolder);
                            onItemCLick(viewHolder.getBindingAdapterPosition());
                            Log.d("TvDrag", "position = " + viewHolder.getBindingAdapterPosition());
                        }
                    }

                }
            }
        });
    }

    private void onItemCLick(int bindingAdapterPosition) {
        oldIndex = mSelectedIndex;
        mSelectedIndex = bindingAdapterPosition;
        if (oldIndex != -1) {
            notifyItemChanged(oldIndex);
        }
        notifyItemChanged(mSelectedIndex);
        if (mOnRvItemListener != null) {
            mOnRvItemListener.onItemClick(getDataList(), mSelectedIndex);
        }
    }

    public void setSelectedIndex(int mSelectedIndex) {
        this.mSelectedIndex = mSelectedIndex;
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case StatusEntity.TYPE_BODY:
                return new StatusHolder(LayoutInflater.from(mContext).inflate(R.layout.item_status_body, parent, false));
            case StatusEntity.TYPE_GROUP:
                return new StatusGroupHolder(LayoutInflater.from(mContext).inflate(R.layout.item_status_group, parent, false));
            default:
                return new StatusTopHolder(LayoutInflater.from(mContext).inflate(R.layout.item_status_top, parent, false));
        }
    }


    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        StatusEntity statusEntity = mDataList.get(position);
        switch (statusEntity.type) {
            case StatusEntity.TYPE_BODY:
                int seat = mDataList.get(position).bodyPosition % 3;
                StatusHolder statusHolder = (StatusHolder) holder;
                statusHolder.mClMood.setSelected(position == mSelectedIndex);
                if (seat == 0) {
                    Util.setViewMargin(holder.itemView, true, 15, 5, 8, 8);
                } else if (seat == 1) {
                    Util.setViewMargin(holder.itemView, true, 10, 10, 8, 8);
                } else {
                    Util.setViewMargin(holder.itemView, true, 5, 15, 8, 8);
                }
                statusHolder.mTvStatus.setText(statusEntity.moodModel.getName());
                GlideUtil.load(mContext, statusEntity.moodModel.getPath(), ((StatusHolder) holder).mRivStatus);
                break;
            case StatusEntity.TYPE_GROUP:
                StatusGroupHolder groupHolder = (StatusGroupHolder) holder;
                groupHolder.mTvGroup.setText(statusEntity.tag);
                break;
            case StatusEntity.TYPE_TOP:
                break;
        }
    }

    public int getTopBanner() {
        return mIvTopBanner == null ? 0 : mIvTopBanner.getMeasuredHeight();
    }

    @Override
    public int getItemViewType(int position) {
        return mDataList.get(position).type;
    }

    public void backScroll() {
        mSelectedIndex = oldIndex;
        notifyDataSetChanged();
    }

    public class StatusTopHolder extends RecyclerView.ViewHolder {


        public StatusTopHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            mTopItemView = itemView;
            mIvTopBanner = itemView.findViewById(R.id.iv_top_banner);
        }
    }

    public class StatusGroupHolder extends RecyclerView.ViewHolder {

        private final TextView mTvGroup;


        public StatusGroupHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            mTvGroup = (TextView) itemView.findViewById(R.id.tv_group);
        }
    }

    public class StatusHolder extends RecyclerView.ViewHolder {


        private final ConstraintLayout mClMood;
        private RoundedImageView mRivStatus;
        private TextView mTvStatus;

        public StatusHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            mRivStatus = (RoundedImageView) itemView.findViewById(R.id.riv_status);
            mTvStatus = (TextView) itemView.findViewById(R.id.tv_status);
            mClMood = itemView.findViewById(R.id.cl_mood);
//            itemView.setOnClickListener(v -> {
//                oldIndex = mSelectedIndex;
//                mSelectedIndex = getBindingAdapterPosition();
//                if (oldIndex != -1) {
//                    notifyItemChanged(oldIndex);
//                }
//                notifyItemChanged(mSelectedIndex);
//                if (mOnRvItemListener != null) {
//                    mOnRvItemListener.onItemClick(getDataList(), mSelectedIndex);
//                }
//            });
        }
    }


}
