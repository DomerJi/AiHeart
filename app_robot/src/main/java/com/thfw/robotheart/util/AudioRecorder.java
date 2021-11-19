package com.thfw.robotheart.util;//package com.thfw.mobileheart.util;
//
//import android.media.AudioFormat;
//import android.media.AudioRecord;
//import android.media.MediaRecorder;
//import android.os.Environment;
//
//import java.io.File;
//import java.io.FileOutputStream;
//
///**
// * Author:pengs
// * Date: 2021/8/4 11:53
// * Describe:Todo
// */
//public class AudioRecorder {
//
//    private AudioRecord mAudioRecord;
//    private FileOutputStream mFileOutputStream;
//    private File mAudioRecordFile;
//    private long stopRecorderTime;
//    private long startRecorderTime;
//
//    /**
//     * 开始录音
//     *
//     * @return
//     */
//    private boolean dostart() {
//        try {
//            //记录开始录音时间
//            startRecorderTime = System.currentTimeMillis();
//
//            //创建录音文件
//            mAudioRecordFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
//                    "/recorderdemo/" + System.currentTimeMillis() + ".pcm");
//            if (!mAudioRecordFile.getParentFile().exists()) {
//                mAudioRecordFile.getParentFile().mkdirs();
//                mAudioRecordFile.createNewFile();
//            }
//
//            //创建文件输出流
//            mFileOutputStream = new FileOutputStream(mAudioRecordFile);
//            //配置AudioRecord
//            int audioSource = MediaRecorder.AudioSource.MIC;
//            //所有android系统都支持
//            int sampleRate = 44100;
//            //单声道输入
//            int channelConfig = AudioFormat.CHANNEL_IN_MONO;
//            //PCM_16是所有android系统都支持的
//            int autioFormat = AudioFormat.ENCODING_PCM_16BIT;
//            //计算AudioRecord内部buffer最小
//            int minBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, autioFormat);
//            //buffer不能小于最低要求，也不能小于我们每次我们读取的大小。
//            mAudioRecord = new AudioRecord(audioSource, sampleRate, channelConfig, autioFormat, Math.max(minBufferSize, BUFFER_SIZE));
//            byte[] mBuffer = new byte[1024];
//            //开始录音
//            mAudioRecord.startRecording();
//
//            //循环读取数据，写入输出流中
//            while (true) {
//                //只要还在录音就一直读取
//                int read = mAudioRecord.read(mBuffer, 0, 1024);
//                if (read <= 0) {
//                    return false;
//                } else {
//                    mFileOutputStream.write(mBuffer, 0, read);
//                }
//
//            }
//
//            //退出循环，停止录音，释放资源
//            stopRecorder();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        } finally {
//            if (mAudioRecord != null) {
//                mAudioRecord.release();
//            }
//        }
//        return true;
//    }
//
//    /**
//     * 停止录音
//     * @return
//     */
//    private boolean doStop() {
//        //停止录音，关闭文件输出流
//        mAudioRecord.stop();
//        mAudioRecord.release();
//        mAudioRecord = null;
//        Log.i("Tag8", "go here");
//        //记录结束时间，统计录音时长
//        stopRecorderTime = System.currentTimeMillis();
//        //大于3秒算成功，在主线程更新UI
//        final int send = (int) (stopRecorderTime - startRecorderTime) / 1000;
//        if (send > 3) {
//            mHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    tv_stream_msg.setText("录音成功：" + send + "秒");
//                    bt_stream_recorder.setText("开始录音");
//                    Log.i("Tag8", "go there");
//                }
//            });
//        } else {
//            recorderFail();
//            return false;
//        }
//        return true;
//    }
//
//}
