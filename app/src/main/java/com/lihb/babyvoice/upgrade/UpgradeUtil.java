package com.lihb.babyvoice.upgrade;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.allenliu.versionchecklib.AVersionService;
import com.allenliu.versionchecklib.HttpRequestMethod;
import com.allenliu.versionchecklib.VersionParams;

/**
 * Created by lihb on 2017/6/28.
 */

public class UpgradeUtil {

    public static final int FROM_MAIN_ACTIVITY = 1;
    public static final int FROM_ME_FRAGMENT = 2;
    public static final String  FROM = "from";

    public static void checkUpgrade(Context context, int from){
        VersionParams versionParams=null;
        context.stopService(new Intent(context,UpgradeService.class));

        versionParams = new VersionParams()
                .setRequestUrl("http://www.baidu.com")
                .setRequestMethod(HttpRequestMethod.GET);

        Intent intent = new Intent(context, UpgradeService.class);
        intent.putExtra(AVersionService.VERSION_PARAMS_KEY, versionParams);
        intent.putExtra(FROM, from);
        context.startService(intent);
    }

    /*
     * 获取当前程序的版本号
    */
    public static String getVersionName(Context context){
        //获取packagemanager的实例
        PackageManager packageManager = context.getPackageManager();
        //getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "0.0";
        }
    }


}
