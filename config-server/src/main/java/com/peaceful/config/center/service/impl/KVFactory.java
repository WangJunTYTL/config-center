package com.peaceful.config.center.service.impl;

import com.peaceful.config.center.dao.enumhandler.PropertyType;
import com.peaceful.config.center.domain.Category;
import com.peaceful.config.center.domain.Property;
import com.peaceful.config.center.service.CategoryException;
import com.peaceful.config.center.service.CategoryReturnCode;
import com.peaceful.config.center.service.CategoryService;
import com.peaceful.config.center.service.KVConfig;

/**
 * Created by Jun on 2018/8/19.
 */
public class KVFactory implements KVConfig {

    private CategoryService categoryService;
    private Category category;

    public KVFactory(CategoryService categoryService, String categoryName) {
        this.categoryService = categoryService;
        this.category = categoryService.getCategoryByName(categoryName);
        if (category == null) {
            throw new CategoryException(CategoryReturnCode.CATEGORY_IS_NOT_EXIST);
        }
    }

    public KVFactory(CategoryService categoryService, long categoryId) {
        this.categoryService = categoryService;
        this.category = categoryService.getCategoryById(categoryId);
        if (category == null) {
            throw new CategoryException(CategoryReturnCode.CATEGORY_IS_NOT_EXIST);
        }
    }

    @Override
    public String get(String key) {
        Property property = getProperty(key);
        return categoryService.getCategoryPropertyValue(category.getId(), property.getId());
    }

    @Override
    public boolean set(String key, String value) {
        Property property = getProperty(key);
        categoryService.updateCategoryPropertyValue(category.getId(), property.getId(), value);
        return true;
    }

    @Override
    public boolean add(String key, String value) {
        Property property = categoryService.getPropertyByName(category.getId(), key);
        if (property != null) {
            set(key, value);
        } else {
            property = new Property();
            property.setDescription("");
            property.setType(PropertyType.INPUT.getCode());
            property.setName(key);
            categoryService.insertProperty(category.getId(), property);
            set(key, value);
        }
        return true;
    }

    private Property getProperty(String propertyName) {
        Property property = categoryService.getPropertyByName(category.getId(), propertyName);
        if (property == null) {
            throw new CategoryException(CategoryReturnCode.PROPERTY_IS_NOT_EXIST);
        }
        return property;
    }


}
