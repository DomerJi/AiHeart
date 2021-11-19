package com.thfw.mobileheart.fragment;

import android.content.Intent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.makeramen.roundedimageview.RoundedImageView;
import com.thfw.base.base.IPresenter;
import com.thfw.mobileheart.activity.StatusActivity;
import com.thfw.mobileheart.activity.heartbox.HeartBoxActivity;
import com.thfw.mobileheart.activity.integral.ClockInActivity;
import com.thfw.mobileheart.activity.login.LoginActivity;
import com.thfw.mobileheart.activity.me.AssessReportActivity;
import com.thfw.mobileheart.activity.me.CollectActivity;
import com.thfw.mobileheart.activity.me.HealthReportActivity;
import com.thfw.mobileheart.activity.me.MeHistoryActivity;
import com.thfw.mobileheart.activity.me.MeTestHistoryActivity;
import com.thfw.mobileheart.activity.settings.HelpBackActivity;
import com.thfw.mobileheart.activity.settings.InfoActivity;
import com.thfw.mobileheart.activity.settings.MeAskListActivity;
import com.thfw.mobileheart.activity.settings.SettingActivity;
import com.thfw.mobileheart.R;
import com.thfw.ui.base.BaseFragment;
import com.thfw.user.login.UserManager;

/**
 * 我的
 */
public class MeFragment extends BaseFragment {

    private RoundedImageView mRivAvatar;
    private TextView mTvName;
    private TextView mTvStatus;
    private ConstraintLayout mMeMessage;
    private LinearLayout mLlStatus;
    private TextView mTvAccompanyDay;
    private ImageView mIvCall;
    private LinearLayout mLlQuit;
    private TextView mTvQuizNumber;
    private LinearLayout mLlCollect;
    private TextView mTvCollectNumber;
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
    private LinearLayout mLlHaertBox;
    private LinearLayout mLlTestReport;
    private LinearLayout mLlSafeReport;
    private LinearLayout mLlHelpBack;
    private LinearLayout mLlSetting;

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

        mRivAvatar = (RoundedImageView) findViewById(R.id.riv_avatar);
        mTvName = (TextView) findViewById(R.id.tv_name);
        mTvStatus = (TextView) findViewById(R.id.tv_status);
        mRivAvatar.setOnClickListener(v -> {
            if (UserManager.getInstance().isLogin()) {
                startActivity(new Intent(mContext, InfoActivity.class));
            } else {
                LoginActivity.startActivity(mContext, LoginActivity.BY_MOBILE);
            }
        });

        mMeMessage = (ConstraintLayout) findViewById(R.id.me_message);
        mLlStatus = (LinearLayout) findViewById(R.id.ll_status);
        mTvAccompanyDay = (TextView) findViewById(R.id.tv_accompany_day);
        mIvCall = (ImageView) findViewById(R.id.iv_call);
        mLlQuit = (LinearLayout) findViewById(R.id.ll_quit);
        mTvQuizNumber = (TextView) findViewById(R.id.tv_quiz_number);
        mLlCollect = (LinearLayout) findViewById(R.id.ll_collect);
        mLlCollect.setOnClickListener(v -> {
            CollectActivity.startActivity(mContext);
        });
        mTvCollectNumber = (TextView) findViewById(R.id.tv_collect_number);
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
        mLlHaertBox = (LinearLayout) findViewById(R.id.ll_haert_box);
        mLlTestReport = (LinearLayout) findViewById(R.id.ll_test_report);
        mLlSafeReport = (LinearLayout) findViewById(R.id.ll_safe_report);
        mLlHelpBack = (LinearLayout) findViewById(R.id.ll_help_back);
        mLlSetting = (LinearLayout) findViewById(R.id.ll_setting);
        mLlSetting.setOnClickListener(v -> {
            startActivity(new Intent(mContext, SettingActivity.class));
        });
        mLlStatus.setOnClickListener(v -> {
            startActivity(new Intent(mContext, StatusActivity.class));
        });
        mLlHaertBox.setOnClickListener(v -> {
            startActivity(new Intent(mContext, HeartBoxActivity.class));
        });
        mLlTimeContinuationDay.setOnClickListener(v -> {
            startActivity(new Intent(mContext, ClockInActivity.class));
        });
        mLlHelpBack.setOnClickListener(v -> {
            startActivity(new Intent(mContext, HelpBackActivity.class));
        });
        mLlQuit.setOnClickListener(v -> {
            startActivity(new Intent(mContext, MeAskListActivity.class));
        });
        mLlTestReport.setOnClickListener(v -> {
            startActivity(new Intent(mContext, AssessReportActivity.class));
        });
        mLlSafeReport.setOnClickListener(v -> {
            startActivity(new Intent(mContext, HealthReportActivity.class));
        });

        // 历史
        mLlHistoryTest.setOnClickListener(v -> MeTestHistoryActivity.startActivity(mContext));
        mLlHistoryExercise.setOnClickListener(v -> MeHistoryActivity.startActivity(mContext, 0));
        mLlHistoryListening.setOnClickListener(v -> MeHistoryActivity.startActivity(mContext, 1));
        mLlHistorySee.setOnClickListener(v -> MeHistoryActivity.startActivity(mContext, 2));
        mLlHistoryRead.setOnClickListener(v -> MeHistoryActivity.startActivity(mContext, 3));
        mLlHistoryStudy.setOnClickListener(v -> MeHistoryActivity.startActivity(mContext, 4));
    }


    @Override
    public void initData() {

    }
}