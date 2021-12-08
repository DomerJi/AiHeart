package com.thfw.base.net;


import com.thfw.base.utils.GsonUtil;
import com.thfw.base.utils.LogUtil;

import java.util.TreeMap;

/**
 * 公共参数创建类
 * Created By jishuaipeng on 2021/5/24
 */
public class NetParams extends TreeMap<String, Object> {

    public static NetParams crete() {
        return new NetParams();
    }


    public NetParams add(String key, Object value) {
        super.put(key, value);
        return this;
    }

    public NetParams print() {
        LogUtil.d("NetParams", " = " + GsonUtil.toJson(this));
        return this;
    }

    public String toJson() {
        return GsonUtil.toJson(this);
    }

}
