package com.thfw.mobileheart.activity.talk;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.IBinder;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.wave.MultiWaveHeader;
import com.thfw.base.api.TalkApi;
import com.thfw.base.base.IPresenter;
import com.thfw.base.face.MyTextWatcher;
import com.thfw.base.models.ChatEntity;
import com.thfw.base.models.TalkModel;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.base.utils.LogUtil;
import com.thfw.mobileheart.R;
import com.thfw.mobileheart.adapter.ChatAdapter;
import com.thfw.mobileheart.util.DialogFactory;
import com.thfw.ui.base.BaseActivity;
import com.thfw.ui.dialog.TDialog;
import com.thfw.ui.dialog.base.BindViewHolder;
import com.thfw.ui.voice.PolicyHelper;
import com.thfw.ui.voice.speech.SpeechHelper;
import com.thfw.ui.widget.TitleView;

import java.util.Random;

public class ChatActivity extends BaseActivity {

    private androidx.recyclerview.widget.RecyclerView rvChatList;
    private ChatAdapter mChatAdapter;
    private android.widget.RelativeLayout mRlSend;
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
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initView() {

        rvChatList = (RecyclerView) findViewById(R.id.rv_chatList);
        LinearLayoutManager manager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        softInput();
        rvChatList.setLayoutManager(manager);
        mChatAdapter = new ChatAdapter(null);
        rvChatList.setAdapter(mChatAdapter);

        mRlRoot = (RelativeLayout) findViewById(R.id.rl_root);

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

        initChatInput();
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

    }


    /**
     * 获取使用时长，根据主题对话和倾诉吐槽
     *
     * @param type
     * @return
     */
    public long getUseDuration(int type) {
        switch (type) {
            // 倾诉吐槽
            case TalkApi.JOIN_TYPE_AI:
                break;
            // 咨询助理
            case TalkApi.JOIN_TYPE_GUIDANCE:
                break;
            // 主题对话
            case TalkApi.JOIN_TYPE_SPEECH_CRAFT:
                break;
        }
        return 1000;
    }

    private void initChatInput() {
        mRlSend = (RelativeLayout) findViewById(R.id.rl_send);
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
            sendInputGo(mEtContent.getText().toString());
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
                            sendInputGo(mEtControlContent.getText().toString());
                            closeVoiceControl();
                        });
                    } else if (isInCloseButton(event.getRawX(), event.getRawY())) {
                        closeVoiceControl();
                    } else {
                        sendInputGo(mEtControlContent.getText().toString());
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
            }

            private void closeVoiceControl() {
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
            public void onResult(String result, boolean end) {
                LogUtil.d(TAG, "result =================================== " + result + " ; end = " + end);

                // 没有按压返回
                if (!PolicyHelper.getInstance().isPressMode()) {
                    return;
                }
                mEtControlContent.append(result);
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

    private void sendInputGo(String content) {
        if (TextUtils.isEmpty(content)) {
            return;
        }
        ChatEntity chatEntity = new ChatEntity();
        chatEntity.type = new Random().nextInt(4);
        chatEntity.talk = content;
        mChatAdapter.addData(chatEntity);
        mChatAdapter.notifyItemInserted(mChatAdapter.getItemCount() - 1);
        rvChatList.smoothScrollToPosition(mChatAdapter.getItemCount() - 1);
        mEtContent.setText("");
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
        vibrator.vibrate(150);
    }

}