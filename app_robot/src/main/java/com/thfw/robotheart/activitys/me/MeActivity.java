package com.thfw.robotheart.activitys.me;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.thfw.base.api.HistoryApi;
import com.thfw.base.base.IPresenter;
import com.thfw.base.models.CommonModel;
import com.thfw.base.net.HttpResult;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.LoginPresenter;
import com.thfw.base.utils.ClickCountUtils;
import com.thfw.base.utils.SharePreferenceUtil;
import com.thfw.robotheart.R;
import com.thfw.robotheart.activitys.RobotBaseActivity;
import com.thfw.robotheart.activitys.login.LoginActivity;
import com.thfw.robotheart.activitys.task.MsgActivity;
import com.thfw.robotheart.activitys.task.TaskActivity;
import com.thfw.robotheart.activitys.test.TestReportActivity;
import com.thfw.robotheart.util.DialogRobotFactory;
import com.thfw.robotheart.util.MsgCountManager;
import com.thfw.robotheart.view.TitleRobotView;
import com.thfw.ui.dialog.TDialog;
import com.thfw.ui.dialog.base.BindViewHolder;
import com.thfw.ui.utils.GlideUtil;
import com.thfw.ui.voice.tts.TtsHelper;
import com.thfw.ui.voice.tts.TtsModel;
import com.thfw.user.login.LoginStatus;
import com.thfw.user.login.UserManager;
import com.thfw.user.models.User;
import com.trello.rxlifecycle2.LifecycleProvider;

public class MeActivity extends RobotBaseActivity implements MsgCountManager.OnCountChangeListener {

    public static final String KEY_INPUT_FACE = "key.input.face";
    private static boolean initFaceState;
    private TitleRobotView mTitleRobotView;
    private com.makeramen.roundedimageview.RoundedImageView mRivAvatar;
    private android.widget.TextView mTvNickname;
    private android.widget.LinearLayout mLlHistory;
    private android.widget.LinearLayout mLlTest;
    private android.widget.LinearLayout mLlExercise;
    private android.widget.LinearLayout mLlSee;
    private android.widget.LinearLayout mLlMusic;
    private android.widget.LinearLayout mLlRead;
    private android.widget.LinearLayout mLlStudy;
    private android.widget.RelativeLayout mRlMsg;
    private android.widget.RelativeLayout mRlCollection;
    private android.widget.RelativeLayout mRlWork;
    private android.widget.RelativeLayout mRlBackHelp;
    private android.widget.RelativeLayout mRlAccountManager;
    private RelativeLayout mRlMeMsg;
    private android.widget.Button mBtLogout;
    private RelativeLayout mRlFace;
    private TextView mTvInputState;
    private TextView mTvTeam;
    private RelativeLayout mRlReport;
    private TextView mTvMeMsgTitle;
    private TextView mTvDotCount;

    public static void resetInitFaceState() {
        MeActivity.initFaceState = false;
    }

