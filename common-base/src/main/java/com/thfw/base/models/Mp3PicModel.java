package com.thfw.base.models;

import com.thfw.base.base.IModel;

/**
 * Author:pengs
 * Date: 2022/8/21 23:10
 * Describe:Todo
 */
public class Mp3PicModel implements IModel {
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "Mp3PicModel{" +
                "url='" + url + '\'' +
                '}';
    }
}
