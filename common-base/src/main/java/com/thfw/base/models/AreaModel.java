package com.thfw.base.models;

import com.contrarywind.interfaces.IPickerViewData;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Author:pengs
 * Date: 2022/1/19 17:20
 * Describe:Todo
 */
public class AreaModel implements IPickerViewData {


    /**
     * code : 110000
     * name : 北京市
     * children : [{"code":"110100","name":"直辖市","children":[{"code":"110101","name":"东城区"},{"code":"110102","name":"西城区"},{"code":"110105","name":"朝阳区"},{"code":"110106","name":"丰台区"},{"code":"110107","name":"石景山区"},{"code":"110108","name":"海淀区"},{"code":"110109","name":"门头沟区"},{"code":"110111","name":"房山区"},{"code":"110112","name":"通州区"},{"code":"110113","name":"顺义区"},{"code":"110114","name":"昌平区"},{"code":"110115","name":"大兴区"},{"code":"110116","name":"怀柔区"},{"code":"110117","name":"平谷区"},{"code":"110118","name":"密云区"},{"code":"110119","name":"延庆区"}]}]
     */

    @SerializedName("code")
    private String code;
    @SerializedName("name")
    private String name;
    @SerializedName("children")
    private List<AreaModel> children;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<AreaModel> getChildren() {
        return children;
    }

    public void setChildren(List<AreaModel> children) {
        this.children = children;
    }

    @Override
    public String getPickerViewText() {
        return name;
    }
}
