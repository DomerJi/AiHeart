package com.thfw.mobileheart.adapter;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.thfw.base.utils.ToastUtil;
import com.thfw.mobileheart.R;
import com.thfw.base.models.VideoModel;
import com.thfw.mobileheart.util.ExoPlayerFactory;
import com.thfw.ui.utils.GlideUtil;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

import static android.view.View.VISIBLE;
import static androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_DRAGGING;
import static androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE;
import static androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_SETTLING;

public class VideoListAdapter extends BaseAdapter<VideoModel, VideoListAdapter.VideoHolder> {

    PlayerListener mPlayerListener;
    Handler handler;
    private int playPosition = 0;

    public VideoListAdapter(List<VideoModel> dataList) {
        super(dataList);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull @NotNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull @NotNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    case SCROLL_STATE_IDLE:
                        int playPosition = layoutManager.findFirstCompletelyVisibleItemPosition();
                        if (playPosition != RecyclerView.NO_POSITION) {
                            notifyDataSetChanged(playPosition);
                        }
                        break;
                    case SCROLL_STATE_DRAGGING:
                        break;
                    case SCROLL_STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onScrolled(@NonNull @NotNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    private void notifyDataSetChanged(int position) {
        if (position != playPosition) {
            notifyItemChanged(playPosition);
            playPosition = position;
            notifyItemChanged(position);
        }
    }

    @Override
    public VideoHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new VideoHolder(LayoutInflater.from(mContext).inflate(R.layout.item_video_play, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull VideoHolder holder, int position) {
        holder.mTvTitle.setText(mDataList.get(position).title);

        if (position == playPosition) {
            holder.mPlayerView.setVisibility(VISIBLE);
            onBottomProgressBar(holder.mPbBar, false);

            if (holder.mPlayerView.getPlayer() != null && holder.mPlayerView.getPlayer().isPlaying()) {
                return;
            }
            holder.mPlayerView.setControllerVisibilityListener(new PlayerControlView.VisibilityListener() {
                @Override
                public void onVisibilityChange(int visibility) {
                    onBottomProgressBar(holder.mPbBar, visibility == VISIBLE);
                }
            });

            ExoPlayer player = ExoPlayerFactory.getExoPlayer();

            holder.mPbLoading.setVisibility(VISIBLE);
            if (mPlayerListener != null) {
                player.removeListener(mPlayerListener);
            }
            mPlayerListener = new PlayerListener();
            mPlayerListener.setPbBar(holder);
            player.addListener(mPlayerListener);
            holder.mPlayerView.setPlayer(player);
            MediaItem mediaItem = MediaItem.fromUri(mDataList.get(position).getUrl());
//            MediaSource mediaSource = new DefaultMediaSourceFactory(mContext).createMediaSource(mediaItem);
            player.setMediaItem(mediaItem);
            player.prepare();
            player.play();
        } else {
            holder.mPbBar.setProgress(0);
            holder.mPbBar.setVisibility(View.GONE);
            holder.mPbLoading.setVisibility(View.GONE);
            recycleHolder(holder);
        }

        GlideUtil.loadVideoScreenshot(mContext, mDataList.get(position).getUrl(), holder.mIvBg, 100);

    }

    private void onBottomProgressBar(ProgressBar mPbBar, boolean hide) {
        mPbBar.setVisibility(hide ? View.GONE : View.VISIBLE);
        if (!hide) {
            ExoPlayer player = ExoPlayerFactory.getExoPlayer();
            if (handler != null) {
                handler.removeCallbacksAndMessages(null);
                handler = null;
            }
            handler = new Handler(Looper.myLooper()) {

                @Override
                public void handleMessage(@NonNull Message msg) {
                    super.handleMessage(msg);
                    int progress = (int) (player.getContentPosition() * 1000 / player.getContentDuration());
                    mPbBar.setProgress(progress);
                    handler.sendEmptyMessageDelayed(0, 1000);
                }

            };

            handler.sendEmptyMessageDelayed(0, 0);


        } else {
            if (handler != null) {
                handler.removeCallbacksAndMessages(null);
                handler = null;
            }
        }
    }

    @Override
    public void onViewRecycled(@NonNull @NotNull VideoHolder holder) {
        recycleHolder(holder);
        super.onViewRecycled(holder);
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull @NotNull VideoHolder holder) {
        recycleHolder(holder);
        super.onViewDetachedFromWindow(holder);
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull @NotNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        ExoPlayerFactory.release();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
    }

    public void recycleHolder(VideoHolder holder) {
        holder.mPlayerView.setVisibility(View.INVISIBLE);
        holder.mPlayerView.setPlayer(null);
//        if (holder.mFlVideo.getChildCount() > 0) {
//            if (holder.mFlVideo.getChildAt(0) instanceof PlayerView) {
//                PlayerView playerView = (PlayerView) holder.mFlVideo.getChildAt(0);
//                if (playerView.getPlayer() != null) {
//                    ExoPlayerFactory.release();
//                }
//            }
//            holder.mFlVideo.removeAllViews();
//        }
    }

    public class PlayerListener implements Player.Listener {
        private VideoHolder videoHolder;

        public void setPbBar(VideoHolder videoHolder) {
            this.videoHolder = videoHolder;
        }

        public ProgressBar getLoading() {
            if (videoHolder != null) {
                return videoHolder.mPbLoading;
            }
            return null;
        }

        @Override
        public void onPlaybackStateChanged(int state) {
            ProgressBar mPbBar = getLoading();
            if (mPbBar == null) {
                return;
            }

            if (state == Player.STATE_READY || state == Player.STATE_ENDED) {
                mPbBar.setVisibility(View.GONE);
            } else {
                mPbBar.setVisibility(VISIBLE);
            }
            if ("error".equals(String.valueOf(mPbBar.getTag()))) {
                mPbBar.setVisibility(View.GONE);
                mPbBar.setTag("");
            }

        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {
            ProgressBar mPbBar = getLoading();
            if (mPbBar == null) {
                return;
            }
            mPbBar.setTag("error");
            mPbBar.setVisibility(View.GONE);

            if (error.type == ExoPlaybackException.TYPE_SOURCE) {
                IOException cause = error.getSourceException();
                if (cause instanceof HttpDataSource.HttpDataSourceException) {
                    // An HTTP error occurred.
                    HttpDataSource.HttpDataSourceException httpError = (HttpDataSource.HttpDataSourceException) cause;
                    // This is the request for which the error occurred.
                    DataSpec requestDataSpec = httpError.dataSpec;
                    // It's possible to find out more about the error both by casting and by
                    // querying the cause.
                    if (httpError instanceof HttpDataSource.InvalidResponseCodeException) {
                        // Cast to InvalidResponseCodeException and retrieve the response code,
                        // message and headers.
                        ToastUtil.show("视频资源错误");
                    } else {
                        // Try calling httpError.getCause() to retrieve the underlying cause,
                        // although note that it may be null.

                        if (httpError.getCause() instanceof UnknownHostException) {
                            ToastUtil.show("网络异常，请检查网络");
                        } else {
                            ToastUtil.show("未知错误" + httpError.getCause());
                        }
                    }
                }
            } else if (error.type == ExoPlaybackException.TYPE_RENDERER) {
                ToastUtil.show("渲染错误 - TYPE_UNEXPECTED");
            } else if (error.type == ExoPlaybackException.TYPE_UNEXPECTED) {
                ToastUtil.show("未知错误 - TYPE_UNEXPECTED");
            } else if (error.type == ExoPlaybackException.TYPE_REMOTE) {
                ToastUtil.show("网络错误");
            }

        }
    }

    public class VideoHolder extends RecyclerView.ViewHolder {

        private TextView mTvTitle;
        private FrameLayout mFlVideo;
        private Button mBtnPlay;
        private ProgressBar mPbBar;
        private ProgressBar mPbLoading;
        private PlayerView mPlayerView;
        private ImageView mIvBg;

        public VideoHolder(@NonNull View itemView) {
            super(itemView);
            initView(itemView);
            mBtnPlay.setOnClickListener(v -> {
                notifyDataSetChanged(getBindingAdapterPosition());
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (mOnRvItemListener != null) {
                        mOnRvItemListener.onItemClick(mDataList, getBindingAdapterPosition());
                    }
                    return true;
                }
            });

        }

        private void initView(View itemView) {
            mTvTitle = itemView.findViewById(R.id.tv_title);
            mFlVideo = itemView.findViewById(R.id.fl_video);
            mPbBar = itemView.findViewById(R.id.pb_bottom);
            mBtnPlay = itemView.findViewById(R.id.btn_play);
            mPlayerView = itemView.findViewById(R.id.mPlayerView);
            mIvBg = itemView.findViewById(R.id.iv_bg);
            mPbLoading = itemView.findViewById(R.id.pb_loading);
        }
    }
}
