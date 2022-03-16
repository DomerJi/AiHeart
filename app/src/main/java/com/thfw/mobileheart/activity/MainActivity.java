package com.thfw.mobileheart.activity;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.thfw.base.base.IPresenter;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.mobileheart.R;
import com.thfw.mobileheart.activity.login.LoginActivity;
import com.thfw.mobileheart.activity.robot.RobotActivity;
import com.thfw.mobileheart.fragment.HomeFragment;
import com.thfw.mobileheart.fragment.MeFragment;
import com.thfw.mobileheart.fragment.MessageFragment;
import com.thfw.mobileheart.util.FragmentLoader;
import com.thfw.mobileheart.view.SimpleAnimatorListener;
import com.thfw.ui.base.BaseActivity;
import com.thfw.user.login.UserManager;


/**
 * 首页
 * Created By jishuaipeng on 2021/7/02
 */
public class MainActivity extends BaseActivity implements Animator.AnimatorListener {

    private Handler handler;
    private androidx.constraintlayout.widget.ConstraintLayout mMainRoot;
    private androidx.constraintlayout.widget.ConstraintLayout mMainRoot2;

    private android.widget.LinearLayout mLlHome;
    private android.widget.ImageView mIvHome;
    private android.widget.TextView mTvHome;

    private android.widget.LinearLayout mLlMessage;
    private android.widget.ImageView mIvMessage;
    private android.widget.TextView mTvMessage;

    private android.widget.LinearLayout mLlMe;
    private android.widget.ImageView mIvMe;
    private android.widget.TextView mTvMe;
    private FragmentLoader mFragmentLoader;
    private View mCurrent;
    private LinearLayout mLlAiChat;

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, MainActivity.class));
    }

    @Override
    public int getContentView() {
        return R.layout.activity_home;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initView() {

        mMainRoot = (ConstraintLayout) findViewById(R.id.main_root);
        mMainRoot2 = (ConstraintLayout) findViewById(R.id.main_root2);

        mLlHome = (LinearLayout) findViewById(R.id.ll_home);
        mIvHome = (ImageView) findViewById(R.id.iv_home);
        mTvHome = (TextView) findViewById(R.id.tv_home);

        mLlAiChat = (LinearLayout) findViewById(R.id.ll_ai_chat);
        mLlMessage = (LinearLayout) findViewById(R.id.ll_message);
        mIvMessage = (ImageView) findViewById(R.id.iv_message);
        mTvMessage = (TextView) findViewById(R.id.tv_message);

        mLlMe = (LinearLayout) findViewById(R.id.ll_me);
        mIvMe = (ImageView) findViewById(R.id.iv_me);
        mTvMe = (TextView) findViewById(R.id.tv_me);
        // 小天智能聊天
        mLlAiChat.setOnClickListener(v -> {
//            ChatActivity.startActivity(mContext);
            startActivity(new Intent(mContext, RobotActivity.class));
        });

//            startActivity(new Intent(mContext, ExoPlayerActivity.class));
//            startActivity(new Intent(mContext, TestActivity.class));
//            startActivity(new Intent(mContext, ChatActivity.class));
    }

    @Override
    public void initData() {

        mFragmentLoader = new FragmentLoader(getSupportFragmentManager(), R.id.fl_content)
                .add(mLlHome.getId(), new HomeFragment())
                .add(mLlMessage.getId(), new MessageFragment())
                .add(mLlMe.getId(), new MeFragment());


        View.OnClickListener mOnTabListener = v -> {
            if (mCurrent == v) {
                return;
            }
            mFragmentLoader.load(v.getId());
            setTab((LinearLayout) v, true);
            setTab((LinearLayout) mCurrent, false);
            mCurrent = v;
        };
        mLlHome.setOnClickListener(mOnTabListener);
        mLlMe.setOnClickListener(mOnTabListener);
        mLlMessage.setOnClickListener(mOnTabListener);
        mLlHome.performClick();
    }

    private void setTab(LinearLayout layout, boolean selected) {
        if (layout != null) {
            int childCount = layout.getChildCount();
            if (childCount > 0) {
                for (int i = 0; i < childCount; i++) {
                    View view = layout.getChildAt(i);
                    if (view != null) {
                        view.setSelected(selected);
                    }
                }
            }
        }
    }

    @Override
    public int getStatusBarColor() {
        return STATUSBAR_TRANSPARENT;
    }

    private void showMain() {
        if (mMainRoot.getVisibility() != View.VISIBLE) {
            // 淡入
            mMainRoot.setAlpha(0f);
            mMainRoot.setVisibility(View.VISIBLE);
            mMainRoot.animate().alpha(1f).setDuration(400).setListener(new SimpleAnimatorListener() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    if (EmptyUtil.isEmpty(MainActivity.this)) {
                        return;
                    }

                    mMainRoot2.setAlpha(0f);
                    mMainRoot2.setVisibility(View.VISIBLE);
                    mMainRoot2.animate().alpha(1f).setDuration(400).setListener(new SimpleAnimatorListener() {

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            if (EmptyUtil.isEmpty(MainActivity.this)) {
                                return;
                            }
                            mMainRoot.setBackgroundColor(getResources().getColor(R.color.page_background_gray));
                        }
                    }).setStartDelay(1200);
                }

            }).setStartDelay(700);

        }
    }


    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                showMain();
            }
        };
        handler.sendEmptyMessageDelayed(0, 300);

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
            if (mMainRoot.getVisibility() != View.VISIBLE) {
                finish();
                return;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!UserManager.getInstance().isLogin()) {
            LoginActivity.startActivity(mContext, LoginActivity.BY_PASSWORD);
        } else {

        }
    }
}
