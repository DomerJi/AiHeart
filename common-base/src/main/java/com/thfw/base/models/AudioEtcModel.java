package com.thfw.base.models;

import com.google.gson.annotations.SerializedName;
import com.thfw.base.base.IModel;

/**
 * Author:pengs
 * Date: 2021/12/2 16:37
 * Describe:Todo
 */
public class AudioEtcModel implements IModel {


    /**
     * id : 473
     * type_id : 51
     * intr : 帮助初学者掌握冥想技巧
     * detail : <p><br></p><p>冥想练习能够帮助我们意识到当下自己的内外情绪和感受，让我们清晰地明白我们在哪里、我们在做什么，而不是沉迷于各种各样的思考中。</p><p><br></p><p>研究表明，坚持进行正念练习，不仅可以帮助我们排解压力，改善夜间睡眠，还能提高我们的情绪调节能力，让我们能够更好地应对生活事件和各式各样的挑战。</p><p><br></p><p>本系列作为初学者入门课程，在为期30天的练习中将由浅至深地帮助初学者掌握冥想的方法和技巧，让每个人都能学会如何在日常生活中进行冥想练习。</p><p><br></p><p><strong>适用人群：</strong></p><p>冥想初学者</p><p><br></p>
     * num : 6355
     * img : http://resource.soulbuddy.cn/public/uploads/musicPic/2104270504046087d38474598.jpeg
     * title : 冥想基础练习
     * music_size : 0
     */

    @SerializedName("id")
    private int id;
    @SerializedName("type_id")
    private int typeId;
    @SerializedName("intr")
    private String intr;
    @SerializedName("detail")
    private String detail;
    @SerializedName("num")
    private int num;
    @SerializedName("img")
    private String img;
    @SerializedName("title")
    private String title;
    @SerializedName("music_size")
    private int musicSize;
    @SerializedName("listen_history_size")
    private int listenHistorySize;

    public int getListenHistorySize() {
        return listenHistorySize;
    }

    public void setListenHistorySize(int listenHistorySize) {
        this.listenHistorySize = listenHistorySize;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getMusicSize() {
        return musicSize;
    }

    public void setMusicSize(int musicSize) {
        this.musicSize = musicSize;
    }
}
