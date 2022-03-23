package com.thfw.models;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.thfw.base.IModel;

import java.io.Serializable;
import java.util.List;

/**
 * Author:pengs
 * Date: 2021/12/8 11:41
 * Describe:Todo
 */
public class TestResultModel implements IModel {

    @SerializedName("result_id")
    private int resultId;

    @SerializedName("test_id")
    private int testId;

    public TestResultModel setTestId(int testId) {
        this.testId = testId;
        return this;
    }

    public int getTestId() {
        return testId;
    }

    @SerializedName("recommend_info")
    private List<RecommendInfoBean> recommendInfo;

    public TestResultModel setResultId(int resultId) {
        this.resultId = resultId;
        return this;
    }

    public int getResultId() {
        return resultId;
    }

    public List<RecommendInfoBean> getRecommendInfo() {
        return recommendInfo;
    }

    public void setRecommendInfo(List<RecommendInfoBean> recommendInfo) {
        this.recommendInfo = recommendInfo;
    }


    public static class RecommendInfoBean implements Serializable {
        /**
         * type : 2
         * info : {"id":1559,"title":"缓解抑郁","pic":"http://resource.soulbuddy.cn/public/uploads/testing/21042711225660878390f18ea.jpg","intr":"","num":686}
         * name : 专业话术
         */

        @SerializedName("type")
        private int type;
        @SerializedName("info")
        private InfoBean info;
        @SerializedName("name")
        private String name;

        public int getType() {
            return type;
        }

        public int getTypeStr() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public InfoBean getInfo() {
            return info;
        }

        public void setInfo(InfoBean info) {
            this.info = info;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public static class InfoBean implements Serializable {
            /**
             * id : 1559
             * title : 缓解抑郁
             * pic : http://resource.soulbuddy.cn/public/uploads/testing/21042711225660878390f18ea.jpg
             * intr :
             * num : 686
             */

            @SerializedName("id")
            private int id;
            @SerializedName("title")
            private String title;
            @SerializedName("pic")
            private String pic;
            @SerializedName("img")
            private String img;
            @SerializedName("intr")
            private String intr;
            @SerializedName("num")
            private int num;

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
                return TextUtils.isEmpty(pic) ? img : pic;
            }

            public void setPic(String pic) {
                this.pic = pic;
            }

            public String getIntr() {
                return intr;
            }

            public void setIntr(String intr) {
                this.intr = intr;
            }

            public int getNum() {
                return num;
            }

            public void setNum(int num) {
                this.num = num;
            }
        }
    }
}
