package com.thfw.ui.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.request.RequestOptions;
import com.thfw.ui.R;

import java.security.MessageDigest;

import static com.bumptech.glide.load.resource.bitmap.VideoDecoder.FRAME_OPTION;

/**
 * Author:pengs
 * Date: 2021/7/22 11:12
 * Describe:Glide图片加载
 */
public class GlideUtil {


    private static final RequestOptions requestOptions = RequestOptions
            .placeholderOf(R.drawable.glide_placeholder)
            .error(R.drawable.glide_error)
            .fallback(R.drawable.glide_fallback);

    public static RequestOptions getRequestOptions() {
        return requestOptions;
    }

    public static void load(Context mContext, String url, ImageView imageView) {
        Glide.with(mContext)
                .load(url)
                .apply(getRequestOptions())
                .centerCrop()
                .into(imageView);
    }

    public static void load(Context mContext, int  resId, ImageView imageView) {
        Glide.with(mContext)
                .load(resId)
                .apply(getRequestOptions())
                .centerCrop()
                .into(imageView);
    }

    public static void load(Activity mContext, String url, ImageView imageView) {
        Glide.with(mContext)
                .load(url)
                .apply(getRequestOptions())
                .centerCrop()
                .into(imageView);
    }

    public static void load(Fragment mContext, String url, ImageView imageView) {
        Glide.with(mContext)
                .load(url)
                .apply(getRequestOptions())
                .centerCrop()
                .into(imageView);
    }

    public static void loadVideoScreenshot(final Context context, String uri, ImageView imageView, long frameTimeMicros) {
        RequestOptions requestOptions = RequestOptions.frameOf(frameTimeMicros);
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        requestOptions.set(FRAME_OPTION, MediaMetadataRetriever.OPTION_CLOSEST);
        requestOptions.transform(new BitmapTransformation() {
            @Override
            protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
                return toTransform;
            }

            @Override
            public void updateDiskCacheKey(MessageDigest messageDigest) {
                try {
                    messageDigest.update((context.getPackageName() + "RotateTransform").getBytes("utf-8"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        Glide.with(context).load(uri).apply(requestOptions).into(imageView);
    }

}
