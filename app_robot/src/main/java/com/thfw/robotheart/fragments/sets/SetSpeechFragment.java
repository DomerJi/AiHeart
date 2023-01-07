package com.thfw.robotheart.fragments.sets;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SynthesizerListener;
import com.thfw.base.base.IPresenter;
import com.thfw.base.face.OnRvItemListener;
import com.thfw.base.models.Informant;
import com.thfw.robotheart.R;
import com.thfw.robotheart.activitys.RobotBaseFragment;
import com.thfw.robotheart.adapter.InformantAdapter;
import com.thfw.robotheart.lhxk.InstructScrollHelper;
import com.thfw.ui.common.LhXkSettingActivity;
import com.thfw.ui.voice.tts.TtsHelper;
import com.thfw.ui.voice.tts.TtsModel;
import com.thfw.ui.widget.DeviceUtil;

import java.util.List;

/**
 * Author:pengs
 * Date: 2021/12/1 9:56
 * Describe:语音设置
 */
public class SetSpeechFragment extends RobotBaseFragment {


    private RelativeLayout mRlTop;
    private Switch mSwitchAllSpeech;
    private RecyclerView mRvList;
    private TextView mTvHint;


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
        mRvList.setLayoutManager(new LinearLayoutManager(mContext));
        mTvHint = (TextView) findViewById(R.id.tv_hint);

        if (DeviceUtil.isLhXk_OS_R_SD01B()) {
            findViewById(R.id.rl_focus_speech).setOnClickListener(v -> {
                LhXkSettingActivity.startActivity(mContext);
            });
        }
    }

    @Override
    protected void initLocalVoice(int type) {
        super.initLocalVoice(type);
        new InstructScrollHelper(SetSpeechFragment.class, mRvList);
    }

    @Override
    public void initData() {
        InformantAdapter informantAdapter = new InformantAdapter(Informant.getInformant());
        informantAdapter.setOnRvItemListener(new OnRvItemListener<Informant>() {
            @Override
            public void onItemClick(List<Informant> list, int position) {

                TtsHelper.getInstance().start(new TtsModel(mTvHint.getText().toString()), new SynthesizerListener() {
                    @Override
                    public void onSpeakBegin() {
                        hintVisible();
                    }

                    @Override
                    public void onBufferProgress(int i, int i1, int i2, String s) {

                    }

                    @Override
                    public void onSpeakPaused() {
                        hintVisible();
                    }

                    @Override
                    public void onSpeakResumed() {
                        hintVisible();
                    }

                    @Override
                    public void onSpeakProgress(int i, int i1, int i2) {
                    }

                    @Override
                    public void onCompleted(SpeechError speechError) {
                        hintVisible();
                    }

                    @Override
                    public void onEvent(int i, int i1, int i2, Bundle bundle) {
                        hintVisible();
                    }
                });
            }
        });
        mRvList.setAdapter(informantAdapter);
    }

    private void hintVisible() {
        if (mTvHint != null) {
            mTvHint.setVisibility(TtsHelper.getInstance().isIng() ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onVisible(boolean isVisible) {
        super.onVisible(isVisible);
        if (!isVisible) {
            TtsHelper.getInstance().stop();
            hintVisible();
        }
    }
}
