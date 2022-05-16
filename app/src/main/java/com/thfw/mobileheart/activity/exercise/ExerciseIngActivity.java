package com.thfw.mobileheart.activity.exercise;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.scwang.wave.MultiWaveHeader;
import com.thfw.base.api.TalkApi;
import com.thfw.base.face.MyTextWatcher;
import com.thfw.base.face.OnRvItemListener;
import com.thfw.base.models.ChatEntity;
import com.thfw.base.models.DialogTalkModel;
import com.thfw.base.net.HttpResult;
import com.thfw.base.net.NetParams;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.TalkPresenter;
import com.thfw.base.presenter.UserToolPresenter;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.base.utils.HourUtil;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.ToastUtil;
import com.thfw.mobileheart.R;
import com.thfw.mobileheart.activity.BaseActivity;
import com.thfw.mobileheart.activity.TestActivity;
import com.thfw.mobileheart.activity.audio.AudioHomeActivity;
import com.thfw.mobileheart.activity.talk.TalkItemJumpHelper;
import com.thfw.mobileheart.activity.talk.ThemeListActivity;
import com.thfw.mobileheart.adapter.ChatAdapter;
import com.thfw.mobileheart.adapter.ChatSelectAdapter;
import com.thfw.mobileheart.util.DialogFactory;
import com.thfw.mobileheart.util.PageJumpUtils;
import com.thfw.mobileheart.view.boom.ExplosionField;
import com.thfw.mobileheart.view.fall.FallObject;
import com.thfw.mobileheart.view.fall.FallingView;
import com.thfw.ui.dialog.TDialog;
import com.thfw.ui.dialog.base.BindViewHolder;
import com.thfw.ui.voice.PolicyHelper;
import com.thfw.ui.voice.speech.SpeechHelper;
import com.thfw.ui.widget.AnimBottomRelativeLayout;
import com.thfw.ui.widget.SpeedLinearLayoutManager;
import com.thfw.ui.widget.TitleView;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.yalantis.ucrop.util.ScreenUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 工具包/成长训练对话
 */
public class ExerciseIngActivity extends BaseActivity<UserToolPresenter> implements UserToolPresenter.UserToolUi<HttpResult<List<DialogTalkModel>>> {


    public static final int REQUEST_CODE = 1;
    private static final String KEY_USED = "key.used";
    private TitleView mTitleView;
    private RecyclerView mRvList;
    private RecyclerView mRvSelect;
    private ChatAdapter mChatAdapter;
    private int mToolPackageId;
    private boolean mUsed;
    private Handler mMainHandler = new Handler(Looper.getMainLooper());
    private Helper mHelper = new Helper();
    private int mCurrentChatType;
    private ChatSelectAdapter mSelectAdapter;
    private ConstraintLayout mClLizi;
    private ImageView mIvLiziText;
    private boolean mIsAchieve;
    private int countDownFinish;
    private SmartRefreshLayout mRefreshLayout;
    private int historyDialogId = -1;
    private AnimBottomRelativeLayout mRlSend;
    private android.widget.EditText mEtContent;
    private TextView mTvSend;
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
    private boolean handleResult;

    public static void startActivity(Context context, int id, boolean used) {
        ((Activity) context).startActivityForResult(new Intent(context, ExerciseIngActivity.class)
                .putExtra(KEY_DATA, id).putExtra(KEY_USED, used), REQUEST_CODE);
    }

    @Override
    public int getContentView() {
        return R.layout.activity_exercise_ing;
    }

    @Override
    public UserToolPresenter onCreatePresenter() {
        return new UserToolPresenter(this);
    }

