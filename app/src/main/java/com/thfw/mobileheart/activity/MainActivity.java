package com.thfw.mobileheart.activity;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.thfw.base.base.IPresenter;
import com.thfw.base.models.OrganizationModel;
import com.thfw.base.models.OrganizationSelectedModel;
import com.thfw.base.net.CommonParameter;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.OrganizationPresenter;
import com.thfw.base.presenter.UserInfoPresenter;
import com.thfw.base.timing.TimingHelper;
import com.thfw.base.timing.WorkInt;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.SharePreferenceUtil;
import com.thfw.mobileheart.MyApplication;
import com.thfw.mobileheart.R;
import com.thfw.mobileheart.activity.login.LoginActivity;
import com.thfw.mobileheart.activity.robot.RobotActivity;
import com.thfw.mobileheart.fragment.HomeFragment;
import com.thfw.mobileheart.fragment.MeFragment;
import com.thfw.mobileheart.fragment.MessageFragment;
import com.thfw.mobileheart.push.MyPreferences;
import com.thfw.mobileheart.push.helper.PushHelper;
import com.thfw.mobileheart.push.tester.UPushAlias;
import com.thfw.mobileheart.util.FragmentLoader;
import com.thfw.mobileheart.view.SimpleAnimatorListener;
import com.thfw.ui.base.BaseActivity;
import com.thfw.ui.voice.tts.TtsHelper;
import com.thfw.ui.voice.tts.TtsModel;
import com.thfw.user.login.User;
import com.thfw.user.login.UserManager;
import com.thfw.user.login.UserObserver;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.umeng.message.PushAgent;
import com.umeng.message.api.UPushRegisterCallback;

