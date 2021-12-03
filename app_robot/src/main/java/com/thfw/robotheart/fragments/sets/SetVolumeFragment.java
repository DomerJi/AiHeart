package com.thfw.robotheart.fragments.sets;

import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.thfw.base.base.IPresenter;
import com.thfw.robotheart.R;
import com.thfw.ui.base.RobotBaseFragment;

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

        mSwitchAllVolume = (Switch) findViewById(R.id.switch_all_volume);
        mLlSeekbarSystemVolume = (LinearLayout) findViewById(R.id.ll_seekbar_system_volume);
        mTvSystemVolumeProgress = (TextView) findViewById(R.id.tv_system_volume_progress);
        mSbSystemVolume = (SeekBar) findViewById(R.id.sb_system_volume);
        mSwitchSysetmVolume = (Switch) findViewById(R.id.switch_sysetm_volume);
        // 系统音量
        mSbSystemVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textProgressChanged(progress, mTvSystemVolumeProgress, mSbSystemVolume);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        mLlSeekbarHintVolume = (LinearLayout) findViewById(R.id.ll_seekbar_hint_volume);
        mTvHintVolumeProgress = (TextView) findViewById(R.id.tv_hint_volume_progress);
        mSbHintVolume = (SeekBar) findViewById(R.id.sb_hint_volume);
        mSwitchHintVolume = (Switch) findViewById(R.id.switch_hint_volume);

        // 提示音量
        mSbHintVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textProgressChanged(progress, mTvHintVolumeProgress, mSbHintVolume);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        mSbBtnVolume = (SeekBar) findViewById(R.id.sb_btn_volume);
        mSwitchBtnVolume = (Switch) findViewById(R.id.switch_btn_volume);
        mLlSeekbarBtnVolume = (LinearLayout) findViewById(R.id.ll_seekbar_btn_volume);
        mTvBtnVolumeProgress = (TextView) findViewById(R.id.tv_btn_volume_progress);

        // 提示音量
        mSbBtnVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textProgressChanged(progress, mTvBtnVolumeProgress, mSbBtnVolume);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

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
    }

    @Override
    public void initData() {

    }

}
