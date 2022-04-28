package com.thfw.mobileheart.activity.task;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.base.face.OnRvItemListener;
import com.thfw.base.models.AudioEtcModel;
import com.thfw.base.models.TalkModel;
import com.thfw.base.models.TaskDetailModel;
import com.thfw.base.models.TaskMusicEtcModel;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.TaskPresenter;
import com.thfw.base.utils.DataChangeHelper;
import com.thfw.base.utils.ToastUtil;
import com.thfw.mobileheart.R;
import com.thfw.mobileheart.activity.BaseActivity;
import com.thfw.mobileheart.activity.audio.AudioPlayerActivity;
import com.thfw.mobileheart.activity.talk.ChatActivity;
import com.thfw.mobileheart.activity.test.TestBeginActivity;
import com.thfw.mobileheart.adapter.TaskChildLineAdapter;
import com.thfw.ui.dialog.LoadingDialog;
import com.thfw.ui.widget.LoadingView;
import com.thfw.ui.widget.TitleView;
import com.trello.rxlifecycle2.LifecycleProvider;

import java.util.HashMap;
import java.util.List;

public class TaskDetailsActivity extends BaseActivity<TaskPresenter> implements TaskPresenter.TaskUi<TaskDetailModel> {

    private int mId;
    private TitleView mTitleRobotView;
    private TextView mTvTaskTitle;
    private RecyclerView mRvList;
    private TextView mTvTaskType;
    private TextView mTvTaskStatus;
    private TextView mTvTaskTime;
    private LoadingView mLoadingView;

    public static void startActivity(Context context, int id) {
        context.startActivity(new Intent(context, TaskDetailsActivity.class)
                .putExtra(KEY_DATA, id));
    }

    @Override
    public int getContentView() {
        return R.layout.activity_task_details;
    }

    @Override
    public TaskPresenter onCreatePresenter() {
        return new TaskPresenter(this);
    }

    @Override
    public void initView() {

        mTitleRobotView = (TitleView) findViewById(R.id.titleRobotView);
        mTvTaskTitle = (TextView) findViewById(R.id.tv_task_title);
        mRvList = (RecyclerView) findViewById(R.id.rv_list);
        mRvList.setLayoutManager(new LinearLayoutManager(mContext));
        mTvTaskType = (TextView) findViewById(R.id.tv_task_type);
        mTvTaskStatus = (TextView) findViewById(R.id.tv_task_status);
        mTvTaskTime = (TextView) findViewById(R.id.tv_time);
        mLoadingView = (LoadingView) findViewById(R.id.loadingView);
    }

    @Override
    public void initData() {
        mId = getIntent().getIntExtra(KEY_DATA, -1);
        if (mId == -1) {
            ToastUtil.show("参数错误");
            finish();
            return;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.onGetInfo(mId);
    }

    @Override
    public LifecycleProvider getLifecycleProvider() {
        return this;
    }

    @Override
    public void onSuccess(TaskDetailModel data) {
        mTvTaskType.setText(data.getTaskTypeStr());
        String oldStatus = mTvTaskStatus.getText().toString();
        mTvTaskStatus.setText(data.getFinish() + "/" + data.getCount());
        if (!TextUtils.isEmpty(oldStatus) && !oldStatus.equals(mTvTaskStatus.getText().toString())) {
            HashMap<String, Object> map = new HashMap<>();
            map.put(DataChangeHelper.DataChangeListener.KEY_ID, mId);
            map.put(DataChangeHelper.DataChangeListener.KEY_TYPE, DataChangeHelper.DataChangeListener.TYPE_TASK);
            map.put(DataChangeHelper.DataChangeListener.KEY_CURRENT, data.getFinish());
            map.put(DataChangeHelper.DataChangeListener.KEY_COUNT, data.getCount());
            DataChangeHelper.getInstance().notify(map);
        }
        mTvTaskTime.setText(data.getDeadline());
        mTvTaskTitle.setText(data.getTitle());
        TaskChildLineAdapter adapter = new TaskChildLineAdapter(data.getContentList());
        adapter.setOnRvItemListener(new OnRvItemListener<TaskDetailModel.ContentListBean>() {
            @Override
            public void onItemClick(List<TaskDetailModel.ContentListBean> list, int position) {
                // 1-测评 2-音频 3-话术
                int type = data.getTaskType();
                int id = list.get(position).getId();
                String title = list.get(position).getTitle();
                switch (type) {
                    case 1:
                        TestBeginActivity.startActivity(mContext, id);
                        break;
                    case 2:
                        musicEtcInfo(list.get(position).getId());
                        break;
                    case 3:
                        ChatActivity.startActivity(mContext, new TalkModel(TalkModel.TYPE_SPEECH_CRAFT)
                                .setId(list.get(position).getId()).setTitle(title));
                        break;
                }
            }
        });
        mRvList.setAdapter(adapter);
        mLoadingView.hide();
    }


    private void musicEtcInfo(int id) {
        LoadingDialog.show(this, "加载中");
        new TaskPresenter<>(new TaskPresenter.TaskUi<TaskMusicEtcModel>() {
            @Override
            public LifecycleProvider getLifecycleProvider() {
                return TaskDetailsActivity.this;
            }

            @Override
            public void onSuccess(TaskMusicEtcModel data) {
                LoadingDialog.hide();
                AudioEtcModel audioEtcModel = new AudioEtcModel();
                audioEtcModel.setId(data.getCollectionId());
                AudioPlayerActivity.startActivity(mContext, audioEtcModel, data);

            }

            @Override
            public void onFail(ResponeThrowable throwable) {
                LoadingDialog.hide();
            }
        }).onMusicInfo(id);
    }

    @Override
    public void onFail(ResponeThrowable throwable) {
        mLoadingView.showFail(v -> {
            mPresenter.onGetInfo(mId);
        });
    }
}