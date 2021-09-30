package com.thfw.mobileheart.model;

import com.thfw.base.base.IModel;

public class ChatEntity implements IModel {

    public static final int TYPE_FROM = 0;
    public static final int TYPE_TO = 1;

    public int type;
    public String talk;

}
