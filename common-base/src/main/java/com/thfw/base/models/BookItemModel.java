package com.thfw.base.models;

import com.google.gson.annotations.SerializedName;
import com.thfw.base.base.IModel;

/**
 * Author:pengs
 * Date: 2021/12/14 10:37
 * Describe:Todo
 */
public class BookItemModel implements IModel {


    /**
     * id : 581
     * title : 为什么相爱的人，也会出轨？-心理学文章-壹心理
     * author : 密友AI
     * img_file : http://resource.soulbuddy.cn/public/images/gamble.png
     * introduction : null
     * browse_num : 21
     */

    @SerializedName("id")
    private int id;
    @SerializedName("title")
    private String title;
    @SerializedName("author")
    private String author;
    @SerializedName("img_file")
    private String imgFile;
    @SerializedName("introduction")
    private String introduction;
    @SerializedName("browse_num")
    private int browseNum;

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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getImgFile() {
        return imgFile;
    }

    public void setImgFile(String imgFile) {
        this.imgFile = imgFile;
    }

    public Object getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public int getBrowseNum() {
        return browseNum;
    }

    public void setBrowseNum(int browseNum) {
        this.browseNum = browseNum;
    }
}
