package com.thfw.robotheart.activitys.test;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.viewpager2.widget.ViewPager2;

import com.thfw.base.face.OnRvItemListener;
import com.thfw.base.models.TestDetailModel;
import com.thfw.base.models.TestResultModel;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.TestPresenter;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.ToastUtil;
import com.thfw.robotheart.R;
import com.thfw.robotheart.adapter.TestngAdapter;
import com.thfw.robotheart.view.DialogRobotFactory;
import com.thfw.robotheart.view.TitleRobotView;
import com.thfw.ui.base.RobotBaseActivity;
import com.thfw.ui.dialog.LoadingDialog;
import com.thfw.ui.dialog.TDialog;
import com.thfw.ui.dialog.base.BindViewHolder;
import com.trello.rxlifecycle2.LifecycleProvider;

import java.util.Iterator;
import java.util.List;

public class TestIngActivity extends RobotBaseActivity<TestPresenter> implements TestPresenter.TestUi<TestResultModel> {


    private static TestDetailModel testDetailModel;
    private com.thfw.robotheart.view.TitleRobotView mTitleRobotView;
    private androidx.viewpager2.widget.ViewPager2 mVpList;
    private TestDetailModel mModel;
    // 测评开始时间
    private long beginTime;
    private TestngAdapter mTestIngAdapter;


    public static void startActivity(Context context, TestDetailModel testDetailModel) {
        TestIngActivity.testDetailModel = testDetailModel;
        context.startActivity(new Intent(context, TestIngActivity.class));
    }

    @Override
    public int getContentView() {
        return R.layout.activity_test_ing;
    }

    @Override
    public TestPresenter onCreatePresenter() {
        return new TestPresenter(this);
    }

    @Override
    public void initView() {

        mTitleRobotView = (TitleRobotView) findViewById(R.id.titleRobotView);
        mVpList = (ViewPager2) findViewById(R.id.vp_list);
    }

    @Override
    public void initData() {
        mModel = testDetailModel;
        testDetailModel = null;
        beginTime = System.currentTimeMillis();
        mTestIngAdapter = new TestngAdapter(mModel.getSubjectArray());
        mVpList.setAdapter(mTestIngAdapter);
        mTestIngAdapter.setOnRvItemListener(new OnRvItemListener<TestDetailModel.SubjectListBean>() {
            Runnable runnable;

            @Override
            public void onItemClick(List<TestDetailModel.SubjectListBean> list, int position) {
                mVpList.removeCallbacks(runnable);
                // 结果
                if (mVpList.getCurrentItem() == mTestIngAdapter.getItemCount() - 1) {
                    submit();
                } else {
                    if (runnable == null) {
                        runnable = new Runnable() {
                            @Override
                            public void run() {
                                mVpList.setCurrentItem(mVpList.getCurrentItem() + 1, false);
                            }
                        };
                    }
                    mVpList.postDelayed(runnable, 300);
                }
            }
        });
        mTestIngAdapter.setBtnListener(new TestngAdapter.OnBtnListener() {
            @Override
            public void onBtnClick(int type) {
                mVpList.setCurrentItem(mVpList.getCurrentItem() - 1, true);
            }
        });
        mVpList.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                boolean mUserInputEnabled = mTestIngAdapter.getDataList().get(position).getSelectedIndex() != -1;
                LogUtil.d(TAG, "mUserInputEnabled = " + mUserInputEnabled);
                mVpList.setUserInputEnabled(false);
            }
        });

    }

    private void submit() {
        LoadingDialog.show(this, "提交中");
        StringBuilder sbOpt = new StringBuilder();
        List<TestDetailModel.SubjectListBean> listBeans = mTestIngAdapter.getDataList();
        Iterator<TestDetailModel.SubjectListBean> iterator = listBeans.iterator();
        while (iterator.hasNext()) {
            sbOpt.append(iterator.next().getSelected().getId());
            if (iterator.hasNext()) {
                sbOpt.append(",");
            }
        }
        // 测评用时（秒）
        int spendTime = (int) ((System.currentTimeMillis() - beginTime) / 1000);
        mPresenter.onSubmit(mModel.getPsychtestInfo().getId(), sbOpt.toString(), spendTime);
    }

    @Override
    public LifecycleProvider getLifecycleProvider() {
        return this;
    }

    @Override
    public void onSuccess(TestResultModel data) {
        LoadingDialog.hide();
        TestResultWebActivity.startActivity(mContext, data);
        finish();
    }

    @Override
    public void onFail(ResponeThrowable throwable) {
        LoadingDialog.hide();
        ToastUtil.show("提交失败");
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        DialogRobotFactory.createCustomDialog(this, new DialogRobotFactory.OnViewCallBack() {
            @Override
            public void callBack(TextView mTvTitle, TextView mTvHint, TextView mTvLeft, TextView mTvRight, View mVLineVertical) {
                mTvHint.setText("确认结束测评吗");
                mTvTitle.setVisibility(View.GONE);
            }

            @Override
            public void onViewClick(BindViewHolder viewHolder, View view, TDialog tDialog) {
                if (view.getId() == R.id.tv_left) {
                    tDialog.dismiss();
                } else {
                    tDialog.dismiss();
                    finish();
                }
            }
        });
    }
}