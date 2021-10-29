package com.thfw.mobileheart.activity.talk;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.base.base.IPresenter;
import com.thfw.base.face.MyTextWatcher;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.mobileheart.adapter.ChatAdapter;
import com.thfw.mobileheart.model.ChatEntity;
import com.thfw.robotheart.R;
import com.thfw.ui.base.BaseActivity;
import com.thfw.ui.dialog.DialogFactory;
import com.thfw.ui.dialog.TDialog;
import com.thfw.ui.dialog.base.BindViewHolder;
import com.thfw.ui.dialog.listener.OnViewClickListener;
import com.thfw.ui.widget.TitleView;

public class AskActivity extends BaseActivity {

    private RecyclerView rvChatList;
    private ChatAdapter mChatAdapter;
    private RecyclerView mRvChatList;
    private RelativeLayout mRlSend;
    private EditText mEtContent;
    private RelativeLayout mRlRoot;
    private TextView mTvSend;
    private TitleView mTitleView;
    private android.widget.ImageView mIvMore;

    public static void startActivity(Context context) {
        startActivity(context, null);
    }

    public static void startActivity(Context context, Bundle bundle) {
        Intent intent = new Intent(context, AskActivity.class);
        if (bundle != null) {
            intent.putExtra(KEY_DATA, bundle);
        }
        context.startActivity(intent);
    }

    @Override
    public int getContentView() {
        return R.layout.activity_ask;
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

        mRvChatList = (RecyclerView) findViewById(R.id.rv_chatList);
        mRlSend = (RelativeLayout) findViewById(R.id.rl_send);
        mEtContent = (EditText) findViewById(R.id.et_content);
        mEtContent.addTextChangedListener(new MyTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTvSend.setEnabled(!EmptyUtil.isEmpty(s.toString()));
            }
        });

        mTvSend = (TextView) findViewById(R.id.tv_send);
        mTvSend.setEnabled(!EmptyUtil.isEmpty(mEtContent.getText().toString()));
        mTvSend.setOnClickListener(v -> {
            ChatEntity chatEntity = new ChatEntity();
            chatEntity.type = ChatEntity.TYPE_TO;
            chatEntity.talk = mEtContent.getText().toString();
            mChatAdapter.addData(chatEntity);
            mChatAdapter.notifyItemInserted(mChatAdapter.getItemCount() - 1);
            rvChatList.smoothScrollToPosition(mChatAdapter.getItemCount() - 1);
            mEtContent.setText("");
        });
        mRlRoot = (RelativeLayout) findViewById(R.id.rl_root);

        mRvChatList.setOnTouchListener(new View.OnTouchListener() {
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
        mIvMore = (ImageView) findViewById(R.id.iv_more);
        mIvMore.setOnClickListener(v -> {
            bottomDialog();
        });
    }

    /**
     * 【弹框】 底部选项
     */
    private void bottomDialog() {
        DialogFactory.createAskMore(this, new OnViewClickListener() {
            @Override
            public void onViewClick(BindViewHolder viewHolder, View view, TDialog tDialog) {

                if (view.getId() == com.thfw.ui.R.id.tv_cancel) {

                }
            }
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

}