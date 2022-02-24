package com.thfw.base.models;

import com.google.gson.annotations.SerializedName;
import com.thfw.base.base.IModel;

/**
 * Author:pengs
 * Date: 2022/2/22 14:02
 * Describe:Todo
 */
public class PresetAvatarModel implements IModel {

    /**
     * id : 1
     * pic : https://resource.soulbuddy.cn/public/uploads/tianhe/pic/head_1.png
     */

    @SerializedName("id")
    private int id;
    @SerializedName("pic")
    private String pic;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }
}
