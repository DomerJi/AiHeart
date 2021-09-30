package com.thfw.ui.widget.stick;

/**
 * Created By jishuaipeng on 2021/5/14
 */
public abstract class ICart implements IStick {

    private boolean checked;
    private int position;
    private int groupPositon;

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public boolean isGroup() {
        return getType() == TYPE_GROUP;
    }

    public boolean isSKU() {
        return getType() == TYPE_BODY;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getGroupPosition() {
        return groupPositon;
    }

    public void setGroupPositon(int groupPositon) {
        this.groupPositon = groupPositon;
    }
}
