package com.thfw.robotheart.fragments.sets;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.provider.Settings;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.thfw.base.base.IPresenter;
import com.thfw.robotheart.R;
import com.thfw.robotheart.activitys.RobotBaseFragment;
import com.thfw.robotheart.view.DialogRobotFactory;
import com.thfw.ui.dialog.TDialog;
import com.thfw.ui.dialog.base.BindViewHolder;

/**
 * Author:pengs
 * Date: 2021/12/1 9:56
 * Describe:Todo
 */
public class SetVolumeFragment extends RobotBaseFragment {

    private Switch mSwitchAllVolume;
    private LinearLayout mLlSeekbarSystemVolume;
    private TextView mTvSystemVolumeProgress;
    private SeekBar mSbSystemVolume;
    private Switch mSwitchSysetmVolume;
    private LinearLayout mLlSeekbarHintVolume;
    private TextView mTvHintVolumeProgress;
    private SeekBar mSbHintVolume;
    private Switch mSwitchHintVolume;
    private LinearLayout mLlSeekbarTintVolume;
    private TextView mTvTintVolumeProgress;
    private SeekBar mSbBtnVolume;
    private Switch mSwitchBtnVolume;
    private LinearLayout mLlSeekbarBtnVolume;
    private TextView mTvBtnVolumeProgress;
    private Runnable mUiRunnable;
    private int maxMusicVolume;
    private int maxSystemVolume;
    private int maxNotificationVolume;
    private AudioManager mAudioManager;

    @Override
    public int getContentView() {
        return R.layout.fragment_set_volume;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initView() {

        //初始化获取音量属性
        mAudioManager = (AudioManager) getActivity().getSystemService(Service.AUDIO_SERVICE);
        maxMusicVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);// 媒体
        maxSystemVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);// 系统
        maxNotificationVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION);// 通知


        mSwitchAllVolume = (Switch) findViewById(R.id.switch_all_volume);
        mLlSeekbarSystemVolume = (LinearLayout) findViewById(R.id.ll_seekbar_system_volume);
        mTvSystemVolumeProgress = (TextView) findViewById(R.id.tv_system_volume_progress);
        mSbSystemVolume = (SeekBar) findViewById(R.id.sb_system_volume);
        mSwitchSysetmVolume = (Switch) findViewById(R.id.switch_sysetm_volume);


        mLlSeekbarHintVolume = (LinearLayout) findViewById(R.id.ll_seekbar_hint_volume);
        mTvHintVolumeProgress = (TextView) findViewById(R.id.tv_hint_volume_progress);
        mSbHintVolume = (SeekBar) findViewById(R.id.sb_hint_volume);
        mSwitchHintVolume = (Switch) findViewById(R.id.switch_hint_volume);

        mSbBtnVolume = (SeekBar) findViewById(R.id.sb_btn_volume);
        mSwitchBtnVolume = (Switch) findViewById(R.id.switch_btn_volume);
        mLlSeekbarBtnVolume = (LinearLayout) findViewById(R.id.ll_seekbar_btn_volume);
        mTvBtnVolumeProgress = (TextView) findViewById(R.id.tv_btn_volume_progress);


    }

    protected void textProgressChanged(int progress, TextView textProgress, SeekBar seekBar) {
        textProgress.setText(progress + "%");
        textProgress.post(() -> {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) textProgress.getLayoutParams();
            int leftMargin = seekBar.getWidth() * progress / 100 - textProgress.getWidth() * progress / 100;
            if (progress == 100) {
                leftMargin = leftMargin - textProgress.getWidth() / textProgress.getText().length();
            }
            lp.leftMargin = leftMargin;
            textProgress.setLayoutParams(lp);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        textProgressChanged(mSbSystemVolume.getProgress(), mTvSystemVolumeProgress, mSbSystemVolume);
        textProgressChanged(mSbHintVolume.getProgress(), mTvHintVolumeProgress, mSbHintVolume);
        textProgressChanged(mSbBtnVolume.getProgress(), mTvBtnVolumeProgress, mSbBtnVolume);


        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && !notificationManager.isNotificationPolicyAccessGranted()) {
            showPermission();
            return;
        }

        // 系统音量
        mSbSystemVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textProgressChanged(progress, mTvSystemVolumeProgress, mSbSystemVolume);
                if (fromUser) {
                    mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, maxSystemVolume * progress / 100, AudioManager.FLAG_PLAY_SOUND);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        // 媒体音量
        mSbHintVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textProgressChanged(progress, mTvHintVolumeProgress, mSbHintVolume);
                if (fromUser) {
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxMusicVolume * progress / 100, AudioManager.FLAG_PLAY_SOUND);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        // 按键音量
        mSbBtnVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textProgressChanged(progress, mTvBtnVolumeProgress, mSbBtnVolume);
                if (fromUser) {
                    mAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, maxNotificationVolume * progress / 100, AudioManager.FLAG_PLAY_SOUND);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mSbSystemVolume.setProgress(mAudioManager.getStreamVolume(AudioManager.STREAM_SYSTEM) * 100 / maxSystemVolume);
        mSbHintVolume.setProgress(mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) * 100 / maxMusicVolume);
        mSbBtnVolume.setProgress(mAudioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION) * 100 / maxNotificationVolume);
    }

    /**
     * 静音权限开启提醒弹框
     */
    private void showPermission() {
        DialogRobotFactory.createCustomDialog(getActivity(), new DialogRobotFactory.OnViewCallBack() {

            @Override
            public void onViewClick(BindViewHolder viewHolder, View view, TDialog tDialog) {
                if (view.getId() == R.id.tv_right) {
                    startActivity(new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS));
                }
                tDialog.dismiss();
            }

            @Override
            public void callBack(TextView mTvTitle, TextView mTvHint, TextView mTvLeft, TextView mTvRight, View mVLineVertical) {
                mTvHint.setText("需要为 " + getResources().getString(R.string.app_name) + " 开启静音权限?");
                mTvTitle.setVisibility(View.GONE);
                mTvLeft.setText("以后");
                mTvRight.setText("现在");
            }
        });
    }

    @Override
    public void initData() {

    }

}
