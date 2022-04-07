package com.thfw.robotheart.activitys;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.makeramen.roundedimageview.RoundedImageView;
import com.opensource.svgaplayer.SVGAImageView;
import com.thfw.base.base.IPresenter;
import com.thfw.base.face.SimpleUpgradeStateListener;
import com.thfw.base.models.OrganizationModel;
import com.thfw.base.models.OrganizationSelectedModel;
import com.thfw.base.models.TalkModel;
import com.thfw.base.net.BaseCodeListener;
import com.thfw.base.net.CommonParameter;
import com.thfw.base.net.OkHttpUtil;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.OrganizationPresenter;
import com.thfw.base.presenter.UserInfoPresenter;
import com.thfw.base.timing.TimingHelper;
import com.thfw.base.timing.WorkInt;
import com.thfw.base.utils.BuglyUtil;
import com.thfw.base.utils.ClickCountUtils;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.SharePreferenceUtil;
import com.thfw.base.utils.ToastUtil;
import com.thfw.robotheart.MyApplication;
import com.thfw.robotheart.R;
import com.thfw.robotheart.activitys.audio.AudioHomeActivity;
import com.thfw.robotheart.activitys.exercise.ExerciseActivity;
import com.thfw.robotheart.activitys.login.LoginActivity;
import com.thfw.robotheart.activitys.me.HotPhoneActivity;
import com.thfw.robotheart.activitys.me.MeActivity;
import com.thfw.robotheart.activitys.me.PrivateSetActivity;
import com.thfw.robotheart.activitys.set.SettingActivity;
import com.thfw.robotheart.activitys.talk.AiTalkActivity;
import com.thfw.robotheart.activitys.talk.ThemeTalkActivity;
import com.thfw.robotheart.activitys.test.TestActivity;
import com.thfw.robotheart.activitys.text.BookActivity;
import com.thfw.robotheart.activitys.text.BookStudyActivity;
import com.thfw.robotheart.activitys.video.VideoHomeActivity;
import com.thfw.robotheart.constants.AnimFileName;
import com.thfw.robotheart.push.MyPreferences;
import com.thfw.robotheart.push.helper.PushHelper;
import com.thfw.robotheart.push.tester.UPushAlias;
import com.thfw.robotheart.service.AutoUpdateService;
import com.thfw.robotheart.util.DialogRobotFactory;
import com.thfw.robotheart.util.MsgCountManager;
import com.thfw.robotheart.util.SVGAHelper;
import com.thfw.robotheart.view.HomeIpTextView;
import com.thfw.robotheart.view.TitleBarView;
import com.thfw.ui.utils.GlideUtil;
import com.thfw.ui.voice.tts.TtsHelper;
import com.thfw.ui.voice.tts.TtsModel;
import com.thfw.ui.widget.MyRobotSearchView;
import com.thfw.ui.widget.WeekView;
import com.thfw.user.login.LoginStatus;
import com.thfw.user.login.UserManager;
import com.thfw.user.login.UserObserver;
import com.thfw.user.models.User;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.umeng.message.PushAgent;
import com.umeng.message.api.UPushRegisterCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.thfw.robotheart.constants.AnimFileName.HOME_IP_ANIM_TIME;

public class MainActivity extends RobotBaseActivity implements View.OnClickListener, MsgCountManager.OnCountChangeListener {

    /**
     * 机构信息、用户信息、友盟初始化标识
     */
    private static boolean initUserInfo;
    private static boolean initOrganization;
    private static boolean initUmeng;
    // 登录动画是否显示，为了不频繁获取sp数据
    private static boolean showLoginAnim;
    private com.thfw.robotheart.view.TitleBarView mTitleBarView;
    private com.thfw.ui.widget.WeekView mWeekView;
    private com.makeramen.roundedimageview.RoundedImageView mRivAvatar;
    private android.widget.TextView mTvNickname;
    private android.widget.TextView mTvInstitution;
    private ConstraintLayout mClSpecialityTalk;
    private android.widget.LinearLayout mLlNavigation;
    private android.widget.LinearLayout mRlRow01;
    private ConstraintLayout mLlTest;
    private ConstraintLayout mLlMusic;
    private android.widget.LinearLayout mRlRow02;
    private ConstraintLayout mLlTalk;
    private ConstraintLayout mLlVideo;
    private android.widget.LinearLayout mRlRow03;
    private ConstraintLayout mLlExercise;
    private ConstraintLayout mLlBook;
    private ConstraintLayout mLlStudy;
    private android.widget.LinearLayout mRlRow04;
    private ConstraintLayout mLlHotCall;
    private android.widget.LinearLayout mLlSetting;
    private android.widget.LinearLayout mLlMe;
    private MyRobotSearchView mMySearch;
    private LinearLayout mLlRiv;
    private ConstraintLayout mClMe;
    private ConstraintLayout mClSetting;
    private TextView mTvDotCount;
    private TextView mTvDotHint;
    private TextView mTvSetDotHint;
    private TextView mTvMeDotHint;
    // 身体和脸部动画
    private SVGAImageView mSvgaBody;
    private SVGAImageView mSvgaFace;
    // 气泡文案动画
    private com.thfw.robotheart.view.HomeIpTextView mHitAnim;

