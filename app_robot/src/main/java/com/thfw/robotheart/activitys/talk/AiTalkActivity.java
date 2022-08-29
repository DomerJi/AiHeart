package com.thfw.robotheart.activitys.talk;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.iflytek.cloud.SpeechError;
import com.opensource.svgaplayer.SVGAImageView;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.thfw.base.api.MusicApi;
import com.thfw.base.face.MyTextWatcher;
import com.thfw.base.face.OnRvItemListener;
import com.thfw.base.models.AudioEtcDetailModel;
import com.thfw.base.models.BaikeModel;
import com.thfw.base.models.ChatEntity;
import com.thfw.base.models.ChosenModel;
import com.thfw.base.models.DialogTalkModel;
import com.thfw.base.models.MusicModel;
import com.thfw.base.models.TalkModel;
import com.thfw.base.net.HttpResult;
import com.thfw.base.net.NetParams;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.TalkPresenter;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.base.utils.GsonUtil;
import com.thfw.base.utils.HourUtil;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.RegularUtil;
import com.thfw.base.utils.SharePreferenceUtil;
import com.thfw.base.utils.StringUtil;
import com.thfw.base.utils.ToastUtil;
import com.thfw.base.utils.Util;
import com.thfw.robotheart.R;
import com.thfw.robotheart.activitys.RobotBaseActivity;
import com.thfw.robotheart.activitys.audio.AudioHomeActivity;
import com.thfw.robotheart.activitys.audio.AudioPlayerActivity;
import com.thfw.robotheart.activitys.test.TestActivity;
import com.thfw.robotheart.adapter.ChatAdapter;
import com.thfw.robotheart.adapter.ChatSelectAdapter;
import com.thfw.robotheart.constants.AnimFileName;
import com.thfw.robotheart.port.ActionParams;
import com.thfw.robotheart.port.SerialManager;
import com.thfw.robotheart.robot.RobotUtil;
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
import com.thfw.ui.widget.LoadingView;
import com.thfw.ui.widget.SpeechTextView;
import com.thfw.ui.widget.SpeedLinearLayoutManager;
import com.thfw.user.login.UserManager;
import com.trello.rxlifecycle2.LifecycleProvider;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class AiTalkActivity extends RobotBaseActivity<TalkPresenter> implements TalkPresenter.TalkUi<HttpResult<List<DialogTalkModel>>> {


    private static final int FACE_TYPE_FREE = 0; // 空闲
    private static final int FACE_TYPE_SPEECH = 1; // 说话
    private static final int FACE_TYPE_LISTEN = 2; // 倾听
    private static final int FACE_TYPE_TOUCH = 3; // 触摸
    // 非倾听模式下
    private static String KEY_ROBOT_SPEECH;
    // 倾听模式下
    private static String KEY_ROBOT_SPEECH_READ;
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
    private int mOriginScene;
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
    private LoadingView mLoadingView;
    private RefreshLayout mRefreshLayout;
    private DialogTalkModel mFirstTalkModel;
    private TalkModel talkModel;
    private boolean handleResult;
    // 读完再说
    private TextView mTvVolumeSwitch;
    private boolean mSpeechMode;
    private boolean mReadAfterSpeech;

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
        KEY_ROBOT_SPEECH_READ = "key.robot_speech_read" + UserManager.getInstance().getUID();
        mTitleRobotView = (TitleRobotView) findViewById(R.id.titleRobotView);
        mClAnim = (ConstraintLayout) findViewById(R.id.cl_anim);
        mLoadingView = findViewById(R.id.loadingView);
        mTvVolumeSwitch = findViewById(R.id.tv_volume_switch);
        mRefreshLayout = findViewById(R.id.refreshLayout);
        mRefreshLayout.setEnableRefresh(false);
        mRefreshLayout.setEnableLoadMore(false);
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull @NotNull RefreshLayout refreshLayout) {
                onPreMessage();
            }
        });

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
        mRvList.setLayoutManager(new SpeedLinearLayoutManager(mContext));
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

        mReadAfterSpeech = SharePreferenceUtil.getBoolean(KEY_ROBOT_SPEECH_READ, false);
        PolicyHelper.getInstance().setSwitchReadAfter(mReadAfterSpeech);

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


        FrameLayout frameLayout = findViewById(R.id.fl_select);
        ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) frameLayout.getLayoutParams();
        lp.matchConstraintMaxWidth = Util.getScreenWidth(mContext) / 4 * 3;
        frameLayout.setLayoutParams(lp);
    }

    /**
     * 历史记录查询
     */
    private void onPreMessage() {
        if (mFirstTalkModel != null) {
            new TalkPresenter<List<DialogTalkModel>>(new TalkPresenter.TalkUi<List<DialogTalkModel>>() {
                @Override
                public LifecycleProvider getLifecycleProvider() {
                    return AiTalkActivity.this;
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

    private void volumeSwitch(boolean fromUser) {
        LogUtil.i(TAG, "fromUser -----------------------------------------------------");
        // 用户点击取反
        if (fromUser) {
            mIvVolumeSwitch.setSelected(!mIvVolumeSwitch.isSelected());
            if (mIvTalkModel.isSelected() && mSpeechMode) {
                SharePreferenceUtil.setBoolean(KEY_ROBOT_SPEECH_READ, mIvVolumeSwitch.isSelected());
            } else {
                SharePreferenceUtil.setBoolean(KEY_ROBOT_SPEECH, mIvVolumeSwitch.isSelected());
            }
        } else {
            if (mIvTalkModel.isSelected() && mSpeechMode) {
                mIvVolumeSwitch.setSelected(SharePreferenceUtil.getBoolean(KEY_ROBOT_SPEECH_READ, true));
            } else {
                mIvVolumeSwitch.setSelected(SharePreferenceUtil.getBoolean(KEY_ROBOT_SPEECH, true));
            }
        }

        if (!mIvVolumeSwitch.isSelected()) {
            LogUtil.i("TtsHelper.getInstance().stop");
            mTtsQueue.clear();
            TtsHelper.getInstance().stop();
            startAnimFaceType(FACE_TYPE_FREE);
        }
        if (mIvTalkModel.isSelected() && mSpeechMode) {
            mReadAfterSpeech = mIvVolumeSwitch.isSelected();
            PolicyHelper.getInstance().setSwitchReadAfter(mReadAfterSpeech);
        }

        readAfterSpeech(fromUser);
    }

    /**
     * 是否开启了语音和倾听交互模式
     *
     * @return
     */
    private boolean isReadAfterSpeech() {
        return mIvTalkModel.isSelected() && mReadAfterSpeech;
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
        mStvText.setSpeechText("");
        if (mHelper.getTalkModel() == null) {
            LogUtil.d(TAG, "mHelper.getTalkModel() == null 对话还未开始！！！");
            return;
        }

        if (mScene == 1 && inputText.length() < 35) {
            if (checkAbility(inputText)) {
                return;
            }
        }

        sendData(inputText, true);

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

        if (isReadAfterSpeech()) {
            PolicyHelper.getInstance().setRequestIng(true);
        }
        readAfterSpeech();
        onDialog(mScene, netParams);
        if (sendTo) {
            sendData(chatEntity);
        }
        mEtContent.setText("");
    }

    private boolean checkAbility(String inputText) {
        String tempText = inputText;
        tempText = tempText.replaceAll("(小密|，|。|！|!|/?|？)", "");
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
        // 跳个舞
        if (checkDance(tempText)) {
            hideInput();
            mEtContent.setText("");
            LogUtil.i(TAG, "----------- speech dance -------------");
            return true;
        }
        // 唱个歌曲
        if (checkMusic(tempText)) {
            hideInput();
            mEtContent.setText("");
            LogUtil.i(TAG, "----------- search music -------------");
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
            if (EmptyUtil.isEmpty(AiTalkActivity.this)) {
                return;
            }
            DialogTalkModel talkModel = new DialogTalkModel();
            talkModel.setType(ChatEntity.TYPE_FROM_NORMAL);
            talkModel.setQuestion(question);
            onTalkData(talkModel);
        }, 1200);
    }

    private boolean checkTureName(String inputText) {
        if (mScene != 1) {
            return false;
        }
        String surname = mContext.getResources().getString(R.string.surname);
        if (RegularUtil.isTrueName(inputText) && surname.contains(inputText.substring(0, surname.length() == 4 ? 2 : 1))) {

            MusicApi.requestBaiKe(inputText, new MusicApi.BaiKeCallback() {
                @Override
                public void onFailure(int code, String msg) {
                    LogUtil.e(TAG, "code = " + code + " ; msg = " + msg);
                    sendData(inputText, false);
                }

                @Override
                public void onResponse(BaikeModel baikeModel) {
                    if (EmptyUtil.isEmpty(AiTalkActivity.this)) {
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

    private boolean checkMusic(String inputText) {
        if (mScene != 1) {
            return false;
        }
        String tempText = inputText;
        // 相关冥想不作音乐处理
        if (inputText.contains("冥想")) {
            return false;
        }
//        String REGEX_MUSIC = ".{0,5}(播放|推荐|放|唱|听|来)(一个|一首|一曲|个|首).{0,20}";
        String REGEX_MUSIC = ".{0,5}(播放|放|唱|听)(一个|一首|一曲|个|首|).{0,20}";
        String REGEX_MUSIC2 = ".{0,5}(推荐|来)(一首|一曲|首).{0,20}";
        String REGEX_MUSIC3 = ".{0,5}(推荐|来)(一个|一首|一曲|个|首|).{0,20}(的音乐|的歌曲|歌儿|歌吧|的歌|歌曲|音乐|曲儿|个歌|music)(吧|嘛|)";
        if (tempText.matches(REGEX_MUSIC)
                || tempText.matches(REGEX_MUSIC2)
                || tempText.matches(REGEX_MUSIC3)) {
            String REGEX_MUSIC_REPLACE = ".{0,5}(播放|推荐|放|唱|听|来)(一个|一首|一曲|个|首|)" +
                    "|(的音乐|的歌曲|歌儿|歌吧|的歌|歌曲|音乐|曲儿|个歌|music)(吧|嘛|)";

            String name = tempText.replaceAll(REGEX_MUSIC_REPLACE, "");
            boolean notEmptyName = !TextUtils.isEmpty(name);
            String ttsHint = null;
            boolean recommend = false;
            if (!notEmptyName || "歌".equals(name)) {
                recommend = true;
                name = RegularUtil.getRandomPeople();
                ttsHint = "为您推荐 " + name + " 的音乐";
            } else {
                recommend = RegularUtil.contains(name);
                ttsHint = "即将为您播放 " + (recommend ? (name + " 的音乐") : name);
            }
            ToastUtil.show(ttsHint);
            final boolean recommendFinal = recommend;
            MusicApi.request(name, new MusicApi.MusicCallback() {
                @Override
                public void onFailure(int code, String msg) {
                    LogUtil.e(TAG, "code = " + code + " ; msg = " + msg);
                    ToastUtil.show("没有找到您想要的音乐");
                }

                @Override
                public void onResponse(List<MusicModel> list) {
                    if (EmptyUtil.isEmpty(AiTalkActivity.this)) {
                        return;
                    }
                    runOnUiThread(() -> {
                        MusicModel musicModel;
                        // 推荐避免重复随机
                        if (recommendFinal) {
                            musicModel = list.get(new Random().nextInt(list.size()));
                        } else {
                            musicModel = list.get(0);
                        }
                        AudioEtcDetailModel.AudioItemModel audioItemModel = musicModel.toAudioItemModel();
                        audioItemModel.mp3WaitList = list;
                        audioItemModel.mp3WaitList.remove(musicModel);
                        AudioPlayerActivity.startActivity(mContext, audioItemModel);
                    });

                }
            });
            return true;
        } else {
            return false;
        }

    }

    private boolean checkDance(String inputText) {
        if (RobotUtil.isInstallRobot() && mScene == 1) {
            RobotUtil.Dance dance = RobotUtil.onDance(inputText);
            if (dance.type == RobotUtil.Dance.NORMAL) {
                return false;
            }
            LogUtil.i(TAG, "dance -> " + dance.toString());
            TtsHelper.getInstance().start(new TtsModel(dance.tts), null);
            switch (dance.type) {
                case RobotUtil.Dance.ALL:
                    SerialManager.getInstance().startAllAction();
                    return true;
                case RobotUtil.Dance.SHAKE:
                    SerialManager.getInstance().startAction(ActionParams.getNormalShake());
                    return true;
                case RobotUtil.Dance.NOD:
                    SerialManager.getInstance().startAction(ActionParams.getNormalNod());
                    return true;
                case RobotUtil.Dance.ROTATE:
                    SerialManager.getInstance().startAction(ActionParams.getNormalRotate());
                    return true;
            }
        }
        return false;
    }


    @Override
    public void initData() {

        talkModel = (TalkModel) getIntent().getSerializableExtra(KEY_DATA);
        if (talkModel == null) {
            ToastUtil.show("参数错误");
            finish();
            return;
        }
        mScene = talkModel.getType() == 3 ? 1 : 2;
        mOriginScene = mScene;
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

        mChatAdapter.setRecommendListener((type, recommendInfoBean, position) -> {
            handleResult = mChatAdapter.getItemCount() < position + 5;
            TalkItemJumpHelper.onItemClick(mContext, type, recommendInfoBean);
        });

        mTitleRobotView.getIvBack().setOnClickListener(v -> {
            finishService();
        });

        AndroidBug5497Workaround.assistActivity(this);
        SoftKeyBoardListener.setListener(this, new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {
                LogUtil.d(TAG, "SoftKeyBoardListener keyBoardShow");
                mRlVoice.setVisibility(View.GONE);
                mRlKeywordInput.setVisibility(View.VISIBLE);
                mRlKeyword.setVisibility(View.GONE);
                softKeyBoardShow = true;
                softKeyBoardShowStvText = mStvText.isShow();
                mStvText.hide();
            }

            @Override
            public void keyBoardHide(int height) {
                LogUtil.d(TAG, "SoftKeyBoardListener keyBoardHide");
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
            // 退出倾听模式
            if (mIvTalkModel.isSelected() || mStvText.isShow()) {
                PolicyHelper.getInstance().end();
                mIvTalkModel.setSelected(false);
                mStvText.hide();
                currentSelect = false;
                onTalkModel(false);
            }
            mRlKeywordInput.setVisibility(View.VISIBLE);
            mRlKeyword.setVisibility(View.GONE);
            showInput(mEtContent);
        });
        PolicyHelper.getInstance().setResultListener(new SpeechHelper.ResultListener() {

            @Override
            public void onResult(String result, boolean append, boolean end) {
                if (!mStvText.isShow()) {
                    return;
                }
                // 未选中，不处理字符
                if (!mIvTalkModel.isSelected()) {
                    return;
                }
                mStvText.setSpeechText(result);
                if (end && !PolicyHelper.getInstance().isPressMode()) {
                    chooseOption(mStvText.getText(), end);
                }
            }

            @Override
            public void onIng(boolean ing) {
                LogUtil.d(TAG, "ing =================================== " + ing);
                if (ing) {
                    startAnimFaceType(FACE_TYPE_LISTEN);
                    mStvText.show();
                } else {
                    // 播完再听 播放tts不消失
                    if (!isReadAfterSpeech()) {
                        if (!(PolicyHelper.getInstance().isSpeechMode()
                                || PolicyHelper.getInstance().isPressMode())) {
                            mStvText.hide();
                        }
                    }
                    startAnimFaceType(FACE_TYPE_FREE);
                }
            }
        });


        mIvTalkModel.setOnTouchListener(new View.OnTouchListener() {

            Runnable touchRunnable;

            private void onTouchDelay() {
                final View.OnTouchListener onTouchListener = this;
                mIvTalkModel.setOnTouchListener(null);
                if (touchRunnable != null) {
                    mIvTalkModel.removeCallbacks(touchRunnable);
                }
                touchRunnable = () -> {
                    mIvTalkModel.setOnTouchListener(onTouchListener);
                    touchRunnable = null;
                };
                mIvTalkModel.postDelayed(touchRunnable, 500);
            }

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // 防止点击过块
                        if (System.currentTimeMillis() - downTime < 500) {
                            onTouchDelay();
                            return true;
                        }
                        downTime = System.currentTimeMillis();
                        mIvTalkModel.setSelected(true);
                        // 开始倾听
                        mTtsQueue.clear();
                        TtsHelper.getInstance().stop();
                        startAnimFaceType(FACE_TYPE_LISTEN);
                        mStvText.show();
                        if (mStvText != null) {
                            mStvText.setSpeechTextHint("倾听中···");
                        }
                        if (!currentSelect) {
                            PolicyHelper.getInstance().startPressed();
                        } else {
                            PolicyHelper.getInstance().startSpeech();
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (System.currentTimeMillis() - downTime < 300) {
                            currentSelect = !currentSelect;
                        }
                        if (currentSelect) {
                            PolicyHelper.getInstance().startSpeech();
                            if (mStvText != null) {
                                mStvText.setSpeechTextHint("倾听中···");
                            }
                        } else {
                            LogUtil.d(TAG, "chooseOption ACTION_UP Pressed End ++++++++++++++++");
                            PolicyHelper.getInstance().end();
                            chooseOption(mStvText.getText(), true);
                            mStvText.hide();
                        }
                        onTalkModel(currentSelect);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (!currentSelect) {
                            PolicyHelper.getInstance().startPressed();
                            LogUtil.d(TAG, "mRlVoice - ing " + SpeechHelper.getInstance().isIng());
                        }
                        mIvTalkModel.setSelected(true);
                        break;
                }
                LogUtil.d(TAG, "mRlVoice - mIvTalkModel.isSelected() " + mIvTalkModel.isSelected());
                return true;
            }
        });


    }

    private void onTalkModel(boolean selected) {
        if (mIvTalkModel.isSelected() != selected) {
            mIvTalkModel.setSelected(selected);
        }
        if (mIvTalkModel.isSelected() && PolicyHelper.getInstance().isSpeechMode()) {
            mIvVolumeSwitch.setColorFilter(getResources().getColor(R.color.text_yellow));
            mTvVolumeSwitch.setTextColor(getResources().getColor(R.color.text_yellow));
            mSpeechMode = true;
        } else {
            mIvVolumeSwitch.setColorFilter(getResources().getColor(R.color.colorRobotFore));
            mTvVolumeSwitch.setTextColor(getResources().getColor(R.color.colorRobotFore));
            mSpeechMode = false;
        }
        volumeSwitch(false);
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
        PolicyHelper.getInstance().setRequestIng(false);
        if (!mLoadingView.isHide()) {
            mLoadingView.hide();
            mRefreshLayout.setEnableRefresh(true);
        }
        if (model != null) {
            List<DialogTalkModel> data = model.getData();
            mTtsQueue.clear();
            if (model.getExt() != null) {
                if (model.getExt().isEnterDeepDialog()) {
                    sendData(ChatEntity.createJoinPage("欢迎进入深度疏导主题对话"));
                }
                // 表情处理
                emojiHandle(model);
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
            } else {
                readAfterSpeech();
            }
        } else {
            readAfterSpeech();
        }

    }

    /**
     * 添加表情到adapter
     * 机器人播报文案和动作
     *
     * @param model
     */
    private void emojiHandle(HttpResult<List<DialogTalkModel>> model) {
        if (model.getExt().isSentiment()) {
            String sentiment = model.getExt().getSentiment();
            sendData(ChatEntity.createEmoji(sentiment));
            String sentimentEmoji = AnimFileName.getTalkEmojiBySentiment(sentiment);
            if (!TextUtils.isEmpty(sentimentEmoji)) {
                AnimFileName.RobotOutPutInfo outPutInfo = AnimFileName.getRobotOutPutInfo(sentimentEmoji);
                if (outPutInfo != null) {
                    LogUtil.i(TAG, "outPutInfo -> " + outPutInfo.toString());
                    if (outPutInfo.actionParams != null) {
                        SerialManager.getInstance().startAction(outPutInfo.actionParams);
                    }
                    if (outPutInfo.tts != null) {
                        TtsHelper.getInstance().stop();
                        mTtsQueue.add(new TtsModel(outPutInfo.tts));
                    }
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        finishService();
    }

    private void readAfterSpeech() {
        readAfterSpeech(false);
    }

    private void readAfterSpeech(boolean formUser) {
        // 开启了语音和倾听交互模式
        if (isReadAfterSpeech()) {
            mStvText.show();
            if (PolicyHelper.getInstance().isRequestIng()) {
                PolicyHelper.getInstance().end();
                if (mStvText != null) {
                    mStvText.setSpeechTextHint("···");
                }
            } else if (mFaceType == FACE_TYPE_SPEECH || TtsHelper.getInstance().isIng()) {
                PolicyHelper.getInstance().end();
                if (mStvText != null) {
                    mStvText.setSpeechTextHint("播报中···");
                }
            } else {
                PolicyHelper.getInstance().startSpeech();
                if (mStvText != null) {
                    mStvText.setSpeechTextHint("倾听中···");
                }
            }
        } else {
            // 用户点击判断是否开始和结束录音
            if (formUser) {
                if (mIvTalkModel.isSelected()) {
                    mTtsQueue.clear();
                    TtsHelper.getInstance().stop();
                    PolicyHelper.getInstance().startSpeech();
                } else {
                    PolicyHelper.getInstance().end();
                }
            } else {
                // 松开后 停止录音
                if (!mIvTalkModel.isSelected() || !mSpeechMode) {
                    PolicyHelper.getInstance().end();
                }
            }
            if (mStvText != null) {
                mStvText.setSpeechTextHint("倾听中···");
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

    private void ttsHandle(ChatEntity chatEntity) {
        LogUtil.d(TAG, "ttsHandle.start");
        if (!isReadAfterSpeech() && (!mIvVolumeSwitch.isSelected() || mIvTalkModel.isSelected())) {
            LogUtil.d(TAG, "ttsHandle.start isSelected false");
            return;
        }

        if (chatEntity.isFrom()) {
            LogUtil.d(TAG, "ttsHandle.start chatEntity.isFrom()");
            TtsModel ttsModel = new TtsModel(chatEntity.getTalk());
            mTtsQueue.add(ttsModel.setCache(false));
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
                        LogUtil.d(TAG, "ttsHandle  ing =  " + ing);
                        if (!ing) {
                            ttsHandle();
                        }
                    }
                });

            } else {
                LogUtil.d(TAG, " ttsHandle mTtsQueue.add(ttsModel);");
                ttsHandle();
            }
        }


    }

    private void ttsHandle() {
        mTtsStarted = true;
        if (mTtsQueue.isEmpty()) {
            LogUtil.d(TAG, " ttsHandle mTtsQueue.add(ttsModel);mTtsQueue.isEmpty()");
            mTtsFinished = true;
            return;
        }
        LogUtil.d(TAG, "ttsHandle TtsHelper.getInstance().start begin");
        startAnimFaceType(FACE_TYPE_SPEECH);
        readAfterSpeech();
        TtsHelper.getInstance().start(mTtsQueue.poll(), new TtsHelper.SimpleSynthesizerListener() {

            @Override
            public void onSpeakBegin() {
                super.onSpeakBegin();
                startAnimFaceType(FACE_TYPE_SPEECH);
                readAfterSpeech();
            }

            @Override
            public void onCompleted(SpeechError speechError) {
                super.onCompleted(speechError);
                if (!isMeResumed() || EmptyUtil.isEmpty(AiTalkActivity.this)) {
                    return;
                }
                LogUtil.d(TAG, "ttsHandle TtsHelper.getInstance().start onCompleted");
                if (!mTtsQueue.isEmpty()) {
                    LogUtil.d(TAG, "ttsHandle TtsHelper.getInstance().start continue");
                    TtsHelper.getInstance().start(mTtsQueue.poll(), this);
                    startAnimFaceType(FACE_TYPE_SPEECH);
                } else {
                    mTtsFinished = true;
                    startAnimFaceType(FACE_TYPE_FREE);
                    readAfterSpeech();
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
        ttsHandle(chatEntity);
        if (mCurrentChatType == ChatEntity.TYPE_FROM_SELECT) {
            hideInput();
            mRvSelect.setVisibility(View.VISIBLE);
            mRlKeyword.setVisibility(View.GONE);
            mRlKeywordInput.setVisibility(View.GONE);
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
                mRlKeywordInput.setVisibility(View.GONE);
            } else {
                mRlKeywordInput.setVisibility(View.VISIBLE);
                mRlKeyword.setVisibility(View.GONE);
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
                            handleResult = true;
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
        LogUtil.d(TAG, "onFail = " + throwable.toString());
        PolicyHelper.getInstance().setRequestIng(false);
        readAfterSpeech();
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
    protected void onActivityResult(int requestCode, int resultCode,
                                    @Nullable @org.jetbrains.annotations.Nullable Intent data) {
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
        mPauseStvTextShow = mStvText.isShow();
        if (mPauseStvTextShow) {
            mStvText.hide();
            PolicyHelper.getInstance().end();
        }

        ttsPauseIng = TtsHelper.getInstance().isIng();
        if (ttsPauseIng) {
            mTtsQueue.clear();
            TtsHelper.getInstance().stop();
        }
        mHitAnim.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isMeResumed2()) {
            if (mPauseStvTextShow) {
                if (currentSelect) {
                    mStvText.show();
                    PolicyHelper.getInstance().startSpeech();
                    startAnimFaceType(FACE_TYPE_LISTEN);
                    if (mStvText != null) {
                        mStvText.setSpeechTextHint("倾听中···");
                    }
                } else {
                    mStvText.hide();
                    PolicyHelper.getInstance().end();
                }
            } else {
                mStvText.hide();
                PolicyHelper.getInstance().end();
            }
            readAfterSpeech();
        }
        mHitAnim.resume();
    }


    @Override
    public void onDestroy() {
        PolicyHelper.getInstance().end();
        TtsHelper.getInstance().stop();
        TtsHelper.getInstance().setCurrentSynthesizerListener(null);
        super.onDestroy();
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