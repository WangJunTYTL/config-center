package com.peaceful.config.center.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import com.alibaba.fastjson.JSON;
import com.peaceful.config.center.dao.CategoryDao;
import com.peaceful.config.center.dao.CategoryPropertyDao;
import com.peaceful.config.center.dao.CategoryPropertyValueDao;
import com.peaceful.config.center.dao.handler.DomainType;
import com.peaceful.config.center.dao.handler.EventType;
import com.peaceful.config.center.dao.handler.PropertyType;
import com.peaceful.config.center.domain.Category;
import com.peaceful.config.center.domain.CategoryProperty;
import com.peaceful.config.center.domain.CategoryPropertyValue;
import com.peaceful.config.center.domain.LogEvent;
import com.peaceful.config.center.domain.Property;
import com.peaceful.config.center.service.CategoryService;
import com.peaceful.config.center.service.CategoryServiceCode;
import com.peaceful.config.center.service.CategoryServiceException;

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
 * Created by wang on 2017/4/18.
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
    public List<CategoryProperty> getCategoryPropertyList(String categoryName) {
        Preconditions.checkArgument(StringUtils.isNotBlank(categoryName), CATEGORY_NAME_ILLEGAL);

        Category category = categoryDao.getCategoryByName(categoryName);
        if (category == null) {
            throw new CategoryServiceException(CategoryServiceCode.CATEGORY_IS_NOT_EXIST);
        }
        return categoryPropertyDao.selectCategoryPropertyList(category.getId());
    }

    @Override
    public Property getPropertyById(long id) {
        return categoryPropertyDao.getPropertyById(id);
    }

    @Override
    public List<CategoryPropertyValue> getCategoryPropertyValue(long categoryId) {

        Category category = categoryDao.getCategoryById(categoryId);
        if (category == null) {
            throw new CategoryServiceException(CategoryServiceCode.CATEGORY_IS_NOT_EXIST);
        }

        List<CategoryProperty> categoryProperties = categoryPropertyDao.selectCategoryPropertyList(category.getId());
        if (categoryProperties == null) {
            return Lists.newArrayList();
        }

        List<CategoryPropertyValue> valueList = categoryPropertyValueDao.selectValueList(category.getId());
        Map<Long, String> categoryPropertyValueMap = Maps.newHashMap();
        if (valueList != null) {
            //先处理已经查到的有记录的类目属性值
            for (CategoryPropertyValue value : valueList) {
                //对于查到的属性值,如果属性值为null,则会去看属性是否包含默认值信息,如果有,返回默认值
                categoryPropertyValueMap.put(value.getPropertyId(), value.getValue());
                if (value.getValue() != null) {
                    continue;
                }
                //临时措施:使用循环查询,后期如果成为瓶颈会修改成批量查询
                Property property = categoryPropertyDao.getPropertyById(value.getPropertyId());
                if (property == null) {
                    throw new CategoryServiceException(CategoryServiceCode.PROPERTY_IS_NOT_EXIST);
                }
                if (property.getDefaultValue() != null) {
                    value.setValue(property.getDefaultValue());
                    categoryPropertyValueMap.put(value.getPropertyId(), value.getValue());
                }
            }
        }

        //处理不在类目属性值表中有记录的、但是的确属于该类目的属性
        for (CategoryProperty categoryProperty : categoryProperties) {
            if (categoryPropertyValueMap.get(categoryProperty.getPropertyId()) != null) {
                //如果这个商户有这个属性的记录,跳过
                continue;
            }
            Property property = categoryPropertyDao.getPropertyById(categoryProperty.getPropertyId());
            if (property == null || property.getDefaultValue() == null) {
                //如果这个属性没有找到或者这个属性本身就没有默认值
                continue;
            }
            //对于没有在CategoryPropertyValue表里查到相应merchantId记录的、且属性是有默认值的,也要填充
            CategoryPropertyValue categoryPropertyValue = new CategoryPropertyValue();
            //因为本条记录实际上并不存在,所以id显性设置为0以区分
            categoryPropertyValue.setId(0);
            categoryPropertyValue.setCategoryId(category.getId());
            categoryPropertyValue.setPropertyId(property.getId());
            categoryPropertyValue.setValue(property.getDefaultValue());
            valueList.add(categoryPropertyValue);
        }
        return valueList;
    }


    @Override
    public CategoryPropertyValue getSingleCategoryPropertyValue(long categoryId, String propertyName) {
        Preconditions.checkArgument(StringUtils.isNotBlank(propertyName), "propertyName-illegal");
        List<CategoryPropertyValue> propertyValues = getCategoryPropertyValue(categoryId);

        Property property = categoryPropertyDao.getPropertyByName(propertyName);
        if (property == null) {
            throw new CategoryServiceException(CategoryServiceCode.PROPERTY_IS_NOT_EXIST);
        }
        for (CategoryPropertyValue categoryPropertyValue : propertyValues) {
            if (categoryPropertyValue.getPropertyId() == property.getId()) {
                return categoryPropertyValue;
            }
        }
        return null;
    }

    @Override
    @Transactional
    public void insertCategory(Category category) {
        Preconditions.checkArgument(category != null, "category params is illegal!");
        Preconditions.checkArgument(StringUtils.isNotBlank(category.getName()), "category name is illegal");
        Preconditions.checkArgument(StringUtils.isNotBlank(category.getDescription()), "category description is illegal");

        Category parent = category.getParentCategory();
        // 如果填了父类目 需要校验父类目的合法性
        if (parent != null && StringUtils.isNotBlank(parent.getName())) {
            parent = categoryDao.getCategoryByName(parent.getName());
            if (parent == null) {
                throw new CategoryServiceException(CategoryServiceCode.PARENT_CATEGORY_IS_NOT_EXIST, category.getName() + "父类目不存在");
            }
            category.setParentCategory(parent);
        } else {
            // 如果不存在父类目 则创建顶级类目
            Category topCategory = new Category();
            category.setParentCategory(topCategory);
        }

        Category old = categoryDao.getCategoryByName(category.getName());
        if (old != null) {
            throw new CategoryServiceException(CategoryServiceCode.CATEGORY_IS_EXIST, category.getName() + "类目已存在");
        }
        categoryDao.insertCategory(category);
        LogEvent logEvent = LogEvent.buildLog(DomainType.CATEGORY, EventType.CATEGORY_UPDATE, category.getId());
        logService.log(logEvent);
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
    public long insertProperty(String categoryName, Property property) {
        Preconditions.checkArgument(property != null, "property is illegal!");
        Preconditions.checkArgument(StringUtils.isNotBlank(categoryName), CATEGORY_NAME_ILLEGAL);
        Preconditions.checkArgument(StringUtils.isNotBlank(property.getName()), "property name is illegal!");
        Preconditions.checkArgument(StringUtils.isNotBlank(property.getDescription()), "property description name is illegal!");
        Preconditions.checkArgument(PropertyType.findByCode(property.getType()) != null, "property type is illegal!");
        if (PropertyType.findByCode(property.getType()).equals(PropertyType.INPUT)) {
            // pass
        } else {
            Preconditions.checkArgument(StringUtils.isNotBlank(property.getOption()), "property optionValue is illegal!");
        }

        Category category = categoryDao.getCategoryByName(categoryName);
        if (category == null) {
            throw new CategoryServiceException(CategoryServiceCode.CATEGORY_IS_NOT_EXIST);
        }

        List<CategoryProperty> categoryPropertyList = getCategoryPropertyList(categoryName);
        Property old = categoryPropertyDao.getPropertyByName(property.getName());
        if (old != null) {
            throw new CategoryServiceException(CategoryServiceCode.PROPERTY_IS_EXIST, "属性已存在");
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
                throw new CategoryServiceException(CategoryServiceCode.PROPERTY_OPTION_VALUE_FORMAT_ERROR, "属性预设值格式不对,必须要是Object格式");
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

        LogEvent logEvent = LogEvent.buildLog(DomainType.PROPERTY,EventType.PROPERTY_UPDATE,propertyId);
        logService.log(logEvent);

        return propertyId;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void insertCategoryPropertyValue(String categoryName, Map<String, String> keyValue) {
        Preconditions.checkArgument(keyValue != null && !keyValue.isEmpty(), "params is illegal!");
        Preconditions.checkArgument(StringUtils.isNotBlank(categoryName), "categoryName is illegal!");

        for (Map.Entry<String, String> key : keyValue.entrySet()) {
            Preconditions.checkArgument(StringUtils.isNotBlank(key.getKey()), "property name is illegal!");
        }

        for (Map.Entry<String, String> entry : keyValue.entrySet()) {
            Category category = categoryDao.getCategoryByName(categoryName);
            if (category == null) {
                throw new CategoryServiceException(CategoryServiceCode.CATEGORY_IS_NOT_EXIST, "类目不存在");
            }
            Property property = categoryPropertyDao.getPropertyByName(entry.getKey());
            if (property == null) {
                throw new CategoryServiceException(CategoryServiceCode.PROPERTY_IS_NOT_EXIST, "属性不已存在");
            }
            if (StringUtils.isNotBlank(property.getOption())) { //校验属性值是否属于属性的预设值
                checkPropertyValueInOption(property, entry.getValue());
            }

            CategoryPropertyValue propertyValue = new CategoryPropertyValue();
            propertyValue.setCategoryId(category.getId());
            propertyValue.setPropertyId(property.getId());
            propertyValue.setValue(entry.getValue());
            // 方便通过db查询时易读
            Map<String, String> snapshotMap = Maps.newHashMap();
            snapshotMap.put("category", categoryName);
            snapshotMap.put("property", entry.getKey());
            snapshotMap.put("value", entry.getValue());

            String snapshotStr = JSON.toJSONString(snapshotMap);
            propertyValue.setSnapshot(snapshotStr);
            categoryPropertyValueDao.insertCategoryPropertyValue(propertyValue);

            LogEvent logEvent = LogEvent.buildLog(DomainType.VALUE,EventType.VALUE_UPDATE,propertyValue.getId());
            logService.log(logEvent);

            // 记录更新log
            logger.info("update categoryName:{} key:{} value:{} ", categoryName, entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void updateCategoryPropertyValue(long categoryId, long propertyId, String value) {
        Category category = categoryDao.getCategoryById(categoryId);
        if (category == null) {
            throw new CategoryServiceException(CategoryServiceCode.CATEGORY_IS_NOT_EXIST, "类目不存在");
        }
        Property property = categoryPropertyDao.getPropertyById(propertyId);
        if (property == null) {
            throw new CategoryServiceException(CategoryServiceCode.PROPERTY_IS_NOT_EXIST, "属性不已存在");
        }

        CategoryPropertyValue propertyValue = new CategoryPropertyValue();
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

        LogEvent logEvent = LogEvent.buildLog(DomainType.VALUE,EventType.VALUE_UPDATE,propertyValue.getId());
        logService.log(logEvent);
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
            throw new CategoryServiceException(CategoryServiceCode.PROPERTY_VALUE_ERROR, "属性值不在预设值范围内");
        } catch (Exception e) {
            logger.info("属性预设值格式错误:e-{},option-{}", e, property.getOption());
            throw new CategoryServiceException(CategoryServiceCode.PROPERTY_OPTION_VALUE_FORMAT_ERROR, "属性预设值格式错误");
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
            throw new CategoryServiceException(CategoryServiceCode.PROPERTY_IS_NOT_EXIST);
        }
        boolean updateRes = categoryPropertyDao.updatePropertyDescription(propertyId, description) > 0 ? true : false;

        LogEvent logEvent = LogEvent.buildLog(DomainType.PROPERTY,EventType.PROPERTY_UPDATE,property.getId());
        logService.log(logEvent);

        logger.info("property:id-{},name-{},description-{}", property.getId(), property.getName(), property.getDescription());
        return updateRes;
    }

    /**
     * 通过属性Id更新默认值
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean updateDefaultValueByName(String propertyName, String defaultValue) {
        Preconditions.checkArgument(StringUtils.isNotBlank(propertyName), "propertyId-illegal");
        //校验属性id是否存在
        Property property = categoryPropertyDao.getPropertyByName(propertyName);
        if (property == null) {
            throw new CategoryServiceException(CategoryServiceCode.PROPERTY_IS_NOT_EXIST, "属性不存在");
        }
        if (StringUtils.isNotBlank(property.getOption())) {
            this.checkPropertyValueInOption(property, defaultValue);
        }
        boolean updateRes = categoryPropertyDao.updatePropertyDefaultValueByName(propertyName, defaultValue) > 0 ? true : false;

        LogEvent logEvent = LogEvent.buildLog(DomainType.PROPERTY,EventType.PROPERTY_UPDATE,property.getId());
        logService.log(logEvent);

        logger.info("update-default-value-res-{}", updateRes);
        return updateRes;
    }

}
