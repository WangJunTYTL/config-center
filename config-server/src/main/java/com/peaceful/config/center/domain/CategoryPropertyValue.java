package com.peaceful.config.center.domain;

import java.io.Serializable;

/**
 * Created by Jun on 2017/4/15.
 */
public class CategoryPropertyValue implements Serializable {

    private long id;
    private String objectKey;
    private long categoryId;
    private long propertyId;
    private String value;
    private String comment;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getObjectKey() {
        return objectKey;
    }

    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
    }

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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
