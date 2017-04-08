package com.lihb.babyvoice.command;

/**
 * Created by Administrator on 2017/4/8.
 */

public class HeadSetPluginChangedCommand extends BaseAndroidCommand {

    public enum HeadSetPluginState {
        HEAD_SET_IN,
        HEAD_SET_OUT
    }

    private HeadSetPluginState mState;

    public HeadSetPluginChangedCommand(HeadSetPluginState state) {
        mState = state;
    }

    public HeadSetPluginState getState() {
        return mState;
    }

    public void setState(HeadSetPluginState state) {
        this.mState = state;
    }

    @Override
    public String toString() {
        return "HeadSetPluginChangedCommand{" +
                "mState=" + mState +
                '}';
    }
}
