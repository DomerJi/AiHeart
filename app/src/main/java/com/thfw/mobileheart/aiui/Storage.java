package com.thfw.mobileheart.aiui;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 存储repo
 */

public class Storage {

    private Context mContext;

    public Storage(Context context) {
        mContext = context;
    }

    public String readAssetFile(String filename) {
        try {
            InputStream input = mContext.getAssets().open(filename);
            return readStringFromStream(input);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String readSDFile(String filePath) {
        try {
            InputStream input = new FileInputStream(filePath);
            String content = readStringFromStream(input);
            input.close();
            return content;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void writeSDFile(String filePath, byte[] content) {
        try {
            File targetFile = new File(filePath);
            if (!targetFile.getParentFile().exists()) {
                targetFile.getParentFile().mkdirs();
            }

            OutputStream output = new FileOutputStream(targetFile);
            output.write(content);
            output.flush();
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String readStringFromStream(InputStream input) throws IOException {
        StringBuilder result = new StringBuilder();
        byte[] buffer = new byte[1024];
        int read;
        while ((read = input.read(buffer)) > 0) {
            result.append(new String(buffer, 0, read));
        }

        return result.toString();
    }
}
