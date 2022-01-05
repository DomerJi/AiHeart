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
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.base.face.MyTextWatcher;
import com.thfw.base.face.OnRvItemListener;
import com.thfw.base.models.AudioEtcDetailModel;
import com.thfw.base.models.AudioEtcModel;
import com.thfw.base.models.ChatEntity;
import com.thfw.base.models.DialogTalkModel;
import com.thfw.base.models.TalkModel;
import com.thfw.base.models.VideoEtcModel;
import com.thfw.base.net.NetParams;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.TalkPresenter;
import com.thfw.base.utils.HourUtil;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.ToastUtil;
import com.thfw.robotheart.R;
import com.thfw.robotheart.adapter.ChatSelectAdapter;
import com.thfw.robotheart.activitys.audio.AudioHomeActivity;
import com.thfw.robotheart.activitys.audio.AudioPlayerActivity;
import com.thfw.robotheart.activitys.test.TestDetailActivity;
import com.thfw.robotheart.activitys.text.BookActivity;
import com.thfw.robotheart.activitys.text.BookDetailActivity;
import com.thfw.robotheart.activitys.video.VideoPlayerActivity;
import com.thfw.robotheart.adapter.ChatAdapter;
import com.thfw.robotheart.util.PageJumpUtils;
import com.thfw.robotheart.view.TitleRobotView;
import com.thfw.ui.base.RobotBaseActivity;
import com.thfw.ui.dialog.DialogFactory;
import com.thfw.ui.dialog.TDialog;
import com.thfw.ui.dialog.base.BindViewHolder;
import com.trello.rxlifecycle2.LifecycleProvider;

import java.util.ArrayList;
import java.util.List;

public class AiTalkActivity extends RobotBaseActivity<TalkPresenter> implements TalkPresenter.TalkUi<List<DialogTalkModel>> {


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
    // 1、树洞聊天 2、咨询助理
    private int mScene;

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