import java.util.ArrayList;
import java.util.List;


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

    /**
     * 机构信息和用户信息初始化标识
     */
    private static boolean initUserInfo;
    private static boolean initOrganization;
    private static boolean initUmeng;

    /**
     * 重新登录后重新获取用户相关信息
     */
    public static void resetInit() {
        initUserInfo = false;
        initOrganization = false;
        initUmeng = false;
    }

    private static boolean hasAgreedAgreement() {
        return MyPreferences.getInstance(MyApplication.getApp()).hasAgreePrivacyAgreement();
    }


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

        if (UserManager.getInstance().isTrueLogin()) {
            // 已登录 初始化用户信息和机构信息
            initUmeng();
            initUserInfo();
            initOrganization();
        }
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


    /**
     * 用户信息更新观察者
     *
     * @return
     */
    @Override
    public UserObserver addObserver() {
        return new UserObserver() {
            @Override
            public void onChanged(UserManager accountManager, User user) {
                if (accountManager.isLogin()) {
                    // 已登录 初始化用户信息和机构信息
                    initUmeng();
                    initUserInfo();
                    initOrganization();
                }
            }
        };
    }

    /**
     * 查询组织信息
     */
    private void initOrganization() {
        if (initOrganization) {
            return;
        }

        new OrganizationPresenter(new OrganizationPresenter.OrganizationUi<OrganizationSelectedModel>() {

            @Override
            public LifecycleProvider getLifecycleProvider() {
                return MainActivity.this;
            }

            @Override
            public void onSuccess(OrganizationSelectedModel data) {
                if (data != null) {
                    LogUtil.d(TAG, "initOrganization onSuccess ++++++++++++++++++++++ ");
                    ArrayList<OrganizationModel.OrganizationBean> mSelecteds = new ArrayList<>();
                    initSelectedList(mSelecteds, data.getOrganization());
                    CommonParameter.setOrganizationSelected(mSelecteds);
                    UserManager.getInstance().getUser().setOrganList(mSelecteds);
                    UserManager.getInstance().notifyUserInfo();
                    UPushAlias.setTag(mSelecteds.get(mSelecteds.size() - 1).getId());
                }
                initOrganization = true;
            }

            @Override
            public void onFail(ResponeThrowable throwable) {
                LogUtil.d(TAG, "initOrganization onFail ++++++++++++++++++++++ ");
                TimingHelper.getInstance().addWorkArriveListener(new TimingHelper.WorkListener() {
                    @Override
                    public void onArrive() {
                        initOrganization();
                        LogUtil.d(TAG, "initOrganization onFail retry ++++++++++++++++++++++ ");
                    }

                    @Override
                    public WorkInt workInt() {
                        return WorkInt.SECOND5_1;
                    }
                });
            }
        }).onGetJoinedList();
    }

    private void initSelectedList(List<OrganizationModel.OrganizationBean> list, OrganizationSelectedModel.OrganizationBean bean) {
        if (bean != null) {
            OrganizationModel.OrganizationBean oBean = new OrganizationModel.OrganizationBean();
            oBean.setId(bean.getId());
            oBean.setName(bean.getName());
            list.add(oBean);
            if (bean.getChildren() != null) {
                initSelectedList(list, bean.getChildren());
            }
        }
    }

    /**
     * 查询用户信息
     */
    private void initUserInfo() {
        if (initUserInfo) {
            return;
        }
        new UserInfoPresenter(new UserInfoPresenter.UserInfoUi<User.UserInfo>() {
            @Override
            public LifecycleProvider getLifecycleProvider() {
                return MainActivity.this;
            }

            @Override
            public void onSuccess(User.UserInfo data) {
                if (data != null) {
                    initUserInfo = true;
                    LogUtil.d(TAG, "initUserInfo onSuccess ++++++++++++++++++++++ ");
                    UserManager.getInstance().getUser().setUserInfo(data);
                    UserManager.getInstance().notifyUserInfo();
                    UPushAlias.set(MyApplication.getApp(), "user_" + data.id, "user");
                    if (SharePreferenceUtil.getBoolean(LoginActivity.KEY_LOGIN_BEGIN_TTS, true)) {
                        SharePreferenceUtil.setBoolean(LoginActivity.KEY_LOGIN_BEGIN_TTS, false);
                        TtsHelper.getInstance().start(new TtsModel("你好" + UserManager.getInstance().getUser().getVisibleName() + ",很高兴见到你"), null);
                    }
                } else {
                    onFail(new ResponeThrowable(0, "data is null"));
                }
            }

            @Override
            public void onFail(ResponeThrowable throwable) {
                LogUtil.d(TAG, "initUserInfo onFail ++++++++++++++++++++++ ");
                TimingHelper.getInstance().addWorkArriveListener(new TimingHelper.WorkListener() {
                    @Override
                    public void onArrive() {
                        initUserInfo();
                        LogUtil.d(TAG, "initUserInfo onFail retry ++++++++++++++++++++++ ");
                    }

                    @Override
                    public WorkInt workInt() {
                        return WorkInt.SECOND5;
                    }
                });
            }
        }).

                onGetUserInfo();
    }

    private void initUmeng() {
        if (initUmeng) {
            return;
        }
        if (hasAgreedAgreement()) {
            PushAgent.getInstance(this).onAppStart();
            String deviceToken = PushAgent.getInstance(this).getRegistrationId();
            LogUtil.d(TAG, "deviceToken = " + deviceToken);
            initUmeng = true;
        } else {
            agreementAfterInitUmeng();
        }
    }

    /**
     * 登录状态下，即点击隐私协议同意按钮后，初始化PushSDK
     */
    private void agreementAfterInitUmeng() {
        MyPreferences.getInstance(getApplicationContext()).setAgreePrivacyAgreement(true);
        PushHelper.init(getApplicationContext());
        PushAgent.getInstance(getApplicationContext()).register(new UPushRegisterCallback() {
            @Override
            public void onSuccess(final String deviceToken) {
                LogUtil.d(TAG, "deviceToken = " + deviceToken);
                initUmeng = true;
            }

            @Override
            public void onFailure(String code, String msg) {
                Log.d("MainActivity", "code:" + code + " msg:" + msg);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        resetInit();
    }
}
