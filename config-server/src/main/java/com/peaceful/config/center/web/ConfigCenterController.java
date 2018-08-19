package com.peaceful.config.center.web;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import com.peaceful.config.center.dao.enumhandler.PropertyType;
import com.peaceful.config.center.domain.Category;
import com.peaceful.config.center.domain.CategoryPropertyValue;
import com.peaceful.config.center.domain.Property;
import com.peaceful.config.center.service.CategoryService;
import com.peaceful.config.center.dataobj.PropertyValueDO;
import com.peaceful.config.center.service.CategoryException;
import com.peaceful.config.center.service.impl.KVFactory;
import com.peaceful.config.center.util.WebResponse;
import com.peaceful.config.center.util.ZNodes;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Jun on 2018/7/20.
 */
@Controller
@ResponseBody
@RequestMapping("config")
public class ConfigCenterController {

    @Autowired
    private CategoryService categoryService;


    private Logger logger = LoggerFactory.getLogger(getClass());

    @RequestMapping("index")
    public String index(HttpServletRequest request) {

        Category category = categoryService.getCategoryByName("root");
        String zNodesJson = ZNodes.buildZNodesJson(category);

        Map<String, Object> data = Maps.newHashMap();
        data.put("zNodes", zNodesJson);
        String templateNum = request.getParameter("tn");
        if (StringUtils.isNotBlank(templateNum)) {
            return WebResponse.HTML("static/vm/index" + templateNum + ".vm", data);
        } else {
            return WebResponse.HTML("static/vm/index.vm", data);
        }
    }

    @RequestMapping("insert-category")
    public Object insertCategory(HttpServletRequest request) {
        String categoryIdStr = request.getParameter("cid");
        String categoryName = request.getParameter("cname");
        long categoryId = Long.parseLong(categoryIdStr);
        Category category = new Category();
        Category parentCategory = categoryService.getCategoryById(categoryId);
        category.setParentCategory(parentCategory);
        category.setName(categoryName);
        category.setDescription("");
        categoryService.insertCategory(category);
        return WebResponse.JSON(200, "OK");
    }

    @RequestMapping("delete-category")
    public Object deleteCategory(HttpServletRequest request) {
        String categoryIdStr = request.getParameter("cid");
        String categoryName = request.getParameter("cname");
        long categoryId = Long.parseLong(categoryIdStr);
        Category category = categoryService.getCategoryById(categoryId);
        if (category.getName().equalsIgnoreCase(categoryName)) {
            categoryService.deleteCategory(categoryName);
            return WebResponse.JSON(200, "OK");
        } else {
            return WebResponse.JSON(300, "目录名称错误，请确认");
        }
    }

    @RequestMapping("/get-kv-list")
    public String getPropertyValue(HttpServletRequest request) {
        String categoryIdStr = request.getParameter("cId");
        if (StringUtils.isBlank(categoryIdStr)) {
            return "Not Fount";
        } else {
            Map<String, Object> data = Maps.newHashMap();
            long categoryId = Long.parseLong(categoryIdStr);
            Category category = categoryService.getCategoryById(categoryId);
            data.put("category", category);
            List<CategoryPropertyValue> categoryPropertyValueList = categoryService.getCategoryPropertyValue(categoryId);
            if (categoryPropertyValueList == null || categoryPropertyValueList.isEmpty()) {
                return WebResponse.HTML("static/vm/propertyValueList.vm", data);
            }
            List<PropertyValueDO> propertyValueList = Lists.newArrayList();
            for (CategoryPropertyValue categoryPropertyValue : categoryPropertyValueList) {
                Property property = categoryService.getPropertyById(categoryPropertyValue.getPropertyId());
                PropertyValueDO propertyValue = new PropertyValueDO(property);
                propertyValue.setId(property.getId());
                propertyValue.setValue(categoryPropertyValue.getValue());
                propertyValueList.add(propertyValue);
            }
            data.put("propertyValueList", propertyValueList);
            return WebResponse.HTML("static/vm/propertyValueList.vm", data);
        }
    }

    @RequestMapping("/update-kv")
    public Object updatePropertyValue(HttpServletRequest request) {
        String categoryIdStr = request.getParameter("cid");
        String propertyIdStr = request.getParameter("pid");
        String value = request.getParameter("newValue");
        String desc = request.getParameter("desc");
        long categoryId = Long.parseLong(categoryIdStr);
        long propertyId = Long.parseLong(propertyIdStr);
        if (StringUtils.isNotBlank(value)) {
            categoryService.updateCategoryPropertyValue(categoryId, propertyId, value);
        }
        if (StringUtils.isNotBlank(desc)) {
            categoryService.updateDescription(propertyId, desc);
        }
        return WebResponse.JSON(200, "OK");
    }

    @RequestMapping("/insert-kv")
    public Object insertKeyValue(HttpServletRequest request) {
        try {
            String categoryIdStr = request.getParameter("cid");
            String key = request.getParameter("key");
            String value = request.getParameter("value");
            String desc = request.getParameter("desc");
            long categoryId = Long.parseLong(categoryIdStr);
            Property property = new Property();
            property.setDescription(desc);
            property.setType(PropertyType.INPUT.getCode());
            property.setName(key);
            long propertyId = categoryService.insertProperty(categoryId, property);
            categoryService.updateCategoryPropertyValue(categoryId, propertyId, value);
            return WebResponse.JSON(200, "OK");
        } catch (CategoryException e) {
            return WebResponse.JSON(500, e.getMessage());
        } catch (Exception e) {
            logger.error("新增属性报错", e);
            return WebResponse.JSON(500, "系统异常，稍后重试");
        }
    }

    @RequestMapping("delete-key")
    public Object deleteKey(HttpServletRequest request) {
        try {
            String categoryIdStr = request.getParameter("cid");
            long categoryId = Long.parseLong(categoryIdStr);
            String propertyName = request.getParameter("key");
            categoryService.deleteProperty(categoryId, propertyName);
            return WebResponse.JSON(200, "OK");
        } catch (CategoryException e) {
            return WebResponse.JSON(500, e.getMessage());
        } catch (Exception e) {
            logger.error("删除属性报错", e);
            return WebResponse.JSON(500, "系统异常，稍后重试");
        }
    }

}
