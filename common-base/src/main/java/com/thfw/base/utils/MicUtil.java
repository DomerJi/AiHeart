package com.thfw.base.utils;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

/**
 * Author:pengs
 * Date: 2022/7/14 18:43
 * Describe:Todo
 */
public class MicUtil {

    public static boolean validateMicAvailability() {
        Boolean available = true;
        AudioRecord recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                44100, AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_DEFAULT, 44100);
        try {
            if (recorder.getRecordingState() != AudioRecord.RECORDSTATE_STOPPED) {
                available = false;
            }

            recorder.startRecording();

            if (recorder.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING) {
                recorder.stop();
                available = false;
            }

            recorder.stop();
        } finally {
            if (recorder != null) {
                recorder.release();
                recorder = null;
            }
        }
        return available;
    }

}
