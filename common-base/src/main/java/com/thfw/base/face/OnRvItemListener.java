package com.thfw.base.face;

import java.util.List;

public interface OnRvItemListener<T> {
    void onItemClick(List<T> list, int position);
}
