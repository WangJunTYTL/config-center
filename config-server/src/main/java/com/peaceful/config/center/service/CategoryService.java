package com.peaceful.config.center.service;


import com.peaceful.config.center.domain.Category;
import com.peaceful.config.center.domain.CategoryProperty;
import com.peaceful.config.center.domain.CategoryPropertyValue;
import com.peaceful.config.center.domain.Property;

import java.util.List;
import java.util.Map;

/**
 * Created by wang on 2017/4/15.
 */
public interface CategoryService {


    /**
     * 插入类目
     */
    void insertCategory(Category category);

    /**
     * 查询类目信息 通过name
     */
    Category getCategoryByName(String name);

    /**
     * 查询类目信息 通过id
     */
    Category getCategoryById(long id);

    /**
     * 获取类目属性列表
     */
    List<CategoryProperty> getCategoryPropertyList(String categoryName);

    /**
     * 查询属性信息
     */
    Property getPropertyById(long id);

    /**
     * 插入属性
     */

    long insertProperty(String categoryName, Property property);


    /**
     * 插入类目属性值
     */
    void insertCategoryPropertyValue(String categoryName, Map<String, String> keyValue);

    void updateCategoryPropertyValue(long categoryId, long propertyId, String value);


    /**
     * 查询指定类目的属性信息
     */
    List<CategoryPropertyValue> getCategoryPropertyValue(long categoryId);

    /**
     * 查询指定类目属性的属性值
     *
     * @return 如果没有类目或者属性, 会抛出CategoryServiceException, 如果商户没有这个属性的属性值, 会返回null
     */
    CategoryPropertyValue getSingleCategoryPropertyValue(long categoryId, String propertyName);

    /**
     * 通过属性名字更新描述
     */
    boolean updateDescription(long propertyId, String description);

    /**
     * 通过属性Id更新默认值
     *
     * @param propertyName 属性Id
     * @param defaultValue 默认值
     */
    boolean updateDefaultValueByName(String propertyName, String defaultValue);

}
