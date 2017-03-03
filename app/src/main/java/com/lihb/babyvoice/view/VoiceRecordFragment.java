package com.lihb.babyvoice.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lihb.babyvoice.R;
import com.lihb.babyvoice.customview.AnimatedRecordingView;
import com.lihb.babyvoice.customview.TitleBar;
import com.lihb.babyvoice.customview.base.BaseFragment;
import com.lihb.babyvoice.utils.FileUtils;
import com.lihb.babyvoice.utils.RecorderHelper;
import com.lihb.babyvoice.utils.StringUtils;
import com.orhanobut.logger.Logger;

/**
 * Created by lhb on 2017/2/10.
 */
public class VoiceRecordFragment extends BaseFragment {

    private TextView recordText;
    private Chronometer mChronometer;
    private TitleBar mTitleBar;
    private String mFileName;
    private AnimatedRecordingView mAnimatedRecordingView;
//    private RecordingView mRecordingView;

    public static VoiceRecordFragment create() {
        return new VoiceRecordFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_voice_record, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        hideBottomTab();
        initRecordHelper();
        initView();
    }

    private void initRecordHelper() {
        mFileName = System.currentTimeMillis() + ".amr";

        RecorderHelper.getInstance().setPath(FileUtils.getAMRFilePath(mFileName));
        RecorderHelper.getInstance().setRecorderListener(mOnRecorderListener);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden == false) {
            hideBottomTab();
            mChronometer.setBase(System.currentTimeMillis());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Logger.i("onResume");
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
                String text = recordText.getText().toString().trim();
                if (StringUtils.areEqual(text, "开始")) {
                    recordText.setText("完成");
                    RecorderHelper.getInstance().startRecord();
                } else {
//                    RecorderHelper.getInstance().cancel();
                    mAnimatedRecordingView.stop();
                    recordText.setText("开始");
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

        mAnimatedRecordingView = (AnimatedRecordingView) getView().findViewById(R.id.animated_recording_view);
//        mRecordingView = (RecordingView) getView().findViewById(R.id.animated_recording_view);
    }

    private RecorderHelper.onRecorderListener mOnRecorderListener = new RecorderHelper.onRecorderListener() {
        @Override
        public void recorderStart() {
            startChronometer(System.currentTimeMillis());
            mAnimatedRecordingView.start();
//            mRecordingView.start();
        }

        @Override
        public void recorderStop() {
            stopChronometer();
            mAnimatedRecordingView.stop();
        }

        @Override
        public void volumeChange(float vol) {
//            Log.e("lihb", "vol = " + vol);
            mAnimatedRecordingView.setVolume(vol);
//            mRecordingView.setVolume(vol);
        }
    };

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
        RecorderHelper.getInstance().cancel();
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

    private VoiceSaveFragment mVoiceSaveFragment;

    private void gotoVoiceSaveFragment() {
        if (null == mVoiceSaveFragment) {
            mVoiceSaveFragment = VoiceSaveFragment.create();
        }
        Bundle bundle = new Bundle();
        bundle.putString("fileName", mFileName);
        mVoiceSaveFragment.setArguments(bundle);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.hide(this);
        transaction.add(R.id.main_layout, mVoiceSaveFragment, "VoiceSaveFragment")
                .show(mVoiceSaveFragment)
                .addToBackStack(null)
                .commit();

    }


}