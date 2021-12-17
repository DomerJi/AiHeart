package com.thfw.robotheart.activitys.talk;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.IBinder;
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

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.base.base.IPresenter;
import com.thfw.base.face.MyTextWatcher;
import com.thfw.base.models.ChatEntity;
import com.thfw.base.models.TalkModel;
import com.thfw.robotheart.R;
import com.thfw.robotheart.adapter.ChatAdapter;
import com.thfw.robotheart.view.TitleRobotView;
import com.thfw.ui.base.RobotBaseActivity;
import com.thfw.ui.dialog.DialogFactory;
import com.thfw.ui.dialog.TDialog;
import com.thfw.ui.dialog.base.BindViewHolder;

import java.util.Random;

public class AiTalkActivity extends RobotBaseActivity {


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

    public static void startActivity(Context context, TalkModel talkModel) {
        context.startActivity(new Intent(context, AiTalkActivity.class).putExtra(KEY_DATA, talkModel));
    }


    @Override
    public int getContentView() {
        return R.layout.activity_ai_talk;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initView() {

        mTitleRobotView = (TitleRobotView) findViewById(R.id.titleRobotView);
        mClAnim = (ConstraintLayout) findViewById(R.id.cl_anim);
        mRvList = (RecyclerView) findViewById(R.id.rv_list);
        mTvSend = (TextView) findViewById(R.id.tv_send);
        mEtContent = (EditText) findViewById(R.id.et_content);
        mRvList.setLayoutManager(new LinearLayoutManager(mContext));
        softInput();
        mTvSend.setOnClickListener(v -> {
            ChatEntity chatEntity = new ChatEntity();
            chatEntity.type = new Random().nextInt(6);
            chatEntity.talk = mEtContent.getText().toString();
            mChatAdapter.addData(chatEntity);
            mChatAdapter.notifyItemInserted(mChatAdapter.getItemCount() - 1);
            mRvList.smoothScrollToPosition(mChatAdapter.getItemCount() - 1);
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

}