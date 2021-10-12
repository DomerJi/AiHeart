package com.thfw.mobileheart.activity.settings;

import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.makeramen.roundedimageview.RoundedImageView;
import com.thfw.base.base.IPresenter;
import com.thfw.mobileheart.adapter.InfoLikeAdapter;
import com.thfw.mobileheart.model.InfoLikeModel;
import com.thfw.robotheart.R;
import com.thfw.ui.base.BaseActivity;
import com.thfw.ui.widget.TitleView;

import java.util.ArrayList;
import java.util.List;

public class InfoActivity extends BaseActivity {

    private com.thfw.ui.widget.TitleView mTitleView;
    private android.widget.LinearLayout mLlAvatar;
    private com.makeramen.roundedimageview.RoundedImageView mRivAvatar;
    private android.widget.TextView mTvNickname;
    private android.widget.TextView mTvName;
    private android.widget.TextView mTvTeam;
    private android.widget.LinearLayout mLlUserId;
    private android.widget.TextView mTvUserid;
    private android.widget.LinearLayout mLlBirthday;
    private android.widget.TextView mTvBirthday;
    private android.widget.LinearLayout mLlSex;
    private android.widget.TextView mTvSex;
    private android.widget.LinearLayout mLlMobile;
    private android.widget.TextView mTvMobile;
    private android.widget.LinearLayout mLlSchooling;
    private android.widget.TextView mTvSchooling;
    private android.widget.LinearLayout mLlMarriage;
    private android.widget.TextView mTvMarriage;
    private android.widget.LinearLayout mLlChildren;
    private android.widget.TextView mTvChildren;
    private android.widget.LinearLayout mLlPoliticCountenance;
    private android.widget.TextView mTvPoliticCountenance;
    private android.widget.LinearLayout mLlLevel;
    private android.widget.TextView mTvLevel;
    private android.widget.LinearLayout mLlHobby;
    private androidx.recyclerview.widget.RecyclerView mRvInfoHobby;
    private android.widget.LinearLayout mLlLike;
    private androidx.recyclerview.widget.RecyclerView mRvInfoLike;

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

        mTitleView = (TitleView) findViewById(R.id.titleView);
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
    }
}