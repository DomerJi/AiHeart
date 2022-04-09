package com.thfw.mobileheart.activity.read;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.common.reflect.TypeToken;
import com.thfw.base.models.BookTypeModel;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.BookPresenter;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.base.utils.GsonUtil;
import com.thfw.base.utils.SharePreferenceUtil;
import com.thfw.mobileheart.R;
import com.thfw.mobileheart.activity.BaseActivity;
import com.thfw.mobileheart.fragment.list.ReadListFragment;
import com.thfw.mobileheart.view.LastTextView;
import com.thfw.ui.widget.LoadingView;
import com.thfw.ui.widget.TitleView;
import com.trello.rxlifecycle2.LifecycleProvider;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ReadHomeActivity extends BaseActivity<BookPresenter> implements BookPresenter.BookUi<BookTypeModel> {

    private static final String KEY_TYPE_LIST = "book.type.list";
    private com.thfw.ui.widget.TitleView mTitleView;
    private com.google.android.material.tabs.TabLayout mTabLayout;
    private androidx.viewpager.widget.ViewPager mViewPager;

    private List<Fragment> tabFragmentList = new ArrayList<>();
    private com.thfw.ui.widget.LoadingView mLoadingView;
    private com.thfw.mobileheart.view.LastTextView mTvLastAudio;

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, ReadHomeActivity.class));
    }

    @Override
    public int getContentView() {
        return R.layout.activity_read_home;
    }

    @Override
    public BookPresenter onCreatePresenter() {
        return new BookPresenter(this);
    }

    @Override
    public void initView() {

        mTitleView = (TitleView) findViewById(R.id.titleView);
        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mLoadingView = (LoadingView) findViewById(R.id.loadingView);
        mTvLastAudio = (LastTextView) findViewById(R.id.tv_last_audio);
    }


    @Override
    public void initData() {
        Type type = new TypeToken<BookTypeModel>() {
        }.getType();
        BookTypeModel cacheModel = SharePreferenceUtil.getObject(KEY_TYPE_LIST, type);
        if (cacheModel != null && !EmptyUtil.isEmpty(cacheModel.getList())) {
            mLoadingView.hide();
            setViewPager(cacheModel.getList());
        } else {
            mPresenter.getArticleType();
        }
    }

    @Override
    public LifecycleProvider getLifecycleProvider() {
        return this;
    }

    @Override
    public void onSuccess(BookTypeModel data) {
        mLoadingView.hide();


        mLoadingView.hide();
        if (data != null) {
            SharePreferenceUtil.setString(KEY_TYPE_LIST, GsonUtil.toJson(data));
            if (mViewPager.getAdapter() == null) {
                setViewPager(data.getList());
            }
        }
    }


    private void setViewPager(List<BookTypeModel.BookTypeImpModel> bookTypeImpModels) {
        int size = bookTypeImpModels.size();
        // 添加tab
        for (int i = 0; i < size; i++) {
            mTabLayout.addTab(mTabLayout.newTab().setText(bookTypeImpModels.get(i).value));
            tabFragmentList.add(new ReadListFragment(bookTypeImpModels.get(i).id));
        }

        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return tabFragmentList.get(position);
            }

            @Override
            public int getCount() {
                return tabFragmentList.size();
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return bookTypeImpModels.get(position).value;
            }
        });

        // 设置TabLayout和ViewPager联动
        mTabLayout.setupWithViewPager(mViewPager, false);
    }


    @Override
    public void onFail(ResponeThrowable throwable) {
        if (mViewPager.getAdapter() == null) {
            mLoadingView.showFail(v -> {
                mPresenter.getArticleType();
            });
        }
    }

    @Override
    public void finish() {
        setResult(RESULT_OK);
        super.finish();
    }
}