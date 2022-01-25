package com.thfw.robotheart.fragments.sets;

import android.widget.Switch;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.base.base.IPresenter;
import com.thfw.base.timing.TimingHelper;
import com.thfw.base.timing.WorkInt;
import com.thfw.base.utils.HourUtil;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.ToastUtil;
import com.thfw.robotheart.R;
import com.thfw.robotheart.adapter.ShutDownAdapter;
import com.thfw.ui.base.RobotBaseFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Author:pengs
 * Date: 2021/12/1 9:56
 * Describe:Todo
 */
public class SetShutdownFragment extends RobotBaseFragment implements TimingHelper.WorkListener {

    private static long shutdownTime;
    private Switch mSwitchAllVolume;
    private RecyclerView mRvHour;
    private RecyclerView mRvMin;
    private RecyclerView mRvSecond;
    private TextView mTvTint;
    private List<String> mHour = new ArrayList<>();
    private List<String> mSecond = new ArrayList<>();
    private List<String> mMin = new ArrayList<>();
    private TextView mTvEcecute;

    @Override
    public int getContentView() {
        return R.layout.fragment_set_shutdown;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initView() {

        mSwitchAllVolume = (Switch) findViewById(R.id.switch_all_volume);
        mRvHour = (RecyclerView) findViewById(R.id.rv_hour);
        mRvMin = (RecyclerView) findViewById(R.id.rv_min);
        mRvSecond = (RecyclerView) findViewById(R.id.rv_second);
        mTvTint = (TextView) findViewById(R.id.tv_tint);
        mTvEcecute = (TextView) findViewById(R.id.tv_ececute);
    }

    @Override
    public void initData() {
        mRvHour.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRvMin.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRvSecond.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        LinearSnapHelper pagerSnapHelper = new LinearSnapHelper();
        LinearSnapHelper pagerSnapHelper02 = new LinearSnapHelper();
        LinearSnapHelper pagerSnapHelper03 = new LinearSnapHelper();
        pagerSnapHelper.attachToRecyclerView(mRvHour);
        pagerSnapHelper02.attachToRecyclerView(mRvSecond);
        pagerSnapHelper03.attachToRecyclerView(mRvMin);

        for (int i = 1; i <= 24; i++) {
            mHour.add(String.format("%02d", i));
        }

        for (int i = 0; i < 60; i++) {
            mSecond.add(String.format("%02d", i));
            mMin.add(String.format("%02d", i));

        }

        ShutDownAdapter mHourAdapter = new ShutDownAdapter(mHour);
        ShutDownAdapter mSecondAdapter = new ShutDownAdapter(mSecond);
        ShutDownAdapter mMinAdapter = new ShutDownAdapter(mMin);
        mRvHour.setAdapter(mHourAdapter);

        mRvSecond.setAdapter(mSecondAdapter);

        mRvMin.setAdapter(mMinAdapter);
        setTime();

        mTvEcecute.setOnClickListener(v -> {
            LinearLayoutManager hourManager = (LinearLayoutManager) mRvHour.getLayoutManager();
            LinearLayoutManager minManager = (LinearLayoutManager) mRvMin.getLayoutManager();
            LinearLayoutManager secondManger = (LinearLayoutManager) mRvSecond.getLayoutManager();
            int position01 = hourManager.findFirstVisibleItemPosition() % mHour.size();
            int position02 = minManager.findFirstVisibleItemPosition() % mMin.size();
            int position03 = secondManger.findFirstVisibleItemPosition() % mSecond.size();


            String hourStr = mHour.get(position01);
            String minStr = mMin.get(position02);
            String secondStr = mSecond.get(position03);


            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            String time = HourUtil.getYYMMDD(System.currentTimeMillis()) + " " + hourStr + ":" + minStr + ":" + secondStr;
            LogUtil.d(TAG, time);
            try {
                Date date = dateFormat.parse(time);
                long limitTime = date.getTime() - System.currentTimeMillis();
                if (limitTime < 0) {
                    limitTime = limitTime + 3600 * 24 * 1000;
                }
                LogUtil.d(TAG, "limitTime = " + limitTime);
                shutdownTime = limitTime;
                TimingHelper.getInstance().addWorkArriveListener(SetShutdownFragment.this);
                mTvTint.setText("距离关机:  " + HourUtil.getLimitTimeALl(limitTime));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });
    }

    private void setTime() {
        Calendar calendar = Calendar.getInstance();
        /**
         * 获取时分秒
         */
        //24小时制
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        //12小时制
        //System.out.println(calendar.get(Calendar.HOUR));
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        LogUtil.d(TAG, "hour = " + hour + "; minute = " + minute + "; second = " + second);
        mRvHour.scrollToPosition(hour + (24 * 10) - 1);
        mRvMin.scrollToPosition(minute + (60 * 10));
        mRvSecond.scrollToPosition(second + (60 * 10));


    }

    @Override
    public void onArrive() {
        shutdownTime = shutdownTime - 1000;
        if (shutdownTime <= 1000) {
            // todo 关机
            ToastUtil.show("要关机了！！！！");
        } else {
            mTvTint.setText("距离关机:  " + HourUtil.getLimitTimeALl(shutdownTime));
        }
    }

    @Override
    public WorkInt workInt() {
        return WorkInt.SECOND;
    }


}
