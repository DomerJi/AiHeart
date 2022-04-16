package com.thfw.base.models;

import com.google.gson.annotations.SerializedName;
import com.thfw.base.base.IModel;

/**
 * Author:pengs
 * Date: 2022/4/15 10:23
 * Describe:Todo
 */
public class MobileRecommendModel implements IModel {


    /**
     * type : 3
     * content_id : 635
     * title : 应对分心
     * pic : http://resource.soulbuddy.cn/public/uploads/musicPic/2203160530006231ae1819398.jpg
     */

    @SerializedName("type")
    private int type;
    @SerializedName("content_id")
    private int contentId;
    @SerializedName("title")
    private String title;
    @SerializedName("pic")
    private String pic;

    public int getType() {
        return type;
    }

    /**
     * type 1-测评 2-文章 3-音频  4-视频 6-思政文章 7-工具包
     *
     * @return
     */
    public String getTypeStr() {
        switch (type) {
            case 1:
                return "测一测";
            case 2:
                return "读一读";
            case 3:
                return "听一听";
            case 4:
                return "看一看";
            case 5:
                return "聊一聊";
            case 6:
                return "学一学";
            case 7:
                return "练一练";
        }
        return "?一?";
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getContentId() {
        return contentId;
    }

    public void setContentId(int contentId) {
        this.contentId = contentId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }
}
