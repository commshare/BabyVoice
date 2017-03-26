package com.lihb.babyvoice.view;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.cokus.wavelibrary.utils.SoundFile;
import com.cokus.wavelibrary.view.WaveformView;
import com.lihb.babyvoice.R;
import com.lihb.babyvoice.customview.TitleBar;
import com.lihb.babyvoice.customview.base.BaseFragment;
import com.lihb.babyvoice.model.BabyVoice;
import com.lihb.babyvoice.utils.CommonToast;
import com.lihb.babyvoice.utils.StringUtils;

import java.io.File;

import static com.lihb.babyvoice.R.id.waveview;

/**
 * Created by lihb on 2017/3/12.
 */

public class VoicePlayFragment extends BaseFragment {

    private String mFileName;
    private TitleBar mTitleBar;
    private SeekBar seekBar;
    private ImageView play_pause_img;
    private WaveformView waveformView;
    private RelativeLayout play_bottom_layout;
    private ImageView category_img;
    private TextView current_time_txt;
    private TextView total_time_txt;
    private TextView voice_analysis_txt;
    private View diagnose_view;
    private MediaPlayer mediaPlayer;
    private boolean isperson;
    private int currentPos = 0;
    private BabyVoice babyVoice;
    private MyHandler myHandler;

    public static VoicePlayFragment create() {
        return new VoicePlayFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            babyVoice = bundle.getParcelable("babyVoice");
        }
        return inflater.inflate(R.layout.fragment_voice_play, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        hideBottomTab();
        initView();
    }

    private void initView() {

        mTitleBar = (TitleBar) getView().findViewById(R.id.title_bar);
        mTitleBar.setLeftOnClickListener(mOnClickListener);
        mTitleBar.setRightOnClickListener(mOnClickListener);

        seekBar = (SeekBar) getView().findViewById(R.id.seek_bar);
        play_pause_img = (ImageView) getView().findViewById(R.id.play_pause_img);
        category_img = (ImageView) getView().findViewById(R.id.category_img);
        voice_analysis_txt = (TextView) getView().findViewById(R.id.voice_analysis_txt);
        current_time_txt = (TextView) getView().findViewById(R.id.current_time_txt);
        total_time_txt = (TextView) getView().findViewById(R.id.total_time_txt);
        waveformView = (WaveformView) getView().findViewById(waveview);
        waveformView.setLine_offset(42);
        play_bottom_layout = (RelativeLayout) getView().findViewById(R.id.play_bottom_layout);

        if (null != babyVoice) {
            mTitleBar.setLeftText(babyVoice.name);
            setCategoryImg(babyVoice.category);
        }
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                current_time_txt.setText(StringUtils.formatTime(progress / 1000));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isperson = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                currentPos = seekBar.getProgress();
                mediaPlayer.seekTo(currentPos);
                isperson = false;

            }
        });

        play_pause_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doPauseResume();
            }
        });
        myHandler = new MyHandler();

        loadDataFromFile();

    }

    File mFile;
    Thread mLoadSoundFileThread;
    SoundFile mSoundFile;
    boolean mLoadingKeepGoing;
//    SamplePlayer mPlayer;
    /** 载入wav文件显示波形 */
    private void loadDataFromFile() {
        try {
            Thread.sleep(300);//让文件写入完成后再载入波形 适当的休眠下
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mFile = new File(getFilename());
        mLoadingKeepGoing = true;
        // Load the sound file in a background thread
        mLoadSoundFileThread = new Thread() {
            public void run() {
                try {
                    mSoundFile = SoundFile.create(mFile.getAbsolutePath(),null);
                    if (mSoundFile == null) {
                        return;
                    }
//                    mPlayer = new SamplePlayer(mSoundFile);
                } catch (final Exception e) {
                    e.printStackTrace();
                    return;
                }
                if (mLoadingKeepGoing) {
                    Runnable runnable = new Runnable() {
                        public void run() {
                            finishOpeningSoundFile();
//                            waveSfv.setVisibility(View.INVISIBLE);
//                            waveView.setVisibility(View.VISIBLE);
                        }
                    };
                    getActivity().runOnUiThread(runnable);
                }
            }
        };
        mLoadSoundFileThread.start();
    }



    float mDensity;
    /**waveview载入波形完成*/
    private void finishOpeningSoundFile() {
        waveformView.setSoundFile(mSoundFile);
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mDensity = metrics.density;
        waveformView.recomputeHeights(mDensity);
    }

    private void setCategoryImg(String category) {
        String[] items = getResources().getStringArray(R.array.voice_type);
        if (StringUtils.areEqual(category, items[0])) {
            category_img.setImageResource(R.mipmap.heart);
        } else if (StringUtils.areEqual(category, items[1])) {
            category_img.setImageResource(R.mipmap.lung);
        } else if (StringUtils.areEqual(category, items[2])) {
            category_img.setImageResource(R.mipmap.voice);
        } else {
            category_img.setImageResource(R.mipmap.other);
        }
    }

    private void doPauseResume() {
        if (isPlaying()) {
            play_pause_img.setImageResource(R.mipmap.play);
            pause();
        } else {
            play_pause_img.setImageResource(R.mipmap.stop);
            play();
        }
    }

    private boolean isPlaying() {
        if (mediaPlayer != null) {
            return mediaPlayer.isPlaying();
        }
        return false;
    }

    /**
     * 暂停
     */
    private void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    /**
     * 播放
     */
    private void play() {
        try {
            if (null == mediaPlayer) { //开始播放
                String url;
                if (null != babyVoice) {
                    url = getFilename();
//                    url = babyVoice.url;
                } else {
                    throw new Exception("no play file find.");
                }
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(url);

                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.prepareAsync();
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mediaPlayer.start();
                        seekBar.setMax(mediaPlayer.getDuration());
                        total_time_txt.setText(StringUtils.formatTime(mediaPlayer.getDuration() / 1000));
                        //更新进度
                        myHandler.removeCallbacks(null);
                        myHandler.sendEmptyMessage(1000);
                    }
                });
            } else {
                // 暂停后播放
                mediaPlayer.start();
            }

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    play_pause_img.setImageResource(R.mipmap.play);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            play_pause_img.setImageResource(R.mipmap.play);
            CommonToast.showShortToast("播放文件失败。");
        }
    }

    private String getFilename() {
        String filepath = Environment.getExternalStorageDirectory().getAbsolutePath();
        File file = new File(filepath, "/babyVoiceRecord");

        if (file.exists()) {
            file.delete();
        }

        return (file.getAbsolutePath() + "/儿童语音1490534481461.wav");
    }



    @Override
    public void onPause() {
        super.onPause();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            resetUI();
        }
    }

    private void resetUI() {
        seekBar.setProgress(0);
        current_time_txt.setText(StringUtils.formatTime(0));
        total_time_txt.setText(StringUtils.formatTime(0));
    }

    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden == false) {
            hideBottomTab();
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

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == mTitleBar.getLeftText()) {
                getActivity().onBackPressed();
            } else if (v == mTitleBar.getRightText()) {

            }
        }
    };

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            sendEmptyMessageDelayed(1000, 100);
            if (isperson) {
                return;
            }
            if (mediaPlayer != null) {
                seekBar.setProgress(mediaPlayer.getCurrentPosition());
            }
        }
    }
}
