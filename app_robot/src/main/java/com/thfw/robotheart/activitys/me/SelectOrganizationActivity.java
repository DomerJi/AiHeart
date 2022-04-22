package com.thfw.robotheart.activitys.me;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.base.base.IModel;
import com.thfw.base.face.OnRvItemListener;
import com.thfw.base.models.OrganizationModel;
import com.thfw.base.models.OrganizationSelectedModel;
import com.thfw.base.net.CommonParameter;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.OrganizationPresenter;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.base.utils.ToastUtil;
import com.thfw.robotheart.R;
import com.thfw.robotheart.activitys.RobotBaseActivity;
import com.thfw.robotheart.adapter.OrganSelectChildrenAdapter;
import com.thfw.robotheart.adapter.OrganSelectedAdapter;
import com.thfw.robotheart.push.tester.UPushAlias;
import com.thfw.robotheart.util.DialogRobotFactory;
import com.thfw.robotheart.view.TitleRobotView;
import com.thfw.ui.dialog.LoadingDialog;
import com.thfw.ui.widget.LoadingView;
import com.thfw.user.login.LoginStatus;
import com.thfw.user.login.UserManager;
import com.trello.rxlifecycle2.LifecycleProvider;

import java.util.ArrayList;
import java.util.List;

public class SelectOrganizationActivity extends RobotBaseActivity<OrganizationPresenter> implements OrganizationPresenter.OrganizationUi<IModel> {

    private com.thfw.robotheart.view.TitleRobotView mTitleRobotView;
    private androidx.recyclerview.widget.RecyclerView mRvSelected;
    private androidx.recyclerview.widget.RecyclerView mRvSelectChildren;
    private com.thfw.ui.widget.LoadingView mLoadingView;
    private OrganizationModel mOrganizationModel;
    private ArrayList<OrganizationModel.OrganizationBean> mSelecteds = new ArrayList<>();
    private OrganSelectedAdapter mOranSelectedAdapter;
    private OrganSelectChildrenAdapter mOrganSelectChildrenAdapter;
    private android.widget.Button mBtConfirm;
    private android.widget.LinearLayout mLlWelcome;
    private android.widget.TextView mTvNickname;
    private android.widget.TextView mTvChooseOrganization;
    private boolean mIsFirst;

    private List<OrganizationSelectedModel.OrganizationBean> mSelectedRequest;
    private ArrayList<String> childIds;

    public static void startActivity(Context context, boolean isFirst) {
        context.startActivity(new Intent(context, SelectOrganizationActivity.class)
                .putExtra(KEY_DATA, isFirst));
    }

    @Override
    public int getContentView() {
        return R.layout.activity_select_organization;
    }

    @Override
    public OrganizationPresenter onCreatePresenter() {
        return new OrganizationPresenter(this);
    }

    @Override
    public void initView() {

        mTitleRobotView = (TitleRobotView) findViewById(R.id.titleRobotView);
        mRvSelected = (RecyclerView) findViewById(R.id.rv_selected);
        mRvSelected.setLayoutManager(new GridLayoutManager(mContext, 6));
        mRvSelectChildren = (RecyclerView) findViewById(R.id.rv_select_children);
        mRvSelectChildren.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mLoadingView = (LoadingView) findViewById(R.id.loadingView);
        mBtConfirm = (Button) findViewById(R.id.bt_confirm);
        mBtConfirm.setOnClickListener(v -> {
            if (EmptyUtil.isEmpty(mSelecteds)) {
                return;
            }
            onConfirm();
        });


        mLlWelcome = (LinearLayout) findViewById(R.id.ll_welcome);
        mTvNickname = (TextView) findViewById(R.id.tv_nickname);
        mTvChooseOrganization = (TextView) findViewById(R.id.tv_choose_organization);

    }

    private void onConfirm() {
        LoadingDialog.show(SelectOrganizationActivity.this, "加载中");
        new OrganizationPresenter(new OrganizationPresenter.OrganizationUi<IModel>() {
            @Override
            public LifecycleProvider getLifecycleProvider() {
                return SelectOrganizationActivity.this;
            }

            @Override
            public void onSuccess(IModel data) {
                CommonParameter.setOrganizationSelected(mSelecteds);
                UserManager.getInstance().getUser().setOrganList(mSelecteds);
                UserManager.getInstance().notifyUserInfo();
                LoadingDialog.hide();
                ToastUtil.show("选择成功");
                mIsFirst = false;
                if (!UserManager.getInstance().getUser().isSetUserInfo()) {
                    InfoActivity.startActivityFirst(mContext);
                } else {
                    UserManager.getInstance().login();
                }
                finish();
            }

            @Override
            public void onFail(ResponeThrowable throwable) {
                LoadingDialog.hide();
                DialogRobotFactory.createSimple(SelectOrganizationActivity.this, throwable.getMessage()
                        + "(code:" + throwable.getCode() + ")");
            }
        }).onSelectOrganization(String.valueOf(mSelecteds.get(mSelecteds.size() - 1).getId()));
    }

    @Override
    public void initData() {
        mIsFirst = getIntent().getBooleanExtra(KEY_DATA, false);

        if (mIsFirst) {
            mTitleRobotView.getLlBack().setVisibility(View.GONE);
        }
        if (UserManager.getInstance().isLogin()) {
            mTvNickname.setText(UserManager.getInstance().getUser().getVisibleName());
        }

        initDataList();
    }

