package com.lihb.babyvoice.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 *
 */
public class PermissionCheckUtil {

    public static final int REQUEST_CODE_SD_PERMISSION = 10087;

    public static boolean checkHasPermission(Activity activity, String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean checkSdPermission(Activity activity, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
            return false;
        } else {
            return true;
        }
    }

    public static boolean sdPermissionGranted(boolean requestCodeMatch, String[] permissions, int[] grantResults) {
        return requestCodeMatch && grantResults != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
    }

    public static void showGrantFailDialog(final Activity activity) {
        //权限申请失败
        showGrantFailDialog(activity, "您关闭了访问存储空间的权限！去手机设置中修改吧~");
    }

    public static void showGrantFailDialog(final Activity activity, String msg) {

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(activity);
        builder.setTitle("Material Design Dialog");
        builder.setMessage("这是 android.support.v7.app.AlertDialog 中的样式");
        builder.setNegativeButton("取消", (dialog, which) -> dialog.dismiss());
        builder.setPositiveButton("去设置", (dialog, which) -> {
            RuntimePermissionsUtil.startInstalledAppDetailsActivity(activity);
        });
        if (!activity.isFinishing()) {
            builder.show();
        }

    }
}
