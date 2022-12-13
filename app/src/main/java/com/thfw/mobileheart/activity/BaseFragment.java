package com.thfw.mobileheart.activity;

import com.thfw.base.base.IPresenter;
import com.thfw.mobileheart.lhxk.LhXkHelper;
import com.thfw.ui.base.IBaseFragment;
import com.thfw.ui.widget.DeviceUtil;

/**
 * 通用基础Fragment
 */
public abstract class BaseFragment<T extends IPresenter> extends IBaseFragment<T> {

    public static final int VOICE_STATIC = 0;
    public static final int VOICE_CHANGED = 1;

    @Override
    public void onVisible(boolean isVisible) {
        super.onVisible(isVisible);
        if (DeviceUtil.isLhXk_CM_GB03D()) {
            if (isVisible) {
                initLocalVoice(VOICE_STATIC);
            } else {
                clearLocalVoice(VOICE_STATIC);
            }
        }

    }

    protected void initLocalVoice(int type) {

    }

    protected void clearLocalVoice(int type) {
        LhXkHelper.removeAction(this.getClass());
    }

}