    private Handler mMainHandler = new Handler(Looper.getMainLooper());
    private Random random = new Random();

    /**
     * 延时开启脸部动画
     */
    private Runnable mStartFaceRunnable = () -> {
        if (isMeResumed()) {
            startFaceAnim();
        }
    };

    /**
     * 延时检查版本动画
     */
    private Runnable mCheckVersionRunnable = () -> {
        if (isMeResumed()) {
            checkVersion();
        }
    };

    /**
     * 登录动画是否显示
     *
     * @param showLoginAnim
     */
    public static void setShowLoginAnim(boolean showLoginAnim) {
        MainActivity.showLoginAnim = showLoginAnim;
    }

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

    @Override
    public int getContentView() {
        return R.layout.activity_main_home;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initView() {
        OkHttpUtil.setBaseCodeListener(code -> {
            if (code == BaseCodeListener.LOGOUT) {
                if (UserManager.getInstance().isTrueLogin()) {
                    ToastUtil.show(com.thfw.ui.R.string.token_not_valid);
                    UserManager.getInstance().logout(LoginStatus.LOGOUT_EXIT);
                    MyApplication.goAppHome((Activity) mContext);
                }
            }
        });
        mTitleBarView = (TitleBarView) findViewById(R.id.titleBarView);
        mWeekView = (WeekView) findViewById(R.id.weekView);
        mRivAvatar = (RoundedImageView) findViewById(R.id.riv_avatar);
        mTvNickname = (TextView) findViewById(R.id.tv_nickname);
        mTvInstitution = (TextView) findViewById(R.id.tv_institution);
        mClSpecialityTalk = findViewById(R.id.cl_speciality_talk);
        mLlNavigation = (LinearLayout) findViewById(R.id.ll_navigation);
        mRlRow01 = (LinearLayout) findViewById(R.id.rl_row_01);
        mLlTest = findViewById(R.id.cl_test);
        mLlMusic = findViewById(R.id.cl_music);
        mRlRow02 = (LinearLayout) findViewById(R.id.rl_row_02);
        mLlTalk = findViewById(R.id.cl_talk);
        mLlVideo = findViewById(R.id.cl_video);
        mRlRow03 = (LinearLayout) findViewById(R.id.rl_row_03);
        mTvDotHint = findViewById(R.id.tv_set_dot_hint);
        mLlExercise = findViewById(R.id.cl_exercise);
        mLlBook = findViewById(R.id.cl_book);
        mLlStudy = findViewById(R.id.cl_study);
        mRlRow04 = (LinearLayout) findViewById(R.id.rl_row_04);
        mLlHotCall = findViewById(R.id.cl_hot_call);
        mLlSetting = (LinearLayout) findViewById(R.id.ll_setting);
        mLlMe = (LinearLayout) findViewById(R.id.ll_me);
        mClSetting = (ConstraintLayout) findViewById(R.id.cl_setting);
        mClMe = (ConstraintLayout) findViewById(R.id.cl_me);
        mTvDotCount = findViewById(R.id.tv_dot_count);
        mLlTest.setOnClickListener(this);
        mLlTalk.setOnClickListener(this);
        mLlMusic.setOnClickListener(this);
        mLlVideo.setOnClickListener(this);
        mLlExercise.setOnClickListener(this);
        mLlBook.setOnClickListener(this);
        mLlStudy.setOnClickListener(this);
        mLlHotCall.setOnClickListener(this);
        mClSetting.setOnClickListener(this);
        mClMe.setOnClickListener(this);
        mClSpecialityTalk.setOnClickListener(this);
        mMySearch = (MyRobotSearchView) findViewById(R.id.my_search);
        // 搜索控件进入搜索页面
        mMySearch.setOnSearchListener(new MyRobotSearchView.OnSearchListener() {
            @Override
            public void onSearch(String key, boolean clickSearch) {

            }

            @Override
            public void onClick(View view) {
                startActivity(new Intent(mContext, SearchActivity.class));
            }
        });

        mLlRiv = (LinearLayout) findViewById(R.id.ll_riv);

        mSvgaBody = (SVGAImageView) findViewById(R.id.svga_body);
        mSvgaFace = (SVGAImageView) findViewById(R.id.svga_face);
        mTvSetDotHint = (TextView) findViewById(R.id.tv_set_dot_hint);
        mTvMeDotHint = (TextView) findViewById(R.id.tv_me_dot_hint);
        mHitAnim = (HomeIpTextView) findViewById(R.id.hit_anim);
    }

    @Override
    public void initData() {
        // 日期、星期连续点击10次进入配置页面（机构id设置）
        View.OnClickListener clickListener = v -> {
            if (ClickCountUtils.click(10)) {
                startActivity(new Intent(mContext, PrivateSetActivity.class));
            }
        };
        mWeekView.setOnClickListener(clickListener);
        mTitleBarView.setOnClickListener(clickListener);
        // 头像点击进入【我的页面】
        mLlRiv.setOnClickListener(v -> {
            if (UserManager.getInstance().isLogin()) {
                startActivity(new Intent(mContext, MeActivity.class));
            }
        });

        if (UserManager.getInstance().isTrueLogin()) {
            setUserMessage(UserManager.getInstance().getUser());
            // 已登录 初始化用户信息和机构信息
            initUmeng();
            initUserInfo();
            initOrganization();
        }
        MsgCountManager.getInstance().addOnCountChangeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!UserManager.getInstance().isTrueLogin()) {
            // 未登录进入登录页面
            LoginActivity.startActivity(mContext, LoginActivity.BY_PASSWORD);
        } else {
            // 首次登录
            showSVGALogin();
            // 开始动画
            animResume(true);
            // 检查版本更新
            mMainHandler.removeCallbacks(mCheckVersionRunnable);
            mMainHandler.postDelayed(mCheckVersionRunnable, isMeResumed2() ? 1000 : 2500);
        }

    }