        mTitleRobotView = (TitleRobotView) findViewById(R.id.titleRobotView);
        mClAnim = (ConstraintLayout) findViewById(R.id.cl_anim);
        mRvList = (RecyclerView) findViewById(R.id.rv_list);
        mRvSelect = (RecyclerView) findViewById(R.id.rv_select);
        mTvSend = (TextView) findViewById(R.id.tv_send);
        mEtContent = (EditText) findViewById(R.id.et_content);
        mRvList.setLayoutManager(new LinearLayoutManager(mContext));
        mRvSelect.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, true));
        softInput();
        mTvSend.setOnClickListener(v -> {
            if (mHelper.getTalkModel() == null) {
                LogUtil.d(TAG, "mHelper.getTalkModel() == null 对话还未开始！！！");
                return;
            }
            ChatEntity chatEntity = new ChatEntity();
            chatEntity.type = ChatEntity.TYPE_TO;
            chatEntity.talk = mEtContent.getText().toString();


            NetParams netParams = NetParams.crete().add("id", mHelper.getTalkModel().getId())
                    .add("question", chatEntity.talk);
            mPresenter.onDialog(mScene, netParams);

            sendData(chatEntity);
            mEtContent.setText("");
        });
        mEtContent.addTextChangedListener(new MyTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTvSend.setEnabled(!TextUtils.isEmpty(s.toString()));
            }
        });
        mRlSend = (RelativeLayout) findViewById(R.id.rl_send);
        mRlVoice = (RelativeLayout) findViewById(R.id.rl_voice);
        mRlKeywordInput = (RelativeLayout) findViewById(R.id.rl_keyword_input);
        mRlKeyword = (RelativeLayout) findViewById(R.id.rl_keyword);
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
            switch (type) {
                case ChatEntity.TYPE_RECOMMEND_TEXT:
                    BookDetailActivity.startActivity(mContext, recommendInfoBean.getId());
                    break;
                case ChatEntity.TYPE_RECOMMEND_VIDEO:
                    ArrayList<VideoEtcModel> videoList = new ArrayList<>();
                    VideoEtcModel videoEtcModel = new VideoEtcModel();
                    videoEtcModel.setId(recommendInfoBean.getId());
                    videoEtcModel.setTitle(recommendInfoBean.getTitle());
                    videoList.add(videoEtcModel);
                    VideoPlayerActivity.startActivity(mContext, videoList, 0);
                    break;
                case ChatEntity.TYPE_RECOMMEND_AUDIO:
                    AudioEtcDetailModel.AudioItemModel audioItemModel = new AudioEtcDetailModel.AudioItemModel();
                    audioItemModel.setId(recommendInfoBean.getId());
                    audioItemModel.setMusicId(recommendInfoBean.getId());
                    audioItemModel.setSfile(recommendInfoBean.getFile());
                    audioItemModel.setImg(recommendInfoBean.getImg());
                    audioItemModel.setTitle(recommendInfoBean.getTitle());
                    AudioPlayerActivity.startActivity(mContext, audioItemModel);
                    break;
                case ChatEntity.TYPE_RECOMMEND_AUDIO_ETC:
                    AudioEtcModel audioEtcModel = new AudioEtcModel();
                    audioEtcModel.setTitle(recommendInfoBean.getTitle());
                    audioEtcModel.setImg(recommendInfoBean.getImg());
                    audioEtcModel.setId(recommendInfoBean.getId());
                    AudioPlayerActivity.startActivity(mContext, audioEtcModel);
                    break;
                case ChatEntity.TYPE_RECOMMEND_TEST:
                    TestDetailActivity.startActivity(mContext, recommendInfoBean.getId());
                    break;
                default:
                    ToastUtil.show("未处理该类型跳转 ->" + type);
                    break;
            }
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
                mRlKeyword.setVisibility(View.GONE);
                mRlVoice.setVisibility(View.GONE);
                mRlKeywordInput.setVisibility(View.VISIBLE);

            }

            @Override
            public void keyBoardHide(int height) {
                mRlKeywordInput.setVisibility(View.GONE);
                mRlKeyword.setVisibility(View.VISIBLE);
                mRlVoice.setVisibility(View.VISIBLE);
            }
        });


        mRlKeyword.setOnClickListener(v -> {
            mRlKeywordInput.setVisibility(View.VISIBLE);
            showInput(mEtContent);
        });

    }


    /**
     * 【弹框】 结束服务提醒
     */
    private void finishService() {
        DialogFactory.createCustomDialog(this, new DialogFactory.OnViewCallBack() {
            @Override
            public void callBack(TextView mTvTitle, TextView mTvHint, TextView mTvLeft, TextView mTvRight, View mVLineVertical) {
                mTvTitle.setText(R.string.finishServiceTitle);
                mTvHint.setText(R.string.finishServiceHint);
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
            if (lastChatEntity != null) {
                long limitTime = chatEntity.time - lastChatEntity.time;
                LogUtil.d(TAG, "sendData -> limitTime = " + limitTime);
                if (limitTime > HourUtil.LEN_MINUTE) {
                    mChatAdapter.addData(ChatEntity.createTime());
                    mChatAdapter.notifyItemInserted(mChatAdapter.getItemCount() - 1);
                    addTime = true;
                    if (lastChatEntity.type == ChatEntity.TYPE_TO && mChatAdapter.getItemCount() > 2) {
                        mChatAdapter.notifyItemChanged(mChatAdapter.getItemCount() - 2);
                    }
                }
            }

        }
        mChatAdapter.addData(chatEntity);
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
    public void onSuccess(List<DialogTalkModel> data) {
        mHelper.setTalks(data);
        onTalkEngine();
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

    private void onTalkData(DialogTalkModel talkModel) {
        Log.d(TAG, talkModel != null ? talkModel.toString() : "talkModel is null");
        if (talkModel == null) {
            return;
        }
        mScene = talkModel.getScene(mScene);
        final ChatEntity chatEntity = new ChatEntity(talkModel);
        sendData(chatEntity);
        if (chatEntity.type == ChatEntity.TYPE_FROM_SELECT) {
            if (mSelectAdapter == null) {
                mSelectAdapter = new ChatSelectAdapter(talkModel.getCheckRadio());
                mRvSelect.setAdapter(mSelectAdapter);
            } else {
                mSelectAdapter.setDataListNotify(talkModel.getCheckRadio());
            }
            mSelectAdapter.setOnRvItemListener(new OnRvItemListener<DialogTalkModel.CheckRadioBean>() {
                @Override
                public void onItemClick(List<DialogTalkModel.CheckRadioBean> list, int position) {
                    DialogTalkModel.CheckRadioBean radioBean = list.get(position);

                    sendData(new ChatEntity(ChatEntity.TYPE_TO, radioBean.getValue()));
                    if (radioBean.getKey() > 0) {
                        NetParams netParams = NetParams.crete().add("id", chatEntity.getTalkModel().getId())
                                .add("value", radioBean.getKey());
                        mPresenter.onDialog(mScene, netParams);
                    } else {
                        PageJumpUtils.jump(radioBean.getHref(), new PageJumpUtils.OnJumpListener() {
                            @Override
                            public void onJump(int type, Object obj) {
                                switch (type) {
                                    case PageJumpUtils.JUMP_TEXT:
                                        startActivity(new Intent(mContext, BookActivity.class));
                                        break;
                                    case PageJumpUtils.JUMP_MUSIC:
                                        startActivity(new Intent(mContext, AudioHomeActivity.class));
                                        break;
                                    case PageJumpUtils.JUMP_AI_HOME:
                                        break;
                                }
                            }
                        });
                    }
                }
            });
        } else {
            if (mSelectAdapter != null) {
                mSelectAdapter.setDataListNotify(null);
            }
        }
    }

    @Override
    public void onFail(ResponeThrowable throwable) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
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
}