package com.thfw.ui.base;

import android.view.View;

/**
 * Author:pengs
 * Date: 2021/7/22 9:11
 * Describe:首次加载状态接口
 */
public interface ILoading {


    Level EMPTY = Level.EMPTY_PRODUCT;

    Level ERROR = Level.ERROR_NET;

    public enum Level {

//        int ERROR_NET = 1;
//        int EMPTY_PRODUCT = 2;
//        int EMPTY_SHOP = 3;
//        int EMPTY_NOTICE = 4;
//        int EMPTY_ADDRESS = 5;
//        int EMPTY_ORDER = 6;

        ERROR_NET(1, "啊欧！网络找不到了啦…", "重新尝试"),
        EMPTY_PRODUCT_CART(2, "购物车里空空如也，去挑选商品吧…", "去逛逛"),
        EMPTY_PRODUCT_SEARCH(2, "抱歉，没有找到符合条件的商品", ""),
        EMPTY_PRODUCT(2, "这里空空如也~", ""),
        EMPTY_SHOP(3, "没有关注的店铺哦", "去逛逛"),
        EMPTY_NOTICE(4, "暂时没有通知哟", "去逛逛"),
        EMPTY_ADDRESS(5, "这里空空如也，快去添加地址吧…", "添加地址"),
        EMPTY_ORDER(6, "这里没有订单哟，快去下单吧…", "去下单");

        // 成员变量
        private int level;
        private String hint;
        private String btnText;

        // 构造方法
        private Level(int level, String hint, String btnText) {
            this.level = level;
            this.hint = hint;
            this.btnText = btnText;
        }

        public String getHint() {
            return hint;
        }

        public int getLevel() {
            return level;
        }

        public String getBtnText() {
            return btnText;
        }
    }


    /**
     * 显示loading，开始加载数据时调用
     */
    public void showLoading();

    /**
     * 展示空布局，通常在没有数据可展示之后
     */
    public void showEmpty();

    /**
     * 展示加载失败状态
     *
     * @param onClickListener 失败，点击重试的回调
     */
    public void showFail(Level level, View.OnClickListener onClickListener);

    public void showFail(View.OnClickListener onClickListener);

    /**
     * 隐藏所有，通常在数据正常加载后调用
     */
    public void hide();
}
