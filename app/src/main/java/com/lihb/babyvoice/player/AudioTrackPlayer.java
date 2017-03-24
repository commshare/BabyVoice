package com.lihb.babyvoice.player;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by lhb on 2017/3/24.
 */

public class AudioTrackPlayer implements IPlayer {

    public int frequency = 11025;
    public int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
    public int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;

    private String mFilePath;

    private int bufferSize;
    private short[] audiodata;
    private DataInputStream dis;
    private AudioTrack audioTrack;
    private boolean isPlaying;

    private static AudioTrackPlayer instance = new AudioTrackPlayer();

    private AudioTrackPlayer() {
    }

    public static AudioTrackPlayer getInstance() {
        return instance;
    }


    @Override
    public void init(String filePath) {
        mFilePath = filePath;
        bufferSize = AudioTrack.getMinBufferSize(frequency,
                channelConfiguration, audioEncoding);
        audiodata = new short[bufferSize / 4];

        try {
            dis = new DataInputStream(
                    new BufferedInputStream(new FileInputStream(filePath)));
            if (null == audioTrack) {
                audioTrack = new AudioTrack(
                        AudioManager.STREAM_MUSIC, frequency,
                        channelConfiguration, audioEncoding, bufferSize,
                        AudioTrack.MODE_STREAM);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Observable<Void> play() {
        isPlaying = true;
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {

                try {
                    audioTrack.play();
                    while (isPlaying && dis.available() > 0) {
                        int i = 0;
                        while (dis.available() > 0 && i < audiodata.length) {
                            audiodata[i] = dis.readShort();
                            i++;
                        }
                        audioTrack.write(audiodata, 0, audiodata.length);
                    }

//                    dis.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

    }

    @Override
    public void pause() {
        isPlaying = false;
    }

    @Override
    public void stop() {
        isPlaying = false;
        if (null != audioTrack) {
            audioTrack.stop();
            audioTrack = null;
        }
    }

    public String getFilePath() {
        return mFilePath;
    }
}
