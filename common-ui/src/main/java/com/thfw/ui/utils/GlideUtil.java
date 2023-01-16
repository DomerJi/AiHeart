package com.thfw.ui.utils;

import static com.bumptech.glide.load.resource.bitmap.VideoDecoder.FRAME_OPTION;

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
import com.thfw.base.ContextApp;
import com.thfw.base.utils.BlurTransformation;
import com.thfw.base.utils.Util;
import com.thfw.ui.R;

import java.security.MessageDigest;

/**
 * Author:pengs
 * Date: 2021/7/22 11:12
 * Describe:Glide图片加载
 */
public class GlideUtil {


    private static RequestOptions requestOptions;
    private static RequestOptions requestOptionsSize;
    private static RequestOptions requestThumbnail;

    public static RequestOptions getRequestOptions() {
        if (requestOptions != null) {
            return requestOptions;
        }
        if (ContextApp.getDeviceType() == ContextApp.DeviceType.ROBOT) {
            requestOptions = RequestOptions.placeholderOf(R.drawable.glide_placeholder).error(R.drawable.glide_error).fallback(R.drawable.glide_fallback);
        } else {
            requestOptions = RequestOptions.placeholderOf(R.drawable.glide_placeholder_phone).error(R.drawable.glide_error_phone).fallback(R.drawable.glide_fallback_phone);
        }
        return requestOptions;
    }

    public static RequestOptions getRequestOptionsSize() {
        if (requestOptionsSize != null) {
            return requestOptionsSize;
        }
        if (ContextApp.getDeviceType() == ContextApp.DeviceType.ROBOT) {
            requestOptionsSize = RequestOptions.placeholderOf(R.drawable.glide_placeholder).error(R.drawable.glide_error).fallback(R.drawable.glide_fallback);
        } else {
            requestOptionsSize = RequestOptions.placeholderOf(R.drawable.glide_placeholder_phone).error(R.drawable.glide_error_phone).fallback(R.drawable.glide_fallback_phone);
        }
        return requestOptionsSize;
    }

    private static RequestOptions getRequestOptionsThumbnail() {
        if (requestThumbnail != null) {
            return requestThumbnail;
        }
        if (ContextApp.getDeviceType() == ContextApp.DeviceType.ROBOT) {
            requestThumbnail = RequestOptions.placeholderOf(R.drawable.glide_placeholder).error(R.drawable.glide_error).fallback(R.drawable.glide_fallback).override(Util.dipToPx(160, ContextApp.get()), Util.dipToPx(90, ContextApp.get()));
        } else {
            requestThumbnail = RequestOptions.placeholderOf(R.drawable.glide_placeholder_phone).error(R.drawable.ic_error_default).fallback(R.drawable.ic_error_default).override(Util.dipToPx(90, ContextApp.get()));
        }
        return requestThumbnail;
    }

    public static void loadThumbnail(Context mContext, String url, ImageView imageView) {
        Glide.with(mContext).load(url).apply(getRequestOptionsThumbnail())
//                .transition(DrawableTransitionOptions.withCrossFade())
                .centerCrop().into(imageView);
    }

    public static void loadBlur(Context mContext, String url, ImageView imageView) {
        Glide.with(mContext).load(url).apply(RequestOptions.bitmapTransform(new BlurTransformation(mContext, 4, 3))).into(imageView);
    }


    public static void load(Context mContext, String url, ImageView imageView) {
        Glide.with(mContext).load(url).apply(getRequestOptions())
//                .transition(DrawableTransitionOptions.withCrossFade())
                .centerCrop().into(imageView);
    }

    public static void load(Context mContext, String url, ImageView imageView, int width, int height) {
        Glide.with(mContext).load(url).apply(getRequestOptionsSize().override(width, height))
//                .transition(DrawableTransitionOptions.withCrossFade())
                .centerCrop().into(imageView);
    }

    public static void load(Context mContext, Object url, ImageView imageView) {
        Glide.with(mContext).load(url).apply(getRequestOptions())
//                .transition(DrawableTransitionOptions.withCrossFade())
                .centerCrop().into(imageView);
    }

    public static void load(Context mContext, Object url, int placeholder, ImageView imageView) {
        Glide.with(mContext).load(url).apply(RequestOptions.placeholderOf(placeholder).error(placeholder).fallback(placeholder)).centerCrop().into(imageView);
    }

    public static void load(Context mContext, int resId, ImageView imageView) {
        Glide.with(mContext).load(resId).apply(getRequestOptions())
//                .transition(DrawableTransitionOptions.withCrossFade())
                .centerCrop().into(imageView);
    }

    public static void load(Activity mContext, String url, ImageView imageView) {
        Glide.with(mContext).load(url).apply(getRequestOptions())
//                .transition(DrawableTransitionOptions.withCrossFade())
                .centerCrop().into(imageView);
    }

    public static void load(Fragment mContext, String url, ImageView imageView) {
        Glide.with(mContext).load(url).apply(getRequestOptions())
//                .transition(DrawableTransitionOptions.withCrossFade())
                .centerCrop().into(imageView);
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
