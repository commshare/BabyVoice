package com.lihb.babyvoice.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

import java.util.ArrayList;

/**
 * Created by lau on 2016/6/7.
 */
// 身体传感器
// 日历
// 摄像头
// 通讯录
// 地理位置
// 麦克风
// 电话
// 短信
// 存储空间     Manifest.permission.WRITE_EXTERNAL_STORAGE


public class RuntimePermissionsUtil {

    public static ArrayList<String> getDeniedPermissions(Activity act, String... permissions) {
        return null;
    }

    public static void startInstalledAppDetailsActivity(final Activity context) {
        if (context == null) {
            return;
        }
        final Intent i = new Intent();
        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setData(Uri.parse("package:" + context.getPackageName()));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivity(i);
    }

    public static void startInstalledAppDetailsActivity(Activity context, int requestCode) {
        context.startActivityForResult(getIntent(context), requestCode);
    }

    private static Intent getIntent(Activity context) {
        if (context != null) {
            Intent i = new Intent();
            i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            i.addCategory(Intent.CATEGORY_DEFAULT);
            i.setData(Uri.parse("package:" + context.getPackageName()));
            i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            return i;
        }
        return null;
    }
}
