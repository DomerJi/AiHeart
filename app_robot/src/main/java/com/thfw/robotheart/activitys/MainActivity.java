package com.thfw.robotheart.activitys;

import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.thfw.base.base.IPresenter;
import com.thfw.base.models.TalkModel;
import com.thfw.base.utils.ClickCountUtils;
import com.thfw.robotheart.R;
import com.thfw.robotheart.activitys.audio.AudioHomeActivity;
import com.thfw.robotheart.activitys.exercise.ExerciseActivity;
import com.thfw.robotheart.activitys.me.HotPhoneActivity;
import com.thfw.robotheart.activitys.me.MeActivity;
import com.thfw.robotheart.activitys.me.PrivateSetActivity;
import com.thfw.robotheart.activitys.me.SettingActivity;
import com.thfw.robotheart.activitys.talk.AiTalkActivity;
import com.thfw.robotheart.activitys.talk.ThemeTalkActivity;
import com.thfw.robotheart.activitys.test.TestActivity;
import com.thfw.robotheart.activitys.text.BookActivity;
import com.thfw.robotheart.activitys.text.BookStudyActivity;
import com.thfw.robotheart.activitys.video.VideoHomeActivity;
import com.thfw.robotheart.view.TitleBarView;
import com.thfw.ui.base.RobotBaseActivity;
import com.thfw.ui.widget.WeekView;
import com.thfw.user.login.User;
import com.thfw.user.login.UserManager;
import com.thfw.user.login.UserObserver;

public class MainActivity extends RobotBaseActivity implements View.OnClickListener {

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

        mLlTest.setOnClickListener(this);
        mLlTalk.setOnClickListener(this);
        mLlMusic.setOnClickListener(this);
        mLlVideo.setOnClickListener(this);
        mLlExercise.setOnClickListener(this);
        mLlBook.setOnClickListener(this);
        mLlStudy.setOnClickListener(this);
        mLlHotCall.setOnClickListener(this);
        mLlSetting.setOnClickListener(this);
        mLlMe.setOnClickListener(this);
        mRlSpecialityTalk.setOnClickListener(this);
    }


    @Override
    public void initData() {
        mWeekView.setOnClickListener(v -> {
            if (ClickCountUtils.click(15)) {
                startActivity(new Intent(mContext, PrivateSetActivity.class));
            }
        });
        if (UserManager.getInstance().isLogin()) {
            mTvInstitution.setText(UserManager.getInstance().getUser().getOrganListStr());
        }
    }

    @Override
    public void onClick(View v) {
        int vId = v.getId();
        if (vId == R.id.ll_me) {
            startActivity(new Intent(mContext, MeActivity.class));
        } else if (vId == R.id.ll_setting) {
            startActivity(new Intent(mContext, SettingActivity.class));
        } else if (vId == R.id.ll_music) {
            startActivity(new Intent(mContext, AudioHomeActivity.class));
        } else if (vId == R.id.ll_test) {
            startActivity(new Intent(mContext, TestActivity.class));
        } else if (vId == R.id.ll_video) {
            startActivity(new Intent(mContext, VideoHomeActivity.class));
        } else if (vId == R.id.ll_exercise) {
            startActivity(new Intent(mContext, ExerciseActivity.class));
        } else if (vId == R.id.rl_speciality_talk) {
            startActivity(new Intent(mContext, ThemeTalkActivity.class));
        } else if (vId == R.id.ll_talk) {
            AiTalkActivity.startActivity(mContext,new TalkModel(TalkModel.TYPE_AI));
        } else if (vId == R.id.ll_hot_call) {
            startActivity(new Intent(mContext, HotPhoneActivity.class));
        } else if (vId == R.id.ll_study) {
            startActivity(new Intent(mContext, BookStudyActivity.class));
        } else if (vId == R.id.ll_book) {
            startActivity(new Intent(mContext, BookActivity.class));
        }
    }

    @Override
    public UserObserver addObserver() {
        return new UserObserver() {
            @Override
            public void onChanged(UserManager accountManager, User user) {
                mTvInstitution.setText(user.getOrganListStr());
            }
        };
    }

}