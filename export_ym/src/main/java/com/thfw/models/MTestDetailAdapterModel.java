package com.thfw.models;

/**
 * Author:pengs
 * Date: 2022/3/11 11:50
 * Describe:Todo
 */
public class MTestDetailAdapterModel {
    public static final int TYPE_TOP = 0;
    public static final int TYPE_BODY = 1;
    public static final int TYPE_HINT = 2;

    private int type;
    private TestDetailModel.HintBean hintBean;

    public MTestDetailAdapterModel setHintBean(TestDetailModel.HintBean hintBean) {
        this.hintBean = hintBean;
        return this;
    }

    public TestDetailModel.HintBean getHintBean() {
        return hintBean;
    }

    public int getType() {
        return type;
    }

    public MTestDetailAdapterModel setType(int type) {
        this.type = type;
        return this;
    }

    private TestDetailModel testDetailModel;

    public MTestDetailAdapterModel setDetailModel(TestDetailModel testDetailModel) {
        this.testDetailModel = testDetailModel;
        return this;
    }

    public TestDetailModel getTestDetailModel() {
        return testDetailModel;
    }
}
