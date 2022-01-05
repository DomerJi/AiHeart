package com.thfw.base.timing;

/**
 * Author:pengs
 * Date: 2021/11/4 11:21
 * Describe:Todo
 */
public enum WorkInt {

    SLEEP(0, 1 * 60, false),
    LOGOUT(1, 1 * 60, false),
    TIME(2, 60, true),
    HINT(3, 20, true),
    SECOND(4, 1, true);

    /**
     * 标识
     */
    private int flag;
    /**
     * 时长
     */
    private int time;
    /**
     * 是否重复
     * false 执行完自动删除
     */
    private boolean repeat;

    private int count;

    public boolean isRepeat() {
        return repeat;
    }


    public int getFlag() {
        return flag;
    }

    public int getCount() {
        return count;
    }

    public int getTime() {
        return time;
    }

    public boolean arrive() {
        boolean arrive = count >= time;
        if (arrive) {
            resetCount();
        }
        return arrive;
    }

    public void resetCount() {
        count = 0;
    }

    public void addCount() {
        count++;
    }

    WorkInt(int flag, int time, boolean repeat) {
        this.flag = flag;
        this.time = time;
        this.repeat = repeat;
    }

    @Override
    public String toString() {
        return "WorkInt{" +
                "flag=" + flag +
                ", time=" + time +
                ", count=" + count +
                '}';
    }
}