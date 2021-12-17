package com.thfw.base.models;

import com.thfw.base.base.IModel;

public class ChatEntity implements IModel {

    public static final int TYPE_FROM = 0;
    public static final int TYPE_TO = 1;
    public static final int TYPE_LINK = 2;
    public static final int TYPE_END_SERVICE = 3;
    public static final int TYPE_FEEDBACK = 4;
    public static final int TYPE_SELECT = 5;
    public static final int TYPE_TIME = 6;

    public int type;
    public String talk;

}
