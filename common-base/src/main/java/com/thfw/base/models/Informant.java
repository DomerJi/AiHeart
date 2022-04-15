package com.thfw.base.models;

import java.util.ArrayList;
import java.util.List;

public class Informant {

    private Informant(String name, String vnc, int code) {
        this.name = name;
        this.vnc = vnc;

        this.code = code;
    }

    public String name;
    public String vnc;
    public String version;
    public int code;

    public static List<Informant> getInformant() {
        List<Informant> informants = new ArrayList<>();
        informants.add(new Informant("安娜·佛罗伊德(普通话)", "x2_xiaoshi_cts", 0));
        informants.add(new Informant("贺顿(普通话)", "x2_yifei", 1));
        informants.add(new Informant("杨贵妃(陕西话)", "x2_xiaoying", 2));
        return informants;
    }
}