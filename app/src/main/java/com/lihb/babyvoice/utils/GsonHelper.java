package com.lihb.babyvoice.utils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.logger.Logger;

import java.io.Reader;
import java.lang.reflect.Type;

/**
 * Created by ZZB on 2015/11/17.
 */
public class GsonHelper {

    private static final String TAG = "GsonHelper";
    private static final Gson sGson = new Gson();
    private static final JsonParser sJsonParser = new JsonParser();

    public static String objectToJson(Object obj) {
        String json = "";
        try {
            json = getGson().toJson(obj);
        } catch (Throwable e) {
            Logger.e(TAG, e);
        }
        return json;
    }

    private static Gson getGson() {
        //        if (sGson == null) {
        //            GsonBuilder gsonBuilder = new GsonBuilder();
        //            //            gsonBuilder.excludeFieldsWithoutExposeAnnotation();
        //            sGson = gsonBuilder.create();
        //        }
        return sGson;
    }

    public static <T> T jsonToObject(String json, Class<T> clazz) {
        T t = null;
        try {
            t = getGson().fromJson(json, clazz);
        } catch (Throwable e) {
            Logger.e(TAG, e);
        }
        return t;
    }

    //只获取json某个key的值
    public static <T> T jsonToObject(String json, String key, Class<T> clazz) {
        try {
            JsonObject root = (JsonObject) sJsonParser.parse(json);
            JsonElement data = root.get(key);
            return getGson().fromJson(data, clazz);
        } catch (Exception e) {
            Logger.e(TAG, e);
            return null;
        }
    }

    public static <T> T jsonToObject(String json, TypeToken<T> token) {
        return jsonToObject(json, token.getType());
    }

    public static <T> T jsonToObject(String json, Type typeOfT) {
        T t = null;
        try {
            t = getGson().fromJson(json, typeOfT);
        } catch (Throwable e) {
            Logger.e(TAG, e);
        }
        return t;
    }

    public static <T> T jsonToObject(JsonElement json, TypeToken<T> token) {
        return jsonToObject(json, token.getType());
    }

    public static <T> T jsonToObject(JsonElement json, Type typeOfT) {
        T t = null;
        try {
            t = getGson().fromJson(json, typeOfT);
        } catch (Throwable e) {
            Logger.e(TAG, e);
        }
        return t;
    }

    public static <T> T jsonToObject(Reader reader, Class<T> clazz) {
        T t = null;
        try {
            t = getGson().fromJson(reader, clazz);
        } catch (Throwable e) {
            Logger.e(TAG, e);
        }
        return t;
    }

    public static <T> T jsonToObject(Reader reader, Type type) {
        T t = null;
        try {
            t = getGson().fromJson(reader, type);
        } catch (Throwable e) {
            Logger.e(TAG, e);
        }
        return t;
    }

    public static String getAsString(String json, String key) {
        String value = "";
        try {
            JsonObject root = (JsonObject) getJsonParser().parse(json);
            value = root.get(key).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    public static JsonParser getJsonParser() {
        //        if (sJsonParser == null) {
        //            sJsonParser = new JsonParser();
        //        }
        return sJsonParser;
    }
}
