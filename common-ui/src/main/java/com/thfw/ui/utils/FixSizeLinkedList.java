package com.thfw.ui.utils;

import java.util.LinkedList;

public class FixSizeLinkedList<T> extends LinkedList<T> {

    private int capacity;

    public FixSizeLinkedList(int capacity) {
        super();
        this.capacity = capacity;
    }

    @Override
    public void add(int index, T element) {
        super.add(index, element); // 先让其更改数量；
        if (size() > capacity) {
            super.removeFirst();
        }
    }

    @Override
    public boolean add(T t) {
        if (size() + 1 > capacity) {
            super.removeFirst();
        }
        return super.add(t);
    }
}

