package com.thfw.base.models;

import com.google.gson.annotations.SerializedName;
import com.thfw.base.base.IModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Author:pengs
 * Date: 2021/12/17 16:56
 * Describe:最后一次视频播放信息
 */
public class VideoLastEtcModel implements IModel {


    /**
     * id : 429
     * title : 7个孤独信号
     * img : http://resource.soulbuddy.cn/public/uploads/videoPic/21011903283460068a2210081.jpg
     * des : null
     * num : 1037
     * pic : http://resource.soulbuddy.cn/public/uploads/videoPic/21011903283460068a2210081.jpg
     * collected : 0
     * history_time : 34245
     * percent_time : 0
     * add_time : 2021-12-20 05:23:43
     */

    @SerializedName("id")
    private int id;
    @SerializedName("title")
    private String title;
    @SerializedName("img")
    private String img;
    @SerializedName("des")
    private Object des;
    @SerializedName("num")
    private int num;
    @SerializedName("pic")
    private String pic;
    @SerializedName("collected")
    private int collected;
    @SerializedName("history_time")
    private String historyTime;
    @SerializedName("percent_time")
    private int percentTime;
    @SerializedName("add_time")
    private String addTime;

    public List<VideoEtcModel> toVideoModel(){
        VideoEtcModel videoModel = new VideoEtcModel();
        videoModel.setTitle(title);
        videoModel.setId(id);
        videoModel.setCollected(collected);
        videoModel.setNum(num);
        videoModel.setPic(pic);
        ArrayList<VideoEtcModel> videoEtcModels = new ArrayList<>();
        return videoEtcModels;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public Object getDes() {
        return des;
    }

    public void setDes(Object des) {
        this.des = des;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public int getCollected() {
        return collected;
    }

    public void setCollected(int collected) {
        this.collected = collected;
    }

    public String getHistoryTime() {
        return historyTime;
    }

    public void setHistoryTime(String historyTime) {
        this.historyTime = historyTime;
    }

    public int getPercentTime() {
        return percentTime;
    }

    public void setPercentTime(int percentTime) {
        this.percentTime = percentTime;
    }

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }
}
