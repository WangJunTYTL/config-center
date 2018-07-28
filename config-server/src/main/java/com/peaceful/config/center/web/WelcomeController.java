package com.peaceful.config.center.web;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import com.peaceful.config.center.domain.Category;
import com.peaceful.config.center.domain.CategoryPropertyValue;
import com.peaceful.config.center.domain.Property;
import com.peaceful.config.center.service.CategoryService;
import com.peaceful.config.center.dataobj.PropertyValueDO;
import com.peaceful.config.center.util.WebResponse;
import com.peaceful.config.center.util.ZNodes;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by wang on 2018/7/20.
 */
@Controller
@ResponseBody
@RequestMapping("category")
public class WelcomeController {

    @Autowired
    private CategoryService categoryService;

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

    @RequestMapping("/property/value/list")
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
                return "<span style='color:red'>" + category.getName() + "目录下无属性，请查看其它目录</span>";
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

    @RequestMapping("/property/value/update")
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

    @RequestMapping("/admin")
    public String admin() {
        Category category = categoryService.getCategoryByName("root");

        String zNodesJson = ZNodes.buildZNodesJson(category);

        Map<String, Object> data = Maps.newHashMap();
        data.put("zNodes", zNodesJson);
        return WebResponse.HTML("/static/vm/admin.vm", data);
    }


}
