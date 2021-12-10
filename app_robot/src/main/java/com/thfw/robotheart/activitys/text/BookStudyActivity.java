package com.thfw.robotheart.activitys.text;

import android.widget.FrameLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.base.base.IPresenter;
import com.thfw.base.face.OnRvItemListener;
import com.thfw.base.models.BookStudyTypeModel;
import com.thfw.robotheart.R;
import com.thfw.robotheart.adapter.BookStudyTypeAdapter;
import com.thfw.robotheart.fragments.text.BookStudyFragment;
import com.thfw.robotheart.util.FragmentLoader;
import com.thfw.robotheart.view.TitleRobotView;
import com.thfw.ui.base.RobotBaseActivity;

import java.util.ArrayList;
import java.util.List;

public class BookStudyActivity extends RobotBaseActivity {


    private com.thfw.robotheart.view.TitleRobotView mTitleRobotView;
    private androidx.recyclerview.widget.RecyclerView mRvType;
    private android.widget.FrameLayout mFlContent;

    @Override
    public int getContentView() {
        return R.layout.activity_book_study;
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
        mRvType.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
    }

    @Override
    public void initData() {
        List<BookStudyTypeModel> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            BookStudyTypeModel model = new BookStudyTypeModel();
            model.id = i;
            model.name = "Praent_" + i;
            if (i % 2 == 0) {
                model.list = new ArrayList<>();
                BookStudyTypeModel childModel = new BookStudyTypeModel();
                childModel.id = (i + 1) * 1000 + 1;
                childModel.name = "Child" + 1;
                model.list.add(childModel);
                BookStudyTypeModel childModel2 = new BookStudyTypeModel();
                childModel2.id = (i + 1) * 1000 + 2;
                childModel2.name = "Child" + 2;
                model.list.add(childModel2);

                BookStudyTypeModel childModel3 = new BookStudyTypeModel();
                childModel3.id = (i + 1) * 1000 + 3;
                childModel3.name = "Child" + 3;
                model.list.add(childModel3);


                BookStudyTypeModel childModel4 = new BookStudyTypeModel();
                childModel4.id = (i + 1) * 1000 + 4;
                childModel4.name = "Child" + 4;
                model.list.add(childModel4);
            }
            list.add(model);
        }
        FragmentLoader fragmentLoader = new FragmentLoader(getSupportFragmentManager(), R.id.fl_content);
        BookStudyTypeAdapter studyTypeAdapter = new BookStudyTypeAdapter(list);
        studyTypeAdapter.setOnRvItemListener(new OnRvItemListener<BookStudyTypeModel>() {

            @Override
            public void onItemClick(List<BookStudyTypeModel> list, int position) {
                int id = list.get(position).id;
                Fragment fragment = fragmentLoader.load(id);
                if (fragment == null) {
                    fragmentLoader.add(id, new BookStudyFragment(id + ""));
                }
                fragmentLoader.load(id);
            }
        });
        studyTypeAdapter.getOnRvItemListener().onItemClick(studyTypeAdapter.getDataList(), 0);
        mRvType.setAdapter(studyTypeAdapter);
    }
}