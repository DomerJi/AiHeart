package com.thfw.robotheart.activitys.video;

import android.widget.FrameLayout;
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

import java.util.ArrayList;
import java.util.List;

public class VideoHomeActivity extends RobotBaseActivity {

    private TitleRobotView mTitleRobotView;
    private RecyclerView mRvList;
    private LinearLayout mLlTop;
    private FrameLayout mFlContent;
    private VideoEtcListFragment mVideoEtcListFragment;
    private TextView mTvLastAudio;
    private LinearLayout mLlHistory;

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

        mTvLastAudio = (TextView) findViewById(R.id.tv_last_audio);

        mLlHistory = (LinearLayout) findViewById(R.id.ll_history);
    }

    @Override
    public void initData() {

        List<VideoTypeModel> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            VideoTypeModel model = new VideoTypeModel();
            model.id = i;
            model.name = "Praent_" + i;
            if (i % 2 == 0) {
                model.list = new ArrayList<>();
                VideoTypeModel childModel = new VideoTypeModel();
                childModel.id = (i + 1) * 1000 + 1;
                childModel.name = "Child" + 1;
                model.list.add(childModel);
                VideoTypeModel childModel2 = new VideoTypeModel();
                childModel2.id = (i + 1) * 1000 + 2;
                childModel2.name = "Child" + 2;
                model.list.add(childModel2);

                VideoTypeModel childModel3 = new VideoTypeModel();
                childModel3.id = (i + 1) * 1000 + 3;
                childModel3.name = "Child" + 3;
                model.list.add(childModel3);


                VideoTypeModel childModel4 = new VideoTypeModel();
                childModel4.id = (i + 1) * 1000 + 4;
                childModel4.name = "Child" + 4;
                model.list.add(childModel4);
            }
            list.add(model);
        }

        FragmentLoader mLoader = new FragmentLoader(getSupportFragmentManager(), R.id.fl_content);
        VideoEtcTypeAdapter mVideoEtcTypeAdapter = new VideoEtcTypeAdapter(list);

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