    private void initDataList() {
        new OrganizationPresenter(new OrganizationPresenter.OrganizationUi<OrganizationSelectedModel>() {


            @Override
            public LifecycleProvider getLifecycleProvider() {
                return SelectOrganizationActivity.this;
            }

            @Override
            public void onSuccess(OrganizationSelectedModel data) {
                if (data != null) {
                    mSelectedRequest = new ArrayList<>();
                    initSelectedList(mSelectedRequest, data.getOrganization());
                }
                mPresenter.onGetOrganizationList();
            }

            @Override
            public void onFail(ResponeThrowable throwable) {
                mLoadingView.showFail(v -> {
                    initDataList();
                });
            }
        }).onGetJoinedList();
    }

    private void initSelectedList(List<OrganizationSelectedModel.OrganizationBean> list, OrganizationSelectedModel.OrganizationBean bean) {
        if (bean != null) {
            list.add(bean);
            if (bean.getChildren() != null) {
                initSelectedList(list, bean.getChildren());
            }
        }
    }

    private void initSelectedList2(List<OrganizationModel.OrganizationBean> list, OrganizationModel.OrganizationBean bean) {
        if (bean != null) {
            list.add(bean);
            if (bean.getChildren() != null) {
                for (OrganizationModel.OrganizationBean b : bean.getChildren()) {
                    if (childIds.contains(String.valueOf(b.getId()))) {
                        initSelectedList2(list, b);
                        break;
                    }
                }

            }
        }
    }

    @Override
    public LifecycleProvider getLifecycleProvider() {
        return this;
    }

    @Override
    public void onSuccess(IModel data) {
        if (data instanceof OrganizationModel) {
            mOrganizationModel = (OrganizationModel) data;
            if (mOrganizationModel == null && mOrganizationModel.getOrganization() == null) {
                mLoadingView.showEmpty();
                return;
            }
            if (mSelectedRequest != null) {
                childIds = new ArrayList<>();
                for (OrganizationSelectedModel.OrganizationBean bean : mSelectedRequest) {
                    childIds.add(String.valueOf(bean.getId()));
                }
                initSelectedList2(mSelecteds, mOrganizationModel.getOrganization());
                CommonParameter.setOrganizationSelected(mSelecteds);
                UserManager.getInstance().getUser().setOrganList(mSelecteds);
                UPushAlias.setTag(mSelecteds.get(mSelecteds.size() - 1).getId());
            } else {
                mSelecteds.add(mOrganizationModel.getOrganization());
            }
            // 已选择信息
            mOranSelectedAdapter = new OrganSelectedAdapter(mSelecteds);
            mOranSelectedAdapter.setOnRvItemListener(new OnRvItemListener<OrganizationModel.OrganizationBean>() {
                @Override
                public void onItemClick(List<OrganizationModel.OrganizationBean> list, int position) {

                    if (position == mSelecteds.size() - 1) {
                        mOrganSelectChildrenAdapter.setDataListNotify(mSelecteds.get(position).getChildren());
                        return;
                    }
                    for (int i = mSelecteds.size() - 1; i > position; i--) {
                        mSelecteds.remove(i);
                    }
                    mOranSelectedAdapter.notifyDataSetChanged();
                    mOrganSelectChildrenAdapter.setDataListNotify(mSelecteds.get(position).getChildren());
                    notifySelectedOrganization();
                }
            });

            mRvSelected.setAdapter(mOranSelectedAdapter);
            notifySelectedOrganization();

            mLoadingView.hide();


            mOrganSelectChildrenAdapter = new OrganSelectChildrenAdapter(mOrganizationModel.getOrganization().getChildren());
            mOrganSelectChildrenAdapter.setOnRvItemListener(new OnRvItemListener<OrganizationModel.OrganizationBean>() {
                @Override
                public void onItemClick(List<OrganizationModel.OrganizationBean> list, int position) {
                    OrganizationModel.OrganizationBean bean = list.get(position);
                    mSelecteds.add(bean);
                    mOranSelectedAdapter.notifyDataSetChanged();
                    mOrganSelectChildrenAdapter.setDataListNotify(bean.getChildren());
                    notifySelectedOrganization();
                }
            });
            mRvSelectChildren.setAdapter(mOrganSelectChildrenAdapter);

            mOranSelectedAdapter.getOnRvItemListener().onItemClick(mSelecteds, mSelecteds.size() - 1);

        }
    }

    private void notifySelectedOrganization() {
        StringBuilder sb = new StringBuilder();
        sb.append("当前选择：");
        int size = mSelecteds.size();

        for (int i = 0; i < size; i++) {
            sb.append(mSelecteds.get(i).getName());
            if (i != size - 1) {
                sb.append(" → ");
            }
        }

        mTvChooseOrganization.setText(sb.toString());
    }

    @Override
    public void onFail(ResponeThrowable throwable) {
        if (!EmptyUtil.isEmpty(mSelecteds)) {
            LoadingDialog.hide();
            return;
        }
        mLoadingView.showFail(v -> {
            initData();
        });
    }

    @Override
    public void onDestroy() {
        if (mIsFirst) {
            UserManager.getInstance().logout(LoginStatus.LOGOUT_EXIT);
        }
        super.onDestroy();
    }

    @Override
    public void finish() {
        if (mIsFirst) {
            return;
        }
        super.finish();
    }
}