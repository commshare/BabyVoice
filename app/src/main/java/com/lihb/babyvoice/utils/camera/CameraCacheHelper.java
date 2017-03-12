package com.lihb.babyvoice.utils.camera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.util.LruCache;
import android.util.Log;

import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * 用于相机的缓存工具类
 * Created by youhouchang on 2016/1/11.
 */
public class CameraCacheHelper {

    public static final String TAG = "CameraCacheHelper";
    private static CameraCacheHelper instance = null;
    private LruCache<String,Bitmap> mMemoryCache = null;
    private static final int MEMORY_SIZE = (int) (Runtime.getRuntime().maxMemory() / 1024);
    public static Set<SoftReference<Bitmap>> mReuseableBitmap;
    
    private CameraCacheHelper(){
        // 初始化Reuseable Bitmap集合
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            mReuseableBitmap = Collections.synchronizedSet(new HashSet<SoftReference<Bitmap>>());
        }

        final int cacheSize = MEMORY_SIZE / 10;
        Log.d(TAG , String.format("memory size is %d , cacheSize is %d" , MEMORY_SIZE, cacheSize));
        mMemoryCache = new LruCache<String,Bitmap>(cacheSize){
            @Override
            protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
                super.entryRemoved(evicted, key, oldValue, newValue);
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
                    Log.i(TAG,String.format("entry removed , old value is %s , new value is %s",oldValue,newValue));
                    mReuseableBitmap.add(new SoftReference<Bitmap>(oldValue));
                }
            }

            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount() / 1024;
            }
        };
    }

    /**
     * CameraCacheHelper及其MemoryCache为单例
     * @return
     */
    public static CameraCacheHelper get(){
        if (instance == null){
            synchronized (CameraCacheHelper.class){
                if (instance == null){
                    instance = new CameraCacheHelper();
                }
            }
        }
        return instance;
    }

    public static LruCache<String, Bitmap> getMemoryCache() {
        return get().mMemoryCache;
    }
    
    /**
     * 返回不同Bitmap配置一个像素占的Byte
     * @param config
     * @return
     */
    private int getBytesPerPixel(Bitmap.Config config) {
        if(config == Bitmap.Config.ARGB_8888) {
            return 4;
        } else if(config == Bitmap.Config.RGB_565) {
            return 2;
        } else if(config == Bitmap.Config.ARGB_4444) {
            return 2;
        } else if(config == Bitmap.Config.ALPHA_8) {
            return 1;
        }
        return 1;
    }

    /**
     * 判断该Bitmap是否可以复用
     * @param candidate
     * @param options
     * @return
     */
    private boolean canUseForBitmap(Bitmap candidate, BitmapFactory.Options options) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int width = options.outWidth / options.inSampleSize;
            int height = options.outHeight / options.inSampleSize;
            int byteCount = width * height * getBytesPerPixel(candidate.getConfig());
            Log.d(TAG,"byteCount = " + byteCount);
            Log.d(TAG,"candidate.getAllocationByteCount() = " + candidate.getAllocationByteCount());
            return byteCount <= candidate.getAllocationByteCount();
        }
        return candidate.getWidth() == options.outWidth &&
                candidate.getHeight() == options.outHeight &&
                options.inSampleSize == 1;
    }

    /**
     * 从ReuseableSet中获取Bitmap
     * @param options
     * @return
     */
    public Bitmap getBitmapFromReuseableSet(BitmapFactory.Options options) {
        Bitmap bitmap = null;
        if(mReuseableBitmap != null && !mReuseableBitmap.isEmpty()) {
            synchronized (mReuseableBitmap) {
                Bitmap item;
                Iterator<SoftReference<Bitmap>> iterator = mReuseableBitmap.iterator();
                while(iterator.hasNext()) {
                    item = iterator.next().get();
                    if(item != null && item.isMutable()) {
                        boolean canUseForBitmap = canUseForBitmap(item, options);
                        Log.d(TAG,String.format("item isRecycled() = %b , item canUseForBitmap = %b",item.isRecycled(),canUseForBitmap));
                        if(!item.isRecycled() && canUseForBitmap) {
                            Log.d(TAG,String.format("can reuse inBitmap memory , bitmap is %s",item.toString()));
                            bitmap = item;
                            iterator.remove();
                            break;
                        }
                    } else {
                        if (item == null) {
                            Log.d(TAG,"sorry , can not reuse inBitmap memory , item == null ");
                        }else {
                            Log.d(TAG,"sorry , can not reuse inBitmap memory , item != null , but is not mutable");
                        }
                        iterator.remove();
                    }
                }
            }
        }
        return bitmap;
    }

}
