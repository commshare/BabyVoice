package com.lihb.babyvoice.utils;

import android.media.MediaRecorder;
import android.os.Handler;
import android.util.Log;

import java.io.File;

/**
 * @author lihb
 * @date 2017/2/24.
 */

public class RecorderHelper {
    private static final String TAG = "RecorderHelper";
    String PATH;
    private int BASE = 50;
    private int SPACE = 60;// 间隔取样时间
    private MediaRecorder mMediaRecorder;
    private static volatile RecorderHelper sInst = null;
    onRecorderListener mListener;
    Handler mHandler = new Handler();

    public static RecorderHelper getInstance() {
        RecorderHelper inst = sInst;
        if (inst == null) {
            synchronized (RecorderHelper.class) {
                inst = sInst;
                if (inst == null) {
                    inst = new RecorderHelper();
                    sInst = inst;
                }
            }
        }
        return inst;
    }

    public RecorderHelper setPath(String path) {
        this.PATH = path;
        return this;
    }

    private RecorderHelper() {
    }

    public void startRecord() {
        try {
            File file = new File(PATH);
            if (!file.exists()) {
                file.createNewFile();
            }

            mMediaRecorder = new MediaRecorder();
            // 设置录音文件的保存位置
            mMediaRecorder.setOutputFile(file.getAbsolutePath());
            // 设置录音的来源（从哪里录音）
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            // 设置录音的保存格式
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
            // 设置录音的编码
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mMediaRecorder.prepare();
            mMediaRecorder.start();
            if (null != mListener) {
                mListener.recorderStart();
            }
            updateMicStatus();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopAndRelease() {
        if (null != mMediaRecorder) {
            mMediaRecorder.stop();
            mMediaRecorder.release();
            mMediaRecorder = null;
            if (null != mListener) {
                mListener.recorderStop();
            }
        }
    }

    public void setRecorderListener(onRecorderListener listener) {
        this.mListener = listener;
    }

    public void cancel() {
        mHandler.removeCallbacksAndMessages(null);
        this.stopAndRelease();
    }

    public interface onRecorderListener {
        void recorderStart();

        void recorderStop();

        void volumeChange(float vol);
    }

    private void updateMicStatus() {
        if (mMediaRecorder != null) {
            double ratio = (double) mMediaRecorder.getMaxAmplitude() / BASE;

//            double db = 0;// 分贝
//            if (ratio > 1) {
//                db = 20 * Math.log10(ratio);
//            }
            Log.d(TAG, "updateMicStatus: ratio = " + ratio);

            if (null != mListener) {
//                mListener.volumeChange((float) db);
                mListener.volumeChange((float) ratio);
            }

            mHandler.postDelayed(mUpdateMicStatusTimer, SPACE);
        }
    }

    private Runnable mUpdateMicStatusTimer = new Runnable() {
        public void run() {
            updateMicStatus();
        }
    };

}
