package com.thfw.robotheart.activitys.text;

import android.widget.FrameLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.common.reflect.TypeToken;
import com.thfw.base.face.OnRvItemListener;
import com.thfw.base.models.BookStudyTypeModel;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.BookPresenter;
import com.thfw.base.utils.GsonUtil;
import com.thfw.base.utils.SharePreferenceUtil;
import com.thfw.robotheart.R;
import com.thfw.robotheart.activitys.RobotBaseActivity;
import com.thfw.robotheart.adapter.BookStudyTypeAdapter;
import com.thfw.robotheart.fragments.text.BookStudyFragment;
import com.thfw.robotheart.util.FragmentLoader;
import com.thfw.robotheart.view.TitleRobotView;
import com.thfw.ui.widget.LoadingView;
import com.trello.rxlifecycle2.LifecycleProvider;

import java.lang.reflect.Type;
import java.util.List;

/**
 * 思政文库
 */
public class BookStudyActivity extends RobotBaseActivity<BookPresenter> implements BookPresenter.BookUi<List<BookStudyTypeModel>> {


    private static final String KEY_TYPE_LIST = "key_booy_study_type_list";
    private com.thfw.robotheart.view.TitleRobotView mTitleRobotView;
    private androidx.recyclerview.widget.RecyclerView mRvType;
    private android.widget.FrameLayout mFlContent;
    private com.thfw.ui.widget.LoadingView mLoadingView;
    private BookStudyTypeAdapter studyTypeAdapter;

    @Override
    public int getContentView() {
        return R.layout.activity_book_study;
    }

    @Override
    public BookPresenter onCreatePresenter() {
        return new BookPresenter(this);
    }

    @Override
    public void initView() {

        mTitleRobotView = (TitleRobotView) findViewById(R.id.titleRobotView);
        mRvType = (RecyclerView) findViewById(R.id.rv_type);
        mFlContent = (FrameLayout) findViewById(R.id.fl_content);
        mRvType.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mLoadingView = (LoadingView) findViewById(R.id.loadingView);
    }

    @Override
    public void initData() {
        FragmentLoader fragmentLoader = new FragmentLoader(getSupportFragmentManager(), R.id.fl_content);
        studyTypeAdapter = new BookStudyTypeAdapter(null);
        studyTypeAdapter.setOnRvItemListener(new OnRvItemListener<BookStudyTypeModel>() {

            @Override
            public void onItemClick(List<BookStudyTypeModel> list, int position) {
                int id = list.get(position).id;
                Fragment fragment = fragmentLoader.load(id);
                if (fragment == null) {
                    fragmentLoader.add(id, new BookStudyFragment(id));
                }
                fragmentLoader.load(id);
            }
        });
        mRvType.setAdapter(studyTypeAdapter);

        Type type = new TypeToken<List<BookStudyTypeModel>>() {
        }.getType();
        List<BookStudyTypeModel> cacheModel = SharePreferenceUtil.getObject(KEY_TYPE_LIST, type);
        if (cacheModel != null) {
            studyTypeAdapter.setDataListNotify(cacheModel);
            if (studyTypeAdapter.getItemCount() > 0) {
                mLoadingView.hide();
                studyTypeAdapter.getOnRvItemListener().onItemClick(studyTypeAdapter.getDataList(), 0);
            }
        }
        mPresenter.getIdeologyArticleType();
    }

    @Override
    public LifecycleProvider getLifecycleProvider() {
        return this;
    }

    @Override
    public void onSuccess(List<BookStudyTypeModel> data) {
        if (data != null) {
            BookStudyTypeModel.sort(data);
            data.add(0, new BookStudyTypeModel("全部", 0));
        }

        SharePreferenceUtil.setString(KEY_TYPE_LIST, GsonUtil.toJson(data));
        mLoadingView.hide();
        boolean isSetEmpty = false;
        if (studyTypeAdapter.getItemCount() == 0) {
            isSetEmpty = true;
        }
        studyTypeAdapter.setDataListNotify(data);
        if (isSetEmpty) {
            studyTypeAdapter.getOnRvItemListener().onItemClick(studyTypeAdapter.getDataList(), 0);
        }
    }

    @Override
    public void onFail(ResponeThrowable throwable) {
        if (studyTypeAdapter.getItemCount() == 0) {
            mLoadingView.showFail(v -> {
                mPresenter.getIdeologyArticleType();
            });
        }
    }
}