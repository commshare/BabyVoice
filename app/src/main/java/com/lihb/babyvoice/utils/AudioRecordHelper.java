package com.lihb.babyvoice.utils;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.orhanobut.logger.Logger;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by lhb on 2017/3/24.
 */

public class AudioRecordHelper {

    private static final String TAG = "AudioRecordHelper";

    private static final int RECORDER_BPP = 16;
    private static final String AUDIO_RECORDER_FOLDER = "AudioRecorder";    //默认录音文件的存储位置
    private static final String AUDIO_RECORDER_TEMP_FILE = "record_temp.raw";
    private static final long SPACE = 100;
    private static int frequency = 22050;
    private static int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;//单声道
    private static int EncodingBitRate = AudioFormat.ENCODING_PCM_16BIT;    //音频数据格式：脉冲编码调制（PCM）每个样品16位
    private AudioRecord audioRecord = null;
    private boolean isRecording = false;

    private RecorderHelper.onRecorderListener mListener;
    private Handler mHandler;


    private int bufferSize;
    private byte[] audioData;
    private DataOutputStream dos;
    private AudioTrack audioTrack;

    private static AudioRecordHelper sInst;
    private int bufferReadResult;
    private int sumOfSquares;
    private double volume;


    private AudioRecordHelper() {
        mHandler = new Handler(Looper.getMainLooper());
    }

    public static AudioRecordHelper getInstance() {
        AudioRecordHelper inst = sInst;
        if (inst == null) {
            synchronized (AudioRecordHelper.class) {
                inst = sInst;
                if (inst == null) {
                    inst = new AudioRecordHelper();
                    sInst = inst;
                }
            }
        }
        return inst;
    }

    public void setRecorderListener(RecorderHelper.onRecorderListener listener) {
        this.mListener = listener;
    }


    public void init() {
        bufferSize = AudioRecord.getMinBufferSize(frequency,
                channelConfiguration, EncodingBitRate);

        audioData = new byte[bufferSize];

        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, frequency,
                channelConfiguration, EncodingBitRate, bufferSize);

