package com.thfw.robotheart.fragments;

import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.base.base.IPresenter;
import com.thfw.base.face.OnRvItemListener;
import com.thfw.base.models.AudioEtcModel;
import com.thfw.base.models.SearchResultModel;
import com.thfw.base.models.TalkModel;
import com.thfw.base.models.VideoEtcModel;
import com.thfw.base.utils.ToastUtil;
import com.thfw.robotheart.R;
import com.thfw.robotheart.activitys.SearchActivity;
import com.thfw.robotheart.activitys.audio.AudioPlayerActivity;
import com.thfw.robotheart.activitys.exercise.ExerciseDetailsActivity;
import com.thfw.robotheart.activitys.talk.AiTalkActivity;
import com.thfw.robotheart.activitys.test.TestDetailActivity;
import com.thfw.robotheart.activitys.text.BookDetailActivity;
import com.thfw.robotheart.activitys.text.BookIdeoDetailActivity;
import com.thfw.robotheart.activitys.video.VideoPlayerActivity;
import com.thfw.robotheart.adapter.SearchAdapter;
import com.thfw.ui.base.RobotBaseFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 搜索结果
 */
public class SearchResultFragment extends RobotBaseFragment {


    private RecyclerView mRvList;
    private List<SearchResultModel.ResultBean> resultBeans;
    private int type;
    private String title;
    private SearchAdapter searchAdapter;

    public SearchResultFragment(int type, String title, List<SearchResultModel.ResultBean> resultBeans) {
        super();
        this.type = type;
        this.resultBeans = resultBeans;
        this.title = title;
    }


    @Override
    public int getContentView() {
        return R.layout.fragment_search_result;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initView() {
        mRvList = (RecyclerView) findViewById(R.id.rv_list);
        mRvList.setLayoutManager(new LinearLayoutManager(mContext));
    }

    public void setResultBeans(List<SearchResultModel.ResultBean> resultBeans) {
        this.resultBeans = resultBeans;
        searchAdapter.setDataList(resultBeans);
    }

    @Override
    public void onVisible(boolean isVisible) {
        super.onVisible(isVisible);
        if (isVisible) {
            if (searchAdapter != null) {
                searchAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void initData() {
        searchAdapter = new SearchAdapter(resultBeans);
        searchAdapter.setOnRvItemListener(new OnRvItemListener<SearchResultModel.ResultBean>() {
            @Override
            public void onItemClick(List<SearchResultModel.ResultBean> list, int position) {
                SearchResultModel.ResultBean resultBean = list.get(position);

                switch (type) {
                    case SearchResultModel.TYPE_TEXT:
                        BookDetailActivity.startActivity(mContext, resultBean.getId());
                    case SearchResultModel.TYPE_IDEO_TEXT:
                        BookIdeoDetailActivity.startActivity(mContext, resultBean.getId());
                        break;
                    case SearchResultModel.TYPE_VIDEO:
                        ArrayList<VideoEtcModel> videoList = new ArrayList<>();
                        VideoEtcModel videoEtcModel = new VideoEtcModel();
                        videoEtcModel.setId(resultBean.getId());
                        videoEtcModel.setTitle(resultBean.getTitle());
                        videoList.add(videoEtcModel);
                        VideoPlayerActivity.startActivity(mContext, videoList, 0);
                        break;
                    case SearchResultModel.TYPE_AUDIO:
                        AudioEtcModel audioEtcModel = new AudioEtcModel();
                        audioEtcModel.setTitle(resultBean.getTitle());
//                        audioEtcModel.setImg(recommendInfoBean.getImg());
                        audioEtcModel.setId(resultBean.getId());
                        AudioPlayerActivity.startActivity(mContext, audioEtcModel);
                        break;
                    case SearchResultModel.TYPE_TEST:
                        TestDetailActivity.startActivity(mContext, resultBean.getId());
                        break;
                    case SearchResultModel.TYPE_TOOL:
                        ExerciseDetailsActivity.startActivity(mContext, resultBean.getId());
                        break;
                    case SearchResultModel.TYPE_DIALOG:
                        AiTalkActivity.startActivity(mContext, new TalkModel(TalkModel.TYPE_SPEECH_CRAFT)
                                .setId(resultBean.getId()));
                        break;
                    default:
                        ToastUtil.show("未处理该类型跳转 ->" + type);
                        break;
                }

            }
        });
        mRvList.setAdapter(searchAdapter);

        mRvList.setOnTouchListener(new View.OnTouchListener() {
            private Handler handler = new Handler();
            private boolean isRun;
            private Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    if (getActivity() instanceof SearchActivity) {
                        ((SearchActivity) getActivity()).onViewPagerNext();
                    }
                }
            };

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    if (event.getY() > 0 && !mRvList.canScrollVertically(1)) {
                        if (!isRun) {
                            isRun = true;
                            handler.postDelayed(runnable, 200);
                        }
                    } else {
                        handler.removeCallbacks(runnable);
                        isRun = false;
                    }
                } else {
                    isRun = false;
                    handler.removeCallbacks(runnable);
                }
                return false;
            }

        });
    }

    public String getTitle() {
        return title;
    }
}