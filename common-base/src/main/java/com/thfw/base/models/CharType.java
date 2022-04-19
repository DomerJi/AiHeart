package com.thfw.base.models;

public enum CharType {
    MOOD("情绪数值", "分"),
    TALK("主题对话", "分钟"),
    AI_TALK("倾诉吐槽", "分钟"),
    TEST("测评问卷", "分钟"),
    AUDIO("正念冥想", "分钟"),
    VIDEO("视频集锦", "分钟"),
    TOOL("训练工具包", "分钟"),
    BOOK("心理文章", "分钟"),
    IDEO_BOOK("思政文章", "分钟"),
    APP("应用程序", "分钟");

    private int type;
    private String label;
    private String unit;

    CharType(String label, String unit) {
        this.label = label;
        this.unit = unit;
        this.type = label.hashCode();
    }

    public String getLabel() {
        return label;
    }

    public int getType() {
        return type;
    }

    public String getUnit() {
        return unit;
    }
}