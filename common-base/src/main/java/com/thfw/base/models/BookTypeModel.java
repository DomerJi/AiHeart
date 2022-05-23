package com.thfw.base.models;

import com.thfw.base.base.IModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Author:pengs
 * Date: 2021/12/2 16:37
 * Describe:Todo
 */
public class BookTypeModel extends HashMap<String, String> implements IModel {


    private List<BookTypeImpModel> mList;


    public List<BookTypeImpModel> getList() {
        if (mList == null) {
            mList = new ArrayList<>();
            mList.add(new BookTypeImpModel("0", "全部"));
            Set<Entry<String, String>> set = entrySet();
            for (Entry<String, String> entry : set) {
                mList.add(new BookTypeImpModel(entry.getKey(), entry.getValue()));
            }
        }
        return mList;
    }

    public static class BookTypeImpModel implements IModel {
        public String key;
        public String value;
        public int id;

        public BookTypeImpModel(String key, String value) {
            this.key = key;
            this.value = value;
            try {
                this.id = Integer.parseInt(key);
            } catch (Exception e) {

            }

        }
    }

}
