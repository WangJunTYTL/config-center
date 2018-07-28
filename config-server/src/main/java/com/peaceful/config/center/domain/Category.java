package com.peaceful.config.center.domain;

import java.io.Serializable;
import java.util.List;

/**
 * Created by wang on 2017/4/15.
 */
public class Category implements Serializable {

    private long id;
    private String name;
    private Category parentCategory;
    private List<Category> childrenCategoryList;
    private String description;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Category getParentCategory() {
        return parentCategory;
    }

    public void setParentCategory(Category parentCategory) {
        this.parentCategory = parentCategory;
    }

    public List<Category> getChildrenCategoryList() {
        return childrenCategoryList;
    }

    public void setChildrenCategoryList(List<Category> childrenCategoryList) {
        this.childrenCategoryList = childrenCategoryList;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
