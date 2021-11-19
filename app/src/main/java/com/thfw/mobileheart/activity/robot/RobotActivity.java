package com.thfw.mobileheart.activity.robot;

import android.content.pm.ActivityInfo;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.thfw.base.base.IPresenter;
import com.thfw.base.utils.Util;
import com.thfw.mobileheart.MyApplication;
import com.thfw.mobileheart.fragment.robot.RobotDetailFragment;
import com.thfw.mobileheart.fragment.robot.RobotFragment;
import com.thfw.mobileheart.R;
import com.thfw.ui.base.BaseActivity;

public class RobotActivity extends BaseActivity {

    private FrameLayout mFlRobot;
    private FrameLayout mFlRobotDetail;
    private RobotFragment mRobotFragment;
    private RobotDetailFragment mRobotDetailFragment;
    private TextView mTvDrag;
    private RelativeLayout.LayoutParams rightLp;
    private RelativeLayout.LayoutParams leftLp;
    private static int MIN_DRAG_WIDTH = 200;
    private static int MAX_DRAG_WIDTH = 0;

    @Override
    public int getContentView() {
        if (Util.isPad(mContext)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        return R.layout.activity_robot;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initView() {
        MAX_DRAG_WIDTH = Util.getScreenLogicWidth(MyApplication.getApp()) - MIN_DRAG_WIDTH;
        mFlRobot = (FrameLayout) findViewById(R.id.fl_robot);
        mFlRobotDetail = (FrameLayout) findViewById(R.id.fl_robot_chatlist);
        mTvDrag = findViewById(R.id.tv_drag);
        mRobotFragment = new RobotFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.fl_robot, mRobotFragment).commit();
        // 平板
        if (Util.isPad(mContext) && mFlRobotDetail != null) {
            mRobotDetailFragment = new RobotDetailFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.fl_robot_chatlist, mRobotDetailFragment).commit();
        }
        if (mTvDrag != null) {
            mTvDrag.setOnTouchListener(new View.OnTouchListener() {
                boolean first = true;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Log.d("TvDrag", "X = " + event.getX());
                    rightLp = (RelativeLayout.LayoutParams) mFlRobotDetail.getLayoutParams();
                    leftLp = (RelativeLayout.LayoutParams) mFlRobot.getLayoutParams();

                    leftLp.width = (int) (leftLp.width + event.getX());
                    // 控制最大最小宽度
                    if (leftLp.width < MIN_DRAG_WIDTH) {
                        leftLp.width = 150;
                    } else if (leftLp.width > MAX_DRAG_WIDTH) {
                        leftLp.width = MAX_DRAG_WIDTH;
                    }

                    rightLp.width = -1;
                    mFlRobotDetail.setLayoutParams(rightLp);
                    mFlRobot.setLayoutParams(leftLp);
                    return true;
                }
            });
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    @Override
    public void initData() {

    }
}