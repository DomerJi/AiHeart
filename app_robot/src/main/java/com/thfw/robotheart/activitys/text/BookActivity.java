package com.thfw.robotheart.activitys.text;

import android.widget.FrameLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.base.base.IPresenter;
import com.thfw.base.face.OnRvItemListener;
import com.thfw.base.models.BookTypeModel;
import com.thfw.robotheart.R;
import com.thfw.robotheart.adapter.BookTypeAdapter;
import com.thfw.robotheart.fragments.text.BookFragment;
import com.thfw.robotheart.util.FragmentLoader;
import com.thfw.robotheart.view.TitleRobotView;
import com.thfw.ui.base.RobotBaseActivity;

import java.util.List;

public class BookActivity extends RobotBaseActivity {

    private com.thfw.robotheart.view.TitleRobotView mTitleRobotView;
    private androidx.recyclerview.widget.RecyclerView mRvType;
    private android.widget.FrameLayout mFlContent;
    private BookTypeAdapter mBookTypeAdapter;

    @Override
    public int getContentView() {
        return R.layout.activity_book;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initView() {

        mTitleRobotView = (TitleRobotView) findViewById(R.id.titleRobotView);
        mRvType = (RecyclerView) findViewById(R.id.rv_type);
        mFlContent = (FrameLayout) findViewById(R.id.fl_content);
        mRvType.setLayoutManager(new LinearLayoutManager(mContext));
    }

    @Override
    public void initData() {
        mBookTypeAdapter = new BookTypeAdapter(null);
        mRvType.setAdapter(mBookTypeAdapter);
        FragmentLoader fragmentLoader = new FragmentLoader(getSupportFragmentManager(), R.id.fl_content);
        mBookTypeAdapter.setOnRvItemListener(new OnRvItemListener<BookTypeModel>() {
            @Override
            public void onItemClick(List<BookTypeModel> list, int position) {
                int id = position;
                Fragment fragment = fragmentLoader.load(id);
                if (fragment == null) {
                    fragmentLoader.add(id, new BookFragment(id + ""));
                }
                fragmentLoader.load(id);
            }
        });
    }
}