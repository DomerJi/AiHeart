package com.thfw.mobileheart.activity.settings;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.common.reflect.TypeToken;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.listener.OnResultCallbackListener;
import com.thfw.base.base.IPresenter;
import com.thfw.base.base.SpeechToAction;
import com.thfw.base.face.MyTextWatcher;
import com.thfw.base.models.CommonModel;
import com.thfw.base.models.HeadModel;
import com.thfw.base.net.ApiHost;
import com.thfw.base.net.HttpResult;
import com.thfw.base.net.MultipartBodyFactory;
import com.thfw.base.net.OkHttpUtil;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.base.utils.FileUtil;
import com.thfw.base.utils.GsonUtil;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.RegularUtil;
import com.thfw.base.utils.ToastUtil;
import com.thfw.mobileheart.R;
import com.thfw.mobileheart.activity.BaseActivity;
import com.thfw.mobileheart.lhxk.LhXkHelper;
import com.thfw.mobileheart.util.GlideImageEngine;
import com.thfw.ui.dialog.LoadingDialog;
import com.thfw.ui.widget.TitleView;
import com.thfw.user.login.UserManager;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MeWillHelpBackActivity extends BaseActivity {

    private com.thfw.ui.widget.TitleView mTitleView;
    private android.widget.TextView mTvContentTitle;
    private android.widget.RelativeLayout mRlContent;
    private android.widget.ImageView mIvAdd;
    private android.widget.EditText mEtHelpbackContent;
    private android.widget.TextView mTvMobileTitle;
    private android.widget.EditText mEtMobile;
    private android.widget.Button mBtSubmit;
    private String avatarUrl;

    @Override
    public int getContentView() {
        return R.layout.activity_me_will_help_back;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initView() {

        mTitleView = (TitleView) findViewById(R.id.titleView);
        mTvContentTitle = (TextView) findViewById(R.id.tv_content_title);
        mRlContent = (RelativeLayout) findViewById(R.id.rl_content);
        mIvAdd = (ImageView) findViewById(R.id.iv_add);
        mEtHelpbackContent = (EditText) findViewById(R.id.et_helpback_content);
        mTvMobileTitle = (TextView) findViewById(R.id.tv_mobile_title);
        mEtMobile = (EditText) findViewById(R.id.et_mobile);
        mBtSubmit = (Button) findViewById(R.id.bt_submit);
        mBtSubmit.setEnabled(false);

        mEtMobile.setText(UserManager.getInstance().getUser().getMobile());
        mIvAdd.setOnClickListener(v -> {
            showAlbum();
        });
        mEtHelpbackContent.addTextChangedListener(new MyTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkSubmit();
            }
        });
        mEtMobile.addTextChangedListener(new MyTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkSubmit();
            }
        });

        if (LogUtil.isLogEnabled()) {
            mTitleView.setRightText("历史反馈");
            mTitleView.setRightOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(mContext, MeHelpBackActivity.class));
                }
            });
        } else {
            mTitleView.setRightText("");
        }
    }

    private void checkSubmit() {
        boolean canContent = !EmptyUtil.isEmpty(mEtHelpbackContent.getText().toString()) && mEtHelpbackContent.getText().toString().length() > 10;
        mBtSubmit.setEnabled(RegularUtil.isPhone(mEtMobile.getText().toString()) && canContent);
    }

    @Override
    public void initData() {
        mBtSubmit.setOnClickListener(v -> {
            uploadHelpBack(avatarUrl, mEtHelpbackContent.getText().toString(), mEtMobile.getText().toString().trim());
        });
    }

    /**
     * 选择图片
     */
    private void showAlbum() {

        //参数很多，根据需要添加
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                .loadImageEngine(new GlideImageEngine())
                .maxSelectNum(1)// 最大图片选择数量
                .minSelectNum(1) // 最小选择数量
                .imageSpanCount(4)// 每行显示个数
                .selectionMode(PictureConfig.SINGLE)// 多选 or 单选PictureConfig.MULTIPLE : PictureConfig.SINGLE
                .previewImage(true)// 是否可预览图片
                .isCamera(true)// 是否显示拍照按钮
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                .compress(true)// 是否压缩
                .enableCrop(true)
                .videoMaxSecond(15) // 过滤掉15秒以上的视频
                .videoMinSecond(2) // 过滤掉2秒以下的视频
                .rotateEnabled(true) // 裁剪是否可旋转图片
                .forResult(new OnResultCallbackListener<LocalMedia>() {
                    @Override
                    public void onResult(List<LocalMedia> result) {
                        LogUtil.e("onResult = " + result.size());
                        LogUtil.e("onResult = " + GsonUtil.toJson(result));
                        avatarUrl = result.get(0).getCompressPath();
                        mIvAdd.setImageBitmap(BitmapFactory.decodeFile(avatarUrl));
                        checkSubmit();
                    }

                    @Override
                    public void onCancel() {
                        ToastUtil.show(getResources().getString(R.string.cancel));
                    }
                });

    }


    private void uploadHelpBack(String avatarUrl, String content, String phone) {
        LoadingDialog.show(this, "反馈中...");
        MultipartBodyFactory factory = MultipartBodyFactory.crete();

        // file
        if (!TextUtils.isEmpty(avatarUrl)) {
            factory.addImage("pic", avatarUrl);
        }
        factory.addString("content", content);
        factory.addString("mobile", phone);
        // 手机端-用户反馈-用户反馈上传
        OkHttpUtil.request(ApiHost.getHost() + "feedback_uploads", factory.build(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                FileUtil.deleteFile(avatarUrl);
                if (EmptyUtil.isEmpty(MeWillHelpBackActivity.this)) {
                    return;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (EmptyUtil.isEmpty(MeWillHelpBackActivity.this)) {
                            return;
                        }
                        LoadingDialog.hide();
                        LogUtil.d(TAG, "反馈上传失败---------------------------------");
                    }
                });

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                FileUtil.deleteFile(avatarUrl);
                if (EmptyUtil.isEmpty(MeWillHelpBackActivity.this)) {
                    return;
                }
                String json = response.body().string();
                LogUtil.d(TAG, "用户反馈上传json --- " + json);
                Type type = new TypeToken<HttpResult<HeadModel>>() {
                }.getType();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (EmptyUtil.isEmpty(MeWillHelpBackActivity.this)) {
                            return;
                        }
                        LoadingDialog.hide();
                        HttpResult<CommonModel> result = GsonUtil.fromJson(json, type);
                        if (result != null && result.isSuccessful()) {
                            LogUtil.d(TAG, "用户反馈上传成功---------------------------------");
                            ToastUtil.show("反馈成功");
                            finish();
                        } else {
                            LogUtil.d(TAG, "用户反馈上传失败_01---------------------------------");

                        }
                    }
                });
            }
        });
    }

    @Override
    protected void initLocalVoice(int type) {
        super.initLocalVoice(type);
        LhXkHelper.putAction(MeWillHelpBackActivity.class, new SpeechToAction("提交反馈", () -> {
            mBtSubmit.performClick();
        }));
    }
}