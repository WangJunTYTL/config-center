package com.peaceful.config.center.service;



/**
 * Created by Jun on 2017/4/18.
 */
public class CategoryException extends RuntimeException {

    private final transient CategoryReturnCode resultCode;

    private final String errMsg;

    public CategoryException(CategoryReturnCode resultCode) {
        this.resultCode = resultCode;
        errMsg = null;
    }

    public CategoryException(CategoryReturnCode resultCode, String msg) {
        super(msg);
        this.errMsg = msg;
        this.resultCode = resultCode;
    }

    public CategoryException(CategoryReturnCode resultCode, Exception e) {
        this.errMsg = e.getMessage();
        this.resultCode = resultCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public CategoryReturnCode getResultCode() {
        return resultCode;
    }

    public String getResultCodeStr() {
        return String.valueOf(resultCode);
    }
}
