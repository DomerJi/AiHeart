package com.thfw.robotheart.util;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.bigkoo.pickerview.view.TimePickerView;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.opensource.svgaplayer.SVGACallback;
import com.opensource.svgaplayer.SVGADrawable;
import com.opensource.svgaplayer.SVGAImageView;
import com.opensource.svgaplayer.SVGAParser;
import com.opensource.svgaplayer.SVGAVideoEntity;
import com.thfw.base.models.AreaModel;
import com.thfw.base.models.PickerData;
import com.thfw.base.models.UrgedMsgModel;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.base.utils.HandlerUtil;
import com.thfw.base.utils.HourUtil;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.SharePreferenceUtil;
import com.thfw.base.utils.Util;
import com.thfw.robotheart.MyApplication;
import com.thfw.robotheart.activitys.me.PrivateSetActivity;
import com.thfw.robotheart.adapter.BaseAdapter;
import com.thfw.robotheart.adapter.DialogLikeAdapter;
import com.thfw.robotheart.constants.AnimFileName;
import com.thfw.robotheart.robot.RobotUtil;
import com.thfw.ui.R;
import com.thfw.ui.dialog.TDialog;
import com.thfw.ui.dialog.base.BindViewHolder;
import com.thfw.ui.dialog.listener.OnBindViewListener;
import com.thfw.ui.dialog.listener.OnViewClickListener;
import com.thfw.ui.utils.DragViewUtil;
import com.thfw.ui.voice.tts.TtsHelper;
import com.thfw.ui.voice.tts.TtsModel;
import com.thfw.ui.widget.InputBoxView;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.List;

import static android.content.Context.VIBRATOR_SERVICE;
import static com.thfw.ui.utils.UrgeUtil.URGE_DELAY_TIME;

/**
 * 弹框工厂
 */
public class DialogRobotFactory {

    private static final String TAG = DialogRobotFactory.class.getSimpleName();
    private static final float WIDTH_ASPECT_2 = 0.6f;
    private static TDialog mSvgaTDialog;
    private static TextView mTvTime;
    private static int minute;
    private static Runnable mMinuteRunnable;
    private static long currentTimeMillis;


    private static TDialog urgedDialog;
    private static Runnable urgedDialogRunnable;

    public static TDialog getUrgedDialog() {
        return urgedDialog;
    }

    public static TDialog getSvgaTDialog() {
        return mSvgaTDialog;
    }

    public static TDialog createCustomDialog(FragmentActivity activity, OnViewCallBack onViewCallBack) {
        return createCustomDialog(activity, onViewCallBack, true);
    }

    /**
     * 通用弹框
     *
     * @param activity
     * @param onViewCallBack
     * @return
     */
    public static TDialog createCustomDialog(FragmentActivity activity, OnViewCallBack onViewCallBack, boolean cancleOutside) {
        return new TDialog.Builder(activity.getSupportFragmentManager())
                .setLayoutRes(R.layout.dialog_custom_layout)
                .setDialogAnimationRes(R.style.animate_dialog_fade)
                .addOnClickListener(R.id.tv_left, R.id.tv_right)
                .setScreenWidthAspect(activity, 0.4f)
                .setCancelableOutside(cancleOutside)
                // R.id.tv_title, R.id.tv_hint, R.id.tv_left, R.id.tv_right
                .setOnBindViewListener(viewHolder -> {
                    TextView mTvTitle = viewHolder.getView(R.id.tv_title);
                    TextView mTvHint = viewHolder.getView(R.id.tv_hint);
                    TextView mTvLeft = viewHolder.getView(R.id.tv_left);
                    TextView mTvRight = viewHolder.getView(R.id.tv_right);
                    View mVLineVertical = viewHolder.getView(R.id.vline_vertical);
                    onViewCallBack.callBack(mTvTitle, mTvHint, mTvLeft, mTvRight, mVLineVertical);
                })
                .setOnViewClickListener(onViewCallBack).create().show();
    }

