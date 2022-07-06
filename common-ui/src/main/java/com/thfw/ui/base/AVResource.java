package com.baidu.iov.dueros.videos.core.svideo.core;

import android.net.Uri;

import com.baidu.iov.dueros.videos.base.utils.LogUtil;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.dash.DashChunkSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.FileDataSource;

/**
 * 表示资源的类，由资源位置描述
 */
public class VideoResource {

    public static final String TAG = "VideoResource";
    private static final String UA = "haokan-codelab";

    /**
     * 本地资源
     */
    public static final int RESOURCE_LOCAL = 0;
    /**
     * 普通网络资源
     */
    public static final int RESOURCE_INTERNET_NORMAL = 1;
    /**
     * DASH资源
     */
    public static final int RESOURCE_INTERNET_DASH = 2;
    /**
     * HLS资源
     */
    public static final int RESOURCE_INTERNET_HLS = 3;
    /**
     * SmoothStreaming资源
     */
    public static final int RESOURCE_INTERNET_SMOOTH_STREAMING = 4;

    /**
     * 资源存储位置
     */
    private Uri mUri;

    /**
     * 资源类型, 取值如下：<br/>
     * RESOURCE_LOCAL、<br/>
     * RESOURCE_INTERNET_NORMAL、<br/>
     * RESOURCE_INTERNET_DASH、<br/>
     * RESOURCE_INTERNET_HLS、<br/>
     * RESOURCE_INTERNET_SMOOTH_STREAMING <br/>
     */
    private int mResourceType;

    public Uri getUri() {
        return mUri;
    }

    public void setUri(Uri uri) {
        mUri = uri;
    }

    public boolean isLocalFlag() {
        return mResourceType == RESOURCE_LOCAL;
    }

    public int getResourceType() {
        return mResourceType;
    }

    public void setResourceType(int resourceType) {
        mResourceType = resourceType;
    }

    public MediaSource getMediaSource() {
        switch (getResourceType()) {
            case VideoResource.RESOURCE_INTERNET_DASH:
                return buildDashMediaSource(getUri());
            case VideoResource.RESOURCE_INTERNET_HLS:
                return buildHlsMediaSource(getUri());
            case VideoResource.RESOURCE_INTERNET_SMOOTH_STREAMING:
                return buildSsMediaSource(getUri());
            case VideoResource.RESOURCE_INTERNET_NORMAL:
                return buildHttpMediaSource(getUri());
            case VideoResource.RESOURCE_LOCAL: // 本地资源
            default:
                return buildLocalMediaSource(getUri());
        }
    }

    private MediaSource buildDashMediaSource(Uri uri) {
        DataSource.Factory manifestDataSourceFactory = new DefaultHttpDataSourceFactory(UA);

        DefaultHttpDataSourceFactory httpDataSourceFactory = new DefaultHttpDataSourceFactory(UA,
                new DefaultBandwidthMeter());
        DashChunkSource.Factory dashChunkSourceFactory = new DefaultDashChunkSource.Factory(httpDataSourceFactory);

        DashMediaSource.Factory factory = new DashMediaSource.Factory(dashChunkSourceFactory,
                manifestDataSourceFactory);

        return factory.createMediaSource(uri);
    }

    private MediaSource buildHlsMediaSource(Uri uri) {
        // Create a data source factory.
        DataSource.Factory dataSourceFactory = new DefaultHttpDataSourceFactory(UA);
        // Create a HLS media source pointing to a playlist uri.
        HlsMediaSource.Factory factory = new HlsMediaSource.Factory(dataSourceFactory);
        HlsMediaSource hlsMediaSource = factory.createMediaSource(uri);

        return hlsMediaSource;
    }

    private MediaSource buildSsMediaSource(Uri uri) {
        // Create a data source factory.
        DataSource.Factory dataSourceFactory = new DefaultHttpDataSourceFactory(UA);
        // Create a SmoothStreaming media source pointing to a manifest uri.
        SsMediaSource.Factory factory = new SsMediaSource.Factory(dataSourceFactory);
        MediaSource mediaSource = factory.createMediaSource(uri);

        return mediaSource;
    }

    private MediaSource buildLocalMediaSource(Uri uri) {
        DataSpec dataSpec = new DataSpec(uri);
        final FileDataSource fileDataSource = new FileDataSource();
        try {
            fileDataSource.open(dataSpec);
        } catch (FileDataSource.FileDataSourceException e) {
            LogUtil.e(TAG, e.getMessage(), e);
            return null;
        }

        DataSource.Factory factory = new DataSource.Factory() {
            @Override
            public DataSource createDataSource() {
                return fileDataSource;
            }
        };

        MediaSource videoSource = new ExtractorMediaSource(fileDataSource.getUri(),
                factory,
                new DefaultExtractorsFactory(),
                null,
                null);

        return videoSource;
    }

    private MediaSource buildHttpMediaSource(Uri uri) {
        DefaultHttpDataSourceFactory httpDataSourceFactory = new DefaultHttpDataSourceFactory(UA);
        ExtractorMediaSource.Factory factory = new ExtractorMediaSource.Factory(httpDataSourceFactory);

        return factory.createMediaSource(uri);
    }

}
