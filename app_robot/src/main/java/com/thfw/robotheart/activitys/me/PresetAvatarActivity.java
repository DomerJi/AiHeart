package com.thfw.robotheart.activitys.me;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.thfw.base.face.OnRvItemListener;
import com.thfw.base.models.CommonModel;
import com.thfw.base.models.PresetAvatarModel;
import com.thfw.base.net.NetParams;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.UserInfoPresenter;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.base.utils.ToastUtil;
import com.thfw.robotheart.R;
import com.thfw.robotheart.activitys.RobotBaseActivity;
import com.thfw.robotheart.adapter.PresetAvatarAdapter;
import com.thfw.robotheart.view.TitleRobotView;
import com.thfw.ui.utils.GlideUtil;
import com.thfw.ui.widget.LoadingView;
import com.thfw.user.login.UserManager;
import com.trello.rxlifecycle2.LifecycleProvider;

import java.util.List;

public class PresetAvatarActivity extends RobotBaseActivity<UserInfoPresenter> implements UserInfoPresenter.UserInfoUi<List<PresetAvatarModel>> {

    private com.thfw.robotheart.view.TitleRobotView mTitleView;
    private com.makeramen.roundedimageview.RoundedImageView mRivAvatar;
    private androidx.recyclerview.widget.RecyclerView mRvList;
    private com.thfw.ui.widget.LoadingView mLoadingView;

    @Override
    public int getContentView() {
        return R.layout.activity_preset_avatar;
    }

    @Override
    public UserInfoPresenter onCreatePresenter() {
        return new UserInfoPresenter(this);
    }

    @Override
    public void initView() {

        mTitleView = (TitleRobotView) findViewById(R.id.titleView);
        mRivAvatar = (RoundedImageView) findViewById(R.id.riv_avatar);
        mRvList = (RecyclerView) findViewById(R.id.rv_list);
        mLoadingView = (LoadingView) findViewById(R.id.loadingView);
        mRvList.setLayoutManager(new GridLayoutManager(mContext, 6));
    }

    @Override
    public void initData() {
        if (UserManager.getInstance().isLogin()) {
            GlideUtil.load(mContext, UserManager.getInstance().getUser().getVisibleAvatar(), mRivAvatar);
        }
        mPresenter.onPresetAvatarList();
    }

    @Override
    public LifecycleProvider getLifecycleProvider() {
        return PresetAvatarActivity.this;
    }

    @Override
    public void onSuccess(List<PresetAvatarModel> data) {
        if (EmptyUtil.isEmpty(data)) {
            mLoadingView.showEmpty();
            return;
        }

        mLoadingView.hide();

        PresetAvatarAdapter presetAvatarAdapter = new PresetAvatarAdapter(data);
        presetAvatarAdapter.setOnRvItemListener(new OnRvItemListener<PresetAvatarModel>() {
            @Override
            public void onItemClick(List<PresetAvatarModel> list, int position) {
                GlideUtil.load(mContext, list.get(position).getPic(), mRivAvatar);
                onUpdateInfo("pic", list.get(position).getPic());
            }
        });
        mRvList.setAdapter(presetAvatarAdapter);
    }

    @Override
    public void onFail(ResponeThrowable throwable) {
        mLoadingView.showFail(v -> {
            mPresenter.onPresetAvatarList();
        });
    }

    public void onUpdateInfo(String key, String value) {

        new UserInfoPresenter(new UserInfoPresenter.UserInfoUi<CommonModel>() {
            @Override
            public LifecycleProvider getLifecycleProvider() {
                return PresetAvatarActivity.this;
            }

            @Override
            public void onSuccess(CommonModel data) {
                ToastUtil.show("设置成功");
                UserManager.getInstance().getUser().getUserInfo().pic = value;
                UserManager.getInstance().notifyUserInfo();
            }

            @Override
            public void onFail(ResponeThrowable throwable) {
                ToastUtil.show("设置失败");
            }
        }).onUpdate(NetParams.crete()
                .add("key", key)
                .add("value", value));
    }
}