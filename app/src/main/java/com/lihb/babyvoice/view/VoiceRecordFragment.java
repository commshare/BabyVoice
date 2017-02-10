package com.lihb.babyvoice.view;

import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.TextView;

import com.lihb.babyvoice.R;
import com.lihb.babyvoice.customview.TitleBar;
import com.lihb.babyvoice.customview.base.BaseFragment;
import com.lihb.babyvoice.utils.StringUtils;
import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by lhb on 2017/2/10.
 */
public class VoiceRecordFragment extends BaseFragment {

    private TextView recordText;
    private MediaRecorder mRecorder;
    private Chronometer mChronometer;
    private SimpleDateFormat sdf;
    private TitleBar mTitleBar;

    public static VoiceRecordFragment create() {
        return new VoiceRecordFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_voice_record, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        sdf = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);
        mChronometer = (Chronometer) view.findViewById(R.id.chronometer);
        recordText = (TextView) view.findViewById(R.id.record_txt);
        recordText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = recordText.getText().toString().trim();
                if (StringUtils.areEqual(text, "开始")) {
                    recordText.setText("完成");
                    startRecording();
                } else {
                    stopRecording();
                    recordText.setText("开始");

                }
            }
        });
        mTitleBar = (TitleBar) view.findViewById(R.id.title_bar);
        mTitleBar.setLeftOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        //设置音源为MicPhone
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        //设置封装格式
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        String fileName = sdf.format(new Date());
        mRecorder.setOutputFile(getAMRFilePath(fileName/*+"-"+System.currentTimeMillis()*/ + ".amr"));
        //设置编码格式
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Logger.e("prepare() failed. cause: %s", e.getMessage());
        }

        mRecorder.start();
        startChronometer(System.currentTimeMillis());
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        stopChronometer();
    }

    /**
     * 判断是否有外部存储设备sdcard
     *
     * @return true | false
     */
    public static boolean isSdcardExit() {
        if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }

    /**
     * 获取编码后的AMR格式音频文件路径
     *
     * @return
     */
    public static String getAMRFilePath(String fileName) {
        String mAudioAMRPath = "";
        if (isSdcardExit()) {
            String fileBasePath = Environment.getExternalStorageDirectory().getAbsolutePath();
            mAudioAMRPath = fileBasePath + "/" + fileName;

        }
        return mAudioAMRPath;
    }

    public void startChronometer(long startTime) {
        mChronometer.setBase(startTime);
        mChronometer.setOnChronometerTickListener(mOnChronometerTickListener);
        mChronometer.start();
    }

    public long stopChronometer() {
        if (mChronometer != null) {
            mChronometer.stop();
            long t = System.currentTimeMillis() - mChronometer.getBase();
            return t;
        }
        return 0;
    }

    public long getChronometerRunningTime() {
        if (mChronometer == null) {
            return 0;
        }
        return System.currentTimeMillis() - mChronometer.getBase();
    }

    /**
     * 计时器的时间变化监听
     */
    private Chronometer.OnChronometerTickListener mOnChronometerTickListener = new Chronometer.OnChronometerTickListener() {
        @Override
        public void onChronometerTick(Chronometer var1) {
            if (mChronometer == null)
                return;

            try {
                long interval = getPublishTime() / 1000;
                int minutes = (int) (interval / 60);
                int seconds = (int) (interval % 60);
                int hours = minutes / 60;
                if (hours > 0) {
                    minutes = minutes % 60;
                }
                int days = hours / 24;
                if (days > 0) {
                    hours = hours % 24;
                }

                StringBuffer stringBuffer = new StringBuffer();
                if (days > 0) {
                    stringBuffer.append(String.format("%02d", days) + "天");
                }
                stringBuffer.append(String.format("%02d", hours) + ":");
                stringBuffer.append(String.format("%02d", minutes) + ":");
                stringBuffer.append(String.format("%02d", seconds));
                mChronometer.setText(stringBuffer.toString());
            } catch (Exception e) {
                Logger.i("Update video time failed:%s", e.toString());
            }

        }
    };

    private long getPublishTime() {
        if (mChronometer == null) {
            return 0;
        }
        return System.currentTimeMillis() - mChronometer.getBase();
    }


}