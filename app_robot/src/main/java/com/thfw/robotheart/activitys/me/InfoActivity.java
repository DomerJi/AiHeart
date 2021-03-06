package com.thfw.robotheart.activitys.me;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.common.reflect.TypeToken;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.listener.OnResultCallbackListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.thfw.base.face.OnRvItemListener;
import com.thfw.base.models.AccountPinyinModel;
import com.thfw.base.models.CommonModel;
import com.thfw.base.models.HeadModel;
import com.thfw.base.models.OrganizationModel;
import com.thfw.base.models.OrganizationSelectedModel;
import com.thfw.base.models.PickerData;
import com.thfw.base.models.PresetAvatarModel;
import com.thfw.base.net.ApiHost;
import com.thfw.base.net.CommonParameter;
import com.thfw.base.net.HttpResult;
import com.thfw.base.net.MultipartBodyFactory;
import com.thfw.base.net.NetParams;
import com.thfw.base.net.OkHttpUtil;
import com.thfw.base.net.ResponeThrowable;
import com.thfw.base.presenter.OrganizationPresenter;
import com.thfw.base.presenter.UserInfoPresenter;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.base.utils.FileUtil;
import com.thfw.base.utils.GsonUtil;
import com.thfw.base.utils.HourUtil;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.PinYinUtil;
import com.thfw.base.utils.StringUtil;
import com.thfw.base.utils.ToastUtil;
import com.thfw.robotheart.R;
import com.thfw.robotheart.activitys.RobotBaseActivity;
import com.thfw.robotheart.activitys.login.BindPhoneActivity;
import com.thfw.robotheart.activitys.login.SetPasswordActivity;
import com.thfw.robotheart.adapter.BaseAdapter;
import com.thfw.robotheart.adapter.DialogLikeAdapter;
import com.thfw.robotheart.adapter.InfoLikeAdapter;
import com.thfw.robotheart.util.AreaUtil;
import com.thfw.robotheart.util.DialogRobotFactory;
import com.thfw.robotheart.util.GlideImageEngine;
import com.thfw.robotheart.view.TitleRobotView;
import com.thfw.ui.dialog.LoadingDialog;
import com.thfw.ui.dialog.TDialog;
import com.thfw.ui.dialog.base.BindViewHolder;
import com.thfw.ui.utils.GlideUtil;
import com.thfw.user.login.LoginStatus;
import com.thfw.user.login.UserManager;
import com.thfw.user.login.UserObserver;
import com.thfw.user.models.User;
import com.trello.rxlifecycle2.LifecycleProvider;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class InfoActivity extends RobotBaseActivity<UserInfoPresenter> implements UserInfoPresenter.UserInfoUi<User.UserInfo> {

    private static final int BIND_CODE = 9;
    ArrayList<String> mHobby = new ArrayList<>();
    ArrayList<String> mSupport = new ArrayList<>();
    private TitleRobotView mTitleView;
    private LinearLayout mLlAvatar;
    private RoundedImageView mRivAvatar;
    private TextView mTvName;
    private TextView mTvTeam;
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
    private LinearLayout mLlAddress;
    private TextView mTvAddress;
    private LinearLayout mLlNationality;
    private TextView mTvNationality;
    private LinearLayout mLlJoinJTime;
    private TextView mTvJoinJTime;
    private LinearLayout mLlName;
    private InfoLikeAdapter hobbyAdapter;
    private InfoLikeAdapter infoLikeAdapter;
    private LinearLayout mLlNickname;
    private TextView mTvNickname;
    private LinearLayout mLlClass;
    private TextView mTvClass;
    private LinearLayout mLlWelcome;
    private TextView mTvWelNickname;
    private boolean mFirstInputMsg;
    private android.widget.Button mBtConfirm;
    private PopupWindow mPopWindow;
    private android.widget.RelativeLayout mClRoot;
    private TextView mTvStar00;
    private TextView mTvStar01;
    private LinearLayout mLlNamePinyin;
    private TextView mTvStar01Pinyin;
    private TextView mTvNamePinyin;
    private View mVPinyinLine;
    private TextView mTvStar02;
    private TextView mTvStar03;
    private TextView mTvStar04;
    private TextView mTvStar05;
    private TextView mTvStar06;
    private TextView mTvStar07;
    private TextView mTvStar08;
    private TextView mTvStar09;
    private TextView mTvStar10;
    private TextView mTvStar11;
    private TextView mTvStar12;
    private TextView mTvStar13;
    private TextView mTvStar14;
    private TextView mTvStar15;
    private TextView mTvStar16;

    public static void startActivityFirst(Context context) {
        context.startActivity(new Intent(context, InfoActivity.class).putExtra(KEY_DATA, true));
    }

    @Override
    public int getContentView() {
        return R.layout.activity_info;
    }

    @Override
    public UserInfoPresenter onCreatePresenter() {
        return new UserInfoPresenter(this);
    }

    @Override
    public void onDestroy() {
        if (mFirstInputMsg) {
            UserManager.getInstance().logout(LoginStatus.LOGOUT_EXIT);
        }
        super.onDestroy();
    }

    @Override
    public void finish() {
        if (!mFirstInputMsg) {
            super.finish();
        }

    }

    @Override
    public void initView() {

        mTitleView = (TitleRobotView) findViewById(R.id.titleView);
        mLlAvatar = (LinearLayout) findViewById(R.id.ll_avatar);
        mRivAvatar = (RoundedImageView) findViewById(R.id.riv_avatar);
        mTvName = (TextView) findViewById(R.id.tv_name);
        mTvTeam = (TextView) findViewById(R.id.tv_team);
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
        mLlAddress = (LinearLayout) findViewById(R.id.ll_address);
        mTvAddress = (TextView) findViewById(R.id.tv_address);
        mLlNationality = (LinearLayout) findViewById(R.id.ll_nationality);
        mTvNationality = (TextView) findViewById(R.id.tv_nationality);
        mLlJoinJTime = (LinearLayout) findViewById(R.id.ll_join_j_time);
        mTvJoinJTime = (TextView) findViewById(R.id.tv_join_j_time);
        mLlName = (LinearLayout) findViewById(R.id.ll_name);
        mLlNickname = (LinearLayout) findViewById(R.id.ll_nickname);
        mTvNickname = (TextView) findViewById(R.id.tv_nickname);
        mLlClass = (LinearLayout) findViewById(R.id.ll_class);
        mTvClass = (TextView) findViewById(R.id.tv_class);
        mLlWelcome = (LinearLayout) findViewById(R.id.ll_welcome);
        mTvWelNickname = (TextView) findViewById(R.id.tv_wel_nickname);


        mBtConfirm = (Button) findViewById(R.id.bt_confirm);
        mClRoot = (RelativeLayout) findViewById(R.id.cl_root);
        mTvStar00 = (TextView) findViewById(R.id.tv_star_00);
        mTvStar01 = (TextView) findViewById(R.id.tv_star_01);
        mLlNamePinyin = (LinearLayout) findViewById(R.id.ll_name_pinyin);
        mTvStar01Pinyin = (TextView) findViewById(R.id.tv_star_01_pinyin);
        mTvNamePinyin = (TextView) findViewById(R.id.tv_name_pinyin);
        mVPinyinLine = (View) findViewById(R.id.v_pinyin_line);
        mTvStar02 = (TextView) findViewById(R.id.tv_star_02);
        mTvStar03 = (TextView) findViewById(R.id.tv_star_03);
        mTvStar04 = (TextView) findViewById(R.id.tv_star_04);
        mTvStar05 = (TextView) findViewById(R.id.tv_star_05);
        mTvStar06 = (TextView) findViewById(R.id.tv_star_06);
        mTvStar07 = (TextView) findViewById(R.id.tv_star_07);
        mTvStar08 = (TextView) findViewById(R.id.tv_star_08);
        mTvStar09 = (TextView) findViewById(R.id.tv_star_09);
        mTvStar10 = (TextView) findViewById(R.id.tv_star_10);
        mTvStar11 = (TextView) findViewById(R.id.tv_star_11);
        mTvStar12 = (TextView) findViewById(R.id.tv_star_12);
        mTvStar13 = (TextView) findViewById(R.id.tv_star_13);
        mTvStar14 = (TextView) findViewById(R.id.tv_star_14);
        mTvStar15 = (TextView) findViewById(R.id.tv_star_15);
        mTvStar16 = (TextView) findViewById(R.id.tv_star_16);
    }

    private boolean inputFinish() {

        if (isTextEmpty(mTvNickname)) {
            ToastUtil.show("???????????????");
            return false;
        }

        if (isTextEmpty(mTvName)) {
            ToastUtil.show("?????????????????????");
            return false;
        }

        if (isTextEmpty(mTvNamePinyin)) {
            ToastUtil.show("?????????????????????");
            return false;
        }

        if (isTextEmpty(mTvBirthday)) {
            ToastUtil.show("??????????????????");
            return false;
        }

        if (isTextEmpty(mTvSex)) {
            ToastUtil.show("???????????????");
            return false;
        }
        if (isTextEmpty(mTvAddress)) {
            ToastUtil.show("???????????????");
            return false;
        }

        if (isTextEmpty(mTvNationality)) {
            ToastUtil.show("???????????????");
            return false;
        }

        if (isTextEmpty(mTvMobile)) {
            ToastUtil.show("??????????????????");
            return false;
        }

        if (isTextEmpty(mTvSchooling)) {
            ToastUtil.show("???????????????");
            return false;
        }

        if (isTextEmpty(mTvChildren)) {
            ToastUtil.show("?????????????????????");
            return false;
        }

        if (isTextEmpty(mTvPoliticCountenance)) {
            ToastUtil.show("?????????????????????");
            return false;
        }

        if (isTextEmpty(mTvJoinJTime)) {
            ToastUtil.show("??????????????????????????????");
            return false;
        }

        if (isTextEmpty(mTvClass)) {
            ToastUtil.show("???????????????");
            return false;
        }

        if (isTextEmpty(mTvLevel)) {
            ToastUtil.show("???????????????");
            return false;
        }


        if (EmptyUtil.isEmpty(mHobby)) {
            ToastUtil.show("?????????????????????");
            return false;
        }

        if (EmptyUtil.isEmpty(mSupport)) {
            ToastUtil.show("????????????????????????");
            return false;
        }

        return true;
    }

    private boolean isTextEmpty(TextView textView) {
        if (textView == null) {
            return true;
        }
        return TextUtils.isEmpty(textView.getText().toString());
    }

    @Override
    public void initData() {

        mFirstInputMsg = getIntent().getBooleanExtra(KEY_DATA, false);
        if (mFirstInputMsg) {
            mLlWelcome.setVisibility(View.VISIBLE);
            mBtConfirm.setVisibility(View.VISIBLE);
            if (UserManager.getInstance().isLogin()) {
                mTvWelNickname.setText(UserManager.getInstance().getUser().getVisibleName());
            }
            mTitleView.getLlBack().setVisibility(View.GONE);
            mBtConfirm.setOnClickListener(v -> {
                if (inputFinish()) {
                    mFirstInputMsg = false;
                    UserManager.getInstance().login();
                    SetPasswordActivity.startActivity(mContext, SetPasswordActivity.SET_FIRST);
                    finish();
                }
            });
            int[] ids = new int[]{R.id.tv_star_00, R.id.tv_star_01, R.id.tv_star_02, R.id.tv_star_03,
                    R.id.tv_star_04, R.id.tv_star_05, R.id.tv_star_06, R.id.tv_star_07,
                    R.id.tv_star_08, R.id.tv_star_09, R.id.tv_star_10, R.id.tv_star_11,
                    R.id.tv_star_12, R.id.tv_star_13, R.id.tv_star_14, R.id.tv_star_15,
                    R.id.tv_star_16};
            for (int id : ids) {
                findViewById(id).setVisibility(View.VISIBLE);
            }
            initOrganization();

        }


        // ?????????????????????
        FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(mContext);
        // flexDirection ?????????????????????????????????????????????????????????????????? LinearLayout ??? vertical ??? horizontal???
        flexboxLayoutManager.setFlexDirection(FlexDirection.ROW);
        // ??????????????????????????????????????????
        // flexWrap ??????????????? Flex ??? LinearLayout ?????????????????????????????????????????????flexWrap?????????????????????????????????
        flexboxLayoutManager.setFlexWrap(FlexWrap.WRAP);
        // ?????????????????????
        // justifyContent ???????????????????????????????????????????????????
        flexboxLayoutManager.setJustifyContent(JustifyContent.FLEX_START);//???????????????????????????

        // ????????????
        mRvInfoHobby.setLayoutManager(flexboxLayoutManager);
        hobbyAdapter = new InfoLikeAdapter(null);
        hobbyAdapter.setOnRvItemListener(new OnRvItemListener<PickerData>() {
            @Override
            public void onItemClick(List<PickerData> list, int position) {
                if (position == hobbyAdapter.getItemCount() - 1) {
                    createLikeDialog();
                }
            }
        });
        mRvInfoHobby.setAdapter(hobbyAdapter);


        // ?????????????????????
        FlexboxLayoutManager flexboxLayoutManager02 = new FlexboxLayoutManager(mContext);
        // flexDirection ?????????????????????????????????????????????????????????????????? LinearLayout ??? vertical ??? horizontal???
        flexboxLayoutManager02.setFlexDirection(FlexDirection.ROW);
        // ??????????????????????????????????????????
        // flexWrap ??????????????? Flex ??? LinearLayout ?????????????????????????????????????????????flexWrap?????????????????????????????????
        flexboxLayoutManager02.setFlexWrap(FlexWrap.WRAP);
        // ?????????????????????
        // justifyContent ???????????????????????????????????????????????????
        flexboxLayoutManager02.setJustifyContent(JustifyContent.FLEX_START);//???????????????????????????
        // ???????????????
        mRvInfoLike.setLayoutManager(flexboxLayoutManager02);
        List<PickerData> likeList = new ArrayList<>();
        infoLikeAdapter = new InfoLikeAdapter(likeList);
        infoLikeAdapter.setOnRvItemListener(new OnRvItemListener<PickerData>() {
            @Override
            public void onItemClick(List<PickerData> list, int position) {
                if (position == infoLikeAdapter.getItemCount() - 1) {
                    createNeedDialog();
                }

            }
        });
        mRvInfoLike.setAdapter(infoLikeAdapter);

        if (UserManager.getInstance().isLogin()) {
            mTvTeam.setText(UserManager.getInstance().getUser().getOrganListStr());
        }
        // ????????????
        mLlTeam.setOnClickListener(v -> {
            startActivity(new Intent(mContext, SelectOrganizationActivity.class));
        });
        // ??????
        mLlAvatar.setOnClickListener(v -> {
            showAlbumSelect();
        });
        // ??????
        mLlName.setOnClickListener(v -> {
            EditInfoActivity.startActivity(mContext, EditInfoActivity.EditType.NAME, mTvName.getText().toString());
        });
        // ????????????
        mLlNamePinyin.setOnClickListener(v -> {
            String name = mTvName.getText().toString();
            String namePinyin = "";
            if (!TextUtils.isEmpty(name)) {
                namePinyin = PinYinUtil.getPinYin(name);
            }
            if (TextUtils.isEmpty(namePinyin)) {
                namePinyin = "";
            } else {
                namePinyin = namePinyin.replaceAll(" ", "");
            }

            EditInfoActivity.startActivity(mContext, EditInfoActivity.EditType.NAME_PINYIN, namePinyin);
        });
        // ??????
        mLlNickname.setOnClickListener(v -> {
            EditInfoActivity.startActivity(mContext, EditInfoActivity.EditType.NICKNAME, mTvNickname.getText().toString());
        });
        // ???????????????
        mLlMobile.setOnClickListener(v -> {
            startActivityForResult(new Intent(mContext, BindPhoneActivity.class), BIND_CODE);
        });
        // ??????
        mLlClass.setOnClickListener(v -> {
            EditInfoActivity.startActivity(mContext, EditInfoActivity.EditType.CLASSES, mTvClass.getText().toString());
        });
        // ????????????
        mLlBirthday.setOnClickListener(v -> {
            DialogRobotFactory.createAddressBirthDay(mContext, findViewById(R.id.cl_root), new OnTimeSelectListener() {
                @Override
                public void onTimeSelect(Date date, View v) {
                    mTvBirthday.setText(HourUtil.getYYMMDD(date));
                    onUpdateInfo("birth", mTvBirthday.getText().toString());
                }
            });
        });
        // ????????????
        mLlAddress.setOnClickListener(v -> {
            DialogRobotFactory.createAddressDialog(mContext, findViewById(R.id.cl_root), new OnOptionsSelectListener() {
                @Override
                public void onOptionsSelect(int options1, int options2, int options3, View v) {
                    mTvAddress.setText(AreaUtil.getOp1().get(options1).getPickerViewText()
                            + " - " + AreaUtil.getOp2().get(options1).get(options2).getPickerViewText());

                    onUpdateInfo("native", mTvAddress.getText().toString());
                }
            });
        });

        mLlSex.setOnClickListener(v -> {
            DialogRobotFactory.createSelectDialog(mContext, findViewById(R.id.cl_root), new OnOptionsSelectListener() {
                @Override
                public void onOptionsSelect(int options1, int options2, int options3, View v) {
                    mTvSex.setText(PickerData.Factory.getSex().get(options1).getPickerViewText());
                    onUpdateInfo("sex", PickerData.Factory.getSex().get(options1).getType());
                }
            }, "????????????", PickerData.Factory.getSex());
        });

        mLlLevel.setOnClickListener(v -> {
            createLevelDialog();
        });

        mLlNationality.setOnClickListener(v -> {
            DialogRobotFactory.createSelectDialog(mContext, findViewById(R.id.cl_root), new OnOptionsSelectListener() {
                @Override
                public void onOptionsSelect(int options1, int options2, int options3, View v) {
                    mTvNationality.setText(PickerData.Factory.getNation().get(options1).getPickerViewText());
                    onUpdateInfo("nation", mTvNationality.getText().toString());
                }
            }, "????????????", PickerData.Factory.getNation());
        });

        mLlSchooling.setOnClickListener(v -> {
            DialogRobotFactory.createSelectDialog(mContext, findViewById(R.id.cl_root), new OnOptionsSelectListener() {
                @Override
                public void onOptionsSelect(int options1, int options2, int options3, View v) {
                    mTvSchooling.setText(PickerData.Factory.getSchool().get(options1).getPickerViewText());
                    onUpdateInfo("education", PickerData.Factory.getSchool().get(options1).getType());
                }
            }, "????????????", PickerData.Factory.getSchool());
        });

        mLlJoinJTime.setOnClickListener(v -> {
            DialogRobotFactory.createAddressBirthDay(mContext, findViewById(R.id.cl_root), new OnTimeSelectListener() {
                @Override
                public void onTimeSelect(Date date, View v) {
                    mTvJoinJTime.setText(HourUtil.getYYMMDD(date));
                    onUpdateInfo("join_time", mTvJoinJTime.getText().toString());
                }
            });
        });

        mLlMarriage.setOnClickListener(v -> {
            DialogRobotFactory.createSelectDialog(mContext, findViewById(R.id.cl_root), new OnOptionsSelectListener() {
                @Override
                public void onOptionsSelect(int options1, int options2, int options3, View v) {
                    mTvMarriage.setText(PickerData.Factory.getMarriage().get(options1).getPickerViewText());
                    onUpdateInfo("marital_status", PickerData.Factory.getMarriage().get(options1).getType());
                }
            }, "??????????????????", PickerData.Factory.getMarriage());
        });

        mLlChildren.setOnClickListener(v -> {
            DialogRobotFactory.createSelectDialog(mContext, findViewById(R.id.cl_root), new OnOptionsSelectListener() {
                @Override
                public void onOptionsSelect(int options1, int options2, int options3, View v) {
                    mTvChildren.setText(PickerData.Factory.getSon().get(options1).getPickerViewText());
                    onUpdateInfo("child_status", PickerData.Factory.getSon().get(options1).getType());
                }
            }, "??????????????????", PickerData.Factory.getSon());
        });

        mLlPoliticCountenance.setOnClickListener(v -> {
            DialogRobotFactory.createSelectDialog(mContext, findViewById(R.id.cl_root), new OnOptionsSelectListener() {
                @Override
                public void onOptionsSelect(int options1, int options2, int options3, View v) {
                    mTvPoliticCountenance.setText(PickerData.Factory.getFace().get(options1).getPickerViewText());
                    onUpdateInfo("political_outlook", PickerData.Factory.getFace().get(options1).getType());
                }
            }, "??????????????????", PickerData.Factory.getFace());
        });


        if (UserManager.getInstance().isLogin()) {
            setUserInfo(UserManager.getInstance().getUser().getUserInfo());
        }

        // ??????
        mPresenter.onGetUserInfo();

    }

    private void createLikeDialog() {


        List<PickerData> list = PickerData.Factory.getLike();
        if (!EmptyUtil.isEmpty(mHobby)) {
            for (PickerData pickerData : list) {
                if (mHobby.contains(pickerData.getPickerViewText())) {
                    pickerData.setCheck(true);
                }
            }

            for (String custom : mHobby) {
                boolean isCustom = true;
                if (custom == null) {
                    continue;
                }
                for (PickerData data : list) {
                    if (custom.equals(data.getPickerViewText())) {
                        isCustom = false;
                        break;
                    }
                }
                if (isCustom) {
                    list.add(new PickerData(custom, -1));
                }
            }
        }

        DialogRobotFactory.createSelectCustomText(this, "????????????", list, new DialogRobotFactory.OnViewSelectCallBack() {

            private BaseAdapter baseAdapter;

            @Override
            public void callBack(BaseAdapter baseAdapter) {
                this.baseAdapter = baseAdapter;
            }

            @Override
            public void onViewClick(BindViewHolder viewHolder, View view, TDialog tDialog) {
                if (view.getId() == R.id.btnCancel) {
                    tDialog.dismiss();
                } else {
                    if (baseAdapter != null) {
                        mHobby.clear();
                        List<PickerData> pickerDatas = baseAdapter.getDataList();
                        List<PickerData> checkedDatas = new ArrayList<>();
                        for (PickerData data : pickerDatas) {
                            if (data.getType() == -1 && !StringUtil.isSpace(data.getPickerViewText())) {
                                mHobby.add(data.getPickerViewText());
                                checkedDatas.add(data);
                            }
                            if (data.getType() != -1 && data.isCheck()) {
                                mHobby.add(data.getPickerViewText());
                                checkedDatas.add(data);
                            }
                        }
                        tDialog.dismiss();
                        hobbyAdapter.setDataListNotify(checkedDatas);
                        LogUtil.d(TAG, "strings = " + GsonUtil.toJson(mHobby));
                        onUpdateInfo("hobby", GsonUtil.toJson(mHobby));
                    }
                }
            }
        });
    }

    private void createLevelDialog() {

        String levelStr = mTvLevel.getText().toString();
        List<PickerData> list = PickerData.Factory.getLevel();
        if (!EmptyUtil.isEmpty(levelStr)) {
            for (PickerData pickerData : list) {
                if (levelStr.equals(pickerData.getPickerViewText())) {
                    pickerData.setCheck(true);
                    break;
                }
            }

            boolean isCustom = true;
            for (PickerData data : list) {
                if (levelStr.equals(data.getPickerViewText())) {
                    isCustom = false;
                    break;
                }
            }
            if (isCustom) {
                list.add(new PickerData(levelStr, -1));
            }

        }

        DialogRobotFactory.createSelectCustomText(this, "??????", list, new DialogRobotFactory.OnViewSelectCallBack() {

            private BaseAdapter baseAdapter;

            @Override
            public void callBack(BaseAdapter baseAdapter) {
                this.baseAdapter = baseAdapter;
                if (baseAdapter instanceof DialogLikeAdapter) {
                    DialogLikeAdapter likeAdapter = (DialogLikeAdapter) baseAdapter;
                    likeAdapter.OpenSingleCheck();
                }
            }

            @Override
            public void onViewClick(BindViewHolder viewHolder, View view, TDialog tDialog) {
                if (view.getId() == R.id.btnCancel) {
                    tDialog.dismiss();
                } else {
                    if (baseAdapter != null) {
                        List<PickerData> pickerDatas = baseAdapter.getDataList();
                        List<PickerData> checkedDatas = new ArrayList<>();
                        for (PickerData data : pickerDatas) {
                            if (data.getType() == -1 && !StringUtil.isSpace(data.getPickerViewText())) {
                                checkedDatas.add(data);
                            }
                            if (data.getType() != -1 && data.isCheck()) {
                                checkedDatas.add(data);
                            }
                        }
                        if (checkedDatas.isEmpty()) {
                            ToastUtil.show("????????????");
                            return;
                        }
                        tDialog.dismiss();
                        mTvLevel.setText(checkedDatas.get(0).getPickerViewText());
                        onUpdateInfo("rank", mTvLevel.getText().toString());
                    }
                }
            }
        });
    }

    private void createNeedDialog() {

        List<PickerData> list = PickerData.Factory.getNeed();
        if (!EmptyUtil.isEmpty(mSupport)) {
            for (PickerData pickerData : list) {
                if (mSupport.contains(pickerData.getPickerViewText())) {
                    pickerData.setCheck(true);
                }
            }

            for (String custom : mSupport) {
                boolean isCustom = true;
                if (custom == null) {
                    continue;
                }
                for (PickerData data : list) {
                    if (custom.equals(data.getPickerViewText())) {
                        isCustom = false;
                        break;
                    }
                }
                if (isCustom) {
                    list.add(new PickerData(custom, -1));
                }
            }
        }

        DialogRobotFactory.createSelectCustomText(this, "???????????????", list, new DialogRobotFactory.OnViewSelectCallBack() {

            private BaseAdapter baseAdapter;

            @Override
            public void callBack(BaseAdapter baseAdapter) {
                this.baseAdapter = baseAdapter;
            }

            @Override
            public void onViewClick(BindViewHolder viewHolder, View view, TDialog tDialog) {
                if (view.getId() == R.id.btnCancel) {
                    tDialog.dismiss();
                } else {
                    if (baseAdapter != null) {
                        mSupport.clear();
                        List<PickerData> pickerDatas = baseAdapter.getDataList();
                        List<PickerData> checkedDatas = new ArrayList<>();
                        for (PickerData data : pickerDatas) {
                            if (data.getType() == -1 && !StringUtil.isSpace(data.getPickerViewText())) {
                                mSupport.add(data.getPickerViewText());
                                checkedDatas.add(data);
                            }
                            if (data.getType() != -1 && data.isCheck()) {
                                mSupport.add(data.getPickerViewText());
                                checkedDatas.add(data);
                            }
                        }
                        tDialog.dismiss();
                        infoLikeAdapter.setDataListNotify(checkedDatas);
                        LogUtil.d(TAG, "strings = " + GsonUtil.toJson(mSupport));
                        onUpdateInfo("support", GsonUtil.toJson(mSupport));
                    }
                }
            }
        });
    }

    @Override
    public UserObserver addObserver() {
        return new UserObserver() {
            @Override
            public void onChanged(UserManager accountManager, User user) {
                mTvTeam.setText(user.getOrganListStr());
                GlideUtil.load(mContext, UserManager.getInstance().getUser().getVisibleAvatar(), mRivAvatar);
            }
        };
    }

    /**
     * ????????????
     */
    private void showAlbumSelect() {
        if (mPopWindow != null) {
            mPopWindow.dismiss();
            mPopWindow = null;
            return;
        }
        View contentView = LayoutInflater.from(InfoActivity.this).inflate(R.layout.popwindow_select_avatar, null);
        mPopWindow = new PopupWindow(contentView);
        mPopWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        // ????????????????????????
        mPopWindow.setOutsideTouchable(true);
        //  mPopWindow.setBackgroundDrawable(new BitmapDrawable()); // ???????????????
        mPopWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView mTvAlbum = contentView.findViewById(R.id.tv_album);
        TextView mTvPreset = contentView.findViewById(R.id.tv_preset);
        // ????????????
        mTvAlbum.setOnClickListener(v -> {
            mPopWindow.dismiss();
            mPopWindow = null;
            showAlbum();
        });
        // ???????????????
        mTvPreset.setOnClickListener(v -> {
            mPopWindow.dismiss();
            mPopWindow = null;
            startActivity(new Intent(mContext, PresetAvatarActivity.class));
        });
        mPopWindow.showAsDropDown(mRivAvatar, -mRivAvatar.getWidth() / 2, 26);
    }

    /**
     * ????????????
     */
    private void showAlbum() {
        //?????????????????????????????????
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())// ??????.PictureMimeType.ofAll()?????????.ofImage()?????????.ofVideo()?????????.ofAudio()
                .imageEngine(new GlideImageEngine())
                .maxSelectNum(1)// ????????????????????????
                .minSelectNum(1) // ??????????????????
                .imageSpanCount(6)// ??????????????????
                .selectionMode(PictureConfig.SINGLE)// ?????? or ??????PictureConfig.MULTIPLE : PictureConfig.SINGLE
                .isPreviewImage(true)// ?????????????????????
                .isCamera(true)// ????????????????????????
                .isZoomAnim(true)// ?????????????????? ???????????? ??????true
                .isCompress(true)// ????????????
                .withAspectRatio(1, 1)// ???????????? ???16:9 3:2 3:4 1:1 ????????????
                .isEnableCrop(true)
                .videoMaxSecond(15) // ?????????15??????????????????
                .videoMinSecond(2) // ?????????2??????????????????
                .rotateEnabled(true) // ???????????????????????????
                .forResult(new OnResultCallbackListener<LocalMedia>() {
                    @Override
                    public void onResult(List<LocalMedia> result) {
                        LogUtil.e("onResult = " + result.size());
                        LogUtil.e("onResult = " + GsonUtil.toJson(result));
                        String avatarUrl = result.get(0).getCompressPath();
                        GlideUtil.load(mContext, avatarUrl, mRivAvatar);
                        uploadAvatar(avatarUrl);
                    }

                    @Override
                    public void onCancel() {
                        ToastUtil.show(getResources().getString(R.string.cancel));
                    }
                });

    }


    private void uploadAvatar(String avatarUrl) {
        LoadingDialog.show(this, "?????????...");
        MultipartBodyFactory factory = MultipartBodyFactory.crete();

        // file
        if (!TextUtils.isEmpty(avatarUrl)) {
            factory.addImage("pic", avatarUrl);
        }
        OkHttpUtil.request(ApiHost.getHost() + "user/pic/upload", factory.build(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                FileUtil.deleteFile(avatarUrl);
                if (EmptyUtil.isEmpty(InfoActivity.this)) {
                    return;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (EmptyUtil.isEmpty(InfoActivity.this)) {
                            return;
                        }
                        LoadingDialog.hide();
                        LogUtil.d(TAG, "??????????????????---------------------------------");
                    }
                });

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                FileUtil.deleteFile(avatarUrl);
                if (EmptyUtil.isEmpty(InfoActivity.this)) {
                    return;
                }
                String json = response.body().string();
                LogUtil.d(TAG, "????????????json --- " + json);
                Type type = new TypeToken<HttpResult<HeadModel>>() {
                }.getType();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (EmptyUtil.isEmpty(InfoActivity.this)) {
                            return;
                        }
                        LoadingDialog.hide();
                        HttpResult<HeadModel> result = GsonUtil.fromJson(json, type);
                        if (result != null && result.isSuccessful()) {
                            LogUtil.d(TAG, "??????????????????---------------------------------");
                            UserManager.getInstance().getUser().getUserInfo().pic = result.getData().pic;
                            UserManager.getInstance().notifyUserInfo();
                        } else {
                            LogUtil.d(TAG, "??????????????????_01---------------------------------");

                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) {
            return;
        }
        switch (requestCode) {
            case 9:
                String phoneNumber = data.getStringExtra(BindPhoneActivity.KEY_RESULT);
                mTvMobile.setText(phoneNumber);
                onUpdateInfo("mobile", phoneNumber);
                UserManager.getInstance().getUser().getUserInfo().mobile = phoneNumber;
                UserManager.getInstance().notifyUserInfo();
                break;
            case 5:
                String accountPinyin = data.getStringExtra(EditInfoActivity.KEY_RESULT);
                mTvNamePinyin.setText(accountPinyin);
                onUpdateInfoAccount("account", accountPinyin);
                break;
            case 2:
                String className = data.getStringExtra(EditInfoActivity.KEY_RESULT);
                mTvClass.setText(className);
                onUpdateInfo("department", className);
                break;
            case 1:
                String name = data.getStringExtra(EditInfoActivity.KEY_RESULT);
                mTvName.setText(name);
                onUpdateInfo("true_name", name);
                UserManager.getInstance().getUser().getUserInfo().trueName = name;
                UserManager.getInstance().notifyUserInfo();
                break;
            case 0:
                String nickName = data.getStringExtra(EditInfoActivity.KEY_RESULT);
                mTvNickname.setText(nickName);
                onUpdateInfo("user_name", nickName);
                UserManager.getInstance().getUser().getUserInfo().userName = nickName;
                UserManager.getInstance().notifyUserInfo();
                break;
        }
    }

    @Override
    public LifecycleProvider getLifecycleProvider() {
        return InfoActivity.this;
    }

    @Override
    public void onSuccess(User.UserInfo userInfo) {
        UserManager.getInstance().getUser().setUserInfo(userInfo);
        setUserInfo(userInfo);
        initDefaultAvatar();
    }


    private void setUserInfo(User.UserInfo userInfo) {
        if (userInfo == null) {
            return;
        }

        if (!TextUtils.isEmpty(userInfo.account)) {
            mLlNamePinyin.setVisibility(View.GONE);
            mVPinyinLine.setVisibility(View.GONE);
        }

        // ????????????
        if (!EmptyUtil.isEmpty(userInfo.hobby)) {
            mHobby.clear();
            mHobby.addAll(userInfo.hobby);
        }

        // ???????????????
        if (!EmptyUtil.isEmpty(userInfo.support)) {
            mSupport.clear();
            mSupport.addAll(userInfo.support);
        }

        GlideUtil.load(mContext, UserManager.getInstance().getUser().getVisibleAvatar(), mRivAvatar);

        mTvNickname.setText(userInfo.userName);
        mTvName.setText(userInfo.trueName);

        mTvBirthday.setText(userInfo.birth);
        mTvSex.setText(PickerData.Factory.getSex(userInfo.sex));

        mTvAddress.setText(userInfo.nativeX);
        mTvNationality.setText(userInfo.nation);

        mTvMobile.setText(userInfo.mobile);
        mTvSchooling.setText(PickerData.Factory.getSchool(userInfo.education));

        mTvMarriage.setText(PickerData.Factory.getMarriage(userInfo.maritalStatus));
        mTvChildren.setText(PickerData.Factory.getSon(userInfo.childStatus));

        mTvJoinJTime.setText(userInfo.joinTime);
        mTvPoliticCountenance.setText(PickerData.Factory.getFace(userInfo.politicalOutlook));

        mTvClass.setText(userInfo.department);
        mTvLevel.setText(userInfo.rank);
        if (!EmptyUtil.isEmpty(mHobby)) {
            List<PickerData> list = new ArrayList<>();
            for (String hobby : mHobby) {
                list.add(new PickerData(hobby));
            }
            hobbyAdapter.setDataListNotify(list);
        }
        if (!EmptyUtil.isEmpty(mSupport)) {
            List<PickerData> list = new ArrayList<>();
            for (String support : mSupport) {
                list.add(new PickerData(support));
            }
            infoLikeAdapter.setDataListNotify(list);
        }


    }

    public void onUpdateInfo(String key, Object value) {

        LoadingDialog.show(this, "?????????...");
        new UserInfoPresenter(new UserInfoPresenter.UserInfoUi<CommonModel>() {
            @Override
            public LifecycleProvider getLifecycleProvider() {
                return InfoActivity.this;
            }

            @Override
            public void onSuccess(CommonModel data) {
                LoadingDialog.hide();
                ToastUtil.show("????????????");
            }

            @Override
            public void onFail(ResponeThrowable throwable) {
                LoadingDialog.hide();
                if ("mobile".equals(key)) {
                    mTvMobile.setText(UserManager.getInstance().getUser().getMobile());
                } else {
                    ToastUtil.show(throwable.getMsg());
                }

            }
        }).onUpdate(NetParams.crete()
                .add("key", key)
                .add("value", value));
    }

    public void onUpdateInfoAccount(String key, Object value) {

        LoadingDialog.show(this, "?????????...");
        new UserInfoPresenter(new UserInfoPresenter.UserInfoUi<AccountPinyinModel>() {
            @Override
            public LifecycleProvider getLifecycleProvider() {
                return InfoActivity.this;
            }

            @Override
            public void onSuccess(AccountPinyinModel data) {
                mTvNamePinyin.setText(String.valueOf(value));
                mLlNamePinyin.setEnabled(false);
                mLlNamePinyin.setAlpha(0.5f);
                UserManager.getInstance().getUser().getUserInfo().account = data.getAccount();
                UserManager.getInstance().notifyUserInfo();
                LoadingDialog.hide();
                ToastUtil.show("????????????");
            }

            @Override
            public void onFail(ResponeThrowable throwable) {
                LoadingDialog.hide();
                mTvNamePinyin.setText("");

            }
        }).onUpdateAccount(NetParams.crete()
                .add("key", key)
                .add("value", value));
    }


    @Override
    public void onFail(ResponeThrowable throwable) {

    }

    /**
     * ??????????????????
     */
    private void initOrganization() {
        new OrganizationPresenter(new OrganizationPresenter.OrganizationUi<OrganizationSelectedModel>() {

            @Override
            public LifecycleProvider getLifecycleProvider() {
                return InfoActivity.this;
            }

            @Override
            public void onSuccess(OrganizationSelectedModel data) {
                if (data != null) {
                    LogUtil.d(TAG, "initOrganization onSuccess ++++++++++++++++++++++ ");
                    ArrayList<OrganizationModel.OrganizationBean> mSelecteds = new ArrayList<>();
                    initSelectedList(mSelecteds, data.getOrganization());
                    CommonParameter.setOrganizationSelected(mSelecteds);
                    CommonParameter.setOrganizationModel(data);
                    if (UserManager.getInstance().isLogin()) {
                        UserManager.getInstance().getUser().setOrganList(mSelecteds);
                        mTvTeam.setText(UserManager.getInstance().getUser().getOrganListStr());
                    }
                }
            }

            @Override
            public void onFail(ResponeThrowable throwable) {
                LogUtil.d(TAG, "initOrganization onFail ++++++++++++++++++++++ ");
            }
        }).onGetJoinedList();
    }

    private void initSelectedList(List<OrganizationModel.OrganizationBean> list, OrganizationSelectedModel.OrganizationBean bean) {
        if (bean != null) {
            OrganizationModel.OrganizationBean oBean = new OrganizationModel.OrganizationBean();
            oBean.setId(bean.getId());
            oBean.setName(bean.getName());
            list.add(oBean);
            if (bean.getChildren() != null) {
                initSelectedList(list, bean.getChildren());
            }
        }
    }


    private void initDefaultAvatar() {
        if (!UserManager.getInstance().isLogin()) {
            return;
        }
        if (!TextUtils.isEmpty(UserManager.getInstance().getUser().getAvatar())) {
            return;
        }
        LogUtil.i(TAG, "initDefaultAvatar+++++++++++++++++++++++++");
        new UserInfoPresenter(new UserInfoPresenter.UserInfoUi<List<PresetAvatarModel>>() {
            @Override
            public LifecycleProvider getLifecycleProvider() {
                return InfoActivity.this;
            }

            @Override
            public void onSuccess(List<PresetAvatarModel> data) {
                if (!EmptyUtil.isEmpty(data)) {
                    onUpdateInfoAvatar(data.get(new Random().nextInt(data.size())).getPic());
                }
                LogUtil.i(TAG, "initDefaultAvatar+++++++++++++++++++++++++onSuccess");
            }

            @Override
            public void onFail(ResponeThrowable throwable) {
                LogUtil.i(TAG, "initDefaultAvatar+++++++++++++++++++++++++onFail");
            }
        }).onPresetAvatarList();
    }

    public void onUpdateInfoAvatar(String value) {
        LogUtil.i(TAG, "onUpdateInfoAvatar+++++++++++++++++++++++++");
        new UserInfoPresenter(new UserInfoPresenter.UserInfoUi<CommonModel>() {
            @Override
            public LifecycleProvider getLifecycleProvider() {
                return InfoActivity.this;
            }

            @Override
            public void onSuccess(CommonModel data) {
                UserManager.getInstance().getUser().getUserInfo().pic = value;
                UserManager.getInstance().notifyUserInfo();
                GlideUtil.load(mContext, value, mRivAvatar);
                LogUtil.i(TAG, "onUpdateInfoAvatar+++++++++++++++++++++++++onSuccess");
            }

            @Override
            public void onFail(ResponeThrowable throwable) {
                LogUtil.i(TAG, "onUpdateInfoAvatar+++++++++++++++++++++++++onFail");
            }
        }).onUpdate(NetParams.crete()
                .add("key", "pic")
                .add("value", value));
    }

}