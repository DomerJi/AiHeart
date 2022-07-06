package com.thfw.ui.base;

import android.net.Uri;
import android.text.TextUtils;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.dash.DashChunkSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.thfw.base.ContextApp;
import com.thfw.base.models.VideoModel;

/**
 * 表示资源的类，由资源位置描述
 */
public class AVResource {


    public static final String TAG = "AVResource";

    public static void setSource(ExoPlayer exoPlayer, String url, int type) {

        Object source = createSource(url, type);
        if (source instanceof MediaItem) {
            exoPlayer.setMediaItem((MediaItem) source);
        } else if (source instanceof MediaSource) {
            exoPlayer.setMediaSource((MediaSource) source);
        }
    }

    public static void setSource(ExoPlayer exoPlayer, VideoModel videoModel) {
        if (!TextUtils.isEmpty(videoModel.getM3u8())) {
            setSource(exoPlayer, videoModel.getM3u8(), RESOURCE_INTERNET_HLS);
//            setSource(exoPlayer, "https://media.soulbuddy.cn/videoM3u8/dfae17643b3d08213a830321fd960ca8/dfae17643b3d08213a830321fd960ca8.m3u8", RESOURCE_INTERNET_HLS);
//            setSource(exoPlayer, "https://bitdash-a.akamaihd.net/content/sintel/hls/playlist.m3u8", RESOURCE_INTERNET_HLS);
        } else {
            setSource(exoPlayer, videoModel.getUrl(), RESOURCE_INTERNET_NORMAL);
        }
    }

    public static Object createSource(String url, int type) {
        switch (type) {
            case RESOURCE_INTERNET_DASH:
                return buildDashMediaSource(Uri.parse(url));
            case RESOURCE_INTERNET_HLS:
//                return new MediaItem.Builder()
//                        .setUri(Uri.parse(url))
//                        .setMimeType(MimeTypes.APPLICATION_M3U8)
//                        .build();

                // Use the explicit MIME type to build an HLS media item.
                return buildHlsMediaSource(Uri.parse(url));
            case RESOURCE_INTERNET_SMOOTH_STREAMING:
                return buildSsMediaSource(Uri.parse(url));
            default:
                return MediaItem.fromUri(Uri.parse(url));
        }
    }


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

//    public MediaSource getMediaSource() {
//        switch (getResourceType()) {
//            case AVResource.RESOURCE_INTERNET_DASH:
//                return buildDashMediaSource(getUri());
//            case AVResource.RESOURCE_INTERNET_HLS:
//                return buildHlsMediaSource(getUri());
//            case AVResource.RESOURCE_INTERNET_SMOOTH_STREAMING:
//                return buildSsMediaSource(getUri());
//            case AVResource.RESOURCE_INTERNET_NORMAL:
//                return buildHttpMediaSource(getUri());
//            case AVResource.RESOURCE_LOCAL: // 本地资源
//            default:
//                return buildLocalMediaSource(getUri());
//        }
//    }

    public static final String getUserAgent() {
        return com.google.android.exoplayer2.util.Util.getUserAgent(ContextApp.get(), "AI咨询师");
    }


    public static MediaSource buildHlsMediaSource(Uri uri) {
        DefaultBandwidthMeter mDefaultBandwidthMeter = new DefaultBandwidthMeter
                .Builder(ContextApp.get())
                .build();
        DefaultHttpDataSourceFactory httpDataSourceFactory = new DefaultHttpDataSourceFactory(getUserAgent(),
                null, 15000, 15000, true);

        DefaultDataSourceFactory upstreamFactory = new DefaultDataSourceFactory(ContextApp.get(), mDefaultBandwidthMeter, httpDataSourceFactory);
        // Create a HLS media source pointing to a playlist uri.
        HlsMediaSource.Factory factory = new HlsMediaSource.Factory(upstreamFactory);
        HlsMediaSource hlsMediaSource = factory.createMediaSource(uri);

        return hlsMediaSource;
    }

    public static MediaSource buildDashMediaSource(Uri uri) {
        DataSource.Factory manifestDataSourceFactory = new DefaultHttpDataSourceFactory(getUserAgent());

        DefaultHttpDataSourceFactory httpDataSourceFactory = new DefaultHttpDataSourceFactory(getUserAgent(),
                new DefaultBandwidthMeter());
        DashChunkSource.Factory dashChunkSourceFactory = new DefaultDashChunkSource.Factory(httpDataSourceFactory);

        DashMediaSource.Factory factory = new DashMediaSource.Factory(dashChunkSourceFactory,
                manifestDataSourceFactory);

        return factory.createMediaSource(uri);
    }


    public static MediaSource buildSsMediaSource(Uri uri) {
        // Create a data source factory.
        DataSource.Factory dataSourceFactory = new DefaultHttpDataSourceFactory(getUserAgent());
        // Create a SmoothStreaming media source pointing to a manifest uri.
        SsMediaSource.Factory factory = new SsMediaSource.Factory(dataSourceFactory);
        MediaSource mediaSource = factory.createMediaSource(uri);

        return mediaSource;
    }

//    private MediaSource buildLocalMediaSource(Uri uri) {
//        DataSpec dataSpec = new DataSpec(uri);
//        final FileDataSource fileDataSource = new FileDataSource();
//        try {
//            fileDataSource.open(dataSpec);
//        } catch (FileDataSource.FileDataSourceException e) {
//            LogUtil.e(TAG, e.getMessage(), e);
//            return null;
//        }
//
//        DataSource.Factory factory = new DataSource.Factory() {
//            @Override
//            public DataSource createDataSource() {
//                return fileDataSource;
//            }
//        };
//
//        MediaSource videoSource = new ExtractorMediaSource(fileDataSource.getUri(),
//                factory,
//                new DefaultExtractorsFactory(),
//                null,
//                null);
//
//        return videoSource;
//    }
//
//    private MediaSource buildHttpMediaSource(Uri uri) {
//        DefaultHttpDataSourceFactory httpDataSourceFactory = new DefaultHttpDataSourceFactory(UA);
//        ExtractorMediaSource.Factory factory = new ExtractorMediaSource.Factory(httpDataSourceFactory);
//
//        return factory.createMediaSource(uri);
//    }


}
