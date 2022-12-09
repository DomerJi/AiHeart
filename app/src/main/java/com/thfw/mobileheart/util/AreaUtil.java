package com.thfw.mobileheart.util;

import android.content.res.Resources;

import com.google.gson.reflect.TypeToken;
import com.thfw.base.models.AreaModel;
import com.thfw.base.utils.GsonUtil;
import com.thfw.mobileheart.MyApplication;
import com.thfw.mobileheart.R;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Author:pengs
 * Date: 2022/1/19 17:23
 * Describe:Todo
 */
public class AreaUtil {

    private static String areaJson;
    private static List<AreaModel> areaModels;

    private static String getAreaJsonString() {

        StringBuffer sb = new StringBuffer();

        InputStream is = null;

        InputStreamReader isr = null;

        BufferedReader br = null;

        String str = "";

        Resources resources = MyApplication.getApp().getResources();

        try {
            is = resources.openRawResource(R.raw.region); // 读取相应的章节
            isr = new InputStreamReader(is, "UTF-8");// 这里添加了UTF-8，解决乱码问题
            br = new BufferedReader(isr);

            while ((str = br.readLine()) != null) {
                sb.append(str);
                sb.append('\n');
            }
            br.close();
            isr.close();
            is.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sb.toString();

    }

    public static List<AreaModel> getOp1() {

        return getArea();
    }


    public static List<List<AreaModel>> getOp2() {
        List<List<AreaModel>> lists = new ArrayList<>();
        List<AreaModel> areaModels = getArea();
        for (AreaModel areaModel : areaModels) {
            lists.add(areaModel.getChildren());
        }
        return lists;
    }

    public static List<List<List<AreaModel>>> getOp3() {
        List<List<List<AreaModel>>> lists = new ArrayList<>();
        List<AreaModel> areaModels = getArea();
        for (AreaModel areaModel : areaModels) {
            List<List<AreaModel>> xianList = new ArrayList<>();
            for (AreaModel shi : areaModel.getChildren()) {
                xianList.add(shi.getChildren());
                lists.add(xianList);
            }
        }
        return lists;
    }

    public static List<AreaModel> getArea() {
        if (areaModels != null) {
            return areaModels;
        }
        if (areaJson == null) {
            areaJson = getAreaJsonString();
        }
        Type type = new TypeToken<List<AreaModel>>() {
        }.getType();
        areaModels = GsonUtil.fromJson(areaJson, type);
        return areaModels;
    }
}
