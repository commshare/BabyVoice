package com.lihb.babyvoice.view;

import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lihb.babyvoice.R;
import com.lihb.babyvoice.customview.base.BaseFragment;
import com.lihb.babyvoice.player.AudioTrackPlayer;
import com.lihb.babyvoice.player.IPlayer;
import com.orhanobut.logger.Logger;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by lhb on 2017/2/8.
 */

public class WatchFragment extends BaseFragment {


    private IPlayer player;

    public static WatchFragment create() {
        return new WatchFragment();
    }

    private String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()
            + "/Android/data/AudioRecorder/files/12.pcm";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_watch, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        player = AudioTrackPlayer.getInstance();
        player.init(filePath);

        view.findViewById(R.id.testplay_start_txt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.play()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.newThread())
                        .subscribe(new Action1<Void>() {
                            @Override
                            public void call(Void aVoid) {
                                Logger.i("success!");
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                Logger.e(throwable.toString());
                            }
                        });
            }
        });
        view.findViewById(R.id.testplay_pause_txt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.pause();
            }
        });
        view.findViewById(R.id.testplay_stop_txt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.stop();
            }
        });


    }

    @Override
    public void onPause() {
        super.onPause();
        player.pause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        player.stop();
    }
}
