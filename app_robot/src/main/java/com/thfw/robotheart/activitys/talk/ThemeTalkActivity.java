package com.thfw.robotheart.activitys.talk;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.opensource.svgaplayer.SVGAImageView;
import com.thfw.base.face.OnRvItemListener;
import com.thfw.base.models.TalkModel;
import com.thfw.base.models.ThemeTalkModel;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.TalkPresenter;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.base.utils.HandlerUtil;
import com.thfw.robotheart.R;
import com.thfw.robotheart.activitys.RobotBaseActivity;
import com.thfw.robotheart.adapter.ThemeTalkAdapter;
import com.thfw.robotheart.constants.AnimFileName;
import com.thfw.robotheart.util.DialogRobotFactory;
import com.thfw.robotheart.util.SVGAHelper;
import com.thfw.robotheart.view.HomeIpTextView;
import com.thfw.robotheart.view.TitleRobotView;
import com.thfw.ui.widget.LoadingView;
import com.trello.rxlifecycle2.LifecycleProvider;

import java.util.List;
import java.util.Random;

public class ThemeTalkActivity extends RobotBaseActivity<TalkPresenter> implements TalkPresenter.TalkUi<List<ThemeTalkModel>> {

    private com.thfw.robotheart.view.TitleRobotView mTitleRobotView;
    private androidx.constraintlayout.widget.ConstraintLayout mClTheme;
    private androidx.recyclerview.widget.RecyclerView mRvList;
    private ThemeTalkAdapter talkAdapter;
    private com.thfw.ui.widget.LoadingView mLoadingView;
    private com.opensource.svgaplayer.SVGAImageView mSvgaBody;
    private com.opensource.svgaplayer.SVGAImageView mSvgaFace;

    private Random random = new Random();
    private com.thfw.robotheart.view.HomeIpTextView mHitAnim;

    @Override
    public int getContentView() {
        return R.layout.activity_theme_talk;
    }

    @Override
    public TalkPresenter onCreatePresenter() {
        return new TalkPresenter(this);
    }

    @Override
    public void initView() {

        mTitleRobotView = (TitleRobotView) findViewById(R.id.titleRobotView);
        mClTheme = (ConstraintLayout) findViewById(R.id.cl_theme);
        mRvList = (RecyclerView) findViewById(R.id.rv_list);
        mRvList.setLayoutManager(new GridLayoutManager(mContext, 3));
        mLoadingView = (LoadingView) findViewById(R.id.loadingView);
        mSvgaBody = (SVGAImageView) findViewById(R.id.svga_body);
        mSvgaFace = (SVGAImageView) findViewById(R.id.svga_face);

        startFaceAnim();
        mHitAnim = (HomeIpTextView) findViewById(R.id.hit_anim);
    }

    private void startFaceAnim() {
        SVGAHelper.playSVGA(mSvgaFace, SVGAHelper.SVGAModel.create(AnimFileName.FACE_FACE).setLoopCount(1), new DialogRobotFactory.SimpleSVGACallBack() {
            @Override
            public void onFinished() {
                if (!isMeResumed()) {
                    return;
                }
                HandlerUtil.getMainHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (isMeResumed()) {
                            startFaceAnim();
                        }
                    }
                }, random.nextInt(AnimFileName.HOME_IP_ANIM_TIME));
            }
        });
    }

    @Override
    public void initData() {
        mPresenter.getDialogList();
        talkAdapter = new ThemeTalkAdapter(null);
        talkAdapter.setOnRvItemListener(new OnRvItemListener<ThemeTalkModel>() {
            @Override
            public void onItemClick(List<ThemeTalkModel> list, int position) {
                AiTalkActivity.startActivity(mContext, new TalkModel(TalkModel.TYPE_SPEECH_CRAFT)
                        .setId(list.get(position).getId())
                        .setCollected(list.get(position).getCollected() == 1)
                        .setTitle(list.get(position).getTitle()));
            }
        });
        mRvList.setAdapter(talkAdapter);
        mClTheme.setOnClickListener(v -> {
            AiTalkActivity.startActivity(mContext, new TalkModel(TalkModel.TYPE_THEME));
        });
    }

    @Override
    public LifecycleProvider getLifecycleProvider() {
        return ThemeTalkActivity.this;
    }

    @Override
    public void onSuccess(List<ThemeTalkModel> data) {
        if (EmptyUtil.isEmpty(data)) {
            mLoadingView.showEmpty();
        }
        mLoadingView.hide();
        talkAdapter.setDataListNotify(data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        animResume(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        animResume(false);
    }

    /**
     * 人物动画暂停/开始
     *
     * @param resume
     */
    private void animResume(boolean resume) {
        if (resume) {
            mHitAnim.resume();
            if (!mSvgaFace.isAnimating()) {
                mSvgaFace.startAnimation();
                if (!mSvgaFace.isAnimating()) {
                    startFaceAnim();
                }
            }
            if (!mSvgaBody.isAnimating()) {
                mSvgaBody.startAnimation();
            }
        } else {
            mHitAnim.pause();
            if (mSvgaFace.isAnimating()) {
                mSvgaFace.startAnimation();
            }
            if (mSvgaBody.isAnimating()) {
                mSvgaBody.stopAnimation();
            }
        }
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