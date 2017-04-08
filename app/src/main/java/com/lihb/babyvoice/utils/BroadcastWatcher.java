package com.lihb.babyvoice.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.lihb.babyvoice.BabyVoiceApp;
import com.lihb.babyvoice.Constant;
import com.lihb.babyvoice.command.HomeKeyPressedCommand;
import com.lihb.babyvoice.command.IncomingPhoneCallCommand;
import com.lihb.babyvoice.command.NetStateChangedCommand;
import com.lihb.babyvoice.command.ScreenCommand;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by chentong on 15/3/16.
 */
public class BroadcastWatcher {
    static final String TAG = "TApp:BroadcastWatcher";

    final static String SYSTEM_DIALOG_REASON_KEY = "reason";
    final static String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
    final static String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";


    private final BabyVoiceApp mApp;
    private InnerReceiver mReceiver;
    private byte mCurrentNetworkType = -1;

    public BroadcastWatcher(BabyVoiceApp app) {
        mApp = app;
    }

    /**
     * 开始监听，注册广播
     */
    public void startWatch() {
        if (mReceiver == null) {
            mReceiver = new InnerReceiver();

            watchCloseSystemDialogs();
            mReceiver.addEventHandler(new BroadcastEventHandler(Intent.ACTION_ANSWER) {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Logger.d("exception get: onHomePressed");
                    RxBus.getDefault().post(new IncomingPhoneCallCommand());
                }
            });
            watchConnectivityAction();
            mReceiver.addEventHandler(new BroadcastEventHandler(Intent.ACTION_SCREEN_ON) {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Logger.d("onScreenOn");
                    mApp.setScreenOn(true);
                    RxBus.getDefault().post(new ScreenCommand(ScreenCommand.On));
                }
            });
            mReceiver.addEventHandler(new BroadcastEventHandler(Intent.ACTION_SCREEN_OFF) {
                @Override
                public void onReceive(Context context, Intent intent) {
                    mApp.setScreenOn(false);
                    Logger.d("onScreenOff");
                    RxBus.getDefault().post(new ScreenCommand(ScreenCommand.Off));
                }
            });
            mReceiver.addEventHandler(new BroadcastEventHandler(Intent.ACTION_USER_PRESENT) {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Logger.d("onUserPresent");
                    RxBus.getDefault().post(new ScreenCommand(ScreenCommand.Present));
                }
            });
            watchRssiChangedAction();

            /**
             * 耳机是否插入监听
             */
            mReceiver.addEventHandler(new BroadcastEventHandler(Intent.ACTION_HEADSET_PLUG) {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Logger.d("headset plug in?");
                    if (intent.hasExtra("state")){
                        if (intent.getIntExtra("state", 0) == 0){
//                            Toast.makeText(context, "headset not connected", Toast.LENGTH_LONG).show();
                            Logger.i("headset not connected");
                            BabyVoiceApp.getInstance().setPlugIn(false);
                        }
                        else if (intent.getIntExtra("state", 0) == 1){
//                            Toast.makeText(context, "headset connected", Toast.LENGTH_LONG).show();
                            Logger.i("headset connected");
                            BabyVoiceApp.getInstance().setPlugIn(true);
                        }
                    }
                }
            });
            mApp.registerReceiver(mReceiver, mReceiver.intentFilter);
        }
    }

    private void watchRssiChangedAction() {
        mReceiver.addEventHandler(new BroadcastEventHandler(WifiManager.RSSI_CHANGED_ACTION) {
            private static final int MIN_RSSI = -70;
            private long mLastReportRssiTimestamp = 0;
            private int mRssi = 0;

            @Override
            public void onReceive(Context context, Intent intent) {

                Logger.d("Network Rssi changed = " + intent);

                final long now = System.currentTimeMillis();
                final WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                if (wifiManager == null) {
                    Logger.e("Network get WIFI_SERVICE failed.");
                    return;
                }

                final WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                if (wifiInfo == null) {
                    Logger.e("Network get WifiInfo failed.");
                    return;
                }

                // 如果网络质量好, 最多半分钟上报一次, 如果不好就每次都报
//                if ((now - mLastReportRssiTimestamp) > 30 * 1000 || wifiInfo.getRssi() < MIN_RSSI) {
//                    mLastReportRssiTimestamp = now;
//                    SharedObjects.INSTANCE.logReportLifeCycle(Log.INFO, "Network WifiInfo: " + wifiInfo.toString());
//                }

                if (wifiInfo.getRssi() < MIN_RSSI && mRssi > MIN_RSSI) {
                    NetStateChangedCommand cmd = new NetStateChangedCommand(NetStateChangedCommand.NetState.NET_RSSI_BAD);
                    RxBus.getDefault().post(cmd);
                } else if (wifiInfo.getRssi() > MIN_RSSI && mRssi < MIN_RSSI) {
                    NetStateChangedCommand cmd = new NetStateChangedCommand(NetStateChangedCommand.NetState.NET_RSSI_GOOD);
                    RxBus.getDefault().post(cmd);
                }
                mRssi = wifiInfo.getRssi();
            }
        });
    }

    private void watchConnectivityAction() {
        mReceiver.addEventHandler(new BroadcastEventHandler(ConnectivityManager.CONNECTIVITY_ACTION) {
            @Override
            public void onReceive(Context context, Intent intent) {

                final byte networkType = NetworkHelper.getNetType(mApp);
                if (mCurrentNetworkType == networkType) {
                    return;
                }
                Logger.i("Network Type is %d", networkType);

                mCurrentNetworkType = networkType;
                NetStateChangedCommand cmd = new NetStateChangedCommand(NetStateChangedCommand.NetState.NET_STATE_NO_NETWORK);
                if (networkType == Constant.SYSNET_WIFI) {
                    cmd.setState(NetStateChangedCommand.NetState.NET_STATE_WIFI);
                } else if (networkType == Constant.SYSNET_DISCONNECT) {
                    cmd.setState(NetStateChangedCommand.NetState.NET_STATE_NO_NETWORK);
                } else if (networkType == Constant.SYSNET_MOBILE) {
                    TelephonyManager telephonyManager = (TelephonyManager) mApp.getSystemService(Context.TELEPHONY_SERVICE);
                    cmd.setState(NetStateChangedCommand.getStateByNetworkType(telephonyManager.getNetworkType()));
                }
                RxBus.getDefault().post(cmd);
            }
        });
    }

    private void watchCloseSystemDialogs() {
        mReceiver.addEventHandler(new BroadcastEventHandler(Intent.ACTION_CLOSE_SYSTEM_DIALOGS) {
            @Override
            public void onReceive(Context context, Intent intent) {

                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                if (reason != null) {
                    if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
                        // 短按home键
                        Logger.d("exception get: on Home Pressed");
                        RxBus.getDefault().post(new HomeKeyPressedCommand());
                    } else if (reason.equals(SYSTEM_DIALOG_REASON_RECENT_APPS)) {
                        // 长按home键
                        Logger.d("exception get: on Home Long Pressed");
                    }
                }
            }
        });
    }

    /**
     * 停止监听，注销广播
     */
    public void stopWatch() {
        if (mReceiver != null) {
            mApp.unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }

    private static abstract class BroadcastEventHandler {
        private final String action;

        public BroadcastEventHandler(final String action) {
            if (TextUtils.isEmpty(action)) {
                throw new IllegalArgumentException("action is null");
            }
            this.action = action;
        }

        public abstract void onReceive(Context context, Intent intent);
    }

    /**
     * 广播接收者
     */
    private class InnerReceiver extends BroadcastReceiver {
        public final IntentFilter intentFilter = new IntentFilter();
        private final List<BroadcastEventHandler> mHandlers = new ArrayList<>();

        public void addEventHandler(@NonNull final BroadcastEventHandler handler) {
            intentFilter.addAction(handler.action);
            synchronized (mHandlers) {
                mHandlers.add(handler);
            }
        }

        @Override
        public void onReceive(Context context, Intent intent) {

            final String action = intent.getAction();
            if (TextUtils.isEmpty(action)) {
                return;
            }

            try {
                synchronized (mHandlers) {
                    for (final BroadcastEventHandler handler : mHandlers) {
                        if (StringUtils.areEqual(handler.action, action)) {
                            handler.onReceive(context, intent);
                        }
                    }
                }
            } catch (Throwable t) {
                Logger.e("handle received action failed, action: %s,%s", action, t);
            }
        }
    }
}