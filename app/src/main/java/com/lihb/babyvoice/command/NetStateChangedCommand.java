package com.lihb.babyvoice.command;

import android.telephony.TelephonyManager;

/**
 * 手机网络状态切换事件
 * <p>
 * Created by caijw on 2015/9/21.
 */
public class NetStateChangedCommand extends BaseAndroidCommand {
    private NetState mState;

    public NetStateChangedCommand(NetState state) {
        mState = state;
    }

    public static NetState getStateByNetworkType(int type) {
        switch (type) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return NetState.NET_STATE_2G;
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return NetState.NET_STATE_3G;
            case TelephonyManager.NETWORK_TYPE_LTE:
                return NetState.NET_STATE_4G;
            default:
                return NetState.NET_STATE_NO_NETWORK;
        }
    }

    public NetState getState() {
        return mState;
    }

    public void setState(NetState state) {
        mState = state;
    }

    @Override
    public String toString() {
        return "NetStateChangedCommand{" +
                "mState=" + mState +
                '}';
    }

    public enum NetState {
        NET_STATE_WIFI,
        NET_STATE_4G,
        NET_STATE_2G,
        NET_STATE_3G,
        NET_STATE_NO_NETWORK,

        NET_YY_DISCONNECT,
        NET_YY_CONNECTED,

        NET_RSSI_BAD,
        NET_RSSI_GOOD
    }
}
