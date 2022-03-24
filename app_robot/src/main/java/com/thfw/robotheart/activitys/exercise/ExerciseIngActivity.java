package com.thfw.robotheart.activitys.exercise;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.BounceInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
import com.thfw.robotheart.R;
import com.thfw.robotheart.activitys.RobotBaseActivity;
import com.thfw.robotheart.activitys.audio.AudioHomeActivity;
import com.thfw.robotheart.activitys.talk.AndroidBug5497Workaround;
import com.thfw.robotheart.activitys.talk.SoftKeyBoardListener;
import com.thfw.robotheart.activitys.talk.TalkItemJumpHelper;
import com.thfw.robotheart.activitys.text.BookActivity;
import com.thfw.robotheart.adapter.ChatAdapter;
import com.thfw.robotheart.adapter.ChatSelectAdapter;
import com.thfw.robotheart.util.PageJumpUtils;
import com.thfw.robotheart.util.DialogRobotFactory;
import com.thfw.robotheart.view.TitleRobotView;
import com.thfw.robotheart.view.boom.ExplosionField;
import com.thfw.robotheart.view.fall.FallObject;
import com.thfw.robotheart.view.fall.FallingView;
import com.thfw.ui.dialog.TDialog;
import com.thfw.ui.dialog.base.BindViewHolder;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.yalantis.ucrop.util.ScreenUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 工具包/成长训练对话
 */
public class ExerciseIngActivity extends RobotBaseActivity<UserToolPresenter> implements UserToolPresenter.UserToolUi<HttpResult<List<DialogTalkModel>>> {


    public static final int REQUEST_CODE = 1;
    private static final String KEY_USED = "key.used";
    private com.thfw.robotheart.view.TitleRobotView mTitleRobotView;
    private androidx.recyclerview.widget.RecyclerView mRvList;
    private android.widget.RelativeLayout mRlSend;
    private android.widget.RelativeLayout mRlKeywordInput;
    private android.widget.TextView mTvSend;
    private android.widget.EditText mEtContent;
    private android.widget.RelativeLayout mRlKeyword;
    private androidx.recyclerview.widget.RecyclerView mRvSelect;
    private ChatAdapter mChatAdapter;
    private int mToolPackageId;
    private boolean mUsed;
    private Handler mMainHandler = new Handler(Looper.getMainLooper());
    private Helper mHelper = new Helper();
    private int mCurrentChatType;
    private ChatSelectAdapter mSelectAdapter;
    private boolean softKeyBoardShow;
    private androidx.constraintlayout.widget.ConstraintLayout mClLizi;
    private android.widget.ImageView mIvLiziText;
    private boolean mIsAchieve;
    private int countDownFinish;

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

        mTitleRobotView = (TitleRobotView) findViewById(R.id.titleRobotView);
        mRvList = (RecyclerView) findViewById(R.id.rv_list);
        mRvList.setLayoutManager(new LinearLayoutManager(mContext));
        mRlSend = (RelativeLayout) findViewById(R.id.rl_send);
        mRlKeywordInput = (RelativeLayout) findViewById(R.id.rl_keyword_input);
        mTvSend = (TextView) findViewById(R.id.tv_send);
        mEtContent = (EditText) findViewById(R.id.et_content);
        mRlKeyword = (RelativeLayout) findViewById(R.id.rl_keyword);
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
            if (!mIsAchieve) {
                finishService();
            }
        });
        AndroidBug5497Workaround.assistActivity(this);

        SoftKeyBoardListener.setListener(this, new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {


            @Override
            public void keyBoardShow(int height) {
                softKeyBoardShow = true;
                mRlKeywordInput.setVisibility(View.VISIBLE);
                mRlKeyword.setVisibility(View.GONE);
            }

            @Override
            public void keyBoardHide(int height) {
                softKeyBoardShow = false;
                mRlKeywordInput.setVisibility(View.GONE);
                if (mCurrentChatType == ChatEntity.TYPE_INPUT) {
                    if (!softKeyBoardShow) {
                        mRlKeyword.setVisibility(View.VISIBLE);
                    }
                }
            }
        });


        mRlKeyword.setOnClickListener(v -> {
            mRlKeywordInput.setVisibility(View.VISIBLE);
            showInput(mEtContent);
        });
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
        countDownFinish = 3;
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
        DialogRobotFactory.createCustomDialog(this, new DialogRobotFactory.OnViewCallBack() {
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
            DialogRobotFactory.createCustomDialog(ExerciseIngActivity.this, new DialogRobotFactory.OnViewCallBack() {
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

    private void setSelectItemListener(ChatEntity chatEntity) {
        mSelectAdapter.setOnRvItemListener(new OnRvItemListener<DialogTalkModel.CheckRadioBean>() {
            @Override
            public void onItemClick(List<DialogTalkModel.CheckRadioBean> list, int position) {
                if (mIsAchieve) {
                    startFinishAnim(new Random().nextBoolean());
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
    }

    @Override
    public void onFail(ResponeThrowable throwable) {
        LogUtil.d(TAG, "fail = " + throwable.getMessage());
        ToastUtil.show(throwable.getMessage());
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