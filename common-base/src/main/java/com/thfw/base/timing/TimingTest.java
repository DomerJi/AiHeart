package com.thfw.base.timing;

/**
 * Author:pengs
 * Date: 2021/11/4 12:35
 * Describe:Todo
 */
public class TimingTest {

    public void test() {
        TimingHelper.getInstance().addWorkArriveListener(new TimingHelper.WorkListener() {
            @Override
            public void onArrive() {

            }

            @Override
            public WorkInt workInt() {
                return WorkInt.SLEEP;
            }
        });
    }
}
