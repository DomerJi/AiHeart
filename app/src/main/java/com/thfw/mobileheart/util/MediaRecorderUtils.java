//package com.thfw.mobileheart.util;
//
//import android.media.MediaRecorder;
//import android.os.Handler;
//import android.util.Log;
//import android.widget.ImageView;
//
//import com.thfw.mobileheart.R;
//
//import java.io.File;
//
///**
// * 录音工具类
// */
//public class MediaRecorderUtils {
//
//    private static MediaRecorder recorder;
//    static MediaRecorderUtils mediaRecorderUtils;
//    static ImageView mimageView;
//    private String path;
//
//    /**
//     * 获得单例对象，传入一个显示音量大小的imageview对象，如不需要显示可以传null
//     */
//    public static MediaRecorderUtils getInstence(ImageView imageView) {
//        if (mediaRecorderUtils == null) {
//            mediaRecorderUtils = new MediaRecorderUtils();
//        }
//        mimageView = imageView;
//        return mediaRecorderUtils;
//    }
//
//    /**
//     * 获得音频路径
//     */
//    public String getPath() {
//        return path;
//    }
//
//    /**
//     * 初始化
//     */
//    private void init() {
//
//        recorder = new MediaRecorder();// new出MediaRecorder对象
//        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//        // 设置MediaRecorder的音频源为麦克风
//        recorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
//        // 设置MediaRecorder录制的音频格式
//        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//        // 设置MediaRecorder录制音频的编码为amr.
//        File file = new File(Utils.IMAGE_SDCARD_MADER);
//        if (!file.exists()) {
//            file.mkdirs();
//        }
//        path = Utils.IMAGE_SDCARD_MADER + Utils.getVoiceFileName() + "stock.amr";
//        recorder.setOutputFile(path);
//        // 设置录制好的音频文件保存路径
//        try {
//            recorder.prepare();// 准备录制
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * 开始录音
//     */
//    public void MediaRecorderStart() {
//        init();
//        try {
//            recorder.start();
//            flag = true;
//            if (mimageView != null) {
//                updateMicStatus();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.e("chen", "录制失败");
//        }
//    }
//
//    /**
//     * 停止录音
//     */
//    public void MediaRecorderStop() {
//        try {
//            recorder.stop();
//            recorder.release(); //释放资源
//            flag = false;
//            mimageView = null;
//            recorder = null;
//        } catch (Exception e) {
//            e.toString();
//        }
//
//    }
//
//    /**
//     * 删除已录制的音频
//     */
//    public void MediaRecorderDelete() {
//        File file = new File(path);
//        if (file.isFile()) {
//            file.delete();
//        }
//        file.exists();
//    }
//
//    ;
//
//    private final Handler mHandler = new Handler();
//    private Runnable mUpdateMicStatusTimer = new Runnable() {
//        public void run() {
//            updateMicStatus();
//        }
//    };
//    private int BASE = 1;
//    private int SPACE = 1000;// 间隔取样时间
//    private boolean flag = true;
//
//    /**
//     * 更新话筒状态
//     */
//    private void updateMicStatus() {
//        if (recorder != null) {
//            double ratio = (double) recorder.getMaxAmplitude() / BASE;
//            double db = 0;// 分贝
//            if (ratio > 1) {
//                db = 20 * Math.log10(ratio);
//            }
//            int i = (int) db / 10;
//            switch (i) {
//                case 1:
//                    mimageView.setImageResource(R.drawable.rc_ic_volume_1);
//                    break;
//                case 2:
//                    mimageView.setImageResource(R.drawable.rc_ic_volume_2);
//                    break;
//                case 3:
//                    mimageView.setImageResource(R.drawable.rc_ic_volume_3);
//                    break;
//                case 4:
//                    mimageView.setImageResource(R.drawable.rc_ic_volume_4);
//                    break;
//                case 5:
//                    mimageView.setImageResource(R.drawable.rc_ic_volume_5);
//                    break;
//                case 6:
//                    mimageView.setImageResource(R.drawable.rc_ic_volume_6);
//                    break;
//                case 7:
//                    mimageView.setImageResource(R.drawable.rc_ic_volume_7);
//                    break;
//                case 8:
//                    mimageView.setImageResource(R.drawable.rc_ic_volume_8);
//                    break;
//            }
//            if (flag) {
//                mHandler.postDelayed(mUpdateMicStatusTimer, SPACE);
//            }
//        }
//    }
//
//}
