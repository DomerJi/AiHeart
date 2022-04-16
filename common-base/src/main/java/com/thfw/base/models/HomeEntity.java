package com.thfw.base.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

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
    public List<BannerModel> bannerModels;
    public MoodLivelyModel moodLivelyModel;
    public String tabTitle;

    public MobileRecommendModel recommendModel;

    public HomeEntity setMoodLivelyModel(MoodLivelyModel moodLivelyModel) {
        this.moodLivelyModel = moodLivelyModel;
        return this;
    }

    public int body2Position = -1;

    public HomeEntity setBannerModels(List<BannerModel> bannerModels) {
        this.bannerModels = bannerModels;
        return this;
    }

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

    public HomeEntity setRecommendModel(MobileRecommendModel recommendModel) {
        this.recommendModel = recommendModel;
        return this;
    }

    public HomeEntity() {
    }

    public class BannerModel {

        @SerializedName("imageUrl")
        public String imageUrl;
    }

}
