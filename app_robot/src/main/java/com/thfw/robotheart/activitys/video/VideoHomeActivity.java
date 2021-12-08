package com.thfw.robotheart.activitys.video;

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
import com.thfw.base.models.VideoTypeModel;
import com.thfw.robotheart.R;
import com.thfw.robotheart.adapter.VideoEtcTypeAdapter;
import com.thfw.robotheart.fragments.media.VideoEtcListFragment;
import com.thfw.robotheart.util.FragmentLoader;
import com.thfw.robotheart.view.TitleRobotView;
import com.thfw.ui.base.RobotBaseActivity;
import com.thfw.ui.widget.MyRobotSearchView;

import java.util.List;

public class VideoHomeActivity extends RobotBaseActivity {

    private TitleRobotView mTitleRobotView;
    private RecyclerView mRvList;
    private LinearLayout mLlTop;
    private FrameLayout mFlContent;
    private VideoEtcListFragment mVideoEtcListFragment;
    private ImageView mIvHistory;
    private TextView mTvLastAudio;
    private MyRobotSearchView mRsvSearch;

    @Override
    public int getContentView() {
        return R.layout.activity_video_home;
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
        VideoEtcTypeAdapter mVideoEtcTypeAdapter = new VideoEtcTypeAdapter(null);

        mVideoEtcTypeAdapter.setOnRvItemListener(new OnRvItemListener<VideoTypeModel>() {
            @Override
            public void onItemClick(List<VideoTypeModel> list, int position) {
                // todo type
                int id = position;
                Fragment fragment = mLoader.load(id);
                if (fragment == null) {
                    mLoader.add(id, new VideoEtcListFragment(String.valueOf(id)));
                }
                mVideoEtcListFragment = (VideoEtcListFragment) mLoader.load(id);
            }
        });

        mVideoEtcTypeAdapter.getOnRvItemListener().onItemClick(mVideoEtcTypeAdapter.getDataList(), 0);

        mRvList.setAdapter(mVideoEtcTypeAdapter);
    }
}