package com.thfw.ui.voice;

/**
 * Author:pengs
 * Date: 2021/11/23 10:51
 * Describe:Todo
 */
public interface ILife {

    /**
     * @return 是否初始化成功
     */
    boolean initialized();

    /**
     * 初始化
     */
    void init();

    /**
     * 准备中
     */
    void prepare();

    /**
     * 开始
     */
    boolean start();

    /**
     * 执行中
     *
     * @return
     */
    boolean isIng();

    /**
     * 结束
     */
    void stop();
}