    /**
     * 脸部动画
     */
    private void startFaceAnim() {
        SVGAHelper.playSVGA(mSvgaFace, SVGAHelper.SVGAModel.create(AnimFileName.FACE_FACE).setLoopCount(1), new DialogRobotFactory.SimpleSVGACallBack() {
            @Override
            public void onFinished() {
                if (isMeResumed()) {
                    mMainHandler.postDelayed(mStartFaceRunnable, random.nextInt(HOME_IP_ANIM_TIME));
                }
            }
        });
    }

    /**
     * 显示登录欢迎动画
     */
    private void showSVGALogin() {
        if (showLoginAnim && SharePreferenceUtil.getBoolean(LoginActivity.KEY_LOGIN_BEGIN, true)) {
            LogUtil.d(TAG, "showSVGALogin start");
            DialogRobotFactory.createSvgaDialog(MainActivity.this, AnimFileName.TRANSITION_WELCOM, new DialogRobotFactory.OnSVGACallBack() {
                @Override
                public void callBack(SVGAImageView svgaImageView) {
                    LogUtil.d(TAG, "showSVGALogin end");
                    SharePreferenceUtil.setBoolean(LoginActivity.KEY_LOGIN_BEGIN, false);
                    showLoginAnim = false;
                }
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        animResume(false);
        mMainHandler.removeCallbacks(mCheckVersionRunnable);
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
                mSvgaFace.stopAnimation();
            }
            if (mSvgaBody.isAnimating()) {
                mSvgaBody.stopAnimation();
            }
        }
    }

    /**
     * 检查版本更新
     */
    private void checkVersion() {
        BuglyUtil.requestNewVersion(new SimpleUpgradeStateListener() {
            @Override
            public void onVersion(boolean hasNewVersion) {
                super.onVersion(hasNewVersion);
                Log.d("requestNewVersion", "hasNewVersion = " + hasNewVersion);
                if (isMeResumed() && !EmptyUtil.isEmpty(MainActivity.this)) {
                    mTvDotHint.setText("新版本");
                    mTvDotHint.setVisibility(hasNewVersion ? View.VISIBLE : View.GONE);
                    if (hasNewVersion) {
                        AutoUpdateService.startUpdate(mContext);
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        int vId = v.getId();
        if (vId == R.id.cl_me) {
            startActivity(new Intent(mContext, MeActivity.class));
        } else if (vId == R.id.cl_setting) {
            startActivity(new Intent(mContext, SettingActivity.class));
        } else if (vId == R.id.cl_music) {
            DialogRobotFactory.createSvgaDialog(MainActivity.this,
                    AnimFileName.TRANSITION_AUDIO,
                    new DialogRobotFactory.OnSVGACallBack() {

                        public void callBack(SVGAImageView svgaImageView) {
                            startActivity(new Intent(mContext, AudioHomeActivity.class));
                        }
                    });
        } else if (vId == R.id.cl_test) {
            DialogRobotFactory.createSvgaDialog(MainActivity.this,
                    AnimFileName.TRANSITION_TEST,
                    new DialogRobotFactory.OnSVGACallBack() {
                        public void callBack(SVGAImageView svgaImageView) {
                            startActivity(new Intent(mContext, TestActivity.class));
                        }
                    });
        } else if (vId == R.id.cl_video) {
            DialogRobotFactory.createSvgaDialog(MainActivity.this,
                    AnimFileName.TRANSITION_VIDEO,
                    new DialogRobotFactory.OnSVGACallBack() {
                        public void callBack(SVGAImageView svgaImageView) {
                            startActivity(new Intent(mContext, VideoHomeActivity.class));
                        }
                    });
        } else if (vId == R.id.cl_exercise) {
            DialogRobotFactory.createSvgaDialog(MainActivity.this,
                    AnimFileName.TRANSITION_TOOL,
                    new DialogRobotFactory.OnSVGACallBack() {
                        public void callBack(SVGAImageView svgaImageView) {
                            startActivity(new Intent(mContext, ExerciseActivity.class));
                        }
                    });
        } else if (vId == R.id.cl_speciality_talk) {
            DialogRobotFactory.createSvgaDialog(MainActivity.this,
                    AnimFileName.TRANSITION_THEME,
                    new DialogRobotFactory.OnSVGACallBack() {
                        public void callBack(SVGAImageView svgaImageView) {
                            startActivity(new Intent(mContext, ThemeTalkActivity.class));
                        }
                    });
        } else if (vId == R.id.cl_talk) {
            DialogRobotFactory.createSvgaDialog(MainActivity.this,
                    AnimFileName.TRANSITION_TALK,
                    new DialogRobotFactory.OnSVGACallBack() {
                        public void callBack(SVGAImageView svgaImageView) {
                            AiTalkActivity.startActivity(mContext, new TalkModel(TalkModel.TYPE_AI));
                        }
                    });
        } else if (vId == R.id.cl_hot_call) {
            startActivity(new Intent(mContext, HotPhoneActivity.class));
        } else if (vId == R.id.cl_study) {
            DialogRobotFactory.createSvgaDialog(MainActivity.this,
                    AnimFileName.TRANSITION_IDEO,
                    new DialogRobotFactory.OnSVGACallBack() {
                        public void callBack(SVGAImageView svgaImageView) {
                            startActivity(new Intent(mContext, BookStudyActivity.class));
                        }
                    });
        } else if (vId == R.id.cl_book) {
            DialogRobotFactory.createSvgaDialog(MainActivity.this,
                    AnimFileName.TRANSITION_BOOK,
                    new DialogRobotFactory.OnSVGACallBack() {
                        public void callBack(SVGAImageView svgaImageView) {
                            startActivity(new Intent(mContext, BookActivity.class));
                        }
                    });
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
                    setUserMessage(user);
                }

            }
        };
    }

    /**
     * 设置用户信息
     *
     * @param user
     */
    private void setUserMessage(User user) {
        mTvInstitution.setText(user.getOrganListStr());
        mTvNickname.setText(user.getVisibleName());
        GlideUtil.load(mContext, user.getVisibleAvatar(), mRivAvatar);
    }

    /**
     * 查询组织信息
     */
    private void initOrganization() {
        if (!initOrganization) {
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
        if (!initUserInfo) {
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
            }).onGetUserInfo();
        }
    }

    private void initUmeng() {
        if (!initUmeng) {
            if (hasAgreedAgreement()) {
                PushAgent.getInstance(this).onAppStart();
                String deviceToken = PushAgent.getInstance(this).getRegistrationId();
                LogUtil.d(TAG, "deviceToken = " + deviceToken);
                initUmeng = true;
            } else {
                agreementAfterInitUmeng();
            }
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
        MsgCountManager.getInstance().removeOnCountChangeListener(this);
        super.onDestroy();
        resetInit();
    }

    @Override
    public void onCount(int numTask, int numSystem) {
        MsgCountManager.setTextView(mTvDotCount, numTask + numSystem);
    }

    @Override
    public void onItemState(int id, boolean read) {
    }

    @Override
    public void onReadAll(int type) {
    }
}