    @Override
    public void initView() {

        mTitleView = (TitleView) findViewById(R.id.titleView);
        mRvList = (RecyclerView) findViewById(R.id.rv_list);
        mRvList.setLayoutManager(new SpeedLinearLayoutManager(mContext));

        mTvSend = (TextView) findViewById(R.id.tv_send);
        mEtContent = (EditText) findViewById(R.id.et_content);
        mRvSelect = (RecyclerView) findViewById(R.id.rv_select);
        mRefreshLayout = findViewById(R.id.refreshLayout);
        mRefreshLayout.setEnableLoadMore(false);


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

        mChatAdapter = new ChatAdapter(null);
        mRvList.setAdapter(mChatAdapter);
        softInput();
        mClLizi = (ConstraintLayout) findViewById(R.id.cl_lizi);
        mIvLiziText = (ImageView) findViewById(R.id.iv_lizi_text);
        mIvLiziText.setVisibility(View.VISIBLE);
        mTvSend.setOnClickListener(v -> {
            String input = mEtContent.getText().toString();
            sendData(new ChatEntity(ChatEntity.TYPE_TO, input));
            onDialog(-1, -1, input, mToolPackageId);
            mEtContent.setText("");
        });
        mEtContent.addTextChangedListener(new MyTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTvSend.setEnabled(!TextUtils.isEmpty(s.toString()));
            }
        });

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

        mTitleView.getIvBack().setOnClickListener(v -> {
            if (!mIsAchieve) {
                finishService();
            }
        });
        initChatInput();

    }

    /**
     * 获取工具包历史记录
     *
     * @param toolPackageId 工具包点击课程的dialog_id
     * @param id            当需要获取最近的15条记录，不传即可，向上翻页时，需传入最后一个ID
     */
    private void historyDialog(int toolPackageId, int id) {
        NetParams netParams = NetParams.crete().add("tool_package_id", toolPackageId);
        if (id > 0) {
            netParams.add("id", id);
        }
        new UserToolPresenter(new UserToolPresenter.UserToolUi<List<DialogTalkModel>>() {
            @Override
            public LifecycleProvider getLifecycleProvider() {
                return ExerciseIngActivity.this;
            }

            @Override
            public void onSuccess(List<DialogTalkModel> data) {
                LogUtil.d("mChatAdapter JPS size onSuccess");
                List<ChatEntity> list = new ArrayList<>();
                if (EmptyUtil.isEmpty(data)) {
                    list.add(ChatEntity.createHint("没有更多数据了"));
                    mRefreshLayout.setEnableRefresh(false);
                } else {
                    historyDialogId = data.get(0).getId();
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
        }).onDialogToolHistory(netParams);
    }

    private void startFinishAnim(boolean isBoom) {

        // 初始化一个红色粒子
        FallObject.Builder builderRed = new FallObject.Builder(getResources().getDrawable(R.drawable.ic_lizi_red));
        FallObject fallObjectRed = builderRed
                .setSpeed(7, true)
                .setSize(100, 50, true)
                .setWind(5, true, true)
                .build();

        // 初始化一个蓝色粒子
        FallObject.Builder builderBlue = new FallObject.Builder(getResources().getDrawable(R.drawable.ic_lizi_blue));
        FallObject fallObjectBlue = builderBlue
                .setSpeed(7, true)
                .setSize(100, 50, true)
                .setWind(5, true, true)
                .build();

        // 初始化一个黄色粒子
        FallObject.Builder builderYellow = new FallObject.Builder(getResources().getDrawable(R.drawable.ic_lizi_yellow));
        FallObject fallObjectYellow = builderYellow
                .setSpeed(7, true)
                .setSize(100, 50, true)
                .setWind(5, true, true)
                .build();

        List<FallObject> newFallObjects = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            newFallObjects.add(fallObjectRed);
            newFallObjects.add(fallObjectYellow);
            newFallObjects.add(fallObjectBlue);
        }


        if (isBoom) {
            // 爆炸
            ExplosionField explosionField = findViewById(R.id.explosionField);
            explosionField.post(new Runnable() {
                @Override
                public void run() {
                    List<Bitmap> bitmaps = new ArrayList<>();
                    for (FallObject fallObject : newFallObjects) {
                        FallObject newFallObject = new FallObject(fallObject.builder, explosionField.getWidth(), explosionField.getHeight());
                        bitmaps.add(newFallObject.getBitmap());
                    }

                    explosionField.explode(bitmaps, explosionField, 0, 1800);

                    int height = ScreenUtils.getScreenHeight(mContext);
                    mIvLiziText.animate().translationY(height / 2 + mIvLiziText.getHeight() / 2)
                            .setInterpolator(new BounceInterpolator())
                            .setDuration(600).setStartDelay(800);
                }
            });


        } else {
            // 下落
            FallingView mFallingView = (FallingView) findViewById(R.id.fallingView);

            mFallingView.post(new Runnable() {
                @Override
                public void run() {

                    // 下落
                    mFallingView.beginDraw(newFallObjects);

                    int height = ScreenUtils.getScreenHeight(mContext);
                    mIvLiziText.animate().translationY(height / 2 + mIvLiziText.getHeight() / 2)
                            .setInterpolator(new BounceInterpolator())
                            .setDuration(800).setStartDelay(800);
                    mFallingView.setFocusable(false);

                }
            });

        }

        TextView tvFinish = findViewById(R.id.tv_finish_3s);
        countDownFinish = 4;
        mMainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (EmptyUtil.isEmpty(ExerciseIngActivity.this)) {
                    return;
                }
                countDownFinish--;
                if (countDownFinish <= 0) {
                    finish();
                } else {
                    tvFinish.setText(countDownFinish + "秒后自动关闭");
                    mMainHandler.postDelayed(this, 1000);
                }
            }
        }, 3000);


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
        } else {
            mChatAdapter.addData(ChatEntity.createTime());
        }
        mChatAdapter.addData(chatEntity);
        mChatAdapter.notifyItemInserted(mChatAdapter.getItemCount() - 1);
        if (!addTime && lastChatEntity != null && lastChatEntity.type == ChatEntity.TYPE_TO
                && mChatAdapter.getItemCount() > 2) {
            mChatAdapter.notifyItemChanged(mChatAdapter.getItemCount() - 2);
        }
        mRvList.smoothScrollToPosition(mChatAdapter.getItemCount() - 1);

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

    /**
     * 【弹框】 结束服务提醒
     */
    private void finishService() {
        DialogFactory.createCustomDialog(this, new DialogFactory.OnViewCallBack() {
            @Override
            public void callBack(TextView mTvTitle, TextView mTvHint, TextView mTvLeft, TextView mTvRight, View mVLineVertical) {
                mTvHint.setText(R.string.finishToolTitle);
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

    @Override
    public void finish() {
        if (mIsAchieve) {
            setResult(RESULT_OK);
        }
        super.finish();

    }

    @Override
    public void initData() {
        mToolPackageId = getIntent().getIntExtra(KEY_DATA, -1);
        mUsed = getIntent().getBooleanExtra(KEY_USED, false);

        if (mToolPackageId == -1) {
            ToastUtil.show("参数错误");
            finish();
            return;
        }

        // 存在断点，询问是否继续训练
        if (mUsed) {
            DialogFactory.createCustomDialog(ExerciseIngActivity.this, new DialogFactory.OnViewCallBack() {
                @Override
                public void callBack(TextView mTvTitle, TextView mTvHint, TextView mTvLeft, TextView mTvRight, View mVLineVertical) {
                    mTvHint.setText("该课程是否继续训练");
                    mTvTitle.setVisibility(View.GONE);
                    mTvLeft.setText("重新开始");
                    mTvRight.setText("继续训练");
                }

                @Override
                public void onViewClick(BindViewHolder viewHolder, View view, TDialog tDialog) {
                    tDialog.dismiss();
                    // 重新开始
                    if (view.getId() == R.id.tv_left) {
                        joinDialog(false);
                    } else {
                        joinDialog(true);
                    }

                }
            });
        } else {
            joinDialog(false);
        }
        // 历史记录
        mRefreshLayout.setEnableRefresh(true);
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull @NotNull RefreshLayout refreshLayout) {
                historyDialog(mToolPackageId, historyDialogId);
            }
        });
    }

    private void joinDialog(boolean continueValue) {
        new TalkPresenter(new TalkPresenter.TalkUi<List<DialogTalkModel>>() {
            @Override
            public LifecycleProvider getLifecycleProvider() {
                return ExerciseIngActivity.this;
            }

            @Override
            public void onSuccess(List<DialogTalkModel> data) {
                ExerciseIngActivity.this.onListDialog(data);
            }

            @Override
            public void onFail(ResponeThrowable throwable) {
                ExerciseIngActivity.this.onFail(throwable);
            }
        }).onJoinDialog(TalkApi.JOIN_TYPE_TOOL, mToolPackageId, continueValue);

    }

    @Override
    public LifecycleProvider getLifecycleProvider() {
        return this;
    }

    public void onDialog(int id, int value, String question, int toolPackageId) {
        NetParams netParams = NetParams.crete();

        if (id > 0) {
            netParams.add("id", id);
            netParams.add("value", value);
        }

        if (!TextUtils.isEmpty(question)) {
            netParams.add("question", question);
        }

        mPresenter.onDialogTool(netParams);
    }

    @Override
    public void onSuccess(HttpResult<List<DialogTalkModel>> data) {
        if (data == null) {
            return;
        }
        if (data.getData() == null) {
            return;
        }
        if (!data.isSuccessful()) {
            return;
        }

        mIsAchieve = data.isAchieve();
        onListDialog(data.getData());
        if (mIsAchieve) {
            startFinishAnim(new Random().nextBoolean());
            return;
        }
    }

    private void onListDialog(List<DialogTalkModel> data) {
        LogUtil.d(TAG, "onSuccess = " + data.size());
        mHelper.setTalks(data);
        onTalkEngine();
    }

    @Override
    public void onBackPressed() {
        if (mIsAchieve) {
            super.onBackPressed();
        } else {
            finishService();
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

        mCurrentChatType = chatEntity.type;
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

    private void sendInputText(String inputText) {
        if (mHelper.getTalkModel() == null) {
            LogUtil.d(TAG, "mHelper.getTalkModel() == null 对话还未开始！！！");
            return;
        }
        sendData(new ChatEntity(ChatEntity.TYPE_TO, inputText));
        onDialog(-1, -1, inputText, mToolPackageId);
        mEtContent.setText("");
    }

    private void setSelectItemListener(ChatEntity chatEntity) {
        mSelectAdapter.setOnRvItemListener(new OnRvItemListener<DialogTalkModel.CheckRadioBean>() {
            @Override
            public void onItemClick(List<DialogTalkModel.CheckRadioBean> list, int position) {
                if (mIsAchieve) {
                    finish();
                    return;
                }
                DialogTalkModel.CheckRadioBean radioBean = list.get(position);
                sendData(new ChatEntity(ChatEntity.TYPE_TO, radioBean.getValue()));
                if (radioBean.getKey() > 0) {
                    onDialog(chatEntity.getTalkModel().getId(), radioBean.getKey(), null, mToolPackageId);
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
        LogUtil.d(TAG, "fail = " + throwable.getMessage());
        ToastUtil.show(throwable.getMessage());
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

        switch (requestCode) {
            case PageJumpUtils.JUMP_TEXT:
            case PageJumpUtils.JUMP_MUSIC:
            case PageJumpUtils.JUMP_AI_HOME:
                // todo 待验证
                if (mHelper.getTalkModel() != null) {
                    mPresenter.onDialogTool(NetParams.crete()
                            .add("id", mHelper.getTalkModel().getId())
                            .add("value", "list_return"));
                }
                break;
            case ChatEntity.TYPE_RECOMMEND_TEXT:
            case ChatEntity.TYPE_RECOMMEND_VIDEO:
            case ChatEntity.TYPE_RECOMMEND_AUDIO:
            case ChatEntity.TYPE_RECOMMEND_AUDIO_ETC:
            case ChatEntity.TYPE_RECOMMEND_TEST:
                mPresenter.onDialogTool(NetParams.crete()
//                        .add("tool_package_id", mToolPackageId)
                        .add("value", "talking_recommend"));
                break;
            default:
                ToastUtil.show("未处理该类型结果 -> " + requestCode);
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMainHandler.removeCallbacksAndMessages(null);
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

            // 录音60S后停止
            private TextView mStopHint;
            int count;
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

        public void setTalkModel(DialogTalkModel talkModel) {
            this.talkModel = talkModel;
        }
    }


}