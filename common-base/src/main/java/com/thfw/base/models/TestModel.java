package com.thfw.base.models;

import com.thfw.base.base.IModel;

/**
 * Author:pengs
 * Date: 2021/10/8 15:08
 * Describe:Todo
 */
public class TestModel implements IModel {


    /**
     * id : 380
     * title : 军人心理应激自评问卷
     * pic : http://resource.soulbuddy.cn/public/uploads/testing/210901045817612f40a975e62.jpeg
     * type : 2
     * num : 244
     * cat : 1
     */

    private int id;
    private String title;
    private String pic;
    private int type;
    private int num;
    private int cat;

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

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getCat() {
        return cat;
    }

    public void setCat(int cat) {
        this.cat = cat;
    }
}
