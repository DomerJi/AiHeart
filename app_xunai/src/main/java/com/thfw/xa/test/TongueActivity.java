package com.thfw.xa.test;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.thfw.base.utils.EmptyUtil;
import com.thfw.base.utils.FileUtil;
import com.thfw.base.utils.GsonUtil;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.ToastUtil;
import com.thfw.ui.dialog.LoadingDialog;
import com.thfw.ui.utils.GlideUtil;
import com.thfw.ui.widget.TitleView;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class TongueActivity extends AppCompatActivity {

    private TitleView mTitleView;
    private ImageView mIvTongueFront;
    private ImageView mIvTongueBack;
    private Button mBtRequest;
    private TextView mTvInfo;
    private Context mContext;
    private static final String TAG = "TongueActivity";
    HashMap<String, String> mParams = new HashMap<>();
    private String KEY_FRONT = "front";
    private String KEY_BACK = "back";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_tongue);
        initView();
    }

    private void initView() {
        mTitleView = (TitleView) findViewById(R.id.titleView);
        mIvTongueFront = (ImageView) findViewById(R.id.iv_tongue_front);
        mIvTongueBack = (ImageView) findViewById(R.id.iv_tongue_back);
        mBtRequest = (Button) findViewById(R.id.bt_request);
        mTvInfo = (TextView) findViewById(R.id.tv_info);

        mIvTongueBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlbum(KEY_BACK);
            }
        });

        mIvTongueFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlbum(KEY_FRONT);
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
                        if (key.equals(KEY_FRONT)) {
                            GlideUtil.load(mContext, avatarUrl, mIvTongueFront);
                        } else {
                            GlideUtil.load(mContext, avatarUrl, mIvTongueBack);
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
                if (EmptyUtil.isEmpty(TongueActivity.this)) {
                    return;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (EmptyUtil.isEmpty(TongueActivity.this)) {
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
                if (EmptyUtil.isEmpty(TongueActivity.this)) {
                    return;
                }
                String json = response.body().string();
                LogUtil.d(TAG, "头像上传json --- " + json);
                Type type = new TypeToken<HttpResult<HeadModel>>() {
                }.getType();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (EmptyUtil.isEmpty(TongueActivity.this)) {
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
        mTvInfo.setText(mTvInfo.getText().toString() + string + "\n");
        mTvInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copy(mContext, mTvInfo.getText().toString());
            }
        });
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