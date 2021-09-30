package com.thfw.mobileheart;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;

import com.google.android.exoplayer2.Player;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.ToastUtil;
import com.thfw.mobileheart.util.AudioModel;
import com.thfw.mobileheart.util.NotifyHelper;
import com.thfw.robotheart.R;
import com.thfw.mobileheart.util.ExoPlayerFactory;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

/**
 * Author:pengs
 * Date: 2021/8/3 11:46
 * Describe:Todo
 */
public class AudioService extends Service {

    private static final String CHANNEL_ID = "music_channel_id";
    private static final String CMD_KEY = "cmd_key";
    private static final String CHANNEL_NAME = "music_channel_name";
    private static final String CHANNEL_DESCRIPTION = "music_channel_description";
    private static final int NOTIFY_ID = 0x1;
    private RemoteViews notifyLayout;
    private NotifyHelper mNotifyHelper;
    private AudioModel picture;

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    public class MyBinder extends Binder {

        MyBinder() {
            super();
        }

        public AudioService getService() {
            return AudioService.this;
        }

        public void setMusic(final AudioModel picture) {
            if (ExoPlayerFactory.getExoPlayer() == null) {
                return;
            }
            setPicture(picture);
            ExoPlayerFactory.getExoPlayer().addListener(listener);
            if (ExoPlayerFactory.getExoPlayer().getPlayWhenReady()) {
                playMusicState(picture);
            } else {
                pauseMusicState(picture);
            }
        }
    }


    public void setPicture(AudioModel picture) {
        this.picture = picture;
    }

    private Player.Listener listener = new Player.Listener() {


        @Override
        public void onPlayWhenReadyChanged(boolean playWhenReady, int reason) {
            if (playWhenReady) {
                playMusicState(picture);
            } else {
                pauseMusicState(picture);
            }
        }
    };

    private void init() {
        mNotifyHelper = NotifyHelper.getInstance(ToastUtil.getAppContext());
        notifyLayout = new RemoteViews(ToastUtil.getAppContext().getPackageName(), R.layout.audio_nofify_player_view);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.hasExtra(CMD_KEY)) {
            int cmd = intent.getIntExtra(CMD_KEY, 0);
            LogUtil.d("CMD_KEY -> " + cmd);
            if (ExoPlayerFactory.getExoPlayer() != null) {
                switch (cmd) {
                    case AudioModel.Cmd.PAUSE:
                        ExoPlayerFactory.getExoPlayer().pause();
                        break;
                    case AudioModel.Cmd.PLAY:
                        ExoPlayerFactory.getExoPlayer().play();
                        break;
                    case AudioModel.Cmd.LAST:
                        if (ExoPlayerFactory.getExoPlayer().hasPrevious()) {
                            ExoPlayerFactory.getExoPlayer().previous();
                        }
                        break;
                    case AudioModel.Cmd.NEXT:
                        if (ExoPlayerFactory.getExoPlayer().hasNext()) {
                            ExoPlayerFactory.getExoPlayer().next();
                        }
                        break;
                }
                if (ExoPlayerFactory.getExoPlayer().getPlayWhenReady()) {
                    playMusicState(new AudioModel());
                } else {
                    pauseMusicState(new AudioModel());
                }
            }

        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 更改了相关设置，比如notifyLayout布局显示之后，需要重新发送前台通知来更新UI
     * 太坑了，居然是这样的
     * 此外，由于我们是显示成一个播放器，因此通知id，使用固定id，就可以保证每次更新之后是同一个通知。
     *
     * @param picture 设置播放器图标为指定音乐图标,此对象属于MVC开发模式的model层数据
     */
    private void mStartForeground(AudioModel picture) {
        mNotifyHelper.CreateChannel(CHANNEL_ID, CHANNEL_NAME, CHANNEL_DESCRIPTION);
        final Notification notification = mNotifyHelper.createForeNotification(CHANNEL_ID, picture, notifyLayout);
//        NotificationManager manager = (NotificationManager) ToastUtil.getAppContext().getSystemService(NOTIFICATION_SERVICE);
        startForeground(NOTIFY_ID, notification);
    }

    /**
     * 处于播放状态下的音乐应该具有的一些配置
     *
     * @param picture
     */
    private void playMusicState(AudioModel picture) {
        notifyLayout.setOnClickPendingIntent(R.id.iv_music_play, getPending(AudioModel.Cmd.PAUSE));
        notifyLayout.setOnClickPendingIntent(R.id.iv_music_last_song, getPending(AudioModel.Cmd.LAST));
        notifyLayout.setOnClickPendingIntent(R.id.iv_music_next_song, getPending(AudioModel.Cmd.NEXT));
        notifyLayout.setImageViewResource(R.id.iv_music_icon, picture.getPicId());
        notifyLayout.setImageViewResource(R.id.iv_music_play, R.drawable.exo_controls_pause);
        mStartForeground(picture);
    }

    /**
     * 处于暂停状态下的音乐应该具有的一些配置
     *
     * @param picture
     */

    private void pauseMusicState(AudioModel picture) {

        notifyLayout.setOnClickPendingIntent(R.id.iv_music_play, getPending(AudioModel.Cmd.PLAY));
        notifyLayout.setOnClickPendingIntent(R.id.iv_music_last_song, getPending(AudioModel.Cmd.LAST));
        notifyLayout.setOnClickPendingIntent(R.id.iv_music_next_song, getPending(AudioModel.Cmd.NEXT));
        notifyLayout.setImageViewResource(R.id.iv_music_icon, picture.getPicId());
        notifyLayout.setImageViewResource(R.id.iv_music_play, R.drawable.exo_controls_play);
        mStartForeground(picture);
    }

    public PendingIntent getPending(int cmd) {
        Intent intent = new Intent(AudioService.this, AudioService.class)
                .putExtra(CMD_KEY, cmd);
        return PendingIntent.getService(AudioService.this, cmd, intent, FLAG_UPDATE_CURRENT);

    }

}
