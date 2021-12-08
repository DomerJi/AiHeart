package com.thfw.robotheart.activitys.me;

import android.content.Intent;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.makeramen.roundedimageview.RoundedImageView;
import com.thfw.base.base.IPresenter;
import com.thfw.base.models.InfoLikeModel;
import com.thfw.base.utils.LogUtil;
import com.thfw.robotheart.R;
import com.thfw.robotheart.adapter.InfoLikeAdapter;
import com.thfw.robotheart.view.TitleRobotView;
import com.thfw.ui.base.BaseActivity;
import com.thfw.user.login.User;
import com.thfw.user.login.UserManager;
import com.thfw.user.login.UserObserver;

import java.util.ArrayList;
import java.util.List;

public class InfoActivity extends BaseActivity {

    private TitleRobotView mTitleView;
    private LinearLayout mLlAvatar;
    private RoundedImageView mRivAvatar;
    private TextView mTvNickname;
    private TextView mTvName;
    private TextView mTvTeam;
    private LinearLayout mLlUserId;
    private TextView mTvUserid;
    private LinearLayout mLlBirthday;
    private TextView mTvBirthday;
    private LinearLayout mLlSex;
    private TextView mTvSex;
    private LinearLayout mLlMobile;
    private TextView mTvMobile;
    private LinearLayout mLlSchooling;
    private TextView mTvSchooling;
    private LinearLayout mLlMarriage;
    private TextView mTvMarriage;
    private LinearLayout mLlChildren;
    private TextView mTvChildren;
    private LinearLayout mLlPoliticCountenance;
    private TextView mTvPoliticCountenance;
    private LinearLayout mLlLevel;
    private TextView mTvLevel;
    private LinearLayout mLlHobby;
    private RecyclerView mRvInfoHobby;
    private LinearLayout mLlLike;
    private RecyclerView mRvInfoLike;
    private LinearLayout mLlTeam;

    @Override
    public int getContentView() {
        return R.layout.activity_info;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initView() {

        mTitleView = (TitleRobotView) findViewById(R.id.titleView);
        mLlAvatar = (LinearLayout) findViewById(R.id.ll_avatar);
        mRivAvatar = (RoundedImageView) findViewById(R.id.riv_avatar);
        mTvNickname = (TextView) findViewById(R.id.tv_nickname);
        mTvName = (TextView) findViewById(R.id.tv_name);
        mTvTeam = (TextView) findViewById(R.id.tv_team);
        mLlUserId = (LinearLayout) findViewById(R.id.ll_user_id);
        mTvUserid = (TextView) findViewById(R.id.tv_userid);
        mLlBirthday = (LinearLayout) findViewById(R.id.ll_birthday);
        mTvBirthday = (TextView) findViewById(R.id.tv_birthday);
        mLlSex = (LinearLayout) findViewById(R.id.ll_sex);
        mTvSex = (TextView) findViewById(R.id.tv_sex);
        mLlMobile = (LinearLayout) findViewById(R.id.ll_mobile);
        mTvMobile = (TextView) findViewById(R.id.tv_mobile);
        mLlSchooling = (LinearLayout) findViewById(R.id.ll_schooling);
        mTvSchooling = (TextView) findViewById(R.id.tv_schooling);
        mLlMarriage = (LinearLayout) findViewById(R.id.ll_marriage);
        mTvMarriage = (TextView) findViewById(R.id.tv_marriage);
        mLlChildren = (LinearLayout) findViewById(R.id.ll_children);
        mTvChildren = (TextView) findViewById(R.id.tv_children);
        mLlPoliticCountenance = (LinearLayout) findViewById(R.id.ll_politic_countenance);
        mTvPoliticCountenance = (TextView) findViewById(R.id.tv_politic_countenance);
        mLlLevel = (LinearLayout) findViewById(R.id.ll_level);
        mTvLevel = (TextView) findViewById(R.id.tv_level);
        mLlHobby = (LinearLayout) findViewById(R.id.ll_hobby);
        mRvInfoHobby = (RecyclerView) findViewById(R.id.rv_info_hobby);
        mLlLike = (LinearLayout) findViewById(R.id.ll_like);
        mRvInfoLike = (RecyclerView) findViewById(R.id.rv_info_like);
        mLlTeam = (LinearLayout) findViewById(R.id.ll_team);
    }

    @Override
    public void initData() {
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


        mRvInfoHobby.setLayoutManager(flexboxLayoutManager);
        List<InfoLikeModel> hobbyList = new ArrayList<>();
        InfoLikeAdapter hobbyAdapter = new InfoLikeAdapter(hobbyList, 10);
        mRvInfoHobby.setAdapter(hobbyAdapter);


        // 设置布局管理器
        FlexboxLayoutManager flexboxLayoutManager02 = new FlexboxLayoutManager(mContext);
        // flexDirection 属性决定主轴的方向（即项目的排列方向）。类似 LinearLayout 的 vertical 和 horizontal。
        flexboxLayoutManager02.setFlexDirection(FlexDirection.ROW);
        // 主轴为水平方向，起点在左端。
        // flexWrap 默认情况下 Flex 跟 LinearLayout 一样，都是不带换行排列的，但是flexWrap属性可以支持换行排列。
        flexboxLayoutManager02.setFlexWrap(FlexWrap.WRAP);
        // 按正常方向换行
        // justifyContent 属性定义了项目在主轴上的对齐方式。
        flexboxLayoutManager02.setJustifyContent(JustifyContent.FLEX_START);//交叉轴的起点对齐。

        mRvInfoLike.setLayoutManager(flexboxLayoutManager02);
        List<InfoLikeModel> likeList = new ArrayList<>();
        InfoLikeAdapter infoLikeAdapter = new InfoLikeAdapter(likeList, 10);
        mRvInfoLike.setAdapter(infoLikeAdapter);

        if (UserManager.getInstance().isLogin()) {
            mTvTeam.setText(UserManager.getInstance().getUser().getOrganListStr());
        }

        mLlTeam.setOnClickListener(v -> {
            LogUtil.d(TAG, "mLlTeam +++++++++++++++ click");
            startActivity(new Intent(mContext, SelectOrganizationActivity.class));
        });
    }

    @Override
    public UserObserver addObserver() {
        return new UserObserver() {
            @Override
            public void onChanged(UserManager accountManager, User user) {
                mTvTeam.setText(user.getOrganListStr());
            }
        };
    }
}