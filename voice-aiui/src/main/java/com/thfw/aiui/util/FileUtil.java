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
     * ??????asset?????????????????????
     * @param oldPath  asset????????????
     * @param newPath  SD??????????????????\
     */
    public static void CopyAssets(Context context, String oldPath, String newPath) {
        try {
            String fileNames[] = context.getAssets().list(oldPath);// ??????assets????????????????????????????????????
            if (fileNames.length > 0) {// ???????????????
                File file = new File(newPath);
                file.mkdirs();// ????????????????????????????????????
                for (String fileName : fileNames) {
                    CopyAssets(context, oldPath + "/" + fileName, newPath + "/" + fileName);
                }
            } else {// ???????????????
                InputStream is = context.getAssets().open(oldPath);
                FileOutputStream fos = new FileOutputStream(new File(newPath));
                byte[] buffer = new byte[1024];
                int byteCount = 0;
                while ((byteCount = is.read(buffer)) != -1) {// ????????????????????????
                    // buffer??????
                    fos.write(buffer, 0, byteCount);// ???????????????????????????????????????
                }
                fos.flush();// ???????????????
                is.close();
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ??????CPU?????????
     *
     * @return CPU?????????(16???)
     * ???????????????"0000000000000000"
     */
    public static String getCPUSerial() {

        String str = "", strCPU = "", cpuAddress = "0000000000000000";

        try {
            // ??????CPU??????
            Process pp = Runtime.getRuntime().exec("cat /proc/cpuinfo");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            // ??????CPU?????????
            for (int i = 1; i < 100; i++) {
                str = input.readLine();
                if (str != null) {
                    // ???????????????????????????
                    if (str.indexOf("Serial") > -1) {
                        // ???????????????
                        strCPU = str.substring(str.indexOf(":") + 1, str.length());
                        // ?????????
                        cpuAddress = strCPU.trim();
                        break;
                    }
                } else {
                    //????????????
                    break;
                }
            }
        } catch (IOException ex) {
            //???????????????
            ex.printStackTrace();
        }
        return cpuAddress;
    }




}
