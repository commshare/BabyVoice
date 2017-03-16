package com.lihb.babyvoice.view;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.lihb.babyvoice.R;
import com.lihb.babyvoice.customview.AnimatedRecordingView;
import com.lihb.babyvoice.customview.TitleBar;
import com.lihb.babyvoice.customview.base.BaseFragment;
import com.lihb.babyvoice.model.BabyVoice;
import com.lihb.babyvoice.utils.CommonToast;
import com.lihb.babyvoice.utils.StringUtils;

/**
 * Created by lihb on 2017/3/12.
 */

public class VoicePlayFragment extends BaseFragment {

    private String mFileName;
    private TitleBar mTitleBar;
    private SeekBar seekBar;
    private ImageView play_pause_img;
    private AnimatedRecordingView animated_playing_view;
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
        animated_playing_view = (AnimatedRecordingView) getView().findViewById(R.id.animated_playing_view);
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

    }

    private void setCategoryImg(String category) {
        String[] items = getResources().getStringArray(R.array.voice_type);
        if (StringUtils.areEqual(category, items[0])) {
            category_img.setImageResource(R.mipmap.heart);
//            play_bottom_layout.setBackgroundResource(R.mipmap.heart);
        } else if (StringUtils.areEqual(category, items[1])) {
            category_img.setImageResource(R.mipmap.lung);
//            play_bottom_layout.setBackgroundResource(R.mipmap.lung);
        } else if (StringUtils.areEqual(category, items[2])) {
            category_img.setImageResource(R.mipmap.voice);
//            play_bottom_layout.setBackgroundResource(R.mipmap.voice);
        } else {
            category_img.setImageResource(R.mipmap.other);
//            play_bottom_layout.setBackgroundResource(R.mipmap.other);
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
                    url = babyVoice.url;
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
