package com.thfw.mobileheart.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.makeramen.roundedimageview.RoundedImageView;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.thfw.base.base.IPresenter;
import com.thfw.base.base.SpeechToAction;
import com.thfw.base.face.PermissionListener;
import com.thfw.base.face.SimpleUpgradeStateListener;
import com.thfw.base.models.CommonModel;
import com.thfw.base.models.MoodLivelyModel;
import com.thfw.base.net.HttpResult;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.LoginPresenter;
import com.thfw.base.utils.BuglyUtil;
import com.thfw.base.utils.ClickCountUtils;
import com.thfw.base.utils.FunctionType;
import com.thfw.base.utils.HandlerUtil;
import com.thfw.base.utils.SharePreferenceUtil;
import com.thfw.mobileheart.MyApplication;
import com.thfw.mobileheart.R;
import com.thfw.mobileheart.activity.BaseActivity;
import com.thfw.mobileheart.activity.BaseFragment;
import com.thfw.mobileheart.activity.PrivateSetActivity;
import com.thfw.mobileheart.activity.login.LoginActivity;
import com.thfw.mobileheart.activity.me.CollectActivity;
import com.thfw.mobileheart.activity.me.MeHistoryActivity;
import com.thfw.mobileheart.activity.mood.MoodDetailActivity;
import com.thfw.mobileheart.activity.mood.StatusActivity;
import com.thfw.mobileheart.activity.settings.HelpBackActivity;
import com.thfw.mobileheart.activity.settings.InfoActivity;
import com.thfw.mobileheart.activity.settings.SettingActivity;
import com.thfw.mobileheart.activity.task.MeTaskActivity;
import com.thfw.mobileheart.activity.test.TestReportActivity;
import com.thfw.mobileheart.constants.UIConfig;
import com.thfw.mobileheart.lhxk.LhXkHelper;
import com.thfw.mobileheart.util.DialogFactory;
import com.thfw.mobileheart.util.FunctionDurationUtil;
import com.thfw.mobileheart.util.MoodLivelyHelper;
import com.thfw.ui.dialog.TDialog;
import com.thfw.ui.dialog.base.BindViewHolder;
import com.thfw.ui.utils.GlideUtil;
import com.thfw.user.login.LoginStatus;
import com.thfw.user.login.UserManager;
import com.thfw.user.login.UserObserver;
import com.thfw.user.models.User;
import com.trello.rxlifecycle2.LifecycleProvider;

import org.jetbrains.annotations.NotNull;

/**
 * 我的
 */
public class MeFragment extends BaseFragment implements MoodLivelyHelper.MoodLivelyListener, MyApplication.OnMinuteListener {

    public static final String KEY_INPUT_FACE = "key.input.face";
    private static boolean initFaceState;
    private RoundedImageView mRivAvatar;
    private TextView mTvName;
    private TextView mTvStatus;
    private ConstraintLayout mMeMessage;
    private LinearLayout mLlStatus;
    private ImageView mIvCall;
    private LinearLayout mLlTimeMinute;
    private TextView mTvTimeMinute;
    private LinearLayout mLlTimeDay;
    private TextView mTvTimeDay;
    private LinearLayout mLlTimeContinuationDay;
    private TextView mTvTimeContinuation;
    private LinearLayout mLlHistoryTest;
    private LinearLayout mLlHistoryExercise;
    private LinearLayout mLlHistoryListening;
    private LinearLayout mLlHistorySee;
    private LinearLayout mLlHistoryRead;
    private LinearLayout mLlHistoryStudy;
    private LinearLayout mLlSafeReport;
    private LinearLayout mLlHelpBack;
    private LinearLayout mLlSetting;
    private LinearLayout mLlMeInfo;
    private LinearLayout mLlMeCollect;
    private LinearLayout mLlMeTask;
    private Button mBtLogout;
    private TextView mTvMeLevel;
    private TextView mTvHistoryHappy;
    private TextView mTvMsgVersion;
    private LinearLayout mLlMeFace;
    private TextView mTvFaceSwitch;
    private ImageView mIvMoodStatus;

    public static void resetInitFaceState() {
        initFaceState = false;
    }

