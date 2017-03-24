package com.lihb.babyvoice.player;

import rx.Observable;

/**
 * Created by lhb on 2017/3/24.
 */

public interface IPlayer {

    void init(String filePath);

    Observable<Void> play();

    void pause();

    void stop();
}