        try {
            dos = new DataOutputStream(
                    new BufferedOutputStream(new FileOutputStream(getTempFilename())));
            if (null == audioTrack) {
                audioTrack = new AudioTrack(
                        AudioManager.STREAM_MUSIC, frequency,
                        channelConfiguration, EncodingBitRate, bufferSize,
                        AudioTrack.MODE_STREAM);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void startRecord() {

        audioRecord.startRecording();
        audioTrack.play();
        updateMicStatus();
        isRecording = true;
        if (null != mListener) {
            mListener.recorderStart();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (isRecording) {
                        bufferReadResult = audioRecord.read(audioData, 0, bufferSize);
                        // 播放
                        audioTrack.write(audioData, 0, bufferReadResult);
                        short[] shortArray = toShortArray(audioData);
                        // 写文件
                        dos.write(audioData);
                        // 计算音量
                        int shortLen = bufferReadResult >> 1;
                        if (AudioRecord.ERROR_INVALID_OPERATION != bufferReadResult) {
                            for (int i = 0; i < shortLen; i++) {
                                int abs = Math.abs(shortArray[i]);
                                sumOfSquares += abs * abs;
                                // 平方和除以数据总长度，得到音量大小。
                                double mean = sumOfSquares / (double) shortLen;
                                volume = 10 * Math.log10(mean);
                                Log.d(TAG, "分贝值:" + volume);

                            }
                        }
                    }
                } catch (Exception e) {
                    Logger.e(e.toString());
                }
            }
        }).start();


//        return Observable.create(new Observable.OnSubscribe<Void>() {
//
//            @Override
//            public void call(Subscriber<? super Void> subscriber) {
//                try {
//                    audioRecord.startRecording();
//                    audioTrack.play();
//                    updateMicStatus();
//                    isRecording = true;
//                    if (null != mListener) {
//                        mListener.recorderStart();
//                    }
//                    while (isRecording) {
//                        bufferReadResult = audioRecord.read(audioData, 0, bufferSize);
//                        // 播放
//                        audioTrack.write(audioData, 0, bufferReadResult);
//
//                        if (AudioRecord.ERROR_INVALID_OPERATION != bufferReadResult) {
//                            for (int i = 0; i < bufferReadResult; i++) {
//                                dos.writeShort(audioData[i]);
//                                int abs = Math.abs(audioData[i]);
//                                sumOfSquares += abs * abs;
//                            }
//                        }
//                    }
//                } catch (Exception e) {
//                    Logger.e(e.toString());
//                }
//
//                try {
//                    dos.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
    }

    public short[] toShortArray(byte[] src) {

        int count = src.length >> 1;
        short[] dest = new short[count];
        for (int i = 0; i < count; i++) {
//            dest[i] = (short) (src[i * 2] << 8 | src[2 * i + 1] & 0xff);
            dest[i] = (short) ((src[i * 2] & 0xff) | ((src[2 * i + 1] & 0xff) << 8));
        }
        return dest;
    }


    public void stopRecord() {

        try {
            dos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (null != audioRecord || null != audioTrack) {
            isRecording = false;

            audioRecord.stop();
            audioRecord.release();

            audioRecord = null;

            audioTrack.stop();
            audioTrack.release();
            audioTrack = null;
        }
        if (null != mListener) {
            mListener.recorderStop();
        }

        copyWaveFile(getTempFilename(), getFilename());
        deleteTempFile();
        mHandler.removeCallbacksAndMessages(null);
    }


    private void updateMicStatus() {

//        int ratio = (Math.abs((int) (sumOfSquares / (float) bufferReadResult*2) / 10000) /*>> 1*/);
////        int ratio = maxValue;
//        Log.e(TAG, "updateMicStatus: ratio = " + ratio);
        if (null != mListener) {
            mListener.volumeChange((float) volume);
        }

        mHandler.postDelayed(mUpdateMicStatusTimer, SPACE);
    }

    private Runnable mUpdateMicStatusTimer = new Runnable() {
        public void run() {
            updateMicStatus();
        }
    };


    private void deleteTempFile() {
        File file = new File(getTempFilename());

        file.delete();
    }

    private String getTempFilename() {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath, AUDIO_RECORDER_FOLDER);

        if (!file.exists()) {
            file.mkdirs();
        }

        File tempFile = new File(filepath, AUDIO_RECORDER_TEMP_FILE);

        if (tempFile.exists())
            tempFile.delete();

        return (file.getAbsolutePath() + "/" + AUDIO_RECORDER_TEMP_FILE);
    }

    private String getFilename() {
        String filepath = Environment.getExternalStorageDirectory().getAbsolutePath();
        File file = new File(filepath, AUDIO_RECORDER_FOLDER);

        if (file.exists()) {
            file.delete();
        }

        return (file.getAbsolutePath() + "/speaker.wav");
    }

    private void copyWaveFile(String inFilename, String outFilename) {
        FileInputStream in = null;
        FileOutputStream out = null;
        long totalAudioLen = 0;
        long totalDataLen = 0;
        long longSampleRate = frequency;
        int channels = 1;
        long byteRate = RECORDER_BPP * frequency * channels / 8;

        byte[] data = new byte[bufferSize];

        try {
            in = new FileInputStream(inFilename);
            out = new FileOutputStream(outFilename);
            totalAudioLen = in.getChannel().size();
            totalDataLen = totalAudioLen + 36;

            //AppLog.logString("File size: " + totalDataLen);

            WriteWaveFileHeader(out, totalAudioLen, totalDataLen,
                    longSampleRate, channels, byteRate);

            while (in.read(data) != -1) {
                out.write(data);
            }

            in.close();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void WriteWaveFileHeader(
            FileOutputStream out, long totalAudioLen,
            long totalDataLen, long longSampleRate, int channels,
            long byteRate) throws IOException {

        byte[] header = new byte[44];

        header[0] = 'R';  // RIFF/WAVE header
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f';  // 'fmt ' chunk
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = 16;  // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1;  // format = 1
        header[21] = 0;
        header[22] = (byte) channels;
        header[23] = 0;
        header[24] = (byte) (longSampleRate & 0xff);
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = (byte) (1 * 16 / 8);  // block align
        header[33] = 0;
        header[34] = RECORDER_BPP;  // bits per sample
        header[35] = 0;
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);
        out.write(header, 0, 44);
    }

    public interface onRecorderListener {
        void recorderStart();

        void recorderStop();

        void volumeChange(float vol);
    }
}
