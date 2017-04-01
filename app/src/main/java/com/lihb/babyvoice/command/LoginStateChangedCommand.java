package com.lihb.babyvoice.command;

/**
 * Created by lhb on 2017/4/1.
 */

public class LoginStateChangedCommand extends BaseAndroidCommand {

    public enum LoginState {
        LOGIN_ON,
        LOGIN_OFF,
    }

    private LoginState mState;

    public LoginStateChangedCommand(LoginState state) {
        mState = state;
    }

    public LoginState getmState() {
        return mState;
    }

    public void setmState(LoginState mState) {
        this.mState = mState;
    }

    @Override
    public String toString() {
        return "LoginStateChangedCommand{" +
                "mState=" + mState +
                '}';
    }
}
