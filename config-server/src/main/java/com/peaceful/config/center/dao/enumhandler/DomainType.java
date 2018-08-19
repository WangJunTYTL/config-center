package com.peaceful.config.center.dao.enumhandler;

/**
 * Created by Jun on 2018/7/23.
 */
public enum DomainType {

    CATEGORY(1),PROPERTY(2),VALUE(3);

    private int code;

    DomainType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static DomainType findByCode(int code){
        for (DomainType domainType:DomainType.values()){
            if (code == domainType.code){
                return domainType;
            }
        }
        return null;
    }
}
