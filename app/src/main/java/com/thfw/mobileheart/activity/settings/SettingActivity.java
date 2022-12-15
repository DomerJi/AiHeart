package com.thfw.mobileheart.activity.settings;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.luck.picture.lib.tools.PictureFileUtils;
import com.thfw.base.base.IPresenter;
import com.thfw.base.face.SimpleUpgradeStateListener;
import com.thfw.base.models.CommonModel;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.LoginPresenter;
import com.thfw.base.utils.BuglyUtil;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.base.utils.HandlerUtil;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.SharePreferenceUtil;
import com.thfw.base.utils.ToastUtil;
import com.thfw.base.utils.Util;
import com.thfw.mobileheart.MyApplication;
import com.thfw.mobileheart.R;
import com.thfw.mobileheart.activity.BaseActivity;
import com.thfw.mobileheart.activity.me.AboutMeActivity;
import com.thfw.mobileheart.activity.service.AutoUpdateService;
import com.thfw.mobileheart.constants.AnimFileName;
import com.thfw.mobileheart.util.AnimFrequencyUtil;
import com.thfw.mobileheart.util.DialogFactory;
import com.thfw.mobileheart.util.FileSizeUtil;
import com.thfw.ui.common.LhXkSettingActivity;
import com.thfw.ui.dialog.LoadingDialog;
import com.thfw.ui.dialog.LoadingMobileDialog;
import com.thfw.ui.dialog.TDialog;
import com.thfw.ui.dialog.base.BindViewHolder;
import com.thfw.ui.widget.DeviceUtil;
import com.thfw.user.login.LoginStatus;
import com.thfw.user.login.UserManager;
import com.trello.rxlifecycle2.LifecycleProvider;

public class SettingActivity extends BaseActivity {

    private android.widget.LinearLayout mLlAccountSafe;
    private android.widget.LinearLayout mLlInfo;
    private android.widget.LinearLayout mLlClearCache;
    private android.widget.LinearLayout mLlPrivacyPolicy;
    private android.widget.LinearLayout mLlAbout;
    private android.widget.Button mBtLogout;
    private LinearLayout mLlVersion;
    private android.widget.TextView mTvVersion;
    private TextView mTvCache;
    private long fileSize;
    private FileSizeUtil.AppCache appCache;
    private Handler mHandler;
    private long deleteNum;
    private TextView mTvMsgVersion;
    private LinearLayout mLlTransition;
    private TextView mTvTransition;
    private PopupWindow mPopWindow;
    private TextView mTvDeleteAccount;
    private LinearLayout mLlVoiceFocus;
    private View mVVoiceFocus;

