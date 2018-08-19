package com.peaceful.config.center.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import com.alibaba.fastjson.JSON;
import com.peaceful.config.center.dao.CategoryDao;
import com.peaceful.config.center.dao.CategoryPropertyDao;
import com.peaceful.config.center.dao.CategoryPropertyValueDao;
import com.peaceful.config.center.dao.enumhandler.DomainType;
import com.peaceful.config.center.dao.enumhandler.EventType;
import com.peaceful.config.center.dao.enumhandler.PropertyType;
import com.peaceful.config.center.domain.Category;
import com.peaceful.config.center.domain.CategoryProperty;
import com.peaceful.config.center.domain.CategoryPropertyValue;
import com.peaceful.config.center.domain.LogEvent;
import com.peaceful.config.center.domain.Property;
import com.peaceful.config.center.service.CategoryService;
import com.peaceful.config.center.service.CategoryReturnCode;
import com.peaceful.config.center.service.CategoryException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Created by Jun on 2017/4/18.
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryDao categoryDao;
    @Autowired
    private CategoryPropertyDao categoryPropertyDao;
    @Autowired
    private CategoryPropertyValueDao categoryPropertyValueDao;
    @Autowired
    private LogService logService;

    private Logger logger = LoggerFactory.getLogger(getClass());

    private static String CATEGORY_NAME_ILLEGAL = "category name is illegal!";


    @Override
    public Property getPropertyById(long id) {
        return categoryPropertyDao.getPropertyById(id);
    }

    @Override
    public Property getPropertyByName(long categoryId, String propertyName) {
        List<CategoryProperty> categoryPropertyList = categoryPropertyDao.selectCategoryPropertyList(categoryId);
        Property property = null;
        for (CategoryProperty categoryProperty : categoryPropertyList) {
            Property found = this.getPropertyById(categoryProperty.getPropertyId());
            if (found.getName().equalsIgnoreCase(propertyName)) { // 忽略大小写
                property = found;
                break;
            }
        }
        return property;
    }

    @Override
    public List<CategoryPropertyValue> getCategoryPropertyValue(long categoryId) {
        Category category = categoryDao.getCategoryById(categoryId);
        if (category == null) {
            throw new CategoryException(CategoryReturnCode.CATEGORY_IS_NOT_EXIST);
        }
        // 查询category下已设置的value列表
        List<CategoryPropertyValue> categoryPropertyValueList = categoryPropertyValueDao.selectValueList(category.getId());

        return categoryPropertyValueList;
    }

    @Override
    @Transactional
    public void insertCategory(Category category) {
        Preconditions.checkArgument(category != null, "category params is illegal!");
        Preconditions.checkArgument(StringUtils.isNotBlank(category.getName()), "category name is illegal");

        Category parent = category.getParentCategory();
        // 如果填了父类目 需要校验父类目的合法性
        if (parent != null && StringUtils.isNotBlank(parent.getName())) {
            parent = categoryDao.getCategoryByName(parent.getName());
            if (parent == null) {
                throw new CategoryException(CategoryReturnCode.PARENT_CATEGORY_IS_NOT_EXIST, category.getName() + "父类目不存在");
            }
            category.setParentCategory(parent);
        } else {
            // 如果不存在父类目 则创建顶级类目
            Category topCategory = new Category();
            category.setParentCategory(topCategory);
        }

        Category old = categoryDao.getCategoryByName(category.getName());
        if (old != null) {
            throw new CategoryException(CategoryReturnCode.CATEGORY_IS_EXIST, category.getName() + "类目已存在");
        }
        categoryDao.insertCategory(category);
        LogEvent logEvent = LogEvent.buildLog(DomainType.CATEGORY, EventType.CATEGORY_UPDATE, category.getId());
        logService.log(logEvent);
    }

    @Override
    @Transactional
    public boolean deleteCategory(String name) {
        Category category = getCategoryByName(name);
        if (category == null) {
            throw new CategoryException(CategoryReturnCode.CATEGORY_IS_NOT_EXIST);
        }
        deleteCategory(category); // 递归删除
        return true;
    }

    private void  deleteCategory(Category category){
        List<CategoryProperty> categoryPropertyList = categoryPropertyDao.selectCategoryPropertyList(category.getId());
        for (CategoryProperty categoryProperty : categoryPropertyList) {
            long propertyId = categoryProperty.getPropertyId();
            categoryPropertyValueDao.deleteByPropertyId(propertyId); // 先删除与该属性相关的值
            categoryPropertyDao.deleteById(propertyId); // 删除属性对象
            categoryPropertyDao.deleteCategoryPropertyMap(propertyId); // 删除关联关系
        }
        categoryDao.deleteByName(category.getName()); // 最后删除类目本身
        List<Category> categoryList = category.getChildrenCategoryList();
        if (categoryList == null ||  categoryList.isEmpty()){
            return;
        }
        for (Category del:categoryList){
            deleteCategory(del);
        }
    }

    @Override
    public Category getCategoryByName(String name) {
        Preconditions.checkArgument(StringUtils.isNotBlank(name), CATEGORY_NAME_ILLEGAL);

        Category category = categoryDao.getCategoryByName(name);
        if (category == null) {
            return null;
        }
        render(category); // 渲染类目父子关系
        return category;
    }

    @Override
    public Category getCategoryById(long id) {
        Category category = categoryDao.getCategoryById(id);
        if (category == null) {
            return null;
        } else {
            return getCategoryByName(category.getName());
        }
    }

    // 递归渲染类目的父子关系
    private void render(Category parent) {
        if (parent == null) {
            return;
        }
        List<Category> categoryList = categoryDao.getCategoryByParentId(parent.getId());
        if (categoryList == null || categoryList.isEmpty()) {
            return;
        }
        parent.setChildrenCategoryList(categoryList);
        for (Category category : categoryList) {
            category.setParentCategory(parent);
            render(category);
        }
    }


    @Override
    @Transactional
    public long insertProperty(long categoryId, Property property) {
        Preconditions.checkArgument(property != null, "property is illegal!");
        Preconditions.checkArgument(StringUtils.isNotBlank(property.getName()), "property name is illegal!");
        Preconditions.checkArgument(StringUtils.isNotBlank(property.getDescription()), "property description name is illegal!");
        Preconditions.checkArgument(PropertyType.findByCode(property.getType()) != null, "property type is illegal!");
        if (PropertyType.findByCode(property.getType()).equals(PropertyType.INPUT)) {
            // pass
        } else {
            Preconditions.checkArgument(StringUtils.isNotBlank(property.getOption()), "property optionValue is illegal!");
        }

        Category category = categoryDao.getCategoryById(categoryId);
        if (category == null) {
            throw new CategoryException(CategoryReturnCode.CATEGORY_IS_NOT_EXIST);
        }

        Property old = this.getPropertyByName(categoryId, property.getName());
        if (old != null) {
            throw new CategoryException(CategoryReturnCode.PROPERTY_IS_EXIST, "属性已存在");
        }
        // 校验预设定值格式
        if (PropertyType.findByCode(property.getType()).equals(PropertyType.INPUT)) {
            // pass
        } else {
            try {
                Map<String, Object> value = JSON.parseObject(property.getOption());
                property.setOption(JSON.toJSONString(value));// 在转一次是为了格式化后存储
            } catch (Exception e) {
                logger.warn("property option value format error", e);
                throw new CategoryException(CategoryReturnCode.PROPERTY_OPTION_VALUE_FORMAT_ERROR, "属性预设值格式不对,必须要是Object格式");
            }
        }
        if (StringUtils.isNotBlank(property.getOption())) {
            this.checkPropertyValueInOption(property, property.getDefaultValue());
        }
        categoryPropertyDao.insertProperty(property);
        long propertyId = property.getId();
        CategoryProperty categoryProperty = new CategoryProperty();
        categoryProperty.setCategoryId(category.getId());
        categoryProperty.setPropertyId(propertyId);
        categoryPropertyDao.insertCategoryProperty(categoryProperty);

        LogEvent logEvent = LogEvent.buildLog(DomainType.PROPERTY, EventType.PROPERTY_UPDATE, propertyId);
        logService.log(logEvent);

        return propertyId;
    }

    @Override
    public long deleteProperty(long categoryId, String propertyName) {
        Property property = this.getPropertyByName(categoryId, propertyName);
        if (property == null) {
            throw new CategoryException(CategoryReturnCode.PROPERTY_IS_NOT_EXIST, "属性不存在");
        }
        deleteProperty(property);
        return 0;
    }

    @Override
    public String getCategoryPropertyValue(long categoryId, long propertyId) {
        CategoryPropertyValue categoryPropertyValue = categoryPropertyValueDao.selectByCategoryIdAndPropertyId(categoryId, propertyId);
        if (categoryPropertyValue == null) {
            Property property = categoryPropertyDao.getPropertyById(propertyId);
            if (property != null) {
                return property.getDefaultValue();
            }
        }
        return categoryPropertyValue.getValue();
    }

    @Override
    public String getCategoryPropertyValue(long categoryId, String key, String propertyName) {
        return null;
    }


    @Override
    @Transactional
    public void updateCategoryPropertyValue(long categoryId, long propertyId, String value) {
        Category category = categoryDao.getCategoryById(categoryId);
        if (category == null) {
            throw new CategoryException(CategoryReturnCode.CATEGORY_IS_NOT_EXIST, "类目不存在");
        }
        Property property = categoryPropertyDao.getPropertyById(propertyId);
        if (property == null) {
            throw new CategoryException(CategoryReturnCode.PROPERTY_IS_NOT_EXIST, "属性不已存在");
        }

        CategoryPropertyValue propertyValue = new CategoryPropertyValue();
        propertyValue.setKey(property.getName());// 如果不指定key名称，默认取属性的名称
        propertyValue.setCategoryId(category.getId());
        propertyValue.setPropertyId(property.getId());
        propertyValue.setValue(value);
        // 方便通过db查询时易读
        Map<String, String> snapshotMap = Maps.newHashMap();
        snapshotMap.put("category", category.getName());
        snapshotMap.put("property", property.getName());
        snapshotMap.put("value", value);

        String snapshotStr = JSON.toJSONString(snapshotMap);
        propertyValue.setSnapshot(snapshotStr);
        categoryPropertyValueDao.insertCategoryPropertyValue(propertyValue);

        LogEvent logEvent = LogEvent.buildLog(DomainType.VALUE, EventType.VALUE_UPDATE, propertyValue.getId());
        logService.log(logEvent);
    }

    @Transactional
    void deleteProperty(Property property) {
        long propertyId = property.getId();
        categoryPropertyDao.deleteById(propertyId);
        categoryPropertyDao.deleteCategoryPropertyMap(propertyId);
        long rows = categoryPropertyValueDao.deleteByPropertyId(propertyId);
        logger.warn("删除属性:{}，附带删除{}条属性值", propertyId, rows);
    }

    /**
     * 校验属性值是否属于属性的预设值
     */
    private void checkPropertyValueInOption(Property property, String propertyValue) {
        if (propertyValue == null) { // 允许属性值为null,但是如果是空串则必须在option范围内
            return;
        }
        try {
            Map<String, Object> value = JSON.parseObject(property.getOption());
            for (Map.Entry<String, Object> optionEntry : value.entrySet()) {
                if (String.valueOf(optionEntry.getValue()).equals(propertyValue)) {
                    return;
                }
            }
            logger.info("属性值不在预设值范围内:{},propertyId:{},option:{}", propertyValue, property.getId(), property.getOption());
            throw new CategoryException(CategoryReturnCode.PROPERTY_VALUE_ERROR, "属性值不在预设值范围内");
        } catch (Exception e) {
            logger.info("属性预设值格式错误:e-{},option-{}", e, property.getOption());
            throw new CategoryException(CategoryReturnCode.PROPERTY_OPTION_VALUE_FORMAT_ERROR, "属性预设值格式错误");
        }
    }

    /**
     * 通过属性名字更新描述
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean updateDescription(long propertyId, String description) {
        Preconditions.checkArgument(StringUtils.isNotBlank(description), "propertyName is illegal");
        Property property = categoryPropertyDao.getPropertyById(propertyId);
        if (property == null) {
            logger.info("updateDescriptionByName error:未发现property");
            throw new CategoryException(CategoryReturnCode.PROPERTY_IS_NOT_EXIST);
        }
        boolean updateRes = categoryPropertyDao.updatePropertyDescription(propertyId, description) > 0 ? true : false;

        LogEvent logEvent = LogEvent.buildLog(DomainType.PROPERTY, EventType.PROPERTY_UPDATE, property.getId());
        logService.log(logEvent);
        return updateRes;
    }

}
