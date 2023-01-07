package com.thfw.robotheart.fragments.sets;

import static com.thfw.robotheart.util.Dormant.KEY_DORMANT_SWITCH;

import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.base.base.IPresenter;
import com.thfw.base.base.SpeechToAction;
import com.thfw.base.utils.SharePreferenceUtil;
import com.thfw.robotheart.R;
import com.thfw.robotheart.activitys.RobotBaseFragment;
import com.thfw.robotheart.adapter.DormantAdapter;
import com.thfw.robotheart.lhxk.InstructScrollHelper;
import com.thfw.robotheart.lhxk.LhXkHelper;

import java.util.ArrayList;

/**
 * Author:pengs
 * Date: 2021/12/1 9:56
 * Describe:休眠设置
 */
public class SetDormantFragment extends RobotBaseFragment {


    private RelativeLayout mRlTop;
    private Switch mSwitchAllDormant;
    private RecyclerView mRvList;


    @Override
    public int getContentView() {
        return R.layout.fragment_set_dormant;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initView() {

        mRlTop = (RelativeLayout) findViewById(R.id.rl_top);
        mSwitchAllDormant = (Switch) findViewById(R.id.switch_all_dormant);
        mRvList = (RecyclerView) findViewById(R.id.rv_list);
        mRvList.setLayoutManager(new LinearLayoutManager(mContext));
        mSwitchAllDormant.setChecked(SharePreferenceUtil.getBoolean(KEY_DORMANT_SWITCH, true));
        mSwitchAllDormant.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharePreferenceUtil.setBoolean(KEY_DORMANT_SWITCH, isChecked);
                setAdapter(isChecked);
            }
        });
        setAdapter(mSwitchAllDormant.isChecked());
    }

    @Override
    public void initData() {

    }

    private void setAdapter(boolean isChecked) {
        if (!isChecked) {
            mRvList.setAdapter(new DormantAdapter(null));
        } else {
            ArrayList<Integer> minutes = new ArrayList<>();
            minutes.add(3);
            minutes.add(5);
            minutes.add(10);
            minutes.add(30);
            minutes.add(60);
            mRvList.setAdapter(new DormantAdapter(minutes));
        }
    }


    @Override
    protected void initLocalVoice(int type) {
        super.initLocalVoice(type);
        LhXkHelper.putAction(SetBlueFragment.class, new SpeechToAction("自动休眠", () -> {
            mSwitchAllDormant.setChecked(!mSwitchAllDormant.isChecked());
        }));
        LhXkHelper.putAction(SetBlueFragment.class, new SpeechToAction("关闭自动休眠", () -> {
            if (mSwitchAllDormant.isChecked()) {
                mSwitchAllDormant.setChecked(false);
            }
        }));

        new InstructScrollHelper(SetDormantFragment.class, mRvList);
    }
}