    @Override
    public int getContentView() {
        return R.layout.fragment_me;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initView() {
        mIvMoodStatus = (ImageView) findViewById(R.id.iv_mood_status);
        mRivAvatar = (RoundedImageView) findViewById(R.id.riv_avatar);
        mTvName = (TextView) findViewById(R.id.tv_name);
        mTvMeLevel = (TextView) findViewById(R.id.tv_me_level);
        mTvMsgVersion = (TextView) findViewById(R.id.tv_massage_version);
        mTvStatus = (TextView) findViewById(R.id.tv_status);
        View.OnClickListener listener = v -> {
            if (UserManager.getInstance().isTrueLogin()) {
                startActivity(new Intent(mContext, InfoActivity.class));
            } else {
                LoginActivity.startActivity(mContext, LoginActivity.BY_MOBILE);
            }
        };
        mRivAvatar.setOnClickListener(listener);
        mTvMeLevel.setOnClickListener(listener);
        mTvName.setOnClickListener(listener);

        mMeMessage = (ConstraintLayout) findViewById(R.id.me_message);
        mLlStatus = (LinearLayout) findViewById(R.id.ll_status);
        mIvCall = (ImageView) findViewById(R.id.iv_call);
        mLlTimeMinute = (LinearLayout) findViewById(R.id.ll_time_minute);
        mTvTimeMinute = (TextView) findViewById(R.id.tv_time_minute);
        mLlTimeDay = (LinearLayout) findViewById(R.id.ll_time_day);
        mTvTimeDay = (TextView) findViewById(R.id.tv_time_day);
        mLlTimeContinuationDay = (LinearLayout) findViewById(R.id.ll_time_continuation_day);
        mTvTimeContinuation = (TextView) findViewById(R.id.tv_time_continuation);
        mLlHistoryTest = (LinearLayout) findViewById(R.id.ll_history_test);
        mLlHistoryExercise = (LinearLayout) findViewById(R.id.ll_history_exercise);
        mLlHistoryListening = (LinearLayout) findViewById(R.id.ll_history_listening);
        mLlHistorySee = (LinearLayout) findViewById(R.id.ll_history_see);
        mLlHistoryRead = (LinearLayout) findViewById(R.id.ll_history_read);
        mLlHistoryStudy = (LinearLayout) findViewById(R.id.ll_history_study);
        mLlSafeReport = (LinearLayout) findViewById(R.id.ll_safe_report);
        mLlHelpBack = (LinearLayout) findViewById(R.id.ll_help_back);
        mLlSetting = (LinearLayout) findViewById(R.id.ll_setting);

        mLlMeInfo = (LinearLayout) findViewById(R.id.ll_me_info);
        mLlMeCollect = (LinearLayout) findViewById(R.id.ll_me_collect);
        mLlMeTask = (LinearLayout) findViewById(R.id.ll_me_task);
        mTvHistoryHappy = (TextView) findViewById(R.id.tv_history_happy);
        mBtLogout = (Button) findViewById(R.id.bt_logout);
        findViewById(R.id.v_top_banner).setOnClickListener(v -> {
            if (ClickCountUtils.click(10)) {
                startActivity(new Intent(mContext, PrivateSetActivity.class));
            }
        });
        // todo 我的任务
        mLlMeTask.setOnClickListener(v -> {
            startActivity(new Intent(mContext, MeTaskActivity.class));
        });
        mMeMessage.setOnClickListener(v -> {
            startActivity(new Intent(mContext, SettingActivity.class));
        });
        mLlMeInfo.setOnClickListener(v -> {
            if (UserManager.getInstance().isTrueLogin()) {
                startActivity(new Intent(mContext, InfoActivity.class));
            } else {
                LoginActivity.startActivity(mContext, LoginActivity.BY_MOBILE);
            }
        });
        // 历史心情
        mTvHistoryHappy.setOnClickListener(v -> {
            MoodDetailActivity.startActivity(mContext);
        });
        mBtLogout.setOnClickListener(v -> {
            logoutDialog();
        });
        mLlMeCollect.setOnClickListener(v -> {
            CollectActivity.startActivity(mContext);
        });
        mLlSetting.setOnClickListener(v -> {
            startActivity(new Intent(mContext, SettingActivity.class));
        });
        mLlStatus.setOnClickListener(v -> {
            StatusActivity.startActivity(mContext);
        });

        findViewById(R.id.ll_time_all).setOnClickListener(v -> {
            MoodDetailActivity.startActivity(mContext);
        });
        mLlHelpBack.setOnClickListener(v -> {
            startActivity(new Intent(mContext, HelpBackActivity.class));
        });
        mLlSafeReport.setOnClickListener(v -> {
            startActivity(new Intent(mContext, TestReportActivity.class));
        });

        // 历史
        mLlHistoryTest.setOnClickListener(v -> MeHistoryActivity.startActivity(mContext, 0));
        mLlHistoryExercise.setOnClickListener(v -> MeHistoryActivity.startActivity(mContext, 1));
        mLlHistoryListening.setOnClickListener(v -> MeHistoryActivity.startActivity(mContext, 2));
        mLlHistorySee.setOnClickListener(v -> MeHistoryActivity.startActivity(mContext, 3));
        mLlHistoryRead.setOnClickListener(v -> MeHistoryActivity.startActivity(mContext, 4));
        mLlHistoryStudy.setOnClickListener(v -> MeHistoryActivity.startActivity(mContext, 5));

        mLlMeFace = (LinearLayout) findViewById(R.id.ll_me_face);
        mTvFaceSwitch = (TextView) findViewById(R.id.tv_face_switch);
        SmartRefreshLayout smartRefresh = (SmartRefreshLayout) findViewById(R.id.smart_refresh);
        smartRefresh.setEnableRefresh(false); // 是否启用下拉刷新功能
        smartRefresh.setEnableLoadMore(false); // 是否启用上拉加载功能
        smartRefresh.setEnablePureScrollMode(true); // 是否启用纯滚动模式
        smartRefresh.setEnableOverScrollBounce(true); // 是否启用越界回弹
        smartRefresh.setEnableOverScrollDrag(true); // 是否启用越界拖动（仿苹果效果）1.0.4
    }


    @Override
    public void initData() {
        setUserMsg();
    }

    private void logoutDialog() {
        DialogFactory.createCustomDialog(getActivity(), new DialogFactory.OnViewCallBack() {
            @Override
            public void callBack(TextView mTvTitle, TextView mTvHint, TextView mTvLeft, TextView mTvRight, View mVLineVertical) {
                mTvHint.setText("确认退出登录吗");
                mTvTitle.setVisibility(View.GONE);
            }

            @Override
            public void onViewClick(BindViewHolder viewHolder, View view, TDialog tDialog) {
                if (view.getId() == R.id.tv_left) {
                    tDialog.dismiss();
                } else {
                    tDialog.dismiss();
                    UserManager.getInstance().logout(LoginStatus.LOGOUT_EXIT);
                    if (!UserManager.getInstance().isTrueLogin()) {
                        LoginActivity.startActivity(mContext, LoginActivity.BY_OTHER);
                    }
                }
            }
        });
    }

    @Override
    public UserObserver addObserver() {
        return new UserObserver() {
            @Override
            public void onChanged(UserManager accountManager, User user) {
                setUserMsg();
                if (accountManager.isNewLogin()) {
                    HandlerUtil.getMainHandler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            MoodLivelyHelper.addListener(MeFragment.this);
                        }
                    }, 1000);
                }
            }
        };
    }

    private void setUserMsg() {
        if (UserManager.getInstance().isTrueLogin()) {
            User user = UserManager.getInstance().getUser();
            mTvName.setText(user.getVisibleName());
            mTvMeLevel.setText(user.getOrganListStr());
            GlideUtil.load(mContext, user.getVisibleAvatar(), mRivAvatar);
            setInputState();
        } else {
            // 活跃时长
            mTvTimeMinute.setText("");
            mTvTimeContinuation.setText("");
            mTvTimeDay.setText("");
            // 姓名昵称
            mTvName.setText("");
            mTvMeLevel.setText("");
            GlideUtil.load(mContext, R.mipmap.ic_launcher, mRivAvatar);
            setInputState(-1);
        }
    }


    @Override
    public void onVisible(boolean isVisible) {
        super.onVisible(isVisible);
        if (isVisible) {
            checkVersion();
            onChanged();
        }
    }

    private void setInputState() {
        if (initFaceState) {
            int inputState = SharePreferenceUtil.getInt(KEY_INPUT_FACE + UserManager.getInstance().getUID(), -1);
            setInputState(inputState);
            return;
        }
        new LoginPresenter(new LoginPresenter.LoginUi<HttpResult<CommonModel>>() {
            @Override
            public LifecycleProvider getLifecycleProvider() {
                return null;
            }

            @Override
            public void onSuccess(HttpResult<CommonModel> data) {
                if (data.isSuccessful()) {
                    initFaceState = true;
                    SharePreferenceUtil.setInt(KEY_INPUT_FACE + UserManager.getInstance().getUID(), 1);
                    setInputState(1);
                } else {
                    initFaceState = true;
                    SharePreferenceUtil.setInt(KEY_INPUT_FACE + UserManager.getInstance().getUID(), 0);
                    setInputState(0);
                }

            }

            @Override
            public void onFail(ResponeThrowable throwable) {
                initFaceState = false;
                SharePreferenceUtil.setInt(KEY_INPUT_FACE + UserManager.getInstance().getUID(), -1);
                setInputState(-1);
            }
        }).onIsFaceOpen();
    }

    private void setInputState(int inputState) {
        if (inputState == 1) {
            mTvFaceSwitch.setText("已录入");
            mLlMeFace.setOnClickListener(v -> {
                if (ClickCountUtils.click(10)) {
                    LoginActivity.startActivity(mContext, LoginActivity.BY_FACE);
                }
            });
        } else if (inputState == 0) {
            mTvFaceSwitch.setText("未录入");
            mLlMeFace.setOnClickListener(v -> {
                if (getActivity() instanceof BaseActivity) {
                    BaseActivity baseActivity = (BaseActivity) getActivity();
                    baseActivity.requestCallPermission(new String[]{UIConfig.NEEDED_PERMISSION[0]}, new PermissionListener() {
                        @Override
                        public void onPermission(boolean has) {
                            if (has) {
                                LoginActivity.startActivity(mContext, LoginActivity.BY_FACE);
                            }
                        }
                    });
                }

            });
        } else {
            mTvFaceSwitch.setText("");
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
                if (isVisible()) {
                    mTvMsgVersion.setText("新");
                    mTvMsgVersion.setVisibility(hasNewVersion ? View.VISIBLE : View.GONE);
                }
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MoodLivelyHelper.addListener(this);
        MyApplication.getApp().addOnMinuteListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        MoodLivelyHelper.removeListener(this);
        MyApplication.getApp().onRemoveOnMinuteListener(this);
    }

    @Override
    public void onMoodLively(MoodLivelyModel data) {
        if (data != null) {
            mTvTimeMinute.setText(FunctionDurationUtil.getFunctionTimeHour(FunctionType.FUNCTION_APP));
            mTvTimeContinuation.setText(String.valueOf(data.getContinueDays()));
            mTvTimeDay.setText(String.valueOf(data.getLoginDays()));
        } else {
            mTvTimeMinute.setText("");
            mTvTimeContinuation.setText("");
            mTvTimeDay.setText("");
        }


        if (data != null && data.getUserMood() != null) {
            GlideUtil.load(mContext, data.getUserMood().getPath(), mIvMoodStatus);
            mTvStatus.setText(data.getUserMood().getName());
        } else {
            GlideUtil.load(mContext, R.drawable.gray_cirlle_bg, mIvMoodStatus);
            mTvStatus.setText(getResources().getString(R.string.mood_defalut_hint));
        }
    }

    @Override
    public void onChanged() {
        if (mTvTimeMinute != null) {
            mTvTimeMinute.setText(FunctionDurationUtil.getFunctionTimeHour(FunctionType.FUNCTION_APP));
        }
    }

    @Override
    protected void initLocalVoice(int type) {
        super.initLocalVoice(type);
        LhXkHelper.putAction(HomeFragment.class, new SpeechToAction("活跃,历史心情", () -> MoodDetailActivity.startActivity(mContext)).setLike(true));
        LhXkHelper.putAction(HomeFragment.class, new SpeechToAction("我测的", () -> MeHistoryActivity.startActivity(mContext, 0)));
        LhXkHelper.putAction(HomeFragment.class, new SpeechToAction("我练的", () -> MeHistoryActivity.startActivity(mContext, 1)));
        LhXkHelper.putAction(HomeFragment.class, new SpeechToAction("我听的", () -> MeHistoryActivity.startActivity(mContext, 2)));
        LhXkHelper.putAction(HomeFragment.class, new SpeechToAction("我看的", () -> MeHistoryActivity.startActivity(mContext, 3)));
        LhXkHelper.putAction(HomeFragment.class, new SpeechToAction("我读的", () -> MeHistoryActivity.startActivity(mContext, 4)));
        LhXkHelper.putAction(HomeFragment.class, new SpeechToAction("我学的", () -> MeHistoryActivity.startActivity(mContext, 5)));
        LhXkHelper.putAction(HomeFragment.class, new SpeechToAction("心理健康档案,健康档案", () -> mLlSafeReport.performClick()));
        LhXkHelper.putAction(HomeFragment.class, new SpeechToAction("个人信息", () -> InfoActivity.startActivity(mContext)));

        LhXkHelper.putAction(HomeFragment.class, new SpeechToAction("人脸录入", () -> mLlMeFace.performClick()));
        LhXkHelper.putAction(HomeFragment.class, new SpeechToAction("我的收藏", () -> mLlMeCollect.performClick()));
        LhXkHelper.putAction(HomeFragment.class, new SpeechToAction("我的任务", () -> mLlMeTask.performClick()));
        LhXkHelper.putAction(HomeFragment.class, new SpeechToAction("帮助与反馈", () -> mLlHelpBack.performClick()));
        LhXkHelper.putAction(HomeFragment.class, new SpeechToAction("设置", () -> mLlSetting.performClick()));
        LhXkHelper.putAction(HomeFragment.class, new SpeechToAction("退出登录", () -> mBtLogout.performClick()).setLike(true));

    }
}