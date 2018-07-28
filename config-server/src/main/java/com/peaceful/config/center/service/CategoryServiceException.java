package com.peaceful.config.center.service;



/**
 * Created by wang on 2017/4/18.
 */
public class CategoryServiceException extends RuntimeException {

    private final transient CategoryServiceCode resultCode;

    private final String errMsg;

    public CategoryServiceException(CategoryServiceCode resultCode) {
        this.resultCode = resultCode;
        errMsg = null;
    }

    public CategoryServiceException(CategoryServiceCode resultCode, String msg) {
        super(msg);
        this.errMsg = msg;
        this.resultCode = resultCode;
    }

    public CategoryServiceException(CategoryServiceCode resultCode, Exception e) {
        this.errMsg = e.getMessage();
        this.resultCode = resultCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public CategoryServiceCode getResultCode() {
        return resultCode;
    }

    public String getResultCodeStr() {
        return String.valueOf(resultCode);
    }
}
