package com.thfw.mobileheart.activity.heartbox;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.thfw.base.base.IPresenter;
import com.thfw.base.models.HeartBoxEntity;
import com.thfw.mobileheart.R;
import com.thfw.mobileheart.adapter.MeBoxAdapter;
import com.thfw.ui.base.BaseActivity;
import com.thfw.ui.widget.TitleView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MeHeartBoxActivity extends BaseActivity {

    private RecyclerView mRecyclerView;
    private com.google.android.material.appbar.AppBarLayout mAppBar;
    private com.google.android.material.appbar.CollapsingToolbarLayout mToolbarLayout;
    private androidx.appcompat.widget.Toolbar mToolbar;
    private ImageView mFab;
    private TitleView mTitleView;
    private androidx.constraintlayout.widget.ConstraintLayout mClUserInfo;
    private android.widget.TextView mTvName;

    @Override
    public int getContentView() {
        return R.layout.activity_me_heart_box;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initView() {
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(new MeBoxAdapter(getDatas()));

        mAppBar = (AppBarLayout) findViewById(R.id.app_bar);
        mToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mFab = (ImageView) findViewById(R.id.fab);
        TitleView titleView = findViewById(R.id.titleView);
        titleView.getTvTitle().setAlpha(0f);
        titleView.getIvBack().setOnClickListener(v -> {
            finish();
        });
        mAppBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean misAppbarExpand = true;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                int scrollRange = appBarLayout.getTotalScrollRange();
                float fraction = 1f * (scrollRange + verticalOffset) / scrollRange;
                titleView.getTvTitle().setAlpha(1 - fraction);
                if (fraction < 0.1 && misAppbarExpand) {
                    misAppbarExpand = false;
                    mClUserInfo.animate().scaleX(0).scaleY(0);
                }
                if (fraction > 0.8 && !misAppbarExpand) {
                    misAppbarExpand = true;
                    mClUserInfo.animate().scaleX(1).scaleY(1);
                }
            }
        });
        mTitleView = (TitleView) findViewById(R.id.titleView);
        mClUserInfo = (ConstraintLayout) findViewById(R.id.cl_user_info);
        mTvName = (TextView) findViewById(R.id.tv_name);
    }

    private List<HeartBoxEntity> getDatas() {
        List<HeartBoxEntity> list = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 30; i++) {
            HeartBoxEntity heartBoxEntity = new HeartBoxEntity();
            heartBoxEntity.time = random.nextLong();
            if (i % 2 == 0) {
                heartBoxEntity.content = getResources().getString(R.string.bigText2);
            }
            int len = random.nextInt(9);
            for (int j = 0; j < len; j++) {
                heartBoxEntity.images.add("");
            }
            list.add(heartBoxEntity);
        }
        return list;
    }

    @Override
    public int getStatusBarColor() {
        return STATUSBAR_TRANSPARENT;
    }

    @Override
    public void initData() {

    }

    public class BoxHolder extends RecyclerView.ViewHolder {

        public BoxHolder(@NonNull @NotNull View itemView) {
            super(itemView);
        }
    }
}