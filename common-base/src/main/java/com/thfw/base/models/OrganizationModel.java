package com.thfw.base.models;

import com.google.gson.annotations.SerializedName;
import com.thfw.base.base.IModel;

import java.io.Serializable;
import java.util.List;

/**
 * Author:pengs
 * Date: 2021/12/7 15:00
 * Describe:Todo
 */
public class OrganizationModel implements IModel {


    /**
     * organization : {"id":1,"name":"测试机构","children":[{"id":2,"name":"师（1）班","children":[{"id":4,"name":"军（1）班","children":[{"id":6,"name":"旅（1）班","if_child":0}]}]},{"id":3,"name":"师（2）班","children":[{"id":5,"name":"军（2）班","children":[{"id":7,"name":"旅（2）班","if_child":0}]}]}]}
     */

    private OrganizationBean organization;

    public OrganizationBean getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationBean organization) {
        this.organization = organization;
    }

    public static class OrganizationBean implements Serializable {
        /**
         * id : 1
         * name : 测试机构
         * children : [{"id":2,"name":"师（1）班","children":[{"id":4,"name":"军（1）班","children":[{"id":6,"name":"旅（1）班","if_child":0}]}]},{"id":3,"name":"师（2）班","children":[{"id":5,"name":"军（2）班","children":[{"id":7,"name":"旅（2）班","if_child":0}]}]}]
         */

        private int id;
        private String name;
        @SerializedName("if_child")
        private int ifChild = -1;
        private List<OrganizationBean> children;

        public boolean isChild() {
            return ifChild == 0;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<OrganizationBean> getChildren() {
            return children;
        }

        public void setChildren(List<OrganizationBean> children) {
            this.children = children;
        }

    }
}
