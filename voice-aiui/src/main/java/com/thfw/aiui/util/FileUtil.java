package com.thfw.aiui.util;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FileUtil {
    private String WRITE_PCM_DIR = "";

    private final static String PCM_SURFFIX = ".pcm";

    private FileOutputStream mFos;

    private FileInputStream mFis;

    public FileUtil(String writeDir) {
        WRITE_PCM_DIR = writeDir;
    }

    public boolean openPcmFile(String filePath) {
        File file = new File(filePath);
        try {
            mFis = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            mFis = null;
            return false;
        }

        return true;
    }

    public int read(byte[] buffer) {
        if (null != mFis) {
            try {
                return mFis.read(buffer);
            } catch (IOException e) {
                e.printStackTrace();
                closeReadFile();
                return 0;
            }
        }

        return -1;
    }

    public void closeReadFile() {
        if (null != mFis) {
            try {
                mFis.close();
                mFis = null;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void createPcmFile() {
        File dir = new File(WRITE_PCM_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        if (null != mFos) {
            return;
        }

        DateFormat df = new SimpleDateFormat("MM-dd-hh-mm-ss", Locale.CHINA);
        String filename = df.format(new Date());
        String pcmPath = WRITE_PCM_DIR + filename  + PCM_SURFFIX;

        File pcm = new File(pcmPath);
        try {
            if(pcm.createNewFile()) {
                mFos = new FileOutputStream(pcm);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write(byte[] data) {
        synchronized (FileUtil.this) {
            if (null != mFos) {
                try {
                    mFos.write(data);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    public void write(byte[] data, int offset, int len) {
        synchronized (FileUtil.this) {
            if (null != mFos) {
                try {
                    mFos.write(data, offset, len);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    public void closeWriteFile() {
        synchronized (FileUtil.this) {
            if (null != mFos) {
                try {
                    mFos.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                mFos = null;
            }
        }
    }


    /**
     * 复制asset文件到指定目录
     * @param oldPath  asset下的路径
     * @param newPath  SD卡下保存路径\
     */
    public static void CopyAssets(Context context, String oldPath, String newPath) {
        try {
            String fileNames[] = context.getAssets().list(oldPath);// 获取assets目录下的所有文件及目录名
            if (fileNames.length > 0) {// 如果是目录
                File file = new File(newPath);
                file.mkdirs();// 如果文件夹不存在，则递归
                for (String fileName : fileNames) {
                    CopyAssets(context, oldPath + "/" + fileName, newPath + "/" + fileName);
                }
            } else {// 如果是文件
                InputStream is = context.getAssets().open(oldPath);
                FileOutputStream fos = new FileOutputStream(new File(newPath));
                byte[] buffer = new byte[1024];
                int byteCount = 0;
                while ((byteCount = is.read(buffer)) != -1) {// 循环从输入流读取
                    // buffer字节
                    fos.write(buffer, 0, byteCount);// 将读取的输入流写入到输出流
                }
                fos.flush();// 刷新缓冲区
                is.close();
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取CPU序列号
     *
     * @return CPU序列号(16位)
     * 读取失败为"0000000000000000"
     */
    public static String getCPUSerial() {

        String str = "", strCPU = "", cpuAddress = "0000000000000000";

        try {
            // 读取CPU信息
            Process pp = Runtime.getRuntime().exec("cat /proc/cpuinfo");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            // 查找CPU序列号
            for (int i = 1; i < 100; i++) {
                str = input.readLine();
                if (str != null) {
                    // 查找到序列号所在行
                    if (str.indexOf("Serial") > -1) {
                        // 提取序列号
                        strCPU = str.substring(str.indexOf(":") + 1, str.length());
                        // 去空格
                        cpuAddress = strCPU.trim();
                        break;
                    }
                } else {
                    //文件结尾
                    break;
                }
            }
        } catch (IOException ex) {
            //赋予默认值
            ex.printStackTrace();
        }
        return cpuAddress;
    }




}
