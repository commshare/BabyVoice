package com.lihb.babyvoice.customview;

/**
 * Created by ruoshili on 12/8/15.
 */
public interface IUiState {

    int kUiInit = 0;
    int kUiActive = 10;
    // 大于kStateActive的状态都会被认为是Paused
    int kUiPaused = 1000;
    int kUiInstanceStateSaved = 2000;
    int kUiDestroyed = 10000;

    boolean isUiActive();

    boolean isUiPaused();

    boolean isUiInstanceStateSaved();

    boolean isUiDestroyed();
}
