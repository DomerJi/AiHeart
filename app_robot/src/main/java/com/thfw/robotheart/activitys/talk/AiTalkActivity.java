package com.thfw.robotheart.activitys.talk;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.iflytek.cloud.SpeechError;
import com.opensource.svgaplayer.SVGAImageView;
import com.thfw.base.face.MyTextWatcher;
import com.thfw.base.face.OnRvItemListener;
import com.thfw.base.models.ChatEntity;
import com.thfw.base.models.ChosenModel;
import com.thfw.base.models.DialogTalkModel;
import com.thfw.base.models.TalkModel;
import com.thfw.base.net.HttpResult;
import com.thfw.base.net.NetParams;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.TalkPresenter;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.base.utils.GsonUtil;
import com.thfw.base.utils.HourUtil;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.SharePreferenceUtil;
import com.thfw.base.utils.StringUtil;
import com.thfw.base.utils.ToastUtil;
import com.thfw.robotheart.R;
import com.thfw.robotheart.activitys.RobotBaseActivity;
import com.thfw.robotheart.activitys.audio.AudioHomeActivity;
import com.thfw.robotheart.activitys.test.TestActivity;
import com.thfw.robotheart.adapter.ChatAdapter;
import com.thfw.robotheart.adapter.ChatSelectAdapter;
import com.thfw.robotheart.constants.AnimFileName;
import com.thfw.robotheart.util.DialogRobotFactory;
import com.thfw.robotheart.util.PageJumpUtils;
import com.thfw.robotheart.util.SVGAHelper;
import com.thfw.robotheart.view.HomeIpTextView;
import com.thfw.robotheart.view.TitleRobotView;
import com.thfw.ui.dialog.TDialog;
import com.thfw.ui.dialog.base.BindViewHolder;
import com.thfw.ui.voice.PolicyHelper;
import com.thfw.ui.voice.speech.SpeechHelper;
import com.thfw.ui.voice.tts.TtsHelper;
import com.thfw.ui.voice.tts.TtsModel;
import com.thfw.ui.widget.SpeechTextView;
import com.thfw.user.login.UserManager;
import com.trello.rxlifecycle2.LifecycleProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class AiTalkActivity extends RobotBaseActivity<TalkPresenter> implements TalkPresenter.TalkUi<HttpResult<List<DialogTalkModel>>> {


    private static final int FACE_TYPE_FREE = 0; // 空闲
    private static final int FACE_TYPE_SPEECH = 1; // 说话
    private static final int FACE_TYPE_LISTEN = 2; // 倾听
    private static final int FACE_TYPE_TOUCH = 3; // 触摸
    private static String KEY_ROBOT_SPEECH;
    long downTime;
    boolean currentSelect = false;
    private com.thfw.robotheart.view.TitleRobotView mTitleRobotView;
    private androidx.constraintlayout.widget.ConstraintLayout mClAnim;
    private androidx.recyclerview.widget.RecyclerView mRvList;
    private ChatAdapter mChatAdapter;
    private EditText mEtContent;
    private android.widget.RelativeLayout mRlSend;
    private android.widget.RelativeLayout mRlVoice;
    private android.widget.RelativeLayout mRlKeywordInput;
    private TextView mTvSend;
    private android.widget.RelativeLayout mRlVoiceSmall;
    private android.widget.RelativeLayout mRlKeyword;
    private RecyclerView mRvSelect;
    private ChatSelectAdapter mSelectAdapter;
    private Handler mMainHandler = new Handler(Looper.getMainLooper());
    private Helper mHelper = new Helper();
    private LinkedList<TtsModel> mTtsQueue = new LinkedList<>();
    private boolean mTtsFinished = false;
    private boolean mTtsStarted = false;
    // 1、树洞聊天 2、咨询助理
    private int mScene;
    private android.widget.LinearLayout mLlTalkHistory;
    private android.widget.ImageView mIvTalkSwitch;
    private android.widget.LinearLayout mLlVolumeSwitch;
    private android.widget.ImageView mIvVolumeSwitch;
    private int mCurrentChatType;
    private boolean softKeyBoardShow;
    private boolean softKeyBoardShowStvText;
    private ImageView mIvTalkModel;
    private SpeechTextView mStvText;
    private boolean mPauseStvTextShow;
    private boolean ttsPauseIng;
    private com.opensource.svgaplayer.SVGAImageView mSvgaBody;
    private com.opensource.svgaplayer.SVGAImageView mSvgaFace;
    private int mFaceType = -1; // 当前面部表情状态
    private com.thfw.robotheart.view.HomeIpTextView mHitAnim;
    // 失败参数暂存;
    private int mSceneError;
    private NetParams mNetParamsError;
    private long lastTime;

    public static void startActivity(Context context, TalkModel talkModel) {
        context.startActivity(new Intent(context, AiTalkActivity.class).putExtra(KEY_DATA, talkModel));
    }


    @Override
    public int getContentView() {
        return R.layout.activity_ai_talk;
    }

    @Override
    public TalkPresenter onCreatePresenter() {
        return new TalkPresenter(this);
    }

    @Override
    public void initView() {
        KEY_ROBOT_SPEECH = "key.robot_speech" + UserManager.getInstance().getUID();
        mTitleRobotView = (TitleRobotView) findViewById(R.id.titleRobotView);
        mClAnim = (ConstraintLayout) findViewById(R.id.cl_anim);
        mRvList = (RecyclerView) findViewById(R.id.rv_list);
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
        mTvSend = (TextView) findViewById(R.id.tv_send);
        mStvText = (SpeechTextView) findViewById(R.id.stv_text);
        mEtContent = (EditText) findViewById(R.id.et_content);
        mRvList.setLayoutManager(new LinearLayoutManager(mContext));
        softInput();
        mTvSend.setOnClickListener(v -> {
            sendInputText(mEtContent.getText().toString());
        });
        mEtContent.addTextChangedListener(new MyTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTvSend.setEnabled(!TextUtils.isEmpty(s.toString()));
            }
        });
        mRlSend = (RelativeLayout) findViewById(R.id.rl_send);
        mRlVoice = (RelativeLayout) findViewById(R.id.rl_voice);
        mIvTalkModel = (ImageView) findViewById(R.id.iv_talk_model);
        mRlKeywordInput = (RelativeLayout) findViewById(R.id.rl_keyword_input);
        mRlKeyword = (RelativeLayout) findViewById(R.id.rl_keyword);
        mLlTalkHistory = (LinearLayout) findViewById(R.id.ll_talk_history);
        mIvTalkSwitch = (ImageView) findViewById(R.id.iv_talk_switch);
        mLlVolumeSwitch = (LinearLayout) findViewById(R.id.ll_volume_switch);
        mIvVolumeSwitch = (ImageView) findViewById(R.id.iv_volume_switch);
        mLlVolumeSwitch.setOnClickListener(v -> {
            volumeSwitch(true);
        });


        // 历史记录
        mLlTalkHistory.setOnClickListener(v -> {
            // todo 去历史记录页面
            TalkHistoryActivity.startActivity(mContext, mScene);
        });
        mSvgaBody = (SVGAImageView) findViewById(R.id.svga_body);
        mSvgaFace = (SVGAImageView) findViewById(R.id.svga_face);

        // 设置是否打开
        boolean speechOpen = SharePreferenceUtil.getBoolean(KEY_ROBOT_SPEECH, true);
        mIvVolumeSwitch.setSelected(speechOpen);
        LogUtil.d(TAG, "speechOpen = " + speechOpen + " , mIvVolumeSwitch.isSelected() = " + mIvVolumeSwitch.isSelected());
        startAnimFaceType(FACE_TYPE_FREE);
        mSvgaBody.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAnimFaceType(FACE_TYPE_TOUCH);
            }
        });


        mHitAnim = (HomeIpTextView) findViewById(R.id.hit_anim);
    }

    private void volumeSwitch(boolean fromUser) {
        if (fromUser) {
            SharePreferenceUtil.setBoolean(KEY_ROBOT_SPEECH, !mIvVolumeSwitch.isSelected());
        }
        if (!mIvVolumeSwitch.isSelected() && PolicyHelper.getInstance().isSpeechMode()) {
            ToastUtil.show("正在倾听，无法打开");
            return;
        }
        mIvVolumeSwitch.setSelected(!mIvVolumeSwitch.isSelected());
        if (mIvVolumeSwitch.isSelected()) {
            if (mTtsFinished) {
                startAnimFaceType(FACE_TYPE_FREE);
                return;
            }
            if (EmptyUtil.isEmpty(mChatAdapter.getDataList())) {
                startAnimFaceType(FACE_TYPE_FREE);
                return;
            }
            List<ChatEntity> chatEntities = mChatAdapter.getDataList();
            List<ChatEntity> ttsEntitys = new ArrayList<>();
            for (int i = chatEntities.size() - 1; i >= 0; i--) {
                ChatEntity chatEntity = chatEntities.get(i);
                if (chatEntity.isFrom()) {
                    ttsEntitys.add(chatEntity);
                } else {
                    break;
                }
            }
            Collections.reverse(ttsEntitys);
            LogUtil.d(TAG, "ttsEntitys.size = " + ttsEntitys.size());
            for (ChatEntity chatEntity : ttsEntitys) {
                ttsHandle(chatEntity);
            }
        } else {
            startAnimFaceType(FACE_TYPE_FREE);
            TtsHelper.getInstance().stop();
            mTtsQueue.clear();
        }
    }

    private void startAnimFaceType(int type) {
        if (this.mFaceType == type) {
            return;
        }
        if (type == FACE_TYPE_TOUCH) {

            SVGAHelper.playSVGA(mSvgaFace, SVGAHelper.SVGAModel.create(AnimFileName.EMOJI_CHUMO).setLoopCount(1), new DialogRobotFactory.SimpleSVGACallBack() {

                @Override
                public void onFinished() {
                    mFaceType = -1;
                    LogUtil.d(TAG, "onFinished startAnimFaceType");
                    if (TtsHelper.getInstance().isIng()) {
                        startAnimFaceType(FACE_TYPE_SPEECH);
                    } else if (SpeechHelper.getInstance().isIng()) {
                        startAnimFaceType(FACE_TYPE_LISTEN);
                    } else {
                        startAnimFaceType(FACE_TYPE_FREE);
                    }
                }
            });
            return;
        }
        String fileName = null;
        switch (type) {
            case FACE_TYPE_LISTEN:
                fileName = AnimFileName.EMOJI_QINGTING;
                break;
            case FACE_TYPE_SPEECH:
                fileName = AnimFileName.EMOJI_SPEECH;
                break;
            default:
                fileName = AnimFileName.EMOJI_KAIJI;
                break;
        }
        this.mFaceType = type;
        LogUtil.d(TAG, "startAnimFaceType type = " + type + ", fileName" + fileName);
        SVGAHelper.playSVGA(mSvgaFace, SVGAHelper.SVGAModel.create(fileName), null);
    }

    private void sendInputText(String inputText) {
        if (mHelper.getTalkModel() == null) {
            LogUtil.d(TAG, "mHelper.getTalkModel() == null 对话还未开始！！！");
            return;
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

    @Override
    public void initData() {

        TalkModel talkModel = (TalkModel) getIntent().getSerializableExtra(KEY_DATA);
        if (talkModel == null) {
            ToastUtil.show("参数错误");
            finish();
            return;
        }
        mScene = talkModel.getType() == 3 ? 1 : 2;
        mPresenter.onJoinDialog(talkModel.getType(), talkModel.getId());
        mTitleRobotView.setCenterText(talkModel.getTitle());
        mChatAdapter = new ChatAdapter(null);
        mRvList.setAdapter(mChatAdapter);
        mRvList.setOnTouchListener(new View.OnTouchListener() {

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

        mChatAdapter.setRecommendListener((type, recommendInfoBean) -> {
            TalkItemJumpHelper.onItemClick(mContext, type, recommendInfoBean);
        });

        FrameLayout parentContent = findViewById(android.R.id.content);

        parentContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                parentContent.getWindowVisibleDisplayFrame(r);

                int displayHeight = r.bottom - r.top;
                Log.v(TAG, "displayHeight:" + displayHeight);

                int parentHeight = parentContent.getHeight();
                Log.v(TAG, "parentHeight:" + parentHeight);

                int softKeyHeight = parentHeight - displayHeight;
                Log.v(TAG, "softKeyHeight:" + softKeyHeight);
            }
        });

        mTitleRobotView.getIvBack().setOnClickListener(v -> {
            finishService();
        });
        AndroidBug5497Workaround.assistActivity(this);

        SoftKeyBoardListener.setListener(this, new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {
                mRlVoice.setVisibility(View.GONE);
                mRlKeywordInput.setVisibility(View.VISIBLE);
                mRlKeyword.setVisibility(View.GONE);
                softKeyBoardShow = true;
                softKeyBoardShowStvText = mStvText.isShow();
                mStvText.hide();
            }

            @Override
            public void keyBoardHide(int height) {
                softKeyBoardShow = false;
                mRlKeywordInput.setVisibility(View.GONE);
                mRlVoice.setVisibility(View.VISIBLE);
                if (mCurrentChatType == ChatEntity.TYPE_INPUT) {
                    mRlKeyword.setVisibility(View.VISIBLE);
                } else {
                    mRlKeyword.setVisibility(View.GONE);
                }

                if (softKeyBoardShowStvText) {
                    mStvText.show();
                }

            }
        });


        mRlKeyword.setOnClickListener(v -> {
            mRlKeywordInput.setVisibility(View.VISIBLE);
            showInput(mEtContent);
        });
        mIvTalkModel.setOnClickListener(v -> {
            mIvTalkModel.setSelected(!mIvTalkModel.isSelected());
        });

        PolicyHelper.getInstance().setResultListener(new SpeechHelper.ResultListener() {
            boolean showOriginVolume = false;

            @Override
            public void onResult(String result, boolean end) {
                LogUtil.d(TAG, "result =================================== " + result + " ; end = " + end);
                if (!mStvText.isShow()) {
                    return;
                }
                // 未选中，不处理字符
                if (!mIvVolumeSwitch.isSelected()) {
                    return;
                }
                if (PolicyHelper.getInstance().isPressMode()) {
                    mStvText.append(result);
                } else {
                    mStvText.setSpeechText(result);
                }
                if (end && !PolicyHelper.getInstance().isPressMode()) {
                    chooseOption(mStvText.getText(), end);
                }
            }

            @Override
            public void onIng(boolean ing) {
                LogUtil.d(TAG, "ing =================================== " + ing);
//                mStvText.show();
                if (ing) {
                    showOriginVolume = mIvVolumeSwitch.isSelected();
                    if (mIvVolumeSwitch.isSelected()) {
                        volumeSwitch(false);
                    }
                    startAnimFaceType(FACE_TYPE_LISTEN);
                    mStvText.show();
                } else {
                    mStvText.hide();
                    if (showOriginVolume) {
                        if (!mIvVolumeSwitch.isSelected()) {
                            volumeSwitch(false);
                        }
                    } else {
                        startAnimFaceType(FACE_TYPE_FREE);
                    }
                }
            }
        });


        mIvTalkModel.setOnTouchListener(new View.OnTouchListener() {


            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        downTime = System.currentTimeMillis();
                        mIvTalkModel.setSelected(true);
                        if (!SpeechHelper.getInstance().isIng()) {
                            if (!currentSelect) {
                                PolicyHelper.getInstance().startPressed();
                            } else {
                                PolicyHelper.getInstance().startSpeech();
                            }
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (System.currentTimeMillis() - downTime < 300) {
                            currentSelect = !currentSelect;
                            mIvTalkModel.setSelected(currentSelect);
                        } else {
                            mIvTalkModel.setSelected(currentSelect);
                        }
                        if (currentSelect) {
                            PolicyHelper.getInstance().startSpeech();
                        } else {
                            PolicyHelper.getInstance().end();
                            LogUtil.d(TAG, "chooseOption ACTION_UP Pressed End ++++++++++++++++");
                            chooseOption(mStvText.getText(), true);
                            mStvText.setSpeechText("");
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (!currentSelect) {
                            PolicyHelper.getInstance().startPressed();
                        }
                        mIvTalkModel.setSelected(true);
                        LogUtil.d(TAG, "mRlVoice - ing " + SpeechHelper.getInstance().isIng());
                        break;
                }
                LogUtil.d(TAG, "mRlVoice - mIvTalkModel.isSelected() " + mIvTalkModel.isSelected());
                return true;
            }
        });


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
        mStvText.setSpeechText("");
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
                    return AiTalkActivity.this;
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


    /**
     * 【弹框】 结束服务提醒
     */
    private void finishService() {
        DialogRobotFactory.createCustomDialog(this, new DialogRobotFactory.OnViewCallBack() {
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
        mRvList.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {//

            @Override
            public void onLayoutChange(View v, int left, int top, int right,
                                       int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom) {
                    mRvList.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            mRvList.scrollToPosition(mChatAdapter.getItemCount() - 1);
                        }
                    }, 100);
                }
            }
        });
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
        mRvList.smoothScrollToPosition(mChatAdapter.getItemCount() - 1);
    }

    @Override
    public LifecycleProvider getLifecycleProvider() {
        return AiTalkActivity.this;
    }


    @Override
    public void onSuccess(HttpResult<List<DialogTalkModel>> model) {
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
                mTtsQueue.clear();
                onTalkEngine();
            }
        }

    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        finishService();
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

    private void ttsHandle(ChatEntity chatEntity) {
        LogUtil.d(TAG, "ttsHandle.start");
        if (!mIvVolumeSwitch.isSelected()) {
            LogUtil.d(TAG, "ttsHandle.start isSelected false");
            return;
        }

        if (chatEntity.isFrom()) {
            LogUtil.d(TAG, "ttsHandle.start chatEntity.isFrom()");
            TtsModel ttsModel = new TtsModel(chatEntity.getTalk());
            mTtsQueue.add(ttsModel);
            LogUtil.d(TAG, " ttsHandle mTtsQueue.add(ttsModel); text =  " + ttsModel.text + " ; = " + mTtsQueue.size());
            mTtsFinished = false;
            boolean ttsIng = TtsHelper.getInstance().isIng();
            if (ttsIng) {
                if (mTtsStarted) {
                    return;
                }
                TtsHelper.getInstance().setCurrentSynthesizerListener(new TtsHelper.SimpleSynthesizerListener() {
                    @Override
                    public void onIng(boolean ing) {
                        super.onIng(ing);
                        if (!isMeResumed() || EmptyUtil.isEmpty(AiTalkActivity.this)) {
                            return;
                        }
                        LogUtil.d(TAG, "ttsHandle  ing=  " + ing);
                        if (!ing) {
                            ttsHandle();
                        }
                    }
                });

            } else {
                ttsHandle();
            }
        }


    }

    private void ttsHandle() {
        mTtsStarted = true;
        if (mTtsQueue.isEmpty()) {
            return;
        }
        LogUtil.d(TAG, "TtsHelper.getInstance().start begin");
        TtsHelper.getInstance().start(mTtsQueue.poll(), new TtsHelper.SimpleSynthesizerListener() {

            @Override
            public void onSpeakBegin() {
                super.onSpeakBegin();
                startAnimFaceType(FACE_TYPE_SPEECH);
            }

            @Override
            public void onCompleted(SpeechError speechError) {
                super.onCompleted(speechError);
                if (!isMeResumed() || EmptyUtil.isEmpty(AiTalkActivity.this)) {
                    return;
                }
                LogUtil.d(TAG, "TtsHelper.getInstance().start onCompleted");
                if (!mTtsQueue.isEmpty()) {
                    LogUtil.d(TAG, "TtsHelper.getInstance().start continue");
                    TtsHelper.getInstance().start(mTtsQueue.poll(), this);
                    startAnimFaceType(FACE_TYPE_SPEECH);
                } else {
                    mTtsFinished = true;
                    startAnimFaceType(FACE_TYPE_FREE);
                }
            }
        });
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
        ttsHandle(chatEntity);
        if (mCurrentChatType == ChatEntity.TYPE_FROM_SELECT) {
            hideInput();
            mRvSelect.setVisibility(View.VISIBLE);
            mRlKeyword.setVisibility(View.GONE);
            if (mSelectAdapter == null) {
                mSelectAdapter = new ChatSelectAdapter(talkModel.getCheckRadio());
                mRvSelect.setAdapter(mSelectAdapter);
            } else {
                mSelectAdapter.setDataListNotify(talkModel.getCheckRadio());
            }
            setSelectItemListener(chatEntity);
        } else if (mCurrentChatType == ChatEntity.TYPE_INPUT) {
            if (!softKeyBoardShow) {
                mRlKeyword.setVisibility(View.VISIBLE);
            }
            mRvSelect.setVisibility(View.GONE);
        } else {
            hideInput();
            mRlKeyword.setVisibility(View.GONE);
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
            mScene = talkModel.getScene(mScene);
            if (mScene == 1) {
                mTitleRobotView.setCenterText("倾诉吐槽");
                sendData(ChatEntity.createJoinPage("欢迎进入倾诉吐槽"));
            } else {
                mTitleRobotView.setCenterText("主题对话");
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
                            switch (type) {
                                case PageJumpUtils.JUMP_TEXT:
                                    startActivityForResult(new Intent(mContext, TestActivity.class), type);
                                    break;
                                case PageJumpUtils.JUMP_MUSIC:
                                    startActivityForResult(new Intent(mContext, AudioHomeActivity.class), type);
                                    break;
                                case PageJumpUtils.JUMP_AI_HOME:
                                    startActivityForResult(new Intent(mContext, ThemeTalkActivity.class), type);
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
        ToastUtil.show("onFail = " + throwable.getMessage());

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
        mPauseStvTextShow = mStvText.isShow();
        if (mPauseStvTextShow) {
            mStvText.hide();
            PolicyHelper.getInstance().end();
        }

        ttsPauseIng = TtsHelper.getInstance().isIng();
        if (ttsPauseIng) {
            TtsHelper.getInstance().pause();
        }
        mHitAnim.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mPauseStvTextShow) {
            mStvText.show();
            if (currentSelect) {
                PolicyHelper.getInstance().startSpeech();
            } else {
                PolicyHelper.getInstance().end();
            }
        }

        if (ttsPauseIng) {
            TtsHelper.getInstance().resume();
        }
        mHitAnim.resume();
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

}