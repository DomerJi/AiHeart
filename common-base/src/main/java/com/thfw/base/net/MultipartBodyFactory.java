package com.thfw.base.net;

import android.text.TextUtils;

import com.thfw.base.utils.EmptyUtil;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * 公共参数创建类
 * Created By jishuaipeng on 2021/5/24
 */
public class MultipartBodyFactory {

    private final MultipartBody.Builder builder;

    private MultipartBodyFactory() {
        builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
    }

    public static MultipartBodyFactory crete() {
        return new MultipartBodyFactory();
    }

    public MultipartBodyFactory addImage(String key, String imageUrl) {
        if (!TextUtils.isEmpty(imageUrl)) {
            File file = new File(imageUrl);
            if (file == null || !file.exists()) {
                // todo 暂时用于修改店铺信息和商品图片修改地方
                addString(key, imageUrl);
                return this;
            }
            builder.addFormDataPart(key, file.getName(), RequestBody.create(MediaType.parse("image/*"), file));
        }
        return this;
    }

    public MultipartBodyFactory addImage(String key, List<String> imageUrls) {
        if (!EmptyUtil.isEmpty(imageUrls)) {
            for (String imageUrl : imageUrls) {
                File file = new File(imageUrl);
                if (file == null || !file.exists()) {
                    // todo 暂时用于修改店铺信息
                    addString(key, imageUrl);
                    return this;
                }
                builder.addFormDataPart(key, file.getName(), RequestBody.create(MediaType.parse("image/*"), file));
            }
        }
        return this;
    }

    public MultipartBodyFactory addVideo(String key, String videoUrl) {
        if (!TextUtils.isEmpty(videoUrl)) {
            File file = new File(videoUrl);
            if (file == null || !file.exists()) {
                return this;
            }
            builder.addFormDataPart(key, file.getName(), RequestBody.create(MediaType.parse("application/octet-stream"), file));
        }
        return this;
    }

    public MultipartBodyFactory addString(String key, String value) {
        builder.addFormDataPart(key, value);
        return this;
    }

    public MultipartBody build() {
        return builder.build();
    }

}
