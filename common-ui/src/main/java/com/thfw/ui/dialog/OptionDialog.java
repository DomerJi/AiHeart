package com.thfw.ui.dialog;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.thfw.base.models.PickerData;
import com.thfw.base.utils.Util;
import com.thfw.ui.R;

import java.util.List;

/**
 * Author:pengs
 * Date: 2022/12/15 10:15
 * Describe:Todo
 */
public class OptionDialog {

    /**
     * 创建通用单选选择框
     */
    public static void create(Context mContext, ViewGroup decorView,
                                          OnOptionsSelectListener optionsSelectListener,
                                          String title, List<PickerData> list,int defPosition) {

        OptionsPickerBuilder optionsPickerBuilder = new OptionsPickerBuilder(mContext, optionsSelectListener).setDecorView(decorView)//必须是RelativeLayout，不设置setDecorView的话，底部虚拟导航栏会显示在弹出的选择器区域
                .setTitleText(title) // 标题文字
                .setTitleSize(17)//标题文字大小
                .setTitleColor(mContext.getResources().getColor(R.color.black))//标题文字颜色
                .setCancelText("取消")//取消按钮文字
                .setCancelColor(mContext.getResources().getColor(R.color.text_content))//取消按钮文字颜色
                .setSubmitText("确定")//确认按钮文字
                .setSubmitColor(mContext.getResources().getColor(R.color.black))//确定按钮文字颜色
                .setContentTextSize(17)//滚轮文字大小
                .setTextColorOut(mContext.getResources().getColor(R.color.text_content))
                .setTextColorCenter(mContext.getResources().getColor(R.color.text_common))//设置选中文本的颜色值
                .setSubCalSize(14)
                .setBgColor(mContext.getResources().getColor(R.color.white))
                .setLineSpacingMultiplier(2.2f) // 行间距
                .setDividerColor(mContext.getResources().getColor(R.color.black_10));// 设置分割线的颜色


        OptionsPickerView<PickerData> optionsPickerView = optionsPickerBuilder.build();
        setOptionPickerView(optionsPickerView, mContext);
        optionsPickerView.setPicker(list);
        optionsPickerView.setSelectOptions(defPosition);
        optionsPickerView.show();
    }

    private static void setOptionPickerView(OptionsPickerView optionsPickerView, Context
            mContext) {
        LinearLayout picker = (LinearLayout) optionsPickerView.findViewById(R.id.optionspicker);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) picker.getLayoutParams();
        layoutParams.height = Util.dipToPx(200, mContext);
        picker.setLayoutParams(layoutParams);
        optionsPickerView.findViewById(R.id.rv_topbar).setBackgroundResource(R.drawable.bg_top_radius_white);

    }
}
