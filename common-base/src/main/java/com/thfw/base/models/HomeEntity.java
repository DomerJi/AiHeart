package com.thfw.base.models;

import com.google.gson.annotations.SerializedName;
import com.thfw.base.base.IModel;

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

    public static class BannerModel implements IModel {

        /**
         * pic : https://resource.soulbuddy.cn/public/uploads/tianhe/pic/famous_scenes_that_astronaut_have_their_meals_at_spacecraft.png
         * type : 0
         * url :
         * content : null
         */

        @SerializedName("pic")
        public String imageUrl;
        @SerializedName("type")
        private int type;
        @SerializedName("url")
        private String url;
        @SerializedName("content")
        private String content;
        @SerializedName("content_id")
        private long contentId;

        public int getType() {
            return type;
        }
    }

}
