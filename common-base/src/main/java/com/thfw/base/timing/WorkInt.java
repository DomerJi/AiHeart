package com.thfw.base.timing;

/**
 * Author:pengs
 * Date: 2021/11/4 11:21
 * Describe:Todo
 */
public enum WorkInt {

    SLEEP(0, 1 * 60, true),
    LOGOUT(1, 1 * 60, false),
    TIME(2, 60, true),
    HINT(3, 20, true),
    SECOND(4, 1, true),
    SECOND5(5, 5, false),
    SECOND5_1(6, 6, false),
    SECOND3(7, 3, false),
    SECOND2(8, 2, true),
    SECOND5_MSG_COUNT(9, 5, true),
    SECOND5_MSG_COUNT2(10, 5, true),
    SECOND7(11, 7, false),
    SECOND_IP(12, 8, false);

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

    WorkInt(int flag, int time, boolean repeat) {
        this.flag = flag;
        this.time = time;
        this.repeat = repeat;
    }

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

    @Override
    public String toString() {
        return "WorkInt{" +
                "flag=" + flag +
                ", time=" + time +
                ", count=" + count +
                '}';
    }
}
