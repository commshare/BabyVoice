package com.lihb.babyvoice.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.text.TextUtils;

import com.lihb.babyvoice.BabyVoiceApp;
import com.lihb.babyvoice.db.impl.PregnantDataImpl;
import com.lihb.babyvoice.db.impl.VaccineDataImpl;
import com.lihb.babyvoice.model.ProductionInspection;
import com.lihb.babyvoice.model.VaccineInfo;
import com.orhanobut.logger.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static com.lihb.babyvoice.BabyVoiceApp.DATA_DIRECTORY;

public class FileUtils {
    public static final int SIZETYPE_B = 1;//获取文件大小单位为B的double值
    public static final int SIZETYPE_KB = 2;//获取文件大小单位为KB的double值
    public static final int SIZETYPE_MB = 3;//获取文件大小单位为MB的double值
    public static final int SIZETYPE_GB = 4;//获取文件大小单位为GB的double值

    /**
     * <br>功能简述:图片缓存的路径信息
     * <br>功能详细描述:
     * <br>注意:当sd card没有挂载时，会返回null
     *
     * @return
     */
    public static String getHeadShotPath() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED) ? Environment
                .getExternalStorageDirectory().getPath() + "/BabyVoice/headshot/" : null;
    }

    /**
     * 保存Image的方法，有sd卡存储到sd卡，没有就存储到手机目录
     *
     * @param fileName
     * @param bitmap
     * @throws IOException
     */
    public static void savaBitmap(String path, String fileName, Bitmap bitmap)
            throws IOException {
        if (bitmap == null) {
            return;
        }
        File folderFile = new File(path);
        if (!folderFile.exists()) {
            folderFile.mkdirs();
        }
        File file = new File(path, fileName);
        file.createNewFile();
        FileOutputStream fos = new FileOutputStream(file);
        bitmap.compress(CompressFormat.JPEG, 100, fos);
        fos.flush();
        fos.close();
    }

    /**
     * 从手机或者sd卡获取Bitmap
     *
     * @param filePath
     * @return
     */
    public static Bitmap getBitmap(String filePath) {
        return BitmapFactory.decodeFile(filePath);
    }


    /**
     * 根据路径加载bitmap
     *
     * @param path 路径
     * @param w    款
     * @param h    长
     * @return
     */
    public static final Bitmap decodeBitmap(String path, int w, int h) {
        try {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            // 设置为ture只获取图片大小
            opts.inJustDecodeBounds = true;
            opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
            // 返回为空
            BitmapFactory.decodeFile(path, opts);
            int width = opts.outWidth;
            int height = opts.outHeight;
            float scaleWidth = 0.f, scaleHeight = 0.f;
            if (width > w || height > h) {
                // 缩放
                scaleWidth = ((float) width) / w;
                scaleHeight = ((float) height) / h;
            }
            opts.inJustDecodeBounds = false;
            float scale = Math.max(scaleWidth, scaleHeight);
            opts.inSampleSize = (int) scale;
            WeakReference<Bitmap> weak = new WeakReference<Bitmap>(BitmapFactory.decodeFile(path, opts));
            Bitmap bMapRotate = Bitmap.createBitmap(weak.get(), 0, 0, weak.get().getWidth(), weak.get().getHeight(), null, true);
            if (bMapRotate != null) {
                return bMapRotate;
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 判断文件是否存在
     *
     * @param filePath
     * @return
     */
    public static boolean isFileExists(String filePath) {
        return new File(filePath).exists();
    }

    /**
     * 获取文件的大小
     *
     * @param file
     * @return
     */
    private static long getFileSize(File file) {
        if (file.exists()) {
            return file.length();
        }

        return 0;
    }

    /**
     * 获取指定文件夹
     *
     * @param f
     * @return
     * @throws Exception
     */
    public static long getFilesSize(File f) throws Exception {
        long size = 0;
        File flist[] = f.listFiles();
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + getFilesSize(flist[i]);
            } else {
                size = size + getFileSize(flist[i]);
            }
        }
        return size;
    }

    /**
     * 转换文件大小
     *
     * @param fileS
     * @return
     */
    public static String FormetFileSize(long fileS) {
        // 蛋疼需求,要求取整.
        DecimalFormat df = new DecimalFormat("#");
//		DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        }
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
        }
        return fileSizeString;
    }

    /**
     * 转换文件大小,指定转换的类型
     *
     * @param fileS
     * @param sizeType
     * @return
     */
    private static double FormetFileSize(long fileS, int sizeType) {
        DecimalFormat df = new DecimalFormat("#.00");
        double fileSize = 0;
        switch (sizeType) {
            case SIZETYPE_B:
                fileSize = Double.valueOf(df.format((double) fileS));
                break;
            case SIZETYPE_KB:
                fileSize = Double.valueOf(df.format((double) fileS / 1024));
                break;
            case SIZETYPE_MB:
                fileSize = Double.valueOf(df.format((double) fileS / 1048576));
                break;
            case SIZETYPE_GB:
                fileSize = Double.valueOf(df.format((double) fileS / 1073741824));
                break;
            default:
                break;
        }
        return fileSize;
    }

    /**
     * 删除SD卡或者手机的缓存图片和目录
     */
    public static void deleteFile(String filePath) {
        File dirFile = new File(filePath);
        if (!dirFile.exists()) {
            return;
        }
        if (dirFile.isDirectory()) {
            String[] children = dirFile.list();
            for (int i = 0; i < children.length; i++) {
                File subFile = new File(dirFile.getAbsolutePath() + File.separator + children[i]);
                if (subFile.isDirectory()) {
                    deleteFile(subFile.getAbsolutePath());
                } else {
                    final File to = new File(subFile.getAbsolutePath() + System.currentTimeMillis());
                    subFile.renameTo(to);
                    to.delete();
//					new File(dirFile, children[i]).delete();
                }
            }
        }

        dirFile.delete();
    }

    /**
     * 只删除dir下面的文件，不删除dir
     *
     * @param filePath
     */
    public static void deleteDirFiles(String filePath) {
        File dirFile = new File(filePath);
        if (!dirFile.exists()) {
            return;
        }
        if (dirFile.isDirectory()) {
            String[] children = dirFile.list();
            for (int i = 0; i < children.length; i++) {
                File subFile = new File(dirFile.getAbsolutePath() + File.separator + children[i]);
                if (subFile.isDirectory()) {
                    deleteFile(subFile.getAbsolutePath());
                } else {
                    final File to = new File(subFile.getAbsolutePath() + System.currentTimeMillis());
                    subFile.renameTo(to);
                    to.delete();
//					new File(dirFile, children[i]).delete();
                }
            }
        }
    }

    /**
     * 复制单个文件
     *
     * @param oldPath String 原文件路径
     * @param newPath String 复制后路径
     * @return boolean
     */
    public static boolean copyFile(String oldPath, String newPath) {
        boolean result = false;
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { // 文件存在时
                File newFile = new File(newPath);
                if (!newFile.getParentFile().exists()) {
                    newFile.getParentFile().mkdirs();
                }
                InputStream inStream = new FileInputStream(oldPath); // 读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; // 字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
                fs.close();
                result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void renameFile(String oldPath, String newPath) {
        File oldFile = new File(oldPath);
        File newFile = new File(newPath);
        oldFile.renameTo(newFile);
    }

    public static String getFileSuffix(String filePath) {
        int index = filePath.lastIndexOf(".");
        String suffix = "";
        if (index != -1) {
            suffix = filePath.substring(index + 1);
        }
        return suffix;
    }

    /**
     * 获取编码后的AMR格式音频文件路径
     *
     * @return
     */
    public static String getVoiceFilePath(String fileName) {
        String mAudioAMRPath = "";
        if (isSdcardExit()) {
            mAudioAMRPath = DATA_DIRECTORY  + fileName;

        }
        return mAudioAMRPath;
    }

    /**
     * 判断是否有外部存储设备sdcard
     *
     * @return true | false
     */
    public static boolean isSdcardExit() {
        if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }

    /**
     * 从raw文件夹中读取数据
     * @param context
     * @param rawId
     * @return
     */
    public static String getFromRaw(Context context,int rawId){
        try {
            InputStreamReader inputReader = new InputStreamReader(context.getResources().openRawResource(rawId));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line="";
            String Result="";
            while((line = bufReader.readLine()) != null)
                Result += line;
            return Result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 创建不同的目录
     */
    public static void createDirectory(String path) {
        if (isSdcardExit()) {
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
        } else {
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
        }
    }

    /**
     * 从assets文件夹中读取数据
     * @param context
     * @param fileName
     * @return
     */
    public String getFromAssets(Context context, String fileName){
        try {
            InputStreamReader inputReader = new InputStreamReader(context.getResources().getAssets().open(fileName) );
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line="";
            String Result="";
            while((line = bufReader.readLine()) != null)
                Result += line;
            return Result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static  List<ProductionInspection> getPregnantData(Context context) {
        List<ProductionInspection> dataList = new ArrayList<>();
        try {
            InputStreamReader inputReader = new InputStreamReader(context.getResources().getAssets().open("pregnant.txt"));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";
            String[] array;
            while ((line = bufReader.readLine()) != null) {
                array = line.split("\\*+");
                ProductionInspection productionInspection = new ProductionInspection();
                productionInspection.no = Integer.valueOf(array[0]);
                productionInspection.event_id = Integer.valueOf(array[1]);
                productionInspection.week = array[2];
                productionInspection.event_name = array[3];
                productionInspection.event_name_en = array[4];
                productionInspection.isDone = Integer.valueOf(array[5]);
                dataList.add(productionInspection);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return dataList;
    }

    public static void insertPregnantData(List<ProductionInspection> dataList) {
        PregnantDataImpl.getInstance()
                .batchInsertData(dataList)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void avoid) {
                        Logger.i("insert PregnantData success.");
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Logger.e("insert PregnantData failed.", throwable.getMessage());
                    }
                });
    }

    public static void queryPregnantData() {
        PregnantDataImpl.getInstance()
                .queryAllData()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<List<ProductionInspection>>() {
                    @Override
                    public void call(List<ProductionInspection> productionInspections) {
                        Logger.i("query PregnantData success.");
                        for (ProductionInspection inspection : productionInspections) {
                            Logger.i(inspection.toString());
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        CommonToast.showShortToast("query PregnantData failed." + throwable.getMessage());
                    }
                });
    }

    public static List<VaccineInfo> getVaccineData(Context context) {
        List<VaccineInfo> dataList = new ArrayList<>();
        try {
            InputStreamReader inputReader = new InputStreamReader(context.getResources().getAssets().open("vaccine.txt"));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";
            String[] array;
            while ((line = bufReader.readLine()) != null) {
                array = line.split("\\*+");
                VaccineInfo vaccineInfo = new VaccineInfo();
                vaccineInfo.vaccineName = array[0];
                vaccineInfo.vaccineNameEn = array[1];
                vaccineInfo.isFree = Integer.valueOf(array[2]);
                vaccineInfo.isInjected = Integer.valueOf(array[3]);
                vaccineInfo.ageToInject = Integer.valueOf(array[4]);
                vaccineInfo.injectDate = array[5];
                dataList.add(vaccineInfo);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return dataList;
    }

    public static void insertVaccineData(List<VaccineInfo> dataList) {
        VaccineDataImpl.getInstance()
                .batchInsertData(dataList)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void avoid) {
                        Logger.i("insert vaccineData success.");
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Logger.e("insert vaccineData failed. " + throwable.getMessage());
                    }
                });
    }

    public static String getVoiceDuration(String path) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(path); //在获取前，设置文件路径（应该只能是本地路径）
        String duration =  retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        retriever.release(); //释放
        if(!TextUtils.isEmpty(duration)){
            long dur = Long.parseLong(duration) / 1000;
            return dur+"";
        }
        return -1+"";
    }

    /**
     * 拼接得到图片下载地址
     * @param picName
     * @return
     */
    public static String getPicUrl(String picName) {
        StringBuilder sb = new StringBuilder("http://123.207.46.152:8080/itingbaby/upload/uploadImages/");
        sb.append(BabyVoiceApp.currUserName).append("/");
        sb.append(picName);
        return sb.toString();
    }

}
