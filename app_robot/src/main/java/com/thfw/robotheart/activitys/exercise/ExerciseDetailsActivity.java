package com.thfw.robotheart.activitys.exercise;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.base.api.HistoryApi;
import com.thfw.base.face.OnRvItemListener;
import com.thfw.base.models.CommonModel;
import com.thfw.base.models.ExerciseModel;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.HistoryPresenter;
import com.thfw.base.presenter.UserToolPresenter;
import com.thfw.base.utils.ToastUtil;
import com.thfw.robotheart.R;
import com.thfw.robotheart.adapter.ExerciseLogcateAdapter;
import com.thfw.robotheart.constants.UIConfig;
import com.thfw.robotheart.view.TitleRobotView;
import com.thfw.ui.base.RobotBaseActivity;
import com.thfw.ui.utils.GlideUtil;
import com.thfw.ui.widget.LoadingView;
import com.trello.rxlifecycle2.LifecycleProvider;

import java.util.List;

/**
 * 工具包/成长训练 详情
 */
public class ExerciseDetailsActivity extends RobotBaseActivity<UserToolPresenter> implements UserToolPresenter.UserToolUi<ExerciseModel> {

    public static final String KEY_ID = "key.id";
    private android.widget.ImageView mIvBg;
    private com.thfw.robotheart.view.TitleRobotView mTitleRobotView;
    private android.widget.TextView mTvVideoTitle;
    private android.widget.TextView mTvVideoContent;
    private android.widget.LinearLayout mLlLike;
    private android.widget.TextView mTvLikeTitle;
    private androidx.recyclerview.widget.RecyclerView mRvLike;
    private androidx.constraintlayout.widget.ConstraintLayout mClBg;
    private ExerciseLogcateAdapter mLogcateAdapter;
    private int mId = -1;
    private ExerciseModel mSimpleModel;
    private com.thfw.ui.widget.LoadingView mLoadingView;
    private boolean requestIng;
    private LinearLayout mLlCollect;
    private ImageView mIvCollect;
    private TextView mTvCollect;

    public static void startActivity(Context context, ExerciseModel exerciseModel) {
        context.startActivity(new Intent(context, ExerciseDetailsActivity.class).putExtra(KEY_DATA, exerciseModel));
    }

    public static void startActivity(Context context, int id) {
        context.startActivity(new Intent(context, ExerciseDetailsActivity.class).putExtra(KEY_ID, id));
    }

    @Override
    public int getContentView() {
        return R.layout.activity_exercise_details;
    }

    @Override
    public UserToolPresenter onCreatePresenter() {
        return new UserToolPresenter(this);
    }

    @Override
    public void initView() {

        mLoadingView = (LoadingView) findViewById(R.id.loadingView);
        mIvBg = (ImageView) findViewById(R.id.iv_bg);
        mTitleRobotView = (TitleRobotView) findViewById(R.id.titleRobotView);
        mTvVideoTitle = (TextView) findViewById(R.id.tv_video_title);
        mTvVideoContent = (TextView) findViewById(R.id.tv_video_content);
        mLlLike = (LinearLayout) findViewById(R.id.ll_like);
        mTvLikeTitle = (TextView) findViewById(R.id.tv_like_title);
        mRvLike = (RecyclerView) findViewById(R.id.rv_like);
        mClBg = (ConstraintLayout) findViewById(R.id.cl_bg);

        if (getIntent().hasExtra(KEY_ID)) {
            mId = getIntent().getIntExtra(KEY_ID, -1);
        }

        if (getIntent().hasExtra(KEY_DATA)) {
            mSimpleModel = (ExerciseModel) getIntent().getSerializableExtra(KEY_DATA);
            mId = mSimpleModel.getId();
        }

        if (mId == -1) {
            ToastUtil.show("参数错误");
            finish();
            return;
        }
        mLlCollect = (LinearLayout) findViewById(R.id.ll_collect);
        mIvCollect = (ImageView) findViewById(R.id.iv_collect);
        mTvCollect = (TextView) findViewById(R.id.tv_collect);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.onGetInfo(mId);
    }

    @Override
    public void initData() {
        mRvLike.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mLogcateAdapter = new ExerciseLogcateAdapter(null);
        mLogcateAdapter.setOnRvItemListener(new OnRvItemListener<ExerciseModel.LinkModel>() {
            @Override
            public void onItemClick(List<ExerciseModel.LinkModel> list, int position) {
                if (list.get(position).getStatus() == -1) {
                    ToastUtil.show("需要按顺序完成后，方可解锁");
                    return;
                }
                ExerciseIngActivity.startActivity(mContext, list.get(position).getDialogId(),
                        list.get(position).isUsed());
            }
        });
        mRvLike.setAdapter(mLogcateAdapter);
    }

    public void addCollect() {
        if (requestIng) {
            return;
        }
        requestIng = true;
        mIvCollect.setSelected(!mIvCollect.isSelected());
        new HistoryPresenter(new HistoryPresenter.HistoryUi<CommonModel>() {
            @Override
            public LifecycleProvider getLifecycleProvider() {
                return ExerciseDetailsActivity.this;
            }

            @Override
            public void onSuccess(CommonModel data) {
                requestIng = false;
                ToastUtil.show(mIvCollect.isSelected() ? UIConfig.COLLECTED : UIConfig.COLLECTED_UN);
            }

            @Override
            public void onFail(ResponeThrowable throwable) {
                requestIng = false;
                ToastUtil.show(mIvCollect.isSelected() ? "收藏失败" : "取消收藏失败");
                mIvCollect.setSelected(!mIvCollect.isSelected());
            }
        }).addCollect(HistoryApi.TYPE_COLLECT_TOOL, mId);
    }

    @Override
    public LifecycleProvider getLifecycleProvider() {
        return this;
    }

    @Override
    public void onSuccess(ExerciseModel data) {
        GlideUtil.load(mContext, data.getPic(), mIvBg);
        mTvVideoTitle.setText(data.getTitle());
        String hint = "<font color='" + UIConfig.COLOR_GRREN + "'>" + "简介：" + "</font>" + data.getDesc();
        mTvVideoContent.setText(Html.fromHtml(hint));
        mTvLikeTitle.setText(data.getTitle() + data.getLinkList().size() + "课时");
        mLogcateAdapter.setDataListNotify(data.getLinkList());
        mIvCollect.setSelected(data.getCollected() == 1);
        mLlCollect.setOnClickListener(v -> {
            addCollect();
        });
        mLoadingView.hide();
    }

    @Override
    public void onFail(ResponeThrowable throwable) {
        mLoadingView.showFail(v -> {
            mPresenter.onGetInfo(mId);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == ExerciseIngActivity.REQUEST_CODE) {
            mPresenter.onGetInfo(mId);
        }
    }
}