package com.thfw.mobileheart.activity.organ;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zbar_code.ZxingScanActivity;
import com.makeramen.roundedimageview.RoundedImageView;
import com.thfw.base.base.IModel;
import com.thfw.base.face.OnRvItemListener;
import com.thfw.base.face.PermissionListener;
import com.thfw.base.models.OrganizationModel;
import com.thfw.base.models.OrganizationSelectedModel;
import com.thfw.base.net.CommonParameter;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.OrganizationPresenter;
import com.thfw.base.utils.AESUtils;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.base.utils.GsonUtil;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.RegularUtil;
import com.thfw.base.utils.ToastUtil;
import com.thfw.mobileheart.MyApplication;
import com.thfw.mobileheart.R;
import com.thfw.mobileheart.activity.BaseActivity;
import com.thfw.mobileheart.activity.settings.InfoActivity;
import com.thfw.mobileheart.adapter.OrganSelectChildrenAdapter;
import com.thfw.mobileheart.adapter.OrganSelectedAdapter;
import com.thfw.mobileheart.constants.UIConfig;
import com.thfw.mobileheart.push.tester.UPushAlias;
import com.thfw.mobileheart.util.DialogFactory;
import com.thfw.ui.dialog.LoadingDialog;
import com.thfw.ui.dialog.TDialog;
import com.thfw.ui.dialog.base.BindViewHolder;
import com.thfw.ui.utils.GlideUtil;
import com.thfw.ui.widget.LoadingView;
import com.thfw.ui.widget.TitleView;
import com.thfw.user.login.LoginStatus;
import com.thfw.user.login.UserManager;
import com.trello.rxlifecycle2.LifecycleProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AskForSelectActivity extends BaseActivity<OrganizationPresenter> implements OrganizationPresenter.OrganizationUi<OrganizationModel> {


    private static final int CODE_ASK_FOR = 12;
    private static String mScanOrganizationId;
    private com.thfw.ui.widget.TitleView mTitleView;
    private com.makeramen.roundedimageview.RoundedImageView mRivAvatar;
    private android.widget.TextView mTvUserName;
    private android.widget.Button mBtScanJoin;
    // ============================= //
    private androidx.recyclerview.widget.RecyclerView mRvSelected;
    private androidx.recyclerview.widget.RecyclerView mRvSelectChildren;
    private com.thfw.ui.widget.LoadingView mLoadingView;
    private OrganizationModel mOrganizationModel;
    private ArrayList<OrganizationModel.OrganizationBean> mSelecteds = new ArrayList<>();
    private OrganSelectedAdapter mOranSelectedAdapter;
    private OrganSelectChildrenAdapter mOrganSelectChildrenAdapter;
    private android.widget.Button mBtConfirm;
    private android.widget.LinearLayout mLlWelcome;
    private android.widget.TextView mTvChooseOrganization;
    private boolean mIsFirst;
    private List<OrganizationSelectedModel.OrganizationBean> mSelectedRequest;
    private ArrayList<String> childIds;
    private androidx.constraintlayout.widget.ConstraintLayout mClRequest;
    private androidx.constraintlayout.widget.ConstraintLayout mClSelect;

    public static void startForResult(Context context, boolean isFirst) {
        ((Activity) context).startActivityForResult(new Intent(context, AskForSelectActivity.class)
                .putExtra(KEY_DATA, isFirst), CODE_ASK_FOR);
    }

    /**
     * https://www.baidu.com/t=1&i=10000
     * https://www.baidu.com/Sl4/ir9bsXacHaDev/QmKQ==
     * <p>
     * https://www.baidu.com/t=1&i=1
     * https://www.baidu.com/3RcI1a4bWnQc8oCo1qa5iw==
     *
     * @param qRCode
     * @return
     */
    public static HashMap<String, String> parseCode(String qRCode) {
        String finalQRCode = qRCode;
        if (TextUtils.isEmpty(finalQRCode)) {
            return null;
        }
        HashMap<String, String> params = new HashMap<>();

        if (finalQRCode.startsWith("http")) {
            String paramsStr = finalQRCode.replaceAll(RegularUtil.REGEX_HTTP, "");
            LogUtil.d("parseCode", "paramsStr = " + paramsStr);
            if (EmptyUtil.isEmpty(paramsStr)) {
                return params;
            }
            if (paramsStr.startsWith("?")) {
                paramsStr = paramsStr.substring(1);
            } else {
                // 匹配地址 中 有参数 以 “？” 来区分https://www.pgyer.com/qjphone?3RcI1a4bWnQc8oCo1qa5iw==
                int askIndex = -1;
                if ((askIndex = finalQRCode.indexOf("?")) != -1) {
                    try {
                        paramsStr = finalQRCode.substring(askIndex + 1);
                    } catch (Exception e) {
                        LogUtil.e("finalQRCode.substring(askIndex + 1) e = " + e.getMessage());
                    }
                }
            }
            LogUtil.d("parseCode", "paramsStr2 = " + paramsStr);
            // 3b86e89d01dafe17【3b86e89d01dafe17】
            String paramsHttp = AESUtils.decryptBase64(paramsStr, "3b86e89d01dafe17");
            LogUtil.d("parseCode", "paramsHttp = " + paramsHttp);
            if (paramsHttp != null && paramsHttp.length() > 2 && paramsHttp.startsWith("t")) {
                String[] mKeyValues = paramsHttp.split("&");
                LogUtil.d("parseCode", "【&】mKeyValues = " + GsonUtil.toJson(mKeyValues));
                if (mKeyValues != null && mKeyValues.length > 0) {
                    for (String keyValue : mKeyValues) {
                        String[] values = keyValue.split("=");
                        LogUtil.d("parseCode", "【=】values = " + GsonUtil.toJson(values));
                        if (values != null && values.length == 2) {
                            params.put(values[0], values[1]);
                        }
                    }
                    LogUtil.d("parseCode", "结果params " + GsonUtil.toJson(params));
                    return params;
                }
            }
        }

        return params;
    }

    /**
     * 读取剪贴板内容，识别成功后弹窗询问。
     * todo 暂时保留
     *
     * @param fragmentActivity
     */
    public static void readClip(FragmentActivity fragmentActivity) {
        String clipContent = MyApplication.paste();
        if (!TextUtils.isEmpty(clipContent)) {
            HashMap<String, String> params = AskForSelectActivity.parseCode(clipContent);
            LogUtil.d("readClip", "codeData params = " + GsonUtil.toJson(params));
            if (!EmptyUtil.isEmpty(params) && !TextUtils.isEmpty(params.get("i"))) {
                String organizationId = params.get("i");
                LogUtil.d("readClip", "codeData organizationId = " + organizationId);
                DialogFactory.createCustomDialog(fragmentActivity, new DialogFactory.OnViewCallBack() {
                    @Override
                    public void callBack(TextView mTvTitle, TextView mTvHint, TextView mTvLeft, TextView mTvRight, View mVLineVertical) {
                        mTvTitle.setText("温馨提示");
                        mTvHint.setText("检测到您复制了'选择机构识别码'，是否现在选择");
                        mTvLeft.setText("取消");
                        mTvRight.setText("去选择");
                    }

                    @Override
                    public void onViewClick(BindViewHolder viewHolder, View view, TDialog tDialog) {
                        tDialog.dismiss();
                        if (view.getId() == com.thfw.ui.R.id.tv_right) {
                            mScanOrganizationId = organizationId;
                            AskForSelectActivity.startForResult(fragmentActivity, false);
                        }
                    }
                });
            }
        }

    }

    @Override
    public int getContentView() {
        return R.layout.activity_askfor_select;
    }

    @Override
    public OrganizationPresenter onCreatePresenter() {
        return new OrganizationPresenter(this);
    }

    @Override
    public void initView() {
        mScanOrganizationId = "";
        mClRequest = (ConstraintLayout) findViewById(R.id.cl_request);
        mTitleView = (TitleView) findViewById(R.id.titleView);
        mRivAvatar = (RoundedImageView) findViewById(R.id.riv_avatar);
        mTvUserName = (TextView) findViewById(R.id.tv_user_name);
        mBtScanJoin = (Button) findViewById(R.id.bt_scan_join);


        //===============================//
        mRvSelected = (RecyclerView) findViewById(R.id.rv_selected);
        mRvSelected.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false));
        mRvSelectChildren = (RecyclerView) findViewById(R.id.rv_select_children);
        mRvSelectChildren.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mLoadingView = (LoadingView) findViewById(R.id.loadingView);
        mBtConfirm = (Button) findViewById(R.id.bt_confirm);
        mBtConfirm.setEnabled(false);
        mBtConfirm.setOnClickListener(v -> {
            if (EmptyUtil.isEmpty(mSelecteds)) {
                return;
            }
            onConfirm();
        });

        mLlWelcome = (LinearLayout) findViewById(R.id.ll_welcome);
        mTvChooseOrganization = (TextView) findViewById(R.id.tv_choose_organization);
        mClSelect = (ConstraintLayout) findViewById(R.id.cl_select);
    }

    /**
     * 数据长度变化监听，必须选择组织层级
     */
    private void onSelectChange() {
        mBtConfirm.setEnabled((EmptyUtil.isEmpty(mSelecteds) || mSelecteds.size() == 1) ? false : true);
    }

    private void initSelectedList() {
        if (!mSelecteds.isEmpty()) {
            mLoadingView.showLoading();
            mSelecteds.clear();
            if (mOranSelectedAdapter != null) {
                mOranSelectedAdapter.setDataListNotify(null);
            }
            if (mOrganSelectChildrenAdapter != null) {
                mOrganSelectChildrenAdapter.setDataListNotify(null);
            }
        }
        mClSelect.setVisibility(View.VISIBLE);
        mClRequest.setVisibility(View.GONE);
        mTitleView.setRightText("重新扫码");
        mTitleView.setOnClickListener(v -> {
            mBtScanJoin.performClick();
        });
        initDataList();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) {
            return;
        }
        switch (requestCode) {
            case ZxingScanActivity.REQUEST_CODE:
                String codeData = data.getStringExtra(ZxingScanActivity.CODE_DATA);
                LogUtil.d(TAG, "codeData = " + codeData);
                HashMap<String, String> params = parseCode(codeData);
                LogUtil.d(TAG, "codeData params = " + GsonUtil.toJson(params));
                if (!EmptyUtil.isEmpty(params) && !TextUtils.isEmpty(params.get("i"))) {
                    String organizationId = params.get("i");
                    LogUtil.d(TAG, "codeData organizationId = " + organizationId);
                    mScanOrganizationId = organizationId;
                    initSelectedList();
                } else {
                    mScanOrganizationId = "";
                    ToastUtil.show("无法识别该二维码！");
                }
                break;
        }
    }

    private void onConfirm() {
        LoadingDialog.show(AskForSelectActivity.this, "加载中");
        new OrganizationPresenter(new OrganizationPresenter.OrganizationUi<IModel>() {
            @Override
            public LifecycleProvider getLifecycleProvider() {
                return AskForSelectActivity.this;
            }

            @Override
            public void onSuccess(IModel data) {
                CommonParameter.setOrganizationSelected(mSelecteds);
                UserManager.getInstance().getUser().setOrganList(mSelecteds);
                if (!EmptyUtil.isEmpty(mSelecteds)) {
                    UserManager.getInstance().getUser().setOrganization(mSelecteds.get(mSelecteds.size() - 1).getId());
                    UPushAlias.setTag(mSelecteds.get(mSelecteds.size() - 1).getId());
                }
                UserManager.getInstance().notifyUserInfo();
                if (!TextUtils.isEmpty(mScanOrganizationId)) {
                    CommonParameter.setOrganizationId(mScanOrganizationId);
                }
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
                DialogFactory.createSimple(AskForSelectActivity.this, throwable.getMessage());
            }
        }).onSelectOrganization(String.valueOf(mSelecteds.get(mSelecteds.size() - 1).getId()));
    }

    @Override
    public void initData() {


        mTvUserName.setText(UserManager.getInstance().getUser().getVisibleName());
        GlideUtil.load(mContext, UserManager.getInstance().getUser().getVisibleAvatar(), mRivAvatar);
        mBtScanJoin.setOnClickListener(v -> {
            requestCallPermission(new String[]{UIConfig.NEEDED_PERMISSION[0], UIConfig.NEEDED_PERMISSION[2]},
                    new PermissionListener() {
                        @Override
                        public void onPermission(boolean has) {
                            if (has) {
                                ZxingScanActivity.startActivityForResult((Activity) mContext);
                            }
                        }
                    });

        });

        //==========================================//
        mIsFirst = getIntent().getBooleanExtra(KEY_DATA, false);

        if (mIsFirst) {
            mTitleView.getIvBack().setVisibility(View.GONE);
        }
        if (UserManager.getInstance().getUser().isNoSetOrgan()) {
            mClSelect.setVisibility(View.GONE);
            mClRequest.setVisibility(View.VISIBLE);
        } else {
            mClSelect.setVisibility(View.VISIBLE);
            mClRequest.setVisibility(View.GONE);
            initSelectedList();
        }

    }

    private void initDataList() {
        new OrganizationPresenter(new OrganizationPresenter.OrganizationUi<OrganizationSelectedModel>() {


            @Override
            public LifecycleProvider getLifecycleProvider() {
                return AskForSelectActivity.this;
            }

            @Override
            public void onSuccess(OrganizationSelectedModel data) {
                if (data != null) {
                    mSelectedRequest = new ArrayList<>();
//                    CommonParameter.setOrganizationModelPhone(data);
                    initSelectedList(mSelectedRequest, data.getOrganization());
                }
                mPresenter.onGetOrganizationList(mScanOrganizationId);
            }

            @Override
            public void onFail(ResponeThrowable throwable) {
                mLoadingView.showFail(v -> {
                    initDataList();
                });
            }
        }).onGetJoinedList(mScanOrganizationId);
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
            onSelectChange();
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
    public void onSuccess(OrganizationModel data) {
        mOrganizationModel = (OrganizationModel) data;
        if (data == null && data.getOrganization() == null) {
            mLoadingView.showEmpty();
            return;
        }
        if (mSelectedRequest != null) {
            childIds = new ArrayList<>();
            for (OrganizationSelectedModel.OrganizationBean bean : mSelectedRequest) {
                childIds.add(String.valueOf(bean.getId()));
            }
            initSelectedList2(mSelecteds, mOrganizationModel.getOrganization());
        } else {
            mSelecteds.add(mOrganizationModel.getOrganization());
            onSelectChange();
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
                onSelectChange();
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
                onSelectChange();
                mOranSelectedAdapter.notifyDataSetChanged();
                mOrganSelectChildrenAdapter.setDataListNotify(bean.getChildren());
                notifySelectedOrganization();
            }
        });
        mRvSelectChildren.setAdapter(mOrganSelectChildrenAdapter);

        mOranSelectedAdapter.getOnRvItemListener().onItemClick(mSelecteds, mSelecteds.size() - 1);
        mRvSelected.scrollToPosition(mSelecteds.size() - 1);
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
        mScanOrganizationId = "";
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