    @Override
    public int getContentView() {
        return R.layout.activity_setting;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initView() {
        mLlAccountSafe = (LinearLayout) findViewById(R.id.ll_account_safe);
        mLlInfo = (LinearLayout) findViewById(R.id.ll_info);
        mLlClearCache = (LinearLayout) findViewById(R.id.ll_clear_cache);
        mLlPrivacyPolicy = (LinearLayout) findViewById(R.id.ll_privacy_policy);
        mLlAbout = (LinearLayout) findViewById(R.id.ll_about);
        mBtLogout = (Button) findViewById(R.id.bt_logout);
        mTvDeleteAccount = (TextView) findViewById(R.id.bt_delete_account);
        mLlInfo.setOnClickListener(v -> {
            startActivity(new Intent(mContext, InfoActivity.class));
        });
        mLlAccountSafe.setOnClickListener(v -> {
            startActivity(new Intent(mContext, AccountSafeActivity.class));
        });
        mLlPrivacyPolicy.setOnClickListener(v -> {
            startActivity(new Intent(mContext, PrivacyPolicyActivity.class));
        });
        mLlAbout.setOnClickListener(v -> {
            startActivity(new Intent(mContext, AboutMeActivity.class));
        });
        mBtLogout.setOnClickListener(v -> {
            UserManager.getInstance().logout(LoginStatus.LOGOUT_EXIT);
            finish();
        });
        appCache = new FileSizeUtil.AppCache();

        mTvCache = (TextView) findViewById(R.id.tv_cache);
        fileSize = appCache.getAppCacheLong();
        mTvCache.setText(appCache.formatFileSize(fileSize));
        mLlClearCache.setOnClickListener(v -> {
            clearCache();
        });

        mLlVersion = (LinearLayout) findViewById(R.id.ll_version);
        mTvVersion = (TextView) findViewById(R.id.tv_version);
        mTvMsgVersion = (TextView) findViewById(R.id.tv_massage_version);
        mTvVersion.setText(Util.getAppVersion(mContext));
        mLlVersion.setOnClickListener(v -> {
            checkVersion(false);
        });

        mLlTransition = (LinearLayout) findViewById(R.id.ll_transition);
        mTvTransition = (TextView) findViewById(R.id.tv_transition);
        mLlTransition.setOnClickListener(v -> {
            showTransitionSelect();
        });
        mTvTransition.setText(AnimFrequencyUtil.getAnimFrequencyStr());

        mTvDeleteAccount.setOnClickListener(v -> {
            DialogFactory.createCustomDialog(SettingActivity.this, new DialogFactory.OnViewCallBack() {
                int countS = 5;
                Runnable runnable;

                @Override
                public void callBack(TextView mTvTitle, TextView mTvHint, TextView mTvLeft, TextView mTvRight, View mVLineVertical) {
                    mTvHint.setText(R.string.deleteAccount);
                    mTvTitle.setText("注销账号？");
                    mTvRight.setEnabled(false);
                    mTvRight.setAlpha(0.5f);

                    mTvRight.setText("注销(" + countS + ")");
                    runnable = () -> {
                        countS--;
                        if (countS <= 0) {
                            mTvRight.setText("注销");
                            mTvRight.setEnabled(true);
                            mTvRight.setAlpha(1f);
                        } else {
                            mTvRight.setText("注销(" + countS + ")");
                            HandlerUtil.getMainHandler().postDelayed(runnable, 1000);
                        }
                    };

                    HandlerUtil.getMainHandler().postDelayed(runnable, 1000);


                }

                @Override
                public void onViewClick(BindViewHolder viewHolder, View view, TDialog tDialog) {
                    if (view.getId() == R.id.tv_left) {
                        if (runnable != null) {
                            HandlerUtil.getMainHandler().removeCallbacks(runnable);
                        }
                        tDialog.dismiss();
                    } else {
                        tDialog.dismiss();
                        deleteAccount();
                    }
                }
            }, false);
        });
        mLlVoiceFocus = (LinearLayout) findViewById(R.id.ll_voice_focus);
        mVVoiceFocus = (View) findViewById(R.id.v_voice_focus);
        if (DeviceUtil.isLhXk_CM_GB03D()) {
            mLlVoiceFocus.setVisibility(View.VISIBLE);
            mVVoiceFocus.setVisibility(View.VISIBLE);
            mLlVoiceFocus.setOnClickListener(v -> LhXkSettingActivity.startActivity(mContext));
        }
    }

    private void deleteAccount() {

        LoadingMobileDialog.show(SettingActivity.this, "正在注销");
        HandlerUtil.getMainHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new LoginPresenter(new LoginPresenter.LoginUi<CommonModel>() {
                    @Override
                    public LifecycleProvider getLifecycleProvider() {
                        return SettingActivity.this;
                    }

                    @Override
                    public void onSuccess(CommonModel data) {
                        LoadingMobileDialog.hide();
                        SharePreferenceUtil.setLong(UserManager.getInstance().getUser().getMobile(), System.currentTimeMillis());
                        ToastUtil.showLong("账号注销成功");
                        mBtLogout.performClick();
                    }

                    @Override
                    public void onFail(ResponeThrowable throwable) {
                        LoadingMobileDialog.hide();
                        // todo 假成功
                        SharePreferenceUtil.setLong(UserManager.getInstance().getUser().getMobile(), System.currentTimeMillis());
                        ToastUtil.showLong("账号注销成功");
                        mBtLogout.performClick();
                    }
                }).onDeleteAccount();

            }
        }, 2500);

    }


    @Override
    protected void onResume() {
        super.onResume();
        checkVersion(true);
    }

    /**
     * 检查版本更新
     */
    private void checkVersion(boolean hint) {
        if (!hint) {
            LoadingDialog.show(SettingActivity.this, "正在检查");
        }
        BuglyUtil.requestNewVersion(new SimpleUpgradeStateListener() {
            @Override
            public void onVersion(boolean hasNewVersion) {
                super.onVersion(hasNewVersion);
                LoadingDialog.hide();
                Log.d("requestNewVersion", "hasNewVersion = " + hasNewVersion);
                if (!isMeResumed() && EmptyUtil.isEmpty(SettingActivity.this)) {
                    return;
                }
                if (hint) {
                    mTvMsgVersion.setText("新");
                    mTvMsgVersion.setVisibility(hasNewVersion ? View.VISIBLE : View.GONE);
                    if (hasNewVersion) {
                        AutoUpdateService.startUpdate(mContext);
                    }
                } else {
                    if (hasNewVersion) {
                        startActivity(new Intent(mContext, SystemAppActivity.class));
                    } else {
                        ToastUtil.show("已经是最新版本");
                    }
                }
            }
        });
    }

    private void clearCache() {
        if (fileSize <= 0) {
            return;
        }
        deleteNum = fileSize / 10;
        if (deleteNum <= 0) {
            deleteNum = 1;
        }
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (mHandler == null || fileSize < 0) {
                    return;
                }
                fileSize = fileSize - deleteNum;

                if (fileSize > 0) {
                    sendEmptyMessageDelayed(0, 90);
                } else {
                    fileSize = 0;
                }
                mTvCache.setText(appCache.formatFileSize(fileSize));
            }
        };
        mHandler.sendEmptyMessageDelayed(0, 0);
        appCache.clearAppCache(new FileSizeUtil.AppCache.clearCacheListener() {
            @Override
            public void onSuccess(String fileSize) {
                LogUtil.d(TAG, "clearAppCache onSuccess ");
                PictureFileUtils.deleteAllCacheDirFile(mContext);
            }

            @Override
            public void onFail() {
                LogUtil.d(TAG, "clearAppCache onFail ");
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                Glide.get(MyApplication.getApp()).clearDiskCache();
            }
        }).start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            fileSize = -1;
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
    }

    @Override
    public void initData() {

    }

    /**
     * 选择出场动画频率
     */
    private void showTransitionSelect() {
        if (mPopWindow != null) {
            mPopWindow.dismiss();
            mPopWindow = null;
            return;
        }
        View contentView = LayoutInflater.from(SettingActivity.this).inflate(R.layout.popwindow_anim_transition_selected, null);
        mPopWindow = new PopupWindow(contentView);
        mPopWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 点击外部取消显示
        mPopWindow.setOutsideTouchable(true);
        //  mPopWindow.setBackgroundDrawable(new BitmapDrawable()); // 括号内过时
        mPopWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView mTvEvery = contentView.findViewById(R.id.tv_every);
        TextView mTvDay = contentView.findViewById(R.id.tv_day);
        TextView mTvWeek = contentView.findViewById(R.id.tv_week);
        switch (AnimFrequencyUtil.getAnimFrequency()) {
            case AnimFileName.Frequency.EVERY_TIME:
                mTvEvery.setTextColor(getResources().getColor(R.color.text_green));
                break;
            case AnimFileName.Frequency.DAY_TIME:
                mTvDay.setTextColor(getResources().getColor(R.color.text_green));
                break;
            case AnimFileName.Frequency.WEEK_TIME:
                mTvWeek.setTextColor(getResources().getColor(R.color.text_green));
                break;
        }
        // 每次
        mTvEvery.setOnClickListener(v -> {
            mPopWindow.dismiss();
            mPopWindow = null;
            AnimFrequencyUtil.setAnimFrequency(0);
            mTvTransition.setText(AnimFrequencyUtil.getAnimFrequencyStr());
        });
        // 每天
        mTvDay.setOnClickListener(v -> {
            mPopWindow.dismiss();
            mPopWindow = null;
            AnimFrequencyUtil.setAnimFrequency(1);
            mTvTransition.setText(AnimFrequencyUtil.getAnimFrequencyStr());
        });
        // 每星期
        mTvWeek.setOnClickListener(v -> {
            mPopWindow.dismiss();
            mPopWindow = null;
            AnimFrequencyUtil.setAnimFrequency(2);
            mTvTransition.setText(AnimFrequencyUtil.getAnimFrequencyStr());
        });

        mPopWindow.showAsDropDown(mTvTransition, 0, 26, Gravity.LEFT);
    }


}