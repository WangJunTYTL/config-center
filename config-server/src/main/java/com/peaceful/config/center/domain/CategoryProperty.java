package com.peaceful.config.center.domain;

import java.io.Serializable;

/**
 * Created by wang on 2017/4/15.
 */
public class CategoryProperty implements Serializable {

    private long categoryId;
    private long propertyId;

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public long getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(long propertyId) {
        this.propertyId = propertyId;
    }
}