    @Override
    public int getContentView() {
        return R.layout.activity_me;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initView() {

        mTitleRobotView = (TitleRobotView) findViewById(R.id.titleRobotView);
        mRivAvatar = (RoundedImageView) findViewById(R.id.riv_avatar);
        mTvNickname = (TextView) findViewById(R.id.tv_nickname);
        mLlHistory = (LinearLayout) findViewById(R.id.ll_history);
        mLlTest = (LinearLayout) findViewById(R.id.ll_test);
        mLlExercise = (LinearLayout) findViewById(R.id.ll_exercise);
        mLlSee = (LinearLayout) findViewById(R.id.ll_see);
        mLlMusic = (LinearLayout) findViewById(R.id.ll_music);
        mLlRead = (LinearLayout) findViewById(R.id.ll_read);
        mLlStudy = (LinearLayout) findViewById(R.id.ll_study);
        mRlMsg = (RelativeLayout) findViewById(R.id.rl_msg);
        mRlCollection = (RelativeLayout) findViewById(R.id.rl_collection);
        mRlWork = (RelativeLayout) findViewById(R.id.rl_work);
        mRlBackHelp = (RelativeLayout) findViewById(R.id.rl_back_help);
        mRlAccountManager = (RelativeLayout) findViewById(R.id.rl_account_manager);
        mRlMeMsg = (RelativeLayout) findViewById(R.id.rl_me_msg);
        mBtLogout = (Button) findViewById(R.id.bt_logout);
        mRlFace = (RelativeLayout) findViewById(R.id.rl_face);
        mTvInputState = (TextView) findViewById(R.id.tv_input_state);
        mTvTeam = (TextView) findViewById(R.id.tv_team);
        mRlReport = (RelativeLayout) findViewById(R.id.rl_report);
        mTvMeMsgTitle = (TextView) findViewById(R.id.tv_me_msg_title);
        mTvDotCount = (TextView) findViewById(R.id.tv_dot_count);
    }

    @Override
    public void initData() {
        mRivAvatar.setOnClickListener(v -> {
            if (!UserManager.getInstance().isLogin()) {
                LoginActivity.startActivity(mContext, LoginActivity.BY_PASSWORD);
            }
        });

        mBtLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogRobotFactory.createCustomDialog(MeActivity.this, new DialogRobotFactory.OnViewCallBack() {

                    @Override
                    public void onViewClick(BindViewHolder viewHolder, View view, TDialog tDialog) {
                        if (view.getId() == R.id.tv_right) {
                            TtsHelper.getInstance().start(new TtsModel("拜拜，下次见哦"), null);
                            UserManager.getInstance().logout(LoginStatus.LOGOUT_EXIT);
                            finish();
                        }
                        tDialog.dismiss();
                    }

                    @Override
                    public void callBack(TextView mTvTitle, TextView mTvHint, TextView mTvLeft, TextView mTvRight, View mVLineVertical) {
                        mTvHint.setText("确定退出登录吗");
                        mTvTitle.setVisibility(View.GONE);
                        mTvLeft.setText("取消");
                        mTvRight.setText("确定");
                    }
                });

            }
        });

        mRlAccountManager.setOnClickListener(v -> {
            if (!UserManager.getInstance().isLogin()) {
                LoginActivity.startActivity(mContext, LoginActivity.BY_PASSWORD);
            } else {
                startActivity(new Intent(mContext, AccountManagerActivity.class));
            }
        });

        mRlMsg.setOnClickListener(v -> {
            if (!UserManager.getInstance().isLogin()) {
                LoginActivity.startActivity(mContext, LoginActivity.BY_PASSWORD);
            } else {
                startActivity(new Intent(mContext, InfoActivity.class));
            }
        });

        mLlExercise.setOnClickListener(v -> {
            HistoryActivity.startActivity(mContext, HistoryApi.TYPE_EXERCISE);
        });
        mLlTest.setOnClickListener(v -> {
            HistoryActivity.startActivity(mContext, HistoryApi.TYPE_TEST);
        });
        mLlStudy.setOnClickListener(v -> {
            HistoryActivity.startActivity(mContext, HistoryApi.TYPE_STUDY);
        });
        mLlMusic.setOnClickListener(v -> {
            HistoryActivity.startActivity(mContext, HistoryApi.TYPE_AUDIO);
        });
        mLlRead.setOnClickListener(v -> {
            HistoryActivity.startActivity(mContext, HistoryApi.TYPE_BOOK);
        });

        mRlCollection.setOnClickListener(v -> {
            startActivity(new Intent(mContext, CollectActivity.class));
        });

        mRlWork.setOnClickListener(v -> {
            startActivity(new Intent(mContext, TaskActivity.class));
        });

        mRlBackHelp.setOnClickListener(v -> {
            startActivity(new Intent(mContext, HelpBackActivity.class));
        });

        mLlSee.setOnClickListener(v -> {
            HistoryActivity.startActivity(mContext, HistoryApi.TYPE_VIDEO);
        });

        mRlReport.setOnClickListener(v -> {
            TestReportActivity.startActivity(mContext);
        });

        mRlMeMsg.setOnClickListener(v -> {
            startActivity(new Intent(mContext, MsgActivity.class));
        });
        MsgCountManager.getInstance().addOnCountChangeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (UserManager.getInstance().isLogin()) {
            setInputState();
            setUserMessage(UserManager.getInstance().getUser());
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
            mTvInputState.setText("已录入");
            mRlFace.setOnClickListener(v -> {
                if (ClickCountUtils.click(10)) {
                    LoginActivity.startActivity(mContext, LoginActivity.BY_FACE);
                }
            });
        } else if (inputState == 0) {
            mTvInputState.setText("未录入");
            mRlFace.setOnClickListener(v -> {
                LoginActivity.startActivity(mContext, LoginActivity.BY_FACE);
            });
        } else {
            mTvInputState.setText("");
            mRlFace.setOnClickListener(v -> {
            });
        }

    }

    private void setUserMessage(User user) {
        mTvTeam.setText(user.getOrganListStr());
        mTvNickname.setText(user.getVisibleName());
        GlideUtil.load(mContext, user.getVisibleAvatar(), mRivAvatar);
    }

    @Override
    public void onCount(int numTask, int numSystem) {
        MsgCountManager.setTextView(mTvDotCount, numTask + numSystem);
    }

    @Override
    public void onDestroy() {
        MsgCountManager.getInstance().removeOnCountChangeListener(this);
        super.onDestroy();
    }

    @Override
    public void onItemState(int id, boolean read) {

    }

    @Override
    public void onReadAll(int type) {

    }
}