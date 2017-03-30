package org.seckill.enums;

/**
 * 枚举表示我们的常量数据字典
 * Created by CHEN on 2016/5/21.
 */
public enum SeckillStateEnum {
    SUCCESS(1,"秒杀成功"),
    END(0,"秒杀结束"),
    REPEAT_KILL(-1,"重复秒杀"),
    INNER_ERROR(-2,"系统异常"),
    DATA_REWRITE(-3,"数据篡改");

    private int state;
    private String stateInfo;

    SeckillStateEnum(int state, String stateInfo) {
        this.state = state;
        this.stateInfo = stateInfo;
    }

    /**
     * 根据state
     * values 内部的所有的字段
     * @param index
     * @return
     * json传化 enum是有问题的要使用转化器
     *
     */
    public static SeckillStateEnum stateOf(int index) {
        for(SeckillStateEnum state:values()) {
            if(state.getState()==index) {
                return state;
            }
        }
        return null;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getStateInfo() {
        return stateInfo;
    }

    public void setStateInfo(String stateInfo) {
        this.stateInfo = stateInfo;
    }
}
