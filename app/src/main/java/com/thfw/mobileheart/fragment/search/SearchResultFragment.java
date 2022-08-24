package com.thfw.mobileheart.fragment.search;

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
import com.thfw.base.utils.ToastUtil;
import com.thfw.mobileheart.R;
import com.thfw.mobileheart.activity.BaseActivity;
import com.thfw.mobileheart.activity.BaseFragment;
import com.thfw.mobileheart.activity.SearchActivity;
import com.thfw.mobileheart.activity.audio.AudioPlayerActivity;
import com.thfw.mobileheart.activity.exercise.ExerciseDetailActivity;
import com.thfw.mobileheart.activity.read.BookDetailActivity;
import com.thfw.mobileheart.activity.read.BookIdeoDetailActivity;
import com.thfw.mobileheart.activity.talk.ChatActivity;
import com.thfw.mobileheart.activity.test.TestBeginActivity;
import com.thfw.mobileheart.activity.video.VideoPlayActivity;
import com.thfw.mobileheart.adapter.SearchAdapter;

import java.util.List;

/**
 * 搜索结果
 */
public class SearchResultFragment extends BaseFragment {


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
                        break;
                    case SearchResultModel.TYPE_IDEO_TEXT:
                        BookIdeoDetailActivity.startActivity(mContext, resultBean.getId());
                        break;
                    case SearchResultModel.TYPE_VIDEO:
                        VideoPlayActivity.startActivity(mContext, resultBean.getId(), false);
                        break;
                    case SearchResultModel.TYPE_AUDIO:
                        AudioEtcModel audioEtcModel = new AudioEtcModel();
                        audioEtcModel.setTitle(resultBean.getTitle());
//                        audioEtcModel.setImg(recommendInfoBean.getImg());
                        audioEtcModel.setId(resultBean.getId());
                        AudioPlayerActivity.startActivity(mContext, audioEtcModel);
                        break;
                    case SearchResultModel.TYPE_TEST:
                        TestBeginActivity.startActivity(mContext, resultBean.getId());
                        break;
                    case SearchResultModel.TYPE_TOOL:
                        ExerciseDetailActivity.startActivity(mContext, resultBean.getId());
                        break;
                    case SearchResultModel.TYPE_DIALOG:
                        ChatActivity.startActivity(mContext, new TalkModel(TalkModel.TYPE_SPEECH_CRAFT)
                                .setId(resultBean.getId()));
                        break;
                    case SearchResultModel.TYPE_HOT_PHONE:
                        if (getActivity() instanceof BaseActivity) {
                            BaseActivity baseActivity = (BaseActivity) getActivity();
                            baseActivity.call(resultBean.getPhone());
                        }
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