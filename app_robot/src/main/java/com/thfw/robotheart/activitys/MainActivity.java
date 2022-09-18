package com.thfw.robotheart.activitys;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.makeramen.roundedimageview.RoundedImageView;
import com.opensource.svgaplayer.SVGAImageView;
import com.thfw.base.api.MusicApi;
import com.thfw.base.base.IPresenter;
import com.thfw.base.face.SimpleUpgradeStateListener;
import com.thfw.base.models.OrganizationModel;
import com.thfw.base.models.OrganizationSelectedModel;
import com.thfw.base.models.TalkModel;
import com.thfw.base.models.UrgedMsgModel;
import com.thfw.base.models.WeatherDetailsModel;
import com.thfw.base.net.CommonParameter;
import com.thfw.base.net.HttpResult;
import com.thfw.base.net.NetParams;
import com.thfw.base.net.OkHttpUtil;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.OrganizationPresenter;
import com.thfw.base.presenter.TaskPresenter;
import com.thfw.base.presenter.UserInfoPresenter;
import com.thfw.base.timing.TimingHelper;
import com.thfw.base.timing.WorkInt;
import com.thfw.base.utils.BuglyUtil;
import com.thfw.base.utils.ClickCountUtils;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.base.utils.HandlerUtil;
import com.thfw.base.utils.HourUtil;
import com.thfw.base.utils.LocationUtils;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.SharePreferenceUtil;
import com.thfw.base.utils.ToastUtil;
import com.thfw.base.utils.WeatherUtil;
import com.thfw.robotheart.MyApplication;
import com.thfw.robotheart.R;
import com.thfw.robotheart.activitys.audio.AudioHomeActivity;
import com.thfw.robotheart.activitys.exercise.ExerciseActivity;
import com.thfw.robotheart.activitys.login.LoginActivity;
import com.thfw.robotheart.activitys.me.HotPhoneActivity;
import com.thfw.robotheart.activitys.me.MeActivity;
import com.thfw.robotheart.activitys.me.PrivateSetActivity;
import com.thfw.robotheart.activitys.set.SettingActivity;
import com.thfw.robotheart.activitys.set.WeatherActivity;
import com.thfw.robotheart.activitys.talk.AiTalkActivity;
import com.thfw.robotheart.activitys.talk.ThemeTalkActivity;
import com.thfw.robotheart.activitys.test.TestActivity;
import com.thfw.robotheart.activitys.text.BookActivity;
import com.thfw.robotheart.activitys.text.BookStudyActivity;
import com.thfw.robotheart.activitys.video.VideoHomeActivity;
import com.thfw.robotheart.constants.AnimFileName;
import com.thfw.robotheart.port.SerialManager;
import com.thfw.robotheart.push.AidlHelper;
import com.thfw.robotheart.push.MyPreferences;
import com.thfw.robotheart.push.helper.PushHelper;
import com.thfw.robotheart.push.tester.UPushAlias;
import com.thfw.robotheart.receiver.BootCompleteReceiver;
import com.thfw.robotheart.robot.RobotUtil;
import com.thfw.robotheart.service.AutoUpdateService;
import com.thfw.robotheart.util.DialogRobotFactory;
import com.thfw.robotheart.util.IconUtil;
import com.thfw.robotheart.util.MsgCountManager;
import com.thfw.robotheart.util.SVGAHelper;
import com.thfw.robotheart.view.HomeIpTextView;
import com.thfw.robotheart.view.TitleBarView;
import com.thfw.ui.dialog.TDialog;
import com.thfw.ui.dialog.base.BindViewHolder;
import com.thfw.ui.utils.GlideUtil;
import com.thfw.ui.utils.UrgeUtil;
import com.thfw.ui.voice.tts.TtsHelper;
import com.thfw.ui.voice.tts.TtsModel;
import com.thfw.ui.voice.wakeup.WakeupHelper;
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
import java.util.HashMap;
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
    private static boolean initTts;
    private static int initUrgedMsg = -1;
    private static final String KEY_WEATHER = "key.weather";
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
    private UrgedMsgModel mUrgedMsgModel;
    // 气泡文案动画
    private com.thfw.robotheart.view.HomeIpTextView mHitAnim;

    private Handler mMainHandler = new Handler(Looper.getMainLooper());
    private Random random = new Random();

    private WeatherDetailsModel weatherInfoModel;

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
    private ImageView mIvHand;
    private ImageView mIvWeather;
    private TextView mTvWeather;
    private ConstraintLayout mClWeather;
    private Runnable mWeatherRunnable = () -> {
        checkWeather();
    };
    // 天气最后一次更新
    private static long notifyWeatherTime;
    // 10分钟后自动更新
    private static final long MIN_WEATHER_TIME = HourUtil.LEN_10_MINUTE;

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
        initTts = false;
        initUrgedMsg = -1;
        AidlHelper.resetId();

        try {
            NotificationManager manager = (NotificationManager) MyApplication.getApp()
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager != null) {
                manager.cancelAll();
            }
        } catch (Exception e) {
            Log.d("resetInit", e.getMessage());
        }

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
        OkHttpUtil.setBaseCodeListener((code, msg) -> {
            switch (code) {
                case HttpResult.FAIL_TOKEN:
                    if (UserManager.getInstance().isTrueLogin()) {
                        ToastUtil.show(com.thfw.ui.R.string.token_not_valid);
                        UserManager.getInstance().logout(LoginStatus.LOGOUT_EXIT);
                        if (isMeResumed()) {
                            LoginActivity.startActivity(mContext, LoginActivity.BY_PASSWORD);
                        } else {
                            MyApplication.goAppHome((Activity) mContext);
                        }
                    }
                    break;
                case HttpResult.SERVICE_TIME_NO_START:
                case HttpResult.SERVICE_TIME_ALREADY_END:
                    if (!UserManager.getInstance().isTrueLogin()) {
                        return;
                    }
                    DialogRobotFactory.createServerStopDialog(MainActivity.this, new DialogRobotFactory.OnViewCallBack() {
                        @Override
                        public void callBack(TextView mTvTitle, TextView mTvHint, TextView mTvLeft, TextView mTvRight, View mVLineVertical) {
                            mTvHint.setText(msg);
                        }

                        @Override
                        public void onViewClick(BindViewHolder viewHolder, View view, TDialog tDialog) {
                            if (!UserManager.getInstance().isTrueLogin()) {
                                return;
                            }
                            UserManager.getInstance().logout(LoginStatus.LOGOUT_EXIT);
                            if (isMeResumed()) {
                                LoginActivity.startActivity(mContext, LoginActivity.BY_PASSWORD);
                            } else {
                                MyApplication.goAppHome((Activity) mContext);
                            }
                            tDialog.dismiss();
                        }
                    });
                    break;
            }
        });

        mIvHand = findViewById(R.id.iv_hand);
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
        mIvWeather = (ImageView) findViewById(R.id.iv_weather);
        mTvWeather = (TextView) findViewById(R.id.tv_weather);
        mClWeather = (ConstraintLayout) findViewById(R.id.cl_weather);
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
            BootCompleteReceiver.checkBootCompleteAnim(this);
            setUserMessage(UserManager.getInstance().getUser());
        } else {
            // 未登录进入登录页面
            LoginActivity.startActivity(mContext, LoginActivity.BY_PASSWORD);
        }

        SerialManager.getInstance().queryCharge();
        SerialManager.getInstance().allToZero();

        String cacheWeather = SharePreferenceUtil.getString(KEY_WEATHER, "");
        if (!TextUtils.isEmpty(cacheWeather)) {
            String weathers[] = cacheWeather.split(" ");
            if (weathers.length == 2 && !TextUtils.isEmpty(weathers[0]) && !TextUtils.isEmpty(weathers[1])) {
                GlideUtil.load(mContext, IconUtil.getWeatherIcon(weathers[0]),
                        R.mipmap.refresh_cloud, mIvWeather);
                mTvWeather.setText(cacheWeather);
            }
        }
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
            // 登录欢迎语
            ttsWelcome();
            // 检查版本更新
            mMainHandler.removeCallbacks(mCheckVersionRunnable);
            mMainHandler.postDelayed(mCheckVersionRunnable, isMeResumed2() ? 1000 : 2500);

            // 已登录 初始化用户信息和机构信息
            initUmeng();
            checkWeather();
            initUserInfo();
            initOrganization();
            initUrgedMsg();
            wakeup();
            GlideUtil.load(mContext, R.drawable.ic_ip_hand, mIvHand);
        }
    }


    /**
     * 测试
     */
    private void wakeup() {
        if (RobotUtil.isInstallRobot()) {
            if (WakeupHelper.getInstance().isIng()) {
                return;
            }
            WakeupHelper.getInstance().setWakeUpListener(new WakeupHelper.OnWakeUpListener() {
                @Override
                public void onWakeup(int angle, int beam) {
                    RobotUtil.wakeup(angle, beam);
                }

                @Override
                public void onError() {

                }
            });
            WakeupHelper.getInstance().start();
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
                    mMainHandler.postDelayed(mStartFaceRunnable, 1500 + random.nextInt(HOME_IP_ANIM_TIME));
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
                    onUrgedDialog();
                }
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (RobotUtil.isInstallRobot() && WakeupHelper.getInstance().isIng()) {
            WakeupHelper.getInstance().stop();
        }
        animResume(false);
        mMainHandler.removeCallbacks(mCheckVersionRunnable);
        if (mWeatherRunnable != null) {
            mMainHandler.removeCallbacks(mWeatherRunnable);
        }
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

                if (accountManager.isNewLogin()) {
                    // 此时延迟两秒，是因为登录同时 activity 没有 resume 网络请求执行有误（在机器性能差的平台上）
                    HandlerUtil.getMainHandler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // 已登录 初始化用户信息和机构信息
                            initUmeng();
                            initUserInfo();
                            initOrganization();
                        }
                    }, 2000);
                }
                setUserMessage(user);
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
        if (!EmptyUtil.isEmpty(mContext)) {
            GlideUtil.load(mContext, user.getVisibleAvatar(), mRivAvatar);
        }
    }

    /**
     * 查询组织信息
     */
    private void initOrganization() {
        if (!initOrganization) {
            if (!UserManager.getInstance().isLogin()) {
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
                        CommonParameter.setOrganizationModel(data);
                        UserManager.getInstance().getUser().setOrganList(mSelecteds);
                        UserManager.getInstance().notifyUserInfo();

                        if (!RobotUtil.isUseUmeng()) {
                            AidlHelper.setOrganId(mSelecteds.get(mSelecteds.size() - 1).getId());
                        } else {
                            UPushAlias.setTag(mSelecteds.get(mSelecteds.size() - 1).getId());
                        }
                    }
                    TimingHelper.getInstance().removeWorkArriveListener(WorkInt.SECOND5_1);
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
            if (!UserManager.getInstance().isLogin()) {
                return;
            }

            MsgCountManager.getInstance().addOnCountChangeListener(this);
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
                        if (!RobotUtil.isUseUmeng()) {
                            AidlHelper.setUserId(data.id);
                        } else {
                            UPushAlias.set(MyApplication.getApp(), "user_" + data.id, "user");
                        }
                        ttsWelcome();
                        TimingHelper.getInstance().removeWorkArriveListener(WorkInt.SECOND5);
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

    private void checkWeather() {
        if (System.currentTimeMillis() - notifyWeatherTime < MIN_WEATHER_TIME) {
            return;
        }
        notifyWeatherTime = System.currentTimeMillis();

        // 当前城市查询
        try {
            LocationUtils.getCNBylocation(mContext);
            if (TextUtils.isEmpty(LocationUtils.getCityName())) {
                LocationUtils.setCityNameListener(new LocationUtils.CityNameListener() {
                    @Override
                    public void callBack(String cityName) {
                        initWeather();
                    }
                });
            } else {
                initWeather();
            }
        } catch (Exception e) {
        }
    }

    private void initWeather() {
        MusicApi.requestWeather(WeatherUtil.getWeatherCityId(), new MusicApi.WeatherCallback() {
            @Override
            public void onFailure(int code, String msg) {
                LogUtil.d(TAG, "requestWeather onFail retry ++++++++++++++++++++++ ");
                TimingHelper.getInstance().addWorkArriveListener(new TimingHelper.WorkListener() {
                    @Override
                    public void onArrive() {
                        initWeather();
                        LogUtil.d(TAG, "requestWeather onFail retry ++++++++++++++++++++++ ");
                    }

                    @Override
                    public WorkInt workInt() {
                        return WorkInt.SECOND7;
                    }
                });
            }

            @Override
            public void onResponse(WeatherDetailsModel weatherInfoModel) {
                MainActivity.this.weatherInfoModel = weatherInfoModel;
                TimingHelper.getInstance().removeWorkArriveListener(WorkInt.SECOND7);
                notifyWeather();
                if (mWeatherRunnable != null) {
                    mMainHandler.removeCallbacks(mWeatherRunnable);
                    mMainHandler.postDelayed(mWeatherRunnable, MIN_WEATHER_TIME + HourUtil.LEN_SECOND);
                }
            }
        });
        LogUtil.i(TAG, "----------- search weather -------------");
    }

    private void notifyWeather() {
        if (weatherInfoModel == null) {
            return;
        }
        runOnUiThread(() -> {
            GlideUtil.load(mContext, IconUtil.getWeatherIcon(weatherInfoModel.getMyWeather()), R.mipmap.refresh_cloud, mIvWeather);
            mTvWeather.setText(weatherInfoModel.getSimpleDesc());
            mTvWeather.setTextColor(mContext.getResources().getColor(R.color.colorRobotFore));
            SharePreferenceUtil.setString(KEY_WEATHER, weatherInfoModel.getSimpleDesc());
            mClWeather.setOnClickListener(v -> {
                GlideUtil.load(mContext, R.mipmap.refresh_cloud, R.mipmap.refresh_cloud, mIvWeather);
                mTvWeather.setText(weatherInfoModel.getSimpleDesc());
                mTvWeather.setTextColor(mContext.getResources().getColor(R.color.colorRobotFore_50));
                WeatherActivity.startActivity(mContext, weatherInfoModel);
            });
        });
    }

    private void initUmeng() {
        if (RobotUtil.isUseUmeng()) {
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
    }

    private void ttsWelcome() {
        if (initTts) {
            return;
        }
        initTts = !SharePreferenceUtil.getBoolean(LoginActivity.KEY_LOGIN_BEGIN_TTS, false);
        if (initTts) {
            return;
        }
        if (isMeResumed() && initUserInfo) {
            SharePreferenceUtil.setBoolean(LoginActivity.KEY_LOGIN_BEGIN_TTS, false);
            TtsHelper.getInstance().start(new TtsModel("你好" + UserManager.getInstance().getUser().getVisibleName() + ",很高兴见到你"), null);
            initTts = true;
        }
    }

    /**
     * 登录状态下，即点击隐私协议同意按钮后，初始化PushSDK
     */
    public void agreementAfterInitUmeng() {
        MyPreferences.getInstance(MyApplication.getApp()).setAgreePrivacyAgreement(true);
        PushHelper.init(MyApplication.getApp());
        PushAgent.getInstance(MyApplication.getApp()).register(new UPushRegisterCallback() {
            @Override
            public void onSuccess(final String deviceToken) {
                LogUtil.d("deviceToken = " + deviceToken);
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
        SerialManager.getInstance().release();
        MsgCountManager.getInstance().removeOnCountChangeListener(this);
        resetInit();
        super.onDestroy();

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

    /**
     * 催促弹窗
     */
    private void initUrgedMsg() {
        if (initUrgedMsg != -1) {
            onUrgedDialog();
            return;
        }
        new TaskPresenter<>(new TaskPresenter.TaskUi<UrgedMsgModel>() {
            @Override
            public LifecycleProvider getLifecycleProvider() {
                return MainActivity.this;
            }

            @Override
            public void onSuccess(UrgedMsgModel data) {
                initUrgedMsg = 1;
                mUrgedMsgModel = data;
                onUrgedDialog();
            }

            @Override
            public void onFail(ResponeThrowable throwable) {
                initUrgedMsg = 0;
            }
        }).getUrgedMsg(NetParams.crete());
    }

    private void onUrgedDialog() {
        if (mUrgedMsgModel == null || !mUrgedMsgModel.isDisplay() ||
                DialogRobotFactory.getSvgaTDialog() != null) {
            return;
        }
        HashMap<Integer, Object> map = new HashMap<>();
        map.put(UrgeUtil.URGE_OBJ, mUrgedMsgModel);
        mUrgedMsgModel.setDisplay(0);
        UrgeUtil.notify(map);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == WeatherActivity.REQUEST_CODE && resultCode == RESULT_OK) {
            if (WeatherActivity.getFirstModel() != null) {
                weatherInfoModel = WeatherActivity.getFirstModel();
                notifyWeather();
            }
        }
    }
}