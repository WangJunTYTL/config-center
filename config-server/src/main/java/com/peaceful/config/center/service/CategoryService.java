package com.peaceful.config.center.service;


import com.peaceful.config.center.domain.Category;
import com.peaceful.config.center.domain.CategoryProperty;
import com.peaceful.config.center.domain.CategoryPropertyValue;
import com.peaceful.config.center.domain.Property;

import java.util.List;
import java.util.Map;

/**
 * Created by Jun on 2017/4/15.
 */
public interface CategoryService {

    /**
     * 插入类目
     */
    void insertCategory(Category category);

    /**
     * 删除类目  同时会删除与之关联的属性、映射关系、类目属性值
     */
    boolean deleteCategory(String name);

    /**
     * 查询类目信息 category name是唯一的
     */
    Category getCategoryByName(String name);

    /**
     * 查询类目信息 通过id
     */
    Category getCategoryById(long id);

    /**
     * 查询属性信息
     */
    Property getPropertyById(long id);

    /**
     * 通过名称查询信息 在某个category下关联的属性名称是唯一的
     */
    Property getPropertyByName(long categoryId, String propertyName);

    /**
     * 插入属性
     */
    long insertProperty(long categoryId, Property property);

    /**
     * 删除属性 同时会删除 类目关联关系、类目属性值
     */
    long deleteProperty(long categoryId, String propertyName);

    String getCategoryPropertyValue(long categoryId,long propertyId);

    String getCategoryPropertyValue(long categoryId,String key,String propertyName);

    void insertPropertyAndValue(long category,Property property,String value);

    void updateCategoryPropertyValue(long categoryId, long propertyId, String value);

    /**
     * 查询指定类目的属性信息
     */
    List<CategoryPropertyValue> getCategoryPropertyValue(long categoryId);

    /**
     * 通过属性名字更新描述
     */
    boolean updateDescription(long propertyId, String description);


}
