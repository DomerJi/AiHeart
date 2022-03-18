package com.thfw.mobileheart.activity.exercise;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.thfw.base.base.IPresenter;
import com.thfw.base.utils.ToastUtil;
import com.thfw.mobileheart.adapter.AnsewerSelectAdapter;
import com.thfw.mobileheart.util.AnsewerModel;
import com.thfw.mobileheart.R;
import com.thfw.ui.base.BaseActivity;
import com.thfw.mobileheart.util.DialogFactory;
import com.thfw.ui.dialog.TDialog;
import com.thfw.ui.dialog.base.BindViewHolder;
import com.thfw.ui.widget.MyScrollView;
import com.thfw.ui.widget.TitleView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ExerciseIngActivity extends BaseActivity {

    private com.thfw.ui.widget.TitleView mTitleView;
    private android.widget.ImageView mIvShare;
    private android.widget.LinearLayout mLlTitle;
    private android.widget.TextView mTvCurrent;
    private android.widget.TextView mTvCount;
    private android.widget.LinearLayout mLlPageFooter;
    private android.widget.TextView mTvFooterCurrent;
    private android.widget.TextView mTvFooterCount;
    private android.widget.RelativeLayout mRlLast;
    private TextView mTvLast;
    private RelativeLayout mRlNext;
    private TextView mTvNext;
    private com.thfw.ui.widget.MyScrollView mMsvContent;
    private TextView mTvTitle;
    private androidx.recyclerview.widget.RecyclerView mRvSelects;
    private TextView mTvSeeHint;
    private TextView mTvAnswer;
    private TextView mTvAnswerValue;
    private TextView mTvMeAnswer;
    private TextView mTvMeAnswerValue;
    private TextView mTvMeAnswerYesOrNo;
    private TextView mTvSubjectParse;

    @Override
    public int getContentView() {
        return R.layout.activity_exercise_ing;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initView() {

        mTitleView = (TitleView) findViewById(R.id.titleView);
        mIvShare = (ImageView) findViewById(R.id.iv_share);
        mLlTitle = (LinearLayout) findViewById(R.id.ll_title);
        mTvCurrent = (TextView) findViewById(R.id.tv_current);
        mTvCount = (TextView) findViewById(R.id.tv_count);
        mLlPageFooter = (LinearLayout) findViewById(R.id.ll_page_footer);
        mTvFooterCurrent = (TextView) findViewById(R.id.tv_footer_current);
        mTvFooterCount = (TextView) findViewById(R.id.tv_footer_count);
        mRlLast = (RelativeLayout) findViewById(R.id.rl_last);
        mTvLast = (TextView) findViewById(R.id.tv_last);
        mRlNext = (RelativeLayout) findViewById(R.id.rl_next);
        mTvNext = (TextView) findViewById(R.id.tv_next);
        mMsvContent = (MyScrollView) findViewById(R.id.msv_content);
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mRvSelects = (RecyclerView) findViewById(R.id.rv_selects);
        mTvSeeHint = (TextView) findViewById(R.id.tv_see_hint);
        mTvAnswer = (TextView) findViewById(R.id.tv_answer);
        mTvAnswerValue = (TextView) findViewById(R.id.tv_answer_value);
        mTvMeAnswer = (TextView) findViewById(R.id.tv_me_answer);
        mTvMeAnswerValue = (TextView) findViewById(R.id.tv_me_answer_value);
        mTvMeAnswerYesOrNo = (TextView) findViewById(R.id.tv_me_answer_yes_or_no);
        mTvSubjectParse = (TextView) findViewById(R.id.tv_subject_parse);

        mTitleView.getIvBack().setOnClickListener(v -> {
            showFinishDialog(new Random().nextBoolean());

        });
    }

    @Override
    public void initData() {
        mTvLast.setOnClickListener(v -> {
        });
        mRlLast.setOnClickListener(v -> {
            mTvLast.performClick();
            ToastUtil.show("last");
        });


        mTvNext.setOnClickListener(v -> {
        });
        mRlNext.setOnClickListener(v -> {
            mTvNext.performClick();
            ToastUtil.show("next");
        });

        List<AnsewerModel> list = getDataList();
        if (list.size() > 4) {
            // 设置布局管理器
            FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(mContext);
            // flexDirection 属性决定主轴的方向（即项目的排列方向）。类似 LinearLayout 的 vertical 和 horizontal。
            flexboxLayoutManager.setFlexDirection(FlexDirection.ROW);
            // 主轴为水平方向，起点在左端。
            // flexWrap 默认情况下 Flex 跟 LinearLayout 一样，都是不带换行排列的，但是flexWrap属性可以支持换行排列。
            flexboxLayoutManager.setFlexWrap(FlexWrap.WRAP);
            // 按正常方向换行
            // justifyContent 属性定义了项目在主轴上的对齐方式。
            flexboxLayoutManager.setJustifyContent(JustifyContent.FLEX_START);//交叉轴的起点对齐。
            mRvSelects.setLayoutManager(flexboxLayoutManager);
        } else {
            mRvSelects.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        }
        mRvSelects.setAdapter(new AnsewerSelectAdapter(list));
    }

    private List<AnsewerModel> getDataList() {
        List<AnsewerModel> ansewerModels = new ArrayList<>();
        Random random = new Random();
        int size = 1 + random.nextInt(8);
        String[] abc = getResources().getStringArray(R.array.ABC);
        String select = "但是看了介绍可是角度骄傲的就";
        for (int i = 0; i < size; i++) {
            int len = random.nextInt(10) + 1;
            ansewerModels.add(new AnsewerModel(abc[i], select.substring(0, len)));
        }
        return ansewerModels;
    }

    private void showFinishDialog(boolean finish) {

        if (!finish) {
            DialogFactory.createCustomThreeDialog(this, new DialogFactory.OnViewThreeCallBack() {
                @Override
                public void onViewClick(BindViewHolder viewHolder, View view, TDialog tDialog) {
                    if (view.getId() == R.id.tv_one) {
                        ToastUtil.show("保存进度并退出");
                        finish();
                    } else if (view.getId() == R.id.tv_two) {
                        ToastUtil.show("直接退出");
                        finish();
                    } else if (view.getId() == R.id.tv_three) {
                        ToastUtil.show("继续练习");
                    }
                    tDialog.dismiss();
                }

                @Override
                public void callBack(TextView mTvTitle, TextView mTvHint, TextView mTvOne, TextView mTvTwo, TextView mTvThree) {
                    mTvTitle.setText("温馨提示");
                    mTvHint.setText("您尚未完成练习并提交答案，确认退出？");
                    mTvOne.setText("保存进度并退出");
                    mTvTwo.setText("直接退出");
                    mTvThree.setText("继续练习");
                }
            });
            return;
        }
        DialogFactory.createCustomDialog(this, new DialogFactory.OnViewCallBack() {
            @Override
            public void callBack(TextView mTvTitle, TextView mTvHint, TextView mTvLeft, TextView mTvRight, View mVLineVertical) {
                mTvTitle.setText("完成练习");
                mTvHint.setText("恭喜您完成所有的练习，是否提交或重新练习");
                mTvLeft.setText("重新练习");
                mTvRight.setText("确认提交");
            }

            @Override
            public void onViewClick(BindViewHolder viewHolder, View view, TDialog tDialog) {
                if (view.getId() == R.id.tv_left) {
                    ToastUtil.show("重新练习");
                } else if (view.getId() == R.id.tv_right) {
                    ToastUtil.show("确认提交");
                }
                tDialog.dismiss();
            }
        });
    }
}