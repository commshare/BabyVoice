package com.lihb.babyvoice.command;

/**
 * Created by huqiuyun on 16/7/21.
 */
public class ScreenCommand extends BaseAndroidCommand {
    public final static int Off = 0;
    public final static int On = 1;
    public final static int Present = 2;

    private final int action;

    public ScreenCommand(int act) {
        action = act;
    }

    int getAction() {
        return action;
    }
}
