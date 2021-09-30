package com.thfw.base.role;

public class RoleLimits {

    private static volatile RoleLimits instance;
    private Role role = new Role();

    private RoleLimits() {
    }

    public static RoleLimits getInstance() {
        if (instance == null) {
            synchronized (RoleLimits.class) {
                if (instance == null) {
                    instance = new RoleLimits();
                }
            }
        }
        return instance;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public int getRoleId() {
        return role.getRoleId();
    }
}
