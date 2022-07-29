package com.thfw.xa.test;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.common.reflect.TypeToken;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.listener.OnResultCallbackListener;
import com.thfw.base.models.HeadModel;
import com.thfw.base.net.ApiHost;
import com.thfw.base.net.HttpResult;
import com.thfw.base.net.MultipartBodyFactory;
import com.thfw.base.net.NetParams;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.base.utils.FileUtil;
import com.thfw.base.utils.GsonUtil;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.SharePreferenceUtil;
import com.thfw.base.utils.ToastUtil;
import com.thfw.ui.dialog.LoadingDialog;
import com.thfw.ui.utils.GlideUtil;
import com.thfw.ui.widget.TitleView;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FaceActivity extends AppCompatActivity {

    private FaceIdModel mFaceIdModel;
    private TitleView mTitleView;
    private Button mBtRequest;
    private TextView mTvInfo;
    private Context mContext;
    private static final String TAG = "FaceActivity";
    HashMap<String, String> mParams = new HashMap<>();
    private String KEY_FACE = "face_";
    private String KEY_FACE_LEFT = "face_left";
    private String KEY_FACE_RIGHT = "face_right";
    private ImageView mIvFace;
    private ImageView mIvFaceLeft;
    private ImageView mIvFaceRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_face);
        initView();
        Type type = new TypeToken<HashMap<String, String>>() {
        }.getType();
        HashMap<String, String> hashMap = SharePreferenceUtil.getObject("FaceKey", type);
        if (hashMap != null) {
            mParams.putAll(hashMap);
            addInfo("mParams : " + GsonUtil.toJson(mParams));
        }

        String faceimg = mParams.get(KEY_FACE);
        String faceleft = mParams.get(KEY_FACE_LEFT);
        String faceright = mParams.get(KEY_FACE_RIGHT);
        if (!TextUtils.isEmpty(faceimg)) {
            GlideUtil.load(mContext, faceimg, mIvFace);
        }

        if (!TextUtils.isEmpty(faceleft)) {
            GlideUtil.load(mContext, faceleft, mIvFaceLeft);
        }
        if (!TextUtils.isEmpty(faceright)) {
            GlideUtil.load(mContext, faceright, mIvFaceRight);
        }

    }

    private void initView() {
        mTitleView = (TitleView) findViewById(R.id.titleView);
        mBtRequest = (Button) findViewById(R.id.bt_request);
        mTvInfo = (TextView) findViewById(R.id.tv_info);
        mIvFace = (ImageView) findViewById(R.id.iv_face);
        mIvFaceLeft = (ImageView) findViewById(R.id.iv_face_left);
        mIvFaceRight = (ImageView) findViewById(R.id.iv_face_right);
        mIvFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlbum(KEY_FACE);
            }
        });
        mIvFaceLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlbum(KEY_FACE_LEFT);
            }
        });

        mIvFaceRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlbum(KEY_FACE_RIGHT);
            }
        });

        mBtRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String face = mParams.get(KEY_FACE);
                String faceLeft = mParams.get(KEY_FACE_LEFT);
                String faceRight = mParams.get(KEY_FACE_RIGHT);
                if (TextUtils.isEmpty(face)) {
                    ToastUtil.show("参数为空");
                    return;
                }
                NetParams params = NetParams.crete().add("faceImgPath", face);

                if (!TextUtils.isEmpty(faceLeft)) {
                    params.add("faceLeftImgPath", faceLeft);
                }

                if (!TextUtils.isEmpty(faceRight)) {
                    params.add("faceRightImgPath", faceRight);
                }
                RequestBody requestBody = XunAiOkHttp.getBody(params);
                SharePreferenceUtil.setString("FaceKey", GsonUtil.toJson(mParams));
                XunAiOkHttp.request2(XunAiOkHttp.XUNAI_URL + XunAiOkHttp.XUNAI_FACE,
                        requestBody, new Callback() {


                            @Override
                            public void onFailure(Call call, IOException e) {
                                addInfo("onFailure");
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                String json = response.body().string();
                                addInfo(json);
                                mFaceIdModel = GsonUtil.fromJson(json, FaceIdModel.class);
                                anywer();
                            }
                        });
            }
        });

    }

    private void anywer() {
        /**
         * faceId	String	是	环节一响应中的data数据，用于关联后续请求
         * answer	Array	是
         * ┗subjectId	Integer	是	题目ID
         * ┗answerCode	Integer	是	根据题型不同，具体代表数值或用户输入项；
         */
        if (mFaceIdModel == null) {
            return;
        }
        NetParams netParams = NetParams.crete()
                .add("faceId", mFaceIdModel.getData().getFaceId());
        List<FaceIdModel.DataBean.SubjectBean> subjectBeans = mFaceIdModel.getData().getSubject();
        List<HashMap<String, Object>> answer = new ArrayList<>();
        for (FaceIdModel.DataBean.SubjectBean subjectBean : subjectBeans) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("subjectId", subjectBean.getSubjectId());
            if (subjectBean.getAnswer().get(0).getAnswerTitle().contains("年龄")) {
                subjectBean.getAnswer().get(0).setAnswerCode(30);
            }
            map.put("answerCode", subjectBean.getAnswer().get(0).getAnswerCode());
            answer.add(map);
        }
        netParams.add("answer", answer);
        addInfo(GsonUtil.toJson(netParams));
        RequestBody requestBody = XunAiOkHttp.getBody(netParams);
        SharePreferenceUtil.setString("FaceKey", GsonUtil.toJson(mParams));
        XunAiOkHttp.request2(XunAiOkHttp.XUNAI_URL + XunAiOkHttp.XUNAI_FACE2,
                requestBody, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        addInfo("onFailure");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String json = response.body().string();
                        Log.d(TAG, "json = " + json);
                        mFaceIdModel = GsonUtil.fromJson(json, FaceIdModel.class);
                        addInfo(json);
                        if (mFaceIdModel.getData().isFinalResult()) {
                            ToastUtil.show("结束");
                        } else {
                            anywer();
                        }
                    }
                });
    }

    /**
     * 选择图片
     */
    private void showAlbum(String key) {
        //参数很多，根据需要添加
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                .imageEngine(new GlideImageEngine())
                .maxSelectNum(1)// 最大图片选择数量
                .minSelectNum(1) // 最小选择数量
                .imageSpanCount(4)// 每行显示个数
                .selectionMode(PictureConfig.SINGLE)// 多选 or 单选PictureConfig.MULTIPLE : PictureConfig.SINGLE
                .isPreviewImage(true)// 是否可预览图片
                .isCamera(true)// 是否显示拍照按钮
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                .isCompress(true)// 是否压缩
                .withAspectRatio(1, 1)// 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                .isEnableCrop(true)
                .videoMaxSecond(15) // 过滤掉15秒以上的视频
                .videoMinSecond(2) // 过滤掉2秒以下的视频
                .rotateEnabled(true) // 裁剪是否可旋转图片
                .forResult(new OnResultCallbackListener<LocalMedia>() {
                    @Override
                    public void onResult(List<LocalMedia> result) {
                        LogUtil.e("onResult = " + result.size());
                        LogUtil.e("onResult = " + GsonUtil.toJson(result));
                        String avatarUrl = result.get(0).getCompressPath();
                        if (key.equals(KEY_FACE)) {
                            GlideUtil.load(mContext, avatarUrl, mIvFace);
                        } else if (key.equals(KEY_FACE_LEFT)) {
                            GlideUtil.load(mContext, avatarUrl, mIvFaceLeft);
                        } else {
                            GlideUtil.load(mContext, avatarUrl, mIvFaceRight);
                        }
                        uploadAvatar(avatarUrl, key);
                    }

                    @Override
                    public void onCancel() {
                        ToastUtil.show(getResources().getString(R.string.cancel));
                    }
                });

    }

    private void uploadAvatar(String avatarUrl, String key) {
        LoadingDialog.show(this, "保存中...");
        MultipartBodyFactory factory = MultipartBodyFactory.crete();

        // file
        if (!TextUtils.isEmpty(avatarUrl)) {
            factory.addImage("pic", avatarUrl);
        }
        XunAiOkHttp.request(ApiHost.getHost() + "user/pic/upload", factory.build(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                FileUtil.deleteFile(avatarUrl);
                if (EmptyUtil.isEmpty(FaceActivity.this)) {
                    return;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (EmptyUtil.isEmpty(FaceActivity.this)) {
                            return;
                        }
                        LoadingDialog.hide();
                        LogUtil.d(TAG, "头像上传失败---------------------------------");
                    }
                });

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                FileUtil.deleteFile(avatarUrl);
                if (EmptyUtil.isEmpty(FaceActivity.this)) {
                    return;
                }
                String json = response.body().string();
                LogUtil.d(TAG, "头像上传json --- " + json);
                Type type = new TypeToken<HttpResult<HeadModel>>() {
                }.getType();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (EmptyUtil.isEmpty(FaceActivity.this)) {
                            return;
                        }
                        LoadingDialog.hide();
                        HttpResult<HeadModel> result = GsonUtil.fromJson(json, type);
                        if (result != null && result.isSuccessful()) {
                            LogUtil.d(TAG, "头像上传成功---------------------------------");
                            mParams.put(key, result.getData().pic);
                            addInfo("key：" + key + " ; pic：" + result.getData().pic);
                        } else {
                            LogUtil.d(TAG, "头像上传失败_01---------------------------------");

                        }
                    }
                });
            }
        });
    }


    private void addInfo(String string) {
        if (ToastUtil.isMainThread()) {
            mTvInfo.setText(mTvInfo.getText().toString() + string + "\n");
            mTvInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    copy(mContext, mTvInfo.getText().toString());
                }
            });
        } else {
            runOnUiThread(() -> {
                addInfo(string);
            });
        }
    }

    /**
     * 复制到剪贴板
     *
     * @param copyText
     */
    public static void copy(Context context, String copyText) {
        // 获取剪贴板管理器：
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        // 创建普通字符型ClipData
        ClipData mClipData = ClipData.newPlainText("Label", copyText);
        // 将ClipData内容放到系统剪贴板里。
        cm.setPrimaryClip(mClipData);
        ToastUtil.show("已复制到剪贴板");
    }
}