package com.thfw.robotheart.activitys;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.makeramen.roundedimageview.RoundedImageView;
import com.opensource.svgaplayer.SVGAImageView;
import com.thfw.base.base.IPresenter;
import com.thfw.base.face.SimpleUpgradeStateListener;
import com.thfw.base.models.OrganizationModel;
import com.thfw.base.models.OrganizationSelectedModel;
import com.thfw.base.models.TalkModel;
import com.thfw.base.net.CommonParameter;
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
import com.thfw.robotheart.view.DialogRobotFactory;
import com.thfw.robotheart.view.TitleBarView;
import com.thfw.ui.utils.GlideUtil;
import com.thfw.ui.widget.MyRobotSearchView;
import com.thfw.ui.widget.WeekView;
import com.thfw.user.login.User;
import com.thfw.user.login.UserManager;
import com.thfw.user.login.UserObserver;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.umeng.message.PushAgent;
import com.umeng.message.api.UPushRegisterCallback;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends RobotBaseActivity implements View.OnClickListener {

    /**
     * 机构信息和用户信息初始化标识
     */
    private static boolean initUserInfo;
    private static boolean initOrganization;
    private com.thfw.robotheart.view.TitleBarView mTitleBarView;
    private com.thfw.ui.widget.WeekView mWeekView;
    private com.makeramen.roundedimageview.RoundedImageView mRivAvatar;
    private android.widget.TextView mTvNickname;
    private android.widget.TextView mTvInstitution;
    private android.widget.RelativeLayout mRlSpecialityTalk;
    private android.widget.LinearLayout mLlNavigation;
    private android.widget.LinearLayout mRlRow01;
    private android.widget.LinearLayout mLlTest;
    private android.widget.LinearLayout mLlMusic;
    private android.widget.LinearLayout mRlRow02;
    private android.widget.LinearLayout mLlTalk;
    private android.widget.LinearLayout mLlVideo;
    private android.widget.LinearLayout mRlRow03;
    private android.widget.LinearLayout mLlExercise;
    private android.widget.LinearLayout mLlBook;
    private android.widget.LinearLayout mLlStudy;
    private android.widget.LinearLayout mRlRow04;
    private android.widget.LinearLayout mLlHotCall;
    private android.widget.LinearLayout mLlSetting;
    private android.widget.LinearLayout mLlMe;
    private MyRobotSearchView mMySearch;
    private LinearLayout mLlRiv;
    private ConstraintLayout mClMe;
    private ConstraintLayout mClSetting;
    private TextView mTvDotCount;
    private Handler mMainHandler = new Handler(Looper.getMainLooper());

    /**
     * 重新登录后重新获取用户相关信息
     */
    public static void resetInit() {
        initUserInfo = false;
        initOrganization = false;
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

        mTitleBarView = (TitleBarView) findViewById(R.id.titleBarView);
        mWeekView = (WeekView) findViewById(R.id.weekView);
        mRivAvatar = (RoundedImageView) findViewById(R.id.riv_avatar);
        mTvNickname = (TextView) findViewById(R.id.tv_nickname);
        mTvInstitution = (TextView) findViewById(R.id.tv_institution);
        mRlSpecialityTalk = (RelativeLayout) findViewById(R.id.rl_speciality_talk);
        mLlNavigation = (LinearLayout) findViewById(R.id.ll_navigation);
        mRlRow01 = (LinearLayout) findViewById(R.id.rl_row_01);
        mLlTest = (LinearLayout) findViewById(R.id.ll_test);
        mLlMusic = (LinearLayout) findViewById(R.id.ll_music);
        mRlRow02 = (LinearLayout) findViewById(R.id.rl_row_02);
        mLlTalk = (LinearLayout) findViewById(R.id.ll_talk);
        mLlVideo = (LinearLayout) findViewById(R.id.ll_video);
        mRlRow03 = (LinearLayout) findViewById(R.id.rl_row_03);
        mLlExercise = (LinearLayout) findViewById(R.id.ll_exercise);
        mLlBook = (LinearLayout) findViewById(R.id.ll_book);
        mLlStudy = (LinearLayout) findViewById(R.id.ll_study);
        mRlRow04 = (LinearLayout) findViewById(R.id.rl_row_04);
        mLlHotCall = (LinearLayout) findViewById(R.id.ll_hot_call);
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
        mRlSpecialityTalk.setOnClickListener(this);
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

        setMsg(100);
    }


    @Override
    public void initData() {
        // 日期、星期连续点击10次进入配置页面（机构id设置）
        mWeekView.setOnClickListener(v -> {
            if (ClickCountUtils.click(10)) {
                startActivity(new Intent(mContext, PrivateSetActivity.class));
            }
        });
        // 登录的情况下进行用户信息配置
        if (UserManager.getInstance().isLogin()) {
            setUserMessage(UserManager.getInstance().getUser());
        }
        // 头像点击进入【我的页面】
        mLlRiv.setOnClickListener(v -> {
            if (UserManager.getInstance().isLogin()) {
                startActivity(new Intent(mContext, MeActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!UserManager.getInstance().isLogin()) {
            // 未登录进入登录页面
            LoginActivity.startActivity(mContext, LoginActivity.BY_PASSWORD);
        } else {
            // 已登录 初始化用户信息和机构信息
            initUserInfo();
            initOrganization();
            initUmeng();
            showSVGALogin();
        }
        // 检查版本更新
        mMainHandler.removeCallbacksAndMessages(null);
        mMainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (EmptyUtil.isEmpty(MainActivity.this)) {
                    return;
                }
                if (isMeResumed()) {
                    checkVersion();
                }
            }
        }, isMeResumed2() ? 1000 : 2500);
    }

    private void showSVGALogin() {
        if (!SharePreferenceUtil.getBoolean(LoginActivity.KEY_LOGIN_BEGIN, true)) {
            return;
        }
        LogUtil.d(TAG, "showSVGALogin start");
        DialogRobotFactory.createSvgaDialog(MainActivity.this, AnimFileName.TRANSITION_WELCOM, new DialogRobotFactory.OnSVGACallBack() {
            @Override
            public void callBack(SVGAImageView svgaImageView) {
                LogUtil.d(TAG, "showSVGALogin end");
                SharePreferenceUtil.setBoolean(LoginActivity.KEY_LOGIN_BEGIN, false);
            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        mMainHandler.removeCallbacksAndMessages(null);
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
                if (EmptyUtil.isEmpty(MainActivity.this)) {
                    return;
                }
                if (isMeResumed()) {
                    TextView view = findViewById(R.id.tv_set_dot_hint);
                    if (view == null) {
                        return;
                    }
                    view.setText("新版本");
                    view.setVisibility(hasNewVersion ? View.VISIBLE : View.GONE);
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
        } else if (vId == R.id.ll_music) {
            DialogRobotFactory.createSvgaDialog(MainActivity.this,
                    AnimFileName.TRANSITION_AUDIO,
                    new DialogRobotFactory.OnSVGACallBack() {

                        public void callBack(SVGAImageView svgaImageView) {
                            startActivity(new Intent(mContext, AudioHomeActivity.class));
                        }
                    });
        } else if (vId == R.id.ll_test) {
            DialogRobotFactory.createSvgaDialog(MainActivity.this,
                    AnimFileName.TRANSITION_TEST,
                    new DialogRobotFactory.OnSVGACallBack() {
                        public void callBack(SVGAImageView svgaImageView) {
                            startActivity(new Intent(mContext, TestActivity.class));
                        }
                    });
        } else if (vId == R.id.ll_video) {
            DialogRobotFactory.createSvgaDialog(MainActivity.this,
                    AnimFileName.TRANSITION_VIDEO,
                    new DialogRobotFactory.OnSVGACallBack() {
                        public void callBack(SVGAImageView svgaImageView) {
                            startActivity(new Intent(mContext, VideoHomeActivity.class));
                        }
                    });
        } else if (vId == R.id.ll_exercise) {
            DialogRobotFactory.createSvgaDialog(MainActivity.this,
                    AnimFileName.TRANSITION_TOOL,
                    new DialogRobotFactory.OnSVGACallBack() {
                        public void callBack(SVGAImageView svgaImageView) {
                            startActivity(new Intent(mContext, ExerciseActivity.class));
                        }
                    });
        } else if (vId == R.id.rl_speciality_talk) {
            DialogRobotFactory.createSvgaDialog(MainActivity.this,
                    AnimFileName.TRANSITION_THEME,
                    new DialogRobotFactory.OnSVGACallBack() {
                        public void callBack(SVGAImageView svgaImageView) {
                            startActivity(new Intent(mContext, ThemeTalkActivity.class));
                        }
                    });
        } else if (vId == R.id.ll_talk) {
            DialogRobotFactory.createSvgaDialog(MainActivity.this,
                    AnimFileName.TRANSITION_TALK,
                    new DialogRobotFactory.OnSVGACallBack() {
                        public void callBack(SVGAImageView svgaImageView) {
                            AiTalkActivity.startActivity(mContext, new TalkModel(TalkModel.TYPE_AI));
                        }
                    });
        } else if (vId == R.id.ll_hot_call) {
            startActivity(new Intent(mContext, HotPhoneActivity.class));
        } else if (vId == R.id.ll_study) {
            DialogRobotFactory.createSvgaDialog(MainActivity.this,
                    AnimFileName.TRANSITION_IDEO,
                    new DialogRobotFactory.OnSVGACallBack() {
                        public void callBack(SVGAImageView svgaImageView) {
                            startActivity(new Intent(mContext, BookStudyActivity.class));
                        }
                    });
        } else if (vId == R.id.ll_book) {
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
        GlideUtil.load(mContext, user.getVisibleAvatar(), mRivAvatar);
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

    /**
     * 设置未读消息数量
     *
     * @param count
     */
    private void setMsg(int count) {
        if (count > 0) {
            mTvDotCount.setVisibility(View.VISIBLE);
            mTvDotCount.setText(count > 99 ? "99+" : String.valueOf(count));
        } else {
            mTvDotCount.setVisibility(View.GONE);
        }
    }

    private void initUmeng() {
        if (hasAgreedAgreement()) {
            PushAgent.getInstance(this).onAppStart();
            String deviceToken = PushAgent.getInstance(this).getRegistrationId();
            LogUtil.d(TAG, "deviceToken = " + deviceToken);
        } else {
            agreementAfterInitUmeng();
        }
    }

    private static boolean hasAgreedAgreement() {
        return MyPreferences.getInstance(MyApplication.getApp()).hasAgreePrivacyAgreement();
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