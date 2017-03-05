package com.lihb.babyvoice.command;

/**
 * Created by lihb on 2017/3/5.
 */

public class PickedCategoryCommand extends BaseAndroidCommand {

    public static final int TYPE_HEART = 0;
    public static final int TYPE_LUNG = 1;
    public static final int TYPE_VOICE = 2;
    public static final int TYPE_OTHER = 3;

    private final int action;

    public PickedCategoryCommand(int act) {
        action = act;
    }

    public int getAction() {
        return action;
    }
}
