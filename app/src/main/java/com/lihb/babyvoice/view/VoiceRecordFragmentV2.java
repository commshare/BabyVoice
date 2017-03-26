package com.lihb.babyvoice.view;

import android.graphics.PixelFormat;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cokus.wavelibrary.draw.WaveCanvas;
import com.cokus.wavelibrary.view.WaveSurfaceView;
import com.lihb.babyvoice.R;
import com.lihb.babyvoice.customview.TitleBar;
import com.lihb.babyvoice.customview.base.BaseFragment;
import com.lihb.babyvoice.utils.FileUtils;
import com.orhanobut.logger.Logger;

/**
 * Created by lhb on 2017/2/10.
 */
public class VoiceRecordFragmentV2 extends BaseFragment {

    private TextView recordText;
    private Chronometer mChronometer;
    private TitleBar mTitleBar;
    private String mFileName ="test";
    private int mRecordType;
    private WaveSurfaceView mWaveSfv;
    private WaveCanvas waveCanvas;
    public static final String DATA_DIRECTORY = Environment
            .getExternalStorageDirectory() + "/babyVoiceRecord/";

    private static final int FREQUENCY = 16000;// 设置音频采样率，44100是目前的标准，但是某些设备仍然支持22050，16000，11025
    private static final int CHANNELCONGIFIGURATION = AudioFormat.CHANNEL_IN_MONO;// 设置单声道声道
    private static final int AUDIOENCODING = AudioFormat.ENCODING_PCM_16BIT;// 音频数据格式：每个样本16位
    public final static int AUDIO_SOURCE = MediaRecorder.AudioSource.MIC;// 音频获取源
    private int recBufSize;// 录音最小buffer大小
    private AudioRecord audioRecord;
    private AudioTrack audioTrack;



    public static VoiceRecordFragmentV2 create() {
        return new VoiceRecordFragmentV2();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            mRecordType = bundle.getInt("recordType");
        }
        return inflater.inflate(R.layout.fragment_voice_recordv2, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        hideBottomTab();
        initView();
        FileUtils.createDirectory(DATA_DIRECTORY);
        if(mWaveSfv != null) {
            mWaveSfv.setLine_off(42);
            //解决surfaceView黑色闪动效果
            mWaveSfv.setZOrderOnTop(true);
            mWaveSfv.getHolder().setFormat(PixelFormat.TRANSPARENT);
        }
    }



    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden == false) {
            hideBottomTab();
            mChronometer.setBase(System.currentTimeMillis());
            if (mWaveSfv != null) {
                mWaveSfv.initDraw();
            }
        }
    }


    private void hideBottomTab() {
        if (getActivity() == null) {
            return;
        }
        // 隐藏底部的导航栏和分割线
        ((LinearLayout) getActivity().findViewById(R.id.linearLayout1)).setVisibility(View.GONE);
        ((View) getActivity().findViewById(R.id.divider_line2)).setVisibility(View.GONE);
    }

    private void initView() {
        mChronometer = (Chronometer) getView().findViewById(R.id.chronometer);
        recordText = (TextView) getView().findViewById(R.id.record_txt);
        recordText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (waveCanvas == null || !waveCanvas.isRecording) {
                    recordText.setText("完成");
                    initAudio();
                } else {
                    recordText.setText("开始");
                    waveCanvas.stop();
                    waveCanvas = null;
                    gotoVoiceSaveFragment();
                }
            }
        });
        mTitleBar = (TitleBar) getView().findViewById(R.id.title_bar);
        mTitleBar.setLeftOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        mWaveSfv = (WaveSurfaceView) getView().findViewById(R.id.wavesfv);

    }

    public void initAudio(){
        recBufSize = AudioRecord.getMinBufferSize(FREQUENCY,
                CHANNELCONGIFIGURATION, AUDIOENCODING);// 录音组件
        audioRecord = new AudioRecord(AUDIO_SOURCE,// 指定音频来源，这里为麦克风
                FREQUENCY, // 16000HZ采样频率
                CHANNELCONGIFIGURATION,// 录制通道
                AUDIO_SOURCE,// 录制编码格式
                recBufSize);// 录制缓冲区大小 //先修改

        audioTrack = new AudioTrack( AudioManager.STREAM_MUSIC, FREQUENCY,
                AudioFormat.CHANNEL_OUT_MONO, AUDIOENCODING, recBufSize,
                AudioTrack.MODE_STREAM);

        waveCanvas = new WaveCanvas();
        waveCanvas.baseLine = mWaveSfv.getHeight() / 2;
        String[] items = getResources().getStringArray(R.array.voice_type);

        mFileName = items[mRecordType] + System.currentTimeMillis();
        waveCanvas.Start(audioRecord, audioTrack,recBufSize, mWaveSfv, mFileName, DATA_DIRECTORY, new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if(msg.what == WaveCanvas.MSG_SINGAL_START){
                    startChronometer(System.currentTimeMillis());
                }else if(msg.what == WaveCanvas.MSG_SINGAL_STOP){
                    stopChronometer();
                }else {
                    String errorMsg = (String) msg.obj;
                    Logger.e(errorMsg);
                }
                return true;
            }
        });
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

    @Override
    public void onPause() {
        super.onPause();
//        RecorderHelper.getInstance().cancel();
    }

    /**
     * 计时器的时间变化监听
     */
    //region 计时器监听器
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

    //endregion

    private long getPublishTime() {
        if (mChronometer == null) {
            return 0;
        }
        return System.currentTimeMillis() - mChronometer.getBase();
    }

    private VoiceSaveFragment mVoiceSaveFragment;

    private void gotoVoiceSaveFragment() {
        if (null == mVoiceSaveFragment) {
            mVoiceSaveFragment = VoiceSaveFragment.create();
        }
        Bundle bundle = new Bundle();
        bundle.putString("fileName", mFileName);
        Log.e("VoiceRecordFragmentV2", "gotoVoiceSaveFragment: filename = " +mFileName );
        mVoiceSaveFragment.setArguments(bundle);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.hide(this);
        transaction.add(R.id.main_layout, mVoiceSaveFragment, "VoiceSaveFragment")
                .show(mVoiceSaveFragment)
                .addToBackStack(null)
                .commit();

    }


}