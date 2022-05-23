package com.thfw.base.models;

import com.thfw.base.base.IModel;

/**
 * Author:pengs
 * Date: 2021/12/2 16:37
 * Describe:Todo
 */
public class AudioTypeModel implements IModel {

    private String name;
    private int key;

    public AudioTypeModel() {

    }

    public AudioTypeModel(String name, int key) {
        this.name = name;
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }
}
