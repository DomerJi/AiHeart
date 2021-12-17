package com.thfw.base.models;

import com.google.gson.annotations.SerializedName;
import com.thfw.base.base.IModel;

import java.util.List;

/**
 * Author:pengs
 * Date: 2021/12/17 16:56
 * Describe:最后一次信息
 */
public class AudioLastEtcModel implements IModel {


    /**
     * id : 476
     * intr : 变化总在发生，学会顺其自然
     * detail : <p><br></p><p>生活中总会有一些意想不到的情况发生，没做好准备的我们常常慌张、不知所措，仿佛自己失去了掌控感。但其实，万物都处于持续的变化中，越是抗拒，我们只会越慌乱。学会顺应变化，能让我们更轻松地去面对。</p><p><br></p><p>本系列将引导你逐渐建立万物持续变化的认识，学会如何顺应变化，让自己面对变化时更加得心应手。</p><p><br></p><p><strong>适用人群：</strong></p><p>经历了工作、生活重大变化的人</p><p>即将面临生活变动的人</p><p>想让生活更有掌控感的人</p>
     * num : 3413
     * img : http://resource.soulbuddy.cn/public/uploads/musicPic/210402095213606678cdabcf8.webp
     * sort : 2
     * add_time : 2021-08-20 17:51:21
     * title : 顺应变化
     * root_id : 6
     * miniprogram_show_name : 顺应变化
     * music_size : 10
     * listen_history : ["1808","1809"]
     * last_music_id : 1809
     * last_index : 2
     * collected : 0
     */

    @SerializedName("id")
    private int id;
    @SerializedName("intr")
    private String intr;
    @SerializedName("detail")
    private String detail;
    @SerializedName("num")
    private int num;
    @SerializedName("img")
    private String img;
    @SerializedName("sort")
    private int sort;
    @SerializedName("add_time")
    private String addTime;
    @SerializedName("title")
    private String title;
    @SerializedName("root_id")
    private int rootId;
    @SerializedName("miniprogram_show_name")
    private String miniprogramShowName;
    @SerializedName("music_size")
    private int musicSize;
    @SerializedName("last_music_id")
    private int lastMusicId;
    @SerializedName("last_index")
    private int lastIndex;
    @SerializedName("collected")
    private int collected;
    @SerializedName("listen_history")
    private List<String> listenHistory;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIntr() {
        return intr;
    }

    public void setIntr(String intr) {
        this.intr = intr;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getRootId() {
        return rootId;
    }

    public void setRootId(int rootId) {
        this.rootId = rootId;
    }

    public String getMiniprogramShowName() {
        return miniprogramShowName;
    }

    public void setMiniprogramShowName(String miniprogramShowName) {
        this.miniprogramShowName = miniprogramShowName;
    }

    public int getMusicSize() {
        return musicSize;
    }

    public void setMusicSize(int musicSize) {
        this.musicSize = musicSize;
    }

    public int getLastMusicId() {
        return lastMusicId;
    }

    public void setLastMusicId(int lastMusicId) {
        this.lastMusicId = lastMusicId;
    }

    public int getLastIndex() {
        return lastIndex;
    }

    public void setLastIndex(int lastIndex) {
        this.lastIndex = lastIndex;
    }

    public int getCollected() {
        return collected;
    }

    public void setCollected(int collected) {
        this.collected = collected;
    }

    public List<String> getListenHistory() {
        return listenHistory;
    }

    public void setListenHistory(List<String> listenHistory) {
        this.listenHistory = listenHistory;
    }

    public AudioEtcModel toAudioEtcModel() {
        AudioEtcModel audioEtcModel = new AudioEtcModel();
        audioEtcModel.setLastMusicId(lastMusicId);
        audioEtcModel.setId(id);
        audioEtcModel.setDetail(detail);
        audioEtcModel.setTitle(title);
        audioEtcModel.setListenHistorySize(lastIndex);
        audioEtcModel.setNum(num);
        audioEtcModel.setMusicSize(musicSize);
        audioEtcModel.setImg(img);
        audioEtcModel.setIntr(intr);
        return audioEtcModel;
    }
}
