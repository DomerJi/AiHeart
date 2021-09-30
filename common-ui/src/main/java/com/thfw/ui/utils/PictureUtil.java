package com.thfw.ui.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Build;

import com.thfw.base.utils.LogUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created By jishuaipeng on 2021/6/4
 */
public class PictureUtil {

    private static Context context;

    public static void init(Context context) {
        PictureUtil.context = context.getApplicationContext();
    }

    /**
     * 获得视频某一帧的缩略图
     *
     * @param videoPath 视频地址
     * @param timeUs    微秒，注意这里是微秒 1秒 = 1 * 1000 * 1000 微秒
     * @return 截取的图片
     */
    public static Bitmap getVideoThumnail(String videoPath, long timeUs) {
        Bitmap bitmap = null;
        MediaMetadataRetriever mediaMetadataRetriever = null;
        try {
            mediaMetadataRetriever = new MediaMetadataRetriever();
            if (Build.VERSION.SDK_INT >= 14) {
                mediaMetadataRetriever.setDataSource(videoPath, new HashMap<String, String>());
            } else {
                mediaMetadataRetriever.setDataSource(videoPath);
            }
            bitmap = mediaMetadataRetriever.getFrameAtTime();
        } catch (Exception e) {
            LogUtil.d("getVideoThumnail -> " + e.getMessage());
        } finally {
            if (mediaMetadataRetriever != null) {
                mediaMetadataRetriever.release();
            }
        }
        return bitmap;
    }

    public static File saveImage(Bitmap bmp) {
        File appDir = new File(context.getCacheDir(), "videoAlbum");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = "temp_0" + ".jpg";
        File file = new File(appDir, fileName);

        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            int inSampleSize = 100; // 默认质量
            int minLen = Math.min(bmp.getHeight(), bmp.getWidth()); // 原图的最小边长
            if (minLen > 400) { // 如果原始图像的最小边长大于400
                float ratio = (float) minLen / 400.0f; // 计算像素压缩比例
                inSampleSize = (int) (100 / ratio);
            }
            LogUtil.d("inSampleSize -> " + inSampleSize);
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, inSampleSize, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }


}
