package com.thfw.robotheart.activitys.text;

import android.widget.FrameLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.base.face.OnRvItemListener;
import com.thfw.base.models.BookTypeModel;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.BookPresenter;
import com.thfw.base.utils.GsonUtil;
import com.thfw.base.utils.SharePreferenceUtil;
import com.thfw.robotheart.R;
import com.thfw.robotheart.adapter.BookTypeAdapter;
import com.thfw.robotheart.fragments.text.BookFragment;
import com.thfw.robotheart.util.FragmentLoader;
import com.thfw.robotheart.view.TitleRobotView;
import com.thfw.ui.base.RobotBaseActivity;
import com.thfw.ui.widget.LoadingView;
import com.trello.rxlifecycle2.LifecycleProvider;

import java.util.List;

/**
 * 心理文库
 */
public class BookActivity extends RobotBaseActivity<BookPresenter> implements BookPresenter.BookUi<BookTypeModel> {

    public static final String KEY_TYPE_LIST = "key.book.type.list";
    private com.thfw.robotheart.view.TitleRobotView mTitleRobotView;
    private androidx.recyclerview.widget.RecyclerView mRvType;
    private android.widget.FrameLayout mFlContent;
    private BookTypeAdapter mBookTypeAdapter;
    private com.thfw.ui.widget.LoadingView mLoadingView;

    @Override
    public int getContentView() {
        return R.layout.activity_book;
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
        mRvType.setLayoutManager(new LinearLayoutManager(mContext));
        mLoadingView = (LoadingView) findViewById(R.id.loadingView);
    }

    @Override
    public void initData() {

        mBookTypeAdapter = new BookTypeAdapter(null);
        mRvType.setAdapter(mBookTypeAdapter);

        FragmentLoader fragmentLoader = new FragmentLoader(getSupportFragmentManager(), R.id.fl_content);
        mBookTypeAdapter.setOnRvItemListener(new OnRvItemListener<BookTypeModel.BookTypeImpModel>() {
            @Override
            public void onItemClick(List<BookTypeModel.BookTypeImpModel> list, int position) {
                int id = list.get(position).id;
                Fragment fragment = fragmentLoader.load(id);
                if (fragment == null) {
                    fragmentLoader.add(id, new BookFragment(id));
                }
                fragmentLoader.load(id);
            }
        });
        BookTypeModel cacheModel = SharePreferenceUtil.getObject(KEY_TYPE_LIST, BookTypeModel.class);
        if (cacheModel != null) {
            mBookTypeAdapter.setDataListNotify(cacheModel.getList());
            if (mBookTypeAdapter.getItemCount() > 0) {
                mLoadingView.hide();
                mBookTypeAdapter.getOnRvItemListener().onItemClick(mBookTypeAdapter.getDataList(), 0);
            }
        }
        mPresenter.getArticleType();
    }

    @Override
    public LifecycleProvider getLifecycleProvider() {
        return this;
    }

    @Override
    public void onSuccess(BookTypeModel data) {
        SharePreferenceUtil.setString(KEY_TYPE_LIST, GsonUtil.toJson(data));
        mLoadingView.hide();

        boolean isSetEmpty = false;
        if (mBookTypeAdapter.getItemCount() == 0) {
            isSetEmpty = true;
        }
        mBookTypeAdapter.setDataListNotify(data.getList());
        if (isSetEmpty) {
            mBookTypeAdapter.getOnRvItemListener().onItemClick(mBookTypeAdapter.getDataList(), 0);
        }
    }

    @Override
    public void onFail(ResponeThrowable throwable) {
        if (mBookTypeAdapter.getItemCount() == 0) {
            mLoadingView.showFail(v -> {
                mPresenter.getArticleType();
            });
        }
    }
}