package com.peaceful.config.center.mapper.enumhandler;

/**
 * Created by Jun on 2018/7/23.
 */
public enum EventType {

    CATEGORY_UPDATE(1),
    PROPERTY_UPDATE(2),
    VALUE_UPDATE(3);

    private int code;

    EventType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static EventType findByCode(int code){
        for (EventType type:EventType.values()){
            if (code == type.code){
                return type;
            }
        }
        return null;
    }
}