    /**
     * 服务不可用弹框
     *
     * @param activity
     * @param onViewCallBack
     * @return
     */
    public static TDialog createServerStopDialog(FragmentActivity activity, OnViewCallBack onViewCallBack) {
        return new TDialog.Builder(activity.getSupportFragmentManager())
                .setLayoutRes(R.layout.dialog_custom_layout)
                .setDialogAnimationRes(R.style.animate_dialog_fade)
                .addOnClickListener(R.id.tv_left, R.id.tv_right)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        onViewCallBack.onViewClick(null, null, null);
                    }
                })
                .setScreenWidthAspect(activity, 0.4f)
                .setCancelableOutside(false)
                // R.id.tv_title, R.id.tv_hint, R.id.tv_left, R.id.tv_right
                .setOnBindViewListener(viewHolder -> {
                    TextView mTvTitle = viewHolder.getView(R.id.tv_title);
                    mTvTitle.setVisibility(View.GONE);
                    TextView mTvHint = viewHolder.getView(R.id.tv_hint);
                    TextView mTvLeft = viewHolder.getView(R.id.tv_left);
                    TextView mTvRight = viewHolder.getView(R.id.tv_right);
                    View mVLineVertical = viewHolder.getView(R.id.vline_vertical);
                    mVLineVertical.setVisibility(View.GONE);
                    mTvLeft.setVisibility(View.GONE);
                    onViewCallBack.callBack(mTvTitle, mTvHint, mTvLeft, mTvRight, mVLineVertical);
                })
                .setOnViewClickListener(onViewCallBack).create().show();
    }

    public static void createSimple(FragmentActivity activity, String msg) {
        createSimple(activity, "温馨提示", msg);
    }

    /**
     * 简单提示
     *
     * @param activity
     */
    public static void createSimple(FragmentActivity activity, String title, String msg) {
        DialogRobotFactory.createCustomDialog(activity, new DialogRobotFactory.OnViewCallBack() {
            @Override
            public void callBack(TextView mTvTitle, TextView mTvHint, TextView mTvLeft, TextView mTvRight, View mVLineVertical) {
                mTvHint.setText(msg);
                mTvTitle.setText(title);
                mTvLeft.setVisibility(View.GONE);
                mTvRight.setBackgroundResource(com.thfw.robotheart.R.drawable.dialog_button_selector);
                mVLineVertical.setVisibility(View.GONE);
            }

            @Override
            public void onViewClick(BindViewHolder viewHolder, View view, TDialog tDialog) {
                tDialog.dismiss();
            }
        }).setCancelable(false);
    }

    /**
     * 简单提示【dismiss回调】
     *
     * @param activity
     */
    public static void createSimple(FragmentActivity activity, String title, String msg,
                                    DialogInterface.OnDismissListener dismissListener) {
        new TDialog.Builder(activity.getSupportFragmentManager())
                .setLayoutRes(R.layout.dialog_custom_layout)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if (dismissListener != null) {
                            dismissListener.onDismiss(dialog);
                        }
                    }
                })
                .setDialogAnimationRes(R.style.animate_dialog_fade)
                .addOnClickListener(R.id.tv_left, R.id.tv_right)
                .setScreenWidthAspect(activity, 0.4f)
                .setCancelableOutside(false)
                // R.id.tv_title, R.id.tv_hint, R.id.tv_left, R.id.tv_right
                .setOnBindViewListener(viewHolder -> {
                    TextView mTvTitle = viewHolder.getView(R.id.tv_title);
                    TextView mTvHint = viewHolder.getView(R.id.tv_hint);
                    TextView mTvLeft = viewHolder.getView(R.id.tv_left);
                    TextView mTvRight = viewHolder.getView(R.id.tv_right);
                    View mVLineVertical = viewHolder.getView(R.id.vline_vertical);
                    mTvHint.setText(msg);
                    mTvTitle.setText(title);
                    mTvLeft.setVisibility(View.GONE);
                    mTvRight.setBackgroundResource(com.thfw.robotheart.R.drawable.dialog_button_selector);
                    mVLineVertical.setVisibility(View.GONE);
                }).setOnViewClickListener(new OnViewClickListener() {
            @Override
            public void onViewClick(BindViewHolder viewHolder, View view, TDialog tDialog) {
                tDialog.dismiss();
                if (dismissListener != null) {
                    dismissListener.onDismiss(tDialog.getDialog());
                }
            }
        }).create().show();
    }


    public static TDialog createUrgedDialog(FragmentActivity activity, UrgedMsgModel model, OnUrgedBack onUrgedBack) {
        final View[] view = new View[1];
        if (urgedDialog != null) {
            urgedDialog.dismiss();
        }
        urgedDialog = new TDialog.Builder(activity.getSupportFragmentManager())
                .setLayoutRes(com.thfw.robotheart.R.layout.dialog_urged_top_layout)
                .setDialogAnimationRes(R.style.animate_dialog_top)
                .setScreenWidthAspect(activity, WIDTH_ASPECT_2)
                .setGravity(Gravity.TOP)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        urgedDialog = null;
                        if (urgedDialogRunnable != null) {
                            HandlerUtil.getMainHandler().removeCallbacks(urgedDialogRunnable);
                        }
                        urgedDialogRunnable = null;
                    }
                })
                .setNotFocusable(true)
                .addOnClickListener(com.thfw.robotheart.R.id.cv_bg)
                .setOnBindViewListener(viewHolder -> {
                    view[0] = viewHolder.getView(com.thfw.robotheart.R.id.cv_bg);
                    TextView textView = viewHolder.getView(R.id.tv_hint);
                    textView.setText(model.getContent());
                    Vibrator vibrator = (Vibrator) activity.getApplicationContext().getSystemService(VIBRATOR_SERVICE);
                    if (vibrator != null) {
                        vibrator.vibrate(200);
                    }
                }).create().show();
        HandlerUtil.getMainHandler().postDelayed(() -> {
            if (view[0] != null) {
                DragViewUtil.registerDragAction(view[0], urgedDialog, v -> {
                    onUrgedBack.onClick(view[0], model);
                    Vibrator vibrator = (Vibrator) activity.getApplicationContext().getSystemService(VIBRATOR_SERVICE);
                    if (vibrator != null) {
                        vibrator.vibrate(120);
                    }
                    HandlerUtil.getMainHandler().postDelayed(() -> {
                        if (urgedDialog != null) {
                            urgedDialog.dismiss();
                        }
                    }, 500);
                });
            }
        }, 200);
        if (urgedDialogRunnable != null) {
            HandlerUtil.getMainHandler().removeCallbacks(urgedDialogRunnable);
        }
        urgedDialogRunnable = () -> {
            if (urgedDialog != null) {
                urgedDialog.dismiss();
            }
        };
        HandlerUtil.getMainHandler().postDelayed(urgedDialogRunnable, URGE_DELAY_TIME);
        return urgedDialog;
    }


    /**
     * svga dialog
     *
     * @param activity
     * @param onViewCallBack
     * @return
     */
    public static void createFullSvgaDialog(FragmentActivity activity, String svgaAssets, final OnSVGACallBack onViewCallBack) {
        String hint = AnimFileName.getHint(svgaAssets);
        dismissSVGA();
        mSvgaTDialog = new TDialog.Builder(activity.getSupportFragmentManager())
                .setLayoutRes(com.thfw.robotheart.R.layout.dialog_full_svga_layout)
                .setDialogAnimationRes(R.style.animate_dialog_fade)
                .setScreenWidthAspect(activity, 1.0f)
                .setScreenHeightAspect(activity, 1.0f)
                .setDimAmount(1f)
                // R.id.tv_title, R.id.tv_hint, R.id.tv_left, R.id.tv_right
                .setOnBindViewListener(viewHolder -> {
                    SVGAParser parser = new SVGAParser(activity);
                    SVGAImageView svgaImageView = viewHolder.getView(com.thfw.robotheart.R.id.svga_dialog);
                    if (!TextUtils.isEmpty(hint)) {
                        TextView mTvHint = viewHolder.getView(com.thfw.robotheart.R.id.tv_hint);
                        // 部分设备底部导航遮挡文字问题
                        if (RobotUtil.isInstallRobot() || Build.DEVICE.contains("3399")) {
                            mTvHint.setPadding(0, 0, 0, Util.dipToPx(48, mTvHint.getContext()));
                        }
                        mTvHint.setText(hint);
                    }
                    // The third parameter is a default parameter, which is null by default. If this method is set, the audio parsing and playback will not be processed internally. The audio File instance will be sent back to the developer through PlayCallback, and the developer will control the audio playback and playback. stop
                    parser.decodeFromAssets(svgaAssets, new SVGAParser.ParseCompletion() {
                        @Override
                        public void onComplete(@NotNull SVGAVideoEntity svgaVideoEntity) {
                            SVGADrawable drawable = new SVGADrawable(svgaVideoEntity);
                            svgaImageView.setImageDrawable(drawable);
                            svgaImageView.startAnimation();
                        }

                        @Override
                        public void onError() {
                        }
                    }, null);

                    svgaImageView.setCallback(new SimpleSVGACallBack() {

                        @Override
                        public void onFinished() {
                            LogUtil.d(TAG, "onFinished");
                            if (svgaImageView.getCallback() == null) {
                                return;
                            }
                            svgaImageView.setCallback(null);
                            dismissSVGA();
                            onViewCallBack.callBack(svgaImageView);
                            svgaImageView.clear();
                        }
                    });
                })
                .setOnViewClickListener(onViewCallBack).create().show();
    }

    private static void dismissSVGA() {
        if (mSvgaTDialog != null) {
            try {
                mSvgaTDialog.dismiss();
            } catch (Exception e) {
                LogUtil.i(TAG, "mSvgaTDialog.dismiss() e = " + e.getMessage());
            } finally {
                mSvgaTDialog = null;
            }
        }
    }

    /**
     * svga dialog
     *
     * @param activity
     * @param onViewCallBack
     * @return
     */
    public static void createSvgaDialog(FragmentActivity activity, String svgaAssets, final OnSVGACallBack onViewCallBack) {
        long time = System.currentTimeMillis();
        if (time - currentTimeMillis < 800) {
            return;
        }
        currentTimeMillis = time;
        // 过场动画出现频率
        if (svgaAssets.startsWith("transition_") && PrivateSetActivity.getAnimFrequency() != AnimFileName.Frequency.EVERY_TIME) {
            int animFrequency = PrivateSetActivity.getAnimFrequency();
            long animTime = SharePreferenceUtil.getLong("Frequency_" + svgaAssets, 0);

            if (animFrequency == AnimFileName.Frequency.WEEK_TIME) {
                if (currentTimeMillis - animTime < HourUtil.LEN_DAY * 7) {
                    onViewCallBack.callBack(null);
                    return;
                }
            } else {
                if (currentTimeMillis - animTime < HourUtil.LEN_DAY) {
                    onViewCallBack.callBack(null);
                    return;
                }
            }
        }
        SharePreferenceUtil.setLong("Frequency_" + svgaAssets, currentTimeMillis);
        String hint = AnimFileName.getHint(svgaAssets);
        // 语音播放
        TtsHelper.getInstance().start(new TtsModel(hint), null);
        dismissSVGA();
        mSvgaTDialog = new TDialog.Builder(activity.getSupportFragmentManager())
                .setLayoutRes(com.thfw.robotheart.R.layout.dialog_svga_layout)
                .setDialogAnimationRes(R.style.animate_dialog_fade)
                .setScreenWidthAspect(activity, 1.0f)
                .setScreenHeightAspect(activity, 1.0f)
                .setDimAmount(0.6f)
                // R.id.tv_title, R.id.tv_hint, R.id.tv_left, R.id.tv_right
                .setOnBindViewListener(viewHolder -> {
                    SVGAParser parser = new SVGAParser(activity);
                    SVGAImageView svgaImageView = viewHolder.getView(com.thfw.robotheart.R.id.svga_dialog);
                    if (!TextUtils.isEmpty(hint)) {
                        mTvTime = viewHolder.getView(com.thfw.robotheart.R.id.tv_time);
                        if (mTvTime != null) {
                            mTvTime.setOnClickListener(v -> {
                                LogUtil.d(TAG, "onFinished click tvtime");
                                if (svgaImageView != null && svgaImageView.getCallback() != null) {
                                    svgaImageView.getCallback().onFinished();
                                }
                            });
                        }
                        mMinuteRunnable = new Runnable() {
                            @Override
                            public void run() {
                                if (mTvTime != null) {
                                    mTvTime.setText(minute + "S");
                                    minute--;
                                    if (minute > 0) {
                                        mTvTime.postDelayed(this, 1000);
                                    }
                                }
                            }
                        };
                        TextView mTvJump = viewHolder.getView(com.thfw.robotheart.R.id.tv_jump);
                        TextView mTvHint = viewHolder.getView(com.thfw.robotheart.R.id.tv_hint);
                        mTvJump.setOnClickListener(v -> {
                            LogUtil.d(TAG, "onFinished click");
                            svgaImageView.getCallback().onFinished();
                        });
                        mTvHint.setText(hint);
                    } else {
                        viewHolder.getView(com.thfw.robotheart.R.id.tv_time).setVisibility(View.INVISIBLE);
                        viewHolder.getView(com.thfw.robotheart.R.id.tv_jump).setVisibility(View.INVISIBLE);
                        viewHolder.getView(com.thfw.robotheart.R.id.tv_hint).setVisibility(View.INVISIBLE);
                    }
                    LogUtil.d(TAG, "Time start = " + time);
                    // The third parameter is a default parameter, which is null by default. If this method is set, the audio parsing and playback will not be processed internally. The audio File instance will be sent back to the developer through PlayCallback, and the developer will control the audio playback and playback. stop
                    parser.decodeFromAssets(svgaAssets, new SVGAParser.ParseCompletion() {
                        @Override
                        public void onComplete(@NotNull SVGAVideoEntity svgaVideoEntity) {
                            SVGADrawable drawable = new SVGADrawable(svgaVideoEntity);

                            if (mTvTime != null) {
                                int fps = svgaVideoEntity.getFPS();
                                int frames = svgaVideoEntity.getFrames();
                                LogUtil.d(TAG, "onComplete = frames" + frames + ", fps = " + fps);
                                minute = (int) (frames * 1.0f / fps + 0.3);
                                mTvTime.postDelayed(mMinuteRunnable, 0);
                            }

                            svgaImageView.setImageDrawable(drawable);
                            svgaImageView.startAnimation();
                            LogUtil.d(TAG, "Time end limt = " + (System.currentTimeMillis() - time));
                        }

                        @Override
                        public void onError() {
                        }
                    }, null);

                    svgaImageView.setCallback(new SimpleSVGACallBack() {

                        @Override
                        public void onFinished() {
                            LogUtil.d(TAG, "onFinished");
                            if (svgaImageView.getCallback() == null) {
                                return;
                            }
                            svgaImageView.setCallback(null);
                            dismissSVGA();
                            onViewCallBack.callBack(svgaImageView);
                            svgaImageView.clear();
                            if (mTvTime != null && mMinuteRunnable != null) {
                                mTvTime.removeCallbacks(mMinuteRunnable);
                                mTvTime = null;
                                mMinuteRunnable = null;
                            }
                        }
                    });
                })
                .setOnViewClickListener(onViewCallBack).create().show();
    }

    public static void createAddressBirthDay(Context mContext, ViewGroup
            decorView, OnTimeSelectListener optionsSelectListener) {

        Calendar selectedDate = Calendar.getInstance();//系统当前时间
        Calendar startDate = Calendar.getInstance();
        startDate.set(selectedDate.get(Calendar.YEAR) - 120, 0, 1);
        Calendar endDate = Calendar.getInstance();
        endDate.set(selectedDate.get(Calendar.YEAR) - 5, selectedDate.get(Calendar.MONTH), selectedDate.get(Calendar.DAY_OF_MONTH));
        TimePickerBuilder builder = new TimePickerBuilder(mContext, optionsSelectListener).setDecorView(decorView)
                .setTitleText("选择年月日")//标题文字
                .setTitleSize(17)//标题文字大小
                .setTitleColor(mContext.getResources().getColor(R.color.black))//标题文字颜色
                .setCancelText("取消")//取消按钮文字
                .setCancelColor(mContext.getResources().getColor(R.color.text_content))//取消按钮文字颜色
                .setSubmitText("确定")//确认按钮文字
                .setSubmitColor(mContext.getResources().getColor(R.color.black))//确定按钮文字颜色
                .setContentTextSize(17)//滚轮文字大小
                .setTextColorOut(mContext.getResources().getColor(R.color.text_content))
                .setTextColorCenter(mContext.getResources().getColor(R.color.text_common))//设置选中文本的颜色值
                .setSubCalSize(14)
                .setDate(endDate)
                .setRangDate(startDate, endDate)
                .setBgColor(mContext.getResources().getColor(R.color.colorRobotDialogBg))
                .setLineSpacingMultiplier(2.2f)//行间距
                .setDividerColor(mContext.getResources().getColor(R.color.black_10));//设置分割线的颜色
        TimePickerView timePickerView = builder.build();
        setOptionPickerView(timePickerView, mContext);
        timePickerView.show();
    }

    /**
     * 单选/多选+自定义
     *
     * @param activity
     * @param onViewCallBack
     * @return
     */
    public static TDialog createSelectCustomText(FragmentActivity activity, String
            title, List<PickerData> likeModels, OnViewSelectCallBack onViewCallBack) {
        return new TDialog.Builder(activity.getSupportFragmentManager())
                .setLayoutRes(com.thfw.robotheart.R.layout.dialog_select_custom_layout)
                .setGravity(Gravity.BOTTOM)
                .setDialogAnimationRes(R.style.animate_dialog)
                .addOnClickListener(R.id.btnCancel, R.id.btnSubmit)
                .setScreenWidthAspect(activity, 1f)
                // R.id.tv_title, R.id.tv_hint, R.id.tv_left, R.id.tv_right
                .setOnBindViewListener(viewHolder -> {
                    RecyclerView mRvSelect = viewHolder.getView(com.thfw.robotheart.R.id.rv_select);
                    TextView mTvTitle = viewHolder.getView(com.thfw.robotheart.R.id.tvTitle);
                    mTvTitle.setText(title);

                    // 设置布局管理器
                    FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(activity);
                    // flexDirection 属性决定主轴的方向（即项目的排列方向）。类似 LinearLayout 的 vertical 和 horizontal。
                    flexboxLayoutManager.setFlexDirection(FlexDirection.ROW);
                    // 主轴为水平方向，起点在左端。
                    // flexWrap 默认情况下 Flex 跟 LinearLayout 一样，都是不带换行排列的，但是flexWrap属性可以支持换行排列。
                    flexboxLayoutManager.setFlexWrap(FlexWrap.WRAP);
                    // 按正常方向换行
                    // justifyContent 属性定义了项目在主轴上的对齐方式。
                    flexboxLayoutManager.setJustifyContent(JustifyContent.FLEX_START);// 交叉轴的起点对齐。
                    mRvSelect.setLayoutManager(flexboxLayoutManager);
                    DialogLikeAdapter dialogLikeAdapter = new DialogLikeAdapter(likeModels);
                    mRvSelect.setAdapter(dialogLikeAdapter);
                    onViewCallBack.callBack(dialogLikeAdapter);

                })
                .setOnViewClickListener(onViewCallBack).create().show();
    }


    /**
     * 创建地区选择弹框
     */
    public static void createAddressDialog(Context mContext, ViewGroup
            decorView, OnOptionsSelectListener optionsSelectListener, int... opsitions) {
        OptionsPickerBuilder optionsPickerBuilder = new OptionsPickerBuilder(mContext, optionsSelectListener).setDecorView(decorView)//必须是RelativeLayout，不设置setDecorView的话，底部虚拟导航栏会显示在弹出的选择器区域
                .setTitleText("选择地区")//标题文字
                .setTitleSize(17)//标题文字大小
                .setTitleColor(mContext.getResources().getColor(R.color.black))//标题文字颜色
                .setCancelText("取消")//取消按钮文字
                .setCancelColor(mContext.getResources().getColor(R.color.text_content))//取消按钮文字颜色
                .setSubmitText("确定")//确认按钮文字
                .setSubmitColor(mContext.getResources().getColor(R.color.black))//确定按钮文字颜色
                .setContentTextSize(17)//滚轮文字大小
                .setTextColorOut(mContext.getResources().getColor(R.color.text_content))
                .setTextColorCenter(mContext.getResources().getColor(R.color.text_common))//设置选中文本的颜色值
                .setSubCalSize(14)
                .setBgColor(mContext.getResources().getColor(R.color.colorRobotDialogBg))
                .setLineSpacingMultiplier(2.2f)//行间距
                .setDividerColor(mContext.getResources().getColor(R.color.black_10));//设置分割线的颜色

        if (!EmptyUtil.isEmpty(opsitions) && opsitions.length == 3) {
            optionsPickerBuilder.setSelectOptions(opsitions[0], opsitions[1], opsitions[2]);
        }

        OptionsPickerView<AreaModel> optionsPickerView = optionsPickerBuilder.build();
        setOptionPickerView(optionsPickerView, mContext);
        optionsPickerView.setPicker(AreaUtil.getOp1(), AreaUtil.getOp2());
        optionsPickerView.show();
    }

    /**
     * 创建通用单选选择框
     */
    public static void createSelectDialog(Context mContext, ViewGroup decorView,
                                          OnOptionsSelectListener optionsSelectListener,
                                          String title, List<PickerData> list) {

        OptionsPickerBuilder optionsPickerBuilder = new OptionsPickerBuilder(mContext, optionsSelectListener).setDecorView(decorView)//必须是RelativeLayout，不设置setDecorView的话，底部虚拟导航栏会显示在弹出的选择器区域
                .setTitleText(title) // 标题文字
                .setTitleSize(17)//标题文字大小
                .setTitleColor(mContext.getResources().getColor(R.color.black))//标题文字颜色
                .setCancelText("取消")//取消按钮文字
                .setCancelColor(mContext.getResources().getColor(R.color.text_content))//取消按钮文字颜色
                .setSubmitText("确定")//确认按钮文字
                .setSubmitColor(mContext.getResources().getColor(R.color.black))//确定按钮文字颜色
                .setContentTextSize(17)//滚轮文字大小
                .setTextColorOut(mContext.getResources().getColor(R.color.text_content))
                .setTextColorCenter(mContext.getResources().getColor(R.color.text_common))//设置选中文本的颜色值
                .setSubCalSize(14)
                .setBgColor(mContext.getResources().getColor(R.color.colorRobotDialogBg))
                .setLineSpacingMultiplier(2.2f) // 行间距
                .setDividerColor(mContext.getResources().getColor(R.color.black_10));// 设置分割线的颜色


        OptionsPickerView<PickerData> optionsPickerView = optionsPickerBuilder.build();
        setOptionPickerView(optionsPickerView, mContext);
        optionsPickerView.setPicker(list);
        optionsPickerView.show();
    }

    private static void setOptionPickerView(OptionsPickerView optionsPickerView, Context
            mContext) {
        LinearLayout picker = (LinearLayout) optionsPickerView.findViewById(R.id.optionspicker);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) picker.getLayoutParams();
        layoutParams.height = Util.dipToPx(235 * MyApplication.getFontScale(), mContext);
        picker.setLayoutParams(layoutParams);
        optionsPickerView.findViewById(R.id.rv_topbar).setBackgroundResource(R.drawable.bg_top_radius_theme_dialog);

    }

    private static void setOptionPickerView(TimePickerView optionsPickerView, Context mContext) {
        LinearLayout picker = (LinearLayout) optionsPickerView.findViewById(R.id.timepicker);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) picker.getLayoutParams();
        layoutParams.height = Util.dipToPx(260 * MyApplication.getFontScale(), mContext);
        picker.setLayoutParams(layoutParams);
        optionsPickerView.findViewById(R.id.rv_topbar).setBackgroundResource(R.drawable.bg_top_radius_theme_dialog);

    }


    /**
     * 通用弹框
     *
     * @param activity
     * @param onViewCallBack
     * @return
     */
    public static TDialog createCustomThreeDialog(FragmentActivity
                                                          activity, OnViewThreeCallBack onViewCallBack) {
        return new TDialog.Builder(activity.getSupportFragmentManager())
                .setLayoutRes(R.layout.dialog_custom_three_layout)
                .setDialogAnimationRes(R.style.animate_dialog_fade)
                .addOnClickListener(R.id.tv_one, R.id.tv_two, R.id.tv_three)
                // R.id.tv_title, R.id.tv_hint, R.id.tv_left, R.id.tv_right
                .setOnBindViewListener(viewHolder -> {
                    TextView mTvTitle = viewHolder.getView(R.id.tv_title);
                    TextView mTvHint = viewHolder.getView(R.id.tv_hint);
                    TextView mTvOne = viewHolder.getView(R.id.tv_one);
                    TextView mTvTwo = viewHolder.getView(R.id.tv_two);
                    TextView mTvThree = viewHolder.getView(R.id.tv_three);
                    onViewCallBack.callBack(mTvTitle, mTvHint, mTvOne, mTvTwo, mTvThree);
                })
                .setOnViewClickListener(onViewCallBack).create().show();
    }

    /**
     * 交易密码
     *
     * @param activity
     * @param onInputCompleteListener
     * @return
     */
    public static TDialog createTransactionPasswordDialog(FragmentActivity
                                                                  activity, InputBoxView.OnInputCompleteListener onInputCompleteListener) {
        return new TDialog.Builder(activity.getSupportFragmentManager())
                .setLayoutRes(R.layout.dialog_transaction_password_layout)
                .setGravity(Gravity.BOTTOM)
                .setScreenWidthAspect(activity, 1)
                .addOnClickListener(R.id.iv_close)
                .setOnBindViewListener(new OnBindViewListener() {
                    @Override
                    public void bindView(BindViewHolder viewHolder) {
                        InputBoxView inputBoxView = viewHolder.getView(R.id.ibv_transaction);
                        inputBoxView.setOnInputCompleteListener(onInputCompleteListener);
                    }
                })
                .setOnViewClickListener(new OnViewClickListener() {
                    @Override
                    public void onViewClick(BindViewHolder viewHolder, View view, TDialog tDialog) {
                        tDialog.dismiss();
                    }
                }).create().show();

    }

    /**
     * 手机验证码
     *
     * @param activity
     * @param onInputCompleteListener
     * @return
     */
    public static TDialog createVerificationCodeDialog(FragmentActivity
                                                               activity, InputBoxView.OnInputCompleteListener onInputCompleteListener) {
        return new TDialog.Builder(activity.getSupportFragmentManager())
                .setLayoutRes(R.layout.dialog_verification_code_layout)
                .setGravity(Gravity.BOTTOM)
                .setScreenWidthAspect(activity, 1)
                .addOnClickListener(R.id.iv_close)
                .setOnBindViewListener(new OnBindViewListener() {
                    @Override
                    public void bindView(BindViewHolder viewHolder) {
                        InputBoxView inputBoxView = viewHolder.getView(R.id.ibv_transaction);
                        inputBoxView.setOnInputCompleteListener(onInputCompleteListener);
                    }
                })
                .setOnViewClickListener(new OnViewClickListener() {
                    @Override
                    public void onViewClick(BindViewHolder viewHolder, View view, TDialog tDialog) {
                        tDialog.dismiss();
                    }
                }).create().show();

    }

    /**
     * 商品筛选
     *
     * @param activity
     * @return
     */
    public static TDialog createScreenDialog(FragmentActivity activity) {
        return new TDialog.Builder(activity.getSupportFragmentManager())
                .setLayoutRes(R.layout.dialog_screen_layout)
                .setDialogAnimationRes(R.style.animate_dialog_right)
                .setGravity(Gravity.RIGHT)
                .setScreenHeightAspect(activity, 1f)
                .setScreenWidthAspect(activity, 0.8f)
                .addOnClickListener(R.id.bt_reset, R.id.bt_confirm)
                .setOnBindViewListener(new OnBindViewListener() {
                    @Override
                    public void bindView(BindViewHolder viewHolder) {
                        RecyclerView rvScreen = viewHolder.getView(R.id.rv_screen);
                    }
                })
                .setOnViewClickListener(new OnViewClickListener() {
                    @Override
                    public void onViewClick(BindViewHolder viewHolder, View view, TDialog tDialog) {
                        if (view.getId() == R.id.bt_confirm) {
                            tDialog.dismiss();
                        }
                    }
                }).create().show();

    }

    /**
     * 【问一问】 更多弹框
     */
    public static TDialog createAskMore(FragmentActivity activity, OnViewClickListener
            onViewClickListener) {
        return new TDialog.Builder(activity.getSupportFragmentManager())
                .setLayoutRes(R.layout.dialog_ask_more_layout)
                .setGravity(Gravity.BOTTOM)
                .setScreenWidthAspect(activity, 1f)
                .addOnClickListener(R.id.tv_cancel, R.id.ll_what, R.id.ll_clear, R.id.ll_help)
                .setOnBindViewListener(new OnBindViewListener() {
                    @Override
                    public void bindView(BindViewHolder viewHolder) {
                    }
                })
                .setOnViewClickListener(onViewClickListener).create().show();
    }

    /**
     * 【分享】第三方
     */
    public static TDialog createShare(FragmentActivity activity, OnBindViewListener
            onBindViewListener, OnViewClickListener onViewClickListener) {
        return new TDialog.Builder(activity.getSupportFragmentManager())
                .setLayoutRes(R.layout.dialog_share_layout)
                .setGravity(Gravity.BOTTOM)
                .setScreenWidthAspect(activity, 1f)
                .addOnClickListener(R.id.ll_wechat, R.id.ll_friend, R.id.ll_qq, R.id.ll_sina, R.id.tv_cancel)
                .setOnBindViewListener(onBindViewListener)
                .setOnViewClickListener(onViewClickListener).create().show();
    }

    /**
     * 【状态】自定义
     */
    public static TDialog createCustomStatus(FragmentActivity activity, OnBindViewListener
            onBindViewListener, OnViewClickListener onViewClickListener) {
        return new TDialog.Builder(activity.getSupportFragmentManager())
                .setLayoutRes(R.layout.dialog_custom_status_layout)
                .setGravity(Gravity.BOTTOM)
                .setScreenWidthAspect(activity, 1f)
                .addOnClickListener(R.id.tv_confirm, R.id.iv_close)
                .setOnBindViewListener(onBindViewListener)
                .setOnViewClickListener(onViewClickListener).create().show();
    }

    public interface OnViewSelectCallBack extends OnViewClickListener {
        void callBack(BaseAdapter baseAdapter);
    }

    public interface OnViewCallBack extends OnViewClickListener {
        void callBack(TextView mTvTitle, TextView mTvHint, TextView mTvLeft, TextView mTvRight, View mVLineVertical);
    }

    public interface OnGIFCallBack extends OnViewClickListener {
        void callBack(ImageView imageView);
    }

    public interface OnViewThreeCallBack extends OnViewClickListener {
        void callBack(TextView mTvTitle, TextView mTvHint, TextView mTvOne, TextView mTvTwo, TextView mTvThree);
    }

    public static abstract class OnSVGACallBack implements OnViewClickListener {
        public abstract void callBack(SVGAImageView svgaImageView);

        @Override
        public void onViewClick(BindViewHolder viewHolder, View view, TDialog tDialog) {

        }
    }

    public abstract static class SimpleSVGACallBack implements SVGACallback {
        @Override
        public void onPause() {

        }

        @Override
        public void onRepeat() {

        }

        @Override
        public void onStep(int i, double v) {

        }
    }

    public interface OnUrgedBack {
        void onClick(View view, UrgedMsgModel urgedMsgModel);
    }

}
