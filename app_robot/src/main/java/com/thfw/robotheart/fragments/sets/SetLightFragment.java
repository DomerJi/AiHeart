package com.thfw.robotheart.fragments.sets;

import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.thfw.base.base.IPresenter;
import com.thfw.robotheart.R;
import com.thfw.robotheart.activitys.RobotBaseFragment;
import com.thfw.ui.widget.BrightnessHelper;

/**
 * Author:pengs
 * Date: 2021/12/1 9:56
 * Describe:Todo
 */
public class SetLightFragment extends RobotBaseFragment {

    private RelativeLayout mRlTop;
    private Switch mSwitchAllLight;
    private RoundedImageView mRivLight;
    private LinearLayout mLlSeekbarHintVolume;
    private TextView mTvHintLightProgress;
    private SeekBar mSbHintLight;

    @Override
    public int getContentView() {
        return R.layout.fragment_set_light;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initView() {

        mRlTop = (RelativeLayout) findViewById(R.id.rl_top);
        mSwitchAllLight = (Switch) findViewById(R.id.switch_all_light);
        mRivLight = (RoundedImageView) findViewById(R.id.riv_light);
        mLlSeekbarHintVolume = (LinearLayout) findViewById(R.id.ll_seekbar_hint_volume);
        mTvHintLightProgress = (TextView) findViewById(R.id.tv_hint_light_progress);
        mSbHintLight = (SeekBar) findViewById(R.id.sb_hint_light);
    }

    @Override
    public void initData() {

        BrightnessHelper mBrightnessHelper = new BrightnessHelper(mContext);
        // 亮度
        mSbHintLight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textProgressChanged(progress, mTvHintLightProgress, mSbHintLight);
                if (fromUser) {
                    mBrightnessHelper.setBrightness(progress / 100f, getActivity());
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        mSbHintLight.setProgress((int) (mBrightnessHelper.getBrightness() * 100));


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
        textProgressChanged(mSbHintLight.getProgress(), mTvHintLightProgress, mSbHintLight);
    }
}
