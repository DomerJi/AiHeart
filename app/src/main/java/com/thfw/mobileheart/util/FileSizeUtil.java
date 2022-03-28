package com.thfw.mobileheart.util;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.thfw.base.ContextApp;
import com.thfw.base.utils.ToastUtil;

import java.io.File;
import java.io.FileInputStream;
import java.text.DecimalFormat;

public class FileSizeUtil {

    public static final String TAG = FileSizeUtil.class.getSimpleName();

    public static final int SIZETYPE_B = 1;//获取文件大小单位为B的double值
    public static final int SIZETYPE_KB = 2;//获取文件大小单位为KB的double值
    public static final int SIZETYPE_MB = 3;//获取文件大小单位为MB的double值
    public static final int SIZETYPE_GB = 4;//获取文件大小单位为GB的double值

    /**
     * 获取文件指定文件的指定单位的大小
     *
     * @param filePath 文件路径
     * @param sizeType 获取大小的类型1为B、2为KB、3为MB、4为GB
     * @return double值的大小
     */
    public static double getFileOrFilesSize(String filePath, int sizeType) {
        File file = new File(filePath);
        long blockSize = 0;
        try {
            if (file.isDirectory()) {
                blockSize = getFileSizes(file);
            } else {
                blockSize = getFileSize(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("获取文件大小", "获取失败!");
        }
        return AppCache.formatFileSize(blockSize, sizeType);
    }

    /**
     * 调用此方法自动计算指定文件或指定文件夹的大小
     *
     * @param filePath 文件路径
     * @return 计算好的带B、KB、MB、GB的字符串
     */
    public static String getAutoFileOrFilesSize(String filePath) {
        File file = new File(filePath);
        long blockSize = 0;
        try {
            if (file.isDirectory()) {
                blockSize = getFileSizes(file);
            } else {
                blockSize = getFileSize(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("获取文件大小", "获取失败!");
        }
        return formatFileSize(blockSize);
    }

    /**
     * 获取指定文件大小
     *
     * @param
     * @return
     * @throws Exception
     */
    private static long getFileSize(File file) throws Exception {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            size = fis.available();
        } else {
            file.createNewFile();
            Log.e("获取文件大小", "文件不存在!");
        }
        return size;
    }

    /**
     * 获取指定文件夹
     *
     * @param f
     * @return
     * @throws Exception
     */
    private static long getFileSizes(File f) throws Exception {
        long size = 0;
        File flist[] = f.listFiles();
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + getFileSizes(flist[i]);
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
    private static String formatFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
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

    public static class AppCache {
        /**
         * 获取app的缓存大小
         * 3. 下载的更新包/storage/emulated/0/Android/data/com.sdxzt.xueliangapp_v3/files/Download/xueliang_update.apk
         * 4. 缓存/storage/emulated/0/Android/data/com.sdxzt.xueliangapp_v3/cache
         */
        File filesDir, cacheDir;

        /**
         * 转换文件大小,指定转换的类型
         *
         * @param fileS
         * @param sizeType
         * @return
         */
        private static double formatFileSize(long fileS, int sizeType) {
            DecimalFormat df = new DecimalFormat("#.00");
            double fileSizeLong = 0;
            switch (sizeType) {
                case SIZETYPE_B:
                    fileSizeLong = Double.valueOf(df.format((double) fileS));
                    break;
                case SIZETYPE_KB:
                    fileSizeLong = Double.valueOf(df.format((double) fileS / 1024));
                    break;
                case SIZETYPE_MB:
                    fileSizeLong = Double.valueOf(df.format((double) fileS / 1048576));
                    break;
                case SIZETYPE_GB:
                    fileSizeLong = Double.valueOf(df.format((double) fileS / 1073741824));
                    break;
                default:
                    break;
            }
            return fileSizeLong;
        }

        public String getAppCache() {
            long fileSize = getAppCacheLong();
            String fileSizeStr = formatFileSize(fileSize);
            Log.d(TAG, "getAppCache: 总缓存大小: " + fileSizeStr);
            return fileSizeStr;
        }

        public long getAppCacheLong() {
            long fileSize = 0;
            filesDir = ContextApp.get().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
            Log.d(TAG, "getAppCache: filesDir大小: " + getDirSize(filesDir));
            ///storage/emulated/0/Android/data/com.sdxzt.xueliangapp_v3/files,这里面有download文件夹,里面是下载的更新包
            cacheDir = ContextApp.get().getExternalCacheDir();
            Log.d(TAG, "getAppCache: cacheDir大小: " + getDirSize(cacheDir));
            ///storage/emulated/0/Android/data/com.sdxzt.xueliangapp_v3/cache
            fileSize += getDirSize(ContextApp.get().getFilesDir());
            fileSize += getDirSize(ContextApp.get().getCacheDir());// 这行是默认的缓存地址,看图片什么的会在这里积累缓存
            fileSize += getDirSize(filesDir);
            fileSize += getDirSize(cacheDir);
            return fileSize;
        }

        /**
         * 获取文件大小(字节为单位)
         *
         * @param dir
         * @return
         */
        private long getDirSize(File dir) {
            if (dir == null) {
                return 0;
            }
            if (!dir.isDirectory()) {
                return 0;
            }
            long dirSize = 0;
            File[] files = dir.listFiles();
            for (File file : files) {
                if (file.isFile()) {
                    dirSize += file.length();//文件的长度就是文件的大小
                } else if (file.isDirectory()) {
                    dirSize += file.length();
                    dirSize += getDirSize(file); // 递归调用继续统计
                }
            }
            return dirSize;
        }

        /**
         * 格式化文件长度
         *
         * @param fileSize
         * @return
         */
        public String formatFileSize(long fileSize) {
            DecimalFormat df = new DecimalFormat("#0.00");//表示小数点前至少一位,0也会显示,后保留两位

            String fileSizeString = "";
            if (fileSize < 1024) {
                fileSizeString = df.format((double) fileSize) + "B";
            } else if (fileSize < 1048576) {
                fileSizeString = df.format((double) fileSize / 1024) + "KB";
            } else if (fileSize < 1073741824) {
                fileSizeString = df.format((double) fileSize / 1048576) + "MB";
            } else {
                fileSizeString = df.format((double) fileSize / 1073741824) + "G";
            }
            return fileSizeString;
        }

        public void clearAppCache(clearCacheListener listener) {

            final Handler handler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(@NonNull Message msg) {
                    super.handleMessage(msg);
                    Log.d(TAG, "handlerMessage: ");
                    if (msg.what == 1) {
                        String fileSize = getAppCache();
                        Log.d(TAG, "setAppCache: 重新显示缓存大小");
                        Log.d(TAG, "setAppCache: 当前缓存" + fileSize);
                        Log.d(TAG, "handlerMessage: 缓存清除完毕");
                        if (listener != null) {
                            listener.onSuccess(fileSize);
                        }
                    } else {
                        Log.d(TAG, "handlerMessage: 缓存清除失败");
                        if (listener != null) {
                            listener.onFail();
                        }
                    }
                }
            };

            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "run: ");
                    Message msg = new Message();
                    try {
                        clearCacheFolder(ToastUtil.getAppContext().getCacheDir(), System.currentTimeMillis());
                        clearCacheFolder(ToastUtil.getAppContext().getFilesDir(), System.currentTimeMillis());
                        clearCacheFolder(filesDir, System.currentTimeMillis());
                        clearCacheFolder(cacheDir, System.currentTimeMillis());
                        msg.what = 1;
                    } catch (Exception e) {
                        e.printStackTrace();
                        msg.what = -1;
                    }
                    handler.sendMessage(msg);
                }
            }).start();
        }

        /**
         * 清除缓存目录
         *
         * @param dir     目录
         * @param curTime 当前系统时间
         */
        private int clearCacheFolder(File dir, long curTime) {
            int deletedFiles = 0;
            if (dir != null && dir.isDirectory()) {
                try {
                    for (File child : dir.listFiles()) {
                        if (child.isDirectory()) {
                            deletedFiles += clearCacheFolder(child, curTime);
                        }
                        if (child.lastModified() < curTime) {
                            if (child.delete()) {
                                deletedFiles++;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Log.d(TAG, "clearCacheFolder: 清除目录: " + dir.getAbsolutePath());
            return deletedFiles;
        }


        public interface clearCacheListener {

            void onSuccess(String fileSize);

            void onFail();
        }
    }


}