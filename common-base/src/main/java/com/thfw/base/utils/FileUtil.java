package com.thfw.base.utils;

import android.text.TextUtils;

import java.io.File;

import okhttp3.ResponseBody;

public class FileUtil {

    public static void deleteFile(String fileNanme) {
        if (!TextUtils.isEmpty(fileNanme)) {
            // 删除源文件
            File file = new File(fileNanme);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    public void saveFile(ResponseBody body) {
//        state = DownloadTaskState.DOWNLOADING;
//        byte[] buf = new byte[2048];
//        int len;
//        FileOutputStream fos = null;
//        try {
//            Log.d(TAG, "saveFile: body content length: " + body.contentLength());
//            srcInputStream = body.byteStream();
//            File dir = tmpFile.getParentFile();
//            if (dir == null) {
//                throw new FileNotFoundException("target file has no dir.");
//            }
//            if (!dir.exists()) {
//                boolean m = dir.mkdirs();
//                onInfo("Create dir " + m + ", " + dir);
//            }
//            File file = tmpFile;
//            if (!file.exists()) {
//                boolean c = file.createNewFile();
//                onInfo("Create new file " + c);
//            }
//            fos = new FileOutputStream(file);
//            long time = System.currentTimeMillis();
//            while ((len = srcInputStream.read(buf)) != -1 && !isCancel) {
//                fos.write(buf, 0, len);
//                int duration = (int) (System.currentTimeMillis() - time);
//
//                int overBytes = len - downloadBytePerMs() * duration;
//                if (overBytes > 0) {
//                    try {
//                        Thread.sleep(overBytes / downloadBytePerMs());
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//                time = System.currentTimeMillis();
//                if (isCancel) {
//                    state = DownloadTaskState.CLOSING;
//                    srcInputStream.close();
//                    break;
//                }
//            }
//            if (!isCancel) {
//                fos.flush();
//                boolean rename = tmpFile.renameTo(targetFile);
//                if (rename) {
//                    setState(DownloadTaskState.DONE);
//                    onSuccess(url);
//                } else {
//                    setState(DownloadTaskState.ERROR);
//                    onError(url, new Exception("Rename file fail. " + tmpFile));
//                }
//            }
//        } catch (FileNotFoundException e) {
//            Log.e(TAG, "saveFile: FileNotFoundException ", e);
//            setState(DownloadTaskState.ERROR);
//            onError(url, e);
//        } catch (Exception e) {
//            Log.e(TAG, "saveFile: IOException ", e);
//            setState(DownloadTaskState.ERROR);
//            onError(url, e);
//        } finally {
//            try {
//                if (srcInputStream != null) {
//                    srcInputStream.close();
//                }
//                if (fos != null) {
//                    fos.close();
//                }
//            } catch (IOException e) {
//                Log.e(TAG, "saveFile", e);
//            }
//            if (isCancel) {
//                onCancel(url);
//            }
//        }
    }
}
