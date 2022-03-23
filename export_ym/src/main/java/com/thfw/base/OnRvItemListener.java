package com.thfw.base;

import java.util.List;

public interface OnRvItemListener<T> {
    void onItemClick(List<T> list, int position);
}
