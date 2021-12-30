package com.thfw.base.models;

import com.google.gson.annotations.SerializedName;
import com.thfw.base.base.IModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Author:pengs
 * Date: 2021/12/29 11:30
 * Describe:Todo
 */
public class SearchResultModel implements IModel {
    public static final int TYPE_TEST = 1;
    public static final int TYPE_DIALOG = 2;
    public static final int TYPE_AUDIO = 3;
    public static final int TYPE_VIDEO = 4;
    public static final int TYPE_TEXT = 5;

    public static class TypeTitleBean {
        public int type;
        public String title;
        public List<ResultBean> list;

        public TypeTitleBean(int type, String title, List<ResultBean> list) {
            this.type = type;
            this.title = title;
            this.list = list;
        }
    }

    private List<ResultBean> allList;

    public List<ResultBean> getAllList() {
        if (allList == null) {
            allList = new ArrayList<>();
            for (ResultBean bean : psychTest) {
                bean.setType(TYPE_TEST);
            }

            for (ResultBean bean : collection) {
                bean.setType(TYPE_AUDIO);
            }

            for (ResultBean bean : videoList) {
                bean.setType(TYPE_VIDEO);
            }

            for (ResultBean bean : dialogList) {
                bean.setType(TYPE_DIALOG);
            }

            for (ResultBean bean : articleList) {
                bean.setType(TYPE_TEXT);
            }

            allList.addAll(psychTest);
            allList.addAll(collection);
            allList.addAll(videoList);
            allList.addAll(dialogList);
            allList.addAll(articleList);
        }
        return allList;
    }

    /**
     * 心理测评
     */
    @SerializedName("psych_test")
    private List<ResultBean> psychTest;

    /**
     * 科普文章
     */
    @SerializedName("article_list")
    private List<ResultBean> articleList;

    /**
     * 主题对话
     */
    @SerializedName("dialog_list")
    private List<ResultBean> dialogList;
    /**
     * 正念冥想
     */
    @SerializedName("collection")
    private List<ResultBean> collection;
    /**
     * 科普视频
     */
    @SerializedName("video_list")
    private List<ResultBean> videoList;


    public List<ResultBean> getPsychTest() {
        return psychTest;
    }

    public List<ResultBean> getDialogList() {
        return dialogList;
    }

    public List<ResultBean> getCollection() {
        return collection;
    }

    public List<ResultBean> getVideoList() {
        return videoList;
    }

    public List<ResultBean> getArticleList() {
        return articleList;
    }

    public static class ResultBean {

        @SerializedName("id")
        private int id;
        @SerializedName("title")
        private String title;

        public int type;

        public void setType(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
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
    }

}
