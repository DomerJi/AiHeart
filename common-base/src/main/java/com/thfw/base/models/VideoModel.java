package com.thfw.base.models;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.thfw.base.base.IModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class VideoModel implements IModel {

    /**
     * 视频测试数据
     */
    private static String videoUrl01 = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";
    private static String videoUrl02 = "http://gslb.miaopai.com/stream/oxX3t3Vm5XPHKUeTS-zbXA__.mp4";
    private static String videoUrl03 = "http://flv2.bn.netease.com/videolib3/1611/28/nNTov5571/SD/nNTov5571-mobile.mp4";
    /**
     * id : 406
     * file : http://resource.soulbuddy.cn/public/uploads/video/storydepression.mp4
     * img : http://resource.soulbuddy.cn/public/uploads/videoPic/21011911060660064c9ed5ee7.jpg
     * des : null
     * num : 1000
     * collected : 0
     * history_time : 0
     */
    @SerializedName("title")
    public String title;
    @SerializedName("id")
    private int id;
    @SerializedName("file")
    private String url;
    @SerializedName("m3u8")
    private String m3u8;
    @SerializedName("img")
    private String img;
    @SerializedName("des")
    private String des;
    @SerializedName("num")
    private int num;
    @SerializedName("collected")
    private int collected;
    @SerializedName("history_time")
    private String historyTime;
    @SerializedName("recommend_list")
    private List<RecommendModel> recommendModels;

    public static List<VideoModel> getVideoUrl() {
        List<VideoModel> videoModels = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            VideoModel videoModel = new VideoModel();
            switch (i % 3) {
                case 0:
                    videoModel.title = "小兔子";
                    videoModel.url = videoUrl01;
                    break;
                case 1:
                    videoModel.title = "马云演讲";
                    videoModel.url = videoUrl02;
                    break;
                default:
                    videoModel.title = "海洋";
                    videoModel.url = videoUrl03;
                    break;
            }

            videoModels.add(videoModel);
        }
        return videoModels;

    }

    public List<RecommendModel> getRecommendModels() {
        return recommendModels;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public String getM3u8(){
        return m3u8;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getDes() {
        return TextUtils.isEmpty(des) ? "抱歉，此视频无简介" : des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getCollected() {
        return collected;
    }

    public void setCollected(int collected) {
        this.collected = collected;
    }

    public long getHistoryTime() {
        if (TextUtils.isEmpty(historyTime)) {
            return 0;
        } else {
            long ms;
            try {
                ms = Long.parseLong(historyTime);
            } catch (Exception e) {
                ms = 0;
            }
            return ms;
        }

    }

    public void setHistoryTime(String historyTime) {
        this.historyTime = historyTime;
    }

    public String getTitle() {
        return title;
    }

    public static class RecommendModel implements Serializable {

        /**
         * id : 872
         * title : 看看你在亲密关系里的依恋类型？
         * img : http://resource.soulbuddy.cn/public/uploads/videoPic/qmgxyllx.jpg
         * pic : http://resource.soulbuddy.cn/public/uploads/videoPic/qmgxyllx.jpg
         */

        @SerializedName("id")
        private int id;
        @SerializedName("title")
        private String title;
        @SerializedName("img")
        private String img;
        @SerializedName("pic")
        private String pic;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getImg() {
            return TextUtils.isEmpty(img) ? pic : img;
        }

        public void setImg(String img) {
            this.img = img;
        }
    }

}
