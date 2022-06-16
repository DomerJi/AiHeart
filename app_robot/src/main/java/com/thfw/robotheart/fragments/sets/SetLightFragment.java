package com.thfw.robotheart.fragments.sets;

import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.thfw.base.base.IPresenter;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.ToastUtil;
import com.thfw.base.utils.Util;
import com.thfw.robotheart.R;
import com.thfw.robotheart.activitys.RobotBaseFragment;
import com.thfw.ui.utils.BrightnessHelper;

/**
 * Author:pengs
 * Date: 2021/12/1 9:56
 * Describe:Todo
 */
public class SetLightFragment extends RobotBaseFragment {

    private RelativeLayout mRlTop;
    private Switch mSwitchAllLight;
    private RoundedImageView mRivLight;
    private RoundedImageView mRivLightBorder;
    private LinearLayout mLlSeekbarHintVolume;
    private TextView mTvHintLightProgress;
    private SeekBar mSbHintLight;
    private BrightnessHelper mBrightnessHelper;
    private TextView mTvAutoHint;

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
        mRivLightBorder = (RoundedImageView) findViewById(R.id.riv_light_border);
        mLlSeekbarHintVolume = (LinearLayout) findViewById(R.id.ll_seekbar_hint_volume);
        mTvHintLightProgress = (TextView) findViewById(R.id.tv_hint_light_progress);
        mSbHintLight = (SeekBar) findViewById(R.id.sb_hint_light);


        mTvAutoHint = (TextView) findViewById(R.id.tv_auto_hint);
    }

    @Override
    public void initData() {

        mBrightnessHelper = new BrightnessHelper(mContext);

        mSwitchAllLight.setChecked(!mBrightnessHelper.isOnAutoBrightness());
        mSwitchAllLight.setOnClickListener(v -> {
            boolean isChecked = mSwitchAllLight.isChecked();
            if (Util.isSystemApp(mContext.getPackageName())) {
                if (isChecked) {
                    mBrightnessHelper.offAutoBrightness();
                } else {
                    mBrightnessHelper.onAutoBrightness();
                }
            } else {
                ToastUtil.show("不支持自动亮度调节");
            }
            mSwitchAllLight.setChecked(!mBrightnessHelper.isOnAutoBrightness());
            setPageState();
        });

        setPageState();
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

    private Handler mHandler = new Handler(Looper.getMainLooper());

    private void onlyRefreshBrightness() {
        if (mSwitchAllLight.isChecked()) {
            return;
        }
        LogUtil.i(TAG, mSbHintLight.getProgress() + " progress ");
        mHandler.removeCallbacksAndMessages(null);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if ((mSwitchAllLight != null && mSwitchAllLight.isChecked())
                        || !isVisible() || EmptyUtil.isEmpty(getActivity())) {
                    return;
                }
                if (mSbHintLight != null) {
                    mSbHintLight.setProgress((int) (mBrightnessHelper.getAutoBrightness(getActivity()) * 100));
                    LogUtil.i(TAG, mSbHintLight.getProgress() + " progress ");
                }
                mHandler.postDelayed(this, 2000);
            }
        }, 1000);

    }

    @Override
    public void onVisible(boolean isVisible) {
        super.onVisible(isVisible);
        if (isVisible) {
            onlyRefreshBrightness();
        } else {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }

    private void setPageState() {
        if (!mSwitchAllLight.isChecked()) {
            mRivLight.setAlpha(0.35f);
            mRivLightBorder.setAlpha(0.35f);
            mLlSeekbarHintVolume.setAlpha(0.35f);
            mSbHintLight.setFocusable(false);
            mSbHintLight.setClickable(false);
            mSbHintLight.setEnabled(false);
            mSbHintLight.setSelected(false);
            onlyRefreshBrightness();
            mTvAutoHint.setVisibility(View.VISIBLE);
        } else {
            mRivLight.setAlpha(1f);
            mRivLightBorder.setAlpha(1f);
            mLlSeekbarHintVolume.setAlpha(1f);
            mSbHintLight.setFocusable(true);
            mSbHintLight.setClickable(true);
            mSbHintLight.setEnabled(true);
            mSbHintLight.setSelected(true);
            mTvAutoHint.setVisibility(View.GONE);
            if (mSbHintLight != null) {
                mSbHintLight.setProgress((int) (mBrightnessHelper.getBrightness() * 100));
            }
        }
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
