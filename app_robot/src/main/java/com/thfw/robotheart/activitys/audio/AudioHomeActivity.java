package com.thfw.robotheart.activitys.audio;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.base.base.IPresenter;
import com.thfw.base.face.OnRvItemListener;
import com.thfw.base.models.AudioTypeModel;
import com.thfw.robotheart.R;
import com.thfw.robotheart.adapter.AudioEtcTypeAdapter;
import com.thfw.robotheart.fragments.audio.AudioEtcListFragment;
import com.thfw.robotheart.util.FragmentLoader;
import com.thfw.robotheart.view.TitleRobotView;
import com.thfw.ui.base.RobotBaseActivity;
import com.thfw.ui.widget.MyRobotSearchView;

import java.util.List;

public class AudioHomeActivity extends RobotBaseActivity {

    private com.thfw.robotheart.view.TitleRobotView mTitleRobotView;
    private androidx.recyclerview.widget.RecyclerView mRvList;
    private android.widget.LinearLayout mLlTop;
    private android.widget.FrameLayout mFlContent;
    private AudioEtcListFragment mAudioEtcListFragment;
    private android.widget.ImageView mIvHistory;
    private android.widget.TextView mTvLastAudio;
    private com.thfw.ui.widget.MyRobotSearchView mRsvSearch;

    @Override
    public int getContentView() {
        return R.layout.activity_audio_home;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initView() {

        mTitleRobotView = (TitleRobotView) findViewById(R.id.titleRobotView);
        mRvList = (RecyclerView) findViewById(R.id.rvList);
        mLlTop = (LinearLayout) findViewById(R.id.ll_top);
        mFlContent = (FrameLayout) findViewById(R.id.fl_content);
        mRvList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));

        mIvHistory = (ImageView) findViewById(R.id.iv_history);
        mTvLastAudio = (TextView) findViewById(R.id.tv_last_audio);
        mRsvSearch = (MyRobotSearchView) findViewById(R.id.rsv_search);
        mRsvSearch.setOnSearchListener(new MyRobotSearchView.OnSearchListener() {
            @Override
            public void onSearch(String key, boolean clickSearch) {

            }

            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public void initData() {
        FragmentLoader mLoader = new FragmentLoader(getSupportFragmentManager(), R.id.fl_content);
        AudioEtcTypeAdapter mAudioEtcTypeAdapter = new AudioEtcTypeAdapter(null);

        mAudioEtcTypeAdapter.setOnRvItemListener(new OnRvItemListener<AudioTypeModel>() {
            @Override
            public void onItemClick(List<AudioTypeModel> list, int position) {
                // todo type
                int id = position;
                Fragment fragment = mLoader.load(id);
                if (fragment == null) {
                    mLoader.add(id, new AudioEtcListFragment(String.valueOf(id)));
                }
                mAudioEtcListFragment = (AudioEtcListFragment) mLoader.load(id);
            }
        });

        mAudioEtcTypeAdapter.getOnRvItemListener().onItemClick(mAudioEtcTypeAdapter.getDataList(), 0);

        mRvList.setAdapter(mAudioEtcTypeAdapter);
    }
}