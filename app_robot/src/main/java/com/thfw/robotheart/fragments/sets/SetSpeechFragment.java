package com.thfw.robotheart.fragments.sets;

import android.widget.RelativeLayout;
import android.widget.Switch;

import androidx.recyclerview.widget.RecyclerView;

import com.thfw.base.base.IPresenter;
import com.thfw.robotheart.R;
import com.thfw.robotheart.activitys.RobotBaseFragment;

/**
 * Author:pengs
 * Date: 2021/12/1 9:56
 * Describe:语音设置
 */
public class SetSpeechFragment extends RobotBaseFragment {


    private RelativeLayout mRlTop;
    private Switch mSwitchAllSpeech;
    private RecyclerView mRvList;

    @Override
    public int getContentView() {
        return R.layout.fragment_set_speech;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initView() {

        mRlTop = (RelativeLayout) findViewById(R.id.rl_top);
        mSwitchAllSpeech = (Switch) findViewById(R.id.switch_all_speech);
        mRvList = (RecyclerView) findViewById(R.id.rv_list);
    }

    @Override
    public void initData() {

    }
}
