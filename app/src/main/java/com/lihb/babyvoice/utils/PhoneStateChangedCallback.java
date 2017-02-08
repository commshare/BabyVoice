package com.lihb.babyvoice.utils;

/**
 * Created by kennex on 2015/8/3.
 */
public interface PhoneStateChangedCallback {
    void onPhoneComingCall(final String phoneNum);

    void onPhoneAcceptCall();

    void onPhoneHangUp();
}
