package com.thfw.mobileheart.model;

import android.graphics.Color;

import java.util.List;
import java.util.Random;

/**
 * Author:pengs
 * Date: 2021/7/22 10:19
 * Describe:首页实体类
 */
public class HomeEntity {

    public static final int TYPE_BANNER = 0;
    public static final int TYPE_SORT = 1;
    public static final int TYPE_CUSTOM_MADE = 2;
    public static final int TYPE_HISTORY = 3;
    public static final int TYPE_BODY = 4;
    public static final int TYPE_BODY2 = 5;
    public static final int TYPE_TAB_TITLE = 6;

    public int type = TYPE_BODY;
    public List<String> imageUrls;
    public String tabTitle;
    public int color;
    public static Random random = new Random();

    public int body2Position = -1;

    public HomeEntity setBody2Position(int body2Position) {
        this.body2Position = body2Position;
        return this;
    }

    public HomeEntity setType(int type) {
        this.type = type;
        return this;
    }

    public HomeEntity setTabTitle(String tabTitle) {
        this.tabTitle = tabTitle;
        return this;
    }

    public HomeEntity() {
        color = Color.rgb(random.nextInt(255),
                random.nextInt(255),
                random.nextInt(255));
    }

}
