package com.lihb.babyvoice.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Administrator on 2017/4/9.
 */

public class SharedPreferencesUtil {

    private final static String ISFIRST_LAUNCH_KEY = "isfirst_launch_pref_key";
    private final static String ISFIRST_SET_PREGNANT_DATE_KEY = "ISFIRST_SET_PREGNANT_DATE_KEY";
    private final static String ISFIRST_SET_BABY_BIRTHDAY_KEY = "ISFIRST_SET_BABY_BIRTHDAY_KEY";
    private final static String SET_COOKIE_KEY = "SET_COOKIE_KEY";

    public static boolean isFirstLaunch(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(ISFIRST_LAUNCH_KEY, true);
    }

    public static void setFirstLaunch(Context context, boolean firstLaunch) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().putBoolean(ISFIRST_LAUNCH_KEY, firstLaunch).apply();
    }

    public static boolean isFirstSetPregnantDate(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(ISFIRST_SET_PREGNANT_DATE_KEY, true);
    }

    public static void setFirstPregnantDate(Context context,boolean firstSetPregnantDate) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().putBoolean(ISFIRST_SET_PREGNANT_DATE_KEY, firstSetPregnantDate).apply();
    }

    public static boolean isFirstSetBabyBirthday(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(ISFIRST_SET_BABY_BIRTHDAY_KEY, true);
    }

    public static void setFirstBabyBirthday(Context context,boolean firstSetBabyBirthday) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().putBoolean(ISFIRST_SET_BABY_BIRTHDAY_KEY, firstSetBabyBirthday).apply();
    }

    public static String getPregnantDateInfo(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("pregnant_date_info", "0/0/0");
    }

    public static void setPregnantDateInfo(Context context, String pregnantInfo) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().putString("pregnant_date_info", pregnantInfo).apply();
    }

    public static String getBabyBirthDayInfo(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("baby_birthday_date_info", "0/0/0");
    }

    public static void setBabyBirthDayInfo(Context context, String BabyBirthDay) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().putString("baby_birthday_date_info", BabyBirthDay).apply();
    }

    public static String getCookie(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(SET_COOKIE_KEY, "");
    }

    public static void setCookie(Context context, String cookieValue) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().putString(SET_COOKIE_KEY, cookieValue).apply();
    }

    /**
     * 简单保存到SharedPreferences中
     * @param username
     * @param password
     */
    public static void saveToPreferences(Context context,String username, String password) {
        //创建sharedPreference对象，info表示文件名，MODE_PRIVATE表示访问权限为私有的
        SharedPreferences sp = context.getSharedPreferences("userinfo", Context.MODE_PRIVATE);

        //获得sp的编辑器
        SharedPreferences.Editor ed = sp.edit();

        //以键值对的显示将用户名和密码保存到sp中
        ed.putString("username", username);
        ed.putString("password", password);

        //提交用户名和密码
        ed.apply();
    }

}
