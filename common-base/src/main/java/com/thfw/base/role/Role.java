package com.thfw.base.role;

import com.google.gson.annotations.SerializedName;

public class Role {

    @SerializedName("name")
    private String name;

    @SerializedName("higherLevel")
    private Role higherLevel;

    @SerializedName("lowerLevel")
    private Role lowerLevel;


    private int roleId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Role getHigherLevel() {
        return higherLevel;
    }

    public void setHigherLevel(Role higherLevel) {
        this.higherLevel = higherLevel;
    }

    public Role getLowerLevel() {
        return lowerLevel;
    }

    public void setLowerLevel(Role lowerLevel) {
        this.lowerLevel = lowerLevel;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }
}
