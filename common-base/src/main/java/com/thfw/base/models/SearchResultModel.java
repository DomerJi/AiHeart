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
    public static final int TYPE_IDEO_TEXT = 6;
    public static final int TYPE_TOOL = 7;
    private List<ResultBean> allList;
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
    /**
     * 成长练习
     */
    @SerializedName("tool_package_list")
    private List<ResultBean> toolPackageList;
    /**
     * 思政文章
     *
     * @return
     */
    @SerializedName("ideology_article_list")
    private List<ResultBean> ideologyList;

    public List<ResultBean> getAllList() {
        if (allList == null) {
            allList = new ArrayList<>();

            if (psychTest != null) {
                for (ResultBean bean : psychTest) {
                    bean.setType(TYPE_TEST);
                }
                allList.addAll(psychTest);
            }
            if (collection != null) {
                for (ResultBean bean : collection) {
                    bean.setType(TYPE_AUDIO);
                }
                allList.addAll(collection);
            }
            if (videoList != null) {
                for (ResultBean bean : videoList) {
                    bean.setType(TYPE_VIDEO);
                }
                allList.addAll(videoList);
            }
            if (dialogList != null) {
                for (ResultBean bean : dialogList) {
                    bean.setType(TYPE_DIALOG);
                }
                allList.addAll(dialogList);
            }
            if (articleList != null) {
                for (ResultBean bean : articleList) {
                    bean.setType(TYPE_TEXT);
                }
                allList.addAll(articleList);
            }
            if (ideologyList != null) {
                for (ResultBean bean : ideologyList) {
                    bean.setType(TYPE_IDEO_TEXT);
                }
                allList.addAll(ideologyList);
            }
            if (toolPackageList != null) {
                for (ResultBean bean : toolPackageList) {
                    bean.setType(TYPE_TOOL);
                }
                allList.addAll(toolPackageList);
            }
        }
        return allList;
    }

    public List<ResultBean> getIdeologyList() {
        return ideologyList;
    }

    public List<ResultBean> getToolPackageList() {
        return toolPackageList;
    }

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

    public static class ResultBean {

        public int contentType;
        @SerializedName("id")
        private int id;
        @SerializedName("title")
        private String title;
        @SerializedName("type")
        private int type;

        public int getVideoType() {
            return type;
        }

        public int getType() {
            return contentType;
        }

        public void setType(int type) {
            this.contentType = type;
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
