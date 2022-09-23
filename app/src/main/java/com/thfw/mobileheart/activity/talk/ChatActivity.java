package com.thfw.mobileheart.activity.talk;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.scwang.wave.MultiWaveHeader;
import com.thfw.base.api.MusicApi;
import com.thfw.base.api.TalkApi;
import com.thfw.base.face.MyTextWatcher;
import com.thfw.base.face.OnRvItemListener;
import com.thfw.base.models.BaikeModel;
import com.thfw.base.models.ChatEntity;
import com.thfw.base.models.ChosenModel;
import com.thfw.base.models.DialogTalkModel;
import com.thfw.base.models.TalkModel;
import com.thfw.base.models.WeatherDetailsModel;
import com.thfw.base.net.HttpResult;
import com.thfw.base.net.NetParams;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.TalkPresenter;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.base.utils.GsonUtil;
import com.thfw.base.utils.HourUtil;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.RegularUtil;
import com.thfw.base.utils.StringUtil;
import com.thfw.base.utils.ToastUtil;
import com.thfw.base.utils.WeatherUtil;
import com.thfw.mobileheart.R;
import com.thfw.mobileheart.activity.BaseActivity;
import com.thfw.mobileheart.activity.audio.AudioHomeActivity;
import com.thfw.mobileheart.activity.test.TestingActivity;
import com.thfw.mobileheart.adapter.ChatAdapter;
import com.thfw.mobileheart.adapter.ChatSelectAdapter;
import com.thfw.mobileheart.constants.AnimFileName;
import com.thfw.mobileheart.util.DialogFactory;
import com.thfw.mobileheart.util.PageJumpUtils;
import com.thfw.ui.dialog.TDialog;
import com.thfw.ui.dialog.base.BindViewHolder;
import com.thfw.ui.voice.PolicyHelper;
import com.thfw.ui.voice.speech.SpeechHelper;
import com.thfw.ui.voice.tts.TtsHelper;
import com.thfw.ui.widget.AnimBottomRelativeLayout;
import com.thfw.ui.widget.LoadingView;
import com.thfw.ui.widget.SpeedLinearLayoutManager;
import com.thfw.ui.widget.TitleView;
import com.trello.rxlifecycle2.LifecycleProvider;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class ChatActivity extends BaseActivity<TalkPresenter> implements TalkPresenter.TalkUi<HttpResult<List<DialogTalkModel>>> {

    private androidx.recyclerview.widget.RecyclerView rvChatList;
    private ChatAdapter mChatAdapter;
    private AnimBottomRelativeLayout mRlSend;
    private android.widget.EditText mEtContent;
    private RelativeLayout mRlRoot;
    private TextView mTvSend;
    private com.thfw.ui.widget.TitleView mTitleView;
    private ImageView mIvInputType;
    private TextView mTvPressSpeech;
    private ConstraintLayout mVoiceControl;
    private ImageView mIvVoiceEdit;
    private ImageView mIvVoiceClose;
    private int[] locationClose;
    private int[] locationEdit;
    private boolean zoomClose;
    private boolean zoomEdit;
    private EditText mEtControlContent;
    private MultiWaveHeader mMultiWaveHeader;
    private ImageView mIvControlVoiceIng;
    private ImageView mIvControlPress;
    private TextView mTvControlHintSend;


    private Handler mMainHandler = new Handler(Looper.getMainLooper());

    private RecyclerView mRvSelect;
    private ChatSelectAdapter mSelectAdapter;
    private Helper mHelper = new Helper();
    // 1、树洞聊天 2、咨询助理
    private int mScene;
    private int mOriginScene;

    // 失败参数暂存;
    private int mSceneError;
    private NetParams mNetParamsError;
    private long lastTime;


    private int mCurrentChatType;
    private LoadingView mLoadingView;
    private TalkModel talkModel;
    private RefreshLayout mRefreshLayout;
    private DialogTalkModel mFirstTalkModel;
    private long joinTime;
    private long useTimeAI;
    private long useTimeTheme;
    private boolean handleResult;
    private boolean futureWeather;

    public static void startActivity(Context context, TalkModel talkModel) {
        context.startActivity(new Intent(context, ChatActivity.class).putExtra(KEY_DATA, talkModel));
    }

    public static void startActivity(Context context) {
        ChatActivity.startActivity(context, null);
    }


    @Override
    public int getContentView() {
        return R.layout.activity_chat;
    }

    @Override
    public TalkPresenter onCreatePresenter() {
        return new TalkPresenter(this);
    }

    @Override
    public void initView() {

        mRlRoot = (RelativeLayout) findViewById(R.id.rl_root);
        mLoadingView = findViewById(R.id.loadingView);
        mRefreshLayout = findViewById(R.id.refreshLayout);
        mRefreshLayout.setEnableRefresh(false);
        mRefreshLayout.setEnableLoadMore(false);
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull @NotNull RefreshLayout refreshLayout) {
                onPreMessage();
            }
        });
        rvChatList = (RecyclerView) findViewById(R.id.rv_chatList);
        rvChatList.setLayoutManager(new SpeedLinearLayoutManager(mContext));
        mRvSelect = (RecyclerView) findViewById(R.id.rv_select);
        // 设置布局管理器
        FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(mContext);
        // flexDirection 属性决定主轴的方向（即项目的排列方向）。类似 LinearLayout 的 vertical 和 horizontal。
        flexboxLayoutManager.setFlexDirection(FlexDirection.ROW);
        // 主轴为水平方向，起点在左端。
        // flexWrap 默认情况下 Flex 跟 LinearLayout 一样，都是不带换行排列的，但是flexWrap属性可以支持换行排列。
        flexboxLayoutManager.setFlexWrap(FlexWrap.WRAP);
        // 按正常方向换行
        // justifyContent 属性定义了项目在主轴上的对齐方式。
        flexboxLayoutManager.setJustifyContent(JustifyContent.FLEX_START);//交叉轴的起点对齐。
        mRvSelect.setLayoutManager(flexboxLayoutManager);

        softInput();
        mChatAdapter = new ChatAdapter(null);
        rvChatList.setAdapter(mChatAdapter);
        mChatAdapter.setRecommendListener(new ChatAdapter.OnRecommendListener() {
            @Override
            public void onRecommend(int type, DialogTalkModel.RecommendInfoBean recommendInfoBean, int position) {
                handleResult = mChatAdapter.getItemCount() < position + 5;
                TalkItemJumpHelper.onItemClick(mContext, type, recommendInfoBean);
            }
        });

        rvChatList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // 获得当前得到焦点的View，一般情况下就是EditText（特殊情况就是轨迹求或者实体案件会移动焦点）

                    View vFocus = getCurrentFocus();
                    if (isShouldHideInput(vFocus, event)) {
                        hideSoftInput(v.getWindowToken());
                        return true;
                    }
                }
                return false;
            }
        });


        mTitleView = (TitleView) findViewById(R.id.titleView);
        mTitleView.getIvBack().setOnClickListener(v -> {
            finishService();
        });
        mTitleView.setRightText("历史记录");
        mTitleView.setRightOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TalkHistoryActivity.startActivity(mContext, mScene);
            }
        });
        initChatInput();
    }

    /**
     * 历史记录查询
     */
    private void onPreMessage() {
        if (mFirstTalkModel != null) {
            new TalkPresenter<List<DialogTalkModel>>(new TalkPresenter.TalkUi<List<DialogTalkModel>>() {
                @Override
                public LifecycleProvider getLifecycleProvider() {
                    return ChatActivity.this;
                }

                @Override
                public void onSuccess(List<DialogTalkModel> data) {
                    LogUtil.d("mChatAdapter JPS size onSuccess");
                    List<ChatEntity> list = new ArrayList<>();
                    if (EmptyUtil.isEmpty(data)) {
                        list.add(ChatEntity.createHint("没有更多数据了"));
                        mRefreshLayout.setEnableRefresh(false);
                    } else {
                        mFirstTalkModel = data.get(0);
                        boolean addTime = false;
                        long lastTime = 0;
                        long firstTime = 0;
                        for (DialogTalkModel model : data) {
                            ChatEntity chatEntity = new ChatEntity(model);
                            chatEntity.setTime(model.getTimeMills());
                            long limit = chatEntity.time - lastTime;
                            if (!addTime || limit > HourUtil.LEN_MINUTE || chatEntity.time - firstTime > HourUtil.LEN_5_MINUTE) {
                                addTime = true;
                                list.add(ChatEntity.createTime(chatEntity.time));
                                firstTime = chatEntity.time;
                            }
                            lastTime = chatEntity.time;
                            list.add(chatEntity);
                            // 个人选项
                            if (model.hasCheckRadio()) {
                                list.add(model.getMeCheckRadio());
                            }
                        }
                    }
                    LogUtil.d("mChatAdapter JPS size 12 - " + mChatAdapter.getItemCount());
                    LogUtil.d("mChatAdapter JPS size " + list.size());
                    mChatAdapter.addDataListNotify(list, true);
                    mRefreshLayout.finishRefresh(true);
                }


                @Override
                public void onFail(ResponeThrowable throwable) {
                    LogUtil.d("mChatAdapter JPS size fail" + throwable.getMessage());
                    mRefreshLayout.finishRefresh(false);
                }
            }) {
            }.onDialogHistory(mOriginScene, "", mFirstTalkModel.getId(), "prev");
        } else {
            LogUtil.d("mChatAdapter JPS size fail mFirstTalkModel == null");
            mRefreshLayout.finishRefresh(false);
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        finishService();
    }

    /**
     * 【弹框】 结束服务提醒
     */
    private void finishService() {
        DialogFactory.createCustomDialog(this, new DialogFactory.OnViewCallBack() {
            @Override
            public void callBack(TextView mTvTitle, TextView mTvHint, TextView mTvLeft, TextView mTvRight, View mVLineVertical) {
                mTvHint.setText(R.string.finishServiceTitle);
                mTvTitle.setVisibility(View.GONE);
            }

            @Override
            public void onViewClick(BindViewHolder viewHolder, View view, TDialog tDialog) {
                if (view.getId() == R.id.tv_left) {
                    tDialog.dismiss();
                } else {
                    tDialog.dismiss();
                    finish();
                }
            }
        });
    }

    private void hideSoftInput(IBinder windowToken) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(windowToken, 0);

    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时没必要隐藏
     *
     * @param v
     * @param event
     * @return
     */
    private boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击EditText的事件，忽略它。
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
        return false;
    }

    private void softInput() {
        rvChatList.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {//

            @Override
            public void onLayoutChange(View v, int left, int top, int right,
                                       int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom) {
                    rvChatList.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            rvChatList.scrollToPosition(mChatAdapter.getItemCount() - 1);
                        }
                    }, 100);
                }
            }
        });
    }

    @Override
    public void initData() {
        talkModel = (TalkModel) getIntent().getSerializableExtra(KEY_DATA);
        if (talkModel == null) {
            ToastUtil.show("参数错误");
            finish();
            return;
        }
        mScene = talkModel.getType() == TalkApi.JOIN_TYPE_AI ? 1 : 2;
        mOriginScene = mScene;
        joinTime = System.currentTimeMillis();
        mPresenter.onJoinDialog(talkModel.getType(), talkModel.getId());
        mTitleView.setCenterText(talkModel.getTitle());
    }

    /**
     * 对话和吐槽切换时间刷新
     */
    private void refreshTime() {
        long currentTimeMillis = System.currentTimeMillis();
        if (mScene == 1) {
            useTimeAI = useTimeAI + (currentTimeMillis - joinTime);
            LogUtil.d(TAG, "useTimeAI = " + HourUtil.getLimitTimeALl(useTimeAI));
        } else {
            useTimeTheme = useTimeTheme + (currentTimeMillis - joinTime);
            LogUtil.d(TAG, "useTimeTheme = " + HourUtil.getLimitTimeALl(useTimeAI));
        }
        joinTime = currentTimeMillis;
    }


    /**
     * 获取使用时长，根据主题对话和倾诉吐槽
     *
     * @param type
     * @return
     */
    public long getUseDuration(int type) {
        refreshTime();
        switch (type) {
            // 倾诉吐槽
            case TalkApi.JOIN_TYPE_AI:
                return useTimeAI;
            // 咨询助理 || 主题对话
            case TalkApi.JOIN_TYPE_GUIDANCE:
            case TalkApi.JOIN_TYPE_SPEECH_CRAFT:
                break;
        }

        return useTimeTheme;
    }

    private void initChatInput() {
        mRlSend = (AnimBottomRelativeLayout) findViewById(R.id.rl_send);
        mEtContent = (EditText) findViewById(R.id.et_content);
        mEtContent.addTextChangedListener(new MyTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTvSend.setEnabled(!EmptyUtil.isEmpty(s.toString().trim()));
            }
        });

        mTvSend = (TextView) findViewById(R.id.tv_send);
        mTvSend.setEnabled(!EmptyUtil.isEmpty(mEtContent.getText().toString()));
        mTvSend.setOnClickListener(v -> {
            sendInputText(mEtContent.getText().toString());
        });
        mIvVoiceEdit = findViewById(R.id.iv_control_voice_edit);
        mIvVoiceClose = findViewById(R.id.iv_control_voice_close);
        mVoiceControl = findViewById(R.id.i_input_voice_control);
        mTvPressSpeech = findViewById(R.id.tv_press_speech);
        mTvControlHintSend = findViewById(R.id.tv_control_hint_send);
        mIvControlPress = findViewById(R.id.iv_control_press);
        mIvControlVoiceIng = findViewById(R.id.iv_control_voice_ing);
        mIvInputType = findViewById(R.id.iv_input_type);
        mEtControlContent = findViewById(R.id.et_control_content);
        mIvInputType.setOnClickListener(v -> {
            boolean inputTypeSelected = !mIvInputType.isSelected();
            mIvInputType.setSelected(inputTypeSelected);
            if (inputTypeSelected) {
                mTvPressSpeech.setVisibility(View.VISIBLE);
                mEtContent.setVisibility(View.GONE);
                hideInput();
            } else {
                mEtContent.setVisibility(View.VISIBLE);
                mTvPressSpeech.setVisibility(View.GONE);
            }
        });
        mMultiWaveHeader = findViewById(R.id.waveHeader);
        mTvPressSpeech.setOnTouchListener(new View.OnTouchListener() {


            int count;
            // 录音60S后停止
            private TextView mStopHint;
            Runnable mStopRunnable = new Runnable() {

                @Override
                public void run() {
                    count--;
                    if (count <= 10) {
                        if (mStopHint != null) {
                            mStopHint.setVisibility(View.VISIBLE);
                            if (count <= 0) {
                                mStopHint.setText("已停止录音");
                                PolicyHelper.getInstance().end();
                            } else {
                                mStopHint.setText(count + "S后将停止录音");
                            }
                        }
                    } else {
                        mStopHint.setVisibility(View.INVISIBLE);
                    }
                    if (count > 0) {
                        mMainHandler.postDelayed(mStopRunnable, 1000);
                    }
                }
            };

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                LogUtil.d("mTvPressSpeech Y = " + event.getY());
                LogUtil.d("mTvPressSpeech X = " + event.getX());
                LogUtil.d("mTvPressSpeech RawY = " + event.getRawY());
                LogUtil.d("mTvPressSpeech RawX = " + event.getRawX());
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    vibrate();
                    openVoiceControl();
                    PolicyHelper.getInstance().startPressed();
                    mIvVoiceEdit.post(new Runnable() {
                        @Override
                        public void run() {
                            initCloseEditLocation();
                        }
                    });
                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    if (isInCloseButton(event.getRawX(), event.getRawY())) {
                        if (!zoomClose) {
                            zoomClose = true;
                            setBlackOrWhite(mIvVoiceClose, false);
                            mIvVoiceClose.animate().scaleX(1.3f).scaleY(1.3f);
                            vibrate();
                        }
                        zoomEdit = false;
                    } else if (isInEditButton(event.getRawX(), event.getRawY())) {
                        if (!zoomEdit) {
                            zoomEdit = true;
                            setBlackOrWhite(mIvVoiceEdit, false);
                            mIvVoiceEdit.animate().scaleX(1.3f).scaleY(1.3f);
                            vibrate();
                        }
                        zoomClose = false;
                    } else {
                        if (zoomEdit) {
                            zoomEdit = false;
                            setBlackOrWhite(mIvVoiceEdit, true);
                            mIvVoiceEdit.animate().scaleX(1).scaleY(1);
                        }
                        if (zoomClose) {
                            zoomClose = false;
                            setBlackOrWhite(mIvVoiceClose, true);
                            mIvVoiceClose.animate().scaleX(1).scaleY(1);
                        }
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    PolicyHelper.getInstance().end();
                    // 编辑文本
                    if (isInEditButton(event.getRawX(), event.getRawY())) {
                        mTvControlHintSend.setVisibility(View.GONE);
                        mIvControlVoiceIng.setVisibility(View.GONE);
                        mIvControlPress.setVisibility(View.GONE);
                        mIvVoiceClose.setOnClickListener(v2 -> {
                            closeVoiceControl();
                            mIvVoiceEdit.setColorFilter(Color.LTGRAY);
                        });
                        mIvVoiceEdit.setImageResource(R.drawable.ic_baseline_check_24);
                        mIvVoiceEdit.setColorFilter(Color.GREEN);
                        mIvVoiceEdit.setOnClickListener(v1 -> {
                            sendInputText(mEtControlContent.getText().toString());
                            closeVoiceControl();
                        });
                    } else if (isInCloseButton(event.getRawX(), event.getRawY())) {
                        closeVoiceControl();
                    } else {
                        sendInputText(mEtControlContent.getText().toString());
                        closeVoiceControl();
                    }
                }
                return false;
            }

            private void openVoiceControl() {
                closeVoiceControl();
                mVoiceControl.setVisibility(View.VISIBLE);
                mTvControlHintSend.setVisibility(View.VISIBLE);
                mIvControlVoiceIng.setVisibility(View.VISIBLE);
                mIvControlPress.setVisibility(View.VISIBLE);
                // 录音60S后停止
                if (mStopHint == null) {
                    mStopHint = findViewById(R.id.tv_stop_hint);
                }
                if (mStopHint != null) {
                    mStopHint.setVisibility(View.INVISIBLE);
                }
                count = 60;
                mMainHandler.postDelayed(mStopRunnable, 1000);
            }

            private void closeVoiceControl() {
                count = 0;
                mMainHandler.removeCallbacks(mStopRunnable);
                hideInput();
                zoomClose = false;
                zoomEdit = false;
                mIvVoiceEdit.setColorFilter(Color.LTGRAY);
                mIvVoiceClose.setScaleX(1f);
                mIvVoiceClose.setScaleY(1f);
                mIvVoiceEdit.setScaleX(1f);
                mIvVoiceEdit.setImageResource(R.drawable.ic_baseline_edit_24);
                mIvVoiceEdit.setScaleY(1f);
                setBlackOrWhite(mIvVoiceClose, true);
                setBlackOrWhite(mIvVoiceEdit, true);
                mVoiceControl.setVisibility(View.GONE);
                mEtControlContent.setText("");
            }
        });
        mTvPressSpeech.setOnClickListener(v -> {

        });

        PolicyHelper.getInstance().setResultListener(new SpeechHelper.ResultListener() {

            @Override
            public void onResult(String result, boolean append, boolean end) {
                // 没有按压返回
                if (!PolicyHelper.getInstance().isPressMode()) {
                    return;
                }
                mEtControlContent.setText(result);
            }

            @Override
            public void onIng(boolean ing) {
                LogUtil.d(TAG, "ing =================================== " + ing);
                if (ing) {
                    mMultiWaveHeader.start();
                } else {
                    mMultiWaveHeader.stop();
                }
            }
        });


    }


    private void setBlackOrWhite(ImageView imageView, boolean black) {
        if (black) {
            imageView.setBackgroundResource(R.drawable.voice_control_btn_bg);
        } else {
            imageView.setBackgroundResource(R.drawable.voice_control_btn_white_bg);
        }
    }

    private boolean isInCloseButton(float rawX, float rawY) {
        if (mIvVoiceClose == null || locationClose == null) {
            return false;
        }
        return rawX > locationClose[0] && rawX < locationClose[0] + mIvVoiceClose.getWidth()
                && rawY > locationClose[1] && rawY < locationClose[1] + mIvVoiceClose.getHeight();
    }

    private boolean isInEditButton(float rawX, float rawY) {
        if (mIvVoiceEdit == null || locationEdit == null) {
            return false;
        }
        return rawX > locationEdit[0] && rawX < locationEdit[0] + mIvVoiceEdit.getWidth()
                && rawY > locationEdit[1] && rawY < locationEdit[1] + mIvVoiceEdit.getHeight();
    }

    private void initCloseEditLocation() {


        if (locationClose == null) {
            locationClose = new int[2];
            mIvVoiceClose.getLocationInWindow(locationClose);
            LogUtil.d("mIvVoiceClose X = " + locationClose[0]);
            LogUtil.d("mIvVoiceClose Y = " + locationClose[1]);
        }

        if (locationEdit == null) {
            locationEdit = new int[2];
            mIvVoiceEdit.getLocationInWindow(locationEdit);
            LogUtil.d("locationEdit X = " + locationEdit[0]);
            LogUtil.d("locationEdit Y = " + locationEdit[1]);
        }
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(120);
    }

    @Override
    public LifecycleProvider getLifecycleProvider() {
        return this;
    }

    @Override
    public void onSuccess(HttpResult<List<DialogTalkModel>> model) {
        LogUtil.d(TAG, " +++++++++++++++++ model = " + model);
        if (!mLoadingView.isHide()) {
            mLoadingView.hide();
            mRefreshLayout.setEnableRefresh(true);
        }
        if (model != null) {
            List<DialogTalkModel> data = model.getData();
            if (model.getExt() != null) {
                if (model.getExt().isEnterDeepDialog()) {
                    sendData(ChatEntity.createJoinPage("欢迎进入深度疏导主题对话"));
                }
                if (model.getExt().isSentiment()
                        && AnimFileName.getTalkEmojiBySentiment(model.getExt().getSentiment()) != null) {
                    sendData(ChatEntity.createEmoji(model.getExt().getSentiment()));
                }
            }
            if (data != null) {
                // 再见
                if (data.isEmpty()) {
                    finish();
                    LogUtil.d(TAG, "end 结束对话 data:[] ++++++++++++++++++++++++++");
                    return;
                }
                mSceneError = -1;
                mNetParamsError = null;
                mHelper.setTalks(data);
                onTalkEngine();
            }
        }

    }

    public void onTalkEngine() {
        if (mHelper.hasNext()) {
            onTalkData(mHelper.next());
            if (mHelper.hasNext()) {
                mMainHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onTalkEngine();
                    }
                }, 800);
            }
        }
    }

    /**
     * 发送
     *
     * @param chatEntity
     */
    public void sendData(ChatEntity chatEntity) {
        boolean addTime = false;
        ChatEntity lastChatEntity = null;
        // 添加时间
        if (mChatAdapter.getItemCount() > 0) {
            lastChatEntity = mChatAdapter.getDataList().get(mChatAdapter.getItemCount() - 1);
            if (lastChatEntity != null && lastChatEntity.isTalkType()) {
                long limitTime = chatEntity.time - lastChatEntity.time;
                LogUtil.d(TAG, "sendData -> limitTime = " + limitTime);
                if (limitTime > HourUtil.LEN_MINUTE ||
                        System.currentTimeMillis() - lastTime > HourUtil.LEN_5_MINUTE) {
                    lastTime = System.currentTimeMillis();
                    mChatAdapter.addData(ChatEntity.createTime());
                    mChatAdapter.notifyItemInserted(mChatAdapter.getItemCount() - 1);
                    addTime = true;
                    if (lastChatEntity.type == ChatEntity.TYPE_TO && mChatAdapter.getItemCount() > 2) {
                        mChatAdapter.notifyItemChanged(mChatAdapter.getItemCount() - 2);
                    }
                }
            }
        } else {
            lastTime = System.currentTimeMillis();
            mChatAdapter.addData(ChatEntity.createTime());
        }
        mChatAdapter.addData(chatEntity);
        // 插入一条
        mChatAdapter.notifyItemInserted(mChatAdapter.getItemCount() - 1);
        if (!addTime && lastChatEntity != null && lastChatEntity.type == ChatEntity.TYPE_TO
                && mChatAdapter.getItemCount() > 2) {
            mChatAdapter.notifyItemChanged(mChatAdapter.getItemCount() - 2);
        }
        rvChatList.smoothScrollToPosition(mChatAdapter.getItemCount() - 1);
    }


    /**
     * 语音匹配自用输入和单选题
     *
     * @param result
     * @param end
     */
    private void chooseOption(String result, boolean end) {
        LogUtil.d(TAG, "chooseOption begin");
        if (StringUtil.isEmpty(result)) {
            return;
        }
        LogUtil.d(TAG, "chooseOption(result)" + result + "; end = " + end);
        LogUtil.d(TAG, "chooseOption(result) mCurrentChatType = " + mCurrentChatType);
        if (mCurrentChatType == ChatEntity.TYPE_INPUT) {
            if (end) {
                sendInputText(result);
            }
        } else if (mCurrentChatType == ChatEntity.TYPE_FROM_SELECT) {
            if (mSelectAdapter == null) {
                return;
            }

            List<DialogTalkModel.CheckRadioBean> list = mSelectAdapter.getDataList();
            if (EmptyUtil.isEmpty(list)) {
                return;
            }

            for (int i = 0, size = list.size(); i < size; i++) {
                if (result.equals(list.get(i).getValue())) {
                    mSelectAdapter.getOnRvItemListener().onItemClick(list, i);
                    return;
                }
            }
            if (!end) {
                return;
            }
            HashMap<String, String> radio = new HashMap<>();
            for (DialogTalkModel.CheckRadioBean bean : list) {
                radio.put(String.valueOf(bean.getKey()), bean.getValue());
            }
            LogUtil.d(TAG, "ChosenModel = " + GsonUtil.toJson(radio));
            new TalkPresenter(new TalkPresenter.TalkUi<ChosenModel>() {
                @Override
                public LifecycleProvider getLifecycleProvider() {
                    return ChatActivity.this;
                }

                @Override
                public void onSuccess(ChosenModel data) {
                    LogUtil.d(TAG, "ChosenModel = " + GsonUtil.toJson(data));
                    if (data != null && !TextUtils.isEmpty(data.chosen)) {
                        if (mSelectAdapter == null || mSelectAdapter.getOnRvItemListener() == null) {
                            return;
                        }
                        for (int i = 0, size = list.size(); i < size; i++) {
                            if (data.chosen.equals(String.valueOf(list.get(i).getKey()))) {
                                LogUtil.d(TAG, "ChosenModel = data.chosen = " + list.get(i).getValue());
                                mSelectAdapter.getOnRvItemListener().onItemClick(list, i);
                                break;
                            }
                        }
                    }
                }

                @Override
                public void onFail(ResponeThrowable throwable) {

                }
            }).onChooseOption(result, radio);
        }

    }

    private void sendInputText(String inputText) {
        if (mHelper.getTalkModel() == null) {
            LogUtil.d(TAG, "mHelper.getTalkModel() == null 对话还未开始！！！");
            return;
        }
        if (EmptyUtil.isEmpty(inputText)) {
            return;
        }

        if (mScene == 1 && inputText.length() < 35) {
            if (checkAbility(inputText)) {
                return;
            }
        }
        ChatEntity chatEntity = new ChatEntity();
        chatEntity.type = ChatEntity.TYPE_TO;
        chatEntity.talk = inputText;

        NetParams netParams = NetParams.crete().add("question", chatEntity.talk);
        LogUtil.d(TAG, "inputText = " + chatEntity.talk);
        if (mScene != 1) {
            netParams.add("id", mHelper.getTalkModel().getId());
        }
        onDialog(mScene, netParams);
        sendData(chatEntity);
        mEtContent.setText("");
    }

    private boolean checkAbility(String inputText) {
        String tempText = inputText;
        tempText = tempText.replaceAll("(小密|，|。|！|!|？)", "");
        if (tempText.length() >= 2 && tempText.length() <= 4) {
            if (checkTureName(tempText)) {
                ChatEntity chatEntity = new ChatEntity();
                chatEntity.type = ChatEntity.TYPE_TO;
                chatEntity.talk = inputText;
                sendData(chatEntity);
                hideInput();
                mEtContent.setText("");
                LogUtil.i(TAG, "----------- speech baike -------------");
                return true;
            }
        }
        // 天气查询
        String weatherId = checkWeather(tempText);
        if (!TextUtils.isEmpty(weatherId)) {
            hideInput();
            mEtContent.setText("");
            MusicApi.requestWeather(weatherId, new MusicApi.WeatherCallback() {
                @Override
                public void onFailure(int code, String msg) {
                    ToastUtil.show("没有查到天气情况");
                }

                @Override
                public void onResponse(WeatherDetailsModel weatherInfoModel) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            sendLocalData(inputText, futureWeather ? weatherInfoModel.getWeekDesc() : weatherInfoModel.getDesc());
                        }
                    });
                }
            });
            LogUtil.i(TAG, "----------- search weather -------------");
            return true;
        }
        // 讲个笑话
        String joke = checkJoke(tempText);
        if (!TextUtils.isEmpty(joke)) {
            sendLocalData(inputText, joke);
            hideInput();
            mEtContent.setText("");
            LogUtil.i(TAG, "----------- speech joke -------------");
            return true;
        }
        return false;
    }

    private void sendData(String inputText, boolean sendTo) {
        ChatEntity chatEntity = new ChatEntity();
        chatEntity.type = ChatEntity.TYPE_TO;
        chatEntity.talk = inputText;

        NetParams netParams = NetParams.crete().add("question", chatEntity.talk);
        LogUtil.d(TAG, "inputText = " + chatEntity.talk);
        if (mScene != 1) {
            netParams.add("id", mHelper.getTalkModel().getId());
        }

        onDialog(mScene, netParams);
        if (sendTo) {
            sendData(chatEntity);
        }
        mEtContent.setText("");
    }

    private boolean checkTureName(String inputText) {
        if (!RegularUtil.isTrueName(inputText)) {
            return false;
        }
        String surname = mContext.getResources().getString(R.string.surname);
        String[] surnames = surname.split(",");
        List<String> surnameList = Arrays.asList(surnames);
        if ((surnameList.contains(inputText.substring(0, 1)) || surnameList.contains(inputText.substring(0, 2)))
                && !inputText.substring(0, 1).equals(inputText.substring(1, 2))) {
            if (containsByWords(inputText)) {
                return false;
            }
            MusicApi.requestBaiKe(inputText, new MusicApi.BaiKeCallback() {
                @Override
                public void onFailure(int code, String msg) {
                    LogUtil.e(TAG, "code = " + code + " ; msg = " + msg);
                    sendData(inputText, false);
                }

                @Override
                public void onResponse(BaikeModel baikeModel) {
                    if (EmptyUtil.isEmpty(ChatActivity.this)) {
                        return;
                    }
                    runOnUiThread(() -> {
                        if (baikeModel.isDescNull()) {
                            sendData(inputText, false);
                        } else {
                            DialogTalkModel talkModel = new DialogTalkModel();
                            talkModel.setType(ChatEntity.TYPE_FROM_NORMAL);
                            talkModel.setQuestion(baikeModel.getDesc());
                            onTalkData(talkModel);
                        }
                    });
                }
            });
            return true;
        }
        return false;
    }

    private String checkWeather(String inputText) {
        if (mScene != 1) {
            futureWeather = false;
            return null;
        }

        String tempText = inputText;
        String weather = ".{0,5}(气温|温度|天气|下雨|有雨|下雪|有雪).{0,3}(怎么样|冷不冷|热不热|吗|嘛).{0,2}";
        String weather2 = ".{0,5}(冷不冷|热不热|冷吗|热吗).{0,2}";
        if (tempText.matches(weather) || tempText.matches(weather2)) {

            String weatherReplace = "(气温|温度|天气|下雨|有雨|下雪|有雪|冷不冷|热不热|冷吗|热吗)|(怎么样|吗|嘛).{0,2}";
            String cityName = tempText.replaceAll(weatherReplace, "");
            cityName = cityName.replaceAll("(今天|现在|今日|市|的)", "");
            LogUtil.i(TAG, "cityName = " + cityName);

            if (TextUtils.isEmpty(cityName)) {
                futureWeather = false;
                return WeatherUtil.getWeatherCityId();
            } else {
                futureWeather = cityName.matches(".{0,5}(昨天|明天|后天|未来|一周).{0,5}");
                LogUtil.i(TAG, "futureWeather = " + futureWeather);
                if (futureWeather) {
                    cityName = cityName.replaceAll("(昨天|明天|后天|未来|一周)", "");
                }
                if (TextUtils.isEmpty(cityName)) {
                    return WeatherUtil.getWeatherCityId();
                }
                return WeatherUtil.getWeatherCityId(cityName);

            }
        } else {
            futureWeather = false;
            return null;
        }
    }

    private String checkJoke(String inputText) {
        if (mScene != 1) {
            return null;
        }

        String tempText = inputText;

        if (tempText.matches(".{0,4}(讲|说|来|听|开).{0,3}(笑话|玩笑).{0,2}")) {
            String[] jokes = mContext.getResources().getStringArray(R.array.jokes);
            return jokes[new Random().nextInt(jokes.length)];
        } else {
            return null;
        }
    }

    /**
     * 不经过网络，假回答，历史记录不存在
     *
     * @param input
     * @param question
     */
    private void sendLocalData(String input, String question) {
        ChatEntity chatEntity = new ChatEntity();
        chatEntity.type = ChatEntity.TYPE_TO;
        chatEntity.talk = input;
        sendData(chatEntity);
        mMainHandler.postDelayed(() -> {
            if (EmptyUtil.isEmpty(ChatActivity.this)) {
                return;
            }
            DialogTalkModel talkModel = new DialogTalkModel();
            talkModel.setType(ChatEntity.TYPE_FROM_NORMAL);
            talkModel.setQuestion(question);
            onTalkData(talkModel);
        }, 1200);
    }

    /**
     * 处理对话数据
     *
     * @param talkModel
     */
    private void onTalkData(DialogTalkModel talkModel) {
        Log.d(TAG, talkModel != null ? talkModel.toString() : "talkModel is null");
        if (talkModel == null) {
            return;
        }
        if (mFirstTalkModel == null) {
            mFirstTalkModel = talkModel;
        }
        final ChatEntity chatEntity = new ChatEntity(talkModel);
        // 树洞 自由输入逻辑判断
        if (mScene == 1) {
            if (chatEntity.type == ChatEntity.TYPE_FROM_SELECT
                    && !EmptyUtil.isEmpty(chatEntity.getTalkModel().getCheckRadio())) {
                mCurrentChatType = chatEntity.type;
            } else {
                mCurrentChatType = ChatEntity.TYPE_INPUT;
            }
        } else {
            mCurrentChatType = chatEntity.type;
        }
        onSceneHandle(talkModel);
        sendData(chatEntity);
        if (mCurrentChatType == ChatEntity.TYPE_FROM_SELECT) {
            hideInput();
            mRvSelect.setVisibility(View.VISIBLE);
            mRlSend.setVisibility(false);
            if (mSelectAdapter == null) {
                mSelectAdapter = new ChatSelectAdapter(talkModel.getCheckRadio());
                mRvSelect.setAdapter(mSelectAdapter);
            } else {
                mSelectAdapter.setDataListNotify(talkModel.getCheckRadio());
            }
            setSelectItemListener(chatEntity);
        } else if (mCurrentChatType == ChatEntity.TYPE_INPUT) {
            mRlSend.setVisibility(true);
            mRvSelect.setVisibility(View.GONE);
        } else {
            hideInput();
            mRlSend.setVisibility(false);
            mRvSelect.setVisibility(View.GONE);
        }
    }

    /**
     * 倾诉吐槽和主题对话跳转处理
     *
     * @param talkModel
     */
    private void onSceneHandle(DialogTalkModel talkModel) {
        if (talkModel.getScene(mScene) != mScene) {
            refreshTime();
            mScene = talkModel.getScene(mScene);
            if (mScene == 1) {
                mTitleView.setCenterText("倾诉吐槽");
                sendData(ChatEntity.createJoinPage("欢迎进入倾诉吐槽"));
            } else {
                mTitleView.setCenterText("主题对话");
                sendData(ChatEntity.createJoinPage("欢迎进入专业心理咨询"));
            }
        }
    }

    private void setSelectItemListener(ChatEntity chatEntity) {
        mSelectAdapter.setOnRvItemListener(new OnRvItemListener<DialogTalkModel.CheckRadioBean>() {
            @Override
            public void onItemClick(List<DialogTalkModel.CheckRadioBean> list, int position) {

                DialogTalkModel.CheckRadioBean radioBean = list.get(position);
                sendData(new ChatEntity(ChatEntity.TYPE_TO, radioBean.getValue()));
                if (radioBean.getKey() > 0) {
                    NetParams netParams = NetParams.crete().add("id", chatEntity.getTalkModel().getId())
                            .add("value", radioBean.getKey());
                    onDialog(mScene, netParams);
                } else {
                    PageJumpUtils.jump(radioBean.getHref(), new PageJumpUtils.OnJumpListener() {
                        @Override
                        public void onJump(int type, Object obj) {
                            handleResult = true;
                            switch (type) {
                                case PageJumpUtils.JUMP_TEXT:
                                    startActivityForResult(new Intent(mContext, TestingActivity.class), type);
                                    break;
                                case PageJumpUtils.JUMP_MUSIC:
                                    startActivityForResult(new Intent(mContext, AudioHomeActivity.class), type);
                                    break;
                                case PageJumpUtils.JUMP_AI_HOME:
                                    startActivityForResult(new Intent(mContext, ThemeListActivity.class), type);
                                    break;
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onFail(ResponeThrowable throwable) {
        LogUtil.d(TAG, "onFail = " + throwable.getMessage());
        if (mChatAdapter != null && mChatAdapter.getItemCount() == 0) {
            mLoadingView.showFail(v -> {
                mScene = talkModel.getType() == 3 ? 1 : 2;
                mPresenter.onJoinDialog(talkModel.getType(), talkModel.getId());
            });
            return;
        }
        // 消息重发
        if (mSceneError != -1 && mNetParamsError != null && mChatAdapter != null) {
            ChatEntity lastChatEntity = mChatAdapter.getDataList().get(mChatAdapter.getItemCount() - 1);
            if (lastChatEntity != null) {
                lastChatEntity.onError();
                mChatAdapter.notifyItemChanged(mChatAdapter.getItemCount() - 1);
                mChatAdapter.setOnSendStateChangeListener(new ChatAdapter.onSendStateChangeListener() {
                    @Override
                    public void onErrorResend() {
                        lastChatEntity.onLoading();
                        mChatAdapter.notifyItemChanged(mChatAdapter.getItemCount() - 1);
                        onDialog(mSceneError, mNetParamsError);
                    }
                });
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (!handleResult) {
            return;
        }
        if (resultCode != RESULT_OK) {
            return;
        }
        if (mScene == 1) {
            // 树洞/倾诉吐槽 返回继续聊
            DialogTalkModel talkModel = new DialogTalkModel();
            talkModel.setType(ChatEntity.TYPE_INPUT);
            talkModel.setQuestion("继续聊");
            onTalkData(talkModel);
            return;
        }
        switch (requestCode) {
            case PageJumpUtils.JUMP_TEXT:
            case PageJumpUtils.JUMP_MUSIC:
            case PageJumpUtils.JUMP_AI_HOME:
                mPresenter.onDialog(mScene, NetParams.crete()
                        .add("id", mHelper.getTalkModel().getId())
                        .add("value", "list_return"));
                break;
            case ChatEntity.TYPE_RECOMMEND_TEXT:
            case ChatEntity.TYPE_RECOMMEND_VIDEO:
            case ChatEntity.TYPE_RECOMMEND_AUDIO:
            case ChatEntity.TYPE_RECOMMEND_AUDIO_ETC:
            case ChatEntity.TYPE_RECOMMEND_TEST:
                mPresenter.onDialog(mScene, NetParams.crete()
                        .add("id", mHelper.getTalkModel().getId())
                        .add("value", "talking_recommend"));
                break;
            default:
                ToastUtil.show("未处理该类型结果 -> " + requestCode);
                break;
        }
    }

    private void onDialog(int mScene, NetParams netParams) {
        mSceneError = mScene;
        mNetParamsError = netParams;
        mPresenter.onDialog(mScene, netParams);
    }


    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        PolicyHelper.getInstance().end();
        TtsHelper.getInstance().stop();
    }

    public class Helper {

        private List<DialogTalkModel> mTalks;
        private int index = 0;
        private DialogTalkModel talkModel;

        public void setTalks(List<DialogTalkModel> mTalks) {
            this.mTalks = mTalks;
            this.index = 0;
        }

        public boolean hasNext() {
            return mTalks != null && mTalks.size() > index;
        }

        public synchronized DialogTalkModel next() {
            if (hasNext()) {
                talkModel = mTalks.get(index);
                index++;
                return talkModel;
            } else {
                return null;
            }
        }

        public DialogTalkModel getTalkModel() {
            return talkModel;
        }
    }

    public boolean containsByWords(String word) {
        long start = System.currentTimeMillis();
        String keywords = mContext.getResources().getString(R.string.short_keyword);
        boolean contains = keywords.contains(word);
        LogUtil.i(TAG, "containsByWords time = " + (System.currentTimeMillis() - start));
        return contains;
    }
}