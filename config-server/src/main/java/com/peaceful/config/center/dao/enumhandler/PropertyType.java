package com.peaceful.config.center.dao.enumhandler;

/**
 * Created by Jun on 2018/7/20.
 */
public enum PropertyType {

    INPUT(0),
    RADIO(1),
    CHECKBOX(2),
    SELECT(3);

    PropertyType(int code) {
        this.code = code;
    }

    private int code;

    public int getCode() {
        return code;
    }

    public static PropertyType findByCode(int code) {
        for (PropertyType type : PropertyType.values()) {
            if (type.code == code) {
                return type;
            }
        }
        return null;
    }
}
